<?php

$app_id = get_int_requared("app_id");
$owner_id = get_int("owner_id");
$event_attach_type = get_int("event_attach_type");
$event_type_id = get_int("event_type_id");
$device_lat = get_double_requared("device_lat");
$device_lon = get_double_requared("device_lon");
$beside = get_int("beside", 0);
$page_offset = get_int("page_offset", 0);
$page_size = get_int("page_size", 20);

if (get_double("fake_device_lat") != null && get_double("fake_device_lon") != null) {
    $device_lat = get_double_requared("fake_device_lat");
    $device_lon = get_double_requared("fake_device_lon");
}

$owner_distance = 100000;
$visible_event_interval_after_event_time = 60 * 60 * 2;

{
    if ($owner_id != null)
        $owner = selectMap("select * from owners where owner_id = $owner_id");

    $result["event_id_list"] = selectList("select event_id from events t1"
        . " where app_id = $app_id "
        . " and event_time > unix_timestamp() - $visible_event_interval_after_event_time"
        . " and " . dist("event_lat", "event_lon", $device_lat, $device_lon) . " < $owner_distance"
        . " and event_visible = 1"
        . ($owner != null ? " and ((owner_id = $owner_id) or "
            . "         ( "
            . "             (event_filter_sex is null or event_filter_sex = '" . $owner["owner_sex"] . "')"
            . ($owner["owner_birthdate"] != null ? " and (event_time - " . $owner["owner_birthdate"] . " >= event_filter_min_year * 31536000)" : "")
            . "         )"
            . "     )"
            . " and ((event_filter_max_members is null) "
            . "       or (select count(*) from members where attach_type = " . ATTACH_TYPE_INVITE
            . "             and attach_id = t1.event_id and owner_id = $owner_id) > 0 "
            . "       or (select count(*) from members where attach_type = " . ATTACH_TYPE_INVITE
            . "             and attach_id = t1.event_id and member_visible = 1) < event_filter_max_members "
            . "      )"
            : "")
        . " order by event_time asc limit $page_offset, $page_size");

    insert_event_list($result["event_id_list"]);
}

if ($beside != 0) {
    $result["device_id_list"] = selectList("select device_id from devices "
        . " where device_gps_time is not null
and " . dist("device_lat", "device_lon", $device_lat, $device_lon) . " < $owner_distance limit 100");
    insert_device_list($result["device_id_list"]);
}

response($result);


