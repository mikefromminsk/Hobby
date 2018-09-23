package com.club.minsk.db;

import com.club.minsk.db.utils.ApiRequest;
import com.club.minsk.db.tables.EventsTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Events extends EventsTable implements Serializable {

    public static final long INVITE_SEND = -2;
    public static final long INVITE_DELIVERED = -1;
    public static final long INVITE_REJECTED = 0;
    public static final long INVITE_ACCEPTED = 1;

    public class Event extends EventsTable.Event implements Serializable {
        public List<MemberStatus> members;
        public List<Long> invite_list;

        public class MemberStatus implements Serializable {
            public Long owner_id;
            public Long member_visible;
        }
    }

    private static Map<Long, Event> events = new HashMap<>();

    public static Events.Event get(long event_id) {
        return events.get(event_id);
    }

    public static void put(Map<Long, Events.Event> events) {
        if (events != null)
            Events.events.putAll(events);
    }

    public static List<Events.Event> getList(List<Long> kudago_event_id_list) {
        List<Events.Event> list = new ArrayList<>();
        if (kudago_event_id_list != null)
            for (Long kudago_event_id : kudago_event_id_list) {
                Events.Event event = get(kudago_event_id);
                if (event != null)
                    list.add(event);
            }
        return list;
    }


    public Event newEventInstance() {
        return new Event();
    }

    private static Events instance = new Events();

    public static Events getInstance() {
        return instance;
    }

    public class ListResponse {
        public List<Long> event_id_list;
        public List<String> device_id_list;
    }


    public interface ListListener {
        void run(ListResponse response);
    }

    public void select(int page_offset, int page_size,
                       final ListListener listener,
                       ErrorListener error) {
        url("events.php");
        add("page_offset", page_offset);
        add("page_size", page_size);
        err(error);
        get(ListResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }


    public void selectBeside(int page_offset, int page_size,
                             final ListListener listener,
                             ErrorListener error) {
        add("beside", 1);
        select(page_offset, page_size, listener, error);
    }


    public class EventResponse {
        public Long event_id;
        public List<Long> message_id_list;
        public List<Long> children_event_id_list;
    }


    public interface EventListener {
        void run(EventResponse response);
    }

    public void selectEvent(Long event_id,
                            String action, final EventListener listener) {
        url("event.php");
        add("event_id", event_id);
        add("action", action);
        get(EventResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((EventResponse) response);
            }
        });
    }


    public void deleteEvent(Long event_id, ExecListener listener) {
        url("event_update.php");
        add("event_id", event_id);
        add("event_visible", 0);
        get(listener);
    }


    public class InsertUpdateEventResponse {
        public Long event_id;
    }


    public interface InsertUpdateEventListener {
        void run(InsertUpdateEventResponse response);
    }

    public void insertUpdateEvent(Event event, List<Long> seleced, final InsertUpdateEventListener listener, ErrorListener error) {
        url("event_insert.php");
        add("event_id", event.event_id);
        add("event_time", event.event_time);
        add("event_lat", event.event_lat);
        add("event_lon", event.event_lon);
        add("event_address", event.event_address);
        add("event_title", event.event_title);
        add("event_filter_min_year", event.event_filter_min_year);
        add("event_filter_sex", event.event_filter_sex);
        add("event_filter_max_members", event.event_filter_max_members);
        add("event_image_link_id", event.event_image_link_id);
        add("event_visible", event.event_visible);
        add("invite_list", seleced);
        err(error);
        get(InsertUpdateEventResponse.class, new ApiRequest.Listener() {
            @Override
            public void run(Object response) {
                listener.run((InsertUpdateEventResponse) response);
            }
        });
    }

}
