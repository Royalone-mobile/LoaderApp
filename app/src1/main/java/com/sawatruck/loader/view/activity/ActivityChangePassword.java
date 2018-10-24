package com.sawatruck.loader.view.activity;

import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.CancelReason;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Notice;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.design.CustomEditText;
import com.sawatruck.loader.view.design.CustomTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class ActivityChangePassword extends BaseActivity {
    @Bind(R.id.edit_currentpassword)
    CustomEditText editCurrentPassword;
    @Bind(R.id.edit_newpassword)
    CustomEditText editNewPassword;
    @Bind(R.id.edit_confirmpassword)
    CustomEditText editConfirmPassword;
    @Bind(R.id.btn_changePassword)
    CustomTextView btnChangePassword;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_changepassword,null);
        ButterKnife.bind(this, view);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
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
        setAppTitle(getResources().getString(R.string.title_change_password));
        showNavHome(false);
    }


    private void changePassword(){
        String strCurrentPassword = editCurrentPassword.getText().toString();
        String strNewPassword = editNewPassword.getText().toString();
        String strConfirmPassword = editConfirmPassword.getText().toString();

        if(!strNewPassword.equals("")) {
            if (!strNewPassword.equals(strConfirmPassword)) {
                Notice.show(R.string.password_match_error);
                return;
            }
        }
        RequestParams requestParams = new RequestParams();
        requestParams.put("OldPassword",strCurrentPassword);
        requestParams.put("NewPassword", strNewPassword);

        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(this).getCurrentUser();

        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        btnChangePassword.setEnabled(false);
        httpUtil.post(Constant.USER_CHANGEPASSWORD_API, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Misc.showResponseMessage( responseBody);
                    finish();
                    btnChangePassword.setEnabled(true);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Misc.showResponseMessage(responseBody);
                    btnChangePassword.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
