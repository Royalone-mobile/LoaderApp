package com.sawatruck.loader.view.activity;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;

import com.fuzzproductions.ratingbar.RatingBar;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.design.CustomEditText;
import com.sawatruck.loader.view.design.CustomTextView;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/23/2017.
 */


public class ActivityRating extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.rating_rank) RatingBar rateTravelRank;
    @Bind(R.id.rating_communication) RatingBar rateCommunication;
    @Bind(R.id.rating_service) RatingBar rateService;
    @Bind(R.id.rating_care_of_goods) RatingBar rateCareGoods;
    @Bind(R.id.rating_punctuality) RatingBar ratePunctuality;
    @Bind(R.id.edit_comment) CustomEditText editComment;
    @Bind(R.id.btn_submit) View btnSubmit;
    @Bind(R.id.txt_pickup_location) CustomTextView txtPickupLocation;
    @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
    @Bind(R.id.txt_current_time) CustomTextView txtCurrentTime;

    private String travelID = "" ;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_rating,null);
        ButterKnife.bind(this, view);

        BaseApplication.getHandler().post(timerRunnable);

        travelID = getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID);
        initView();
        btnSubmit.setOnClickListener(this);
        return view;
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            txtCurrentTime.setText(Misc.getUTCDatetimeAsStringWithPM());
            BaseApplication.getHandler().postDelayed(this,1000);
        }
    };

    private void initView(){
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(context).getCurrentUser().getToken());

        httpUtil.get(Constant.GET_TRAVEL_BY_ID +"/" + getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    JSONObject responseObject= new JSONObject(paramString);

                    JSONObject locationObject = responseObject.getJSONObject("ToLocation");
                    String cityName  = locationObject.getString("CityName");
                    String countryName  = locationObject.getString("Name");

                    txtDeliveryLocation.setText(cityName.concat(",").concat(countryName));

                    locationObject = responseObject.getJSONObject("FromLocation");
                    cityName  = locationObject.getString("CityName");
                    countryName  = locationObject.getString("Name");
                    txtPickupLocation.setText(cityName.concat(",").concat(countryName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] responseBody, Throwable throwable) {
                Misc.showResponseMessage(responseBody);
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getString(R.string.title_rating));
        showNavHome(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_submit:
                rateTravel();
                break;
        }
    }

    private class RateAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.postBody(Constant.RATE_TRAVEL_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Misc.showResponseMessage( responseBody);
                                ActivityRating.this.finish();
                                btnSubmit.setEnabled(true);
                            }
                        });
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(final String errorString) {
                    try {
                        btnSubmit.setEnabled(true);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Misc.showResponseMessage( errorString);
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

    public void rateTravel(){
        JSONObject jsonObject = new JSONObject();
        String strComment = editComment.getText().toString();
        if(strComment.length() == 0) {
            editComment.setError(getResources().getString(R.string.error_enter_comment));
            editComment.requestFocus();
            return;
        }

        // TODO Travel Id
        float ratingRank = rateTravelRank.getRating();
        float ratingService = rateService.getRating();
        float ratingPunctuality = ratePunctuality.getRating();
        float ratingCommunication = rateCommunication.getRating();
        float ratingCareGoods = rateCareGoods.getRating();

        try {
            jsonObject.put("Rank", ratingRank*20);
            jsonObject.put("TravelID", travelID);
            jsonObject.put("Services_as_Described", ratingService*20);
            jsonObject.put("Punctuality", ratingPunctuality*20);
            jsonObject.put("Care_of_Goods", ratingCareGoods*20);
            jsonObject.put("Communication", ratingCommunication*20);

            jsonObject.put("Message", editComment.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        btnSubmit.setEnabled(false);
        RateAsyncTask rateAsyncTask = new RateAsyncTask();
        rateAsyncTask.execute(jsonObject.toString());
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
}

