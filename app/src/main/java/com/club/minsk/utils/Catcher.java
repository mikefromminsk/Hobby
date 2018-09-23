package com.club.minsk.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.club.minsk.App;
import com.club.minsk.SplashActivity;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Catcher implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;
    public static int open_count = 1;

    public Catcher() {
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {

        if (open_count <= 2) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            final String stack = sw.toString();

            Intent intent = new Intent(App.getActiveActivity(), SplashActivity.class);
            intent.putExtra("stack", stack);
            intent.putExtra("open_count", open_count + 1);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(App.getActiveActivity()
                    , 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Following code will restart your application after 2 seconds
            AlarmManager mgr = (AlarmManager) App.getActiveActivity().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);

            //This will stop your application and take out from it.
            System.exit(2);
        }
        else
            defaultUEH.uncaughtException(thread, throwable);
    }
}
