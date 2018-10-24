package com.sawatruck.loader.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fuzzproductions.ratingbar.RatingBar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Advertisement;
import com.sawatruck.loader.entities.CancelReason;
import com.sawatruck.loader.entities.Driver;
import com.sawatruck.loader.entities.Load;
import com.sawatruck.loader.entities.NewPoint;
import com.sawatruck.loader.entities.OfferDetail;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.GoogleMapPath;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Notice;
import com.sawatruck.loader.utils.Serializer;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.design.CircularImage;
import com.sawatruck.loader.view.design.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/20/2017.
 */

public class ActivityBookedDetails extends BaseActivity implements View.OnClickListener, OnMapReadyCallback {
    @Bind(R.id.btn_view_delivery_details) View btnDeliveryDetails;
    @Bind(R.id.btn_cancel) CustomTextView btnCancel;
    @Bind(R.id.btn_call) CustomTextView btnCall;
    @Bind(R.id.btn_message) CustomTextView btnMessage;
    @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
    @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
    @Bind(R.id.img_driver_photo) CircularImage imgDriverPhoto;
    @Bind(R.id.txt_truck_type) CustomTextView txtTruckType;
    @Bind(R.id.rating_driver) RatingBar ratingDriver;
    @Bind(R.id.txt_driver_name) CustomTextView txtDriverName;
    @Bind(R.id.toolbar_share_travel) View btnShareTravel;
    @Bind(R.id.toolbar_show_hide_card) ImageView btnShowHideCard;
    @Bind(R.id.layout_card) CardView layoutCardView;

    private Marker prevMarker;
    private Load load;
    private Advertisement advertisement;

    public int ZOOM = 15;
    private ArrayList<CancelReason> cancelReasons = new ArrayList();
    private GoogleMap _googleMap;
    private SupportMapFragment supportMapFragment;

