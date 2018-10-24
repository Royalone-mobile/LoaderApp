package com.sawatruck.loader.view.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.controller.RegionCodeAPI;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.AppSettings;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Notice;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.design.CustomEditText;

import net.igenius.customcheckbox.CustomCheckBox;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
/**
 * Created by royal on 8/19/2017.
 */

public class ActivitySignUp extends BaseActivity implements View.OnClickListener ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    @Bind(R.id.btn_signup) View btnSignUp;
    @Bind(R.id.edit_firstname) CustomEditText editFirstName;
    @Bind(R.id.edit_lastname) CustomEditText editLastName;
    @Bind(R.id.edit_email) CustomEditText editEmail;
    @Bind(R.id.edit_password) CustomEditText editPassword;
    @Bind(R.id.edit_phonecode) CustomEditText editPhoneCode;
    @Bind(R.id.edit_phonenumber) CustomEditText editPhoneNumber;
    @Bind(R.id.btn_signup_facebook) View btnSignUpFacebook;
    @Bind(R.id.btn_signup_google) View btnSignUpGoogle;
    @Bind(R.id.chk_accept_terms) CustomCheckBox chkAcceptTerms;


    public static final int RC_SIGN_UP = 100;
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;
    private boolean signedInUser;

    private CallbackManager callbackManager;

    private GoogleApiClient mGoogleApiClient;


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
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_signup,null);
        ButterKnife.bind(this, view);
        btnSignUp.setOnClickListener(this);
        btnSignUpFacebook.setOnClickListener(this);
        btnSignUpGoogle.setOnClickListener(this);

		chkAcceptTerms.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                if(chkAcceptTerms.isChecked())
                    btnSignUp.setEnabled(true);
                else
                    btnSignUp.setEnabled(false);
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("738995436229-shkkc8p17cdk7a7qqb6fg2c256rn162b.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getString(R.string.title_signup));
        setRegionCode();
        String countryCode = Misc.getCountryDialCode();
        editPhoneCode.setText("+" + countryCode);
        showNavHome(false);

        CallbackManager callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginResult.getAccessToken().getUserId();
                AccessToken accessToken = loginResult.getAccessToken();
                final Bundle[] bFacebookData = new Bundle[1];
                Log.d(TAG, "Facebook success");
                Toast.makeText(context, Profile.getCurrentProfile().getFirstName(), Toast.LENGTH_LONG).show();
                Log.i(TAG, Profile.getCurrentProfile().getName());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                    }
                });

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_signup:
                signUp();
                break;
            case R.id.btn_signup_google:
                googlePlusLogin();
                break;
            case R.id.btn_signup_facebook:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(signedInUser ){
        }
        signedInUser = false;
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            // store mConnectionResult
            mConnectionResult = connectionResult;

            if (signedInUser) {
                resolveSignInError();
                signedInUser = false;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == RC_SIGN_UP) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Status ss = result.getStatus();
        GoogleSignInAccount acct = result.getSignInAccount();
        if (result.isSuccess()) {
            final String id = acct.getId();
            String personName = acct.getDisplayName();
            String email = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();


        } else {

        }
    }

    private void googlePlusLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_UP);
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_UP);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public void signUp(){
        if(editFirstName.getText().toString().length() == 0){
            editFirstName.setError(getString(R.string.error_enter_firstname));
            editFirstName.requestFocus();
            return;
        }
        if(editLastName.getText().toString().length() == 0){
            editLastName.setError(getString(R.string.error_enter_lastname));
            editLastName.requestFocus();
            return;
        }
        if(editEmail.getText().toString().length() == 0){
            editEmail.setError(getString(R.string.error_enter_email));
            editEmail.requestFocus();
            return;
        }
        if(editPassword.getText().toString().length() == 0){
            editPassword.setError(getString(R.string.error_enter_password));
            editPassword.requestFocus();
            return;
        }
        if(editPhoneNumber.getText().toString().length() == 0){
            editPhoneNumber.setError(getString(R.string.error_enter_phonenumber));
            editPhoneNumber.requestFocus();
            return;
        }
        if(!chkAcceptTerms.isChecked()) {
            Notice.show(getString(R.string.error_accept_terms));
            return;
        }


        String countryCode = Misc.getCountryDialCode();
        final RequestParams params = new RequestParams();

        final String strEmail = editEmail.getText().toString();

        if(!Misc.isEmailValid(strEmail)){
            editEmail.setError(getString(R.string.error_invalid_email));
            editEmail.requestFocus();
            return;
        }

        params.put("FirstName", editFirstName.getText().toString());
        params.put("LastName", editLastName.getText().toString());
        params.put("Email", editEmail.getText().toString());
        params.put("Password", editPassword.getText().toString());
        params.put("PhoneNumber", editPhoneNumber.getText().toString());
        params.put("MobileCountryID", countryCode);
        params.put("NationalityID", countryCode);
        params.put("LivingCountryID", countryCode);
        params.put("TermsAccepted", String.valueOf(chkAcceptTerms.isChecked()));
        params.put("UserName", editEmail.getText().toString());

        Logger.error(params.toString());

        btnSignUp.setEnabled(false);
        HttpUtil.getInstance().post(Constant.USER_SIGNUPLOADER_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                Logger.error(paramString);

                try {
                    JSONObject jsonObject = new JSONObject(paramString).getJSONObject("Object");
                    JSONObject userObject =  jsonObject.getJSONObject("AccessToken");
                    User user = BaseApplication.getGson().fromJson(userObject.toString(), User.class);

                    if(user!=null){

                        user.setToken(user.getTokenType() + " " + user.getToken());
                        UserManager.with(getApplicationContext()).setCurrentUser(user);
                        BaseApplication.getInstance().reconfigHub();
                    }
                }catch (Exception e) {

                }
                sendSMS(strEmail);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
                btnSignUp.setEnabled(true);
            }
        });
    }

    public void sendSMS(final String email) {
        RequestParams params = new RequestParams();
        params.put("Email", email);

        HttpUtil.getInstance().post(Constant.CONFIRM_PHONE_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Misc.showResponseMessage(responseBody);
                Intent intent = new Intent(ActivitySignUp.this, ActivityVerifyAccount.class);
                intent.putExtra("email", email);
                startActivity(intent);
                ActivitySignUp.this.finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorString, Throwable error) {
                Misc.showResponseMessage(errorString);
                btnSignUp.setEnabled(true);
            }
        });
    }


    public void setRegionCode(){
        RegionCodeAPI regionCodeAPI = RegionCodeAPI.getInstance();
        regionCodeAPI.setContext(this);
        regionCodeAPI.execute();
    }
}
