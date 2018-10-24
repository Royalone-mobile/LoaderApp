package com.sawatruck.loader.view.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Driver;
import com.sawatruck.loader.entities.Load;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Serializer;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.view.adapter.SneakPeekAdapter;
import com.todddavies.components.progressbar.ProgressWheel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/22/2017.
 */


public class ActivitySneakPeek extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = ActivitySneakPeek.class.getSimpleName();
    @Bind(R.id.btn_invite_drivers) View btnInviteDrivers;
    @Bind(R.id.rc_container) RecyclerView rcContainer;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    SneakPeekAdapter sneakPeekAdapter;

    public void initView() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        sneakPeekAdapter = new SneakPeekAdapter(this);
        rcContainer.setAdapter(sneakPeekAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        btnInviteDrivers.setVisibility(View.GONE);

        btnInviteDrivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult();
            }
        });

    }

    public void setInviteVisibility(boolean bVisible){
        if(bVisible)
            btnInviteDrivers.setVisibility(View.VISIBLE);
        else
            btnInviteDrivers.setVisibility(View.GONE);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_sneakpeek, null, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        setAppTitle(getResources().getString(R.string.sneak_peek));
        showNavHome(false);

        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.startSpinning();
        sneakPeekAdapter.initializeAdapter();
        try {
            getSneakPeaks();
        } catch (Exception e) {
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
    public void onRefresh() {
        sneakPeekAdapter.initializeAdapter();
        try {
            getSneakPeaks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getSneakPeaks() throws JSONException {
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
        orderObject.put("Dir", "Asc");
        rootObject.put("Order", orderArray);

        JSONObject sneakPeakObject = new JSONObject();
        sneakPeakObject.put("Name", "SneakPeek");
        sneakPeakObject.put("Data", "");
        sneakPeakObject.put("Orderable", true);
        sneakPeakObject.put("Searchable", false);


        //TODO sneakpeek
        JSONObject searchObject1 = new JSONObject();

        Logger.error(" Sneak peek LOAD ID = " + getIntent().getStringExtra(Constant.INTENT_LOAD_ID));
        searchObject1.put("Value", getIntent().getStringExtra(Constant.INTENT_LOAD_ID));
//        searchObject1.put("Value", "B19ABE00-E936-4984-8AE1-00609F83ACDF");
        searchObject1.put("Regex", false);

        sneakPeakObject.put("Search", searchObject1);


        JSONArray columnsArray=  new JSONArray();
        columnsArray.put(sneakPeakObject);

        rootObject.put("Columns", columnsArray);

        String json = StringUtil.escapeString(rootObject.toString());
        SneakPeekAsyncTask sneakPeekAsyncTask = new SneakPeekAsyncTask();
        sneakPeekAsyncTask.execute(json);
    }

    private class SneakPeekAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.postBody(Constant.SNEAK_PEEK_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray jsonArray = (JSONArray) jsonObject.get("Data");
                        final ArrayList<Load> loads = new ArrayList<>();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            Load load = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Load.class);
                            loads.add(load);
                        }
                        sneakPeekAdapter.setLoads(loads);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Misc.showResponseMessage(responseBody);
                                sneakPeekAdapter.notifyDataSetChanged();
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
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Misc.showResponseMessage(errorString);
                                loadingProgress.setVisibility(View.GONE);
                                loadingProgress.stopSpinning();
                                if (swipeRefreshLayout.isRefreshing())
                                    swipeRefreshLayout.setRefreshing(false);
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



    public void finishWithResult() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        ArrayList<Load> loads = sneakPeekAdapter.getLoads();
        ArrayList<Boolean> invitationList = sneakPeekAdapter.getInvitationList();

        ArrayList<Driver> invitedDrivers = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i<invitationList.size(); i++){
            boolean bValue = invitationList.get(i);
            if(bValue){
                Driver driver = loads.get(i).getDriver();
                invitedDrivers.add(driver);
                String strDriver = Serializer.getInstance().serializeDriver(driver);
                try {
                    JSONObject jsonObject = new JSONObject(strDriver);
                    jsonArray.put(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        intent.putExtra("drivers", invitedDrivers.toString());
        finish();
    }


}

