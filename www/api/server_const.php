<?php

$image_placeholder = "http://i84.fastpic.ru/big/2016/1122/1d/4d8a8c58478f69fe7cb9518ce50b8f1d.jpg";
$image_placeholder_link_id = put_image("http://i84.fastpic.ru/big/2016/1122/1d/4d8a8c58478f69fe7cb9518ce50b8f1d.jpg");
$avatar_placeholder_link_id = put_image("http://i95.fastpic.ru/big/2017/0519/98/ed292253896262981bf644c68b239998.jpg", true);

string_insert(null, null, "image_placeholder", $image_placeholder);
string_insert(null, null, "image_placeholder_link_id", $image_placeholder_link_id);
string_insert(null, null, "avatar_placeholder_link_id", $avatar_placeholder_link_id);

string_insert(null, null, "google_maps_api_key", "AIzaSyDknyN4hV7UrHICCl7pHlrvcAMP_y_hSiA");
string_insert(null, null, "visible_event_interval_after_event_time", 3600);
string_insert(null, null, "app_menu", "invites,map,likes,dialogs,notifies,settings,support");
string_insert(null, null, "app_menu_default", "invites");

string_insert(null, null, "google_firebase_api_key", "AIzaSyAzo9bjx7lP6Li5x8AlW2jHhSCIroGeTWk");
string_insert(null, null, "app_color", "#ff527dad");
string_insert(null, null, "unban_image_link_id", $image_placeholder_link_id);
string_insert(null, null, "app_license_link_id", $image_placeholder_link_id);

string_insert(null, null, "owner_distance", 100000);
string_insert(null, null, "admin_owner_id", 448672540884);
string_insert(null, null, "super_admin_name", "Михаил Гайдук");
string_insert(null, null, "super_admin_email", "fans.by@mail.ru");
string_insert(null, null, "super_admin_vk_owner_id", 17210363);

string_insert(null, null, "test_question_count", 5);

string_insert(1, null, "app_package", "hobby");
string_insert(1, null, "app_name", "Hobby");
string_insert(1, null, "vk_app_id", 5724066);
string_insert(1, null, "fb_app_id", "1153876024679161");
string_insert(1, null, "app_description", "welcome to Hobby.fm");
string_insert(1, null, "app_logo_link_id", $avatar_placeholder_link_id);


//string_insert.*?,.*?,.*?

include_file("server_const_ru.php");
include_file("server_const_en.php");

$result["response"] = true;
response($result);
