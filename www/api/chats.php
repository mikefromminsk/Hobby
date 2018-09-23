<?php


$owner_id = get_int_requared("owner_id");


$result["chats"] = select("select "
    . "  t5.message_id, t5.attach_type, t5.attach_id,"
    . " ( select count(*) from messages t3 where t1.attach_type = t3.attach_type and t1.attach_id = t3.attach_id and t3.message_read_time is null and t3.owner_id <> $owner_id) unread_messages, "
    . " ( select group_concat(owner_id separator ',') from members t4 where t1.attach_type = t4.attach_type and t1.attach_id = t4.attach_id and t4.member_visible = 1 group by t4.attach_type) as members"
    . " from members t1 "
    . " left join messages t5 on t5.message_id = ( select message_id from messages t2 where t1.attach_type = t2.attach_type and t1.attach_id = t2.attach_id order by message_send_time desc limit 1)"
    . " where t1.owner_id = $owner_id and t5.message_id is not null order by t5.message_send_time desc limit 100");

foreach ($result["chats"] as &$chat) {
    $chat["members"] = explode(",", $chat["members"]);
    insert_owners($chat["members"]);
    insert_message($chat["message_id"]);
    if ($chat["attach_type"] == ATTACH_TYPE_INVITE)
        insert_event($chat["attach_id"]);
}

response($result);
