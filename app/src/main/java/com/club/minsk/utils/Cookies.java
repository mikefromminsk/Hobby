package com.club.minsk.utils;

import android.content.Context;

import com.club.minsk.BuildConfig;
import com.club.minsk.db.Strings;

import java.util.Date;
import java.util.Map;

public class Cookies {

    private static DBHelper db;

    public static void init(Context applicationContext, DBHelper dbHelper) {
        if (db != null)
            db.close();
        Cookies.db = dbHelper;
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
        return db.get(name);
    }

    public static Long getInt(String name) {
        String number = get(name);
        if (number != null)
            return Long.valueOf(number);
        return null;
    }

    public static void set(String key, String value) {
        db.put(key, value);
    }

    public static Map<String, String> getMap() {
        return db.all();
    }

    public static void clear() {
        String device_token = get("device_token");
        db.clear();
        set("device_token", device_token);
    }

    public static void remove(String key) {
        db.delete(key);
    }

    public static Double getFloat(String key) {
        String number = get(key);
        if (number != null)
            return Double.valueOf(number);
        return null;
    }
}
