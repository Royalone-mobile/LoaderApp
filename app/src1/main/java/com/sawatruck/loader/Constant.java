package com.sawatruck.loader;

import com.paypal.android.sdk.payments.PayPalConfiguration;

/**
 * Created by royal on 8/26/2017.
 */

public class Constant {
    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    public static final String PLACES_API_RADIUS = "170000";
    public static final String PLACES_API_KEY = "AIzaSyBvBK1UJRGAGzZAOs0aOkehWuYvZz2XO9g";
//    public static final String GEOCODE_API_KEY = "AIzaSyCvD3wsta-BQAhxcIvb10nM6bScR1uDsXc";
    public static final String GEOCODE_API_KEY = "AIzaSyD4Xvb1c4ERgQsfFgTP18MVtijcKH3U1ms";

    public static final String STRIPE_API_KEY = "pk_test_sqQGxHJxMnfbME7034NRrWiE";

    public static final String GOOGLE_API_SCOPE_URL = "https://www.googleapis.com/auth/plus.login";

    public static final int GET_PLACE_REQUEST_CODE = 2017;
    public static final int requestLoadLocationCode = 120, requestUnLoadLocationCode = 121;
    public static final int INVITE_DRIVER_REQUEST_CODE = 2018;


    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     * <p>
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     * <p>
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    public static final String PAYPAL_CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox environments.
    public static final String PAYPAL_CONFIG_CLIENT_ID = "AUQxuEFEaN1OtooxRgR5sLNCnwDfDOYWBHat-j4zqki67FQPhnAerPEwW3cHNWzmd5bvQPXmLBRp6dWv";
//    public static final String PAYPAL_CONFIG_CLIENT_ID = "AUQxuEFEaN1OtooxRgR5sLNCnwDfDOYWBHat-j4zqki67FQPhnAerPEwW3cHNWzmd5bvQPXmLBRp6dWv";
    public static final int PAYPAL_REQUEST_CODE = 2020;
    public static final int FIREBASE_NOTIFY = 220;

    public static final String INTENT_TRAVEL_TYPE = "load_or_adbooking";
    public static final String IS_EMAIL_VERIFIED_API = "http://api.sawatruck.com/api/users/IsEmailConfirmed";
    public static final String GET_OFFERS_BY_LOAD_ID_API = "http://api.sawatruck.com/api/Offers/getOffersByLoad";
    public static final String INTENT_LOAD_ID = "load_id";

    public static final String INTENT_AD_BOOKING_ID = "ad_booking_id";
    public static final String SAWATRUCK_CLIENT_ID = "sawaTruckLoaderApplication";
    public static final String SAWATRUCK_CLIENT_SECRET = "ma^&%(*HGFGHGJK54*&YGBGcam";


    public static final String INTENT_LOCATION = "map_location";

    public static final String INTENT_FROM_LOCATION = "from_location";
    public static final String INTENT_TO_LOCATION = "to_location";
    public static final String INTENT_ADVERTISEMENT_ID = "advertisement_id";
    public static final String INTENT_OFFER_ID = "offer_id";
    public static final String GET_OFFER_BY_ID_API = "http://api.sawatruck.com/api/Offers/Get";
    public static final String INTENT_USER_ID = "user_id";
    public static final String INTENT_USERNAME = "username";

    public static final String PARAM_ID = "id";
    public static final String PARAM_AUTHORIZATION = "Authorization";
    public static final String GET_BULK_TYPES_API = "http://api.sawatruck.com/api/BulkTypes/Get";
    public static final String INTENT_TRAVEL_ID = "travel_id";
    public static final String GET_TO_DO_API = "http://api.sawatruck.com/api/Travels/GetToDo";
    public static final String MAKE_SEEN_NOTIFICATION_API = "http://api.sawatruck.com/api/NotificationUser/put";
    public static final String GET_NOTIFICATIONS_UNSEEN_API = "http://api.sawatruck.com/api/NotificationUser/GetUnseen";


    public static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(PAYPAL_CONFIG_ENVIRONMENT)
            .clientId(PAYPAL_CONFIG_CLIENT_ID);


