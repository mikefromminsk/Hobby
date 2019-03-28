package com.club.minsk.db.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageUpload extends ApiRequest {

    public class Response {
        public Long link_id;
    }

    public interface Listener {
        void run(Response response);
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public ImageUpload(Bitmap image, final Listener listener, ErrorListener error) {
        url("upload_image.php");
        add("image_base64", BitMapToString(image));
        err(error);
        get(Response.class, new ApiRequest.Listener() {
            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }
}
