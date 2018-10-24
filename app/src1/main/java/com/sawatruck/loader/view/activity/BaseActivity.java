package com.sawatruck.loader.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.sawatruck.loader.R;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.view.design.CustomTextView;

import java.util.List;

/**
 * Created by royalone on 2016-12-27.
 */

public abstract class BaseActivity extends AppCompatActivity {
    static String TAG = BaseActivity.class.getSimpleName();

    protected Context context;
    protected int mScreenWidth;
    protected int mScreenHeight;
    protected ProgressDialog pDialog;
    protected Toolbar toolbar;
    public static boolean active = false;
    public static boolean toggleState = false;  //false -- home true -- back
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        initLayout();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        initToolbar();
        Misc.applyLocale(this);
    }
    /**
     * init layout
     */
    private void initLayout() {
        View v = getContentView();
        setContentView(v);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void initToolbar()
    {
        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ico_home);
        CustomTextView toolbarTitle = (CustomTextView) toolbar.findViewById(R.id.toolbar_title);

        toolbarTitle.setBold(true);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void showNavHome(boolean bHome)
    {
        if(toolbar==null) return;
        if(bHome) {
            toolbar.setNavigationIcon(R.drawable.ico_home);
            toggleState = true;
        }
        else {
            toolbar.setNavigationIcon(R.drawable.ico_back);
            toggleState = false;
        }
    }

    public void showOptions(boolean bOption){
        if(toolbar==null) return;
        ImageView btnNotification  = (ImageView)findViewById(R.id.btn_notification);
        ImageView btnSearchFilter  = (ImageView)findViewById(R.id.btn_searchfilter);
        CustomTextView txtNotificationCount = (CustomTextView ) findViewById(R.id.txt_notification_count);

        ImageView btnAddAd  = (ImageView)findViewById(R.id.btn_add_load);
        if(bOption) {
            btnNotification.setVisibility(View.VISIBLE);
            btnSearchFilter.setVisibility(View.VISIBLE);
            txtNotificationCount.setVisibility(View.VISIBLE);
            btnAddAd.setVisibility(View.GONE);
        }
        else {
            btnNotification.setVisibility(View.GONE);
            btnSearchFilter.setVisibility(View.GONE);
            txtNotificationCount.setVisibility(View.GONE);
            btnAddAd.setVisibility(View.GONE);
        }
    }

    public void showAddLoad(){
        if(toolbar==null) return;
        ImageView btnAddAd  = (ImageView)findViewById(R.id.btn_add_load);
        btnAddAd.setVisibility(View.VISIBLE);
    }

    public void showOnlyNotificationMenu(){
        if(toolbar==null) return;
        ImageView btnNotification  = (ImageView)findViewById(R.id.btn_notification);
        ImageView btnSearchFilter  = (ImageView)findViewById(R.id.btn_searchfilter);

        btnNotification.setVisibility(View.VISIBLE);
        btnSearchFilter.setVisibility(View.GONE);

    }

    public void setAppTitle(String title){
        try {
            toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
            CustomTextView txtTitle = (CustomTextView) toolbar.findViewById(R.id.toolbar_title);
            txtTitle.setText(title);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected abstract View getContentView();

    protected void changeFragment(int id, Fragment fragment, String tag) {
        if (tag == null)
            getSupportFragmentManager().beginTransaction().replace(id, fragment).commit();
        else {
            getSupportFragmentManager().beginTransaction().replace(id, fragment, tag).commit();

        }
    }

    protected void changeFragment(int container, Fragment fragment) {
        changeFragment(container, fragment, null);
    }


    protected void clearFragments(@Nullable String tag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> allFragments = fragmentManager.getFragments();
        if (allFragments != null && allFragments.size() > 0) {

            for (Fragment frag : allFragments) {
                if (frag != null && (tag == null || (frag.getTag() != null && frag.getTag().equals(tag)))) {
                    fragmentManager.beginTransaction().remove(frag).commit();
                }
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * Back button
     */
    @Override
    public void onBackPressed() {
//        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
//        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
//
//        String activityName = this.getClass().getSimpleName();
//        if(activityName.equals(MainActivity.class.getSimpleName())){
//            if (mHits[0] >= SystemClock.uptimeMillis() - 1500) {
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("EXIT", true);
//                startActivity(intent);
//                super.onBackPressed();
//                return;
//            }
//            Toast.makeText(this, getString(R.string.error_press_again),Toast.LENGTH_LONG).show();
//        }
        super.onBackPressed();
    }


    /**
     * back button
     *
     * @param v
     */
    public void back(View v) {
        onBackPressed();
    }

    protected void showError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy(){

        super.onDestroy();
        active = false;
    }

    public void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Ready...");
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) {

            try {

                pDialog.show();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) {

            try {

                pDialog.dismiss();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        }
    }

    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        if(mainFragment != null)
//            getSupportFragmentManager().putFragment(outState, MAIN_FRAGMENT, mainFragment);
//        if(searchVideoGridFragment != null && searchVideoGridFragment.isVisible())
//            getSupportFragmentManager().putFragment(outState, SEARCH_FRAGMENT, searchVideoGridFragment);
//        if(genreFragment !=null && genreFragment.isVisible())
//            getSupportFragmentManager().putFragment(outState, GENRE_FRAGMENT, genreFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart(){
        super.onStart();
        active = true;
    }
    @Override
    public void onStop(){
        super.onStop();

    }
}