    public static final String BROADCAST_ACTION = "Send Message";
    public static final String API_URL = "http://api.sawatruck.com/api/";
    public static final String USER_GETBALANCE_API = "http://api.sawatruck.com/api/Users/GetUserBalance";
    public static final String USER_SIGNIN_API = "http://api.sawatruck.com/Token";

    public static final String USER_CONTROLLER = API_URL.concat("Users/");

    public static final String USER_SIGNUPLOADER_API = USER_CONTROLLER.concat("SignupClient");

    public static final String USER_FORGOTPASSWORD_API = USER_CONTROLLER.concat("ForgetPassword");
    public static final String USER_CHANGEPASSWORD_API = USER_CONTROLLER.concat("ChangeUserPassword");

    public static final String USER_EDITPROFILE_API = USER_CONTROLLER.concat("Put");


    public static final String RATE_TRAVEL_API = API_URL.concat("rating/rateTrucker");
    public static final String GET_USER_RATING_API = USER_CONTROLLER.concat("GetUserRatingStatistics");
    public static final String GET_CURRENCIES_API = "http://api.sawatruck.com/api/Currencies/get";
    public static final String GET_TRUCK_TYPE_API = "http://api.sawatruck.com/Api/TruckTypes";
    public static final String GET_LOCATIONS_API = "http://api.sawatruck.com/api/locatios/favourite";
    public static final String GET_TRUCK_BRAND_API = "http://api.sawatruck.com/Api/VehicleBrands/Get";
    public static final String USER_VIEWPROFILE_API = "http://api.sawatruck.com/api/users/MyInfo";
    public static final String GET_COLOR_API = "http://api.sawatruck.com/Api/ColorMultiLinguals/";
    public static final String GET_TRUCK_CLASSES_API = "http://api.sawatruck.com/Api/TruckClasses";
    public static final String GET_LOADS_API = "http://api.sawatruck.com/api/Loads/Get";
    public static final String VERIFY_PHONE_API = "http://api.sawatruck.com/api/users/VerifyPhoneCode";
    public static final String CONFIRM_PHONE_API = "http://api.sawatruck.com/api/users/ConfirmPhone";
    public static final String ADD_PROMOCODE_API = "http://api.sawatruck.com/Api/UserActiveCopon/post";
    public static final String ADD_TRUCK_API = "http://api.sawatruck.com/api/Trucks/Post";
    public static final String GET_AD_BY_ID_API = "http://api.sawatruck.com/api/Advertisements/Get";
    public static final String GET_MY_NOTIFICATIONS_API = "http://api.sawatruck.com/api/NotificationUser/GetMyNotification";
    public static final String GET_AD_BOOKING_BY_ID_API = "http://api.sawatruck.com/api/AdvertisementBookings/get";
    public static final String TRANSACTION_HISTORY_API = "http://api.sawatruck.com/api/TransactionDetail/search";
    public static final String GET_USER_INBOX_API = "http://api.sawatruck.com/api/Messages/GetMyMessages";
    public static final String GET_MESSAGE_API = "http://api.sawatruck.com/api/Messages/History";
    public static final String SEND_MESSAGE_API = "http://api.sawatruck.com/api/Messages/Post";
    public static final String CANCEL_DELIVERY_API = "http://api.sawatruck.com/api/Travels/Cancel";
    public static final String CHARGE_BALANCE_STRIPE_API = "http://api.sawatruck.com/api/AccountFinance/ChargeByStripe_DoCheckoutPayment";
    public static final String CANCEL_REASON_API = "http://api.sawatruck.com/api/CancelReasons/Get";
    public static final String CONFIRM_OFFER_API = "http://api.sawatruck.com/api/Offers/ConfirmOffer";
    public static final String GET_TRACKING_API = "http://api.sawatruck.com/api/Tracking/getByNumber/";
    public static final long LOCATION_TRACK_INTERVAL = 1000 * 20;

