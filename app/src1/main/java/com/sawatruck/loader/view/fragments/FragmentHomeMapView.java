package com.sawatruck.loader.view.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.AddressDetail;
import com.sawatruck.loader.entities.Advertisement;
import com.sawatruck.loader.utils.AppSettings;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Serializer;
import com.sawatruck.loader.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by royal on 8/20/2017.
 */

public class FragmentHomeMapView extends BaseFragment implements OnMapReadyCallback {
    private static final String ARG_POSITION = "position";
    public static int ZOOM = 10;
    private GoogleMap mMap;
    private SupportMapFragment supportMapFragment;

    private static FragmentHomeMapView _instance;
    private ArrayList<Advertisement> advertisements = new ArrayList<>();

    public static FragmentHomeMapView getInstance(int position) {
        FragmentHomeMapView f = new FragmentHomeMapView();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_map_view, container, false);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        try {
            searchAds();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        _instance = this;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        _instance = null;
    }



    private void searchAds() throws JSONException {
        LatLng latLng = new LatLng(AppSettings.with(getContext()).getCurrentLat(), AppSettings.with(getContext()).getCurrentLng());
        String strPickupLocation = AppSettings.with(getContext()).getPickupLocation();
        Double pickupLatitude = latLng.latitude;
        Double pickupLongitude = latLng.longitude;
        try {
            AddressDetail addressDetail = Serializer.getInstance().deserializeAddressDetail(strPickupLocation);
            pickupLatitude =  addressDetail.getLatitude();
            pickupLongitude =  addressDetail.getLongitude();
        }catch (Exception e) {
            Logger.error("----------------Pickup 1111111111111111location setting is null-----------------");
        }

        JSONObject rootObject = new JSONObject();
        rootObject.put("Length", 20);
        rootObject.put("Start", 0);

        JSONObject loadTypeObject = new JSONObject();
        loadTypeObject.put("Name", "LoadType");

        loadTypeObject.put("Data", "");
        loadTypeObject.put("Orderable", false);
        loadTypeObject.put("Searchable", false);

        JSONObject searchObject1 = new JSONObject();
        // TODO LOADTYPES
        searchObject1.put("Value", "");
        searchObject1.put("Regex", false);

        loadTypeObject.put("Search", searchObject1);


        JSONObject truckClassObject = new JSONObject();
        truckClassObject.put("Name", "TruckClass");
        truckClassObject.put("Data", "");
        truckClassObject.put("Orderable", false);
        truckClassObject.put("Searchable", true);

        JSONObject searchObject2 = new JSONObject();
        searchObject2.put("Value", "");
        searchObject2.put("Regex", false);

        truckClassObject.put("Search", searchObject2);

        JSONObject fromLatitudeObject = new JSONObject();
        fromLatitudeObject.put("Name", "FromLatitude");
        fromLatitudeObject.put("Data", "");
        fromLatitudeObject.put("Orderable", true);
        fromLatitudeObject.put("Searchable", true);

        JSONObject searchObject3 = new JSONObject();

        searchObject3.put("Value", String.valueOf(pickupLatitude));
        searchObject3.put("Regex", false);
        fromLatitudeObject.put("Search", searchObject3);


        JSONObject fromLongitudeObject = new JSONObject();
        fromLongitudeObject.put("Name", "FromLongitude");
        fromLongitudeObject.put("Data", "");
        fromLongitudeObject.put("Orderable", true);
        fromLongitudeObject.put("Searchable", true);

        JSONObject searchObject4 = new JSONObject();

        searchObject4.put("Value", String.valueOf(pickupLongitude));
        searchObject4.put("Regex", false);
        fromLongitudeObject.put("Search", searchObject4);

        JSONObject distanceObject = new JSONObject();
        distanceObject.put("Name", "Distance");
        distanceObject.put("Data", "");
        distanceObject.put("Orderable", true);
        distanceObject.put("Searchable", true);

        JSONObject searchObject5 = new JSONObject();
        searchObject5.put("Value", Constant.PLACES_API_RADIUS);
        searchObject5.put("Regex", false);
        distanceObject.put("Search", searchObject5);


        JSONObject truckTypeObject = new JSONObject();
        truckTypeObject.put("Name", "TruckType");
        truckTypeObject.put("Data", "");
        truckTypeObject.put("Orderable", false);
        truckTypeObject.put("Searchable", true);

        JSONObject searchObject6 = new JSONObject();
        searchObject6.put("Value", String.valueOf(AppSettings.with(getContext()).getTruckType()));
        searchObject6.put("Regex", false);
        truckTypeObject.put("Search", searchObject6);

        JSONArray columnsArray=  new JSONArray();
        columnsArray.put(loadTypeObject);
        columnsArray.put(truckClassObject);
        columnsArray.put(fromLatitudeObject);
        columnsArray.put(fromLongitudeObject);
        columnsArray.put(distanceObject);
        columnsArray.put(truckTypeObject);

        rootObject.put("Columns", columnsArray);

        String json = StringUtil.escapeString(rootObject.toString());
        AdSearchAsyncTask adSearchAsyncTask = new AdSearchAsyncTask();
        adSearchAsyncTask.execute(json);
    }

