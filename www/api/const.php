<?php
define("SERVER_ID", '172.17.0.70');

$domains = explode(".", $_SERVER["HTTP_HOST"]);
if (sizeof($domains) == 2 && $domains[1] == 'localhost'
    || sizeof($domains) == 4 && $domains[1] == 'pe' && $domains[2] == 'hu'
    || sizeof($domains) == 3 && $domains[1] != 'pe' && $domains[2] != 'hu'
){
    define("APP_PACKAGE",  $domains[0]);
    unset($domains[0]);
    $HTTP_HOST = implode(".", $domains);
    define("SERVER_HOST",  ($HTTP_HOST == "localhost" ? SERVER_ID : $HTTP_HOST));
}else{
    define("APP_PACKAGE",  "");
    define("SERVER_HOST",  ($_SERVER['HTTP_HOST'] == "localhost" ? SERVER_ID : $_SERVER['HTTP_HOST']));
}

define("SERVER_URL",  "http://" . SERVER_HOST . "/");
define("UPLOAD_DIR", "upload/");
define("ERROR_OWNERS_EMAIL_IS_BUSY", 101);
define("ERROR_OWNERS_BANNED", 102);
define("ERROR_EMAIL_NOT_EXIST", 103);
define("ERROR_EVENT_TO_FAR", 104);

define("OWNER_LANG_DEFAULT",  "RU");
define("OWNER_SEX_DEFAULT",  2);
define("OWNER_RELATION_PREFIX",  "relation_");

define("OWNER_SEX_WOMAN",  "W");
define("OWNER_SEX_MAN",  "M");

define("MESSAGE_TYPE_TEXT",  0);

define("PUSH_TYPE_SETTING_NAME_PREFIX",  "push_type_");
define("PUSH_TYPE_SETTING_VALUE_PRE_PREFIX",  "value_");
define("PUSH_TYPE_SETTING_VALUE_PREFIX",  PUSH_TYPE_SETTING_VALUE_PRE_PREFIX . PUSH_TYPE_SETTING_NAME_PREFIX);

define("PUSH_TYPE_INVITE_CREATE",  4);
define("PUSH_TYPE_INVITE_FRIEND_CREATE",  15);
define("PUSH_TYPE_INVITE_CANCELED",  12);
define("PUSH_TYPE_INVITE_UPDATED",  13);
define("PUSH_TYPE_MESSAGE_READ",  1);
define("PUSH_TYPE_MESSAGE_INSERT",  2);
define("PUSH_TYPE_MEMBER_INSERT",  14);
define("PUSH_TYPE_OWNER_LIKE",  8);

define("PUSH_TYPE",  "push_type");
define("PUSH_IMAGE",  "push_image");
define("PUSH_TITLE",  "push_title");
define("PUSH_TEXT",  "push_text");
define("PUSH_TIME",  "push_time");

define("MESSAGE_VALUE_ERROR_PARAM_IS_NO_SET", 2);
define("MESSAGE_VALUE_ERROR_CLIENT_VERSION_IS_OLD", 100);
define("MESSAGE_VALUE_ERROR_ERROR_BANNED", 102);

define("NOTIFY_TYPE_", 0);

define("STRING_SETTING_NOTIFY_TYPE_NAME_PREFIX",  "setting_notify_type_");
define("STRING_SETTING_NOTIFY_TYPE_DEFAULT_VISIBLE_PREFIX",  "setting_notify_default_");
define("STRING_EVENT_TYPE_PREFIX",  "event_type_");
define("STRING_YEAR_PREFIX",  "year_");
define("STRING_DAY_PREFIX",  "day_");
define("STRING_HOUR_PREFIX",  "hour_");
define("STRING_MIN_PREFIX",  "min_");
define("STRING_SEC_PREFIX",  "sec_");
define("STRING_OWNER_PRIVACY",  "owner_privacy");
define("STRING_MENU_PREFIX",  "menu_");

define("STRING_SETTING_PREFIX",  "setting_");
define("STRING_SETTING_POSITION",  "setting_");
define("STRING_SETTING_PRIVACY",  "setting_");
define("STRING_SETTING_NOTIFY",  "setting_");
define("STRING_SETTING_LANG",  "setting_");
define("STRING_SETTING_FEEDBACK",  "setting_feedback");
define("STRING_SETTING_ABOUT",  "setting_");
define("STRING_SETTING_EXIT",  "setting_");
define("STRING_APP_MENU",  "app_menu");
define("STRING_APP_MENU_DEFAULT",  "app_menu_default");
define("STRING_ADMIN_OWNER_ID",  "admin_owner_id");
define("STRING_EVENT_TYPE_NAME_PREFIX",  "event_type_name_");

define("STRING_LANG_PREFIX",  "slang_");
define("STRING_FEEDBACK_STATUS_PREFIX",  "feedback_status_");

define("EVENT_ERROR_MORE_INSERT_IN_INTERVAL",  1);

define("ATTACH_TYPE_FRIENDS",  0);
define("ATTACH_TYPE_INVITE",  1);
define("ATTACH_TYPE_LIKE_OWNER",  2);
define("ATTACH_TYPE_DIALOG",  4);
define("ATTACH_TYPE_LINK",  5);

define("LINK_TYPE_IMAGE",  0);

define("INVITE_SEND", -2);
define("INVITE_DELIVERED", -1);
define("INVITE_REJECTED", 0);
define("INVITE_ACCEPTED", 1);


