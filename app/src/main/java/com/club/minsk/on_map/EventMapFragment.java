package com.club.minsk.on_map;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.club.minsk.db.Devices;
import com.club.minsk.db.Links;
import com.club.minsk.invite.InviteFragment;
import com.club.minsk.owner.OwnerFragment;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Events;
import com.club.minsk.db.Owners;
import com.club.minsk.db.tables.DevicesTable;
import com.club.minsk.utils.AppFragment;
import com.club.minsk.utils.Cookies;
import com.club.minsk.utils.Format;

import java.util.ArrayList;
import java.util.List;

public class EventMapFragment extends AppFragment implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    ClusterManager mClusterManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_map, container, false);
        setTitle(R.string.menu_map);

        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.event_map2));

        mapFragment.getMapAsync(this);

        FloatingActionButton zoom_add = (FloatingActionButton)view.findViewById(R.id.zoom_add);
        FloatingActionButton zoom_sub = (FloatingActionButton)view.findViewById(R.id.zoom_sub);

        zoom_sub.setColorPressed(App.app_color);
        zoom_add.setColorPressed(App.app_color);

        zoom_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target,
                        mMap.getCameraPosition().zoom - 2));
            }
        });
        zoom_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target,
                        mMap.getCameraPosition().zoom + 2));
            }
        });


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Cookies.getFloat("device_lat"), Cookies.getFloat("device_lon")), 14));

        mClusterManager = new ClusterManager<>(App.getActiveActivity(), mMap);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        final CustomClusterRenderer renderer = new CustomClusterRenderer(mClusterManager);
        mClusterManager.setRenderer(renderer);
        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ImageCluster>() {
            @Override
            public void onClusterItemInfoWindowClick(ImageCluster marker) {
                if (marker.attach instanceof Events.Event) {
                    Events.Event event = (Events.Event) marker.attach;
                    App.openFragment(new InviteFragment(), "event_id", event.event_id);
                } else if (marker.attach instanceof DevicesTable.Device) {
                    DevicesTable.Device device = (DevicesTable.Device) marker.attach;
                    App.openFragment(new OwnerFragment(), "owner_id", device.owner_id);
                }
            }

        });

        new Events().selectBeside(0, 50, new Events.ListListener() {
            @Override
            public void run(Events.ListResponse response) {
                if (response.event_id_list != null)
                    for (Long event_id : response.event_id_list)
                        App.getImageLoader().get(
                                "http://" + App.getHost() + "/img/picker.png",
                                new ImageCluster(Events.get(event_id)));
                if (response.device_id_list != null)
                    for (String device_id : response.device_id_list)
                        if (Devices.get(device_id).owner_id != null)
                            App.getImageLoader().get(
                                    Links.get(Owners.get(Devices.get(device_id).owner_id).owner_avatar_link_id),
                                    new ImageCluster(Devices.get(device_id)));
            }
        }, null);

    }

    Double deg2rad(Double deg) {
        return deg * (Math.PI / 180);
    }

    double dist(Double lat1, Double lon1, Double lat2, Double lon2) {
        Double R = 6371d; // Radius of the earth in km
        Double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        Double dLon = deg2rad(lon2 - lon1);
        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double d = R * c; // Distance in km
        return d * 1000d;
    }

    List<LatLng> dynMarkers = new ArrayList<>();

    boolean is_free(Double lat,Double  lon, Double dist) {
        for (int i = 0; i < dynMarkers.size(); i++)
            if (dist(dynMarkers.get(i).latitude, dynMarkers.get(i).longitude, lat, lon) <= dist)
                return false;
        return true;
    };

    LatLng move(Double lat, Double lon, Double x_meters, Double y_meters) {
        Double pi = Math.PI;
        Double R = 6371000d;
        Double dLat = x_meters / R;
        Double dLng = y_meters / ( R * Math.cos(pi * lat / 180) );
        lat = lat + ( dLat * 180 / pi );
        lon = lon + ( dLng * 180 / pi );
        return new LatLng(lat, lon);
    };

    LatLng spiral(Double index) {
        Double needP, gotP;
        Double phi, ep, x = 0d, y = 0d, x2, y2, xC, yC, D, l;

        x2 = y2 = xC = yC = 0d; //центр спирали
        phi = 0d;
        ep = 0.1d;//ep - точность (чем меньше, тем меньше погрешность)
        D = 100d;// D - расстояние между точками
        l = 0d;
        needP = index;//needP - сколько нужно точек
        gotP = 0d;

        while (gotP < needP) {
            x = xC + 10 * phi * Math.sin(phi);
            y = yC + 10 * phi * Math.cos(phi);
            l += Math.sqrt((x2 - x) * (x2 - x) + (y2 - y) * (y2 - y));
            x2 = x;
            y2 = y;
            if (l > D) {
                //когда мы сюда заходим, в x, y лежат координаты точки с индексом gotP
                l = 0d;
                gotP++;
            }
            phi += ep;
        }
        return new LatLng(x/ 5d, y/5d);
    };


    LatLng getMarkerPosition(Double lat, Double lon){
        LatLng pos = new LatLng(lat, lon);
        Double index = 1d;
        while (!is_free(pos.latitude, pos.longitude, 10d)) {
            LatLng move_to = spiral(index);
            pos = move(pos.latitude, pos.longitude, move_to.latitude, move_to.longitude);
            index += 1d;
        }
        dynMarkers.add(pos);
        return pos;
    }

    public class CustomClusterRenderer extends DefaultClusterRenderer<ImageCluster> {
        public CustomClusterRenderer(ClusterManager<ImageCluster> clusterManager) {
            super(App.getActiveActivity(), mMap, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(ImageCluster item,
                                                   MarkerOptions markerOptions) {
            Object attach = item.attach;
            String title = "";
            String info = "";
            Bitmap bitmap = null;
            if (attach instanceof Events.Event) {
                Events.Event event = (Events.Event) attach;
                title = Owners.get(event.owner_id).owner_name;
                info = Format.dateFormat(event.event_time);
                bitmap = Bitmap.createScaledBitmap(item.bitmap, 50, 50, false);

            }
            if (attach instanceof DevicesTable.Device) {
                DevicesTable.Device device = (DevicesTable.Device) attach;
                title = Owners.get(device.owner_id).owner_name;
                info = Format.bdateFormat(Owners.get(device.owner_id).owner_birthdate);
                bitmap = Bitmap.createScaledBitmap(item.bitmap, 50, 50, false);
            }
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .anchor(0.5f, 0.5f)
                    .title(title)
                    .snippet(info);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!getActivity().isFinishing())
            getActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
    }

    @Override
    public void onBack() {

    }

    class ImageCluster implements com.android.volley.toolbox.ImageLoader.ImageListener, ClusterItem {

        Object attach;
        Bitmap bitmap;
        LatLng pos;

        public ImageCluster(Object attach) {
            this.attach = attach;
        }

        @Override
        public LatLng getPosition() {
            if (pos != null)
                return pos;
            if (attach instanceof Events.Event) {
                Events.Event event = (Events.Event) attach;
                pos = getMarkerPosition(event.event_lat, event.event_lon);
                return pos;
            }
            if (attach instanceof DevicesTable.Device) {
                DevicesTable.Device device = (DevicesTable.Device) attach;
                pos = getMarkerPosition(device.device_lat, device.device_lon);
                return pos;
            }
            pos = getMarkerPosition(Cookies.getFloat("device_lat"), Cookies.getFloat("device_lon"));
            return pos;
        }

        @Override
        public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer imageContainer, boolean b) {
            if (imageContainer.getBitmap() != null) {
                bitmap = imageContainer.getBitmap();
                mClusterManager.addItem(this);
                mClusterManager.cluster();
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }
    }
}
