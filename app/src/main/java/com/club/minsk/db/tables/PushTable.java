package com.club.minsk.db.tables;

//PushTable.java

import com.club.minsk.db.utils.ApiRequest;
import android.support.annotation.NonNull;

import java.util.List;

public class PushTable extends ApiRequest {

    public static final String PUSH_TIME = "push_time";
    public static final String PUSH_TYPE_SETTING_NAME_PREFIX = "push_type_";
    public static final String PUSH_TYPE_SETTING_VALUE_PRE_PREFIX = "value_";
    public static final String PUSH_TYPE_SETTING_VALUE_PREFIX = "value_push_type_";

    public static final long PUSH_TYPE_MESSAGE_READ = 1L;
    public static final long PUSH_TYPE_MESSAGE_INSERT = 2L;

    public static final long PUSH_TYPE_INVITE_CREATE = 4L;
    public static final long PUSH_TYPE_INVITE_FRIEND_CREATE = 15L;
    public static final long PUSH_TYPE_OWNER_LIKE = 8L;
    public static final long PUSH_TYPE_MEMBER_INSERT = 14L;
    public static final long PUSH_TYPE_INVITE_CANCELED = 12L;
    public static final long PUSH_TYPE_INVITE_UPDATED = 13L;

    public static final String PUSH_TYPE = "push_type";
    public static final String PUSH_VALUE = "push_value";
    public static final String PUSH_IMAGE = "push_image";
    public static final String PUSH_TITLE = "push_title";
    public static final String PUSH_TEXT = "push_text";

    public class Push{
        public String device_id;
        public Long push_time;
        public String push_json_data;
    }

    public class Response{
        public Push push;
    }

    public interface Listener {
        void run(Response response);
    }

    protected void insert(@NonNull Long push_time,
                          @NonNull String push_json_data,
                          final Listener listener) {
        url("push_insert.php");
        add("push_time", push_time);
        add("push_json_data", push_json_data);
        get(Push.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }


    protected void update(Long push_time,
                          String push_json_data,
                          final ExecListener listener) {
        url("push_update.php");
        add("push_time", push_time);
        add("push_json_data", push_json_data);
        get(listener);
    }

    public class ListResponse {
        public List<Push> response;
    }


    public interface ListListener {
        void run(ListResponse response);
    }


    // Overload this function
    protected void select(Long push_time,
                          String push_json_data,
                          final ListListener listener) {
        url("push.php");
        add("push_time", push_time);
        add("push_json_data", push_json_data);
        get(ListResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }
}

