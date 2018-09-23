package com.club.minsk.db.utils;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.club.minsk.App;
import com.club.minsk.BuildConfig;
import com.club.minsk.db.Devices;
import com.club.minsk.db.Events;
import com.club.minsk.db.Links;
import com.club.minsk.db.Messages;
import com.club.minsk.db.Owners;
import com.club.minsk.db.Strings;
import com.club.minsk.db.tables.DevicesTable;
import com.club.minsk.db.tables.EventTypesTable;
import com.club.minsk.db.tables.MessagesTable;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.Cookies;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiRequest {

    public int CONNECTION_TIMEOUT = 3000;
    public static final int CONNECTION_RETRY_COUNT = 1;

    public static final int ERROR_EXEC_RESULT_EMPTY = -2;
    public static final int ERROR_CONNECTION = -1;
    public static final int ERROR_PARAMS_IS_NO_SET = 0;
    public static final int ERROR_TOKEN_IS_BAD = 4;

    public static String TAG = "ssds";
    public static Gson json = new Gson();

    protected Map<String, String> params = new HashMap<>();

    public String getHost() {
        String host = "";
        if (Cookies.get("api_host") != null)
            host = Cookies.get("api_host");
        else if (BuildConfig.DEBUG)
            host = Strings.get("debug_host");
        else
            host = Strings.get("release_host");
        return "http://" + host + "/api/";
    }

    public ApiRequest() {
        add(Cookies.getMap());
    }

    public interface ExecListener {
        void run(boolean success);
    }

    ExecListener execListener;

    class ApiResponse {
        public Boolean response;
        public Map<Long, Owners.Owner> owners;
        public Map<Long, Links.Link> links;
        public Map<Long, Events.Event> events;
        public Map<Long, EventTypesTable.EventType> event_types;
        public Map<String, DevicesTable.Device> devices;
        public Map<String, String> strings;
        public Map<Long, MessagesTable.Message> messages;
        public List<String> servers;
        public List<Long> notify_message_id_list;
    }

    public void get(ExecListener listener) {
        this.execListener = listener;
        get();
    }

    public String getUrlWithParams() {
        StringBuilder get = new StringBuilder();
        get.append(getHost()).append("?s=").append(params.get("script_name"));
        for (String key : params.keySet())
            if (!key.equals("script_name"))
                get.append("&").append(key).append("=").append(URLEncoder.encode(params.get(key)));
        return get.toString();
    }

    int currentRetryCount = 1;

    public void get() {

        Log.e(TAG, getUrlWithParams());

        JsonObjectRequest request = new JsonObjectRequest
                (com.android.volley.Request.Method.POST, getHost(), getParams(),
                        new com.android.volley.Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                String response_json = jsonObject.toString();
                                try {
                                    jsonObject.getInt("error_code");
                                    if (errorListener != null)
                                        errorListener.run(json.fromJson(response_json, Error.class));
                                    App.show(getUrlWithParams());
                                } catch (JSONException e) {

                                    ApiResponse apiResponse = json.fromJson(response_json, ApiResponse.class);
                                    Devices.put(apiResponse.devices);
                                    Owners.put(apiResponse.owners);
                                    Links.put(apiResponse.links);
                                    Events.put(apiResponse.events);
                                    Strings.put(apiResponse.strings);
                                    Messages.put(apiResponse.messages);

                                    if (execListener != null) {
                                        if (apiResponse.response != null)
                                            execListener.run(apiResponse.response);
                                        else if (errorListener != null)
                                            errorListener.run(new Error(ERROR_EXEC_RESULT_EMPTY));
                                    } else {
                                        if (listener != null)
                                            listener.run(json.fromJson(response_json, parseClass));
                                    }
                                }
                            }
                        }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (volleyError instanceof TimeoutError && connectionErrorListener != null) {
                            if (connectionErrorListener.run(new Error(ERROR_CONNECTION)) == 0)
                                get();
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        HttpURLConnection we = (HttpURLConnection) new URL(getUrlWithParams()).openConnection();
                                        String ss = AndroidUtils.streamToString(we.getInputStream());
                                        App.log(ss);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            App.log(volleyError.getClass().getSimpleName());
                        }
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(CONNECTION_TIMEOUT, CONNECTION_RETRY_COUNT, 1.0F));
        App.getInstance().addToRequestQueue(request);
    }

    public void setConnectionTimeout(int connectionTimeout) {
        CONNECTION_TIMEOUT = connectionTimeout;
    }


    public void add(String key, String val) {
        if (val != null && !val.equals(""))
            params.put(key, val);
    }

    public void add(String key, Long val) {
        if (val != null)
            add(key, new BigDecimal(val).toPlainString());
    }

    public void add(String key, Integer val) {
        if (val != null)
            add(key, new BigDecimal(val).toPlainString());
    }

    public void add(String key, Double val) {
        if (val != null)
            add(key, new BigDecimal(val).toPlainString());
    }

    public void add(String key, Boolean val) {
        if (val != null)
            add(key, val ? 1 : 0);
    }

    public void add(String key, int val) {
        add(key, new BigDecimal(val).toPlainString());
    }

    public void add(String key, double val) {
        add(key, new BigDecimal(val).toPlainString());
    }

    public void add(String key, float val) {
        add(key, new BigDecimal(val).toPlainString());
    }

    public void add(String key, long val) {
        add(key, new BigDecimal(val).toPlainString());
    }

    public void add(String key, boolean val) {
        add(key, val ? 1 : 0);
    }

    public void add(String key, List<Long> list) {
        if (list == null || list.isEmpty())
            return;
        String value = "";
        for (int i = 0; i < list.size(); i++)
            value += list.get(i) + (i != list.size() - 1 ? "," : "");
        add(key, value);
    }

    public void add(String key, String[] list) {
        String value = "";
        if (list != null)
            for (int i = 0; i < list.length; i++)
                value += list[i] + (i != list.length - 1 ? "," : "");
        add(key, value);
    }

    public void add(Map<String, String> params) {
        if (params != null)
            for (String key : params.keySet())
                add(key, params.get(key));
    }

    public void url(String url) {
        add("script_name", url);
    }

    public JSONObject getParams() {
        JSONObject jsonRequest = new JSONObject();
        for (String key : params.keySet())
            try {
                jsonRequest.put(key, params.get(key));
            } catch (JSONException e) {
            }
        return jsonRequest;
    }


    Class parseClass;


    public interface Listener {
        void run(Object response);
    }

    public Listener listener;


    public class Error implements Serializable {
        public Integer error_code;
        public String error_message;

        public Error(int error_code) {
            this.error_code = error_code;
        }
    }

    public interface ErrorListener {
        void run(Error error);
    }

    public interface ConnectionErrorListener {
        int run(Error error);
    }

    static ConnectionErrorListener connectionErrorListener;

    public static void setConnectionErrorListener(ConnectionErrorListener connectionErrorListener) {
        ApiRequest.connectionErrorListener = connectionErrorListener;
    }

    ErrorListener errorListener;

    public void err(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void get(Class parseClass, Listener listener) {
        this.parseClass = parseClass;
        get(listener);
    }

    public void get(Listener listener) {
        this.listener = listener;
        get();
    }
}
