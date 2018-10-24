package com.sawatruck.loader.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.paypal.android.sdk.payments.PayPalService;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.AddressDetail;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.AppSettings;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.NetUtil;
import com.sawatruck.loader.utils.Serializer;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.adapter.DrawerAdapter;
import com.sawatruck.loader.view.design.CircularImage;
import com.sawatruck.loader.view.design.CustomTextView;
import com.sawatruck.loader.view.design.DrawerOption;
import com.sawatruck.loader.view.fragments.FragmentMenuBalance;
import com.sawatruck.loader.view.fragments.FragmentMenuBooking;
import com.sawatruck.loader.view.fragments.FragmentMenuHome;
import com.sawatruck.loader.view.fragments.FragmentMenuInBox;
import com.sawatruck.loader.view.fragments.FragmentMenuLoad;
import com.sawatruck.loader.view.fragments.FragmentMenuRating;
import com.sawatruck.loader.view.fragments.FragmentMenuSetting;
import com.sawatruck.loader.view.fragments.FragmentMenuToDo;
import com.sawatruck.loader.view.fragments.FragmentPostLoad;

import org.json.JSONArray;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.fragment_container) FrameLayout fragmentContainer;
    private DrawerOption[] mValues;
    private DrawerAdapter mAdapter;
    private static ListView _DrawerListView;
    private Bundle _savedInstanceState;
    private static MainActivity _instance;
    private long[] mHits = new long[2];
    private View sideMenu;
    private FragmentMenuHome homeFragment;
    private FragmentMenuInBox inboxFragment;
    private FragmentMenuBalance myBalanceFragment;
    private FragmentMenuBooking myBidsFragment;
    private FragmentMenuLoad myAdsFragment;
    private FragmentMenuRating myRatingFragment;
    private FragmentMenuSetting settingFragment;
    private SlidingMenu slidingMenu;
    private FragmentMenuToDo fragmentMenuToDo;


    public static MainActivity getInstance(){
        return _instance;
    }

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_main,null);
        ButterKnife.bind(this, view);

        _instance = this;
        Fabric.with(this, new Crashlytics());

        requestCameraPermission();
        requestReadStoragePermission();
        requestLocationPermission();
        requestCallPermission();
        NetUtil.turnGPSOn();

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, Constant.paypalConfig);
        startService(intent);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        int userType = UserManager.with(this).getUserType();

        if(userType == 0) {
            setupGuestNavigationBar();
            FragmentMenuHome homeFragment = new FragmentMenuHome();
            inflateFragmentWithTag(homeFragment, FragmentMenuHome.TAG);
        }
        else {
            showNavHome(true);
            setupNavigationBar();
            setupFragments();
        }

        return view;
    }


    private void fetchGetToDo() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(context).getCurrentUser().getToken());

        httpUtil.get(Constant.GET_TO_DO_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    JSONArray jsonArray= new JSONArray(paramString);

                    Logger.error("todo size = " + jsonArray.length());
                    mAdapter.getItem(6).setCount(jsonArray.length());
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    @Override
    public void onDestroy(){
        stopService(new Intent(this, PayPalService.class));
        _instance = null;
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();

        if (mHits[0] >= SystemClock.uptimeMillis() - 1000) {
                super.onBackPressed();
            }
        else
            Toast.makeText(this, getString(R.string.error_press_again), Toast.LENGTH_SHORT).show();
    }

    public static void finishSelf() {
        if(_instance != null)
            _instance.finish();
    }



    public static void updateNavigationBar(){
        if(_instance == null) return;
        CircularImage navAvatar = (CircularImage) _instance.sideMenu.findViewById(R.id.nav_user_avatar);
        CustomTextView navUserName = (CustomTextView) _instance.sideMenu.findViewById(R.id.nav_user_name);
        navUserName.setBold(true);

        User user = UserManager.with(_instance).getCurrentUser();

        try {
            BaseApplication.getPicasso().load(user.getPhotoUrl()).placeholder(R.drawable.ico_user).fit().into(navAvatar);
        }catch (Exception e){
            e.printStackTrace();
        }
        navUserName.setText(user.getFullName());
    }

    public void setupGuestNavigationBar(){
        slidingMenu = new SlidingMenu(this);

        if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_arabic))){
            slidingMenu.setMode(SlidingMenu.RIGHT);
            slidingMenu.setTouchModeAbove(SlidingMenu.RIGHT);
        }
        else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_english))){
            slidingMenu.setMode(SlidingMenu.LEFT);
            slidingMenu.setTouchModeAbove(SlidingMenu.LEFT);
        }


        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeEnabled(true);

        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW );
        View sideMenuGuest = getLayoutInflater().inflate(R.layout.sidemenu_guest, null, false);
        CustomTextView btnLogIn = (CustomTextView) sideMenuGuest.findViewById(R.id.btn_login);
        CustomTextView btnCreateAccount = (CustomTextView) sideMenuGuest.findViewById(R.id.btn_create_account);
        CustomTextView txtNoticeGuest = (CustomTextView)sideMenuGuest.findViewById(R.id.txt_notice_guest);
        CustomTextView txtVersion = (CustomTextView)sideMenuGuest.findViewById(R.id.txt_version);

        txtVersion.setText(Misc.getVersionName(BaseApplication.getContext()));


        txtNoticeGuest.setText(Html.fromHtml(getString(R.string.notice_guest)));

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseApplication.getContext(), ActivitySignIn.class);
                startActivity(intent);
                finish();
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseApplication.getContext(), ActivitySignUp.class);
                startActivity(intent);
                finish();
            }
        });
        slidingMenu.setMenu(sideMenuGuest);

    }
    public void setupNavigationBar(){
        slidingMenu = new SlidingMenu(this);
        if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_arabic))){
            slidingMenu.setMode(SlidingMenu.RIGHT);
            slidingMenu.setTouchModeAbove(SlidingMenu.RIGHT);
        }
        else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_english))){
            slidingMenu.setMode(SlidingMenu.LEFT);
            slidingMenu.setTouchModeAbove(SlidingMenu.LEFT);
        }

        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeEnabled(true);

        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        sideMenu = getLayoutInflater().inflate(R.layout.sidemenu, null, false);
        slidingMenu.setMenu(sideMenu);

        _DrawerListView = (ListView)sideMenu.findViewById(R.id.nav_menulist);
        _DrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onNavItemSelected(position);
            }
        });


        CircularImage navAvatar = (CircularImage) sideMenu.findViewById(R.id.nav_user_avatar);
        CustomTextView navUserName = (CustomTextView) sideMenu.findViewById(R.id.nav_user_name);
        navUserName.setBold(true);

        navAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityViewProfile.class);
                startActivity(intent);
            }
        });
        navUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityViewProfile.class);
                startActivity(intent);
            }
        });


        mValues = new DrawerOption[]{
                new DrawerOption(getString(R.string.sidemenu_home), R.drawable.ico_sidemenu_home, 0),
                new DrawerOption(getString(R.string.sidemenu_my_loads), R.drawable.ico_sidemenu_loads, 0),
                new DrawerOption(getString(R.string.sidemenu_my_bookings), R.drawable.ico_sidemenu_ad, 0),
                new DrawerOption(getString(R.string.sidemenu_mybalance), R.drawable.ico_dollar, 0),
                new DrawerOption(getString(R.string.sidemenu_inbox), R.drawable.ico_inbox, 0),
                new DrawerOption(getString(R.string.sidemenu_myrating), R.drawable.ico_rating, 0),
                new DrawerOption(getString(R.string.sidemenu_todo), R.drawable.ico_sidemenu_truck, 0),
                new DrawerOption(getString(R.string.sidemenu_settings), R.drawable.ico_setting, 0),
                new DrawerOption(getString(R.string.sidemenu_logout), R.drawable.ico_logout, 0)
        };

        mAdapter = new DrawerAdapter(this,mValues);
        _DrawerListView.setAdapter(mAdapter);
        _DrawerListView.setItemChecked(0, true);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(homeFragment != null && homeFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuHome.TAG, homeFragment);
        if(inboxFragment != null && inboxFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuInBox.TAG, inboxFragment);
        if(myBalanceFragment != null && myBalanceFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuBalance.TAG, myBalanceFragment);
        if(myBidsFragment != null && myBidsFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuBooking.TAG, myBidsFragment);
        if(myAdsFragment != null && myAdsFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuLoad.TAG, myAdsFragment);
        if(myRatingFragment != null && myRatingFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuRating.TAG, myRatingFragment);
        if(settingFragment != null && settingFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuSetting.TAG, settingFragment);
        if(fragmentMenuToDo != null && fragmentMenuToDo.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuToDo.TAG, fragmentMenuToDo);
        super.onSaveInstanceState(outState);
    }

    public void setupFragments(){
        if(fragmentContainer != null) {
            if(_savedInstanceState != null) {
                homeFragment = (FragmentMenuHome) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuHome.TAG);
                inboxFragment = (FragmentMenuInBox) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuInBox.TAG);
                myBalanceFragment = (FragmentMenuBalance) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuBalance.TAG);
                myBidsFragment = (FragmentMenuBooking) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuBooking.TAG);
                myAdsFragment = (FragmentMenuLoad) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuLoad.TAG);
                myRatingFragment = (FragmentMenuRating) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuRating.TAG);
                settingFragment = (FragmentMenuSetting) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuSetting.TAG);
                fragmentMenuToDo = (FragmentMenuToDo) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuToDo.TAG);
            }

            homeFragment = new FragmentMenuHome();
            if(homeFragment == null)
                homeFragment = new FragmentMenuHome();
            if(inboxFragment == null)
                inboxFragment = new FragmentMenuInBox();
            if(myBalanceFragment == null)
                myBalanceFragment = new FragmentMenuBalance();
            if(myBidsFragment == null)
                myBidsFragment = new FragmentMenuBooking();
            if(myAdsFragment == null)
                myAdsFragment = new FragmentMenuLoad();
            if(myRatingFragment == null)
                myRatingFragment = new FragmentMenuRating();
            if(settingFragment == null)
                settingFragment = new FragmentMenuSetting();
            if(fragmentMenuToDo == null)
                fragmentMenuToDo = new FragmentMenuToDo();

            inflateFragmentWithTag(homeFragment, FragmentMenuHome.TAG);
        }
    }

    public void showSlidingMenu(){
        if (!slidingMenu.isMenuShowing())
            slidingMenu.showMenu(false);
    }
    public void hideSlidingMenu(){
        if(slidingMenu.isMenuShowing())
            slidingMenu.showContent();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                if(toggleState) {
                    super.onBackPressed();
                }
                else {
                    showSlidingMenu();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void inflateFragmentWithTag(Fragment fragment,String TAG) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment,TAG);
        transaction.commitAllowingStateLoss();
    }

    public void addFragmentWithTag(Fragment fragment,String TAG) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragment,TAG);
        transaction.commitAllowingStateLoss();
    }

    public void removeFragmentWithTag(Fragment fragment,String TAG) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(getSupportFragmentManager().findFragmentByTag(TAG));
        transaction.commitAllowingStateLoss();
    }

    public void showFragmentWithTag(String TAG) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.show(getSupportFragmentManager().findFragmentByTag(TAG));
        transaction.commitAllowingStateLoss();
    }

    public void hideFragmentWithTag(String TAG) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(getSupportFragmentManager().findFragmentByTag(TAG));
        transaction.commitAllowingStateLoss();
    }

    public void changeFragmentWithTag(Fragment fragment, String TAG) {
        if(getSupportFragmentManager().findFragmentByTag(TAG) != null) {
            Logger.error("showFragmentWithTag");
            showFragmentWithTag(TAG);
        }
        else {
            Logger.error("addFragmentWithTag");
            addFragmentWithTag(fragment, TAG);
        }
    }


    public void onNavItemSelected(int position)
    {
        hideSlidingMenu();
        switch(position){
            case 0:
                inflateFragmentWithTag(homeFragment, FragmentMenuHome.TAG);
                break;
            case 1:
                inflateFragmentWithTag(myAdsFragment, FragmentMenuLoad.TAG);
                break;
            case 2:
                inflateFragmentWithTag(myBidsFragment, FragmentMenuBooking.TAG);
                break;
            case 3:
                inflateFragmentWithTag(myBalanceFragment, FragmentMenuBalance.TAG);
                break;
            case 4:
                inflateFragmentWithTag(inboxFragment, FragmentMenuInBox.TAG);
                break;
            case 5:
                inflateFragmentWithTag(myRatingFragment, FragmentMenuRating.TAG);
                break;
            case 6:
                inflateFragmentWithTag(fragmentMenuToDo, FragmentMenuToDo.TAG);
                break;
            case 7:
                inflateFragmentWithTag(settingFragment, FragmentMenuSetting.TAG);
                break;
            case 8:
                signOut();
                break;
        }
    }

    public void signOut() {
        User user = new User();
        UserManager.with(this).setUserType(0);
        UserManager.with(this).setCurrentUser(user);
        finish();
        Intent intent = new Intent(this, ActivitySplash.class);
        startActivity(intent);
        NotificationManager mNotificationManager =
                (NotificationManager) BaseApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        ShortcutBadger.removeCount(BaseApplication.getContext());

    }

    private void requestLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions((Activity)this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    11); // Last parameter is requestCode
            return;
        }

    }
    @Override
    public void onResume(){
        super.onResume();

        _instance = this;

        fetchGetToDo();
        Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        ImageView btnNotification = (ImageView)toolbar.findViewById(R.id.btn_notification);
        ImageView btnSearchFilter = (ImageView) toolbar.findViewById(R.id.btn_searchfilter);
        btnNotification.setOnClickListener(this);
        btnSearchFilter.setOnClickListener(this);

        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                getNotifications();

                BaseApplication._application.getLocation();

                BaseApplication.getHandler().postAtTime(this, System.currentTimeMillis() + Constant.LOCATION_TRACK_INTERVAL);
                BaseApplication.getHandler().postDelayed(this, Constant.LOCATION_TRACK_INTERVAL);

            }
        });

        if(sideMenu == null) return;
        CircularImage navAvatar = (CircularImage) sideMenu.findViewById(R.id.nav_user_avatar);
        CustomTextView navUserName = (CustomTextView) sideMenu.findViewById(R.id.nav_user_name);
        navUserName.setBold(true);

        User user = UserManager.with(this).getCurrentUser();

        try {
            BaseApplication.getPicasso().load(user.getPhotoUrl()).placeholder(R.drawable.ico_user).fit().into(navAvatar);
        }catch (Exception e){
            e.printStackTrace();
        }


        navUserName.setText(user.getFullName());
    }

    @Override
    public void onClick(View v) {
        int id =v.getId();
        Intent intent;
        switch (id) {
            case R.id.btn_notification:
                intent = new Intent(BaseApplication.getContext(), ActivityNotification.class);
                startActivity(intent);
                break;
            case R.id.btn_searchfilter:
                intent = new Intent(BaseApplication.getContext(), ActivitySearchFilter.class);
                startActivity(intent);
                break;
        }
    }

    public static void selectLoadTab(){
        if(getInstance() !=null)
            getInstance().onNavItemSelected(1);
    }

    public static void selectAdTab(){
        if(getInstance() !=null)
            getInstance().onNavItemSelected(2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) return;
        AddressDetail addressDetail = new AddressDetail();
        try {
            String json = data.getStringExtra("address");
            addressDetail = Serializer.getInstance().deserializeAddressDetail(json);
            switch (requestCode) {
                case Constant.requestLoadLocationCode:
                    FragmentPostLoad.setPickupLocation(addressDetail);
                    break;
                case Constant.requestUnLoadLocationCode:
                    FragmentPostLoad.setDeliveryLocation(addressDetail);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestReadStoragePermission()
    {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                }
            }
        }

    }

    public void requestCameraPermission() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            1);

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            1);
                }
            }
        }

    }

    public  boolean requestCallPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


    private void getNotifications() {
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(this).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_NOTIFICATIONS_UNSEEN_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);
//                Logger.error("----get notifications----");
//                Logger.error(paramString);
                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
                    CustomTextView txtNotificationCount = (CustomTextView) toolbar.findViewById(R.id.txt_notification_count);
                    txtNotificationCount.setText(String.valueOf(jsonArray.length()));

                    AppSettings.with(BaseApplication.getContext()).setNotificationCount(jsonArray.length());
                    ShortcutBadger.applyCount(BaseApplication.getContext(),jsonArray.length());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }
}

