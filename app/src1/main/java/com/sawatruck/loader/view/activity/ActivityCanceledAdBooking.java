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
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Notice;
import com.sawatruck.loader.utils.Serializer;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.design.CustomTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/20/2017.
 */

public class ActivityCanceledAdBooking extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.txt_truck_type1) CustomTextView txtTruckType1;
    @Bind(R.id.txt_truck_type2) CustomTextView txtTruckType2;
    @Bind(R.id.txt_distance) CustomTextView txtDistance;
    @Bind(R.id.txt_budget) CustomTextView txtBudget;
    @Bind(R.id.txt_available_days) CustomTextView txtAvailableDays;
    @Bind(R.id.btn_showmap) View btnShowMap;

    private Advertisement advertisement;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_canceled_booking, null);
        ButterKnife.bind(this, view);

        initAdBookingView();
        btnShowMap.setOnClickListener(this);
        return view;
    }

    private void initAdBookingView() {

        HttpUtil httpUtil = HttpUtil.getInstance();
        User user = UserManager.with(this).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        httpUtil.get(Constant.GET_AD_BY_ID_API + "/" + getIntent().getStringExtra(Constant.INTENT_ADVERTISEMENT_ID), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);
                advertisement = BaseApplication.getGson().fromJson(paramString, Advertisement.class);

                txtAvailableDays.setText(advertisement.getAvailable());
                txtTruckType1.setText(advertisement.getTruckTypeName1());
                txtTruckType2.setText(advertisement.getTruckTypeName2());
                txtBudget.setText(advertisement.getBudget());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
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
        setAppTitle(getString(R.string.title_cancel_boooking));
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
                break;
            case R.id.btn_showmap:
                onShowMap();
                break;
        }
    }

    private void onShowMap(){
        Intent intent = new Intent(this, ActivityMap.class);
        try {
            if(advertisement == null) {
                Notice.show(R.string.error_loading_booking_details);
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
