package com;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class Http {

    static Gson json = (new GsonBuilder()).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    static String get(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10 * 1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
            conn.setRequestProperty("charset", "utf-8");
            conn.connect();
            return convertStreamToString(conn.getInputStream());
        } catch (Exception var10) {
            var10.printStackTrace();
        }
        return null;
    }

    static String post(String urlString, Map<String, String> request) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setConnectTimeout(10 * 1000);
            huc.setRequestMethod("POST");
            huc.setDoOutput(true);
            huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
            huc.setRequestProperty("charset", "utf-8");
            huc.connect();
            String jsonRequest = Http.json.toJson(request);
            huc.getOutputStream().write(jsonRequest.getBytes());
            return convertStreamToString(huc.getInputStream());
        } catch (Exception var10) {
            var10.printStackTrace();
        }
        return null;
    }

    public static Object getJson(String urlString, Class parseObject) {
        String jsonResponse = get(urlString);
        if (jsonResponse != null && parseObject != null)
            return json.fromJson(jsonResponse, parseObject);
        return null;
    }

    static Object postJson(String urlString, Map<String, String> request, Class parseObject) {
        String jsonResponse = post(urlString, request);
        if (jsonResponse != null && parseObject != null)
            return json.fromJson(jsonResponse, parseObject);
        return null;
    }
}
