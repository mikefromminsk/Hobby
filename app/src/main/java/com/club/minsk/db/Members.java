package com.club.minsk.db;

import android.support.annotation.NonNull;

import com.club.minsk.App;
import com.club.minsk.db.tables.MembersTable;
import com.club.minsk.db.utils.ApiRequest;

import java.util.List;

public class Members extends MembersTable {


    public class ListResponse {
        public List<Long> owner_id_list;
        public List<Long> likes;
        public List<Long> beside;
        public List<Long> friends;
        public List<Long> search;
        public List<Long> friends_of_friends;
    }

    public interface ListListener {
        void run(ListResponse response);
    }

    public class MemberInsertResponse {
        public Long member_id;
    }

    public interface MemberInsertListener {
        void run(MemberInsertResponse response);
    }

    public void insert(@NonNull Long member_attach_type,
                          @NonNull Long member_attach_id,
                          final MemberInsertListener listener) {
        url("member_insert.php");
        add("attach_type", member_attach_type);
        add("attach_id", member_attach_id);
        get(MemberInsertResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((MemberInsertResponse) response);
            }
        });
    }

    public void subscribe(Long event_id, MemberInsertListener listener) {
        insert(ATTACH_TYPE_INVITE, event_id, listener);
    }


    public void addToFriends(Long to_owner_id, MemberInsertListener listener) {
        insert(MembersTable.ATTACH_TYPE_FRIENDS, to_owner_id, listener);
    }


    public void insertFriends(String vkontakte, String[] items, final ListListener listener) {
        url("friends_insert.php");
        add("social_network", vkontakte);
        add("social_id_list", items);
        get(ListResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }

    public void memberList(String sex, Long min_years, String search_name, final ListListener listener) {
        url("members.php");
        add("sex", sex);
        add("min_years", min_years);
        add("name", search_name);
        add("likes", 1);
        add("beside", 1);
        add("friends", 1);
        get(ListResponse.class, new ApiRequest.Listener() {
            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }

    public void membersBeside(String sex, Long min_years, Long page_offset, Long page_size,
                        final ListListener listener) {
        url("members.php");
        add("sex", sex);
        add("min_years", min_years);
        add("beside", 1);
        add("page_offset", page_offset);
        add("page_size", page_size);
        get(ListResponse.class, new ApiRequest.Listener() {
            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }

    public String deliveredRequestLink(Long event_id){
        url("member_insert.php");
        add("attach_type", ATTACH_TYPE_INVITE);
        add("attach_id", event_id);
        add("member_visible", -1);
        App.log(getUrlWithParams());
        return getUrlWithParams();
    }

    public String rejectRequestLink(Long event_id){
        url("member_insert.php");
        add("attach_type", ATTACH_TYPE_INVITE);
        add("attach_id", event_id);
        add("member_visible", 0);
        App.log(getUrlWithParams());
        return getUrlWithParams();
    }

}
