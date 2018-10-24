package com.sawatruck.loader.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuzzproductions.ratingbar.RatingBar;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Driver;
import com.sawatruck.loader.utils.ActivityUtil;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.view.activity.ActivityInviteDriver;
import com.sawatruck.loader.view.activity.ActivityMessage;
import com.sawatruck.loader.view.design.CircularImage;
import com.sawatruck.loader.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/22/2017.
 */

public class InviteDriverAdapter extends RecyclerView.Adapter<InviteDriverAdapter.MyViewHolder> {
    private ArrayList<Driver> drivers =  new ArrayList<>();
    private Context context;
    private ArrayList<Boolean> invitationList = new ArrayList<>();
    private int driversCount = 0;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.invite_driver_item, parent, false);
        return new MyViewHolder(v);
    }

    public InviteDriverAdapter() {

    }

    public InviteDriverAdapter(Context context) {
        this.context = context;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Driver driver = drivers.get(position);
        holder.txtUserName.setText(driver.getFullName());
        holder.txtCountry.setText(driver.getCountryName());
        holder.txtTruckType.setText(driver.getVehicleTypeName());

        try {
            BaseApplication.getPicasso().load(driver.getImageUrl()).placeholder(R.drawable.ico_user).fit().into(holder.imgUser);
        }catch (Exception e){
            e.printStackTrace();
        }

        double rating = Double.valueOf(driver.getRating())/20.0f;
        holder.ratingUser.setRating((float)rating);

        final ActivityInviteDriver activityInviteDriver = (ActivityInviteDriver) context;

        holder.btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityMessage.class);
                intent.putExtra(Constant.INTENT_USER_ID, driver.getID());
                intent.putExtra(Constant.INTENT_USERNAME,driver.getFullName());
                ActivityUtil.goOtherActivityFlipTransition(context, intent);
            }
        });

        if(invitationList.get(position)) {
            holder.btnInvite.setText(context.getString(R.string.btn_invited));
            holder.btnInvite.setTextColor(Misc.getColorFromResource(R.color.colorWhite));
            holder.btnInvite.setBackground(context.getResources().getDrawable(R.drawable.sharp_round_bluebutton));
        }
        else  {
            holder.btnInvite.setText(context.getString(R.string.btn_invite));
            holder.btnInvite.setTextColor(Misc.getColorFromResource(R.color.colorLightBlack));
            holder.btnInvite.setBackground(context.getResources().getDrawable(R.drawable.sharp_round_blueborderbutton));
        }

        holder.btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!invitationList.get(position)) {
                    holder.btnInvite.setText(context.getString(R.string.btn_invited));
                    holder.btnInvite.setTextColor(Misc.getColorFromResource(R.color.colorWhite));
                    holder.btnInvite.setBackground(context.getResources().getDrawable(R.drawable.sharp_round_bluebutton));
                    invitationList.set(position, true);
                    driversCount ++ ;
                    activityInviteDriver.setInviteVisibility(true);
                }
                else  {
                    holder.btnInvite.setText(context.getString(R.string.btn_invite));
                    holder.btnInvite.setTextColor(Misc.getColorFromResource(R.color.colorLightBlack));
                    holder.btnInvite.setBackground(context.getResources().getDrawable(R.drawable.sharp_round_blueborderbutton));
                    invitationList.set(position, false);
                    driversCount --;
                    if(driversCount == 0)
                        activityInviteDriver.setInviteVisibility(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    public void setDrivers(ArrayList<Driver> drivers) {
        this.drivers = drivers;
        for(Driver driver:drivers){
            this.invitationList.add(false);
        }
    }

    public ArrayList<Driver> getDrivers(){
        return this.drivers;
    }

    public void initializeAdapter() {
        this.drivers = new ArrayList<>();
        invitationList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<Boolean> getInvitationList() {
        return invitationList;
    }

    public void setInvitationList(ArrayList<Boolean> invitationList) {
        this.invitationList = invitationList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.btn_message) View btnMessage;
        @Bind(R.id.btn_invite) TextView btnInvite;
        @Bind(R.id.rating_bar) RatingBar ratingUser;
        @Bind(R.id.img_user) CircularImage imgUser;
        @Bind(R.id.txt_truck_type) CustomTextView txtTruckType;
        @Bind(R.id.txt_meesa) CustomTextView txtUserName;
        @Bind(R.id.txt_country) CustomTextView txtCountry;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}