package com.sawatruck.loader.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fuzzproductions.ratingbar.RatingBar;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Load;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.view.activity.ActivitySneakPeek;
import com.sawatruck.loader.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/22/2017.
 */

public class SneakPeekAdapter extends RecyclerView.Adapter<SneakPeekAdapter.MyViewHolder> {
    private ArrayList<Load> loads = new ArrayList<>();
    private Context context;
    private ArrayList<Boolean> invitationList = new ArrayList<>();
    private int invitesCount = 0;
    @Override
    public SneakPeekAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.sneakpeek_list_item, parent, false);
        return new SneakPeekAdapter.MyViewHolder(v);
    }

    public SneakPeekAdapter() {

    }

    public SneakPeekAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(final SneakPeekAdapter.MyViewHolder holder, final int position) {
        final Load load = loads.get(position);

        holder.txtDeliveryLocation.setText(load.getToLocation().getCityName() + ", " + load.getToLocation().getCountryName());
        holder.txtLoadLocation.setText(load.getFromLocation().getCityName() + ", " + load.getFromLocation().getCountryName());
        holder.txtUserName.setText(load.getUser().getFullName());
        holder.txtPrice.setText( String.valueOf(load.getBudget()) + " " + load.getCurrency());
        holder.txtLoadType.setText( load.getLoadType());
        holder.txtDistance.setText(load.getDistance().concat(context.getString(R.string.kilometer)));

        float rating = Float.valueOf(load.getUser().getRate())/20.0f;
        holder.ratingBar.setRating(rating);
        try {
            BaseApplication.getPicasso().load(load.getLoadPhotos().get(0).getPhotoPath()).placeholder(R.drawable.ico_truck).into(holder.imgTruck);
        }catch (Exception e){
            e.printStackTrace();
        }

        final ActivitySneakPeek activitySneakPeek = (ActivitySneakPeek) context;


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
                    invitesCount++ ;
                    activitySneakPeek.setInviteVisibility(true);
                }
                else  {
                    holder.btnInvite.setText(context.getString(R.string.btn_invite));
                    holder.btnInvite.setTextColor(Misc.getColorFromResource(R.color.colorLightBlack));
                    holder.btnInvite.setBackground(context.getResources().getDrawable(R.drawable.sharp_round_blueborderbutton));
                    invitationList.set(position, false);
                    invitesCount--;
                    if(invitesCount == 0)
                        activitySneakPeek.setInviteVisibility(false);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return loads.size();
    }

    public void initializeAdapter() {
        this.loads =  new ArrayList<>();
        invitationList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<Load> getLoads() {
        return loads;
    }

    public void setLoads(ArrayList<Load> loads) {
        this.loads = loads;
        for(Load load:loads){
            this.invitationList.add(false);
        }
    }

    public ArrayList<Boolean> getInvitationList() {
        return invitationList;
    }

    public void setInvitationList(ArrayList<Boolean> invitationList) {
        this.invitationList = invitationList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.btn_invite) CustomTextView btnInvite;
        @Bind(R.id.txt_price) CustomTextView txtPrice;
        @Bind(R.id.txt_distance) CustomTextView txtDistance;
        @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
        @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
        @Bind(R.id.txt_meesa) CustomTextView txtUserName;
        @Bind(R.id.img_truck) ImageView imgTruck;
        @Bind(R.id.rating_bar) RatingBar ratingBar;
        @Bind(R.id.txt_load_type) CustomTextView txtLoadType;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
