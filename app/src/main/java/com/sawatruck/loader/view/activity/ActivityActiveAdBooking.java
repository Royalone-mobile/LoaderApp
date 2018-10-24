package com.sawatruck.loader.view.activity;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Advertisement;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Notice;
import com.sawatruck.loader.utils.Serializer;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.design.CustomTextView;
import com.sawatruck.loader.view.fragments.FragmentMenuBooking;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/20/2017.
 */

public class ActivityActiveAdBooking extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.txt_truck_type1) CustomTextView txtTruckType1;
    @Bind(R.id.txt_truck_type2) CustomTextView txtTruckType2;
    @Bind(R.id.txt_distance) CustomTextView txtDistance;
    @Bind(R.id.txt_budget) CustomTextView txtBudget;
    @Bind(R.id.txt_available_days) CustomTextView txtAvailableDays;
    @Bind(R.id.btn_showmap) View btnShowMap;
    @Bind(R.id.btn_cancel) View btnCancel;

    private Advertisement advertisement;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_active_booking, null);
        ButterKnife.bind(this, view);

        initAdBookingView();
        btnCancel.setOnClickListener(this);
        btnShowMap.setOnClickListener(this);
        return view;
    }

    private void initAdBookingView() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user = UserManager.with(this).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        try {
            httpUtil.get(Constant.GET_AD_BY_ID_API + "/" + getIntent().getStringExtra(Constant.INTENT_ADVERTISEMENT_ID), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String paramString = new String(responseBody);
                        paramString = StringUtil.escapeString(paramString);
                        advertisement = BaseApplication.getGson().fromJson(paramString, Advertisement.class);
                        txtAvailableDays.setText(advertisement.getAvailable());
                        txtTruckType1.setText(advertisement.getTruckTypeName1());
                        txtTruckType2.setText(advertisement.getTruckTypeName2());
                        txtBudget.setText(advertisement.getBudget());
                        txtDistance.setText(advertisement.getDistance().concat(getString(R.string.kilometer)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Misc.showResponseMessage(responseBody);
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
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
        setAppTitle(getString(R.string.title_active_boooking));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_cancel:
                cancelBooking();
                break;
            case R.id.btn_showmap:
                onShowMap();
                break;
        }
    }

    private void cancelBooking() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        try {
            httpUtil.put(Constant.CANCEL_ADBOOKING_API + "/" + getIntent().getStringExtra(Constant.INTENT_AD_BOOKING_ID), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, final byte[] responseBody) {
                    try {
                        FragmentMenuBooking.chooseTab(2);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, final byte[] errorString, Throwable throwable) {
                    Misc.showResponseMessage(errorString);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void onShowMap(){
        Intent intent = new Intent(this, ActivityMap.class);

        try {
            if(advertisement == null) {
                Notice.show( R.string.error_loading_booking_details);
                return;
            }

            String fromLocation = Serializer.getInstance().serializeLocation(advertisement.getPickupLocation());
            String toLocation = Serializer.getInstance().serializeLocation(advertisement.getDeliveryLocation());
            intent.putExtra(Constant.INTENT_FROM_LOCATION, fromLocation);
            intent.putExtra(Constant.INTENT_TO_LOCATION, toLocation);
            startActivity(intent);
        }catch (Exception e ){
            e.printStackTrace();
        }
    }
}
