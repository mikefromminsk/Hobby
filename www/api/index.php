<?php
set_include_path($_SERVER["DOCUMENT_ROOT"]);
include_once "db.php";

function include_file($script_name)
{
    $script_path = get_include_path() . "/api/" . $script_name;
    if (file_exists($script_path))
        include_once $script_path;
    else
        die("include file not found $script_path");
}

include_file("const.php");

function response_file($script_name, $params = array())
{
    $params = array_merge($params, array(
        "app_id" => get("app_id"),
        "server_request" => 1,
    ));
    $prev_get_params = $_GET;
    $prev_post_params = $_POST;
    $_GET = array();
    $_POST = array();
    foreach ($params as $param_name => $param_value)
        $_GET[$param_name] = $param_value;
    include_file($script_name);
    $_GET = $prev_get_params;
    $_POST = $prev_post_params;
    return $GLOBALS["response"];
}


function to_global_path($filename)
{
    return "../" . $filename;
}

function resize_image($image, $max_size = 800)
{
    $width = imagesx($image);
    $height = imagesy($image);

    $ratio = $width / $height;
    if ($ratio > 1) {
        $new_width = min($max_size, $width);
        $new_height = $new_width / $ratio;
    } else {
        $new_height = min($max_size, $height);
        $new_width = $new_height * $ratio;
    }
    $dst = imagecreatetruecolor($new_width, $new_height);
    imagealphablending($dst, false);
    imagesavealpha($dst, true);
    imagecopyresampled($dst, $image, 0, 0, 0, 0, $new_width, $new_height, $width, $height);

    return $dst;
}

function round_image($image)
{

    $width = imagesx($image);
    $height = imagesy($image);

    $dst_wh = min($width, $height);

    $dst = imagecreatetruecolor($dst_wh, $dst_wh);

    imagecopy($dst, $image, 0, 0, $width / 2 - $dst_wh / 2, $height / 2 - $dst_wh / 2, $dst_wh, $dst_wh);

    $width = $dst_wh;
    $height = $dst_wh;

    $mask = imagecreatetruecolor($width, $height);
    $maskTransparent = imagecolorallocate($mask, 255, 0, 255);
    imagecolortransparent($mask, $maskTransparent);
    imagefilledellipse($mask, $width / 2, $height / 2, $width, $height, $maskTransparent);
    imagecopymerge($dst, $mask, 0, 0, 0, 0, $width, $height, 100);

    $dstTransparent = imagecolorallocate($dst, 255, 0, 255);
    imagefill($dst, 0, 0, $dstTransparent);
    imagefill($dst, $width - 1, 0, $dstTransparent);
    imagefill($dst, 0, $height - 1, $dstTransparent);
    imagefill($dst, $width - 1, $height - 1, $dstTransparent);
    imagecolortransparent($dst, $dstTransparent);

    return $dst;
}

function getSslImage($url)
{
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
    curl_setopt($ch, CURLOPT_HEADER, false);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_REFERER, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
    $result = curl_exec($ch);
    curl_close($ch);
    return $result;
}

function save_image($image, $rounded = false, $jpeg = false, $max_size = 800)
{
    $image = imagecreatefromstring($image);
    if ($image === false)
        error(USER_ERROR);
    $image = resize_image($image, $max_size);
    if ($rounded)
        $image = round_image($image);

    $image_file = to_global_path(UPLOAD_DIR . random_id());

    if ($rounded == false && $jpeg == true)
        imagejpeg($image, $image_file, 75); //quality 0 - 9
    else
        imagepng($image, $image_file, 6); //quality 0 - 9

    imagedestroy($image);

    $new_file_name = UPLOAD_DIR . md5_file($image_file) . ".png";

    if (file_exists($new_file_name))
        $link_id = scalar("select link_id from links where link_local_path = '$new_file_name' limit 1");

    if ($link_id == null) {
        if (rename($image_file, to_global_path($new_file_name)) !== false) {
            $link_id = random_key("links", "link_id");
            insertList("links", array(
                "link_id" => $link_id,
                "link_local_path" => $new_file_name,
            ));
        } else {
            return null;
        }

    }
    return $link_id;
}

