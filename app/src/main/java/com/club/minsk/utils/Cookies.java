package com.club.minsk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.club.minsk.BuildConfig;
import com.club.minsk.db.Strings;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Cookies {

    private static SharedPreferences ownerStore;

    public static void init(Context applicationContext) {
        ownerStore = applicationContext.getSharedPreferences("owner", Context.MODE_PRIVATE);

        set("app_id", Strings.get("app_id"));
        set("device_id", AndroidUtils.getDeviceId(applicationContext));
        set("device_lang", AndroidUtils.getLang(applicationContext));
        if (BuildConfig.DEBUG) {
            set("device_lat", "" + 53.9);
            set("device_lon", "" + 27.56667);
            set("device_gps_time", "" + new Date().getTime() / 1000);
        }
    }

    public static String get(String name) {
        if (ownerStore != null) {
            if (!ownerStore.contains(name))
                return null;
            return ownerStore.getString(name, null);
        }else
            return null;
    }

    public static Long getInt(String name) {
        String number = get(name);
        if (number != null)
            return Long.valueOf(number);
        return null;
    }

    public static void set(String key, String value) {
        ownerStore.edit().putString(key, value).apply();
    }

    public static Map<String, String> getMap() {
        Map<String, String> result = new HashMap<>();
        Map<String, ?> allEntries = ownerStore.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet())
            result.put(entry.getKey(), entry.getValue().toString());
        return result;
    }

    public static void clear() {
        String device_token = get("device_token");
        ownerStore.edit().clear().apply();
        set("device_token", device_token);
    }

    public static void remove(String key) {
        ownerStore.edit().remove(key).apply();
    }

    public static Double getFloat(String key) {
        String number = get(key);
        if (number != null)
            return Double.valueOf(number);
        return null;
    }
}