    public static void refreshView() {
        if(_instance!=null){
            try {
                _instance.searchAds();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class AdSearchAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.postBody(Constant.AD_SEARCH_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray jsonArray = (JSONArray) jsonObject.get("Data");
                        advertisements = new ArrayList<Advertisement>();

                        for (int j = 0; j < jsonArray.length(); j++) {
                            Advertisement advertisement = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Advertisement.class);
                            advertisements.add(advertisement);
                        }

                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if(mMap!=null)
                                    mMap.clear();
                                onMapReady(mMap);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(final String errorString) {
                }
            });
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng currentLatLng = new LatLng(AppSettings.with(getContext()).getCurrentLat(), AppSettings.with(getContext()).getCurrentLng());


        mMap = googleMap;
        if(mMap==null) return;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        if(advertisements.size() == 0) {
            try {
                searchAds();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        double maxCircleRad = 0.0f;

        try {
            LatLng firstLatLng = new LatLng(Double.valueOf(advertisements.get(0).getPickupLocation().getLatitude()), Double.valueOf(advertisements.get(0).getPickupLocation().getLongitude()));
            LatLng prevLatLng = firstLatLng;
            List<LatLng> locations = new ArrayList<>();

            for (Advertisement advertisement : advertisements) {
                LatLng loadLocation = new LatLng(Double.valueOf(advertisement.getPickupLocation().getLatitude()), Double.valueOf(advertisement.getPickupLocation().getLongitude()));

                double circleRad = getCircleRad(prevLatLng,loadLocation);
                prevLatLng = loadLocation;

                if (circleRad >= maxCircleRad) {
                    maxCircleRad = circleRad;
                }

                locations.add(loadLocation);
//                ZOOM = Misc.getZoomLevel(maxCircleRad);
            }

//            HashMap<LatLng, Integer> maps =  getPackets(locations);
            final HashMap<LatLng, ArrayList<Advertisement>> maps =  getPackets(advertisements);

            for(Object key: maps.keySet()){
                Bitmap bmp = makeBitmap(getContext(), String.valueOf(maps.get(key).size()));
                LatLng latLng = (LatLng) key;
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.advertisement_location)).draggable(true).flat(true).
                        icon(BitmapDescriptorFactory.fromBitmap(bmp)));
            }

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    final HashMap<LatLng, ArrayList<Advertisement>> maps =  getPackets(advertisements);
                    LatLng latLng =  marker.getPosition();
                    ArrayList<Advertisement> advertisements = maps.get(latLng);

                    FragmentMenuHome fragmentMenuHome = (FragmentMenuHome)getParentFragment();

                    fragmentMenuHome.tab2.advertisements = advertisements;

                    fragmentMenuHome.pager.setCurrentItem(1);
                    fragmentMenuHome.tab2.adAdapter.setAdvertisements(advertisements);
                    fragmentMenuHome.tab2.adAdapter.notifyDataSetChanged();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  HashMap<LatLng, ArrayList<Advertisement>> getPackets(List<Advertisement> advertisements) {
        HashMap<LatLng, ArrayList<Advertisement>> hashMap = new HashMap<>();
        for (Advertisement advertisement : advertisements) {
            LatLng latLng = new LatLng(Double.valueOf(advertisement.getPickupLocation().getLatitude()), Double.valueOf(advertisement.getPickupLocation().getLongitude()));
            if (hashMap.get(latLng) == null) {
                ArrayList<Advertisement> newList = new ArrayList<>();
                newList.add(advertisement);
                hashMap.put(latLng, newList);
            } else {
                ArrayList<Advertisement> newList = hashMap.get(latLng);
                newList.add(advertisement);
                hashMap.put(latLng, newList);
            }
        }
        return hashMap;
    }

    public Bitmap makeBitmap(Context context, String text)
    {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.marker_truck);
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED); // Text color
        paint.setTextSize(14 * scale); // Text size
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // Text shadow
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int x = bitmap.getWidth() - bounds.width()/2 - bitmap.getWidth()/2; // 10 for padding from right
        int y = bitmap.getHeight()/2 + bounds.height()/2;

        canvas.drawText(text, x, y, paint);

        return  bitmap;
    }

    private double getCircleRad(LatLng latLng1, LatLng latLng2) {
        Location fromLocation =  new Location("");
        Location toLocation =  new Location("");
        fromLocation.setLatitude(latLng1.latitude);
        toLocation.setLatitude(latLng2.latitude);
        fromLocation.setLongitude(latLng1.longitude);
        toLocation.setLongitude(latLng2.longitude);
        return fromLocation.distanceTo(toLocation);
    }
}
