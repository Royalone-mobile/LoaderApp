package com.sawatruck.loader.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makeramen.roundedimageview.RoundedImageView;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Load;
import com.sawatruck.loader.entities.LoadPhoto;
import com.sawatruck.loader.view.activity.ActivityBookedDetails;
import com.sawatruck.loader.view.activity.ActivityListingDetails;
import com.sawatruck.loader.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/20/2017.
 */


public class LoadsAdapter extends RecyclerView.Adapter<LoadsAdapter.MyViewHolder> {
    private ArrayList<Load> loads = new ArrayList<>();
    private Context context;
    private int tabPosition = -1;

    @Override
    public LoadsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.load_list_item, parent, false);
        return new LoadsAdapter.MyViewHolder(v);
    }

    public LoadsAdapter(Context context) {
        this.context = context;
    }

    public LoadsAdapter(Context context, int tabPosition) {
        this.context = context;
        this.tabPosition = tabPosition;
    }

    @Override
    public void onBindViewHolder(LoadsAdapter.MyViewHolder holder, final int position) {
        final Load load = loads.get(position);

        if(load.getLoadPhotos().size()>0) {
            LoadPhoto loadPhoto = load.getLoadPhotos().get(0);
            try {
                BaseApplication.getPicasso().load(loadPhoto.getPhotoPath()).placeholder(R.drawable.ico_truck).into(holder.imgLoadPhoto);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        holder.txtBudgetPrice.setText(load.getOfferPrice().concat(" ").concat(load.getCurrency()));
        holder.txtDeliveryDate.setText(load.getUnloadDateEnd());
        holder.txtLoadDate.setText(load.getLoadDateFrom());
        holder.txtLoadName.setText(load.getName());
        holder.txtLoadLocation.setText(load.getFromLocation().getCityName().concat(", ").concat(load.getFromLocation().getCountryName()));
        holder.txtDeliveryLocation.setText(load.getToLocation().getCityName().concat(", ").concat(load.getToLocation().getCountryName()));
        holder.txtLoadType.setText(load.getLoadType());

        if(load.getOffersCount()>0) {
            String bids = context.getResources().getQuantityString(R.plurals.bids, load.getOffersCount(), load.getOffersCount());
            holder.txtDetails.setText(bids.concat(", ").concat(context.getString(R.string.best_price)).concat(" ").concat(load.getBestOffer()).concat(" ").concat(load.getCurrency()));
        }
        else
            holder.txtDetails.setText(context.getString(R.string.no_bids));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                switch (tabPosition){
                    case 0:
                        intent = new Intent(context, ActivityBookedDetails.class);

                        break;
                    case 1:
                        intent = new Intent(context, ActivityListingDetails.class);
                        break;
                }
                intent.putExtra(Constant.INTENT_TRAVEL_TYPE, 1);
                intent.putExtra(Constant.INTENT_LOAD_ID, load.getID());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return loads.size();
    }

    public void initializeAdapter() {
        this.loads = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<Load> getLoads() {
        return loads;
    }

    public void setLoads(ArrayList<Load> loads) {
        this.loads = loads;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_load_photo) RoundedImageView imgLoadPhoto;
        @Bind(R.id.txt_load_name) CustomTextView txtLoadName;
        @Bind(R.id.txt_load_type) CustomTextView txtLoadType;
        @Bind(R.id.txt_budget_price) CustomTextView txtBudgetPrice;
        @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
        @Bind(R.id.txt_load_date) CustomTextView txtLoadDate;
        @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
        @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;
        @Bind(R.id.txt_details) CustomTextView txtDetails;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}