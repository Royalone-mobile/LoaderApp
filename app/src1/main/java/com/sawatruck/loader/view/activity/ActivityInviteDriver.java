package com.sawatruck.loader.view.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Driver;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Serializer;
import com.sawatruck.loader.view.adapter.InviteDriverAdapter;
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

public class ActivityInviteDriver extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = ActivityInviteDriver.class.getSimpleName();
    @Bind(R.id.btn_invite_drivers) View btnInviteDrivers;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.rc_container) RecyclerView rcContainer;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;

    InviteDriverAdapter inviteDriverAdapter;

    public void initView() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        inviteDriverAdapter = new InviteDriverAdapter(this);
        rcContainer.setAdapter(inviteDriverAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        btnInviteDrivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult();
            }
        });
        btnInviteDrivers.setVisibility(View.GONE);
    }

    public void setInviteVisibility(boolean bVisible){
        if(bVisible)
            btnInviteDrivers.setVisibility(View.VISIBLE);
        else
            btnInviteDrivers.setVisibility(View.GONE);
    }

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_invite_driver, null, false);
        ButterKnife.bind(this, view);
        initView();

        setAppTitle(getResources().getString(R.string.invite_driver));
        showOptions(false);
        return view;
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
    public void onResume(){
        super.onResume();

        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.startSpinning();
        getDrivers();
        setAppTitle(getResources().getString(R.string.title_invite_drivers));
        showNavHome(false);
    }

    public void finishWithResult() {
        Intent intent = new Intent();

        ArrayList<Driver> drivers = inviteDriverAdapter.getDrivers();
        ArrayList<Boolean> invitationList = inviteDriverAdapter.getInvitationList();

        ArrayList<Driver> invitedDrivers = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i<invitationList.size(); i++){
            boolean bValue = invitationList.get(i);
            if(bValue){
                Driver driver = drivers.get(i);
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

        intent.putExtra("drivers", jsonArray.toString());
        setResult(RESULT_OK, intent);
        finish();
    }


    public void getDrivers(){
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Start", 0);
            jsonObject.put("Length", 20);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GetDriversAsyncTask getDriversAsyncTask = new GetDriversAsyncTask();
        getDriversAsyncTask.execute(jsonObject.toString());
    }

    @Override
    public void onRefresh() {
        inviteDriverAdapter.initializeAdapter();
        getDrivers();
    }


    private class GetDriversAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];
            HttpUtil.postBody(Constant.GET_DRIVER_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray jsonArray = (JSONArray) jsonObject.get("Data");
                        final ArrayList<Driver> drivers = new ArrayList<>();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            Driver driver = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Driver.class);
                            drivers.add(driver);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Misc.showResponseMessage(responseBody);
                                inviteDriverAdapter.setDrivers(drivers);
                                inviteDriverAdapter.notifyDataSetChanged();
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
                                if (swipeRefreshLayout.isRefreshing())
                                    swipeRefreshLayout.setRefreshing(false);
                                loadingProgress.setVisibility(View.GONE);
                                loadingProgress.stopSpinning();
                            }
                        });
                    }catch (Exception e ){
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
    }
}
