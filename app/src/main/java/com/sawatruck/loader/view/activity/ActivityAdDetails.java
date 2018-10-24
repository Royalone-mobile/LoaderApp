package com.sawatruck.loader.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.fuzzproductions.ratingbar.RatingBar;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Advertisement;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.ActivityUtil;
import com.sawatruck.loader.utils.AppSettings;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Serializer;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.design.CircularImage;
import com.sawatruck.loader.view.design.CustomEditText;
import com.sawatruck.loader.view.design.CustomTextView;
import com.sawatruck.loader.view.fragments.FragmentMenuBooking;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/23/2017.
 */


public class ActivityAdDetails extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.btn_showmap) View btnShowMap;
    @Bind(R.id.txt_distance) CustomTextView txtDistance;
    @Bind(R.id.txt_available_days) CustomTextView txtAvailableDays;
    @Bind(R.id.txt_truck_type1) CustomTextView txtTruckType1;
    @Bind(R.id.txt_truck_type2) CustomTextView txtTruckType2;
    @Bind(R.id.txt_budget) CustomTextView txtBudget;
    @Bind(R.id.img_photo) CircularImage imgPhoto;
    @Bind(R.id.txt_driver_name) CustomTextView txtDriverName;
    @Bind(R.id.rating_bar) RatingBar ratingUser;
    @Bind(R.id.btn_book) View btnBook;
    @Bind(R.id.btn_message) View btnMessage;
    private Advertisement advertisement = new Advertisement();

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_ad_details,null);
        ButterKnife.bind(this, view);
        btnShowMap.setOnClickListener(this);
        btnBook.setOnClickListener(this);
        btnMessage.setOnClickListener(this);
        getAdvertisement();
        return view;
    }

    private void getAdvertisement() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(this).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        Logger.error("Advertisement id = " + getIntent().getStringExtra(Constant.INTENT_ADVERTISEMENT_ID) );

        httpUtil.get(Constant.GET_AD_BY_ID_API +"/" + getIntent().getStringExtra(Constant.INTENT_ADVERTISEMENT_ID), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    advertisement = BaseApplication.getGson().fromJson(paramString, Advertisement.class);
                    txtBudget.setText(advertisement.getCurrency().concat(" ").concat(advertisement.getBudget()));
                    txtTruckType1.setText(advertisement.getTruckTypeName1());
                    txtTruckType2.setText(advertisement.getTruckTypeName2());
                    txtDistance.setText(advertisement.getDistance().concat(getString(R.string.kilometer)));
                    txtAvailableDays.setText(advertisement.getAvailable());
                    txtDriverName.setText(advertisement.getDriverName());
                    double rating = Double.valueOf(advertisement.getDriverRating())/20.0f;
                    ratingUser.setRating((float)rating);
                    BaseApplication.getPicasso().load(advertisement.getDriverImageUrl()).placeholder(R.drawable.ico_user).into(imgPhoto);

                    if(!advertisement.getPermission().isOwner()&&advertisement.getPermission().isCanAddBooking())
                        btnBook.setVisibility(View.VISIBLE);
                    else
                        btnBook.setVisibility(View.GONE);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }
        });
    }


    private void initPhoneCodeSpinner(final Spinner spinnerPhoneCode){
        try {
            Logger.error("-----------------------------initPhoneCodeSpinner--------------------");

            ArrayList<String> list = new ArrayList();
            ArrayAdapter adapter = new ArrayAdapter(
                    ActivityAdDetails.this,
                    R.layout.spinner_item,
                    list);
            spinnerPhoneCode.setAdapter(adapter);

            HttpUtil.getInstance().get(Constant.GET_ACTIVE_COUNTRIES_DIAL_CODE_API, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    ArrayList<String> list = new ArrayList();
                    int nUserCodePos = 0;
                    String countryCode = Misc.getCountryDialCode();
                    try {
                        JSONArray jsonArray = new JSONArray(paramString);
                        for(int j =0; j<jsonArray.length(); j++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(j);
                            String strCode  = jsonObject.getString("Code");
                            list.add(strCode);

                            if (strCode.equals(countryCode)) {
                                nUserCodePos = j;
                            }
                        }

                        ArrayAdapter adapter = new ArrayAdapter(
                                ActivityAdDetails.this,
                                R.layout.spinner_item,
                                list);

                        spinnerPhoneCode.setAdapter(adapter);
                        spinnerPhoneCode.setSelection(nUserCodePos);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                }
            });
        }catch (Exception e) {

        }
    }
    private void alertBookAd(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View viewBookAd = getLayoutInflater().inflate(R.layout.dialog_recipient_information,null);


        final CustomEditText editRecipientName = (CustomEditText)viewBookAd.findViewById(R.id.edit_recipient_name);
        final CustomEditText editRecipientPhone = (CustomEditText)viewBookAd.findViewById(R.id.edit_recipient_phone);
        final Spinner spinnerPhoneCode = (Spinner)viewBookAd.findViewById(R.id.spinner_phone_code);

        initPhoneCodeSpinner(spinnerPhoneCode);

        final RadioButton radioCard = (RadioButton)viewBookAd.findViewById(R.id.radio_card);


        final Button btnOk = (Button)viewBookAd.findViewById(R.id.btn_book);

        builder.setView(viewBookAd);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();




        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strRecipientName = editRecipientName.getText().toString();
                String strRecipientPhone = spinnerPhoneCode.getSelectedItem().toString()  + editRecipientPhone.getText().toString();

                Logger.error("recipient phone = " + strRecipientPhone);
                if(strRecipientName.length() == 0){
                    editRecipientName.setError(getString(R.string.error_enter_recipient_name));
                    editRecipientName.requestFocus();
                    return;
                }

                if(editRecipientPhone.length() == 0){
                    editRecipientPhone.setError(getString(R.string.error_enter_recipient_phone));
                    editRecipientPhone.requestFocus();
                    return;
                }

                HttpUtil httpUtil = HttpUtil.getInstance();
                RequestParams requestParams = new RequestParams();

                requestParams.put("AdvertisementID",advertisement.getID());
                requestParams.put("RecipientName",strRecipientName);
                requestParams.put("RecipientPhoneNumber",strRecipientPhone);

                if(radioCard.isChecked())
                    requestParams.put("PaymentMethod","0");
                else
                    requestParams.put("PaymentMethod","4");

                User user  = UserManager.with(context).getCurrentUser();
                httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
                httpUtil.post(Constant.BOOK_AD_API, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            Misc.showResponseMessage(responseBody);


                            FragmentMenuBooking.Initial_TAB  = 1;

                            MainActivity.selectAdTab();
                            ActivityAdDetails.this.finish();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Misc.showResponseMessage(responseBody);
                    }
                });

                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getString(R.string.title_ad_details));
        showNavHome(false);
    }


    private void onShowMap(){
        Intent intent = new Intent(this, ActivityMap.class);
        String fromLocation = Serializer.getInstance().serializeLocation(advertisement.getPickupLocation());
        String toLocation  = Serializer.getInstance().serializeLocation(advertisement.getDeliveryLocation());
        intent.putExtra(Constant.INTENT_FROM_LOCATION, fromLocation);
        intent.putExtra(Constant.INTENT_TO_LOCATION, toLocation);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_showmap:
                onShowMap();
                break;
            case R.id.btn_book:
                alertBookAd();
                break;
            case R.id.btn_message:
                Intent intent = new Intent(context, ActivityMessage.class);
                intent.putExtra(Constant.INTENT_USER_ID, advertisement.getDriverId());
                intent.putExtra(Constant.INTENT_USERNAME, advertisement.getDriverName());
                ActivityUtil.goOtherActivityFlipTransition(context, intent);
                break;
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
}
