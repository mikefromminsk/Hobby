<?php

$attach_type = get_int_requared("attach_type");
$attach_id = get_int_requared("attach_id");
$owner_id = get_int_requared("owner_id");
$message_text = get_string_requared("message_text");

$message_id = random_key("messages", "message_id");
$message_send_time = time();

if ($attach_type == ATTACH_TYPE_DIALOG)
    $attach_id = search_dialog_id($owner_id, $attach_id);

$result["response"] = insertList("messages", array(
    "message_id" => $message_id,
    "attach_type" => $attach_type,
    "attach_id" => $attach_id,
    "owner_id" => $owner_id,
    "message_text" => $message_text,
    "message_send_time" => $message_send_time
));

$result["message_id"] = $message_id;
insert_message($message_id);

$members = selectList("select owner_id from members where attach_type = $attach_type and attach_id = $attach_id");
$owner = selectMap("select * from owners where owner_id = $owner_id");
insert_owners($members);

notify($members,
    PUSH_TYPE_MESSAGE_INSERT,
    $owner["owner_avatar_link_id"],
    $owner["owner_name"],
    $message_text,
    array(
        "message_id" => $message_id,
        "attach_type" => $attach_type,
        "attach_id" => get_int_requared("attach_id"),
        "owner_id" => $owner_id,
        "message_text" => $message_text,
        "message_send_time" => $message_send_time,
    ));


response($result);
