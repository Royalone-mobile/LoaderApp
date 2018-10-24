package com.sawatruck.loader.view.activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.AddressDetail;
import com.sawatruck.loader.entities.LoadType;
import com.sawatruck.loader.entities.TruckClass;
import com.sawatruck.loader.entities.TruckType;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.AppSettings;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Serializer;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.design.CustomTextView;
import com.sawatruck.loader.view.fragments.FragmentHomeMapView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/16/2017.
 */

public class ActivitySearchFilter extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.spin_truckType) Spinner spinnerTruckType;
    @Bind(R.id.btn_applyfilter) Button btnApplyFilter;
    @Bind(R.id.btn_pickup_location) View btnPickupLocation;
    @Bind(R.id.txt_pickup_location) CustomTextView txtPickupLocation;

    ArrayList<TruckType> truckTypes = new ArrayList<>();
    AddressDetail addressDetail;
    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_searchfilter,null);
        ButterKnife.bind(this, view);
        btnApplyFilter.setOnClickListener(this);

        btnPickupLocation.setOnClickListener(this);

        initView();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getString(R.string.title_search_filter));
        showNavHome(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_applyfilter:
                applySearch();
                break;
            case R.id.btn_pickup_location:
                getPickupLocation();
                break;
        }
    }

    public void getPickupLocation(){
        Intent intent = new Intent(this, ActivityCities.class);
        startActivityForResult(intent,Constant.requestLoadLocationCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) return;
        try {
            String json = data.getStringExtra("address");
            AppSettings.with(this).setPickupLocation(json);
            addressDetail = Serializer.getInstance().deserializeAddressDetail(json);
            switch (requestCode) {
                case Constant.requestLoadLocationCode:
                    txtPickupLocation.setText(addressDetail.getFormatted_address());
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applySearch(){
		try{
			if(truckTypes.size()>=spinnerTruckType.getSelectedItemPosition()){
                if(spinnerTruckType.getSelectedItemPosition() == 0)
                    AppSettings.with(context).setTruckType("");
                else {
                    TruckType truckType = truckTypes.get(spinnerTruckType.getSelectedItemPosition());
                    AppSettings.with(context).setTruckType(String.valueOf(truckType.getID()));
                }
			}
		}catch (Exception e){
            e.printStackTrace();
        }
        finish();
        FragmentHomeMapView.refreshView();
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

    public void initView(){
        getAllTruckTypes();

        try {
            String str = AppSettings.with(this).getPickupLocation();
            addressDetail = Serializer.getInstance().deserializeAddressDetail(str);
            txtPickupLocation.setText(addressDetail.getFormatted_address());
        }catch (Exception e){

        }
    }



    public void getAllTruckTypes(){
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_TRUCK_TYPE_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                truckTypes = new ArrayList<>();
                ArrayList<String> truckTypesList = new ArrayList<>();
                truckTypesList.add("All");
                int firstSelection = 0;
                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for(int j=0; j<jsonArray.length(); j++) {
                        TruckType truckType = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), TruckType.class);
                        truckTypes.add(truckType);
                        truckTypesList.add(truckType.getName());

                        if( String.valueOf(truckType.getID()).equals(AppSettings.with(context).getTruckType()))
                            firstSelection  =  j;

                    }

                    ArrayAdapter adapter = new ArrayAdapter(
                            context,
                            R.layout.spinner_item,
                            truckTypesList);
                    adapter.setDropDownViewResource(R.layout.spinner_select);
                    spinnerTruckType.setAdapter(adapter);
                    if(truckTypes.size()>0)
                        spinnerTruckType.setSelection(firstSelection);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }


}