    private int loadOrBooking;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_booking_detail, null);
        ButterKnife.bind(this, view);
        btnCall.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnMessage.setOnClickListener(this);
        btnDeliveryDetails.setOnClickListener(this);
        btnShareTravel.setOnClickListener(this);
        btnShowHideCard.setOnClickListener(this);

        getCancelReasons();

        loadOrBooking = getIntent().getIntExtra(Constant.INTENT_TRAVEL_TYPE, 0);
        if (loadOrBooking == 1) {
            String loadId = getIntent().getStringExtra(Constant.INTENT_LOAD_ID);
            fetchLoad(loadId);
        }
        else if (loadOrBooking == 2) {
            initAdBookingView();
        }
        else if (loadOrBooking == 3)
            initOfferViewFCM();


        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        return view;
    }

    private void initOfferViewFCM(){
        final String offerID = getIntent().getStringExtra(Constant.INTENT_OFFER_ID);
        RequestParams params = new RequestParams();
        params.put(Constant.PARAM_ID, offerID);
        HttpUtil.getInstance().get(Constant.GET_OFFER_BY_ID_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                try {
                    Logger.error("initOfferViewFCM");
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    OfferDetail offerDetail = BaseApplication.getGson().fromJson(paramString, OfferDetail.class);
                    loadOrBooking = 1;
                    load = offerDetail.getLoad();
                    fetchLoad(load.getLoadID());
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    private final BroadcastReceiver onNewPointReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String strData = intent.getStringExtra("data");
                NewPoint newPoint = BaseApplication.getGson().fromJson(strData, NewPoint.class);

                int loadOrBooking = getIntent().getIntExtra(Constant.INTENT_TRAVEL_TYPE, 0);

                if (loadOrBooking == 1) {
                    if (load.getStatus() < 3) {
                        btnCancel.setVisibility(View.VISIBLE);
                        btnCall.setVisibility(View.VISIBLE);
                    }
                    else {
                        btnCall.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.GONE);
                    }
                    if (load.getDriver() == null) return;
                    if (!load.getTravelID().equals(newPoint.getTrackingSessionID())) return;
                } else if (loadOrBooking == 2) {
                    if(advertisement.getStatus()==0 || advertisement.getStatus() ==3)
                        btnCancel.setVisibility(View.GONE);
                    if (!advertisement.getTravelID().equals(newPoint.getTrackingSessionID()))
                        return;
                }

                if(points.size() !=0) {
                    points.add(new LatLng(newPoint.getLat(),newPoint.getLong()));
                    PolylineOptions lineOptions = new PolylineOptions();

                    lineOptions.addAll(points);
                    lineOptions.width(8);
                    lineOptions.color(Color.parseColor("#FB8E02"));

                    _googleMap.addPolyline(lineOptions);
                }

                LatLng currentDriverLatLng = new LatLng(newPoint.getLat(), newPoint.getLong());

//                if(prevMarker != null)
//                    prevMarker.remove();
//                prevMarker = _googleMap.addMarker(new MarkerOptions().position(currentDriverLatLng).title(getString(R.string.driver_location)).draggable(true).flat(true).
//                        icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_truck_tracking)));

                Location currentDriverLocation =  new Location("location");
                Location destLocation =  new Location("location");

                currentDriverLocation.setLatitude(newPoint.getLat());
                currentDriverLocation.setLongitude(newPoint.getLong());

                if(prevMarker==null) {
                    prevMarker = _googleMap.addMarker(new MarkerOptions().position(currentDriverLatLng).title(getString(R.string.driver_location)).draggable(true).flat(true).
                            icon(BitmapDescriptorFactory.fromBitmap(Misc.getMarkerBitmap(R.drawable.ico_truck_tracking))));
                    _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentDriverLatLng, ZOOM));
                }
                else {
                    animateMarkerNew(currentDriverLocation, prevMarker);
                    _googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentDriverLatLng));
                }


                // TODO Should show on the way to pickup location realtime
                if( newPoint.getStatus() == 2){
                    if(loadOrBooking == 2) {
                        destLocation.setLatitude(Double.valueOf(advertisement.getPickupLocation().getLatitude()));
                        destLocation.setLongitude(Double.valueOf(advertisement.getPickupLocation().getLongitude()));
                    }
                    else {
                        destLocation.setLatitude(Double.valueOf(load.getFromLocation().getLatitude()));
                        destLocation.setLongitude(Double.valueOf(load.getFromLocation().getLongitude()));
                    }
                } //TODO Should show on the way to delivery location realtime
                else {
                    if(loadOrBooking == 2) {
                        destLocation.setLatitude(Double.valueOf(advertisement.getDeliveryLocation().getLatitude()));
                        destLocation.setLongitude(Double.valueOf(advertisement.getDeliveryLocation().getLongitude()));
                    }
                    else {
                        destLocation.setLatitude(Double.valueOf(load.getToLocation().getLatitude()));
                        destLocation.setLongitude(Double.valueOf(load.getToLocation().getLongitude()));
                    }
                }