function put_image($image_url, $rounded = false, $jpeg = false, $max_size = 800)
{
    if ($image_url == null)
        return null;
    $link_id = scalar("select link_id from links where link_external_url = '$image_url'");

    if ($link_id == null) {
        if (parse_url($image_url, PHP_URL_HOST) != SERVER_HOST) {
            $link_id = scalar("select link_id from links where link_local_path = '" . parse_url($image_url, PHP_URL_PATH) . "'");
        }
        if ($link_id == null) {
            $image = getSslImage($image_url);
            return save_image($image, $rounded, $jpeg, $max_size);
        }
    }
    return $link_id;
}


function insert_owner($owner_id)
{
    if ($owner_id != null && !in_array($owner_id, $GLOBALS["owners"]))
        $GLOBALS["owners"][] = $owner_id;
}

function insert_owners($owner_id_list)
{
    foreach ($owner_id_list as $owner_id)
        insert_owner($owner_id);
}

function insert_link($link_id)
{
    if ($link_id != null && ($GLOBALS["links"] == null || !in_array($link_id, $GLOBALS["links"])))
        $GLOBALS["links"][] = $link_id;
}

function insert_event_types($app_id)
{
    $GLOBALS["event_types"] = $app_id;
}

function get_link($link_id)
{
    if (is_numeric($link_id))
        return SERVER_URL . scalar("select link_local_path from links where link_id = $link_id");
    return null;
}


function insert_event($event_id)
{
    if ($event_id != null && !in_array($event_id, $GLOBALS["events"]))
        $GLOBALS["events"][] = $event_id;
}

function insert_event_list($event_list)
{
    foreach ($event_list as $event_id)
        insert_event($event_id);
}

function insert_device($device_id)
{
    if ($device_id != null && !in_array($device_id, $GLOBALS["devices"]))
        $GLOBALS["devices"][] = $device_id;
}

function insert_device_list($device_list)
{
    foreach ($device_list as $device_id)
        insert_device($device_id);
}

function insert_message($message_id)
{
    if ($message_id != null && !in_array($message_id, $GLOBALS["messages"]))
        $GLOBALS["messages"][] = $message_id;
}

function insert_messages($message_id_list)
{
    foreach ($message_id_list as $message_id)
        insert_message($message_id);
}

