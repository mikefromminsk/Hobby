package com.club.minsk.db.tables;

//DialogsTable.java

import com.club.minsk.db.utils.ApiRequest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

public class DialogsTable extends ApiRequest {


    public class Dialog{
        public Long dialog_id;
        public Long first_owner_id;
        public Long second_owner_id;
        public Long dialog_ban_initiator;
    }

    public class Response{
        public Dialog dialog;
    }

    public interface Listener {
        void run(Response response);
    }

    protected void insert(@NonNull Long first_owner_id,
                          @NonNull Long second_owner_id,
                          @Nullable Long dialog_read_time,
                          @Nullable Long dialog_ban_initiator,
                          final Listener listener) {
        url("dialog_insert.php");
        add("first_owner_id", first_owner_id);
        add("second_owner_id", second_owner_id);
        add("dialog_read_time", dialog_read_time);
        add("dialog_ban_initiator", dialog_ban_initiator);
        get(Dialog.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((Response) response);
            }
        });
    }


    protected void update(Long dialog_id,
                          Long first_owner_id,
                          Long second_owner_id,
                          Long dialog_read_time,
                          Long dialog_ban_initiator,
                          final ExecListener listener) {
        url("dialog_update.php");
        add("dialog_id", dialog_id);
        add("first_owner_id", first_owner_id);
        add("second_owner_id", second_owner_id);
        add("dialog_read_time", dialog_read_time);
        add("dialog_ban_initiator", dialog_ban_initiator);
        get(listener);
    }

    public class ListResponse {
        public List<Dialog> response;
    }


    public interface ListListener {
        void run(ListResponse response);
    }


    // Overload this function
    protected void select(Long dialog_id,
                          Long first_owner_id,
                          Long second_owner_id,
                          Long dialog_read_time,
                          Long dialog_ban_initiator,
                          final ListListener listener) {
        url("dialogs.php");
        add("dialog_id", dialog_id);
        add("first_owner_id", first_owner_id);
        add("second_owner_id", second_owner_id);
        add("dialog_read_time", dialog_read_time);
        add("dialog_ban_initiator", dialog_ban_initiator);
        get(ListResponse.class, new ApiRequest.Listener() {

            @Override
            public void run(Object response) {
                listener.run((ListResponse) response);
            }
        });
    }
}

