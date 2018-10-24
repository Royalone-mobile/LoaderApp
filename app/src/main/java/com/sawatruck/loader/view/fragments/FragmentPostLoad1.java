package com.sawatruck.loader.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.BulkType;
import com.sawatruck.loader.entities.MeasureUnit;
import com.sawatruck.loader.entities.Package;
import com.sawatruck.loader.entities.TruckBrand;
import com.sawatruck.loader.entities.TruckType;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.AppSettings;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.activity.BaseActivity;
import com.sawatruck.loader.view.activity.MainActivity;
import com.sawatruck.loader.view.adapter.PackageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/19/2017.
 */


public class FragmentPostLoad1 extends BaseFragment implements View.OnClickListener {
    public static final String TAG = FragmentPostLoad1.class.getSimpleName();
    @Bind(R.id.btn_back) View btnBack;
    @Bind(R.id.btn_next) View btnNext;
    @Bind(R.id.btn_add_package) View btnAddPackage;
    @Bind(R.id.rv_container) RecyclerView rcContainer;

    private PackageAdapter packageAdapter;
    private int loadTypeID = 6;
    private String postLoadContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_loads_step_one, container, false);
        ButterKnife.bind(this, view);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnAddPackage.setOnClickListener(this);
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.title_post_loads));
        initView();
        getTruckBrands();
        getTruckTypes();
        getMeasureUnits();
        getAllBulkTypes();

        return view;
    }

    private void getAllBulkTypes() {
        HttpUtil httpUtil = new HttpUtil();
        User user = UserManager.with(getContext()).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_arabic))){
            httpUtil.getClient().addHeader("Accept-Language", "ar-Global");
        }

        httpUtil.get(Constant.GET_BULK_TYPES_API,  new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);

                    ArrayList<BulkType> measureUnits = new ArrayList<>();


                    JSONArray jsonArray = new JSONArray(paramString);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        BulkType measureUnit = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), BulkType.class);
                        measureUnits.add(measureUnit);
                    }

                    PackageAdapter.bulkTypes = measureUnits;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            packageAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public void initView() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        packageAdapter = new PackageAdapter(getContext());
        rcContainer.setAdapter(packageAdapter);

        PackageAdapter.setLoadTypeID(loadTypeID);
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        MainActivity mainActivity = (MainActivity) getActivity();
        switch (id) {
            case R.id.btn_back:
                FragmentPostLoad fragmentPostLoad = new FragmentPostLoad();
                Bundle bundle = new Bundle();
                bundle.putString("PAYLOAD", postLoadContent);

                fragmentPostLoad.setArguments(bundle);
                mainActivity.hideFragmentWithTag(FragmentPostLoad1.TAG);
                mainActivity.changeFragmentWithTag(fragmentPostLoad, FragmentPostLoad.TAG);
                break;
            case R.id.btn_next:
                nextForm();
                break;
            case R.id.btn_add_package:
                ArrayList<Package> packages = packageAdapter.getPackages();
                packages.add(new Package());
                packageAdapter.setPackages(packages);
                packageAdapter.notifyDataSetChanged();
                rcContainer.scrollToPosition(packages.size() - 1);
                break;
        }
    }

    public void nextForm() {
        MainActivity mainActivity = (MainActivity) getActivity();
        FragmentPostLoad2 fragmentPostLoad2 = new FragmentPostLoad2();
        try {
            JSONObject postLoadObject = new JSONObject(postLoadContent);
            JSONArray packageArray = new JSONArray();
            for (Package item : packageAdapter.getPackages()) {
                JSONObject packageObject = new JSONObject();
                if (item.getMeasureUnit() != null)
                    packageObject.put("MeasureUnitID", item.getMeasureUnit().getID());
                if(item.getBulkType()!=null)
                    packageObject.put("BulkTypeID", item.getBulkType().getID());
                if (item.getTruckBrand() != null)
                    packageObject.put("VehicleBrandID", item.getTruckBrand().getCompanyID());
                if (item.getTruckType() != null)
                    packageObject.put("VehicleTypeID", item.getTruckType().getID());

                packageObject.put("Model", item.getModel());
                packageObject.put("Quantity", item.getQuantity());
                packageObject.put("WeightPerUnit", item.getUnitWeight());
                packageArray.put(packageObject);
            }
            JSONObject jsonObject = new JSONObject();

            int loadTypeID = postLoadObject.getInt("LoadTypeID");
            switch (loadTypeID) {
                case Constant.LOAD_TYPE_CAR:
                    jsonObject.put("LoadVehiclePackages", packageArray);
                    postLoadObject.put("LoadVehicle", jsonObject);
                    break;
                case Constant.LOAD_TYPE_BULK:
                    jsonObject.put("LoadBulkPackages", packageArray);
                    postLoadObject.put("LoadBulk", jsonObject);
                    break;
            }
            fragmentPostLoad2.setPostLoadContent(postLoadObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mainActivity.hideFragmentWithTag(FragmentPostLoad1.TAG);
        fragmentPostLoad2.setLoadTypeID(this.loadTypeID);

        mainActivity.changeFragmentWithTag(fragmentPostLoad2, FragmentPostLoad2.TAG);
    }

    public void getMeasureUnits() {
        HttpUtil httpUtil = new HttpUtil();
        User user = UserManager.with(getContext()).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_MEASURE_UNIT_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);

                    ArrayList<MeasureUnit> measureUnits = new ArrayList<>();


                    JSONArray jsonArray = new JSONArray(paramString);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        MeasureUnit measureUnit = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), MeasureUnit.class);
                        measureUnits.add(measureUnit);
                    }

                    PackageAdapter.measureUnits = measureUnits;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            packageAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public void getTruckBrands() {
        HttpUtil httpUtil = new HttpUtil();
        User user = UserManager.with(getContext()).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_TRUCK_BRAND_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);

                    ArrayList<TruckBrand> truckBrands = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(paramString);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        TruckBrand truckBrand = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), TruckBrand.class);
                        truckBrands.add(truckBrand);
                    }
                    PackageAdapter.truckBrands = truckBrands;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            packageAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public void getTruckTypes() {
        HttpUtil httpUtil = new HttpUtil();
        User user = UserManager.with(getContext()).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_TRUCK_TYPE_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    ArrayList<TruckType> truckTypes = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(paramString);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        TruckType truckBrand = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), TruckType.class);
                        truckTypes.add(truckBrand);
                    }
                    PackageAdapter.truckTypes = truckTypes;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            packageAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public String getPostLoadContent() {
        return postLoadContent;
    }

    public void setPostLoadContent(String postLoadContent) {
        this.postLoadContent = postLoadContent;
    }

    public void setLoadTypeID(int loadTypeID) {
        this.loadTypeID = loadTypeID;
    }
}