function cache_data($owner_id)
{
    $result = array();

    if ($GLOBALS["messages"] != null) {
        $messages = select("select * from messages where message_id in (" . implode(",", $GLOBALS["messages"]) . ")");
        foreach ($messages as $message) {
            $result["messages"][$message["message_id"]] = $message;
            insert_owner($message["owner_id"]);
        }
    }

    if ($GLOBALS["events"] != null) {
        $events = select("select t1.* from events t1 where t1.event_id in (" . implode(",", $GLOBALS["events"]) . ")");
        foreach ($events as $event) {
            $event["members"] = select("select owner_id, member_visible from members where attach_type = ". ATTACH_TYPE_INVITE . " and attach_id = " . $event["event_id"] . " order by member_visible desc, member_time");
            foreach ($event["members"] as $event_member)
                 insert_owner($event_member["owner_id"]);
            $result["events"][$event["event_id"]] = $event;
            insert_link($event["event_image_link_id"]);
            insert_owner($event["owner_id"]);
        }
        $GLOBALS["events"] = null;
    }
    if ($GLOBALS["devices"] != null) {
        $devices = select("select * from devices where device_id in ('" . implode("','", $GLOBALS["devices"]) . "')");
        foreach ($devices as $device) {
            insert_owner($device["owner_id"]);
            $result["devices"][$device["device_id"]] = $device;
        }
    }
    if ($GLOBALS["owners"] != null) {
        $owners = select("select t1.*"
            . ($owner_id != null ? ", (select group_concat(attach_type separator ',') from members where attach_id = t1.owner_id and owner_id = $owner_id and member_visible = 1 group by owner_id) as member_request " : "")
            . ($owner_id != null ? ", (select group_concat(attach_type separator ',') from members where attach_id = $owner_id and owner_id = t1.owner_id and member_visible = 1 group by owner_id) as member_confirm " : "")
            . " from owners t1 where t1.owner_id in (" . implode(",", $GLOBALS["owners"]) . ")");
        foreach ($owners as &$owner) {
            $member_request = explode(",", $owner["member_request"]);
            $member_confirm = explode(",", $owner["member_confirm"]);
            $owner["is_friend"] =
                (array_search("" . ATTACH_TYPE_FRIENDS, $member_request) !== false &&
                    array_search("" . ATTACH_TYPE_FRIENDS, $member_confirm) !== false) ? 1 : 0;
            $owner["is_friend_request"] = (array_search("" . ATTACH_TYPE_FRIENDS, $member_request) !== false) ? 1 : 0;
            $owner["is_liked"] =
                (array_search("" . ATTACH_TYPE_LIKE_OWNER, $member_request) !== false &&
                    array_search("" . ATTACH_TYPE_LIKE_OWNER, $member_confirm) !== false) ? 1 : 0;
            $owner["is_like_request"] = array_search("" . ATTACH_TYPE_LIKE_OWNER, $member_request) !== false ? 1 : 0;
            $result["owners"][$owner["owner_id"]] = $owner;
            insert_link($owner["owner_avatar_link_id"]);
            insert_link($owner["owner_photo_link_id"]);
        }
        $GLOBALS["owners"] = null;
    }
    if ($GLOBALS["links"] != null) {
        $links = select("select * from links where link_id in (" . implode(",", $GLOBALS["links"]) . ")");
        foreach ($links as $link) {
            $result["links"][$link["link_id"]]["link_url"] = SERVER_URL . $link["link_local_path"];
        }
        $GLOBALS["links"] = null;
    }

    return $result;
}


function microtime_long()
{
    list($usec, $sec) = explode(" ", microtime());
    return ($sec + $usec) * 10000;
}


function mysql_unreal_escape_string($string)
{
    $characters = array('x00', 'n', 'r', '\\', '\'', '"', 'x1a');
    $o_chars = array("\x00", "\n", "\r", "\\", "'", "\"", "\x1a");
    for ($i = 0; $i < strlen($string); $i++) {
        if (substr($string, $i, 1) == '\\') {
            foreach ($characters as $index => $char) {
                if ($i <= strlen($string) - strlen($char) && substr($string, $i + 1, strlen($char)) == $char) {
                    $string = substr_replace($string, $o_chars[$index], $i, strlen($char) + 1);
                    break;
                }
            }
        }
    }
    return $string;
}

function response($result)
{
    $result = array_extend($result, cache_data(get_int("owner_id")));
    if (get('server_request') != null)
        $GLOBALS["response"] = $result;
    else
        echo json_encode_readable($result);
}

function error($error_code, $error_message = "", $error_photo = "", $error_redirect = "", $error_redirect_title = "")
{
    $db_error_messages = array(
        ERROR_PARAMS_IS_NO_SET => "params is not set",
        ERROR_DUPLICATE_REQUEST => "duplicate request",
        ERROR_MYSQL => "sql error",
    );
    if ($error_message == null)
        $error_message = $db_error_messages[$error_code] == '' ? "unknown error" : $db_error_messages[$error_code];

    db_error($error_code, $error_message, $error_photo, $error_redirect, $error_redirect_title);
}


function dist($lat1, $lon1, $lat2, $lon2)
{
    return " round(
        ( 6371000 * acos( cos( radians($lat2) )
                * cos( radians( $lat1 ) )
                * cos( radians( $lon1 ) - radians($lon2) )
                + sin( radians( $lat2) )
                * sin( radians( $lat1 ) ) ) )
        ) ";
}


function write_php_ini($array, $file)
{
    $res = array("<?php");
    foreach ($array as $key => $val) {
        if (is_array($val)) {
            $res[] = "[$key]";
            foreach ($val as $skey => $sval) $res[] = "$skey = " . (is_numeric($sval) ? $sval : '"' . $sval . '"');
        } else $res[] = "$key = " . (is_numeric($val) ? $val : '"' . $val . '"');
    }
    safefilerewrite($file, implode("\r\n", $res));
}

