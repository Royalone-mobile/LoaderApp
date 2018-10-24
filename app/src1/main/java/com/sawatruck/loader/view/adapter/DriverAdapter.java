package com.sawatruck.loader.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Driver;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 10/3/2017.
 */


public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.MyViewHolder> {
    private ArrayList<Driver> drivers =  new ArrayList<>();
    private Context context;
    private ArrayList<Boolean> invitationList = new ArrayList<>();
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.invited_driver_item, parent, false);
        return new MyViewHolder(v);
    }

    public DriverAdapter() {

    }

    public DriverAdapter(Context context) {
        this.context = context;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Driver driver = drivers.get(position);
        try {
            BaseApplication.getPicasso().load(driver.getImageUrl()).placeholder(R.drawable.ico_user).fit().into(holder.imgUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        @Bind(R.id.img_user) ImageView imgUser;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}