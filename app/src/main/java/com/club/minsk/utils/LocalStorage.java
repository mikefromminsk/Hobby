package com.club.minsk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.club.minsk.App;

import java.util.Date;

public class LocalStorage {

    private static SharedPreferences ownerStrings;

    public static void init() {
        ownerStrings = App.getInstance().getSharedPreferences("local_storage", Context.MODE_PRIVATE);
    }

    public static Object get(String key, @NonNull Object defValue) {
        if (ownerStrings == null)
            init();
        if (defValue instanceof Boolean)
            return ownerStrings.getBoolean(key, false);
        if (defValue instanceof Integer)
            return ownerStrings.getInt(key, 0);
        if (defValue instanceof Long)
            return ownerStrings.getLong(key, 0);
        if (defValue instanceof String)
            return ownerStrings.getString(key, "");
        if (defValue instanceof Float)
            return ownerStrings.getFloat(key, 0);
        if (defValue instanceof Double)
            return ownerStrings.getFloat(key, 0);
        return defValue;
    }

    public static void set(String key, @NonNull Object value) {
        if (ownerStrings == null)
            init();
        if (value instanceof Boolean)
            ownerStrings.edit().putBoolean(key, (Boolean) value).apply();
        if (value instanceof Integer)
            ownerStrings.edit().putInt(key, (Integer) value).apply();
        if (value instanceof Long)
            ownerStrings.edit().putLong(key, (Long) value).apply();
        if (value instanceof String)
            ownerStrings.edit().putString(key, (String) value).apply();
        if (value instanceof Float)
            ownerStrings.edit().putFloat(key, (Float) value).apply();
        if (value instanceof Double)
            ownerStrings.edit().putFloat(key, (Float) value).apply();
    }

    public static void clear() {
        if (ownerStrings == null)
            init();
        String token = getToken();
        ownerStrings.edit().clear().apply();
        setToken(token);
    }


    public static void setAppReposted() {
        set("app_reposted", true);
    }

    public static boolean getAppReposted() {
        return (boolean) get("app_reposted", false);
    }

    public static void setAppColor(int newColor) {
        set("app_color", newColor);
        App.app_color = newColor;
    }

    public static void setLastFriendsUpdateRequest() {
        set("last_friends_update_request_time", new Date());
    }

    public static long getLastFriendsUpdateRequest() {
        return (long) get("last_friends_update_request_time", 0L);
    }

    public static void setFirstRun() {
        set("first_run_bool", false);
    }

    public static Boolean getFirstRun() {
        return (Boolean) get("first_run_bool", true);
    }


    public static String getToken() {
        return (String) get("token", "");
    }

    public static void setToken(String new_token) {
        set("token", new_token);
    }



/*

    private static String position_prefix = "position_";
    private static String position_lat_postfix = "_lat";
    private static String position_lon_postfix = "_lon";
    private static String position_name_postfix = "_name";
        public static List<UserPosition> getUserPositions() {
            List<UserPosition> userPositions = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                double device_lat = ownerStrings.getFloat(appStrings.position_prefix + i + position_lat_postfix, 0);
                double device_lon = ownerStrings.getFloat(appStrings.position_prefix + i + position_lon_postfix, 0);
                String name = ownerStrings.get(appStrings.position_prefix + i + position_name_postfix, null);
                if (device_lat != 0 && device_lon != 0 && name != null)
                    userPositions.add(new UserPosition(device_lat, device_lon, name));
            }
            return userPositions;
        }


        public static void addUserPosition(UserPosition userPosition) {
            List<UserPosition> userPositions = getUserPositions();
            int next_id = userPositions.size();
            ownerStrings.edit()
                    .putFloat(appStrings.position_prefix + next_id + position_lat_postfix, (float) userPosition.device_lat)
                    .putFloat(appStrings.position_prefix + next_id + position_lon_postfix, (float) userPosition.device_lon)
                    .putString(appStrings.position_prefix + next_id + position_name_postfix, userPosition.address)
                    .apply();
        }

    public static void setActivePosition(int active_position) {
        set("active_position", active_position);
        loadLatLon();
    }

    public static int getActivePosition() {
        return (int) get("active_position", -1);
    }

*/


}
