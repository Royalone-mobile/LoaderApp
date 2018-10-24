package com.sawatruck.loader.view.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Load;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.adapter.LoadsAdapter;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/19/2017.
 */


public class FragmentLoadOpened extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_POSITION = "position";

    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.rc_container) RecyclerView rcContainer;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;
    private LoadsAdapter loadsAdapter;

    public static FragmentLoadOpened getInstance(int position) {
        FragmentLoadOpened f = new FragmentLoadOpened();
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

        showProgressDialog();
        return view;
    }

    public void initView(){

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        loadsAdapter = new LoadsAdapter(getContext(),1);
        rcContainer.setAdapter(loadsAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onResume() {
        super.onResume();

        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.startSpinning();

        loadsAdapter.initializeAdapter();
        getOpenedLoad();
    }

    public void getOpenedLoad(){
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(getContext()).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        RequestParams params = new RequestParams();
        params.put("status", String.valueOf(Constant.LoadStatus.Active));

        httpUtil.get(Constant.GET_MY_LOAD_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);

                    ArrayList<Load> loads = new ArrayList<>();

                        JSONArray jsonArray= new JSONArray(paramString);
                        for(int j=0; j<jsonArray.length(); j++) {
                            Load load = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Load.class);
                            loads.add(load);
                        }

                        loadsAdapter.setLoads(loads);
                        loadsAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] errorString, Throwable throwable) {
                Misc.showResponseMessage(errorString);
            }
            @Override
            public void onFinish(){
                try {
                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);
                    loadingProgress.setVisibility(View.GONE);
                    loadingProgress.stopSpinning();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onRefresh() {
        loadsAdapter.initializeAdapter();
        getOpenedLoad();
    }
}

