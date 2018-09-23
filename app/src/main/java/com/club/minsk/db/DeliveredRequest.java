package com.club.minsk.db;


import android.os.AsyncTask;

import com.club.minsk.utils.AndroidUtils;

import java.net.HttpURLConnection;
import java.net.URL;

public class DeliveredRequest extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            String response = AndroidUtils.convertStreamToString(urlConnection.getInputStream());
        } catch (Exception ignore) {
        }
        return null;
    }
}