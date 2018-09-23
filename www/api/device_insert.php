<?php

$app_id = get_int_requared("app_id");
$device_id = get_string_requared("device_id");
$owner_id = get_int("owner_id");
$device_token = get_string("device_token");
$device_lat = get_double("device_lat");
$device_lon = get_double("device_lon");
$device_gps_time = get_double("device_gps_time");

$device_exist = scalar("select count(*) from devices where device_id = '$device_id' and app_id = $app_id");

if ($device_exist != 0) {
    $result["response"] = updateList("devices", array(
        "owner_id" => $owner_id,
        "device_token" => $device_token,
        "device_lat" => $device_lat,
        "device_lon" => $device_lon,
        "device_gps_time" => $device_gps_time,
        "device_update_time" => "unix_timestamp()",
    ), "device_id", $device_id);



} else {

    if ($device_lat == null || $device_lon == null) {
        $city = getCity()["city"];
        $device_lat = $city["lat"];
        $device_lon = $city["lon"];
    }
    $result["response"] = insertList("devices", array(
        "device_id" => $device_id,
        "app_id" => $app_id,
        "owner_id" => $owner_id,
        "device_token" => $device_token,
        "device_lat" => $device_lat,
        "device_lon" => $device_lon,
        "device_gps_time" => $device_gps_time,
        "device_update_time" => "unix_timestamp()",
    ));
}

insert_device($device_id);

response($result);

