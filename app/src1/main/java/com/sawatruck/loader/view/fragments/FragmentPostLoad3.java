package com.sawatruck.loader.view.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Currency;
import com.sawatruck.loader.entities.Driver;
import com.sawatruck.loader.entities.Load;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Notice;
import com.sawatruck.loader.utils.Serializer;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.activity.ActivityInviteDriver;
import com.sawatruck.loader.view.activity.BaseActivity;
import com.sawatruck.loader.view.activity.MainActivity;
import com.sawatruck.loader.view.adapter.DriverAdapter;
import com.sawatruck.loader.view.design.CustomEditText;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/19/2017.
 */


public class FragmentPostLoad3 extends BaseFragment implements View.OnClickListener {
    public static final String TAG = FragmentPostLoad3.class.getSimpleName();
    @Bind(R.id.btn_back) View btnBack;
    @Bind(R.id.btn_next) View btnNext;
    @Bind(R.id.edit_price) CustomEditText editPrice;
    @Bind(R.id.spinner_currency) BetterSpinner spinnerCurrency;
    @Bind(R.id.btn_invite_user) View btnInviteUser;
    @Bind(R.id.rv_drivers) RecyclerView rvDrivers;

    private ArrayList<Currency> currencies = new ArrayList<>();
    private ArrayList<Driver> drivers = new ArrayList<>();

    private Currency currentCurrency;
    private String postLoadContent;
    private DriverAdapter driverAdapter;
    private int loadTypeID = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_loads_step_three, container, false);
        ButterKnife.bind(this,view);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnInviteUser.setOnClickListener(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        rvDrivers.setLayoutManager(mLayoutManager);

        driverAdapter = new DriverAdapter(getContext());
        rvDrivers.setAdapter(driverAdapter);

        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.title_post_loads));
        initSpinnerCurrency();
        getCurrencies();

        spinnerCurrency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentCurrency = currencies.get(position);
            }
        });

        return view;
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
    public void onClick(View v) {
        int id =v.getId();
        MainActivity mainActivity = (MainActivity)getActivity();

        switch (id) {
            case R.id.btn_back:
                FragmentPostLoad2 fragmentPostLoad2 = new FragmentPostLoad2();
                fragmentPostLoad2.setPostLoadContent(postLoadContent);
                mainActivity.hideFragmentWithTag(FragmentPostLoad3.TAG);
                mainActivity.changeFragmentWithTag(fragmentPostLoad2, FragmentPostLoad2.TAG);
                break;

            case R.id.btn_next:
                submitFinal();
                break;

            case R.id.btn_invite_user:
                Intent intent = new Intent(getActivity(), ActivityInviteDriver.class);
                startActivityForResult(intent, Constant.INVITE_DRIVER_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constant.INVITE_DRIVER_REQUEST_CODE) {
            try {
                String json = data.getStringExtra("drivers");
                Logger.error("drivers");
                Logger.error(json);
                JSONArray jsonArray = new JSONArray(json);
                for(int i = 0 ; i<jsonArray.length(); i++){
                    Driver driver = Serializer.getInstance().deserializeDriver(jsonArray.getJSONObject(i).toString());
                    drivers.add(driver);
                }
                driverAdapter.setDrivers(drivers);
                driverAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void submitFinal(){
        if(editPrice.getText().toString().length() == 0) {
            editPrice.setError(getString(R.string.error_enter_price));
            editPrice.requestFocus();
            return;
        }

        if(currentCurrency == null) {
            Notice.show(getString(R.string.error_select_currency));
            return;
        }
        try {
            JSONObject postLoadObject = new JSONObject(postLoadContent);
            postLoadObject.put("Budget",editPrice.getText().toString());
            postLoadObject.put("CurrencyID",currentCurrency.getID());

            Logger.error("Post load with Details Json");
            Logger.error(postLoadObject.toString());


            PostLoadAsyncTask postLoadAsyncTask = new PostLoadAsyncTask();
            postLoadAsyncTask.execute(postLoadObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getLoadTypeID() {
        return loadTypeID;
    }

    public void setLoadTypeID(int loadTypeID) {
        this.loadTypeID = loadTypeID;
    }

    private class PostLoadAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];
            HttpUtil.postBody(Constant.POST_LOAD_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    if(responseBody==null) return;
                    String paramString = StringUtil.escapeString(responseBody);
                    try {
                        JSONObject jsonObject = new JSONObject(paramString);
                        JSONObject contentObject =  jsonObject.getJSONObject("Object");
                        Load load = BaseApplication.getGson().fromJson(contentObject.toString(), Load.class);
                        for(Driver driver:drivers) {
                            JSONObject inviteObject = new JSONObject();
                            inviteObject.put("DriverID", driver.getID());
                            inviteObject.put("LoadID", load.getID());
                            inviteObject.put("Message", "Test Invite Driver new");
                            InviteDriverAsyncTask inviteDriverAsyncTask = new InviteDriverAsyncTask();
                            inviteDriverAsyncTask.execute(inviteObject.toString());
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.selectLoadTab();
                                Misc.showResponseMessage(responseBody);
                            }
                        });

                    } catch (Exception e) {
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

    private class InviteDriverAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];
            HttpUtil.postBody(Constant.INVITE_DRIVER_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Misc.showResponseMessage(responseBody);
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


    public void getCurrencies(){
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(getContext()).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_CURRENCIES_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                currencies = new ArrayList<>();
                ArrayList<String> codeList = new ArrayList<>();
                int defaultCurrencyPosition = 0;
                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for(int j=0; j<jsonArray.length(); j++) {
                        Currency currency = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Currency.class);
                        currencies.add(currency);
                        codeList.add(currency.getCode());

                        if(currency.getID().equals(UserManager.with(getContext()).getCurrentUser().getDefaultCurrencyID())){
                            currentCurrency = currency;
                        }

                        defaultCurrencyPosition = j;
                    }

                    ArrayAdapter adapter = new ArrayAdapter(
                            getContext(),
                            R.layout.spinner_item,
                            codeList);

                    adapter.setDropDownViewResource(R.layout.spinner_select);
                    spinnerCurrency.setAdapter(adapter);


//                    if(currencies.size()>0) {
//                        CharSequence charSequence = currentCurrency.getCode();
//
//
//                        spinnerCurrency.setHint(UserManager.with(getActivity()).getCurrentUser().getDefaultCurrencyID());
//                        spinnerCurrency.setSelection(defaultCurrencyPosition);
//                        spinnerCurrency.setSelected(true);
//                    }
//
                    for(Currency currency :currencies){
                        if(currency.getID().equals(UserManager.with(getActivity()).getCurrentUser().getDefaultCurrencyID()))
                        {
                            currentCurrency = currency;
                            spinnerCurrency.setHint(currentCurrency.getCode());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }

    public void initSpinnerCurrency(){
        ArrayAdapter adapter = new ArrayAdapter(
                getContext(),
                R.layout.spinner_item,
                currencies);
        adapter.setDropDownViewResource(R.layout.spinner_select);
        spinnerCurrency.setAdapter(adapter);
    }


    public String getPostLoadContent() {
        return postLoadContent;
    }

    public void setPostLoadContent(String postLoadContent) {
        this.postLoadContent = postLoadContent;
    }
}
