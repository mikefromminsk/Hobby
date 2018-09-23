<?php
$owner_id = get_int("owner_id");
$to_owner_id = get_int_requared("to_owner_id");

$result["owner_id"] = $to_owner_id;
insert_owner($to_owner_id);

response($result);
