package com.sawatruck.loader.view.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.sawatruck.loader.R;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.view.activity.BaseActivity;
import com.sawatruck.loader.view.activity.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/19/2017.
 */


public class FragmentMenuLoad extends BaseFragment {
    public static final String TAG = FragmentMenuLoad.class.getSimpleName();
    public static int Initial_TAB = 0;
    FragmentLoadBooked tab1;
    FragmentLoadOpened tab2;
    FragmentLoadCompleted tab3;
    FragmentLoadCanceled tab4;

    @Bind(R.id.pager) ViewPager pager;
    @Bind(R.id.tabs) PagerSlidingTabStrip tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sliding_tab, container, false);
        ButterKnife.bind(this,view);

        BaseActivity baseActivity = (BaseActivity)getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.sidemenu_my_loads));
        baseActivity.showOptions(false);

        LoadPagerAdapter adPagerAdapter = new LoadPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adPagerAdapter);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(viewPageListener);

        pager.setCurrentItem(Initial_TAB);

        ImageView btnAddAd  = (ImageView)getActivity().findViewById(R.id.btn_add_load);
        btnAddAd.setVisibility(View.VISIBLE);
        btnAddAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.getInstance()!=null) {
                    FragmentPostLoad fragmentPostLoad = new FragmentPostLoad();
                    MainActivity.getInstance().inflateFragmentWithTag(fragmentPostLoad, FragmentPostLoad.TAG);
                }
            }
        });

        try {
            Typeface myFont = Typeface.createFromAsset(getContext().getAssets(), "font/MyriadPro-Regular.otf");
            tabs.setTypeface(myFont,0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        setTabTextColor(Initial_TAB);

        Initial_TAB = 0;
        return view;
    }

    private ViewPager.OnPageChangeListener viewPageListener = new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
           setTabTextColor(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void setTabTextColor(int tabPosition) {

         //Set first tab selected
        LinearLayout mTabsLinearLayout = ((LinearLayout) tabs.getChildAt(0));
        for (int i = 0; i < mTabsLinearLayout.getChildCount(); i++) {
            TextView tv = (TextView) mTabsLinearLayout.getChildAt(i);

            if(i == tabPosition){
                tv.setTextColor(Misc.getColorFromResource(R.color.colorDarkOrange));
            } else {
                tv.setTextColor(Misc.getColorFromResource(R.color.colorLightGray));
            }
        }
    }


    public class LoadPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {getResources().getString(R.string.tab_booked), getResources().getString(R.string.tab_opened),
                getResources().getString(R.string.tab_completed),getResources().getString(R.string.tab_cancelled)};

        LoadPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment tabFragment = null;
            if (position == 0) {
                if (tab1 == null) {
                    tab1 = FragmentLoadBooked.getInstance(position);
                }
                tabFragment = tab1;
            } else if (position == 1) {
                if (tab2 == null) {
                    tab2 = FragmentLoadOpened.getInstance(position);
                }
                tabFragment = tab2;
            } else if (position ==2){
                if (tab3 == null) {
                    tab3 = FragmentLoadCompleted.getInstance(position);
                }
                tabFragment = tab3;
            }
             else if (position ==3){
                            if (tab4 == null) {
                                tab4 = FragmentLoadCanceled.getInstance(position);
                            }
                            tabFragment = tab4;
                        }
            return tabFragment;
        }
    }
}
