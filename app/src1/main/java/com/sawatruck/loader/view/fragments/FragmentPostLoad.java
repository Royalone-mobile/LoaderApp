package com.sawatruck.loader.view.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.bruce.pickerview.popwindow.TimePickerPopWin;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.AddressDetail;
import com.sawatruck.loader.entities.LoadType;
import com.sawatruck.loader.utils.AppSettings;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Notice;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.activity.ActivityAdDetails;
import com.sawatruck.loader.view.activity.ActivityCities;
import com.sawatruck.loader.view.activity.BaseActivity;
import com.sawatruck.loader.view.activity.MainActivity;
import com.sawatruck.loader.view.adapter.LoadTypeAdapter;
import com.sawatruck.loader.view.design.CustomEditText;
import com.sawatruck.loader.view.design.CustomTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/19/2017.
 */


public class FragmentPostLoad extends BaseFragment implements View.OnClickListener {
    public static final String TAG = FragmentPostLoad.class.getSimpleName();
    @Bind(R.id.btn_fast_submit) TextView btnFastSubmit;
    @Bind(R.id.btn_add_details) TextView btnAddDetails;
    @Bind(R.id.edit_recipient_phone) CustomEditText editRecipientPhone;
    @Bind(R.id.edit_recipient_name) CustomEditText editRecipientName;
    @Bind(R.id.txt_load_date) CustomTextView txtPickupDate;
    @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
    @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;
    @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
    @Bind(R.id.txt_details) CustomEditText editDetails;
    @Bind(R.id.btn_delivery_date) View btnDeliveryDate;


    @Bind(R.id.btn_delivery_location) View btnDeliveryLocation;
    @Bind(R.id.btn_load_location) View btnLoadLocation;
    @Bind(R.id.btn_load_date) View btnLoadDate;
    @Bind(R.id.btn_pickup_time) View btnPickupTime;
    @Bind(R.id.txt_pickup_time) TextView txtPickupTime;

    @Bind(R.id.rv_container) RecyclerView rvContainer;
    @Bind(R.id.radio_credit_card) RadioButton radioCreditCard;
    @Bind(R.id.radio_cash) RadioButton radioCash;
    @Bind(R.id.spinner_phone_code) Spinner spinnerPhoneCode;

    LoadTypeAdapter loadTypeAdapter;
    Date loadDate = new Date(), unloadDate = new Date();

    private String payLoad = "{}";

