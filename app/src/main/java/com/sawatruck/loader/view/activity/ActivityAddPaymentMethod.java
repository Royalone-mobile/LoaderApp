package com.sawatruck.loader.view.activity;

import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.sawatruck.loader.R;
import com.sawatruck.loader.view.design.CustomEditText;
import com.sawatruck.loader.view.design.FourDigitCardFormatWatcher;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ActivityAddPaymentMethod extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.edit_cardholdername) CustomEditText editCardHolderName;
    @Bind(R.id.edit_expiredate) CustomEditText editExpireDate;
    @Bind(R.id.edit_cardnumber) CustomEditText editCardNumber;
    @Bind(R.id.edit_CVV) CustomEditText editCVV;
    @Bind(R.id.btn_cancel) View btnCancel;
    @Bind(R.id.btn_charge) View btnCharge;
    @Bind(R.id.radio_creditcard) RadioButton radioCreditCard;
    @Bind(R.id.radio_paypal) RadioButton radioPayPal;
    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_add_payment_method,null);
        ButterKnife.bind(this, view);

        btnCharge.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        editCardNumber.addTextChangedListener(new FourDigitCardFormatWatcher());
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
        setAppTitle(getResources().getString(R.string.title_add_payment_method));
        showNavHome(false);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_charge:
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }
}
