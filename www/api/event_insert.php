<?php

$event_id = get_int("event_id");
$owner_id = get_int_requared("owner_id");

$owner = selectMap("select * from owners where owner_id = $owner_id");

if ($event_id == null) {

    $app_id = get_int_requared("app_id");
    $event_time = get_int_requared("event_time");
    $event_lat = get_double_requared("event_lat");
    $event_lon = get_double_requared("event_lon");
    $event_address = get_string("event_address");
    $event_filter_min_year = get_int("event_filter_min_year");
    $event_filter_sex = get_int("event_filter_sex");
    $event_filter_max_members = get_int("event_filter_max_members");
    $event_title = get_string("event_title");
    $event_image_link_id = get_int("event_image_link_id");

    $event_id = random_key("events", "event_id");

    $result["response"] = insertList("events", array(
        "app_id" => $app_id,
        "event_id" => $event_id,
        "owner_id" => $owner_id,
        "event_time" => $event_time,
        "event_lat" => $event_lat,
        "event_lon" => $event_lon,
        "event_address" => $event_address,
        "event_filter_min_year" => $event_filter_min_year,
        "event_filter_sex" => $event_filter_sex,
        "event_filter_max_members" => $event_filter_max_members,
        "event_title" => $event_title,
        "event_image_link_id" => $event_image_link_id,
    ));

    insertList("members", array(
        "member_id" => random_key("members", "member_id"),
        "attach_type" => ATTACH_TYPE_INVITE,
        "attach_id" => $event_id,
        "owner_id" => $owner_id,
        "member_time" => "unix_timestamp()"
    ));

    $invite_list = get_int_array("invite_list");
    if ($invite_list != null) {
        foreach ($invite_list as $invited_owner_id)
            if ($invited_owner_id != $owner_id)
                insertList("members", array(
                    "member_id" => random_key("members", "member_id"),
                    "attach_type" => ATTACH_TYPE_INVITE,
                    "attach_id" => $event_id,
                    "owner_id" => $invited_owner_id,
                    "member_visible" => INVITE_SEND,
                    "member_time" => "unix_timestamp()",
                ));

        $friends = selectList("select owner_id from members where attach_type = " . ATTACH_TYPE_FRIENDS
            . " and attach_id = $owner_id and owner_id in (" . implode(",", $invite_list) . ")");

        if ($friends != null){
            notify($friends,
                PUSH_TYPE_INVITE_FRIEND_CREATE,
                $owner["owner_avatar_link_id"],
                $owner["owner_name"],
                $event_title,
                array("event_id" => $event_id));
            $invite_list = array_diff($invite_list, $friends);
        }

        notify($invite_list,
            PUSH_TYPE_INVITE_CREATE,
            $owner["owner_avatar_link_id"],
            $owner["owner_name"],
            $event_title,
            array("event_id" => $event_id));
    }

} else {
    $app_id = get_int("app_id");
    $event_id = get_int_requared("event_id");
    $event_time = get_int("event_time");
    $event_lat = get_double("event_lat");
    $event_lon = get_double("event_lon");
    $event_address = get_string("event_address");
    $event_filter_min_year = get_int("event_filter_min_year");
    $event_filter_sex = get_int("event_filter_sex");
    $event_filter_max_members = get_int("event_filter_max_members");
    $event_title = get_string("event_title");
    $event_visible = get_int("event_visible");
    $event_image_link_id = get_int("event_image_link_id");

    $result["response"] = updateList("events", array(
        "app_id" => $app_id,
        "owner_id" => $owner_id,
        "event_time" => $event_time,
        "event_lat" => $event_lat,
        "event_lon" => $event_lon,
        "event_address" => $event_address,
        "event_filter_min_year" => $event_filter_min_year,
        "event_filter_sex" => $event_filter_sex,
        "event_filter_max_members" => $event_filter_max_members,
        "event_title" => $event_title,
        "event_image_link_id" => put_image($event_image_link_id),
        "event_visible" => $event_visible,
    ), "event_id", $event_id);

    $owners = selectList("select owner_id from members where attach_type = " . ATTACH_TYPE_INVITE . " and attach_id = $event_id");
    if ($event_visible == 0) {
        notify($owners,
            PUSH_TYPE_INVITE_CANCELED,
            $owner["owner_avatar_link_id"],
            $owner["owner_name"],
            array("event_id" => $event_id));
    } else {
        notify($owners,
            PUSH_TYPE_INVITE_UPDATED,
            $owner["owner_avatar_link_id"],
            $owner["owner_name"],
            array("event_id" => $event_id));
    }

}
$result["event_id"] = $event_id;
insert_event($event_id);

response($result);