    private AddressDetail pickupLocation, deliveryLocation;
    static FragmentPostLoad _instance;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postloads, container, false);
        ButterKnife.bind(this,view);



        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        btnFastSubmit.setOnClickListener(this);
        btnAddDetails.setOnClickListener(this);
        btnPickupTime.setOnClickListener(this);
        btnDeliveryDate.setOnClickListener(this);
        btnLoadDate.setOnClickListener(this);
        btnDeliveryLocation.setOnClickListener(this);
        btnLoadLocation.setOnClickListener(this);

        loadDate = new Date();
        loadDate.setDate(loadDate.getDate() + 1);
        unloadDate = new Date();
        unloadDate.setDate(unloadDate.getDate() + 2);

        Locale locale = new Locale(Locale.ENGLISH.getLanguage());
        SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd",locale);


        String pickupDate = convertFormat.format(loadDate);
        String deliveryDate = convertFormat.format(unloadDate);

        txtPickupDate.setText(pickupDate);
        txtDeliveryDate.setText(deliveryDate);
        txtPickupTime.setText("08:00AM");


        _instance = this;
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.title_post_loads));
        baseActivity.showOnlyNotificationMenu();

        LinearLayoutManager layoutManager =  new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvContainer.setLayoutManager(layoutManager);
        loadTypeAdapter  = new LoadTypeAdapter(getContext());
        rvContainer.setAdapter(loadTypeAdapter);



        getAddress(AppSettings.with(getActivity()).getCurrentLat(), AppSettings.with(getActivity()).getCurrentLng());
        getAllLoadTypes();

        initPhoneCodeSpinner(spinnerPhoneCode);
        return view;
    }

    private void initPhoneCodeSpinner(final Spinner spinnerPhoneCode){
        try {
            Logger.error("-----------------------------initPhoneCodeSpinner--------------------");

            ArrayList<String> list = new ArrayList();
            ArrayAdapter adapter = new ArrayAdapter(
                    getContext(),
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

                            if (strCode.equals(countryCode.trim())) {
                                nUserCodePos = j;
                            }
                        }

                        ArrayAdapter adapter = new ArrayAdapter(
                                getContext(),
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

    public void getAddress(Double latitude, Double longitude) {
        LocationAsyncTask locationAsyncTask = new LocationAsyncTask();
        Location location = new Location("location");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        locationAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,location);
    }

    private class LocationAsyncTask extends AsyncTask<Location, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Location... params) {
            Location location = params[0];

            final AddressDetail addressDetail = Misc.getAddressDetail(location.getLatitude(), location.getLongitude());

            pickupLocation = addressDetail;
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtLoadLocation.setText(addressDetail.getFormatted_address());
                    }
                });
            }catch(Exception e) {

            }
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
    @Override
    public void onDestroy(){
        _instance = null;
        super.onDestroy();
    }
    @Override
    public void onClick(View v) {
        int id =v.getId();
        switch (id) {
            case R.id.btn_delivery_date:
                getDeliveryDate();
                break;
            case R.id.btn_load_date:
                getLoadDate();
                break;
            case R.id.btn_delivery_location:
                getDeliveryLocation();
                break;
            case R.id.btn_load_location:
                getLoadLocation();
                break;
            case R.id.btn_add_details:
                addDetails();
                break;
            case R.id.btn_fast_submit:
                onFastSubmit();
                break;
            case R.id.btn_pickup_time:
                getPickupTime();
                break;
        }
    }

    public void getPickupTime(){
        TimePickerPopWin timePickerPopWin=new TimePickerPopWin.Builder(getActivity(), new  TimePickerPopWin.OnTimePickListener() {
            @Override
            public void onTimePickCompleted(int hour, int minute, String AM_PM, String time) {
                txtPickupTime.setText(time);
            }
        }).textConfirm(getString(R.string.btn_confirm)) //text of confirm button
                .textCancel("")
                .btnTextSize(16)
                .viewTextSize(25)
                .colorCancel(Color.parseColor("#999999"))
                .colorConfirm(Color.parseColor("#009900"))
                .build();
        timePickerPopWin.showPopWin(getActivity());
    }


    private void addDetails() {
        if(editDetails.getText().toString().length() == 0){
            editDetails.setError(getString(R.string.error_enter_load_description));
            editDetails.requestFocus();
            return;
        }

        if(editRecipientName.getText().toString().length() == 0){
            editRecipientName.setError(getString(R.string.error_enter_recipient_name));
            editRecipientName.requestFocus();
            return;
        }

        if(editRecipientPhone.getText().toString().length() == 0){
            editRecipientPhone.setError(getString(R.string.error_enter_recipient_phone));
            editRecipientPhone.requestFocus();
            return;
        }

        if(pickupLocation == null) {
            Notice.show(getString(R.string.error_enter_pickup_location));
            return;
        }

        if(deliveryLocation == null) {
            Notice.show( getString(R.string.error_enter_delivery_location));
            return;
        }

        if(loadTypeAdapter.getLoadTypes().get(loadTypeAdapter.getCurrentSelectedItem()).getLoadTypeID() == 10){
            return;
        }

        MainActivity mainActivity = (MainActivity)getActivity();
        FragmentPostLoad1 fragmentPostLoad1 = new FragmentPostLoad1();
        fragmentPostLoad1.setPostLoadContent(buildJSON(false));
        FragmentPostLoad2 fragmentPostLoad2 = new FragmentPostLoad2();
        fragmentPostLoad2.setPostLoadContent(buildJSON(false));

        int loadTypeID = loadTypeAdapter.getLoadTypes().get(loadTypeAdapter.getCurrentSelectedItem()).getLoadTypeID();
        fragmentPostLoad1.setLoadTypeID(loadTypeID);
        fragmentPostLoad2.setLoadTypeID(loadTypeID);

        if(loadTypeID == 6 || loadTypeID == 3) {
            mainActivity.hideFragmentWithTag(FragmentPostLoad.TAG);
            mainActivity.changeFragmentWithTag(fragmentPostLoad1, FragmentPostLoad1.TAG);
        }
        else{

            mainActivity.hideFragmentWithTag(FragmentPostLoad.TAG);
            mainActivity.changeFragmentWithTag(fragmentPostLoad2, FragmentPostLoad2.TAG);
        }
    }

    public void getAllLoadTypes(){
        HttpUtil.getInstance().get(Constant.GET_ALL_LOAD_TYPES_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    Logger.error("-----All Loads--------------");
                    Logger.error(paramString);
                    ArrayList<LoadType> loadTypes = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(paramString);

                    for(int j =0 ; j<jsonArray.length(); j++) {
                        JSONObject ratingObject = (JSONObject) jsonArray.get(j);
                        LoadType loadType = BaseApplication.getGson().fromJson(ratingObject.toString(), LoadType.class);
                        loadTypes.add(loadType);
                    }
                    Logger.error("-----All Loads length = " + loadTypes.size());
                    loadTypeAdapter.setLoadTypes(loadTypes);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadTypeAdapter.notifyDataSetChanged();
                            loadTypeAdapter.tryMoveSelection();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, final byte[] errorString, Throwable throwable) {
            }
        });
    }

    private void onFastSubmit() {


        if(editDetails.getText().toString().length() == 0){
            editDetails.setError(getString(R.string.error_enter_load_description));
            editDetails.requestFocus();
            return;
        }

        if(editRecipientName.getText().toString().length() == 0){
            editRecipientName.setError(getString(R.string.error_enter_recipient_name));
            editRecipientName.requestFocus();
            return;
        }

        if(editRecipientPhone.getText().toString().length() == 0){
            editRecipientPhone.setError(getString(R.string.error_enter_recipient_phone));
            editRecipientPhone.requestFocus();
            return;
        }

        if(pickupLocation == null) {
            Notice.show(getString(R.string.error_enter_pickup_location));
            return;
        }

        if(deliveryLocation == null) {
            Notice.show(getString(R.string.error_enter_delivery_location));
            return;
        }

        hideKeyboard();

        FastSubmitLoadAsyncTask fastSubmitLoadAsyncTask = new FastSubmitLoadAsyncTask();
        fastSubmitLoadAsyncTask.execute(buildJSON(true));

    }

    public String buildJSON(boolean isFastSubmit){
        String response = "";
        try {
            JSONObject jsonObject = new JSONObject(payLoad);
            jsonObject.put("LoadDateFrom", txtPickupDate.getText().toString().concat(" ").concat(txtPickupTime.getText().toString()));
            jsonObject.put("LoadDateEnd", txtPickupDate.getText().toString().concat(" ").concat(txtPickupTime.getText().toString()));
            jsonObject.put("UnloadDateFrom",txtDeliveryDate.getText().toString());
            jsonObject.put("UnloadDateEnd",txtDeliveryDate.getText().toString());

            if(isFastSubmit)
                jsonObject.put("LoadTypeID", "10");
            else
                jsonObject.put("LoadTypeID",loadTypeAdapter.getLoadTypes().get(loadTypeAdapter.getCurrentSelectedItem()).getLoadTypeID());

            jsonObject.put("LoadDetails", editDetails.getText().toString());
            jsonObject.put("CurrencyID", UserManager.with(getContext()).getCurrentUser().getDefaultCurrencyID());
            jsonObject.put("IsCash", String.valueOf(radioCash.isChecked()));
            jsonObject.put("ReceiverName",editRecipientName.getText().toString());


            String recipientPhone = spinnerPhoneCode.getSelectedItem().toString()  + editRecipientPhone.getText().toString();
            Logger.error("recipient phone = " + recipientPhone);
            jsonObject.put("ReceiverPhone",recipientPhone);

            JSONObject fromLocationObject = new JSONObject();
            fromLocationObject.put("formatted_address", pickupLocation.getFormatted_address());
            fromLocationObject.put("city", pickupLocation.getCity());
            fromLocationObject.put("country", pickupLocation.getCountry());
            fromLocationObject.put("AdministrativeAreaLevel1", pickupLocation.getAdministrativeAreaLevel1());
            fromLocationObject.put("latitude", String.valueOf(pickupLocation.getLatitude()));
            fromLocationObject.put("longitude", String.valueOf(pickupLocation.getLongitude()));

            JSONObject toLocationObject = new JSONObject();
            toLocationObject.put("formatted_address", deliveryLocation.getFormatted_address());
            toLocationObject.put("city", deliveryLocation.getCity());
            toLocationObject.put("country", deliveryLocation.getCountry());
            toLocationObject.put("AdministrativeAreaLevel1", deliveryLocation.getAdministrativeAreaLevel1());
            toLocationObject.put("latitude", String.valueOf(deliveryLocation.getLatitude()));
            toLocationObject.put("longitude", String.valueOf(deliveryLocation.getLongitude()));

            jsonObject.put("FromLocation",fromLocationObject);
            jsonObject.put("ToLocation",toLocationObject);

            Location locationFrom = new Location("");
            Location locationTo = new Location("");

            if(pickupLocation==null || deliveryLocation==null)
                jsonObject.put("Distance","0");
            else {
                locationFrom.setLatitude(pickupLocation.getLatitude());
                locationFrom.setLongitude(pickupLocation.getLongitude());
                locationTo.setLatitude(deliveryLocation.getLatitude());
                locationTo.setLongitude(deliveryLocation.getLongitude());

                float distance = locationFrom.distanceTo(locationTo);
                jsonObject.put("Distance", String.valueOf(distance));
            }


            response =  jsonObject.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
    private class FastSubmitLoadAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.postBody(Constant.POST_LOAD_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Misc.showResponseMessage(responseBody);
                                FragmentMenuLoad.Initial_TAB = 1;
                                MainActivity.selectLoadTab();
                            }
                        });
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(final String errorString) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Misc.showResponseMessage(errorString);
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

    public void getLoadLocation(){
        Intent intent = new Intent(getActivity(), ActivityCities.class);
        getActivity().startActivityForResult(intent,Constant.requestLoadLocationCode);
    }

    public void getDeliveryLocation(){
        Intent intent = new Intent(getActivity(), ActivityCities.class);
        getActivity().startActivityForResult(intent,Constant.requestUnLoadLocationCode);
    }


    public void getLoadDate(){
        Date now = new Date();
        Locale locale = new Locale(Locale.ENGLISH.getLanguage());
        SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd",locale);
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd",locale);
        String strDate = "";
        if(txtPickupDate.getText().length() == 0) {
            strDate = convertFormat.format(now);
        }
        else if(txtPickupDate.getText().length() >= 0){
            try {
                Date date = parseFormat.parse(txtPickupDate.getText().toString());

                strDate = convertFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(getActivity(), new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                Date tempDate = null;

                try {

                    DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                    tempDate  = f.parse(dateDesc);
                    tempDate.setDate(day+1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(Misc.dateToLong(new Date())>Misc.dateToLong(tempDate)) {
                    Notice.show( R.string.error_date_ahead_today);
                    return;
                }
                tempDate.setDate(day);
                java.text.NumberFormat mNumberFormat= java.text.NumberFormat.getIntegerInstance(Locale.US);
                mNumberFormat.setGroupingUsed(false);
                txtPickupDate.setText(String.format("%s-%s-%s", mNumberFormat.format(year), mNumberFormat.format(month),mNumberFormat.format(day)));
                loadDate = tempDate;
            }
        }).textConfirm(getString(R.string.btn_confirm)) //text of confirm button
                .textCancel("")
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .minYear(1990) //min year in loop
                .maxYear(2030) // max year in loop
                .showDayMonthYear(true) // shows like dd mm yyyy (default is false)
                .dateChose(strDate) // date chose when init popwindow
                .build();
        pickerPopWin.showPopWin(getActivity());

    }

    public void getDeliveryDate(){
        Date now = new Date();
        now.setDate(now.getDate() + 1);

        Locale locale = new Locale(Locale.ENGLISH.getLanguage());
        SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd",locale);
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd",locale);
        String strDate = "";
        if(txtDeliveryDate.getText().length() == 0) {
            strDate = convertFormat.format(now);
        }
        else if(txtDeliveryDate.getText().length() >= 0){
            try {
                Date date = parseFormat.parse(txtDeliveryDate.getText().toString());
                strDate = convertFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(getActivity(), new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {

                DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                Date tempDate = null;
                try {
                    tempDate  = f.parse(dateDesc);
                    tempDate.setDate(day+1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(Misc.dateToLong(new Date())>Misc.dateToLong(tempDate)) {
                    Notice.show(R.string.error_date_ahead_today);
                    return;
                }

                tempDate.setDate(day);

                if(Misc.dateToLong(tempDate)<Misc.dateToLong(loadDate)) {
                    Notice.show( R.string.error_delivery_date_ahead);
                    return;
                }

                unloadDate = tempDate;
                java.text.NumberFormat mNumberFormat= java.text.NumberFormat.getIntegerInstance(Locale.US);
                mNumberFormat.setGroupingUsed(false);
                txtDeliveryDate.setText(String.format("%s-%s-%s", mNumberFormat.format(year), mNumberFormat.format(month),mNumberFormat.format(day)));
            }
        }).textConfirm(getString(R.string.btn_confirm)) //text of confirm button
                .textCancel("")
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .minYear(1990) //min year in loop
                .maxYear(2030) // max year in loop
                .showDayMonthYear(true) // shows like dd mm yyyy (default is false)
                .dateChose(strDate) // date chose when init popwindow
                .build();
        pickerPopWin.showPopWin(getActivity());
    }

    public static void setPickupLocation(AddressDetail addressDetail){
        if(_instance == null) return;
        _instance.pickupLocation = addressDetail;
        _instance.txtLoadLocation.setText(addressDetail.getFormatted_address());
    }

    public static void setDeliveryLocation(AddressDetail addressDetail){
        if(_instance == null) return;
        _instance.deliveryLocation = addressDetail;
        _instance.txtDeliveryLocation.setText(addressDetail.getFormatted_address());
    }

}
