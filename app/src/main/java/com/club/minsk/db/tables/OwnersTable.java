package com.club.minsk.db.tables;

//OwnersTable.java

import com.club.minsk.db.utils.ApiRequest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

public class OwnersTable extends ApiRequest {

    public static final long OWNER_SEX_DEFAULT = 2L;
    public static final String OWNER_SEX_WOMAN = "W";
    public static final String OWNER_SEX_MAN = "M";
    public static final String OWNER_LANG_DEFAULT = "RU";
    public static final String OWNER_RELATION_PREFIX = "relation_";

    public class Owner{
        public Long app_id;
        public Long owner_id;
        public Long owner_token;
        public String owner_name;
        public String owner_first_name;
        public String owner_last_name;
        public String owner_sex;
        public Long owner_avatar_link_id;
        public Long owner_photo_link_id;
        public Long vk_owner_id;
        public Long fb_owner_id;
        public String owner_email;
        public String owner_password;
        public Long owner_birthdate;
        public String owner_lang;
        public Long owner_unban_time;
        public Long owner_login_time;
        public Long owner_show_count;
        public String owner_phone;
        public String owner_city;
    }

    public class Response{
        public Owner owner;
    }

    public interface Listener {
        void run(Response response);
    }

    protected void insert(@NonNull Long owner_token,
                          @NonNull String owner_name,
                          @NonNull String owner_first_name,
                          @NonNull String owner_last_name,
                          @NonNull String owner_sex,
                          @NonNull Long owner_avatar_link_id,
                          @NonNull Long owner_photo_link_id,
                          @Nullable Long vk_owner_id,
                          @Nullable Long fb_owner_id,
                          @Nullable String owner_email,
                          @Nullable String owner_password,
                          @Nullable Long owner_birthdate,
                          @Nullable String owner_lang,
                          @Nullable Long owner_unban_time,
                          Long owner_show_count,
                          final Listener listener) {
        url("owner_insert.php");
        add("owner_token", owner_token);
        add("owner_name", owner_name);
        add("owner_first_name", owner_first_name);
        add("owner_last_name", owner_last_name);
        add("owner_sex", owner_sex);
        add("owner_avatar_link_id", owner_avatar_link_id);
        add("owner_photo_link_id", owner_photo_link_id);
        add("vk_owner_id", vk_owner_id);
        add("fb_owner_id", fb_owner_id);
        add("owner_email", owner_email);
        add("owner_password", owner_password);
        add("owner_birthdate", owner_birthdate);
        add("owner_lang", owner_lang);
        add("owner_unban_time", owner_unban_time);
        add("owner_show_count", owner_show_count);
        get(Owner.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }


    protected void update(Long owner_token,
                          String owner_name,
                          String owner_first_name,
                          String owner_last_name,
                          String owner_sex,
                          Long owner_avatar_link_id,
                          Long owner_photo_link_id,
                          Long vk_owner_id,
                          Long fb_owner_id,
                          String owner_email,
                          String owner_password,
                          Long owner_birthdate,
                          String owner_lang,
                          Long owner_unban_time,
                          Long owner_login_time,
                          Long owner_show_count,
                          final ExecListener listener) {
        url("owner_update.php");
        add("owner_token", owner_token);
        add("owner_name", owner_name);
        add("owner_first_name", owner_first_name);
        add("owner_last_name", owner_last_name);
        add("owner_sex", owner_sex);
        add("owner_avatar_link_id", owner_avatar_link_id);
        add("owner_photo_link_id", owner_photo_link_id);
        add("vk_owner_id", vk_owner_id);
        add("fb_owner_id", fb_owner_id);
        add("owner_email", owner_email);
        add("owner_password", owner_password);
        add("owner_birthdate", owner_birthdate);
        add("owner_lang", owner_lang);
        add("owner_unban_time", owner_unban_time);
        add("owner_login_time", owner_login_time);
        add("owner_show_count", owner_show_count);
        get(listener);
    }

    public class ListResponse {
        public List<Owner> response;
    }


    public interface ListListener {
        void run(ListResponse response);
    }


    // Overload this function
    protected void select(Long owner_token,
                          String owner_name,
                          String owner_first_name,
                          String owner_last_name,
                          String owner_sex,
                          Long owner_avatar_link_id,
                          Long owner_photo_link_id,
                          Long vk_owner_id,
                          Long fb_owner_id,
                          String owner_email,
                          String owner_password,
                          Long owner_birthdate,
                          String owner_lang,
                          Long owner_unban_time,
                          Long owner_login_time,
                          Long owner_show_count,
                          final ListListener listener) {
        url("owners.php");
        add("owner_token", owner_token);
        add("owner_name", owner_name);
        add("owner_first_name", owner_first_name);
        add("owner_last_name", owner_last_name);
        add("owner_sex", owner_sex);
        add("owner_avatar_link_id", owner_avatar_link_id);
        add("owner_photo_link_id", owner_photo_link_id);
        add("vk_owner_id", vk_owner_id);
        add("fb_owner_id", fb_owner_id);
        add("owner_email", owner_email);
        add("owner_password", owner_password);
        add("owner_birthdate", owner_birthdate);
        add("owner_lang", owner_lang);
        add("owner_unban_time", owner_unban_time);
        add("owner_login_time", owner_login_time);
        add("owner_show_count", owner_show_count);
        get(ListResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }
}

