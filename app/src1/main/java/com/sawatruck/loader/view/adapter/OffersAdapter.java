package com.sawatruck.loader.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuzzproductions.ratingbar.RatingBar;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.Offer;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.ActivityUtil;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.activity.ActivityListingDetails;
import com.sawatruck.loader.view.activity.ActivityMessage;
import com.sawatruck.loader.view.design.CircularImage;
import com.sawatruck.loader.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/22/2017.
 */

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.MyViewHolder> {
    private ArrayList<Offer> offers = new ArrayList<>();
    private Context context;



    @Override
    public OffersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.bid_list_item, parent, false);
        return new OffersAdapter.MyViewHolder(v);
    }

    public OffersAdapter() {

    }

    public OffersAdapter(Context context) {
        this.context = context;
    }
    @Override
    public void onBindViewHolder(OffersAdapter.MyViewHolder holder,final int position) {
        Offer offer = offers.get(position);

        try{

            holder.txtUserName.setText(offer.getUser().getFullName());
            holder.txtOfferPrice.setText( String.valueOf(offer.getPrice()) + " " + offer.getCurrency() );
            holder.txtTruckType.setText(offer.getTruckType() );
            float rating = Float.valueOf(offer.getUser().getRate())/20.0f;

            holder.ratingBar.setRating(rating);

            try {
                BaseApplication.getPicasso().load(offer.getUser().getImageUrl()).placeholder(R.drawable.ico_truck).into(holder.imgUserPhoto);
            }catch (Exception e){
                e.printStackTrace();
            }

            holder.btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Offer offer = offers.get(position);
                    Intent intent = new Intent(context, ActivityMessage.class);
                    intent.putExtra("user_id", offer.getUser().getUserID());
                    intent.putExtra("username", offer.getUser().getFullName());
                    ActivityUtil.goOtherActivityFlipTransition(context, intent);
                }
            });

            if(offer.getStatus() == 2) {
                holder.btnReject.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnUnAccept.setVisibility(View.VISIBLE);
            }



            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Offer offer = offers.get(position);
                    HttpUtil httpUtil = HttpUtil.getInstance();
                    User user = UserManager.with(context).getCurrentUser();
                    httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
                    RequestParams params = new RequestParams();
                    params.put("ID", offer.getID());
                    httpUtil.put(Constant.ACCEPT_OFFER_API + "?ID=" + offer.getID(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, final byte[] responseBody) {
                            try {
                                final ActivityListingDetails activityListingDetails = (ActivityListingDetails) context;
                                activityListingDetails.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Misc.showResponseMessage( responseBody);
                                        if(offer.getStatus() == 1)
                                            activityListingDetails.selectTab(2);
                                    }
                                });


                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, final byte[] errorString, Throwable throwable) {
                            try {
                                ActivityListingDetails activityListingDetails = (ActivityListingDetails) context;
                                activityListingDetails.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Misc.showResponseMessage( errorString);
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            holder.btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Offer offer = offers.get(position);
                    HttpUtil httpUtil = HttpUtil.getInstance();
                    User user = UserManager.with(context).getCurrentUser();
                    httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
                    RequestParams params = new RequestParams();
                    params.put("ID", offer.getID());

                    httpUtil.put(Constant.REJECT_OFFER_API + "/" + offer.getID(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, final byte[] responseBody) {
                            try {
                                ActivityListingDetails activityListingDetails = (ActivityListingDetails) context;
                                activityListingDetails.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Misc.showResponseMessage(responseBody);
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, final byte[] errorString, Throwable throwable) {
                            try {
                                ActivityListingDetails activityListingDetails = (ActivityListingDetails) context;
                                activityListingDetails.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Misc.showResponseMessage(errorString);
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            holder.btnUnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Offer offer = offers.get(position);
                    HttpUtil httpUtil = HttpUtil.getInstance();
                    User user = UserManager.with(context).getCurrentUser();
                    httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
                    RequestParams params = new RequestParams();
                    params.put("ID", offer.getID());

                    httpUtil.put(Constant.UNACCEPT_OFFER_API + "/" + offer.getID(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, final byte[] responseBody) {
                            try {
                                final ActivityListingDetails activityListingDetails = (ActivityListingDetails) context;
                                activityListingDetails.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Misc.showResponseMessage( responseBody);
                                        if(offer.getStatus() == 2)
                                            activityListingDetails.selectTab(1);
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, final byte[] errorString, Throwable throwable) {
                            try {
                                ActivityListingDetails activityListingDetails = (ActivityListingDetails) context;
                                activityListingDetails.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Misc.showResponseMessage(errorString);
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    public void initializeAdapter() {
       offers = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<Offer> getOffers() {
        return offers;
    }

    public void setOffers(ArrayList<Offer> offers) {
        this.offers = offers;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_meesa) CustomTextView txtUserName;
        @Bind(R.id.txt_truck_type) CustomTextView txtTruckType;
        @Bind(R.id.txt_offer_price) CustomTextView txtOfferPrice;
        @Bind(R.id.btn_message) CustomTextView btnMessage;
        @Bind(R.id.btn_accept) CustomTextView btnAccept;
        @Bind(R.id.btn_reject) CustomTextView btnReject;
        @Bind(R.id.btn_unaccept) CustomTextView btnUnAccept;
        @Bind(R.id.img_driver_photo) CircularImage imgUserPhoto;
        @Bind(R.id.rating_bar) RatingBar ratingBar;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}