package com.sawatruck.loader.view.activity;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.sawatruck.loader.R;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.view.fragments.FragmentActiveOffers;
import com.sawatruck.loader.view.fragments.FragmentAcceptedOffers;
import com.sawatruck.loader.view.fragments.FragmentListingDetails;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 9/23/2017.
 */

public class ActivityListingDetails extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.pager) ViewPager pager;
    @Bind(R.id.tabs) PagerSlidingTabStrip tabs;

    private FragmentListingDetails tab1;
    private FragmentActiveOffers tab2;
    private FragmentAcceptedOffers tab3;


    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_listing_details,null);
        ButterKnife.bind(this, view);

        ListingPagerAdapter listingPagerAdapter = new ListingPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(listingPagerAdapter);
        tabs.setViewPager(pager);
        pager.setCurrentItem(0);

        try {
            Typeface myFont = Typeface.createFromAsset(context.getAssets(), "font/MyriadPro-Regular.otf");
            tabs.setTypeface(myFont,0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {

                LinearLayout mTabsLinearLayout = ((LinearLayout) tabs.getChildAt(0));
                for (int i = 0; i < mTabsLinearLayout.getChildCount(); i++) {
                    TextView tv = (TextView) mTabsLinearLayout.getChildAt(i);
                    if(i == position){
                        tv.setTextColor(Misc.getColorFromResource(R.color.colorDarkOrange));
                    } else {
                        tv.setTextColor(Misc.getColorFromResource(R.color.colorLightGray));
                    }
                }

            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        setUpTabStrip();
        return view;
    }

    public void selectTab(int position){
        try {
            pager.setCurrentItem(position);
            if(position == 1)
                tab2.onRefresh();
            else if(position == 2)
                tab3.onRefresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setUpTabStrip() {

        //your other customizations related to tab strip...blahblah
        // Set first tab selected
        LinearLayout mTabsLinearLayout = ((LinearLayout) tabs.getChildAt(0));
        for (int i = 0; i < mTabsLinearLayout.getChildCount(); i++) {
            TextView tv = (TextView) mTabsLinearLayout.getChildAt(i);
            tv.setHorizontallyScrolling(false);
            if(i == 0){
                tv.setTextColor(Misc.getColorFromResource(R.color.colorDarkOrange));
            } else {
                tv.setTextColor(Misc.getColorFromResource(R.color.colorLightGray));
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getResources().getString(R.string.title_listing_details));
        showNavHome(false);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
    }


    public class ListingPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {getResources().getString(R.string.details), getResources().getString(R.string.active_bids),  getResources().getString(R.string.confirmed_bids)
        };

        ListingPagerAdapter(FragmentManager fm) {
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
                    tab1 = FragmentListingDetails.getInstance(position);
                }
                tabFragment = tab1;
            } else if (position == 1) {
                if (tab2 == null) {
                    tab2 = FragmentActiveOffers.getInstance(position);
                }
                tabFragment = tab2;
            } else if (position == 2) {
                if (tab3 == null) {
                    tab3 = FragmentAcceptedOffers.getInstance(position);
                }
                tabFragment = tab3;
            }

            return tabFragment;
        }
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
}
