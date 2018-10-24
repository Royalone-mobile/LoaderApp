package com.sawatruck.loader.view.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Driver;
import com.sawatruck.loader.entities.Load;
import com.sawatruck.loader.entities.LoadPhoto;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Notice;
import com.sawatruck.loader.utils.Serializer;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.view.activity.ActivityInviteDriver;
import com.sawatruck.loader.view.activity.ActivityMap;
import com.sawatruck.loader.view.activity.ActivitySneakPeek;
import com.sawatruck.loader.view.design.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/22/2017.
 */

public class FragmentListingDetails extends BaseFragment{
    private static final String ARG_POSITION = "position";

    @Bind(R.id.btn_invite_driver) View btnInviteDriver;
    @Bind(R.id.btn_sneakpeek) View btnSneakPeek;
    @Bind(R.id.btn_showmap) Button btnShowMap;
    @Bind(R.id.txt_load_date) CustomTextView txtLoadDate;
    @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;
    @Bind(R.id.txt_distance) CustomTextView txtDistance;
    @Bind(R.id.txt_load_details) CustomTextView txtLoadDetails;
    @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
    @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
    @Bind(R.id.txt_load_name) CustomTextView txtLoadName;
    @Bind(R.id.txt_load_type) CustomTextView txtLoadType;
    @Bind(R.id.img_load_photo) ImageView imgLoadPhoto;
    @Bind(R.id.txt_budget_price) CustomTextView txtBudgetPrice;


    Load load;
    public static final String TAG = FragmentListingDetails.class.getSimpleName();
    public static FragmentListingDetails getInstance(int position) {
        FragmentListingDetails f = new FragmentListingDetails();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing_details, container, false);
        ButterKnife.bind(this,view);

        btnInviteDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityInviteDriver.class);
                startActivityForResult(intent, Constant.INVITE_DRIVER_REQUEST_CODE);
            }
        });

        btnSneakPeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(load==null) {
                    Notice.show(R.string.error_load_null);
                    return;
                }
                Intent intent = new Intent(getActivity(), ActivitySneakPeek.class);
                intent.putExtra(Constant.INTENT_LOAD_ID , load.getID());
                startActivityForResult(intent, Constant.INVITE_DRIVER_REQUEST_CODE);
            }
        });

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowMap();
            }
        });

        initView();

        return view;
    }

    public void onShowMap(){
        if(load==null) return;
        Intent intent = new Intent(getContext(), ActivityMap.class);
        String fromLocation = Serializer.getInstance().serializeLocation(load.getFromLocation());
        String toLocation  = Serializer.getInstance().serializeLocation(load.getToLocation());
        intent.putExtra(Constant.INTENT_FROM_LOCATION, fromLocation);
        intent.putExtra(Constant.INTENT_TO_LOCATION, toLocation);
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(load ==null) return;
        if(requestCode == Constant.INVITE_DRIVER_REQUEST_CODE) {
            try {
                String json = data.getStringExtra("drivers");
                JSONArray jsonArray = new JSONArray(json);
                for(int i = 0 ; i<jsonArray.length(); i++){
                    Driver driver = Serializer.getInstance().deserializeDriver(jsonArray.getJSONObject(i).toString());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("DriverID", driver.getID());
                    jsonObject.put("LoadID", load.getID());
                    jsonObject.put("Message", "Test Invite Driver new");
                    InviteDriverAsyncTask inviteDriverAsyncTask = new InviteDriverAsyncTask();
                    inviteDriverAsyncTask.execute(jsonObject.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public void initView(){
        String loadId = getActivity().getIntent().getStringExtra("load_id");
        RequestParams params = new RequestParams();
        params.put("id", loadId);
        HttpUtil.getInstance().get(Constant.GET_LOADS_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);
                try{
                    JSONObject jsonObject = new JSONObject(paramString);
                    JSONObject contentObject = jsonObject.getJSONObject("Object");
                    load = BaseApplication.getGson().fromJson(contentObject.toString(), Load.class);
                    txtBudgetPrice.setText(load.getBudget() + " " + load.getCurrency());
                    txtDeliveryDate.setText(load.getUnloadDateEnd());
                    txtLoadDate.setText(load.getLoadDateFrom());
                    txtLoadName.setText(load.getName());
                    txtLoadLocation.setText(load.getFromLocation().getCityName() + ", " + load.getFromLocation().getCountryName());
                    txtDeliveryLocation.setText(load.getToLocation().getCityName() + ", " + load.getToLocation().getCountryName());
                    txtLoadType.setText(load.getLoadType());
                    txtLoadDetails.setText(load.getLoadDetails());
                    txtDistance.setText(load.getDistance().concat(getString(R.string.kilometer)));
                    if(load.getLoadPhotos().size()>0) {
                        LoadPhoto loadPhoto = load.getLoadPhotos().get(0);
                        BaseApplication.getPicasso().load(loadPhoto.getPhotoPath()).placeholder(R.drawable.ico_truck).into(imgLoadPhoto);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }
}
