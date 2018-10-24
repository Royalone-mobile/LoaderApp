package com.sawatruck.loader.view.activity;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Advertisement;
import com.sawatruck.loader.entities.Load;
import com.sawatruck.loader.entities.LoadPhoto;
import com.sawatruck.loader.entities.TravelDetails;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.design.CustomTextView;
import com.sawatruck.loader.view.design.TimelineView.TimelineRow;
import com.sawatruck.loader.view.design.TimelineView.TimelineViewAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/17/2017.
 */

public class ActivityDeliveryDetails extends BaseActivity {
    @Bind(R.id.img_load_photo) ImageView imgLoadPhoto;
    @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;
    @Bind(R.id.txt_pickup_date) CustomTextView txtPickupDate;
    @Bind(R.id.txt_distance) CustomTextView txtDistance;
    @Bind(R.id.txt_load_details) CustomTextView txtLoadDetails;
    @Bind(R.id.timeline_listView) ListView timeLineView;
    @Bind(R.id.txt_load_type) CustomTextView txtLoadType;

    private Load load;
    private Advertisement advertisement;
    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_deliverydetails,null);
        ButterKnife.bind(this, view);
        initTimeline();

        int loadOrBooking = getIntent().getIntExtra(Constant.INTENT_TRAVEL_TYPE,0);
        if(loadOrBooking == 1)
            initLoadView();
        else if(loadOrBooking == 2)
            initAdBookingView();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        setAppTitle(getString(R.string.title_delivery_details));
        showNavHome(false);
    }

    private void initAdBookingView() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user = UserManager.with(this).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        httpUtil.get(Constant.GET_AD_BY_ID_API + "/" + getIntent().getStringExtra(Constant.INTENT_AD_BOOKING_ID), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    Logger.error("--------------------Advertisement -------------------------------");
                    Logger.error(paramString);
                    advertisement = BaseApplication.getGson().fromJson(paramString, Advertisement.class);
                    txtDeliveryDate.setText(advertisement.getDeliveryDate());
                    txtPickupDate.setText(advertisement.getPickupDate());
                    txtLoadType.setText(advertisement.getTruckTypeName1().concat(",").concat(advertisement.getTruckTypeName2()));
                    txtLoadDetails.setText(advertisement.getDeliveryDescription());

                    txtDistance.setText(advertisement.getDistance().concat("km"));

                    ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();

                    if(advertisement.getTravelDetails()==null) return;

                    HashMap<String,Boolean> statusMap = new HashMap<String,Boolean>();

                    if(advertisement.getTravelDetails().size()>0) {
                        for(int j = 0; j<advertisement.getTravelDetails().size(); j++) {
                            TravelDetails travelDetails = advertisement.getTravelDetails().get(j);
                            if(statusMap.get(travelDetails.getStatus()) == null) {

                                TimelineRow myRow = new TimelineRow(j);


                                Date date = Misc.getDateFromString(travelDetails.getDate());

                                myRow.setDate(date);
                                myRow.setTitle(travelDetails.getStatus());


                                myRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.ico_loads));
                                myRow.setBellowLineColor(Color.argb(200, 0, 0, 0));
                                myRow.setBellowLineSize(2);
                                myRow.setImageSize(30);
                                myRow.setBackgroundColor(Color.argb(255, 255, 255, 0));
                                myRow.setBackgroundSize(30);
                                timelineRowsList.add(myRow);
                                statusMap.put(travelDetails.getStatus(), true);
                            }
                        }
                        ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(ActivityDeliveryDetails.this, 0, timelineRowsList, false);
                        timeLineView.setAdapter(myAdapter);
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

    public void initLoadView(){
        RequestParams params = new RequestParams();
        String loadId = getIntent().getStringExtra(Constant.INTENT_LOAD_ID);
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

                    if(load.getLoadPhotos().size()>0) {
                        LoadPhoto loadPhoto = load.getLoadPhotos().get(0);
                        try {
                            BaseApplication.getPicasso().load(loadPhoto.getPhotoPath()).placeholder(R.drawable.ico_truck).into(imgLoadPhoto);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    txtDeliveryDate.setText(load.getUnloadDateEnd());
                    txtPickupDate.setText(load.getLoadDateFrom());
                    txtLoadType.setText(load.getLoadType());
                    txtLoadDetails.setText(load.getLoadDetails());

                    txtDistance.setText(load.getDistance().concat(getString(R.string.kilometer)));
                    ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();


                    if (load.getTravelDetails().size() > 0) {
                        for (int j = 0; j < load.getTravelDetails().size(); j++) {
                            TravelDetails travelDetails = load.getTravelDetails().get(j);
                            TimelineRow myRow = new TimelineRow(j);
                            myRow.setDate(Misc.getDateFromString(Misc.getTimeZoneDate(travelDetails.getDate())));
                            myRow.setTitle(travelDetails.getStatus());
                            myRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.ico_loads));
                            myRow.setBellowLineColor(Color.argb(200, 0, 0, 0));
                            myRow.setBellowLineSize(2);
                            myRow.setImageSize(30);
                            myRow.setBackgroundColor(Color.argb(255, 255, 255, 0));
                            myRow.setBackgroundSize(30);
                            timelineRowsList.add(myRow);
                        }
                        ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(ActivityDeliveryDetails.this, 0, timelineRowsList, false);
                        timeLineView.setAdapter(myAdapter);
                    }

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

    public void initTimeline(){
        ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();
        ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList, false);
        timeLineView.setAdapter(myAdapter);
    }
}
