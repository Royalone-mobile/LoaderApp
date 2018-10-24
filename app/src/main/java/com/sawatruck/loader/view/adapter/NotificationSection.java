package com.sawatruck.loader.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.Foreground;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.NotificationModel;
import com.sawatruck.loader.entities.OfferDetail;
import com.sawatruck.loader.utils.CircleTransformation;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.activity.ActivityActiveAdBooking;
import com.sawatruck.loader.view.activity.ActivityAdDetails;
import com.sawatruck.loader.view.activity.ActivityBookedDetails;
import com.sawatruck.loader.view.activity.ActivityCanceledAdBooking;
import com.sawatruck.loader.view.activity.ActivityListingDetails;
import com.sawatruck.loader.view.activity.ActivityMessage;
import com.sawatruck.loader.view.activity.ActivityNotification;
import com.sawatruck.loader.view.activity.ActivityRating;
import com.sawatruck.loader.view.design.CustomTextView;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by royal on 9/20/2017.
 */

public class NotificationSection extends StatelessSection {
    private Context context;
    private ArrayList<NotificationModel> notifications = new ArrayList<>();

    public NotificationSection(Context context) {
        super(R.layout.layout_section_header, R.layout.notification_item);
        this.context = context;
    }
    public NotificationSection(ArrayList<NotificationModel> notifications) {
        super(R.layout.layout_section_header, R.layout.notification_item);
        this.notifications = notifications;
    }

    public NotificationSection(Context context, ArrayList<NotificationModel> notifications) {
        super(R.layout.layout_section_header, R.layout.notification_item);
        this.notifications = notifications;
        this.context = context;
    }

    @Override
    public int getContentItemsTotal() {
        return notifications.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }
    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        if(notifications.size()>0)

            headerHolder.txtNotificationDate.setText(Misc.getNotificationSpanFromDate(context,Misc.getDateFromString(notifications.get(0).getNotification().getDate())));
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;

        final NotificationModel notificationModel = notifications.get(position);
        Logger.error(notificationModel.getNotification().getScreenName());

        itemHolder.txtTime.setText(Misc.getTimeStringFromDate(Misc.getTimeZoneDate(notificationModel.getNotification().getDate())));
        itemHolder.txtMessage.setText(notificationModel.getNotification().getMessage());

        itemHolder.txtMessage.setSelected(true);

        itemHolder.itemView.setTag(notificationModel);