    public static final String LOADER_GET_MY_BOOKING_API = "http://api.sawatruck.com/api/AdvertisementBookings/GetMyBookings";
    public static final String UPLOAD_USER_PHOTO_API = "http://api.sawatruck.com/api/Upload/PostUserPhoto";
    public static final String POST_LOAD_API = "http://api.sawatruck.com/api/Loads/post";
    public static final String GET_ALL_LOAD_TYPES_API = "http://api.sawatruck.com/Api/LoadTypes/Get";
    public static final String SNEAK_PEEK_API = "http://api.sawatruck.com/api/SneakPeek/search";
    public static final String GET_MEASURE_UNIT_API = "http://api.sawatruck.com/Api/MeasureUnits";

    public static final String ACCEPT_OFFER_API = "http://api.sawatruck.com/api/Offers/AcceptOffer";
    public static final String UNACCEPT_OFFER_API = "http://api.sawatruck.com/api/Offers/UnAcceptOffer";
    public static final String REJECT_OFFER_API = "http://api.sawatruck.com/api/Offers/CancelOffer";
    public static final String GET_TRAVEL_BY_ID = "http://api.sawatruck.com/api/travels/get";
    public static final String GET_MY_LOAD_API = "http://api.sawatruck.com/api/Loads/GetMyLoadsByStatus";
    public static final String INVITE_DRIVER_API = "";
    public static final String GET_DRIVER_API = "http://api.sawatruck.com/api/Users/Drivers";

    public static final String AD_SEARCH_API = "http://api.sawatruck.com/api/Advertisements/search";
    public static final String BOOK_AD_API = "http://api.sawatruck.com/api/AdvertisementBookings/Post";

    public static final String CANCEL_ADBOOKING_API = "http://api.sawatruck.com/api/AdvertisementBookings/Cancel";

    public static final int LOAD_TYPE_CAR = 6;
    public static final int LOAD_TYPE_BULK = 3;
    public static final int LOCATION_REQUEST_CODE = 2042;

    public static final String GET_ALL_COUNTRIES_API = "http://api.sawatruck.com/api/Countries/get";
    public static final String GET_CITIES_BY_COUNTRY_API = "http://api.sawatruck.com/api/Cities/GetCitiesNameByCountry";


    public static final String SEARCH_CITY_API = "http://api.sawatruck.com/api/cities/search";

    public static final String VERIFY_EMAIL_API = "http://api.sawatruck.com/api/users/ConfirmEmail";
    public static final String GET_ACTIVE_COUNTRIES_DIAL_CODE_API = "http://api.sawatruck.com/api/Countries/GetActiveCountriesDialCode";


    public enum OfferStatus {
        Deleted(0) , Active(1), Accepted(2), Rejected(3), Canceled(4), Confirmed(5), CanceledAfterConfirmed(6), CanceledAfterAccepted(7);
        public final int index;
        OfferStatus(int index) {
            this.index = index;
        }
    }

    public enum AdvertisementBookingStatus {
        Pending(0), Approved(1),Declined(2),Canceled (3),CanceledAfterApproved(4);
        public final int index;
        AdvertisementBookingStatus(int index) {
            this.index = index;
        }
    }

    public enum AdvertisementStatus {
        Canceled(0),Pending(1),Booked(2), CanceledAfterBooked(3);
        public final int index;
        AdvertisementStatus(int index) {
            this.index = index;
        }
    }

    public enum LoadStatus {
        Active(1), Reserved(2),Completed(3), Canceled(4),CanceledAfterConfirmed(5), CancelBeforConfirmed(6);
        public final int index;
        LoadStatus(int index) {
            this.index = index;
        }
    }

    public enum TravelStatus {
        Active(0),Canceled(1),Completed(2),Rated(3),CollectPayment(4),Deleted(5);
        public final int index;
        TravelStatus(int index) {
            this.index = index;
        }
    }

    public enum TrackingStatus {
        None(0) ,GoToPickUp(1),OnThePickUpWay(2),PickedUp(3),StartRealTravel(4),OnTheWay(5),End(6);
        public final int index;
        TrackingStatus(int index) {
            this.index = index;
        }
    }

}

