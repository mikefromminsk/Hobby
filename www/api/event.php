<?php

$event_id = get_int_requared("event_id");
$action = get_string("action");

$result["event_id"] = $event_id;

if ($action != null)
    response_file("member_insert.php",
        array(
            "owner_id" => get_int_requared("owner_id"),
            "device_id" => get_string_requared("device_id"),
            "attach_type" => ATTACH_TYPE_INVITE,
            "attach_id" => $event_id,
            "member_visible" => $action == "accept" ? INVITE_ACCEPTED : INVITE_REJECTED
        ));

insert_event($event_id);

response($result);