function safefilerewrite($fileName, $dataToSave)
{
    if ($fp = fopen($fileName, 'w')) {
        $startTime = microtime(TRUE);
        do {
            $canWrite = flock($fp, LOCK_EX);
            // If lock not obtained sleep for 0 - 100 milliseconds, to avoid collision and CPU load
            if (!$canWrite) usleep(round(rand(0, 100) * 1000));
        } while ((!$canWrite) and ((microtime(TRUE) - $startTime) < 5));

        //file was locked so now we can store information
        if ($canWrite) {
            fwrite($fp, $dataToSave);
            flock($fp, LOCK_UN);
        }
        fclose($fp);
    }
}

function getIP(){
    $ip = $_SERVER['REMOTE_ADDR'];
    if ($ip == '::1'
        || $ip == '192.168.1.10'
        || $ip == '192.168.1.5'
        || $ip == '127.0.0.1'
    )
        $ip = "93.84.13.235";
    return $ip;
}

function getCity()
{
    $city_info = $GLOBALS["city_info"];
    if ($city_info == null){
        include_once("lib/sxgeo/sxgeo.php");
        $SxGeo = new SxGeo($_SERVER["DOCUMENT_ROOT"] . '/lib/sxgeo/sxgeocity.dat');
        $city_info = $SxGeo->getCityFull(getIP());
        $GLOBALS["city_info"] = $city_info;
    }
    /*
     * {"city":{"id":625144,"lat":53.9,"lon":27.56667,"name_ru":"\u041c\u0438\u043d\u0441\u043a","name_en":"Minsk"},
     * "region":{"id":625143,"name_ru":"\u041c\u0438\u043d\u0441\u043a","name_en":"Horad Minsk","iso":"BY-HM"},
     * "country":{"id":36,"iso":"BY","lat":53,"lon":28,"name_ru":"\u0411\u0435\u043b\u0430\u0440\u0443\u0441\u044c","name_en":"Belarus"}}
     * */
    return $city_info;
}

function notify($owner_ids, $push_type, $image_link_id, $title, $text, $data = null)
{
    if ($owner_ids == null)
        return false;

    if (!is_array($owner_ids))
        $owner_ids = array($owner_ids);

    if (count($owner_ids) == 0)
        return false;

    $owner_ids = selectList("select owner_id from owners where owner_id in (" . implode(",", $owner_ids) . ")");

    $device_id = get_string_requared("device_id");
    if (!is_array($owner_ids))
        $owner_ids = array($owner_ids);

    if (count($owner_ids) == 0)
        return false;

    $data[PUSH_TYPE] = $push_type;
    if ($image_link_id != null)
        $data[PUSH_IMAGE] = get_link($image_link_id);;
    $data[PUSH_TITLE] = $title;
    $data[PUSH_TEXT] = $text;

    if (isset($data["message_type"]))
        error(USER_ERROR);

    $device_tokens = selectList("select device_token from devices
    where owner_id in (" . implode(",", $owner_ids) . ")
    and device_token is not null and device_id <> '$device_id'");
    $fields = array
    (
        'registration_ids' => $device_tokens,
        'data' => $data
    );
    $headers = array
    (
        'Authorization: key=AIzaSyAzo9bjx7lP6Li5x8AlW2jHhSCIroGeTWk',
        'Content-type: application/json; charset=utf-8'
    );

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, 'https://android.googleapis.com/gcm/send');
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
    $result = curl_exec($ch);
    curl_close($ch);
    return $result;
}

function search_dialog_id($owner_id, $to_owner_id)
{
    return scalar("select t1.attach_id from members t1 "
        . " join members t2 on t2.owner_id = $to_owner_id and t2.attach_type = " . ATTACH_TYPE_DIALOG
        . " where t1.owner_id = $owner_id and t1.attach_type = " . ATTACH_TYPE_DIALOG . " and t1.attach_id = t2.attach_id");
}

include_file(get("script_name", get("s")));