<?php

$owner_id = get_int_requared("owner_id");
$attach_type = get_int_requared("attach_type");
$attach_id = get_int_requared("attach_id");
$member_visible = get_int("member_visible");

$member = selectMap("select * from members where attach_type = $attach_type and attach_id = $attach_id and owner_id = $owner_id");

$member_id = $member["member_id"];
if ($member_visible === null)
    $member_visible = $member == null ? 1 : ($member["member_visible"] == 1 ? 0 : 1);

if ($member != null) {

    $result["response"] = updateList("members", array(
        "member_visible" => $member_visible,
        "member_time" => "unix_timestamp()",
    ), "member_id", $member["member_id"]);

} else {

    $member_id = random_key("members", "member_id");

    $result["response"] = insertList("members", array(
        "member_id" => $member_id,
        "attach_type" => $attach_type,
        "attach_id" => $attach_id,
        "owner_id" => $owner_id,
        "member_visible" => $member_visible,
        "member_time" => "unix_timestamp()",
    ));
}


if ($member_visible == 1) {
    $push_type = PUSH_TYPE_MEMBER_INSERT;
    if ($attach_type == ATTACH_TYPE_LIKE_OWNER)
        $push_type = PUSH_TYPE_OWNER_LIKE;

    if ($attach_type == ATTACH_TYPE_FRIENDS || $attach_type == ATTACH_TYPE_LIKE_OWNER){
        $members = $attach_id;
        $is_confirm = scalar("select count(*) from members where attach_type = $attach_type and attach_id = $owner_id and owner_id = $attach_id and member_visible = 1");
    } else {
        $members = selectList("select owner_id from members where attach_type = $attach_type and attach_id = $attach_id  and member_visible = 1");
        $is_confirm = 0;
    }

    $owner = selectMap("select * from owners where owner_id = $owner_id");
    notify($members,
        $push_type,
        $owner["owner_avatar_link_id"],
        $owner["owner_name"],
        null,
        array(
            "attach_type" => $attach_type,
            "attach_id" => $attach_id,
            "owner_id" => $owner_id,
            "is_confirm" => $is_confirm,
        ));
}
insert_owner($attach_id);
insert_event($attach_id);

$result["member_visible"] = $member_visible;

response($result);