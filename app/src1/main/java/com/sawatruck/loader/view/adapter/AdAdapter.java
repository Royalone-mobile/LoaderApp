package com.sawatruck.loader.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Advertisement;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.activity.ActivityAdDetails;
import com.sawatruck.loader.view.activity.ActivitySignIn;
import com.sawatruck.loader.view.activity.MainActivity;
import com.sawatruck.loader.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/20/2017.
 */


public class AdAdapter extends RecyclerView.Adapter<AdAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Advertisement> advertisements = new ArrayList<>();

    @Override
    public AdAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.ad_list_item, parent, false);
        return new AdAdapter.MyViewHolder(v);
    }

    public AdAdapter() {

    }

    public AdAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(AdAdapter.MyViewHolder holder, int position) {
        final Advertisement advertisement  = advertisements.get(position);
        holder.txtTruckType1.setText(advertisement.getTruckTypeName1());
        holder.txtTruckType2.setText(advertisement.getTruckTypeName2());
        holder.txtBudgetPrice.setText(advertisement.getBudget() + " " + advertisement.getCurrency());
        holder.txtPickupDate.setText(advertisement.getPickupDate());
        holder.txtDeliveryDate.setText(advertisement.getDeliveryDate());

        try {

            String pickupLocation = advertisement.getPickupCity().concat(" ").concat(advertisement.getPickupCountry());
            String deliveryLocation = advertisement.getDeliveryCity().concat(" ").concat(advertisement.getDeliveryCountry());

            holder.txtDeliveryLocation.setText(deliveryLocation);
            holder.txtPickupLocation.setText(pickupLocation);

            BaseApplication.getPicasso().load(advertisement.getTruckImageURL()).placeholder(R.drawable.ico_user).into(holder.imgPhoto);
        }catch (Exception e){
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userType = UserManager.with(BaseApplication.getContext()).getUserType();
                if(userType == 0) {
                    Intent intent = new Intent(context, ActivitySignIn.class);
                    context.startActivity(intent);
                    MainActivity.finishSelf();
                }
                else {
                    Intent intent = new Intent(context, ActivityAdDetails.class);
                    intent.putExtra(Constant.INTENT_ADVERTISEMENT_ID, advertisement.getID());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return advertisements.size();
    }

    public ArrayList<Advertisement> getAdvertisements() {
        return advertisements;
    }

    public void setAdvertisements(ArrayList<Advertisement> advertisements) {
        this.advertisements = advertisements;
    }

    public void initializeAdapter() {
        this.advertisements = new ArrayList<>();
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_truck_type1) CustomTextView txtTruckType1;
        @Bind(R.id.txt_truck_type2) CustomTextView txtTruckType2;
        @Bind(R.id.txt_budget_price) CustomTextView txtBudgetPrice;
        @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;
        @Bind(R.id.txt_pickup_date) CustomTextView txtPickupDate;
        @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
        @Bind(R.id.txt_pickup_location) CustomTextView txtPickupLocation;
        @Bind(R.id.img_photo) ImageView imgPhoto;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}