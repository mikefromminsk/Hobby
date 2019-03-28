package com.club.minsk.utils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Strings;
import com.club.minsk.db.utils.ApiRequest;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

public class Geocoder {


    public class LatLng {
        public BigDecimal lat;
        public BigDecimal lng;
    }

    public class GeocoderGeometry {
        public LatLng location;
    }

    public class GeocoderResult {
        public List<String> types;
        public String formattedAddress;
        public String formatted_address;
        public GeocoderGeometry geometry;
    }

    public class GeocodeResponse {
        public Object status;
        public List<GeocoderResult> results;
    }

    static Gson gson = (new GsonBuilder()).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    static Object getJson(String urlString, Class parseObject) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setConnectTimeout(10 * 1000);
            huc.setRequestMethod("GET");
            huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
            huc.connect();
            InputStreamReader reader = new InputStreamReader(huc.getInputStream(), "UTF-8");

            try {
                return gson.fromJson(reader, parseObject);
            } finally {
                reader.close();
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        }
        return null;
    }

    static long lastRequestTime = 0;

    public static LatLng getLocation(String address) {
        if (address == null)
            return null;

        if (new Date().getTime() - lastRequestTime <= 100) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("getLocation: " + address);

        String urlString = "http://maps.googleapis.com/maps/api/geocode/json?";
        urlString = urlString + "&address=" + URLEncoder.encode(address);
        GeocodeResponse reponse = (GeocodeResponse) getJson(urlString, GeocodeResponse.class);

        if (reponse != null && reponse.results.size() >= 1)
            return reponse.results.get(0).geometry.location;

        return null;
    }

    //
    public static String getAddress(double lat, double lon, String language) {
        if (lat == 0 || lon == 0)
            return null;

        if (new Date().getTime() - lastRequestTime <= 100) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("getAddress: " + lat + " " + lon);

        String urlString = getAddressLink(lat, lon, language);
        GeocodeResponse reponse = (GeocodeResponse) getJson(urlString, GeocodeResponse.class);

        if (reponse != null && reponse.results.size() >= 1)
            return reponse.results.get(0).formattedAddress;

        return null;
    }

    public static String getAddressLink(double lat, double lon, String language) {
        return "https://maps.googleapis.com/maps/api/geocode/json?"
                + "&latlng=" + lat + "," + lon
                + "&language=" + URLEncoder.encode(language)
                + "&key=" + Strings.get(R.string.google_maps_geocoding_api_key);
    }


    public static String getAutocompleteLink(String address, String lang) {
        String url =  "https://maps.googleapis.com/maps/api/place/autocomplete/json?types=geocode&sensor=false"
                + "&key=" + Strings.get(R.string.google_maps_geocoding_api_key)
                + "&input=" + URLEncoder.encode(address)
                + "&language=" + lang;
        return url;
    }

    public static String getPlaceLink(String placeid, String lang){
        return "https://maps.googleapis.com/maps/api/place/details/json?"
                + "&key=" + Strings.get(R.string.google_maps_geocoding_api_key)
                + "&placeid=" + placeid
                + "&language=" + lang;
    }


    public class Prediction {
        public String description;
        public String place_id;
    }

    public class AutoComplete {
        public List<Prediction> predictions;
    }

      /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                }
            });
*/



    public interface AddressListener {
        void run(String address);
    }

    public static void getAddress(double lat, double lon, final AddressListener listener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Geocoder.getAddressLink(lat, lon, AndroidUtils.getLang(App.getActiveActivity())),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Geocoder.GeocodeResponse reponse = ApiRequest.json.fromJson(jsonObject.toString(), Geocoder.GeocodeResponse.class);
                        if (reponse != null && reponse.results.size() >= 1)
                            listener.run(reponse.results.get(0).formatted_address);
                        else
                            listener.run(null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        listener.run(null);
                    }
                });
        App.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public interface AutocompleListener {
        void run(List<Geocoder.Prediction>  address);
    }

    public static void getAddressCompleted(String address, final AutocompleListener listener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Geocoder.getAutocompleteLink(address, AndroidUtils.getLang(App.getActiveActivity())),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Geocoder.AutoComplete response = ApiRequest.json.fromJson(jsonObject.toString(),
                                Geocoder.AutoComplete.class);
                        if (response.predictions != null && response.predictions.size() != 0){
                            listener.run(response.predictions);
                        }
                        else
                            listener.run(null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        listener.run(null);
                    }
                });
        App.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    class Place{
        public PlaceResult result;
    }
    public class PlaceResult{
        public GeocoderGeometry geometry;
    }

    public interface PlaceListener {
        void run(PlaceResult place);
    }

    public static void getPlace(String placeid, final PlaceListener listener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Geocoder.getPlaceLink(placeid, AndroidUtils.getLang(App.getActiveActivity())),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Place response = ApiRequest.json.fromJson(jsonObject.toString(),
                                Place.class);
                        if (response.result != null){
                            listener.run(response.result);
                        }
                        else
                            listener.run(null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        listener.run(null);
                    }
                });
        App.getInstance().addToRequestQueue(jsonObjectRequest);

    }
}
