package com;

import java.util.List;
import java.util.Map;

public class AppRequest extends ApiRequest {

    public class Response{
        List<App> apps;
        public class App{
            public String app_package;
            public String app_name;
            public String google_maps_api_key;
            public Long app_logo_link_id;
            public Long vk_app_id;
            public Long fb_app_id;
            public String app_host;
        }

        public Map<Long, Link> links;
        public class Link{
            String link_url;
        }
    }

    interface Listener{
        void run(Response response);
    }

    public AppRequest(String app_id, final Listener listener, Error error) {
        host = "http://fans.by/api/";
        url("");
        add("s", "apps.php");
        add("find_app_id", app_id);
        err(error);
        get(Response.class, new ApiRequest.Listener() {
            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }
}
