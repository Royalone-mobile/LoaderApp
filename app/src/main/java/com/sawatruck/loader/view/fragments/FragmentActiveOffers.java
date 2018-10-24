package com.sawatruck.loader.view.fragments;

import android.app.Activity;
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
import com.sawatruck.loader.entities.Offer;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.adapter.OffersAdapter;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/22/2017.
 */


public class FragmentActiveOffers extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_POSITION = "position";
    public static final String TAG = FragmentActiveOffers.class.getSimpleName();
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.rc_container) RecyclerView rcContainer;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;

    OffersAdapter offersAdapter;

    public static FragmentActiveOffers getInstance(int position) {
        FragmentActiveOffers f = new FragmentActiveOffers();
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
        return view;
    }

    public void initView(){
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        offersAdapter = new OffersAdapter(getActivity());
        rcContainer.setAdapter(offersAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

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
        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.startSpinning();
        offersAdapter.initializeAdapter();
        try {
            getMyLoadOffers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {
        offersAdapter.initializeAdapter();
        try {
            getMyLoadOffers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void getMyLoadOffers() throws JSONException {
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(getContext()).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        String loadId = getActivity().getIntent().getStringExtra(Constant.INTENT_LOAD_ID);
        RequestParams params = new RequestParams();
        params.put("id", loadId);

        httpUtil.get(Constant.GET_OFFERS_BY_LOAD_ID_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final byte[] responseBody) {
                String paramString = new String(responseBody);

                try {
                    JSONObject jsonObject = new JSONObject(paramString);
                    JSONArray jsonArray = (JSONArray) jsonObject.get("Object");
                    final ArrayList<Offer> offers = new ArrayList<>();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        Offer offer = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Offer.class);
                        if(offer.getStatus() == 1)
                            offers.add(offer);
                    }

                    offersAdapter.setOffers(offers);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingProgress.setVisibility(View.GONE);
                            loadingProgress.stopSpinning();
                            offersAdapter.notifyDataSetChanged();
                            if(swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, final byte[] errorString, Throwable throwable) {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingProgress.setVisibility(View.GONE);
                            loadingProgress.stopSpinning();

                            Misc.showResponseMessage(errorString);
                            if(swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
