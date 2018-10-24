package com.sawatruck.loader.controller;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.Foreground;
import com.sawatruck.loader.R;
import com.sawatruck.loader.utils.AppSettings;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.view.activity.ActivityActiveAdBooking;
import com.sawatruck.loader.view.activity.ActivityAdDetails;
import com.sawatruck.loader.view.activity.ActivityBookedDetails;
import com.sawatruck.loader.view.activity.ActivityCanceledAdBooking;
import com.sawatruck.loader.view.activity.ActivityListingDetails;
import com.sawatruck.loader.view.activity.ActivityMessage;
import com.sawatruck.loader.view.activity.ActivityNotification;
import com.sawatruck.loader.view.activity.ActivityRating;
import com.sawatruck.loader.view.activity.BaseActivity;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by royal on 9/23/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    public static int notification_number = 0;
    @Override
    public void handleIntent(Intent intent) {
        Logger.error("handleIntent");
        if(intent.getExtras()==null) return;
        if(!AppSettings.with(BaseApplication.getContext()).getNotificationSetting()) return;

        PendingIntent contentIntent;
        String Text= "";

        String Title = "";

        try{
            String ID = intent.getStringExtra("ID");
            String ScreenName = intent.getStringExtra("ScreenName");
            Text   = intent.getStringExtra("Text");
            Title = intent.getStringExtra("Title");
            Logger.error(ScreenName);
            contentIntent = getPendingIntent(ID,ScreenName,intent);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(BaseApplication.getContext()).setSmallIcon(R.mipmap.ic_sawatruck)
                        .setContentTitle(Title).setContentText(Text).setContentIntent(contentIntent);


        Intent deleteIntent = new Intent(this, DeleteNotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, deleteIntent, 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        mBuilder.setAutoCancel(true);

        int notificationCount = AppSettings.with(BaseApplication.getContext()).getNotificationCount();
        notificationCount = notificationCount + 1;
        AppSettings.with(BaseApplication.getContext()).setNotificationCount(notificationCount);
        ShortcutBadger.applyCount(BaseApplication.getContext(), notificationCount);

        mBuilder.setDeleteIntent(pendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) BaseApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Constant.FIREBASE_NOTIFY, mBuilder.build());




        playNotificationSound(BaseApplication.getContext());
    }

    public static void playNotificationSound(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PendingIntent getPendingIntent(String ID, String ScreenName, Intent fcmIntent) {
        Intent intent =new Intent(BaseApplication.getContext(), ActivityNotification.class);
        int Status = 0;

        if(fcmIntent.hasExtra("Status")) {
          if(fcmIntent.getStringExtra("Status")!=null)
            Status = Integer.valueOf(fcmIntent.getStringExtra("Status"));
        }
        Logger.error(ScreenName);

        switch (ScreenName) {
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
                if(fcmIntent.hasExtra("Title"))
                    title = fcmIntent.getStringExtra("Title");
                intent.putExtra(Constant.INTENT_USERNAME,title);
                intent.putExtra(Constant.INTENT_USER_ID, ID);
                break;
            case "RatingScreen":
                intent = new Intent(BaseApplication.getContext(), ActivityRating.class);
                intent.putExtra(Constant.INTENT_TRAVEL_ID, ID);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                final View viewCancelBooking = LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.dialog_rate_driver, null);

                final Button btnOk = (Button) viewCancelBooking.findViewById(R.id.btn_ok);
                final Button btnCancel = (Button) viewCancelBooking.findViewById(R.id.btn_cancel);

                builder.setView(viewCancelBooking);
                final AlertDialog alertDialog = builder.create();

                final Intent finalIntent  = new Intent(BaseApplication.getContext(), ActivityRating.class);
                finalIntent.putExtra(Constant.INTENT_TRAVEL_ID, ID);
                finalIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BaseApplication.getContext().startActivity(finalIntent);
                        alertDialog.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                break;
            case "Tracking":
                showTracking(ID);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(BaseApplication.getContext(), notification_number, intent, 0);
        Logger.error("----------Notification Number----------------" + notification_number);
        notification_number++;
        return contentIntent;
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

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        BaseApplication.getContext().startActivity(intent);
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

}
