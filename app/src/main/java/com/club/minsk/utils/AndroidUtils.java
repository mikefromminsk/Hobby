package com.club.minsk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AndroidUtils {


    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static Point getDisplaySize(WindowManager windowManager){
        try {
            if(Build.VERSION.SDK_INT > 16) {
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);
                return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
            }else{
                return new Point(0, 0);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new Point(0, 0);
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    static void androidUtilsLog(String message) {
        //App.getInstance().show(message);
    }

    public static String base64Decode(String message) {
        if (message == null)
            return "";
        try {
            byte[] strData = Base64.decode(message, Base64.DEFAULT);
            message = new String(strData, "UTF-8");
        } catch (Exception e) {
            androidUtilsLog(e.getMessage());
        }
        return message;
    }

    public static String base64Encode(String base64) {
        try {
            byte[] data = base64.getBytes("UTF-8");
            base64 = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception ignored) {
        }
        return base64.trim();
    }


    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static long distMeters(double lat, double lon, double lat2, double lon2) {
        return (int) (6371f * Math.acos(Math.cos(Math.toRadians(lat2)) //3959 miles
                * Math.cos(Math.toRadians(lat))
                * Math.cos(Math.toRadians(lon) - Math.toRadians(lon2))
                + Math.sin(Math.toRadians(lat2))
                * Math.sin(Math.toRadians(lat))) * 1000f);
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }


    public static void playSound(String sound) {
        new Thread(new PlayRun(sound)).start();
    }

    public static String getModel() {
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;
        return model;
    }

    public static String getLang(Context context) {
        return context.getResources().getConfiguration().locale.getCountry();
    }

    public static int getIntResByName(Context context, String name) {
        int resId = context.getResources().getIdentifier(name, "integer", context.getPackageName());
        try {
            return context.getResources().getInteger(resId);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String implode(String separator, List<String> strings) {
        String result = "";
        if (strings != null)
            for (int i = 0; i < strings.size(); i++)
                result += strings.get(i) + (i != strings.size() - 1 ? separator : "");
        return result;
    }

    public static List<String> explode(String separator, String string) {
        List<String> result = new ArrayList<>();
        if (string != null && !string.isEmpty())
            result.addAll(Arrays.asList(string.split(separator)));
        return result;
    }

    public static void shareText(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/html");
        context.startActivity(sendIntent);
    }

    public static Long toLong(String number) {
        if (number == null)
            return null;
        return (long) (double) Double.valueOf(number);
    }

    public static void openAppInGooglePlay(Context context) {
        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static String googlePlayLink(Context context) {
        return "https://play.google.com/store/apps/details?id=" + context.getPackageName();
    }

    private static class PlayRun implements Runnable {
        String audio;
        final MediaPlayer mediaPlayer = new MediaPlayer();

        public PlayRun(String audio) {
            this.audio = audio;
        }

        @Override
        public void run() {
            try {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.setDataSource(audio);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.prepare();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*public static void callPhone(Context context, String tel) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        context.startActivity(intent);
    }*/
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnected();
    }

    public static String streamToString(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

}
