package com.club.minsk.db.tables;

//EventTypesTable.java

import com.club.minsk.db.utils.ApiRequest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

public class EventTypesTable extends ApiRequest {

    public static final long EVENT_ATTACH_TYPE_INVITE = 0L;
    public static final long EVENT_ATTACH_TYPE_EVENT = 1L;

    public class EventType{
        public Long app_id;
        public Long event_type_id;
        public Long event_attach_type;
        public String event_type_string_name;
        public Long event_type_image_link_id;
        public String event_type_slug;
        public Long event_type_visible;
    }

    public class Response{
        public EventType eventType;
    }

    public interface Listener {
        void run(Response response);
    }

    protected void insert(@NonNull Long event_attach_type,
                          @NonNull String event_type_string_name,
                          @NonNull Long event_type_image_link_id,
                          @Nullable String event_type_slug,
                          Long event_type_visible,
                          final Listener listener) {
        url("event_type_insert.php");
        add("event_attach_type", event_attach_type);
        add("event_type_string_name", event_type_string_name);
        add("event_type_image_link_id", event_type_image_link_id);
        add("event_type_slug", event_type_slug);
        add("event_type_visible", event_type_visible);
        get(EventType.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }


    protected void update(Long event_type_id,
                          Long event_attach_type,
                          String event_type_string_name,
                          Long event_type_image_link_id,
                          String event_type_slug,
                          Long event_type_visible,
                          final ExecListener listener) {
        url("event_type_update.php");
        add("event_type_id", event_type_id);
        add("event_attach_type", event_attach_type);
        add("event_type_string_name", event_type_string_name);
        add("event_type_image_link_id", event_type_image_link_id);
        add("event_type_slug", event_type_slug);
        add("event_type_visible", event_type_visible);
        get(listener);
    }


    public void delete(Long event_type_id, boolean event_type_visible, ExecListener listener){
        url("event_type_update.php");
        add("event_type_id", event_type_id);
        add("event_type_visible", event_type_visible);
        get(listener);
    }

    public class ListResponse {
        public List<EventType> response;
    }


    public interface ListListener {
        void run(ListResponse response);
    }


    // Overload this function
    protected void select(Long event_type_id,
                          Long event_attach_type,
                          String event_type_string_name,
                          Long event_type_image_link_id,
                          String event_type_slug,
                          Long event_type_visible,
                          final ListListener listener) {
        url("event_types.php");
        add("event_type_id", event_type_id);
        add("event_attach_type", event_attach_type);
        add("event_type_string_name", event_type_string_name);
        add("event_type_image_link_id", event_type_image_link_id);
        add("event_type_slug", event_type_slug);
        add("event_type_visible", event_type_visible);
        get(ListResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }
}

