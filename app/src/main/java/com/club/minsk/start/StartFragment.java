package com.club.minsk.start;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.club.minsk.login.SocialFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.club.minsk.App;
import com.club.minsk.error.ErrorFragment;
import com.club.minsk.utils.Cookies;
import com.club.minsk.MainActivity;
import com.club.minsk.R;
import com.club.minsk.db.Owners;
import com.club.minsk.db.utils.ApiRequest;
import com.club.minsk.utils.AppFragment;
import com.club.minsk.db.Strings;

public class StartFragment extends AppFragment {

    Button hello_next;
    TextView start_text;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        vk_owner_image = (ImageView) view.findViewById(R.id.vk_owner_image);
        owner_insert_gsp_image = (ImageView) view.findViewById(R.id.owner_insert_gsp_image);
        start_text = (TextView) view.findViewById(R.id.start_text);


        hello_next = (Button) view.findViewById(R.id.hello_next);

        hello_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!vk_owner_image.isSelected()) {
                    initOwner();
                } else if (!owner_insert_gsp_image.isSelected()) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                } else {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (hello_next.getVisibility() != View.VISIBLE)
                    checkAndStart();
            }
        }, 10000);

        initOwner();
        if (ActivityCompat.checkSelfPermission(App.getActiveActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(App.getActiveActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Dexter.checkPermission(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse response) {
                    getLastLocation();
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse response) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            getLastLocation();
        }
        return view;
    }


    void getLastLocation() {
        LocationManager locationManager = App.getInstance().getLocationManager();

        if (ActivityCompat.checkSelfPermission(App.getActiveActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(App.getActiveActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        // set location
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            Long locationTime = location.getTime();
            App.updateLocation(location.getLatitude(), location.getLongitude(), locationTime);
        }

        //update location NETWORK_PROVIDER
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, App.getInstance().locationListener, null);


        try {
            getActivity().unregisterReceiver(mGpsSwitchStateReceiver);
        } catch (IllegalArgumentException e) {
        }
        getActivity().registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        testGps();
    }

    private BroadcastReceiver mGpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                if (App.getInstance().getLocationManager() != null
                        && App.getInstance().getLocationManager().getProvider(LocationManager.GPS_PROVIDER) != null
                        && App.getInstance().getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    owner_insert_gsp_image.setSelected(true);
                    checkAndStart();
                } else {
                    owner_insert_gsp_image.setSelected(false);
                }
            }
        }
    };

    void testGps() {
        if (/*(Cookies.get("first_start") == null)
                &&*/ App.getInstance().getLocationManager() != null
                && App.getInstance().getLocationManager().getProvider(LocationManager.GPS_PROVIDER) != null) {
            if (App.getInstance().getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                owner_insert_gsp_image.setSelected(true);
                if (vk_owner_image.isSelected() && owner_insert_gsp_image.isSelected())
                    checkAndStart();
            } else {
                owner_insert_gsp_image.setSelected(false);
            }
        }
    }


    public void initOwner() {

        if (Owners.self() != null) {
            vk_owner_image.setSelected(true);
        } else {
            setText(Strings.get(R.string.login_to_server));
            vk_owner_image.setSelected(false);

            if (Cookies.get("owner_id") != null)
                new Owners().login(ownerInsertRequestListener, ownerInsertErrorListenerListener);
            else
                App.startFragment(new SocialFragment());
        }

    }

    Owners.Listener ownerInsertRequestListener = new Owners.Listener() {

        @Override
        public void run(Owners.LoginData loginData) {
            Cookies.set("owner_id", "" + loginData.owner_id);
            vk_owner_image.setSelected(true);
            checkAndStart();
        }
    };


    ApiRequest.ErrorListener ownerInsertErrorListenerListener = new ApiRequest.ErrorListener() {
        @Override
        public void run(ApiRequest.Error error) {
            if (error.error_code == ApiRequest.ERROR_CONNECTION) {
                checkAndStart();
            } else if (error.error_code == ApiRequest.ERROR_TOKEN_IS_BAD) {
                try {
                    getActivity().unregisterReceiver(mGpsSwitchStateReceiver);
                } catch (IllegalArgumentException e) {
                }
                App.logout();
            } else {
                App.startFragment(new ErrorFragment(), "error", error);
            }
        }
    };


    ImageView vk_owner_image;
    ImageView owner_insert_gsp_image;

    @Override
    public void onBack() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (first_checkAndStart)
            checkAndStart();
    }

    class TextSetRunnable implements Runnable {
        String text;
        TextView start_text;

        public TextSetRunnable(String text, TextView start_text) {
            this.text = text;
            this.start_text = start_text;
        }

        @Override
        public void run() {
            start_text.setText(text);
            start_text.setVisibility(View.VISIBLE);
        }
    }

    void setText(String text) {
        start_text.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new TextSetRunnable(text, start_text), 350);
    }

    Handler openMainActivityThread;
    Boolean first_checkAndStart = false;

    void checkAndStart() {
        first_checkAndStart = true;
        if (!vk_owner_image.isSelected()) {
            if (getActivity() != null && !getActivity().isFinishing()) {
                if (start_text.getText().toString().equals(Strings.get(R.string.login_to_server)))
                    setText(Strings.get(R.string.check_internet));
                hello_next.setText(Strings.get(R.string.repeat_login));
                hello_next.setVisibility(View.VISIBLE);
            }
        } else if (!owner_insert_gsp_image.isSelected()) {
            setText(Strings.get(R.string.enable_gps));
            hello_next.setText(Strings.get(R.string.enable_gps_settings));
            hello_next.setVisibility(View.VISIBLE);
        } else {
            if (openMainActivityThread == null) {
                startGpsUpdates();
                start_text.setVisibility(View.INVISIBLE);
                hello_next.setVisibility(View.INVISIBLE);
                openMainActivityThread = new Handler();
                openMainActivityThread.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getActivity().unregisterReceiver(mGpsSwitchStateReceiver);
                        } catch (IllegalArgumentException e) {
                        }
                        FragmentActivity activity = App.getActiveActivity();
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtras(activity.getIntent());
                        activity.startActivity(intent);
                        activity.finish();
                    }
                }, 350);
            }
        }
    }


    void startGpsUpdates() {
        LocationManager locationManager = App.getInstance().getLocationManager();
        if (ActivityCompat.checkSelfPermission(App.getActiveActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 60 * 1000, 10, App.getInstance().locationListener);
    }

}