//                double circleRad = currentDriverLocation.distanceTo(destLocation)/2*1000; //multiply by 1000 to make units in KM
//                ZOOM = Misc.getZoomLevel(circleRad) + 8;
//
//                _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentDriverLatLng, ZOOM));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    private void initAdBookingView() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user = UserManager.with(this).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        Logger.error("----------initAdBookingView----------------" +  getIntent().getStringExtra(Constant.INTENT_AD_BOOKING_ID));

        httpUtil.get(Constant.GET_AD_BY_ID_API + "/" + getIntent().getStringExtra(Constant.INTENT_AD_BOOKING_ID), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    Logger.error("--------------------Advertisement -------------------------------");
                    Logger.error(paramString);
                    advertisement = BaseApplication.getGson().fromJson(paramString, Advertisement.class);
                    txtLoadLocation.setText(advertisement.getPickupLocation().getAddressDetails().concat(advertisement.getPickupLocation().getCityName()).concat(advertisement.getPickupLocation().getCountryName()));
                    txtDeliveryLocation.setText(advertisement.getDeliveryLocation().getAddressDetails().concat(advertisement.getDeliveryLocation().getCityName()).concat(advertisement.getDeliveryLocation().getCountryName()));
                    txtTruckType.setText(advertisement.getTruckTypeName1().concat(",").concat(advertisement.getTruckTypeName2()));
                    txtDriverName.setText(advertisement.getDriverName());
                    float rating = Float.valueOf(advertisement.getDriverRating()) / 20.0f;
                    ratingDriver.setRating(rating);
                    if (advertisement.getStatus() == 2)
                        btnCall.setVisibility(View.VISIBLE);
                    else
                        btnCall.setVisibility(View.GONE);
                    BaseApplication.getPicasso().load(advertisement.getDriverImageUrl()).placeholder(R.drawable.ico_truck).into(imgDriverPhoto);

                    Logger.error("------------advertisement.getTrackingStatus()--------------" + advertisement.getTrackingStatus());

                    drawLocations();

                    BaseApplication.getInstance().openTrackingHub(advertisement.getTravelNumber());

                    btnDeliveryDetails.setVisibility(View.VISIBLE);


                    if(advertisement.getTravelStatus() != 1)
                        btnCancel.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Misc.showResponseMessage( responseBody);
            }
        });

    }


    public void fetchLoad(String loadId) {
        RequestParams params = new RequestParams();
        params.put("id", loadId);
        HttpUtil.getInstance().get(Constant.GET_LOADS_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);
                Logger.error("Load Details");
                Logger.error(paramString);
                try {
                    JSONObject jsonObject = new JSONObject(paramString);
                    JSONObject contentObject = jsonObject.getJSONObject("Object");
                    load = BaseApplication.getGson().fromJson(contentObject.toString(), Load.class);



                    txtLoadLocation.setText(load.getFromLocation().getCityName().concat(", ").concat(load.getFromLocation().getCountryName()));
                    txtDeliveryLocation.setText(load.getToLocation().getCityName() + ", " + load.getToLocation().getCountryName());


                    if (load.getStatus() < 3) {

                        btnCall.setVisibility(View.VISIBLE);
                    }
                    else {
                        btnCall.setVisibility(View.GONE);
                    }

                    if(load.getTravelStatus() != 1)
                        btnCancel.setVisibility(View.GONE);

                    if (load.getDriver() != null) {
                        Driver driver = load.getDriver();
                        try {
                            BaseApplication.getPicasso().load(driver.getImgUrl()).placeholder(R.drawable.ico_truck).into(imgDriverPhoto);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        txtDriverName.setText(driver.getFullName());
                        float rating = Float.valueOf(driver.getRate()) / 20.0f;
                        ratingDriver.setRating(rating);
                    }

                    BaseApplication.getInstance().openTrackingHub(load.getTravelNumber());

                    drawLocations();
                    btnDeliveryDetails.setVisibility(View.VISIBLE);


                    Logger.error(load.getTruckTypeName1().concat(",").concat(load.getTruckTypeName2()));

                    txtTruckType.setText(load.getTruckTypeName1().concat(",").concat(load.getTruckTypeName2()));


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
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
    public void onResume() {
        super.onResume();
        showNavHome(false);
        setAppTitle(getString(R.string.arriving));

        registerReceiver(onNewPointReceiver, new IntentFilter("onNewPoint"));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        BaseApplication.getInstance().closeTrackingHub();
        unregisterReceiver(onNewPointReceiver);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        switch (id) {
            case R.id.btn_view_delivery_details:
                intent = new Intent(this, ActivityDeliveryDetails.class);
                if (loadOrBooking == 1)
                    intent.putExtra(Constant.INTENT_LOAD_ID, load.getID());
//                    intent.putExtra("load", Serializer.getInstance().serializeLoad(load));
                else if (loadOrBooking == 2)
                    intent.putExtra(Constant.INTENT_AD_BOOKING_ID, advertisement.getID());
//                    intent.putExtra("advertisement", Serializer.getInstance().serializeAdvertisement(advertisement));
                intent.putExtra(Constant.INTENT_TRAVEL_TYPE, loadOrBooking);
                startActivity(intent);

                break;
            case R.id.toolbar_share_travel:
                shareTravel();
                break;
            case R.id.btn_call:
                //TODO Call driver from activity offer
                try {
                    if (loadOrBooking == 1) {
                        if (load.getDriver() == null) {
                            Notice.show(R.string.no_driver_phone);
                            return;
                        }
                        if(load.getDriver().getPhoneNumber() == null || load.getDriver().getPhoneNumber().length() == 0 ) {
                            Notice.show(R.string.no_driver_phone);
                            return;
                        }
                        callPerson(load.getDriver().getPhoneNumber());
                    } else if (loadOrBooking == 2) {
                        if(advertisement==null || advertisement.getDriverPhoneNumber().length()==0) {
                            Notice.show(R.string.no_driver_phone);
                            return;
                        }
                        callPerson(advertisement.getDriverPhoneNumber());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_message:
                intent = new Intent(this, ActivityMessage.class);

                if (loadOrBooking == 1) {
                        intent.putExtra(Constant.INTENT_USERNAME, load.getDriver().getFullName());
                        intent.putExtra(Constant.INTENT_USER_ID, load.getDriver().getID());
                } else if (loadOrBooking == 2) {
                    try {
                        intent.putExtra(Constant.INTENT_USER_ID, advertisement.getDriverId());
                        intent.putExtra(Constant.INTENT_USERNAME, advertisement.getDriverName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                startActivity(intent);
                break;
            case R.id.btn_cancel:
                alertCancelDelivery();
                break;

            case R.id.toolbar_show_hide_card:
                if(layoutCardView.getVisibility() == View.GONE) {
                    layoutCardView.setVisibility(View.VISIBLE);
                    BaseApplication.getPicasso().load(R.drawable.btn_close).into(btnShowHideCard);
                }
                else {
                    layoutCardView.setVisibility(View.GONE);
                    BaseApplication.getPicasso().load(R.drawable.btn_open).into(btnShowHideCard);
                }
                break;
        }
    }

    private void shareTravel() {
        try {
            Logger.error("share url");
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            if (loadOrBooking == 1)
                intent.putExtra(Intent.EXTRA_TEXT, load.getShareText());
            else
                intent.putExtra(Intent.EXTRA_TEXT, advertisement.getShareText());
            startActivity(Intent.createChooser(intent, getString(R.string.share_travel)));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callPerson(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        _googleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        drawLocations();
    }


    private void drawLocations() {
        LatLng loadLatLng = null, deliveryLatLng = null;
        try {
            Location currentLocation = new Location("location");
            Location loadLocation = new Location("location");
            Location deliveryLocation = new Location("location");

            if (loadOrBooking == 1) {
                loadLocation.setLatitude(Double.valueOf(load.getFromLocation().getLatitude()));
                loadLocation.setLongitude(Double.valueOf(load.getFromLocation().getLongitude()));
                deliveryLocation.setLatitude(Double.valueOf(load.getToLocation().getLatitude()));
                deliveryLocation.setLongitude(Double.valueOf(load.getToLocation().getLongitude()));

                /// Shows Tracking lines from driver location
                getTracking(String.valueOf(load.getTravelNumber()));

            } else if (loadOrBooking == 2) {
                loadLocation.setLatitude(Double.valueOf(advertisement.getPickupLocation().getLatitude()));
                loadLocation.setLongitude(Double.valueOf(advertisement.getPickupLocation().getLongitude()));
                deliveryLocation.setLatitude(Double.valueOf(advertisement.getDeliveryLocation().getLatitude()));
                deliveryLocation.setLongitude(Double.valueOf(advertisement.getDeliveryLocation().getLongitude()));

                /// Shows Tracking lines from driver location

                getTracking(String.valueOf(advertisement.getTravelNumber()));
            }

//            double circleRad = currentLocation.distanceTo(loadLocation) / 2 * 1000;//multiply by 1000 to make units in KM
//            ZOOM = Misc.getZoomLevel(circleRad) + 8;

            loadLatLng = new LatLng(loadLocation.getLatitude(), loadLocation.getLongitude());
            deliveryLatLng = new LatLng(deliveryLocation.getLatitude(), deliveryLocation.getLongitude());

            _googleMap.addMarker(new MarkerOptions().position(loadLatLng).title(getString(R.string.load_location)).draggable(true).flat(true).
                    icon(BitmapDescriptorFactory.fromBitmap(Misc.getMarkerBitmap(R.drawable.pickup_marker))));

            _googleMap.addMarker(new MarkerOptions().position(deliveryLatLng).title(getString(R.string.delivery_location)).draggable(true).flat(true).
                    icon(BitmapDescriptorFactory.fromBitmap(Misc.getMarkerBitmap(R.drawable.delivery_marker))));


            _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(deliveryLatLng, ZOOM));


//            GoogleMapPath googleMapPath = new GoogleMapPath(_googleMap, loadLatLng, deliveryLatLng);
//            googleMapPath.drawPath();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<LatLng> points  = new ArrayList<>();

    public void getTracking(String trackingNumber){
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user = UserManager.with(context).getCurrentUser();



        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        httpUtil.get(Constant.GET_TRACKING_API + trackingNumber, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    Logger.error("-------------------------------------GET TRACKING-------------------------------------");
//                    Logger.error(paramString);
                    JSONArray jsonArray = new JSONArray(paramString);


                    PolylineOptions lineOptions =  new PolylineOptions();

                    for (int j = 0; j < jsonArray.length(); j++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(j);

                        double lat = jsonObject.getDouble("Lat");
                        double lng = jsonObject.getDouble("Long");

                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }

                    lineOptions.addAll(points);
                    lineOptions.width(8);
                    lineOptions.color(Color.parseColor("#FB8E02"));


//                    Logger.error(points.toString());
                    if (lineOptions != null) {
                        _googleMap.addPolyline(lineOptions);
                    } else {
                        Log.d("onPostExecute", "without Polylines drawn");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage( responseBody);
            }
        });
    }
    public void getCancelReasons() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        httpUtil.get(Constant.CANCEL_REASON_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    JSONArray jsonArray = new JSONArray(paramString);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        CancelReason cancelReason = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), CancelReason.class);
                        cancelReasons.add(cancelReason);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Misc.showResponseMessage( responseBody);
            }
        });
    }

    public void alertCancelDelivery() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            final View viewCancelBooking = getLayoutInflater().inflate(R.layout.dialog_cancel_delivery, null);

            final LinearLayout layoutCheckbox = (LinearLayout) viewCancelBooking.findViewById(R.id.layout_checkbox);

            for (CancelReason cancelReason : cancelReasons) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(cancelReason.getName());
                checkBox.setTag(cancelReason.getID());
                layoutCheckbox.addView(checkBox);
            }
            layoutCheckbox.getChildAt(0).performClick();

            final Button btnOk = (Button) viewCancelBooking.findViewById(R.id.btn_ok);
            final Button btnCancel = (Button) viewCancelBooking.findViewById(R.id.btn_cancel);

            builder.setView(viewCancelBooking);

            final AlertDialog alertDialog = builder.create();

            alertDialog.show();
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int childCount = layoutCheckbox.getChildCount();
                    ArrayList<String> reasonIds = new ArrayList<String>();

                    for(int i =0; i<childCount; i++) {
                        CheckBox checkBox = (CheckBox)layoutCheckbox.getChildAt(i);
                        if(checkBox.isChecked()){
                            int reasonId = (int)checkBox.getTag();
                            Logger.error("-----------------Reason ID -------------------");
                            Logger.error(String.valueOf(reasonId));
                            reasonIds.add(String.valueOf(reasonId));
                        }
                    }
                    if(reasonIds.size() == 0) {
                        Notice.show(getString(R.string.choose_one_reason));
                        return;
                    }
                    JSONObject jsonObject = new JSONObject();
                    String json = "{'ReasonsIds':" + reasonIds + "}";

                    try {
                        jsonObject.put("ReasonsIds", reasonIds);
                        btnCancel.setEnabled(false);
                        Logger.error("request Body = " + json);
                        CancelBookingAsyncTask cancelBookingAsyncTask = new CancelBookingAsyncTask();
                        if (loadOrBooking == 1) {
                            cancelBookingAsyncTask.execute(load.getTravel().getTravelID(), json);
                        } else if (loadOrBooking == 2) {
                            cancelBookingAsyncTask.execute(advertisement.getID(), json);
                        }
                        alertDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class CancelBookingAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String travelID = params[0];
            String json = params[1];

            HttpUtil.putBody(Constant.CANCEL_DELIVERY_API + "/" + travelID, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Misc.showResponseMessage( responseBody);
                                ActivityBookedDetails.this.finish();
                                if (loadOrBooking == 1)
                                    MainActivity.selectLoadTab();
                                else
                                    MainActivity.selectAdTab();
                            }
                        });
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(final String errorString) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btnCancel.setEnabled(true);
                                Logger.error(errorString);
                                Notice.show(errorString);
                            }
                        });
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
    }


    private void animateMarkerNew(final Location destination, final Marker marker) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);

                        marker.setRotation(getBearing(startPosition, new LatLng(destination.getLatitude(), destination.getLongitude())));
                    } catch (Exception ex) {
                        //I don't care atm..
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    // if (mMarker != null) {
                    // mMarker.remove();
                    // }
                    // mMarker = googleMap.addMarker(new MarkerOptions().position(endPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));

                }
            });
            valueAnimator.start();
        }
    }

    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

    //Method for finding bearing between two points
    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
}
