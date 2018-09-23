package com.club.minsk.db;

import com.club.minsk.db.utils.ApiRequest;
import com.club.minsk.db.tables.OwnersTable;
import com.club.minsk.utils.Cookies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Owners extends OwnersTable {


    public class Owner extends OwnersTable.Owner{
        public Long is_friend;
        public Long is_friend_request;
        public Long is_liked;
        public Long is_like_request;
    }


    public class LoginData {
        public Long owner_id;
    }

    public static Map<Long, Owners.Owner> owners = new HashMap<>();

    public static Owners.Owner get(Long owner_id) {
        return owners.get(owner_id);
    }


    public static void put(Map<Long, Owners.Owner> new_owners) {
        if (new_owners != null)
            owners.putAll(new_owners);
    }

    public static Owners.Owner self() {
        return owners.get(Cookies.getInt("owner_id"));
    }

    public static void clear() {
        owners = new HashMap<>();
    }


    public interface Listener {
        void run(LoginData loginData);
    }

    public void reg(String owner_first_name,
                    String owner_last_name,
                    String owner_sex,
                    String owner_avatar,
                    String owner_photo,
                    String vk_owner_id,
                    String fb_owner_id,
                    Long owner_birthdate,
                    String owner_email,
                    String owner_phone,
                    String social_name,
                    String[] social_members,
                    final Owners.Listener listener,
                    ErrorListener error) {
        url("owner_login.php");
        add("owner_first_name", owner_first_name);
        add("owner_last_name", owner_last_name);
        add("owner_sex", owner_sex);
        add("owner_avatar", owner_avatar);
        add("owner_photo", owner_photo);
        add("vk_owner_id", vk_owner_id);
        add("fb_owner_id", fb_owner_id);
        add("owner_email", owner_email);
        add("owner_phone", owner_phone);
        add("owner_birthdate", owner_birthdate);
        add("social_name", social_name);
        add("social_members", social_members);
        err(error);
        get(LoginData.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((LoginData) response);
            }
        });
    }


    public void login(final Owners.Listener listener,
                      ErrorListener error) {
        url("owner_login.php");
        err(error);
        get(LoginData.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((LoginData) response);
            }
        });
    }

    public class ProfileResponse {
        public Long owner_id;
    }

    public interface ProfileListener {
        void run(ProfileResponse loginData);
    }

    public void getProfile(Long to_owner_id, final ProfileListener listener) {
        url("owner.php");
        add("to_owner_id", to_owner_id);
        get(ProfileResponse.class, new ApiRequest.Listener() {
            @Override
            public void run(Object response) {
                listener.run((ProfileResponse) response);
            }
        });
    }

    public class SearchResponse {
        public List<Long> owner_id_list;
    }


    public interface SearchListener {
        void run(SearchResponse response);
    }

    public void search(String owner_name, final SearchListener listener) {
        url("owner_search.php");
        add("owner_name", owner_name);
        get(SearchResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((SearchResponse) response);
            }
        });
    }


    public void set_lang(String owner_lang, ExecListener listener) {
        url("owner_lang_update.php");
        add("owner_lang", owner_lang);
        get(listener);
    }


    public void forgotPassword(String owner_email) {
        url("password_forgot.php");
        add("owner_email", owner_email);
        get();
    }



    public void changeAvatar(Long owner_photo_link_id,
                             final Owners.Listener listener,
                             ErrorListener error) {
        url("owner_login.php");
        add("owner_photo_link_id", owner_photo_link_id);
        err(error);
        get(LoginData.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((LoginData) response);
            }
        });
    }


}
