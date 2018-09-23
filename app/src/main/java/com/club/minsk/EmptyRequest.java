package com.club.minsk;

import com.club.minsk.db.utils.ApiRequest;

import java.util.List;

public class EmptyRequest extends ApiRequest {

    public class Response{
        public List<String> list;
    }

    public interface Listener{
        void run(Response response);
    }

    public void select(final Listener listener) {
        url("examplte.php");
        get(Response.class, new ApiRequest.Listener() {
            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }
}
