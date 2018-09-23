package com.club.minsk.db.tables;

//EventsTable.java

import com.club.minsk.db.utils.ApiRequest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public class EventsTable extends ApiRequest {

    public static final long EVENT_ATTACH_TYPE_INVITE = 0L;
    public static final long EVENT_ATTACH_TYPE_EVENT = 1L;
    public static final long EVENT_ERROR_MORE_INSERT_IN_INTERVAL = 1L;

    public class Event implements Serializable{
        public Long event_id;
        public Long event_time;
        public Double event_lat;
        public Double event_lon;
        public Long owner_id;
        public String event_address;
        public Long event_filter_min_year;
        public String event_filter_sex;
        public Long event_filter_max_members;
        public String event_title;
        public Long event_image_link_id;
        public Long event_visible;
    }

    public class Response{
        public Event event;
    }

    public interface Listener {
        void run(Response response);
    }

    protected void insert(@NonNull Long event_attach_type,
                          @NonNull Long event_type_id,
                          @NonNull Long event_time,
                          @NonNull Double event_lat,
                          @NonNull Double event_lon,
                          @Nullable Long event_attach_id,
                          @Nullable Long event_end_time,
                          @Nullable String event_price,
                          @Nullable String event_address,
                          @Nullable Long event_filter_min_year,
                          @Nullable String event_filter_sex,
                          @Nullable Long event_filter_max_members,
                          @Nullable String event_title,
                          @Nullable String event_description,
                          @Nullable Long event_image_link_id,
                          Long event_visible,
                          final Listener listener) {
        url("event_insert.php");
        add("event_attach_type", event_attach_type);
        add("event_type_id", event_type_id);
        add("event_time", event_time);
        add("event_lat", event_lat);
        add("event_lon", event_lon);
        add("event_attach_id", event_attach_id);
        add("event_end_time", event_end_time);
        add("event_price", event_price);
        add("event_address", event_address);
        add("event_filter_min_year", event_filter_min_year);
        add("event_filter_sex", event_filter_sex);
        add("event_filter_max_members", event_filter_max_members);
        add("event_title", event_title);
        add("event_description", event_description);
        add("event_image_link_id", event_image_link_id);
        add("event_visible", event_visible);
        get(Event.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }


    protected void update(Long event_id,
                          Long event_attach_type,
                          Long event_type_id,
                          Long event_time,
                          Double event_lat,
                          Double event_lon,
                          Long event_attach_id,
                          Long event_end_time,
                          String event_price,
                          String event_address,
                          Long event_filter_min_year,
                          String event_filter_sex,
                          Long event_filter_max_members,
                          String event_title,
                          String event_description,
                          Long event_image_link_id,
                          Long event_visible,
                          final ExecListener listener) {
        url("event_update.php");
        add("event_id", event_id);
        add("event_attach_type", event_attach_type);
        add("event_type_id", event_type_id);
        add("event_time", event_time);
        add("event_lat", event_lat);
        add("event_lon", event_lon);
        add("event_attach_id", event_attach_id);
        add("event_end_time", event_end_time);
        add("event_price", event_price);
        add("event_address", event_address);
        add("event_filter_min_year", event_filter_min_year);
        add("event_filter_sex", event_filter_sex);
        add("event_filter_max_members", event_filter_max_members);
        add("event_title", event_title);
        add("event_description", event_description);
        add("event_image_link_id", event_image_link_id);
        add("event_visible", event_visible);
        get(listener);
    }


    public void delete(Long event_id, boolean event_visible, ExecListener listener){
        url("event_update.php");
        add("event_id", event_id);
        add("event_visible", event_visible);
        get(listener);
    }

    public class ListResponse {
        public List<Event> response;
    }


    public interface ListListener {
        void run(ListResponse response);
    }


    // Overload this function
    protected void select(Long event_id,
                          Long event_attach_type,
                          Long event_type_id,
                          Long event_time,
                          Double event_lat,
                          Double event_lon,
                          Long event_attach_id,
                          Long event_end_time,
                          String event_price,
                          String event_address,
                          Long event_filter_min_year,
                          String event_filter_sex,
                          Long event_filter_max_members,
                          String event_title,
                          String event_description,
                          Long event_image_link_id,
                          Long event_visible,
                          final ListListener listener) {
        url("events.php");
        add("event_id", event_id);
        add("event_attach_type", event_attach_type);
        add("event_type_id", event_type_id);
        add("event_time", event_time);
        add("event_lat", event_lat);
        add("event_lon", event_lon);
        add("event_attach_id", event_attach_id);
        add("event_end_time", event_end_time);
        add("event_price", event_price);
        add("event_address", event_address);
        add("event_filter_min_year", event_filter_min_year);
        add("event_filter_sex", event_filter_sex);
        add("event_filter_max_members", event_filter_max_members);
        add("event_title", event_title);
        add("event_description", event_description);
        add("event_image_link_id", event_image_link_id);
        add("event_visible", event_visible);
        get(ListResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }
}

