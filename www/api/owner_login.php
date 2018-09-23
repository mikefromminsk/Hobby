<?php
$result;

$app_id = get_int_requared("app_id");
$owner_email = get_string("owner_email");
$vk_owner_id = get_int("vk_owner_id");
$fb_owner_id = get_int("fb_owner_id");
$owner_phone = get_string("owner_phone");
$owner_id = get_int('owner_id');

if ($vk_owner_id == null
    && $fb_owner_id == null
    && $owner_email == null
    && $owner_id == null
    && $owner_phone == null
)
    error(ERROR_PARAMS_IS_NO_SET);

$old_owner_id = null;
if ($vk_owner_id != null)
    $old_owner_id = scalar("select owner_id from owners where vk_owner_id = $vk_owner_id and app_id = $app_id");
if ($old_owner_id == null && $fb_owner_id != null)
    $old_owner_id = scalar("select owner_id from owners where fb_owner_id = $fb_owner_id and app_id = $app_id");
if ($old_owner_id == null && $owner_email != null)
    $old_owner_id = scalar("select owner_id from owners where owner_email = '$owner_email' and app_id = $app_id");
if ($old_owner_id == null && $owner_phone != null)
    $old_owner_id = scalar("select owner_id from owners where owner_phone = '$owner_phone' and app_id = $app_id");
if ($old_owner_id == null && $owner_id != null) {
    $old_owner_id = scalar("select owner_id from owners where owner_id = $owner_id");
    if ($old_owner_id == null)
        error(ERROR_TOKEN_IS_BAD);
}

if ($old_owner_id != null)
    $owner_id = doubleval($old_owner_id);
else
    $owner_id = null;

if ($owner_id == null) {
    $owner_first_name = get_string_requared("owner_first_name");
    $owner_last_name = get_string("owner_last_name");
    $owner_name = "$owner_first_name $owner_last_name";
    $owner_birthdate = get_int_requared("owner_birthdate");
    $owner_sex = get_string_requared("owner_sex");
    $owner_id = random_key("owners", "owner_id");
    $owner_phone = get_string_requared("owner_phone");

    $owner_avatar_link_id = get_int("owner_avatar_link_id");
    $owner_photo_link_id = get_int("owner_photo_link_id");
    $owner_avatar = get_string("owner_avatar");
    $owner_photo = get_string("owner_photo");
    if ($owner_photo != null)
        $owner_photo_link_id = put_image($owner_photo);

    if ($owner_avatar != null)
        $owner_avatar_link_id = put_image($owner_avatar, true);
    if ($owner_avatar == null && $owner_photo != null)
        $owner_avatar_link_id = put_image($owner_photo, true);


    $result["response"] = insertList("owners", array(
        "app_id" => $app_id,
        "owner_id" => $owner_id,
        "owner_name" => $owner_name,
        "owner_first_name" => $owner_first_name,
        "owner_last_name" => $owner_last_name,
        "owner_sex" => $owner_sex,
        "owner_birthdate" => $owner_birthdate,
        "vk_owner_id" => $vk_owner_id,
        "fb_owner_id" => $fb_owner_id,
        "owner_email" => $owner_email,
        "owner_phone" => $owner_phone,
        "owner_city" => getCity()["city"]["name_ru"],
        "owner_avatar_link_id" => $owner_avatar_link_id,
        "owner_photo_link_id" => $owner_photo_link_id,
        "owner_login_time" => "unix_timestamp()",
    ));


    $social_name = get_string("social_name");
    $social_members = get_int_array("social_members");

    if ($social_name == "vkontakte")
        $members = selectList("select owner_id from owners where app_id = $app_id and vk_owner_id in (" . implode(",", $social_members) . ")");
    if ($social_name == "facebook")
        $members = selectList("select owner_id from owners where app_id = $app_id and fb_owner_id in (" . implode(",", $social_members) . ")");
    if ($members != null) {
        foreach ($members as $member_owner_id)
            insertList("members", array(
                "member_id" => random_key("members", "member_id"),
                "attach_type" => ATTACH_TYPE_FRIENDS,
                "attach_id" => $member_owner_id,
                "owner_id" => $owner_id,
                "member_time" => "unix_timestamp()",
            ));
        notify($members,
            PUSH_TYPE_MEMBER_INSERT,
            $owner_avatar_link_id,
            $owner_name,
            "Hello :)",
            array("owner_id" => $owner_id));
    }

    $device_lat = get_double("device_lat");
    $device_lon = get_double("device_lon");

    if ($device_lat != null && $device_lon != null){
        $neighbors = selectList("select distinct t1.owner_id from devices t1"
            . " where t1.app_id = $app_id "
            . " and " . dist("device_lat", "device_lon", $device_lat, $device_lon) . " < 3000 "
            . " and t1.device_gps_time is not null "
            . " and t1.owner_id <> $owner_id ");

        notify($neighbors,
            PUSH_TYPE_MEMBER_INSERT,
            $owner_avatar_link_id,
            "Новый участник поблизости",
            $owner_name,
            array("owner_id" => $owner_id));
    }

} else {

    $vk_owner_id = get_int("vk_owner_id");
    $fb_owner_id = get_int("fb_owner_id");
    $owner_email = get_string("owner_email");
    $owner_avatar_link_id = get_int("owner_avatar_link_id");
    $owner_photo_link_id = get_int("owner_photo_link_id");
    $owner_avatar = get_string("owner_avatar");
    $owner_photo = get_string("owner_photo");
    if ($owner_avatar != null)
        $owner_avatar_link_id = put_image($owner_avatar, true);
    if ($owner_photo != null)
        $owner_photo_link_id = put_image($owner_photo);
    if ($owner_avatar == null && $owner_photo != null)
        $owner_avatar_link_id = put_image($owner_photo, true);

    updateList("owners", array(
        "app_id" => $app_id,
        "owner_avatar_link_id" => $owner_avatar_link_id,
        "owner_photo_link_id" => $owner_photo_link_id,
        "vk_owner_id" => $vk_owner_id,
        "fb_owner_id" => $fb_owner_id,
        "owner_email" => $owner_email,
        "owner_login_time" => "unix_timestamp()",
    ), "owner_id", $owner_id);
}

$link_invites = selectList("select attach_id from members where attach_type = " . ATTACH_TYPE_LINK . " and owner_id = " . ip2long(getIP()). " and member_visible = 1");
if ($link_invites != null){
    foreach($link_invites as $event_id)
        response_file("member_insert.php", array(
            "device_id" => ip2long(getIP()),
            "attach_type" => ATTACH_TYPE_INVITE,
            "attach_id" => $event_id,
            "owner_id" => $owner_id,
            "member_visible" => INVITE_SEND,
        ));
    update("update members set member_visible = 0 where attach_type = " . ATTACH_TYPE_LINK . " and owner_id = " . ip2long(getIP()). " and member_visible = 1");
}

$result["owner_id"] = $owner_id;

insert_device(get_string_requared("device_id"));
insert_owner($owner_id);

response($result);