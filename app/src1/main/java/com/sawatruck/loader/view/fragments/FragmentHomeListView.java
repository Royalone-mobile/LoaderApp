package com.sawatruck.loader.view.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.AddressDetail;
import com.sawatruck.loader.entities.Advertisement;
import com.sawatruck.loader.utils.AppSettings;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Serializer;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.view.adapter.AdAdapter;
import com.sawatruck.loader.view.design.HideShowScrollListener;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/20/2017.
 */


public class FragmentHomeListView extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_POSITION = "position";
    @Bind(R.id.rc_container) RecyclerView rcContainer;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;
    public AdAdapter adAdapter;
    public ArrayList<Advertisement> advertisements = new ArrayList<>();
    static FragmentHomeListView _instance;
    public static FragmentHomeListView getInstance(int position) {
        FragmentHomeListView f = new FragmentHomeListView();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        ButterKnife.bind(this,view);
        initView();
        _instance = this;
        return view;
    }

    public void initView(){
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        adAdapter = new AdAdapter(getContext());
        rcContainer.setAdapter(adAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        rcContainer.addOnScrollListener(new HideShowScrollListener() {

            @Override
            public void onHide() {
                final FragmentMenuHome parentFragment = (FragmentMenuHome) getParentFragment();
                parentFragment.getPostAddButton().animate().setDuration(200).translationY(parentFragment.getPostAddButton().getHeight()).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        parentFragment.getPostAddButton().setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onShow() {
                final FragmentMenuHome parentFragment = (FragmentMenuHome) getParentFragment();
                parentFragment.getPostAddButton().animate().setDuration(200).translationY(0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        parentFragment.getPostAddButton().setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onScrolled() {

            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.startSpinning();
        adAdapter.initializeAdapter();
        try {
            searchAds();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        adAdapter.initializeAdapter();
        try {
            searchAds();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void searchAds() throws JSONException {
        LatLng latLng = new LatLng(AppSettings.with(getContext()).getCurrentLat(), AppSettings.with(getContext()).getCurrentLng());
        String strPickupLocation = AppSettings.with(getContext()).getPickupLocation();
        Double pickupLatitude = latLng.latitude;
        Double pickupLongitude = latLng.longitude;
        try {
            AddressDetail addressDetail = Serializer.getInstance().deserializeAddressDetail(strPickupLocation);
            pickupLatitude =  addressDetail.getLatitude();
            pickupLongitude =  addressDetail.getLongitude();
        }catch (Exception e) {
            Logger.error("----------------Pickup location setting is null-----------------");
        }

        JSONObject rootObject = new JSONObject();
        rootObject.put("Length", 20);
        rootObject.put("Start", 0);

        JSONObject loadTypeObject = new JSONObject();
        loadTypeObject.put("Name", "LoadType");

        loadTypeObject.put("Data", "");
        loadTypeObject.put("Orderable", false);
        loadTypeObject.put("Searchable", false);

        JSONObject searchObject1 = new JSONObject();
        // TODO LOADTYPES
        searchObject1.put("Value", "");
        searchObject1.put("Regex", false);

        loadTypeObject.put("Search", searchObject1);


        JSONObject truckClassObject = new JSONObject();
        truckClassObject.put("Name", "TruckClass");
        truckClassObject.put("Data", "");
        truckClassObject.put("Orderable", false);
        truckClassObject.put("Searchable", true);

        JSONObject searchObject2 = new JSONObject();
        searchObject2.put("Value", "");
        searchObject2.put("Regex", false);

        truckClassObject.put("Search", searchObject2);

        JSONObject fromLatitudeObject = new JSONObject();
        fromLatitudeObject.put("Name", "FromLatitude");
        fromLatitudeObject.put("Data", "");
        fromLatitudeObject.put("Orderable", true);
        fromLatitudeObject.put("Searchable", true);

        JSONObject searchObject3 = new JSONObject();

        searchObject3.put("Value", String.valueOf(pickupLatitude));
        searchObject3.put("Regex", false);
        fromLatitudeObject.put("Search", searchObject3);


        JSONObject fromLongitudeObject = new JSONObject();
        fromLongitudeObject.put("Name", "FromLongitude");
        fromLongitudeObject.put("Data", "");
        fromLongitudeObject.put("Orderable", true);
        fromLongitudeObject.put("Searchable", true);

        JSONObject searchObject4 = new JSONObject();

        searchObject4.put("Value", String.valueOf(pickupLongitude));

        searchObject4.put("Regex", false);
        fromLongitudeObject.put("Search", searchObject4);

        JSONObject distanceObject = new JSONObject();
        distanceObject.put("Name", "Distance");
        distanceObject.put("Data", "");
        distanceObject.put("Orderable", true);
        distanceObject.put("Searchable", true);

        JSONObject searchObject5 = new JSONObject();
        searchObject5.put("Value", Constant.PLACES_API_RADIUS);
        searchObject5.put("Regex", false);
        distanceObject.put("Search", searchObject5);


        JSONObject truckTypeObject = new JSONObject();
        truckTypeObject.put("Name", "TruckType");
        truckTypeObject.put("Data", "");
        truckTypeObject.put("Orderable", false);
        truckTypeObject.put("Searchable", true);

        JSONObject searchObject6 = new JSONObject();
        searchObject6.put("Value", String.valueOf(AppSettings.with(getContext()).getTruckType()));
        searchObject6.put("Regex", false);
        truckTypeObject.put("Search", searchObject6);

        JSONArray columnsArray=  new JSONArray();
        columnsArray.put(loadTypeObject);
        columnsArray.put(truckClassObject);
        columnsArray.put(fromLatitudeObject);
        columnsArray.put(fromLongitudeObject);
        columnsArray.put(distanceObject);
        columnsArray.put(truckTypeObject);

        rootObject.put("Columns", columnsArray);

        String json = StringUtil.escapeString(rootObject.toString());
        Logger.error("SEARCH");
        Logger.error(json);
        AdSearchAsyncTask adSearchAsyncTask = new AdSearchAsyncTask();
        adSearchAsyncTask.execute(json);
    }


    private class AdSearchAsyncTask extends AsyncTask<String, Void, Void> {
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
                        advertisements = new ArrayList<Advertisement>();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            Advertisement advertisement = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Advertisement.class);
                            advertisements.add(advertisement);
                        }

                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                adAdapter.setAdvertisements(advertisements);
                                adAdapter.notifyDataSetChanged();
                                if(swipeRefreshLayout.isRefreshing())
                                    swipeRefreshLayout.setRefreshing(false);
                                loadingProgress.setVisibility(View.GONE);
                                loadingProgress.stopSpinning();
                            }


                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(final String errorString) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                            loadingProgress.setVisibility(View.GONE);
                            loadingProgress.stopSpinning();
                            Logger.error(errorString);
                            Log.println(Log.ASSERT, FragmentHomeListView.this.getClass().getSimpleName(), "onFailure");
                        }
                    });
                }
            });
            return null;
        }
    }

}
