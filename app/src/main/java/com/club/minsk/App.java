package com.club.minsk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.club.minsk.db.Events;
import com.club.minsk.login.Person;
import com.club.minsk.login.reg.AvatarFragment;
import com.club.minsk.login.reg.NameFragment;
import com.club.minsk.login.reg.PhoneFragment;
import com.club.minsk.login.reg.SexFragment;
import com.club.minsk.login.reg.YearFragment;
import com.club.minsk.start.StartFragment;
import com.club.minsk.utils.Catcher;
import com.karumi.dexter.Dexter;
import com.club.minsk.db.Devices;
import com.club.minsk.db.Messages;
import com.club.minsk.db.tables.EventsTable;
import com.club.minsk.db.utils.ApiRequest;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.AppFragment;
import com.club.minsk.db.Strings;
import com.club.minsk.utils.Cookies;
import com.club.minsk.utils.LruBitmapCache;

import java.io.Serializable;
import java.util.List;

public class App extends MultiDexApplication {

    private static App mInstance;
    private static AppCompatActivity activeActivity;

    public static Events.Event event;

    public static int app_color = 0xff665cac;
    public static Person owner = new Person();
    public static List<Messages.ChatsList.Chat> chats;
    public static String rq_link;

    @Override
    public void onCreate() {
        if (!BuildConfig.DEBUG)
            Thread.setDefaultUncaughtExceptionHandler(new Catcher());

        super.onCreate();
        mInstance = this;

        Cookies.init(getApplicationContext());
    }


    public static synchronized App getInstance() {
        return mInstance;
    }


    public static Integer getIntResByName(String aString) {
        return AndroidUtils.getIntResByName(getInstance(), aString);
    }


    public static void log(String log_text) {
        if (BuildConfig.DEBUG) {
            Log.e("ssds", log_text);
        }
    }


    public static AppCompatActivity getActiveActivity() {
        return activeActivity;
    }

    public void setActiveActivity(AppCompatActivity activity) {
        Dexter.initialize(activity);
        activeActivity = activity;
    }

    public static Fragment getActiveFragment() {
        FragmentActivity activity = getActiveActivity();
        if (activity instanceof StartActivity) {
            return activity.getSupportFragmentManager().findFragmentById(R.id.start_content_frame);
        }
        if (activity instanceof MainActivity) {
            return activity.getSupportFragmentManager().findFragmentById(R.id.main_content_frame);
        }
        return null;
    }

    private LocationManager locationManager;

    public LocationManager getLocationManager() {
        if (locationManager == null)
            locationManager = (LocationManager) getActiveActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager;
    }


    public static void updateLocation(double latitude, double longitude, long gps_time) {
        if (!BuildConfig.DEBUG) {
            Cookies.set("device_lat", "" + latitude);
            Cookies.set("device_lon", "" + longitude);
            if (gps_time != 0)
                Cookies.set("device_gps_time", "" + gps_time / 1000);
        }
        new Devices().insert();
    }

