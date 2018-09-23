package com.club.minsk.db.tables;

//MembersTable.java

import com.club.minsk.db.utils.ApiRequest;
import android.support.annotation.NonNull;

import java.util.List;

public class MembersTable extends ApiRequest {

    public static final long ATTACH_TYPE_FRIENDS = 0L;
    public static final long ATTACH_TYPE_INVITE = 1L;
    public static final long ATTACH_TYPE_LIKE_OWNER = 2L;
    public static final long ATTACH_TYPE_DIALOG = 4;

    public class Member{
        public Long member_id;
        public Long member_attach_type;
        public Long member_attach_id;
        public Long owner_id;
        public Long member_time;
        public Long member_visible;
    }

    public class Response{
        public Member member;
    }

    public interface Listener {
        void run(Response response);
    }

    protected void insert(@NonNull Long member_attach_type,
                          @NonNull Long member_attach_id,
                          @NonNull Long member_time,
                          Long member_visible,
                          final Listener listener) {
        url("member_insert.php");
        add("member_attach_type", member_attach_type);
        add("member_attach_id", member_attach_id);
        add("member_time", member_time);
        add("member_visible", member_visible);
        get(Member.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }


    protected void update(Long member_id,
                          Long member_attach_type,
                          Long member_attach_id,
                          Long member_time,
                          Long member_visible,
                          final ExecListener listener) {
        url("member_update.php");
        add("member_id", member_id);
        add("member_attach_type", member_attach_type);
        add("member_attach_id", member_attach_id);
        add("member_time", member_time);
        add("member_visible", member_visible);
        get(listener);
    }


    public void delete(Long member_id, boolean member_visible, ExecListener listener){
        url("member_update.php");
        add("member_id", member_id);
        add("member_visible", member_visible);
        get(listener);
    }

    public class ListResponse {
        public List<Member> response;
    }


    public interface ListListener {
        void run(ListResponse response);
    }


    // Overload this function
    protected void select(Long member_id,
                          Long member_attach_type,
                          Long member_attach_id,
                          Long member_time,
                          Long member_visible,
                          final ListListener listener) {
        url("members.php");
        add("member_id", member_id);
        add("member_attach_type", member_attach_type);
        add("member_attach_id", member_attach_id);
        add("member_time", member_time);
        add("member_visible", member_visible);
        get(ListResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }
}

