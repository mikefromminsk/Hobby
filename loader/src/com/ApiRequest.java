package com;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ApiRequest {

    public String host;
    private String url;
    private Map<String, String> request = new HashMap<>();
    static Random random = new Random();

    public ApiRequest() {
        host = "http://x29a100.pe.hu/";
        add("rid", random.nextInt());
        add("app_id", 0);
        add("version", 30);
        add("owner_id", 17210363);
    }

    public void url(String url) {
        this.url = url;
    }

    public void add(String key, String val) {
        if (val != null && !val.equals(""))
            request.put(key, val);
    }

    public void add(String key, Long val) {
        if (val != null)
            add(key, String.valueOf(val));
    }

    public void add(String key, Integer val) {
        if (val != null)
            add(key, String.valueOf(val));
    }

    public void add(String key, Double val) {
        if (val != null)
            add(key, String.valueOf(val));
    }

    public void add(String key, double val) {
        add(key, String.valueOf(val));
    }

    public void add(String key, float val) {
        add(key, String.valueOf(val));
    }


    public void add(String key, int val) {
        add(key, String.valueOf(val));
    }

    Class parseClass;

    public interface Listener {
        void run(Object response);
    }

    Listener listener;

    public void get(Class parseClass, Listener listener) {
        this.parseClass = parseClass;
        get(listener);
    }

    public void get(Listener listener) {
        this.listener = listener;
        get();
    }

    public interface Error {
        void run(int error_code);
    }

    Error error;
    public ApiRequest err(Error error) {
        this.error = error;
        return this;
    }

    public class ErrorResponse {
        String error;
        Integer error_code;
    }

    public String getGetUrl() {
        StringBuilder get = new StringBuilder();
        get.append(host).append(url).append("?");
        for (String key : request.keySet())
            get.append("&").append(key).append("=").append(request.get(key));
        return get.toString();
    }

    public void get() {

        System.out.println(getGetUrl());

        String responseStr = Http.post(host + url, request);
        if (responseStr != null) {
            try {
                ErrorResponse errorResponse = Http.json.fromJson(responseStr, ErrorResponse.class);
                if (errorResponse.error == null) {
                    if (parseClass != null) {
                        Object response = Http.json.fromJson(responseStr, parseClass);
                        if (listener != null)
                            listener.run(response);
                    }else{
                        if (listener != null)
                            listener.run(responseStr);
                    }
                }else{
                    if (error != null)
                        error.run(errorResponse.error_code);
                }
            } catch (Exception e) {
                if (error != null)
                    error.run(0);
            }
        } else {
            System.out.println("request error");
        }
    }

}
