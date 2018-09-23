package com.club.minsk.db.tables;

//DevicesTable.java

import com.club.minsk.db.utils.ApiRequest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

public class DevicesTable extends ApiRequest {


    public class Device{
        public String device_id;
        public Double device_lat;
        public Double device_lon;
        public String platform_name;
        public Long platform_version;
        public Long app_id;
        public Long app_version;
        public Long owner_id;
        public String device_model;
        public String device_token;
        public String device_ip;
        public String device_city;
        public Long device_gps_time;
        public Long device_update_time;
    }

    public class Response{
        public Device device;
    }

    public interface Listener {
        void run(Response response);
    }

    protected void insert(@NonNull Double device_lat,
                          @NonNull Double device_lon,
                          @Nullable String device_model,
                          @Nullable String device_token,
                          @Nullable String device_ip,
                          @Nullable String device_city,
                          @Nullable Long device_gps_time,
                          final Listener listener) {
        url("device_insert.php");
        add("device_lat", device_lat);
        add("device_lon", device_lon);
        add("device_model", device_model);
        add("device_token", device_token);
        add("device_ip", device_ip);
        add("device_city", device_city);
        add("device_gps_time", device_gps_time);
        get(Device.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }


    protected void update(Double device_lat,
                          Double device_lon,
                          String device_model,
                          String device_token,
                          String device_ip,
                          String device_city,
                          Long device_gps_time,
                          Long device_update_time,
                          final ExecListener listener) {
        url("device_update.php");
        add("device_lat", device_lat);
        add("device_lon", device_lon);
        add("device_model", device_model);
        add("device_token", device_token);
        add("device_ip", device_ip);
        add("device_city", device_city);
        add("device_gps_time", device_gps_time);
        add("device_update_time", device_update_time);
        get(listener);
    }

    public class ListResponse {
        public List<Device> response;
    }


    public interface ListListener {
        void run(ListResponse response);
    }


    // Overload this function
    protected void select(Double device_lat,
                          Double device_lon,
                          String device_model,
                          String device_token,
                          String device_ip,
                          String device_city,
                          Long device_gps_time,
                          Long device_update_time,
                          final ListListener listener) {
        url("devices.php");
        add("device_lat", device_lat);
        add("device_lon", device_lon);
        add("device_model", device_model);
        add("device_token", device_token);
        add("device_ip", device_ip);
        add("device_city", device_city);
        add("device_gps_time", device_gps_time);
        add("device_update_time", device_update_time);
        get(ListResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }
}

