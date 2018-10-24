package com.sawatruck.loader.view.activity;

import android.location.Location;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.SawaTruckLocation;
import com.sawatruck.loader.utils.GoogleMapPath;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Serializer;

import butterknife.ButterKnife;

/**
 * Created by royal on 8/31/2017.
 */

public class ActivityMap extends BaseActivity implements OnMapReadyCallback{
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    public  int ZOOM = 10;
    SawaTruckLocation fromLocation, toLocation;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_map,null);
        ButterKnife.bind(this, view);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        String strFromLocation = getIntent().getStringExtra(Constant.INTENT_FROM_LOCATION);
        String strToLocation = getIntent().getStringExtra(Constant.INTENT_TO_LOCATION);
        fromLocation = Serializer.getInstance().deserializeLocation(strFromLocation);
        toLocation = Serializer.getInstance().deserializeLocation(strToLocation);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        setAppTitle(getResources().getString(R.string.title_map));
        showNavHome(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        try {

            Location location1 =  new Location("location");
            Location location2 =  new Location("location");
            location1.setLatitude(Double.valueOf(fromLocation.getLatitude()));
            location1.setLongitude(Double.valueOf(fromLocation.getLongitude()));
            location2.setLatitude(Double.valueOf(toLocation.getLatitude()));
            location2.setLongitude(Double.valueOf(toLocation.getLongitude()));

            Logger.error(String.valueOf(location1.distanceTo(location2)));
            double circleRad = location1.distanceTo(location2)/2*1000;//multiply by 1000 to make units in KM
            Logger.error(String.valueOf(circleRad));
            ZOOM = Misc.getZoomLevel(circleRad) + 10;
            Logger.error("Zoom:" + String.valueOf(ZOOM));

            LatLng loadLocation = new LatLng(Double.valueOf(fromLocation.getLatitude()), Double.valueOf(fromLocation.getLongitude()));
            LatLng deliveryLocation = new LatLng(Double.valueOf(toLocation.getLatitude()) , Double.valueOf(toLocation.getLongitude()));

            googleMap.addMarker(new MarkerOptions().position(loadLocation).title("Load SawaTruckLocation").draggable(true).flat(true).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            googleMap.addMarker(new MarkerOptions().position(deliveryLocation).title("Delivery SawaTruckLocation").draggable(true).flat(true).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loadLocation, ZOOM));


            GoogleMapPath googleMapPath = new GoogleMapPath(mMap, loadLocation, deliveryLocation);

            googleMapPath.drawPath();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
