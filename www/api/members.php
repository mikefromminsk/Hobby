<?php
$app_id = get_int_requared("app_id");
$owner_id = get_int_requared("owner_id");
$device_lat = get_double("device_lat");
$device_lon = get_double("device_lon");
$beside = get_int("beside");
$friends = get_int("friends");
$likes = get_int("likes");

$sex = get_string("sex");
$min_years = get_int("min_years");
$name = get_string("name");
$page_offset = get_int("page_offset", 0);
$page_size = get_int("page_size", 20);
$owner_distance = 100000;

$result["result"] = true;


if ($name != null) {
    $result["search"] = selectList("select owner_id from owners t1 "
        . " where app_id = $app_id "
        . ($sex == 'M' || $sex == 'W' ? " and t1.owner_sex = '$sex' " : "")
        . ($min_years != null ? " and t1.owner_birthdate <= unix_timestamp() - $min_years * 31536000 " : "")
        . ($name != null ? " and lower(t1.owner_name) like lower('%$name%')" : "")
        . " and t1.owner_id <> $owner_id "
        . " order by owner_login_time desc limit $page_offset, $page_size");
    insert_owners($result["search"]);
}

if ($beside == 1 && $device_lat != null && $device_lon != null) {
    $result["beside"] = selectList("select distinct t1.owner_id from devices t1"
        . " inner join owners t2 on t1.owner_id = t2.owner_id "
        . ($sex == 'M' || $sex == 'W' ? " and t2.owner_sex = '$sex' " : "")
        . ($min_years != null ? " and t2.owner_birthdate <= unix_timestamp() - $min_years * 31536000 " : "")
        . ($name != null ? " and lower(t2.owner_name) like lower('%$name%')" : "")
        . " where t1.app_id = $app_id "
        . " and " . dist("device_lat", "device_lon", $device_lat, $device_lon) . " < $owner_distance "
        . " and t1.owner_id <> $owner_id "
        . " limit $page_offset, $page_size");
    insert_owners($result["beside"]);
}

if ($friends == 1) {
    $result["friends"] = selectList("select t1.attach_id from members t1 "
        . " inner join owners t2 on t1.owner_id = t2.owner_id "
        . ($sex == 'M' || $sex == 'W' ? " and t2.owner_sex = '$sex' " : "")
        . ($min_years != null ? " and t2.owner_birthdate <= unix_timestamp() - $min_years * 31536000 " : "")
        . ($name != null ? " and lower(t2.owner_name) like lower('%$name%')" : "")
        . " where t1.attach_type = " . ATTACH_TYPE_FRIENDS
        . " and t1.owner_id = $owner_id "
        . " and t1.attach_id <> $owner_id "
        . " and t1.member_visible = 1 order by t1.member_time desc limit $page_offset, $page_size");
    if ($result["friends"] != null)
        $result["friends_of_friends"] = selectList("select distinct t1.attach_id, t1.member_time from members t1 "
            . " inner join owners t2 on t1.owner_id = t2.owner_id "
            . ($sex == 'M' || $sex == 'W' ? " and t2.owner_sex = '$sex' " : "")
            . ($min_years != null ? " and t2.owner_birthdate <= unix_timestamp() - $min_years * 31536000 " : "")
            . ($name != null ? " and lower(t2.owner_name) like lower('%$name%')" : "")
            . " where t1.attach_type = " . ATTACH_TYPE_FRIENDS
            . " and t1.owner_id in (" . implode(",", $result["friends"]) . ")"
            . " and t1.attach_id not in (" . implode(",", $result["friends"]) . ")"
            . " and t1.member_visible = 1 order by t1.member_time desc limit $page_offset, $page_size");
    insert_owners($result["friends"]);
    insert_owners($result["friends_of_friends"]);
}

if ($likes == 1) {
    $result["likes"] = selectList("select attach_id from members t1 "
        . " inner join owners t2 on t1.owner_id = t2.owner_id "
        . ($sex == 'M' || $sex == 'W' ? " and t2.owner_sex = '$sex' " : "")
        . ($min_years != null ? " and t2.owner_birthdate <= unix_timestamp() - $min_years * 31536000 " : "")
        . ($name != null ? " and lower(t2.owner_name) like lower('%$name%')" : "")
        . " where attach_type = " . ATTACH_TYPE_LIKE_OWNER
        . " and t1.owner_id = $owner_id "
        . " and t1.attach_id <> $owner_id "
        . " and member_visible = 1 order by member_time desc limit $page_offset, $page_size");
    insert_owners($result["likes"]);
    if (count($result["likes"]) > 0)
        $result["mutual_likes"] = selectList("select attach_id from members t1 "
            . " where attach_type = " . ATTACH_TYPE_LIKE_OWNER
            . " and owner_id in (" . implode(",", $result["likes"]) . ") "
            . " and attach_id = $owner_id "
            . " and attach_id <> $owner_id "
            . " and member_visible = 1 order by member_time desc limit $page_offset, $page_size");
}


response($result);