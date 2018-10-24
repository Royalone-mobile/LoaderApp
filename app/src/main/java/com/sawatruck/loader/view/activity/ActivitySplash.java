package com.sawatruck.loader.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.R;
import com.sawatruck.loader.controller.RegionCodeAPI;
import com.sawatruck.loader.utils.AppSettings;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.design.CustomTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/19/2017.
 */

public class ActivitySplash extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.btn_post_loads) View btnPostLoad;
    @Bind(R.id.btn_search_ads) View btnSearchAds;
    @Bind(R.id.txt_version) CustomTextView txtVersion;
    @Bind(R.id.btn_spanish) View btnSpanish;
    @Bind(R.id.btn_english) View btnEnglish;
    @Bind(R.id.btn_arabic) View btnArabic;
    @Bind(R.id.layout_languages) View layoutLanguage;
    @Bind(R.id.layout_modes) View layoutMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        setRegionCode();

        btnEnglish = findViewById(R.id.btn_english);
        btnSpanish = findViewById(R.id.btn_spanish);
        btnArabic = findViewById(R.id.btn_arabic);
        txtVersion = (CustomTextView)findViewById(R.id.txt_version);
        btnPostLoad = findViewById(R.id.btn_post_loads);
        btnSearchAds = findViewById(R.id.btn_search_ads);
        layoutMode = findViewById(R.id.layout_modes);
        layoutLanguage = findViewById(R.id.layout_languages);

        btnSpanish.setOnClickListener(this);
        btnEnglish.setOnClickListener(this);
        btnArabic.setOnClickListener(this);

        txtVersion.setText(Misc.getVersionName(BaseApplication.getContext()));

        int userType = UserManager.with(this).getUserType();
        if(userType == 1) {
            goHome();
            return;
        }

        boolean bLanguageDefined = AppSettings.with(BaseApplication.getContext()).getLanguageDefined();
        if(bLanguageDefined)
            layoutLanguage.setVisibility(View.GONE);
        else
            layoutMode.setVisibility(View.GONE);
    }


    public void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onResume(){
        super.onResume();
        btnPostLoad.setOnClickListener(this);
        btnSearchAds.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent(this, MainActivity.class);

        switch (id){
            case R.id.btn_post_loads:
                intent.putExtra("post_ad_from_splash", true);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_search_ads:
                intent.putExtra("search_load_from_splash", true);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_spanish:
                AppSettings.with(BaseApplication.getContext()).setLangCode(getString(R.string.locale_spanish));
                Misc.applyLocale(BaseApplication.getContext());
                AppSettings.with(BaseApplication.getContext()).setLanguageDefined(true);
                applyLanguage();
                break;
            case R.id.btn_english:
                AppSettings.with(BaseApplication.getContext()).setLangCode(getString(R.string.locale_english));
                Misc.applyLocale(BaseApplication.getContext());
                AppSettings.with(BaseApplication.getContext()).setLanguageDefined(true);
                applyLanguage();
                break;
            case R.id.btn_arabic:
                AppSettings.with(BaseApplication.getContext()).setLangCode(getString(R.string.locale_arabic));
                Misc.applyLocale(BaseApplication.getContext());
                AppSettings.with(BaseApplication.getContext()).setLanguageDefined(true);
                applyLanguage();
                break;
        }
    }

    private void applyLanguage() {
        finish();
        Intent intent = new Intent(BaseApplication.getContext(), ActivitySplash.class);
        startActivity(intent);
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

    public void setRegionCode(){
        RegionCodeAPI regionCodeAPI = RegionCodeAPI.getInstance();
        regionCodeAPI.setContext(this);
        regionCodeAPI.execute();
    }

}
