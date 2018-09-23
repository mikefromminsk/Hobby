package com.club.minsk.invite_new.place;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.club.minsk.App;
import com.club.minsk.BuildConfig;
import com.club.minsk.R;
import com.club.minsk.db.Events;
import com.club.minsk.db.Strings;
import com.club.minsk.invite_new.comment.CommentFragment;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.AppFragment;
import com.club.minsk.utils.Cookies;
import com.club.minsk.utils.Format;
import com.club.minsk.utils.Geocoder;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlaceFragment extends AppFragment implements OnMapReadyCallback, View.OnClickListener {


    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    TextView invite_new_position_time_text;
    MaterialEditText invite_new_position_address_edit;
    RecyclerView invite_new_position_autocomplete;
    AutocompleteAdapter autocompleteAdapter;
    FloatingActionButton invite_new_position_ready;
    RelativeLayout invite_new_position_time_layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_invite_place, container, false);

        setTitle(R.string.set_position_title);

        invite_new_position_address_edit = (MaterialEditText) view.findViewById(R.id.invite_new_position_address_edit);
        invite_new_position_ready = (FloatingActionButton) view.findViewById(R.id.invite_new_position_ready);
        FloatingActionButton invite_new_position_zoom_add = (FloatingActionButton) view.findViewById(R.id.invite_new_position_zoom_add);
        FloatingActionButton invite_new_position_zoom_sub = (FloatingActionButton) view.findViewById(R.id.invite_new_position_zoom_sub);
        invite_new_position_time_layout = (RelativeLayout) view.findViewById(R.id.invite_new_position_time_layout);
        ImageView invite_new_position_time_image = (ImageView) view.findViewById(R.id.invite_new_position_time_image);
        ImageView invite_new_position_picker = (ImageView) view.findViewById(R.id.invite_new_position_picker);
        invite_new_position_time_text = (TextView) view.findViewById(R.id.invite_new_position_time_text);
        invite_new_position_autocomplete = (RecyclerView) view.findViewById(R.id.invite_new_position_autocomplete);


        if (App.event == null) {
            App.event = new Events().new Event();
            App.event.event_lat = Double.valueOf(Cookies.get("device_lat"));
            App.event.event_lon = Double.valueOf(Cookies.get("device_lon"));
            App.event.event_time = new Date().getTime() / 1000;
            if (App.event.event_filter_max_members == null)
                App.event.event_filter_max_members = 2L;
            if (App.event.event_filter_min_year == null)
                App.event.event_filter_min_year = 18L;
        }

        Bundle args = getArguments();
        if (args != null) {
            long invited_owner_id = args.getLong("invited");
            if (invited_owner_id != 0) {
                if (App.event.invite_list == null)
                    App.event.invite_list = new ArrayList<>();
                if (App.event.invite_list.indexOf(invited_owner_id) == -1)
                    App.event.invite_list.add(invited_owner_id);
            }
        }

        invite_new_position_time_image.setColorFilter(App.app_color);
        invite_new_position_ready.setColorNormal(App.app_color);
        invite_new_position_ready.setColorPressed(App.app_color);

        invite_new_position_ready.setOnClickListener(this);
        invite_new_position_picker.setOnClickListener(this);
        invite_new_position_time_text.setText(Format.dateFormat(App.event.event_time));
        invite_new_position_address_edit.setHint(Strings.get(R.string.address));
        invite_new_position_address_edit.setFloatingLabelText(Strings.get(R.string.address));
        if (App.event.event_address != null)
            invite_new_position_address_edit.setText(App.event.event_address);
        invite_new_position_autocomplete.setLayoutManager(new LinearLayoutManager(App.getActiveActivity(), LinearLayoutManager.VERTICAL, true));
        autocompleteAdapter = new AutocompleteAdapter(null);
        invite_new_position_autocomplete.setAdapter(autocompleteAdapter);

        invite_new_position_address_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    invite_new_position_autocomplete.setVisibility(View.VISIBLE);
                }
            }
        });

        invite_new_position_zoom_sub.setColorPressed(App.app_color);
        invite_new_position_zoom_add.setColorPressed(App.app_color);

        invite_new_position_zoom_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target,
                        mMap.getCameraPosition().zoom - 2));
            }
        });
        invite_new_position_zoom_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target,
                        mMap.getCameraPosition().zoom + 2));
            }
        });

        invite_new_position_address_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Geocoder.getAddressCompleted(charSequence.toString(), new Geocoder.AutocompleListener() {
                    @Override
                    public void run(List<Geocoder.Prediction> address) {
                        autocompleteAdapter.setData(address);
                        invite_new_position_autocomplete.scrollToPosition(0);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        invite_new_position_time_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(App.event.event_time * 1000L);
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog mDatePicker;
                        mDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                final int selYear = year;
                                final int selMonth = month;
                                final int selDay = day;

                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);
                                TimePickerDialog mTimePicker;
                                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        Calendar cal = Calendar.getInstance();
                                        cal.set(Calendar.YEAR, selYear);
                                        cal.set(Calendar.MONTH, selMonth);
                                        cal.set(Calendar.DAY_OF_MONTH, selDay);
                                        cal.set(Calendar.HOUR_OF_DAY, selectedHour);
                                        cal.set(Calendar.MINUTE, selectedMinute);

                                        App.event.event_time = cal.getTimeInMillis() / 1000;
                                        invite_new_position_time_text.setText(Format.dateFormat(App.event.event_time));
                                    }
                                }, hour, minute, true);
                                mTimePicker.setTitle(Strings.get(R.string.time));
                                mTimePicker.show();
                            }
                        }, year, month, day);
                        mDatePicker.setTitle(Strings.get(R.string.date));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            mDatePicker.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis() - 1000);
                            mDatePicker.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis() + 14 * 24 * 60 * 60 * 1000 + 1000);
                        }
                        mDatePicker.show();
                    }
                }, 500);

            }
        });

        return view;
    }


    @Override
    public void onClick(View view) {

        if (invite_new_position_autocomplete.getVisibility() == View.VISIBLE) {
            String address = invite_new_position_address_edit.getText().toString();
            Geocoder.LatLng latLng = Geocoder.getLocation(address);
            if (latLng != null) {
                App.event.event_lat = mMap.getCameraPosition().target.latitude;
                App.event.event_lon = mMap.getCameraPosition().target.longitude;
                App.event.event_address = address;
                invite_new_position_address_edit.setText(address);
            } else {
                App.user_show(R.string.place_not_found);
            }
            invite_new_position_autocomplete.setVisibility(View.GONE);

        } else {
            if (!BuildConfig.DEBUG) {
                App.event.event_lat = mMap.getCameraPosition().target.latitude;
                App.event.event_lon = mMap.getCameraPosition().target.longitude;
            }

            invite_new_position_ready.setEnabled(false);
            Geocoder.getAddress(App.event.event_lat, App.event.event_lon, new Geocoder.AddressListener() {
                @Override
                public void run(String address) {
                    if (address != null) {
                        App.event.event_address = address;
                        invite_new_position_address_edit.setText(address);
                        App.openFragment(new CommentFragment());
                    } else
                        invite_new_position_ready.setEnabled(true);
                }
            });
        }
    }

    private void setupMapIfNeeded() {
        if (mMap == null) {
            mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.invite_new_position_map));
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupMapIfNeeded();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (App.event.event_lat != null && App.event.event_lon != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(App.event.event_lat, App.event.event_lon), 14));
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Geocoder.getAddress(mMap.getCameraPosition().target.latitude,
                        mMap.getCameraPosition().target.longitude,
                        new Geocoder.AddressListener() {
                            @Override
                            public void run(String address) {
                                if (address != null) {
                                    App.event.event_address = address;
                                    invite_new_position_address_edit.setText(address);
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapFragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction().remove(mapFragment).commit();
            mapFragment = null;
        }
    }

    @Override
    public void onBack() {

    }

    public void setAutocomplete(Geocoder.Prediction prediction) {
        AndroidUtils.hideKeyboard(App.getActiveActivity());
        invite_new_position_autocomplete.setVisibility(View.GONE);
        invite_new_position_address_edit.setText(prediction.description);
        invite_new_position_time_layout.requestFocus();

        Geocoder.getPlace(prediction.place_id, new Geocoder.PlaceListener() {
            @Override
            public void run(Geocoder.PlaceResult place) {
                App.event.event_lat = place.geometry.location.lat.doubleValue();
                App.event.event_lon = place.geometry.location.lng.doubleValue();
                if (!BuildConfig.DEBUG)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(App.event.event_lat, App.event.event_lon), 14));
            }
        });
    }

}