        try {
            BaseApplication.getPicasso().load(notificationModel.getNotification().getImageUrl()).placeholder(R.drawable.ico_user).transform(new CircleTransformation()).fit().into(itemHolder.imgSender);
        }catch (Exception e){
            e.printStackTrace();
        }

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeSeen(notificationModel);
                handleNotification(notificationModel);
            }
        });
    }


    private void makeSeen(final NotificationModel notificationModel) {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(context).getCurrentUser().getToken());

        httpUtil.put(Constant.MAKE_SEEN_NOTIFICATION_API + "/" + notificationModel.getID(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                notifications.remove(notificationModel);
//                ActivityNotification.resetNotifications();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Logger.error("onFailure");
            }
        });
    }


    private void handleNotification(final NotificationModel notificationModel) {
        Intent intent = new Intent();
        int Status = 0;
        try {
            String ID = notificationModel.getNotification().getTargetID();

            if(notificationModel.getNotification().getStatus()!=null)
              Status = Integer.valueOf(notificationModel.getNotification().getStatus());

            Logger.error( "ScreenName= " + notificationModel.getNotification().getScreenName());
            switch (notificationModel.getNotification().getScreenName()) {
                case "BidDetails":
                    Logger.error("BidDetails");
                    intent =  new Intent(BaseApplication.getContext(), ActivityListingDetails.class);
                    //TODO load =>2, Ad => 1
                    intent.putExtra(Constant.INTENT_LOAD_ID, ID);
                    intent.putExtra(Constant.INTENT_TRAVEL_TYPE, 1);
                    break;
                case "LoadDetails":
                    Logger.error("LoadDetails");
                    if(Status == 2) intent =  new Intent(BaseApplication.getContext(), ActivityBookedDetails.class);
                    else if(Status == 1)  intent =  new Intent(BaseApplication.getContext(), ActivityListingDetails.class);
                    else new Intent(BaseApplication.getContext(), ActivityNotification.class);
                    intent.putExtra(Constant.INTENT_LOAD_ID, ID);
                    intent.putExtra(Constant.INTENT_TRAVEL_TYPE, 1);
                    break;
                case "AdvertisementDetails":
                    Logger.error("AdvertisementDetails");
                    intent =  new Intent(BaseApplication.getContext(), ActivityAdDetails.class);
                    intent.putExtra(Constant.INTENT_ADVERTISEMENT_ID, ID);
                    break;
                case "AdBookingDetails":
                    if(Status == 1) intent =  new Intent(BaseApplication.getContext(), ActivityBookedDetails.class);
                    else if(Status == 0)  intent = new Intent(BaseApplication.getContext(), ActivityActiveAdBooking.class);
                    else new Intent(BaseApplication.getContext(), ActivityCanceledAdBooking.class);
                    intent.putExtra(Constant.INTENT_TRAVEL_TYPE, 2);
                    intent.putExtra(Constant.INTENT_AD_BOOKING_ID, ID);
                    break;
                case "ChatScreen":
                    Logger.error("ChatScreen");
                    intent = new Intent(BaseApplication.getContext(), ActivityMessage.class);
                    String title = "";
                    title = notificationModel.getNotification().getTitle();
                    intent.putExtra(Constant.INTENT_USERNAME,title);
                    intent.putExtra(Constant.INTENT_USER_ID, ID);
                    break;
                case "RatingScreen":
                    intent = new Intent(BaseApplication.getContext(), ActivityRating.class);
                    intent.putExtra(Constant.INTENT_TRAVEL_ID, ID);
                    break;
                case "Tracking":
                    showTracking(ID);
                  break;
            }
            context.startActivity(intent);
        }catch (Exception e) {

        }
    }

    private void showTracking(final String travelID) {
        HttpUtil.getInstance().get(Constant.GET_TRAVEL_BY_ID + "/" + travelID, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                try {
                    Logger.error("showTracking");
                    Logger.error(travelID);
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);

                    Intent intent =  new Intent(BaseApplication.getContext(), ActivityBookedDetails.class);
                    JSONObject jsonObject = new JSONObject(paramString);

                    String Id = "";
                    if(jsonObject.has("IsFromLoad")) {
                        Boolean isFromLoad = jsonObject.getBoolean("IsFromLoad");
                        Logger.error("IsFromLoad=" + isFromLoad);


                        if(isFromLoad) {
                            intent.putExtra(Constant.INTENT_TRAVEL_TYPE, 1);
                            if(jsonObject.has("Load")) {
                                JSONObject loadObject = jsonObject.getJSONObject("Load");
                                if(loadObject.has("ID"))
                                    Id = loadObject.getString("ID");
                                intent.putExtra(Constant.INTENT_LOAD_ID, Id);
                            }
                        } else {
                            intent.putExtra(Constant.INTENT_TRAVEL_TYPE, 2);
                            if(jsonObject.has("OfferID"))
                                Id = jsonObject.getString("OfferID");
                            intent.putExtra(Constant.INTENT_AD_BOOKING_ID, Id);
                        }

                        context.startActivity(intent);
                    }


                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public ArrayList<NotificationModel> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<NotificationModel> notifications) {
        this.notifications = notifications;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_sender) ImageView imgSender;
        @Bind(R.id.txt_message) CustomTextView txtMessage;
        @Bind(R.id.txt_time) CustomTextView txtTime;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_notification_date) CustomTextView txtNotificationDate;

        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }
    }
}
