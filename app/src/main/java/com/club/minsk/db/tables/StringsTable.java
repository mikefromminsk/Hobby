package com.club.minsk.db.tables;

//StringsTable.java

import com.club.minsk.db.utils.ApiRequest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

public class StringsTable extends ApiRequest {

    public static final String OWNER_LANG_DEFAULT = "RU";
    public static final long OWNER_SEX_DEFAULT = 2L;
    public static final String OWNER_SEX_WOMAN = "W";
    public static final String OWNER_SEX_MAN = "M";
    public static final String STRING_SETTING_NOTIFY_TYPE_NAME_PREFIX = "setting_notify_type_";
    public static final String STRING_SETTING_NOTIFY_TYPE_DEFAULT_VISIBLE_PREFIX = "setting_notify_default_";
    public static final String STRING_EVENT_TYPE_PREFIX = "event_type_";
    public static final String STRING_YEAR_PREFIX = "year_";
    public static final String STRING_DAY_PREFIX = "day_";
    public static final String STRING_HOUR_PREFIX = "hour_";
    public static final String STRING_MIN_PREFIX = "min_";
    public static final String STRING_SEC_PREFIX = "sec_";
    public static final String STRING_OWNER_PRIVACY = "owner_privacy";
    public static final String STRING_MENU_PREFIX = "menu_";
    public static final String STRING_SETTING_PREFIX = "setting_";
    public static final String STRING_SETTING_POSITION = "setting_";
    public static final String STRING_SETTING_PRIVACY = "setting_";
    public static final String STRING_SETTING_NOTIFY = "setting_";
    public static final String STRING_SETTING_LANG = "setting_";
    public static final String STRING_SETTING_FEEDBACK = "setting_feedback";
    public static final String STRING_SETTING_ABOUT = "setting_";
    public static final String STRING_SETTING_EXIT = "setting_";
    public static final String STRING_APP_MENU = "app_menu";
    public static final String STRING_ADMIN_OWNER_ID = "admin_owner_id";
    public static final String STRING_LANG_PREFIX = "slang_";
    public static final String STRING_FEEDBACK_STATUS_PREFIX = "feedback_status_";

    public class DbString{
        public Long string_id;
        public String string_name;
        public String string_text;
        public Long owner_id;
        public Long app_id;
        public String platform_name;
        public String owner_lang;
        public String owner_sex;
    }

    public class Response{
        public DbString dbString;
    }

    public interface Listener {
        void run(Response response);
    }

    protected void insert(@NonNull String string_name,
                          @NonNull String string_text,
                          @Nullable String owner_lang,
                          @Nullable String owner_sex,
                          final Listener listener) {
        url("string_insert.php");
        add("string_name", string_name);
        add("string_text", string_text);
        add("owner_lang", owner_lang);
        add("owner_sex", owner_sex);
        get(DbString.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }


    protected void update(Long string_id,
                          String string_name,
                          String string_text,
                          String owner_lang,
                          String owner_sex,
                          final ExecListener listener) {
        url("string_update.php");
        add("string_id", string_id);
        add("string_name", string_name);
        add("string_text", string_text);
        add("owner_lang", owner_lang);
        add("owner_sex", owner_sex);
        get(listener);
    }

    public class ListResponse {
        public List<DbString> response;
    }


    public interface ListListener {
        void run(ListResponse response);
    }


    // Overload this function
    protected void select(Long string_id,
                          String string_name,
                          String string_text,
                          String owner_lang,
                          String owner_sex,
                          final ListListener listener) {
        url("strings.php");
        add("string_id", string_id);
        add("string_name", string_name);
        add("string_text", string_text);
        add("owner_lang", owner_lang);
        add("owner_sex", owner_sex);
        get(ListResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }
}

