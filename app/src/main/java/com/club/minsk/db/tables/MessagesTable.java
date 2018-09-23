package com.club.minsk.db.tables;

//MessagesTable.java

import com.club.minsk.db.utils.ApiRequest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

public class MessagesTable extends ApiRequest {

    public static final long MESSAGE_ATTACH_TYPE_DIALOG = 0L;
    public static final long MESSAGE_ATTACH_TYPE_CHAT = 1L;
    public static final long MESSAGE_ATTACH_TYPE_EVENT_COMMENT = 2L;
    public static final long MESSAGE_ATTACH_TYPE_ERROR = 3L;
    public static final long MESSAGE_ATTACH_TYPE_FEEDBACK = 4L;
    public static final long MESSAGE_ATTACH_TYPE_NOTIFY = 5L;
    public static final long MESSAGE_ATTACH_TYPE_ANONYMOUS_CHAT = 6L;
    public static final long MESSAGE_TYPE_TEXT = 0L;
    public static final long MESSAGE_VALUE_ERROR_PARAM_IS_NO_SET = 2L;
    public static final long MESSAGE_VALUE_ERROR_CLIENT_VERSION_IS_OLD = 100L;
    public static final long MESSAGE_VALUE_ERROR_ERROR_BANNED = 102L;

    public class Message{
        public Long message_id;
        public Long attach_type;
        public Long attach_id;
        public Long owner_id;
        public String message_text;
        public Long message_read_time;
        public Long message_send_time;
    }

    public class Response{
        public Message message;
    }

    public interface Listener {
        void run(Response response);
    }

    protected void insert(@NonNull Long message_attach_type,
                          @NonNull Long message_type,
                          @Nullable Long message_attach_id,
                          @Nullable Long to_owner_id,
                          @Nullable Long message_value,
                          @Nullable String message_title,
                          @Nullable String message_text,
                          @Nullable Long message_image_link_id,
                          @Nullable Long message_update_time,
                          Long message_visible,
                          final Listener listener) {
        url("message_insert.php");
        add("message_attach_type", message_attach_type);
        add("message_type", message_type);
        add("message_attach_id", message_attach_id);
        add("to_owner_id", to_owner_id);
        add("message_value", message_value);
        add("message_title", message_title);
        add("message_text", message_text);
        add("message_image_link_id", message_image_link_id);
        add("message_update_time", message_update_time);
        add("message_visible", message_visible);
        get(Message.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }


    protected void update(Long message_id,
                          Long message_attach_type,
                          Long message_type,
                          Long message_attach_id,
                          Long to_owner_id,
                          Long message_value,
                          String message_title,
                          String message_text,
                          Long message_image_link_id,
                          Long message_update_time,
                          Long message_send_time,
                          Long message_visible,
                          final ExecListener listener) {
        url("message_update.php");
        add("message_id", message_id);
        add("message_attach_type", message_attach_type);
        add("message_type", message_type);
        add("message_attach_id", message_attach_id);
        add("to_owner_id", to_owner_id);
        add("message_value", message_value);
        add("message_title", message_title);
        add("message_text", message_text);
        add("message_image_link_id", message_image_link_id);
        add("message_update_time", message_update_time);
        add("message_send_time", message_send_time);
        add("message_visible", message_visible);
        get(listener);
    }


    public void delete(Long message_id, boolean message_visible, ExecListener listener){
        url("message_update.php");
        add("message_id", message_id);
        add("message_visible", message_visible);
        get(listener);
    }

    public class ListResponse {
        public List<Message> response;
    }


    public interface ListListener {
        void run(ListResponse response);
    }


    // Overload this function
    protected void select(Long message_id,
                          Long message_attach_type,
                          Long message_type,
                          Long message_attach_id,
                          Long to_owner_id,
                          Long message_value,
                          String message_title,
                          String message_text,
                          Long message_image_link_id,
                          Long message_update_time,
                          Long message_send_time,
                          Long message_visible,
                          final ListListener listener) {
        url("messages.php");
        add("message_id", message_id);
        add("message_attach_type", message_attach_type);
        add("message_type", message_type);
        add("message_attach_id", message_attach_id);
        add("to_owner_id", to_owner_id);
        add("message_value", message_value);
        add("message_title", message_title);
        add("message_text", message_text);
        add("message_image_link_id", message_image_link_id);
        add("message_update_time", message_update_time);
        add("message_send_time", message_send_time);
        add("message_visible", message_visible);
        get(ListResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }
}

