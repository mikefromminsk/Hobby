package com.club.minsk.db;


import com.club.minsk.App;
import com.club.minsk.db.tables.DevicesTable;
import com.club.minsk.utils.AndroidUtils;

import java.util.HashMap;
import java.util.Map;

public class Devices extends DevicesTable {

    private static Map<String, Device> devices = new HashMap<>();

    public static Device get(String device_id){
        return devices.get(device_id);
    }

    public static void put(Map<String, Device> new_devices){
        if (new_devices != null)
            devices.putAll(new_devices);
    }

    public static Device self() {
        return devices.get(AndroidUtils.getDeviceId(App.getActiveActivity()));
    }

    public Recommended newRecommended() {
        return new Recommended();
    }

    static Devices instance = new Devices();
    public static Devices getInstance() {
        return instance;
    }

    public class Recommended{
        public String logo;
        public String title;
        public String link;
    }


    public void insert() {
        url("device_insert.php");
        get();
    }

    public void tokenUpdate(String token) {
        url("device_insert.php");
        add("device_token", token);
        get();
    }

}
