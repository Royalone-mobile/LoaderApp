package com.sawatruck.loader;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sawatruck.loader.controller.RegionCodeAPI;
import com.sawatruck.loader.utils.AppSettings;
import com.sawatruck.loader.utils.GPSTracker;
import com.sawatruck.loader.utils.Logger;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.UserManager;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

/**
 * Created by royal on 8/19/2017.
 */

@ReportsCrashes(mailTo = "stuntblitz@gmail.com", // my email here
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.app_name)

public class BaseApplication extends MultiDexApplication {
    public static BaseApplication _application;
    private static Picasso picasso;
    private static Gson gson;
    private static Handler handler;
    private static final String HUB_URL = "http://api.sawatruck.com/";
    private static HubConnection hubConnection;
    private static HubProxy hubProxy;

    public final String TAG = BaseApplication.class.getSimpleName();

    public static BaseApplication getInstance() {
        return _application;
    }


    public static Gson getGson() {
        return gson;
    }

    public static void setGson(Gson gson) {
        BaseApplication.gson = gson;
    }

    public static Picasso getPicasso() {
        return picasso;
    }

    public static void setPicasso(Picasso picasso) {
        BaseApplication.picasso = picasso;
    }

    private static HubConnection trackingHubConnection;


    @Override
    public void onCreate() {
        super.onCreate();
        _application = this;
        gson =  new Gson();
        handler = new Handler(Looper.getMainLooper());
        ACRA.init(this);
        Foreground.init(this);

        picasso = Picasso.with(getApplicationContext());
        Misc.applyLocale(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        hubConnection = new HubConnection(HUB_URL, true);

        Credentials credentials = new Credentials() {
            @Override
            public void prepareRequest(Request request) {
                request.addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(getContext()).getCurrentUser().getToken());

            }
        };

        hubConnection.setCredentials(credentials);
        HubProxy hubProxy = hubConnection.createHubProxy("MainHub");
        configConnectFuture(hubProxy, hubConnection);
    }

    public void openTrackingHub(int travelID){
        Logger.error("-------------openTrackingHub------------");

        String query = "TravelID="+travelID;
        Logger.error(query);
        try {
            trackingHubConnection = new HubConnection(HUB_URL, query, true, new microsoft.aspnet.signalr.client.Logger() {
                @Override
                public void log(String s, LogLevel logLevel) {
                }
            });

            Credentials credentials = new Credentials() {
                @Override
                public void prepareRequest(Request request) {
                    request.addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(getContext()).getCurrentUser().getToken());

                }
            };

            trackingHubConnection.setCredentials(credentials);
            hubProxy = trackingHubConnection.createHubProxy("MainHub");
            configConnectFuture(hubProxy, trackingHubConnection);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void closeTrackingHub(){
        Logger.error("-------------closeTrackingHub------------");
        try {
            trackingHubConnection.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reconfigHub(){
        hubConnection = new HubConnection(HUB_URL, true);


        Credentials credentials = new Credentials() {
            @Override
            public void prepareRequest(Request request) {
                request.addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(getContext()).getCurrentUser().getToken());
            }
        };

        hubConnection.setCredentials(credentials);
        hubProxy = hubConnection.createHubProxy("MainHub");
        configConnectFuture(hubProxy, hubConnection);
    }
    private void configConnectFuture(HubProxy hubProxy, HubConnection hubConnection) {
        try {
            /**
             * in case you want to use Credentials;
             */
            // here to start

            SignalRFuture<Void> signalRFuture = hubConnection.start(new ServerSentEventsTransport(hubConnection.getLogger()));
            signalRFuture.get();

            // here to ON[Method]
            hubProxy.on("TestPushData", new SubscriptionHandler1<String>() {
                @Override
                public void run(final String s) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Logger.error("Result=" + s);
                        }
                    });
                }
            }, String.class);

            hubProxy.on("OnNewPoint", new SubscriptionHandler1<String>() {
                @Override
                public void run(final String s) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Logger.error("OnNewPoint=" + s);
                            Intent intent = new Intent();
                            intent.setAction("onNewPoint");
                            intent.putExtra("data",s);
                            BaseApplication.getContext().sendBroadcast(intent);
                        }
                    });
                }
            }, String.class);

            hubProxy.on("OnReceiveChatMessage", new SubscriptionHandler1<String>() {
                @Override
                public void run(final String s) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.setAction("onReceiveMessage");
                            intent.putExtra("message",s);
                            BaseApplication.getContext().sendBroadcast(intent);
                        }
                    });
                }
            }, String.class);

            hubProxy.on("OnSendChatMessage", new SubscriptionHandler1<String>() {
                @Override
                public void run(final String s) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.setAction("onSendMessage");
                            intent.putExtra("message",s);
                            BaseApplication.getContext().sendBroadcast(intent);
                        }
                    });
                }
            }, String.class);


            hubProxy.on("TestHub", new SubscriptionHandler1<String>() {
                @Override
                public void run(final String s) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }, String.class);


            buildCallBack(hubConnection);
        } catch (Exception e) {

        }
    }

    /**
     * This Method to build receive MSG/Error
     * @param hubConnection
     */
    private void buildCallBack(HubConnection hubConnection) {
        if (hubConnection == null) {
            return;
        }
        // this callback for Error
        hubConnection.error(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });
        // this callback for received Msg
        hubConnection.received(new MessageReceivedHandler() {
            @Override
            public void onMessageReceived(final JsonElement jsonElement) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });
    }

    public void getLocation() {
        GPSTracker gpsTracker = GPSTracker.getInstance(this);
        try {
            Location location = gpsTracker.getLocation();
            AppSettings.with(this).setCurrentLat(location.getLatitude());
            AppSettings.with(this).setCurrentLng(location.getLongitude());
        } catch (Exception e) {
            RegionCodeAPI regionCodeAPI = RegionCodeAPI.getInstance();
            regionCodeAPI.setContext(this);
            regionCodeAPI.execute();
            e.printStackTrace();
        }
    }

    public static boolean isServiceRunning(Class<?> serviceClasss) {
        ActivityManager manager = (ActivityManager) _application.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClasss.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Context getContext() {
        return _application.getApplicationContext();
    }

    public static Handler getHandler() {
        return handler;
    }

    public static void setHandler(Handler handler) {
        BaseApplication.handler = handler;
    }

    public static HubProxy getHubProxy() {
        return hubProxy;
    }

    public static void setHubProxy(HubProxy hubProxy) {
        BaseApplication.hubProxy = hubProxy;
    }

    public static HubConnection getHubConnection() {
        return hubConnection;
    }

    public static void setHubConnection(HubConnection hubConnection) {
        BaseApplication.hubConnection = hubConnection;
    }
}