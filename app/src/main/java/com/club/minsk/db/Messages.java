package com.club.minsk.db;

import com.club.minsk.db.utils.ApiRequest;
import com.club.minsk.db.tables.MessagesTable;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.EmojiReplacements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messages extends MessagesTable {

    private static Map<Long, Message> messages = new HashMap<>();

    public static Message get(Long message_id) {
        return messages.get(message_id);
    }

    public static void put(Map<Long, Message> new_messsages) {
        if (new_messsages != null)
            messages.putAll(new_messsages);
    }

    public Message newMessageInstance() {
        return new Message();
    }

    private static Messages instance = new Messages();

    public static Messages getInstance() {
        return instance;
    }

    public void chatRead(Long attach_type, Long attach_id) {
        url("chat_read.php");
        add("attach_type", attach_type);
        add("attach_id", attach_id);
        get();
    }

    public class Response {
        public Long message_id;
    }

    public interface Listener {
        void run(Response response);
    }


    public class MessageList {
        public List<Long> message_id_list;
        public List<Long> members;
    }


    public interface ListListener {
        void run(MessageList response);
    }


    public void selectChatMessages(Long attach_type, Long attach_id,
                                   final ListListener listener) {
        url("messages.php");
        add("attach_type", attach_type);
        add("attach_id", attach_id);
        get(MessageList.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((MessageList) response);
            }
        });
    }


    public void insertDialogMessage(Long attach_type,
                                    Long attach_id,
                                    String message_text,
                                    final Listener listener, ErrorListener error) {
        url("message_insert.php");
        add("attach_type", attach_type);
        add("attach_id", attach_id);
        add("message_text", AndroidUtils.base64Encode(EmojiReplacements.replaceInText(message_text, true)));
        err(error);
        get(Response.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }


    public class ChatsList {
        public List<Chat> chats;

        public class Chat {
            public Long message_id;
            public Long attach_type;
            public Long attach_id;
            public Long unread_messages;
            public List<Long> members;
        }
    }

    public interface ChatsListener {
        void run(ChatsList response);
    }

    public void chats(final ChatsListener listener) {
        url("chats.php");
        get(ChatsList.class, new ApiRequest.Listener() {
            @Override
            public void run(Object response) {
                listener.run((ChatsList) response);
            }
        });
    }


}
