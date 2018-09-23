<?php

include "api/index.php";


header("Content-type: html; charset=utf-8");
$event_id = get_int("id");
$event = selectMap("select * from events where event_id = $event_id");
$owner = selectMap("select * from owners where owner_id = " . $event["owner_id"]);
$url = "http://192.168.1.10/invite?id=" . $event_id;

if ($event_id != null &&
    scalar("select count(*) from members where attach_type = " . ATTACH_TYPE_LINK . " and attach_id = $event_id") == 0
)
    insertList("members", array(
        "member_id" => random_key("members", "member_id"),
        "attach_type" => ATTACH_TYPE_LINK,
        "attach_id" => $event_id,
        "owner_id" => ip2long(getIP()),
        "member_time" => "unix_timestamp()",
    ))

?>
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" href="img/logo.png">
    <title>Приглашение</title>
    <link rel="stylesheet" href="css/angular-material.min.css">
    <link rel="stylesheet" href="css/ripple.css">

    <link rel="stylesheet" href="css/classic/cubeslider.css" type="text/css" media="all"/>

    <style>
        @media screen and (orientation: portrait) {
            .hide-mobile {
                display: none;
            }
            .action-button {
                width: 100%;
            }
        }

    </style>
</head>
<body style="background: #2e5d65 url('img/glare_background_blur_dark_63553_2560x1440.jpg') no-repeat;
            background-size: auto 100%;" class="layout-row">
<div class="flex layout-row">
    <div class="layout-row layout-align-center-center hide-mobile">

        <div id="demo" style="transform: rotate(-10deg);">
            <img style="height: 400px; width: auto" src="img/IJYK33ZKKo4.jpg"/>
            <img style="height: 400px; width: auto" src="img/IJYK33ZKKo4.jpg"/>
            <img style="height: 400px; width: auto" src="img/IJYK33ZKKo4.jpg"/>
            <img style="height: 400px; width: auto" src="img/IJYK33ZKKo4.jpg"/>
        </div>
    </div>
    <div class="layout-column flex">
        <div class="layout-column flex layout-align-center-center"
             style="color: #ffffff; text-shadow:0 0 5px #00c6ff;">
            <div
                style="margin: 0 20px 0 20px; font-size: 3.5em">
                Го гулять Минск
            </div>
            <div
                style="margin: 20px 20px 20px 20px; max-width: 400px; font-size: 2em">
                Приложение для поиска компании на вечер.
            </div>
            <div
                style="margin: 0 20px 0 20px; max-width: 400px; font-size: 2em">
                Нас уже <span id="owner_count"
                              style="font-size: 1.5em"><?= scalar("select count(*) from owners where app_id = " . $owner["app_id"]) ?></span>
            </div>
        </div>
        <div style="padding: 20px;">
            <a id="download_button" style="text-decoration: none;" target="_blank"
               class="layout-row layout-align-end-end"
               href="https://play.google.com/store/apps/details?id=com.club.minsk">
                <button class="action-button">Скачать приложение</button>
            </a>
            <a id="open_button" style="text-decoration: none; display: none" target="_blank"
               class="layout-row layout-align-end-end">
                <button class="action-button">Открыть в приложении</button>
            </a>
        </div>
    </div>
</div>
<div class="layout-column card hide-mobile" style="height: 100%; width:300px">
    <div class="flex"></div>
    <div class="flex" style="text-align: center; font-size: 24px">
        Ваше приглашение
    </div>
    <div style="text-align: center" class="layout-column">
        Шаг 1
        <a class="layout-align-center-center " style="text-decoration: none;" target="_blank"
           href="https://play.google.com/store/apps/details?id=com.club.minsk">
            <button style="background-color: #ffffff; color: #000000" class="flex">Скачай приложение</button>
        </a>
    </div>
    <div class="flex"></div>
    <div style="text-align: center" class="layout-column">
        Шаг 2<br>
        Отсканируй QR код
        <div class="layout-row layout-align-center-center ">
            <img style="width: 200px; height: 200px;"
                 src="https://chart.googleapis.com/chart?chs=200x200&cht=qr&choe=UTF-8&chl=<?= urlencode($url) ?>"/>
        </div>
    </div>
    <div class="flex"></div>
</div>

<script src="js/jquery.js" type="text/javascript"></script>
<script src="js/cubeslider-min.js" type="text/javascript"></script>
<script>
    function doSomething() {
        var rand = Math.round(Math.random() * (2 - 1)) + 1;
        $('#owner_count').text(parseInt($('#owner_count').text()) + rand);
    }

    function loop() {
        var rand = Math.round(Math.random() * (7000 - 3500)) + 3500;
        setTimeout(function () {
            doSomething();
            loop();
        }, rand);
    }
    loop();


    $('#demo').cubeslider({
        cubesNum: {rows: 5, cols: 1},
        orientation: 'h',
        spreadPixel: 0,
        cubeSync: 100,
        perspective: 600,
        autoplay: true
    });
/*
    if (navigator.userAgent.toLowerCase().indexOf("android") > -1) {
        $('.hide-mobile').css("display", "none");
    }*/

    var xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://localhost:18846', true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState != 4) return;
        if (xhr.status == 200) {
            $('#download_button').css("display", "none");
            $('#open_button')
                .css("display", "inherit")
                .attr("href", window.location.href);
        }
    };
    xhr.send();

</script>
</body>
</html>