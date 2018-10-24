package com.sawatruck.loader.view.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.AddressDetail;
import com.sawatruck.loader.entities.Truck;
import com.sawatruck.loader.repository.DBController;
import com.sawatruck.loader.repository.LoadSearchDAO;
import com.sawatruck.loader.utils.AppSettings;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Notice;
import com.sawatruck.loader.utils.Serializer;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.view.adapter.SavedSearchAdapter;
import com.sawatruck.loader.view.design.CustomTextView;
import com.sawatruck.loader.view.fragments.FragmentPostLoad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/20/2017.
 */


public class ActivitySearchAdvertisement extends BaseActivity implements View.OnClickListener, SavedSearchAdapter.SavedSearchClickListener, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.btn_load_date) View btnLoadDate;
    @Bind(R.id.btn_unload_date) View btnUnLoadDate;
    @Bind(R.id.btn_load_location) View btnLoadLocation;
    @Bind(R.id.btn_unload_location) View btnUnLoadLocation;
    @Bind(R.id.txt_load_date) CustomTextView txtLoadDate;
    @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
    @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;
    @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
    @Bind(R.id.btn_save_search) CustomTextView btnSaveSearch;
    @Bind(R.id.rv_saved_search) RecyclerView rvSavedSearch;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.btn_search) View btnSearch;
    private final int requestLoadLocationCode = 120 , requestUnLoadLocationCode= 121;
    private Date loadDate = new Date(), unloadDate = new Date();
    private SavedSearchAdapter  savedSearchAdapter;
    private AddressDetail pickupLocation, deliveryLocation;


    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_search_truck,null);
        ButterKnife.bind(this, view);

        btnLoadDate.setOnClickListener(this);
        btnUnLoadDate.setOnClickListener(this);
        btnLoadLocation.setOnClickListener(this);
        btnUnLoadLocation.setOnClickListener(this);
        btnSaveSearch.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        initView();

        getAddress(AppSettings.with(this).getCurrentLat(), AppSettings.with(this).getCurrentLng());
        return view;
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtLoadLocation.setText(addressDetail.getFormatted_address());
                }
            });
            return null;
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        showNavHome(false);
        loadSearch();
        setAppTitle(getResources().getString(R.string.title_search_load));
        savedSearchAdapter.setSavedSearchClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_load_date:
                getLoadDate();
                break;
            case R.id.btn_unload_date:
                getUnLoadDate();
                break;
            case R.id.btn_load_location:
                getLoadLocation();
                break;
            case R.id.btn_unload_location:
                getDeliveryLocation();
                break;
            case R.id.btn_save_search:
                alertSaveSearch();
                break;
            case R.id.btn_search:
                try {
                    searchTrucks();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void searchTrucks() throws JSONException {
		if(pickupLocation==null || deliveryLocation==null) return;
        JSONObject rootObject = new JSONObject();
        JSONObject searchObject = new JSONObject();
        searchObject.put("Value", "");
        searchObject.put("Regex", false);
        rootObject.put("Length", 20);
        rootObject.put("Start", 0);
        rootObject.put("Search", searchObject);
        JSONArray orderArray = new JSONArray();
        JSONObject orderObject = new JSONObject();
        orderArray.put(orderObject);
        orderObject.put("Column", 0);
        orderObject.put("Dir", 0);
        rootObject.put("Order", orderArray);


        JSONObject paletteNumberObject = new JSONObject();
        paletteNumberObject.put("Name", "PaletteNumber");
        paletteNumberObject.put("Data", "");
        paletteNumberObject.put("Orderable", false);
        paletteNumberObject.put("Searchable", false);

        JSONObject searchObject1 = new JSONObject();
        searchObject1.put("Value", "");
        searchObject1.put("Regex", false);

        paletteNumberObject.put("Search", searchObject1);

        JSONObject truckBrandObject = new JSONObject();
        truckBrandObject.put("Name", "VehicleBrandID");
        truckBrandObject.put("Data", "");
        truckBrandObject.put("Orderable", true);
        truckBrandObject.put("Searchable", true);

        JSONObject searchObject2 = new JSONObject();
        searchObject2.put("Value", "");
        searchObject2.put("Regex", false);

        truckBrandObject.put("Search", searchObject2);

        JSONObject modelObject = new JSONObject();
        modelObject.put("Name", "Model");
        modelObject.put("Data", "");
        modelObject.put("Orderable", true);
        modelObject.put("Searchable", true);

        JSONObject searchObject3 = new JSONObject();
        searchObject3.put("Regex", false);
        modelObject.put("Search", searchObject3);


        JSONObject ownerNameObject = new JSONObject();
        ownerNameObject.put("Name", "OwnerName");
        ownerNameObject.put("Data", "");
        ownerNameObject.put("Orderable", true);
        ownerNameObject.put("Searchable", true);

        JSONObject searchObject4 = new JSONObject();
        searchObject4.put("Value", "");
        searchObject4.put("Regex", false);
        ownerNameObject.put("Search", searchObject4);



        JSONObject pickupDateObject = new JSONObject();
        pickupDateObject.put("Name", "PickupDate");
        pickupDateObject.put("Data", "");
        pickupDateObject.put("Orderable", false);
        pickupDateObject.put("Searchable", true);

        JSONObject searchObject5 = new JSONObject();
        searchObject5.put("Value", txtLoadDate.getText().toString());
        searchObject5.put("Regex", false);
        pickupDateObject.put("Search", searchObject5);


        JSONObject deliveryDateObject = new JSONObject();
        deliveryDateObject.put("Name", "DeliveryDate");
        deliveryDateObject.put("Data", "");
        deliveryDateObject.put("Orderable", false);
        deliveryDateObject.put("Searchable", true);

        JSONObject searchObject6 = new JSONObject();
        searchObject6.put("Value", txtDeliveryDate.getText().toString());
        searchObject6.put("Regex", false);
        deliveryDateObject.put("Search", searchObject6);


        JSONArray columnsArray=  new JSONArray();
        columnsArray.put(paletteNumberObject);
        columnsArray.put(truckBrandObject);
        columnsArray.put(modelObject);
        columnsArray.put(ownerNameObject);
        columnsArray.put(pickupDateObject);
        columnsArray.put(deliveryDateObject);

        rootObject.put("Columns", columnsArray);

        String json = StringUtil.escapeString(rootObject.toString());
        TruckSearchAsyncTask truckSearchAsyncTask = new TruckSearchAsyncTask();
        truckSearchAsyncTask.execute(json);

    }

    private class TruckSearchAsyncTask extends AsyncTask<String, Void, Void> {
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
                        final ArrayList<Truck> trucks = new ArrayList<>();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            Truck truck = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Truck.class);

                            trucks.add(truck);
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Misc.showResponseMessage(responseBody);
                                ActivitySearchAdvertisement.this.finish();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(final String errorString) {
                    try {
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

    public void alertSaveSearch(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View viewSaveSearch = getLayoutInflater().inflate(R.layout.dialog_enter_savename,null);

        final EditText editSaveName = (EditText)viewSaveSearch.findViewById(R.id.edit_saveName);

        final Button btnOk = (Button)viewSaveSearch.findViewById(R.id.btn_ok);
        final Button btnCancel = (Button)viewSaveSearch.findViewById(R.id.btn_cancel);

        builder.setView(viewSaveSearch);

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
                if(editSaveName.getText().length() == 0){
                    editSaveName.setError(getString(R.string.error_enter_save_name));
                    editSaveName.requestFocus();
                    return;
                }
                saveSearch(editSaveName.getText().toString());
                hideSearchButton();
                alertDialog.dismiss();
            }
        });
    }

    public void loadSearch(){
        DBController dbController = DBController.getInstance().open(this);

        ArrayList<LoadSearchDAO> loadSearchDAOs = dbController.getSearchs();

        savedSearchAdapter.setLoadSearchs(loadSearchDAOs);
        savedSearchAdapter.notifyDataSetChanged();
        dbController.closeDB();
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }
    public void saveSearch(String searchName){
        DBController dbController = DBController.getInstance().open(this);
        LoadSearchDAO loadSearchDAO = new LoadSearchDAO();
        loadSearchDAO.setDeliveryDate(txtDeliveryDate.getText().toString());
        loadSearchDAO.setLoadDate(txtLoadDate.getText().toString());
        loadSearchDAO.setDeliveryLocation(txtDeliveryLocation.getText().toString());
        loadSearchDAO.setLoadLocation(txtLoadLocation.getText().toString());

        loadSearchDAO.setLoadAddress(Serializer.getInstance().serializeAddressDetail(pickupLocation));
        loadSearchDAO.setDeliveryAddress(Serializer.getInstance().serializeAddressDetail(deliveryLocation));

        loadSearchDAO.setSearchName(searchName);
        dbController.insertSearch(loadSearchDAO);
        dbController.closeDB();
        loadSearch();
    }

    public void initView(){
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rvSavedSearch.setVerticalScrollBarEnabled(true);
        rvSavedSearch.setLayoutManager(mLayoutManager);
        savedSearchAdapter = new SavedSearchAdapter(this);
        rvSavedSearch.setAdapter(savedSearchAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void getLoadLocation(){
        Intent intent = new Intent(this, ActivityCities.class);
        startActivityForResult(intent,requestLoadLocationCode);
    }

    public void getDeliveryLocation(){
        Intent intent = new Intent(this, ActivityCities.class);
        startActivityForResult(intent,requestUnLoadLocationCode);
    }

    public void showSearchButton(){
        if(txtLoadDate.getText().length() == 0 || txtLoadLocation.getText().length() == 0 || txtDeliveryDate.getText().length() == 0 || txtDeliveryLocation.getText().length() == 0)
            return;

        btnSaveSearch.setVisibility(View.VISIBLE);
    }

    public void hideSearchButton(){
        btnSaveSearch.setVisibility(View.GONE);
    }


    public void getLoadDate(){
        Date now = new Date();
        SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = "";
        if(txtLoadDate.getText().length() == 0) {
            strDate = convertFormat.format(now);
        }
        else if(txtLoadDate.getText().length() >= 0){
            try {
                Date date = parseFormat.parse(txtLoadDate.getText().toString());

                strDate = convertFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(this, new DatePickerPopWin.OnDatePickedListener() {
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
                    Notice.show(R.string.error_date_ahead_today);
                    return;
                }
                tempDate.setDate(day);

                java.text.NumberFormat mNumberFormat= java.text.NumberFormat.getIntegerInstance(Locale.US);
                mNumberFormat.setGroupingUsed(false);
                txtLoadDate.setText(String.format("%s-%s-%s", mNumberFormat.format(year), mNumberFormat.format(month),mNumberFormat.format(day)));
                loadDate = tempDate;
                showSearchButton();
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
        pickerPopWin.showPopWin(this);

    }

    public void getUnLoadDate(){
        Date now = new Date();
        now.setDate(now.getDate() + 1);
        SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd");
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

        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(this, new DatePickerPopWin.OnDatePickedListener() {
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
                    Notice.show(R.string.error_delivery_date_ahead);
                    return;
                }

                unloadDate = tempDate;
                java.text.NumberFormat mNumberFormat= java.text.NumberFormat.getIntegerInstance(Locale.US);
                mNumberFormat.setGroupingUsed(false);
                txtDeliveryDate.setText(String.format("%s-%s-%s", mNumberFormat.format(year), mNumberFormat.format(month),mNumberFormat.format(day)));
                showSearchButton();
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
        pickerPopWin.showPopWin(this);
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
    public void onSavedSearchClick(LoadSearchDAO searchItem) {
        txtLoadDate.setText(searchItem.getLoadDate());
        txtLoadLocation.setText(searchItem.getLoadLocation());
        txtDeliveryDate.setText(searchItem.getDeliveryDate());
        txtDeliveryLocation.setText(searchItem.getDeliveryLocation());

        pickupLocation = Serializer.getInstance().deserializeAddressDetail(searchItem.getLoadAddress());
        deliveryLocation = Serializer.getInstance().deserializeAddressDetail(searchItem.getDeliveryAddress());
    }


    @Override
    public void onRefresh() {
        loadSearch();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) return;
        AddressDetail addressDetail;
        try {
            String json = data.getStringExtra("address");
            addressDetail = Serializer.getInstance().deserializeAddressDetail(json);
            switch (requestCode) {
                case requestLoadLocationCode:
                    pickupLocation = addressDetail;
                    txtLoadLocation.setText(addressDetail.getFormatted_address());
                    break;
                case requestUnLoadLocationCode:
                    deliveryLocation = addressDetail;
                    txtDeliveryLocation.setText(addressDetail.getFormatted_address());
                    break;
            }
            showSearchButton();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
