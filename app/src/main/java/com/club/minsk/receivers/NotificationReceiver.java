package com.club.minsk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.club.minsk.App;
import com.club.minsk.db.DeliveredRequest;
import com.club.minsk.db.Members;
import com.club.minsk.db.tables.PushTable;
import com.club.minsk.utils.AndroidUtils;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle data = intent.getExtras();
        String notifyType = data.getString(PushTable.PUSH_TYPE, "");

        if (notifyType.equals("" + PushTable.PUSH_TYPE_INVITE_CREATE) ||
                notifyType.equals("" + PushTable.PUSH_TYPE_INVITE_FRIEND_CREATE)) {
            new DeliveredRequest().execute(new Members().rejectRequestLink(AndroidUtils.toLong(data.getString("event_id"))));
        }

    }
}