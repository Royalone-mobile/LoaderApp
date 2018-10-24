package com.sawatruck.loader.view.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Balance;
import com.sawatruck.loader.utils.ActivityUtil;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.activity.ActivityAddPaymentMethod;
import com.sawatruck.loader.view.activity.ActivityChargeBalance;
import com.sawatruck.loader.view.activity.ActivityTransactionHistory;
import com.sawatruck.loader.view.activity.BaseActivity;
import com.sawatruck.loader.view.design.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/19/2017.
 */


public class FragmentMenuBalance extends BaseFragment implements View.OnClickListener {
    public static final String TAG = FragmentMenuBalance.class.getSimpleName();

    @Bind(R.id.btn_transaction_history) View btnTransactionHistory;
    @Bind(R.id.btn_add_payment_method) View btnAddPayment;
    @Bind(R.id.btn_add_promo_code) View btnAddPromoCode;
    @Bind(R.id.btn_charge_balance) View btnChargeBalance;

    @Bind(R.id.txt_balance) CustomTextView txtBalance;
    @Bind(R.id.txt_point) CustomTextView txtPoint;
    @Bind(R.id.txt_promocode) CustomTextView txtPromoCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mybalance, container, false);
        ButterKnife.bind(this,view);

        BaseActivity baseActivity = (BaseActivity)getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.sidemenu_mybalance));
        baseActivity.showOptions(false);

        btnTransactionHistory.setOnClickListener(this);
        btnAddPayment.setOnClickListener(this);
        btnAddPromoCode.setOnClickListener(this);
        btnChargeBalance.setOnClickListener(this);

        txtBalance.setBold(true);
        txtPoint.setBold(true);
        txtPromoCode.setBold(true);


        return view;
    }

    public void getMyBalance(){
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(getContext()).getCurrentUser().getToken());
//        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, BaseApplication.getContext().getResources().getString(R.string.truck_token));

        httpUtil.getInstance().get(Constant.USER_GETBALANCE_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);
                try {
                    JSONObject jsonObject = new JSONObject(paramString);
                    JSONObject contentObject = jsonObject.getJSONObject("Object");
                    Balance balance = BaseApplication.getGson().fromJson(contentObject.toString(), Balance.class);

                    String strBalance = NumberFormat.getInstance().format(Double.valueOf(balance.getBalance()));

                    txtBalance.setText(balance.getCurrency() + " " + strBalance);

                    txtPoint.setText(NumberFormat.getInstance().format(Double.valueOf(balance.getPoints())));
                    txtPromoCode.setText(balance.getPromoCode());
                    UserManager.with(getContext()).setBalance(balance);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }
        });

    }

    @Override
    public void onResume()
    {
        super.onResume();
        getMyBalance();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_transaction_history:
                viewTransactionHistory();
                break;
            case R.id.btn_add_payment_method:
                addPaymentMethod();
                break;
            case R.id.btn_add_promo_code:
                addPromoCode();
                break;
            case R.id.btn_charge_balance:
                chargeBalance();
                break;
        }
    }

    public void viewTransactionHistory(){
        Intent intent = new Intent(getActivity(), ActivityTransactionHistory.class);
        ActivityUtil.goOtherActivityFlipTransition(getContext(), intent);
    }
    public void addPaymentMethod(){
        Intent intent = new Intent(getActivity(), ActivityAddPaymentMethod.class);
        ActivityUtil.goOtherActivityFlipTransition(getContext(), intent);
    }
    public void addPromoCode(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final View viewAddPromocode = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_promocode,null);

        final EditText editPromoCode = (EditText)viewAddPromocode.findViewById(R.id.edit_promocode);

        final Button btnOk = (Button)viewAddPromocode.findViewById(R.id.btn_ok);
        final Button btnCancel = (Button)viewAddPromocode.findViewById(R.id.btn_cancel);


        builder.setView(viewAddPromocode);

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
                if(editPromoCode.getText().length() == 0){
                    editPromoCode.setError(getString(R.string.enter_promocode));
                    editPromoCode.requestFocus();
                    return;
                }
                RequestParams requestParams = new RequestParams();
                requestParams.put("code", editPromoCode.getText().toString());
                HttpUtil.getInstance().post(Constant.ADD_PROMOCODE_API, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Misc.showResponseMessage(responseBody);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Misc.showResponseMessage(responseBody);
                    }
                });
                alertDialog.dismiss();
            }
        });
    }
    public void chargeBalance(){
        Intent intent = new Intent(getActivity(), ActivityChargeBalance.class);
        ActivityUtil.goOtherActivityFlipTransition(getContext(), intent);
    }

}
