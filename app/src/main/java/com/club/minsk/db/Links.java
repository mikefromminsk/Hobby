package com.club.minsk.db;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import com.club.minsk.db.tables.LinksTable;
import com.club.minsk.db.utils.ApiRequest;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Links extends LinksTable {


    public class Link extends LinksTable.Link {
        public String link_url;
    }

    private static Map<Long, Link> links = new HashMap<>();

    public static String get(Long link_id){
        Links.Link link = links.get(link_id);
        if (link != null)
            return link.link_url;
        return Strings.get("image_placeholder");
    }

    public static LinksTable.Link getLink(Long event_image_link_id) {
        return links.get(event_image_link_id);
    }

    public static void put(Map<Long, Links.Link> new_links){
        if (new_links != null)
            links.putAll(new_links);
    }

    public class Response {
        public Long image_link_id;
    }

    public interface Listener {
        void run(Response response);
    }

    String encodedString;
    Bitmap bitmap;
    Listener listener;


    public void upload_image(Bitmap bitmap,
                             final Listener listener,
                             ErrorListener error){
        this.bitmap = bitmap;
        this.listener = listener;
        err(error);
        encodeImagetoString();
    }

    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                byte[] byte_arr = stream.toByteArray();
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                upload();
            }
        }.execute(null, null, null);
    }

    void upload(){
        url("upload_image_base64.php");
        add("image", encodedString);
        setConnectionTimeout(30000);
        get(Response.class, new ApiRequest.Listener() {
            @Override
            public void run(Object response) {
                listener.run((Response)response);
            }
        });
    }
}
