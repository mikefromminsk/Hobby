package com.club.minsk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.club.minsk.db.DeliveredRequest;
import com.club.minsk.db.Members;
import com.club.minsk.db.Strings;
import com.club.minsk.db.tables.PushTable;
import com.club.minsk.messages.MessagesFragment;
import com.club.minsk.receivers.NotificationReceiver;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.Cookies;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();


        Long push_type = AndroidUtils.toLong(data.get(PushTable.PUSH_TYPE));
        Fragment activeFragment = App.getActiveFragment();
        if (push_type.equals(PushTable.PUSH_TYPE_MESSAGE_INSERT)) {
            if (activeFragment instanceof MessagesFragment) {
                ((MessagesFragment) activeFragment).insertMessage(data);
                return;
            }
            data.put(PushTable.PUSH_TEXT, AndroidUtils.base64Decode(data.get(PushTable.PUSH_TEXT)));
        } else if (push_type.equals(PushTable.PUSH_TYPE_MESSAGE_READ)) {
            if (activeFragment instanceof MessagesFragment)
                ((MessagesFragment) activeFragment).readMessage(data);
            return;
        }
        notification(data);
    }

    Intent createIntent(String notificationTag, Map<String, String> data){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        for (String key : data.keySet())
            intent.putExtra(key, data.get(key));
        intent.putExtra("notification_tag", notificationTag);
        return intent;
    }

    public void notification(Map<String, String> data) {


        String notifyType = data.get(PushTable.PUSH_TYPE);

        String notificationTag = "other" + (int) (Math.random() * Integer.MAX_VALUE);
        Integer notifyIcon = R.drawable.notify_invite;
        String notifyText = data.get(PushTable.PUSH_TEXT);
        int priority = Notification.PRIORITY_DEFAULT;

        if ("0".equals(Cookies.get(PushTable.PUSH_TYPE_SETTING_VALUE_PREFIX + notifyType))){
            if (notifyType.equals("" + PushTable.PUSH_TYPE_INVITE_CREATE) ||
                    notifyType.equals("" + PushTable.PUSH_TYPE_INVITE_FRIEND_CREATE))
                new DeliveredRequest().execute(new Members().rejectRequestLink(AndroidUtils.toLong(data.get("event_id"))));
            return;
        }
        if (notifyType.equals("" + PushTable.PUSH_TYPE_INVITE_CREATE) ||
                notifyType.equals("" + PushTable.PUSH_TYPE_INVITE_FRIEND_CREATE))
            new DeliveredRequest().execute(new Members().deliveredRequestLink(AndroidUtils.toLong(data.get("event_id"))));

        if (notifyType.equals("" + PushTable.PUSH_TYPE_INVITE_FRIEND_CREATE))
            priority = Notification.PRIORITY_HIGH;

        switch (notifyType) {
            case "" + PushTable.PUSH_TYPE_INVITE_CREATE:
            case "" + PushTable.PUSH_TYPE_INVITE_FRIEND_CREATE:
                notificationTag = "invite_" + data.get("event_id");
                notifyIcon = R.drawable.menu_invites;
                if (notifyText == null)
                    notifyText = Strings.get(R.string.add_invite);
                break;
            case "" + PushTable.PUSH_TYPE_INVITE_CANCELED:
                notificationTag = "invite_" + data.get("event_id");
                notifyIcon = R.drawable.menu_invites;
                notifyText = Strings.get(R.string.invite_deleted);
                break;
            case "" + PushTable.PUSH_TYPE_INVITE_UPDATED:
                notificationTag = "invite_" + data.get("event_id");
                notifyIcon = R.drawable.menu_invites;
                notifyText = Strings.get(R.string.invite_updated);
                break;
            case "" + PushTable.PUSH_TYPE_MESSAGE_INSERT:
                notificationTag = "chat_" + data.get("attach_type") + "," + data.get("attach_id");
                notifyIcon = R.drawable.menu_messages;
                break;
            case "" + PushTable.PUSH_TYPE_OWNER_LIKE:
                notifyIcon = R.drawable.menu_likes;
                if ("1".equals(data.get("is_confirm"))) {
                    notifyText = Strings.get(R.string.add_like_confirm);
                } else {
                    notifyText = Strings.get(R.string.add_like);
                }
                break;
            case "" + PushTable.PUSH_TYPE_MEMBER_INSERT:
                Long attach_type = AndroidUtils.toLong(data.get("attach_type"));
                if (attach_type.equals(Members.ATTACH_TYPE_FRIENDS)) {
                    notifyIcon = R.drawable.menu_contacts;
                    if ("1".equals(data.get("is_confirm"))) {
                        notifyText = Strings.get(R.string.add_freind_confirm);
                    } else {
                        notifyText = Strings.get(R.string.add_freind_request);
                    }
                }
                if (attach_type.equals(Members.ATTACH_TYPE_INVITE)) {
                    notifyIcon = R.drawable.menu_contacts;
                    notifyText = Strings.get(R.string.add_join);
                }
                break;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, createIntent(notificationTag, data),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder nb = new Notification.Builder(getApplicationContext())
                .setAutoCancel(true)
                .setSmallIcon(notifyIcon)
                .setContentTitle(data.get(PushTable.PUSH_TITLE))
                .setContentText(notifyText)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setPriority(priority)
                .setVibrate(new long[]{0, 200})
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notify));


        if (notifyType.equals("" + PushTable.PUSH_TYPE_INVITE_FRIEND_CREATE) ||
                notifyType.equals("" + PushTable.PUSH_TYPE_INVITE_CREATE))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                Intent accept_intent = createIntent(notificationTag, data);
                accept_intent.putExtra("action", "accept");
                nb.addAction(new Notification.Action(R.drawable.action_accept, Strings.get(R.string.accept_invite),
                        PendingIntent.getActivity(getApplicationContext(), 0, accept_intent, PendingIntent.FLAG_UPDATE_CURRENT)));

                Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
                for (String key : data.keySet())
                    intent.putExtra(key, data.get(key));
                nb.setDeleteIntent(PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0));
            }

        if (data.get(PushTable.PUSH_IMAGE) != null) {
            Bitmap photo = getBitmapfromUrl(data.get(PushTable.PUSH_IMAGE));
            if (photo != null)
                nb.setLargeIcon(photo);
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            notificationManager.notify(notificationTag, 0, nb.build());
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




}