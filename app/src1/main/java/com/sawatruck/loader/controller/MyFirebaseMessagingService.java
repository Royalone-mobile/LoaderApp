package com.sawatruck.loader.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.utils.AppSettings;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.view.activity.ActivityActiveAdBooking;
import com.sawatruck.loader.view.activity.ActivityAdDetails;
import com.sawatruck.loader.view.activity.ActivityBookedDetails;
import com.sawatruck.loader.view.activity.ActivityCanceledAdBooking;
import com.sawatruck.loader.view.activity.ActivityListingDetails;
import com.sawatruck.loader.view.activity.ActivityMessage;
import com.sawatruck.loader.view.activity.ActivityNotification;
import com.sawatruck.loader.view.activity.ActivityRating;

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

        if(fcmIntent.hasExtra("Status"))
            Status =  Integer.valueOf(fcmIntent.getStringExtra("Status"));
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
                break;
        }
        PendingIntent contentIntent = PendingIntent.getActivity(BaseApplication.getContext(), notification_number, intent, 0);
        Logger.error("----------Notification Number----------------" + notification_number);
        notification_number++;
        return contentIntent;
    }
}