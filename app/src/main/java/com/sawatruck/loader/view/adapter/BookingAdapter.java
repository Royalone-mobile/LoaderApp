package com.sawatruck.loader.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.fuzzproductions.ratingbar.RatingBar;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.AdvertisementBooking;
import com.sawatruck.loader.view.activity.ActivityActiveAdBooking;
import com.sawatruck.loader.view.activity.ActivityBookedDetails;
import com.sawatruck.loader.view.activity.ActivityCanceledAdBooking;
import com.sawatruck.loader.view.design.CircularImage;
import com.sawatruck.loader.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/20/2017.
 */


public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.MyViewHolder> {
    private int tabPosition = -1;
    private ArrayList<AdvertisementBooking> advertisementBookings = new ArrayList<>();
    private Context context;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.booking_list_item, parent, false);
        return new MyViewHolder(v);
    }

    public BookingAdapter() {

    }

    public BookingAdapter(Context context) {
        this.context = context;
    }

    public BookingAdapter(Context context, int tabPosition) {
        this.context = context;
        this.tabPosition = tabPosition;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final AdvertisementBooking advertisementBooking = advertisementBookings.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                switch (tabPosition) {
                    case 0:
                        intent = new Intent(context, ActivityBookedDetails.class);
                        intent.putExtra(Constant.INTENT_AD_BOOKING_ID, advertisementBooking.getAdvertisementID());
                        break;
                    case 1:

                        intent = new Intent(context, ActivityActiveAdBooking.class);
                        intent.putExtra(Constant.INTENT_ADVERTISEMENT_ID, advertisementBooking.getAdvertisementID());
                        intent.putExtra(Constant.INTENT_AD_BOOKING_ID, advertisementBooking.getID());
                        break;
                    case 2:
                        intent = new Intent(context, ActivityCanceledAdBooking.class);

                        intent.putExtra(Constant.INTENT_ADVERTISEMENT_ID, advertisementBooking.getAdvertisementID());
                        intent.putExtra(Constant.INTENT_AD_BOOKING_ID, advertisementBooking.getID());
                        break;
                }
                intent.putExtra(Constant.INTENT_TRAVEL_TYPE, 2);

                context.startActivity(intent);
            }
        });

        try {
            holder.txtUserName.setText(advertisementBooking.getDriverFullName());
            holder.txtBudgetPrice.setText(String.valueOf(advertisementBooking.getBudget()).concat(" ").concat(advertisementBooking.getCurrency()));
            holder.txtLoadDate.setText(advertisementBooking.getPickupDate());
            holder.txtLoadLocation.setText(advertisementBooking.getPickupCity().concat(", ").concat(advertisementBooking.getPickupCountry())) ;
            holder.txtDeliveryDate.setText(advertisementBooking.getDeliveryDate());
            holder.txtDeliveryLocation.setText(advertisementBooking.getDeliveryCity().concat(", ").concat(advertisementBooking.getDeliveryCountry()));
            holder.txtTruckType.setText(advertisementBooking.getTruckType());

            BaseApplication.getPicasso().load(advertisementBooking.getDriverImageURL()).placeholder(R.drawable.ico_user).into(holder.imgPhoto);

            if(advertisementBooking.getStatus() == 2) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGray));
            }

            double rating = Double.valueOf(advertisementBooking.getDriverRating())/20.0f;
            holder.ratingBar.setRating((float)rating);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return advertisementBookings.size();
    }

    public int getTabPosition() {
        return tabPosition;
    }

    public void setTabPosition(int tabPosition) {
        this.tabPosition = tabPosition;
    }

    public void initializeAdapter() {
        this.advertisementBookings = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<AdvertisementBooking> getAdvertisementBookings() {
        return advertisementBookings;
    }

    public void setAdvertisementBookings(ArrayList<AdvertisementBooking> advertisementBookings) {
        this.advertisementBookings = advertisementBookings;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_meesa) CustomTextView txtUserName;
        @Bind(R.id.txt_truck_type) CustomTextView txtTruckType;
        @Bind(R.id.txt_budget_price) CustomTextView txtBudgetPrice;
        @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
        @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
        @Bind(R.id.txt_load_date) CustomTextView txtLoadDate;
        @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;
        @Bind(R.id.rating_bar) RatingBar ratingBar;
        @Bind(R.id.img_photo) CircularImage imgPhoto;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}