    public LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (location == null)
                return;
            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                updateLocation(location.getLatitude(), location.getLongitude(), location.getTime() / 1000);
            }
            if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER) && (Cookies.get("device_gps_time") == null)) {
                updateLocation(location.getLatitude(), location.getLongitude(), location.getTime() / 1000);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };


    static Bundle argsToBundle(Object... args) throws Exception {
        Bundle bundle = new Bundle();
        if (args.length == 0) {
            return null;
        } else if (args.length == 1) {
            Object param = args[0];
            if (param instanceof Bundle) {
                bundle = (Bundle) param;
            }
        } else
            for (int i = 0; i < args.length / 2; i++) {
                String key = (String) args[i * 2];
                Object param = args[(i * 2) + 1];
                if (param == null) {
                } else if (param instanceof String) {
                    bundle.putString(key, (String) param);
                } else if (param instanceof Integer) {
                    bundle.putInt(key, (Integer) param);
                } else if (param instanceof Double) {
                    bundle.putDouble(key, (Double) param);
                } else if (param instanceof Long) {
                    bundle.putLong(key, (Long) param);
                } else if (param instanceof Serializable) {
                    bundle.putSerializable(key, (Serializable) param);
                } else {
                    throw new Exception();
                }
            }
        return bundle;
    }


    static int ANIMATION_FADE = 0;
    static int ANIMATION_ALPHA = 1;

    static void openFragment(boolean clear_back_stack,
                             int animation,
                             boolean fragment_replace,
                             boolean add_to_back_stack,
                             Fragment fragment, Object... args) {
        FragmentActivity activity = getActiveActivity();

        Bundle bundle = null;
        try {
            bundle = argsToBundle(args);
            if (bundle != null)
                fragment.setArguments(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        if (clear_back_stack)
            fragmentTransaction.disallowAddToBackStack();
        if (animation == ANIMATION_FADE)
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_pop_in, R.anim.fade_pop_out);
        else if (animation == ANIMATION_ALPHA)
            fragmentTransaction.setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out, R.anim.alpha_in, R.anim.alpha_out);

        if (fragment_replace) {
            if (activity instanceof StartActivity) {
                fragmentTransaction.replace(R.id.start_content_frame, fragment);
            } else if (activity instanceof MainActivity) {
                fragmentTransaction.replace(R.id.main_content_frame, fragment);
            }
        } else {
            if (activity instanceof StartActivity) {
                fragmentTransaction.add(R.id.start_content_frame, fragment);
            } else if (activity instanceof MainActivity) {
                fragmentTransaction.add(R.id.main_content_frame, fragment);
            }
        }
        if (add_to_back_stack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        App.closeMenu();
        AndroidUtils.hideKeyboard(App.getActiveActivity());
    }


    final static int OPEN_FRAGMENT = 0;
    final static int START_FRAGMENT = 1;
    final static int ADD_FRAGMENT = 2;

    public static void openFragment(Fragment fragment, Object... args) {
        dynOpenFragment(OPEN_FRAGMENT, fragment, args);
    }

    public static void startFragment(Fragment fragment, Object... args) {
        dynOpenFragment(START_FRAGMENT, fragment, args);
    }

    public static void addFragment(Fragment fragment, Object... args) {
        dynOpenFragment(ADD_FRAGMENT, fragment, args);
    }

    public static void insertEvent(List<Long> selected, final Events.InsertUpdateEventListener listener) {
        final ProgressDialog dialog = new ProgressDialog(getActiveActivity());
        dialog.setMessage(Strings.get(R.string.event_insert_loading_dialog));
        dialog.setCancelable(false);
        dialog.show();
        if (event.event_id == null) {
            new Events().insertUpdateEvent(event, selected, new Events.InsertUpdateEventListener() {
                        @Override
                        public void run(Events.InsertUpdateEventResponse response) {
                            dialog.dismiss();
                            listener.run(response);
                        }
                    },
                    new ApiRequest.ErrorListener() {
                        @Override
                        public void run(ApiRequest.Error error) {
                            if (error.error_code == EventsTable.EVENT_ERROR_MORE_INSERT_IN_INTERVAL)
                                App.user_show(R.string.more_events);
                            else
                                App.user_show(R.string.event_create_error);
                            dialog.dismiss();
                            listener.run(null);
                        }
                    });
        } else {
            new Events().insertUpdateEvent(event, selected, new Events.InsertUpdateEventListener() {
                        @Override
                        public void run(Events.InsertUpdateEventResponse response) {
                            App.user_show(R.string.event_is_update);
                            dialog.dismiss();
                            listener.run(response);
                        }
                    },
                    new ApiRequest.ErrorListener() {
                        @Override
                        public void run(ApiRequest.Error error) {
                            App.user_show(R.string.event_is_not_update);
                            dialog.dismiss();
                            listener.run(null);
                        }
                    });
        }
    }

    public static Object getHost() {
        return Strings.get(BuildConfig.DEBUG ? "debug_host" : "release_host");
    }

    public static void nextRegistrationFragment() {
        if (owner.sex == null)
            App.openFragment(new SexFragment());
        else if (owner.bdate == null)
            App.openFragment(new YearFragment());
        else if (owner.first_name == null)
            App.openFragment(new NameFragment());
        else if (owner.photo == null)
            App.openFragment(new AvatarFragment());
        else if (owner.phone == null)
            App.openFragment(new PhoneFragment());
        else
            App.openFragment(new StartFragment());
    }


    static class DelayOpen extends AsyncTask<Void, Void, Void> {
        Fragment fragment;
        Object[] args;

        public DelayOpen(Fragment fragment, Object... args) {
            super();
            this.args = args;
            this.fragment = fragment;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }
            openFragment(false, ANIMATION_FADE, true, true, fragment, args);
            return null;
        }
    }

    public static void dynOpenFragment(int openType, Fragment fragment, Object... args) {
        switch (openType) {
            case OPEN_FRAGMENT:
                new DelayOpen(fragment, args).execute();
                break;

            case START_FRAGMENT:
                openFragment(true, ANIMATION_ALPHA, true, false, fragment, args);
                break;

            case ADD_FRAGMENT:
                openFragment(false, ANIMATION_ALPHA, false, true, fragment, args);
                break;
        }
    }

    public static void backFragment() {
        getActiveActivity().getSupportFragmentManager().popBackStackImmediate();
        Fragment fragment = getActiveActivity().getSupportFragmentManager().findFragmentById(R.id.main_content_frame);
        if (fragment instanceof AppFragment)
            ((AppFragment) fragment).onBack();
    }

    public static void closeMenu() {
        if (App.getActiveActivity() instanceof MainActivity) {
            App.getActiveActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DrawerLayout drawer = (DrawerLayout) App.getActiveActivity().findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            });
        }
    }


    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(getInstance().getApplicationContext());
        return mRequestQueue;
    }

    public static ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null)
            mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache());
        return mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


    public static void show(String error) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(getInstance(), error, Toast.LENGTH_SHORT).show();
            log(error);
        }
    }

    public static void user_show(int message_resource_id) {
        String message = Strings.get(message_resource_id);
        if (getActiveActivity() instanceof StartActivity)
            Snackbar.make(getActiveActivity().findViewById(R.id.start_content_frame), message, Snackbar.LENGTH_SHORT).show();
        if (getActiveActivity() instanceof MainActivity)
            Snackbar.make(getActiveActivity().findViewById(R.id.main_content_frame), message, Snackbar.LENGTH_SHORT).show();
    }


    public static void logout() {
        Cookies.clear();
        Cookies.init(getInstance());
        activeActivity.startActivity(new Intent(activeActivity, StartActivity.class));
        activeActivity.finish();
    }

}
