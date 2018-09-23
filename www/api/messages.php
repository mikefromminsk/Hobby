<?php


$device_id = get_string_requared('device_id');
$app_id = get_int_requared('app_id');
$owner_id = get_int_requared('owner_id');
$attach_type = get_int_requared('attach_type');
$attach_id = get_int_requared('attach_id');

if ($attach_type == ATTACH_TYPE_DIALOG)
{
    $to_owner_id = $attach_id;
    $attach_id = search_dialog_id($owner_id, $to_owner_id);
    if ($attach_id == null) {
        $attach_id = random_key("members", "member_id");
        insertList("members", array(
            "member_id" => random_key("members", "member_id"),
            "attach_type" => $attach_type,
            "attach_id" => $attach_id,
            "owner_id" => $owner_id,
            "member_time" => "unix_timestamp()"
        ));
        insertList("members", array(
            "member_id" => random_key("members", "member_id"),
            "attach_type" => $attach_type,
            "attach_id" => $attach_id,
            "owner_id" => $to_owner_id,
            "member_time" => "unix_timestamp()"
        ));
    }
}

$result["message_id_list"] = selectList("select message_id from messages
where attach_type = $attach_type and attach_id = $attach_id
order by message_send_time desc limit 50");
$result["message_id_list"] = array_reverse($result["message_id_list"]);
insert_messages($result["message_id_list"]);

$result["members"] = selectList("select owner_id from members where attach_type = $attach_type and attach_id = $attach_id ");
insert_owners($result["members"]);

if ($attach_type == ATTACH_TYPE_INVITE)
    insert_event($attach_id);

update("update messages set message_read_time = unix_timestamp()"
    . " where attach_type = $attach_type and attach_id = $attach_id "
    . " and owner_id <> $owner_id and message_read_time is null");
notify($result["members"],
    PUSH_TYPE_MESSAGE_READ,
    null,
    null,
    null,
    array(
        "attach_type" => $attach_type,
        "attach_id" => $attach_id,
        "owner_id" => $owner_id,
    ));

response($result);
