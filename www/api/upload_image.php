<?php
$image = get_string("image_base64");
$image = base64_decode($image);

if ($image == null)
    $result["link_id"] = put_image(get_string_requared("image_url"));
else
    $result["link_id"] = save_image($image);

if ($result["link_id"] == null)
    error(USER_ERROR);

insert_link($result["link_id"]);

response($result);