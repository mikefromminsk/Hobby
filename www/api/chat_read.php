<?php

$attach_type = get_int_requared("attach_type");
$attach_id = get_int_requared("attach_id");
$owner_id = get_int_requared("owner_id");

if ($attach_type == ATTACH_TYPE_DIALOG)
    $attach_id = search_dialog_id($owner_id, $attach_id);

update("update messages set message_read_time = unix_timestamp()"
    . " where attach_type = $attach_type and attach_id = $attach_id "
    . " and owner_id <> $owner_id and message_read_time is null");
$members = selectList("select owner_id from members where attach_type = $attach_type and attach_id = $attach_id ");
notify($members,
    PUSH_TYPE_MESSAGE_READ,
    null,
    null,
    null,
    array(
        "attach_type" => $attach_type,
        "attach_id" => $attach_id,
        "owner_id" => $owner_id,
    ));

$result["response"] = true;

response($result);