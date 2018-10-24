package com.sawatruck.loader.utils;

import com.google.gson.Gson;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.entities.AddressDetail;
import com.sawatruck.loader.entities.Advertisement;
import com.sawatruck.loader.entities.AdvertisementBooking;
import com.sawatruck.loader.entities.Balance;
import com.sawatruck.loader.entities.Driver;
import com.sawatruck.loader.entities.Load;
import com.sawatruck.loader.entities.SawaTruckLocation;
import com.sawatruck.loader.entities.User;

/**
 * Created by royal on 8/28/2017.
 */

public class Serializer {
    private final Gson gson = BaseApplication.getGson();
    private Serializer() {}

    private static Serializer _instance ;
    static {
        _instance = new Serializer();
    }

    public static Serializer getInstance(){
        return _instance;
    }

    public String serializeUser(User user) {
        String jsonString = gson.toJson(user, User.class);
        return jsonString;
    }

    public User deserializeUser(String jsonString) {
        User user = gson.fromJson(jsonString, User.class);
        return user;
    }

    public String serializeBalance(Balance balance) {
        String jsonString = gson.toJson(balance, Balance.class);
        return jsonString;
    }

    public Balance deserializeBalance(String jsonString) {
        Balance balance = gson.fromJson(jsonString, Balance.class);
        return balance;
    }

    public String serializeLoad(Load load) {
        String jsonString = gson.toJson(load, Load.class);
        return jsonString;
    }

    public Load deserializeLoad(String strLoad) {
        Load load = gson.fromJson(strLoad, Load.class);
        return load;
    }

    public SawaTruckLocation deserializeLocation(String strFromLocation) {
        SawaTruckLocation location = gson.fromJson(strFromLocation, SawaTruckLocation.class);
        return location;
    }

    public String serializeLocation(SawaTruckLocation toLocation) {
        String jsonString = gson.toJson(toLocation, SawaTruckLocation.class);
        return jsonString;
    }

    public String serializeAddressDetail(AddressDetail addressDetail) {
        String jsonString = gson.toJson(addressDetail, AddressDetail.class);
        return jsonString;
    }

    public AddressDetail deserializeAddressDetail(String strAddressDetail) {
        AddressDetail addressDetail = gson.fromJson(strAddressDetail, AddressDetail.class);
        return addressDetail;
    }

    public String serializeBookingDetails(AdvertisementBooking advertisementBooking) {
        String jsonString = gson.toJson(advertisementBooking, AdvertisementBooking.class);
        return jsonString;
    }

    public AdvertisementBooking deserializeBookingDetails(String details) {
        AdvertisementBooking advertisementBooking = gson.fromJson(details, AdvertisementBooking.class);
        return advertisementBooking;
    }

    public String serializeDriver(Driver driver) {
        String jsonString = gson.toJson(driver, Driver.class);
        return jsonString;
    }

    public Driver deserializeDriver(String strDriver) {
        Driver driver = gson.fromJson(strDriver, Driver.class);
        return driver;
    }

    public String serializeAdvertisement(Advertisement advertisement) {
        String jsonString = gson.toJson(advertisement, Advertisement.class);
        return jsonString;
    }

    public Advertisement deserializeAdvertisement(String strAdvertisement) {
        Advertisement advertisement = gson.fromJson(strAdvertisement, Advertisement.class);
        return advertisement;
    }
}
