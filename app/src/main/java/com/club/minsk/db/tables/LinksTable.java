package com.club.minsk.db.tables;

//LinksTable.java

import com.club.minsk.db.utils.ApiRequest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

public class LinksTable extends ApiRequest {

    public static final long LINK_TYPE_IMAGE = 0L;

    public class Link{
        public Long link_id;
        public Long link_type;
        public String link_local_path;
        public String link_external_url;
        public Long link_image_width;
        public Long link_image_height;
    }

    public class Response{
        public Link link;
    }

    public interface Listener {
        void run(Response response);
    }

    protected void insert(@NonNull Long link_type,
                          @NonNull String link_local_path,
                          @Nullable String link_external_url,
                          @Nullable Long link_image_width,
                          @Nullable Long link_image_height,
                          final Listener listener) {
        url("link_insert.php");
        add("link_type", link_type);
        add("link_local_path", link_local_path);
        add("link_external_url", link_external_url);
        add("link_image_width", link_image_width);
        add("link_image_height", link_image_height);
        get(Link.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }


    protected void update(Long link_id,
                          Long link_type,
                          String link_local_path,
                          String link_external_url,
                          Long link_image_width,
                          Long link_image_height,
                          final ExecListener listener) {
        url("link_update.php");
        add("link_id", link_id);
        add("link_type", link_type);
        add("link_local_path", link_local_path);
        add("link_external_url", link_external_url);
        add("link_image_width", link_image_width);
        add("link_image_height", link_image_height);
        get(listener);
    }

    public class ListResponse {
        public List<Link> response;
    }


    public interface ListListener {
        void run(ListResponse response);
    }


    // Overload this function
    protected void select(Long link_id,
                          Long link_type,
                          String link_local_path,
                          String link_external_url,
                          Long link_image_width,
                          Long link_image_height,
                          final ListListener listener) {
        url("links.php");
        add("link_id", link_id);
        add("link_type", link_type);
        add("link_local_path", link_local_path);
        add("link_external_url", link_external_url);
        add("link_image_width", link_image_width);
        add("link_image_height", link_image_height);
        get(ListResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }
}

