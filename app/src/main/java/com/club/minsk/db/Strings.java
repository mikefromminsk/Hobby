package com.club.minsk.db;

import com.club.minsk.App;
import com.club.minsk.db.tables.StringsTable;
import com.club.minsk.db.utils.ApiRequest;

import java.util.HashMap;
import java.util.Map;

public class Strings extends StringsTable {

    public final static String unset = "unset";
    public static Map<String, String> strings = new HashMap<>();

    public static String get(String resourceName) {
        try {
            if (strings.get(resourceName) != null)
                return strings.get(resourceName);
            else {
                int stringId = App.getInstance().getResources().getIdentifier(resourceName, "string",
                        App.getInstance().getPackageName());
                return App.getInstance().getString(stringId);
            }
        } catch (Exception e) {
            return unset + " " + resourceName;
        }
    }

    public static String get(int resourceId) {
        return get(App.getInstance().getResources().getResourceEntryName(resourceId));
    }

    public static Long getInt(String string_name) {
        try {
            return Long.valueOf(get(string_name));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Map<String, String> getMapWithPrefix(String prefix) {
        Map<String, String> result = new HashMap<>();
        for (String string_name : strings.keySet())
            if (string_name.indexOf(prefix) == 0)
                result.put(string_name, strings.get(string_name));
        return result;
    }

    public static void put(Map<String, String> new_strings) {
        if (new_strings != null && strings != null)
            strings.putAll(new_strings);
    }


    public void insertString(String string_name, String string_value, ExecListener listener) {
        url("string_insert.php");
        add("string_name", string_name);
        add("string_text", string_value);
        get(listener);
    }

    public class LangResponse {
        public String owner_lang;
        public Map<String, String> strings;
    }

    public interface LangListener {
        void run(LangResponse response);
    }

    public void setLang(String lang, final LangListener listener) {
        url("owner_lang_update.php");
        add("owner_lang", lang);
        get(LangResponse.class, new ApiRequest.Listener() {
            @Override
            public void run(Object response) {
                listener.run((LangResponse) response);
            }
        });
    }
}
