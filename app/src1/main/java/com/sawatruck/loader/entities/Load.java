package com.sawatruck.loader.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by royal on 8/31/2017.
 */

public class Load {
    private String LoadID;
    private String ID;
    private boolean HasLoadingStations;
    private String PublishDate;
    private String LoadDateFrom;
    private String LoadDateEnd;
    private String UnloadDateFrom;
    private String UnloadDateEnd;
    private String SawaTruckUserID;
    private String Distance;
    private String BestOffer;
    private int CurrencyID;
    private String Budget;
    private int TruckTypeID;
    private int TruckBodyworkTypeID;
    private int OffersCount;
    private String currency;
    private SawaTruckLocation FromLocation;
    private SawaTruckLocation ToLocation;
    private ArrayList<OfferDetail> Offers;
    private Customer User;
    private ArrayList<LoadPhoto> LoadPhotos;
    private ArrayList<TravelDetails> TravelDetails;

    private String ReceiverName;
    private boolean IsCompleteTruckReservation;
    private String ReceiverPhone;
    private boolean IsMyLoad;
    private String LoadDetails;
    private boolean followed;
    private String LoadType;
    private int Status;
    private int CommentsCount;
    private String StatusText;
    private String Name;
    private int TravelStatus = 0 ;
    private String OfferPrice;

    private String TravelID;

    @SerializedName("Driver")
    private Driver driver;
    private String ReceiverID;

    private String ShareText;
    private String ShareLink;

    private Travel Travel;
    private int TravelNumber;
    private Truck Truck;

    private String TruckTypeName1;
    private String TruckTypeName2;

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }


    public String getLoadID() {
        return LoadID;
    }

    public void setLoadID(String loadID) {
        LoadID = loadID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public boolean isHasLoadingStations() {
        return HasLoadingStations;
    }

    public void setHasLoadingStations(boolean hasLoadingStations) {
        HasLoadingStations = hasLoadingStations;
    }

    public String getPublishDate() {
        return PublishDate;
    }

    public void setPublishDate(String publishDate) {
        PublishDate = publishDate;
    }

    public String getLoadDateFrom() {
        return LoadDateFrom;
    }

    public void setLoadDateFrom(String loadDateFrom) {
        LoadDateFrom = loadDateFrom;
    }

    public String getLoadDateEnd() {
        return LoadDateEnd;
    }

    public void setLoadDateEnd(String loadDateEnd) {
        LoadDateEnd = loadDateEnd;
    }

    public String getUnloadDateFrom() {
        return UnloadDateFrom;
    }

    public void setUnloadDateFrom(String unloadDateFrom) {
        UnloadDateFrom = unloadDateFrom;
    }

    public String getUnloadDateEnd() {
        return UnloadDateEnd;
    }

    public void setUnloadDateEnd(String unloadDateEnd) {
        UnloadDateEnd = unloadDateEnd;
    }

    public String getSawaTruckUserID() {
        return SawaTruckUserID;
    }

    public void setSawaTruckUserID(String sawaTruckUserID) {
        SawaTruckUserID = sawaTruckUserID;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getBestOffer() {
        return BestOffer;
    }

    public void setBestOffer(String bestOffer) {
        BestOffer = bestOffer;
    }

    public int getCurrencyID() {
        return CurrencyID;
    }

    public void setCurrencyID(int currencyID) {
        CurrencyID = currencyID;
    }

    public String getBudget() {
        return Budget;
    }


    public void setBudget(String budget) {
        Budget = budget;
    }

    public int getTruckTypeID() {
        return TruckTypeID;
    }

    public void setTruckTypeID(int truckTypeID) {
        TruckTypeID = truckTypeID;
    }

    public int getTruckBodyworkTypeID() {
        return TruckBodyworkTypeID;
    }

    public void setTruckBodyworkTypeID(int truckBodyworkTypeID) {
        TruckBodyworkTypeID = truckBodyworkTypeID;
    }

    public int getOffersCount() {
        return OffersCount;
    }

    public void setOffersCount(int offersCount) {
        OffersCount = offersCount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public SawaTruckLocation getFromLocation() {
        return FromLocation;
    }

    public void setFromLocation(SawaTruckLocation fromLocation) {
        FromLocation = fromLocation;
    }

    public SawaTruckLocation getToLocation() {
        return ToLocation;
    }

    public void setToLocation(SawaTruckLocation toLocation) {
        ToLocation = toLocation;
    }

    public Customer getUser() {
        return User;
    }

    public void setUser(Customer user) {
        this.User = user;
    }

    public ArrayList<LoadPhoto> getLoadPhotos() {
        return LoadPhotos;
    }

    public void setLoadPhotos(ArrayList<LoadPhoto> loadPhotos) {
        LoadPhotos = loadPhotos;
    }

//    public String getLoadParcel() {
//        return LoadParcel;
//    }
//
//    public void setLoadParcel(String loadParcel) {
//        LoadParcel = loadParcel;
//    }
//
//    public String getLoadPalletized() {
//        return LoadPalletized;
//    }
//
//    public void setLoadPalletized(String loadPalletized) {
//        LoadPalletized = loadPalletized;
//    }
//
//    public String getLoadBulk() {
//        return LoadBulk;
//    }
//
//    public void setLoadBulk(String loadBulk) {
//        LoadBulk = loadBulk;
//    }
//
//    public String getLoadVehicle() {
//        return LoadVehicle;
//    }
//
//    public void setLoadVehicle(String loadVehicle) {
//        LoadVehicle = loadVehicle;
//    }
//
//    public String getLoadBoat() {
//        return LoadBoat;
//    }
//
//    public void setLoadBoat(String loadBoat) {
//        LoadBoat = loadBoat;
//    }
//
//    public String getLoadEquipment() {
//        return LoadEquipment;
//    }
//
//    public void setLoadEquipment(String loadEquipment) {
//        LoadEquipment = loadEquipment;
//    }
//
//    public String getLoadFurniture() {
//        return LoadFurniture;
//    }
//
//    public void setLoadFurniture(String loadFurniture) {
//        LoadFurniture = loadFurniture;
//    }
//
//    public String getLoadContainer() {
//        return LoadContainer;
//    }
//
//    public void setLoadContainer(String loadContainer) {
//        LoadContainer = loadContainer;
//    }
//
//    public String getLoadClearance() {
//        return LoadClearance;
//    }
//
//    public void setLoadClearance(String loadClearance) {
//        LoadClearance = loadClearance;
//    }
//
//    public String getLoadManpower() {
//        return LoadManpower;
//    }
//
//    public void setLoadManpower(String loadManpower) {
//        LoadManpower = loadManpower;
//    }
//
//    public String getLoadMovingHome() {
//        return LoadMovingHome;
//    }
//
//    public void setLoadMovingHome(String loadMovingHome) {
//        LoadMovingHome = loadMovingHome;
//    }

    public String getReceiverName() {
        return ReceiverName;
    }

    public void setReceiverName(String receiverName) {
        ReceiverName = receiverName;
    }

    public boolean isCompleteTruckReservation() {
        return IsCompleteTruckReservation;
    }

    public void setCompleteTruckReservation(boolean completeTruckReservation) {
        IsCompleteTruckReservation = completeTruckReservation;
    }

    public String getReceiverPhone() {
        return ReceiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        ReceiverPhone = receiverPhone;
    }

    public boolean isMyLoad() {
        return IsMyLoad;
    }

    public void setMyLoad(boolean myLoad) {
        IsMyLoad = myLoad;
    }

    public String getLoadDetails() {
        return LoadDetails;
    }

    public void setLoadDetails(String loadDetails) {
        LoadDetails = loadDetails;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public String getLoadType() {
        return LoadType;
    }

    public void setLoadType(String loadType) {
        LoadType = loadType;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getCommentsCount() {
        return CommentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        CommentsCount = commentsCount;
    }

    public String getStatusText() {
        return StatusText;
    }

    public void setStatusText(String statusText) {
        StatusText = statusText;
    }



    public ArrayList<OfferDetail> getOffers() {
        return Offers;
    }

    public void setOffers(ArrayList<OfferDetail> offers) {
        Offers = offers;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<com.sawatruck.loader.entities.TravelDetails> getTravelDetails() {
        return TravelDetails;
    }

    public void setTravelDetails(ArrayList<com.sawatruck.loader.entities.TravelDetails> travelDetails) {
        TravelDetails = travelDetails;
    }

    public String getTravelID() {
        return TravelID;
    }

    public void setTravelID(String travelID) {
        TravelID = travelID;
    }


    public String getReceiverID() {
        return ReceiverID;
    }

    public void setReceiverID(String receiverID) {
        ReceiverID = receiverID;
    }

    public String getShareText() {
        return ShareText;
    }

    public void setShareText(String shareText) {
        ShareText = shareText;
    }

    public String getShareLink() {
        return ShareLink;
    }

    public void setShareLink(String shareLink) {
        ShareLink = shareLink;
    }

    public com.sawatruck.loader.entities.Travel getTravel() {
        return Travel;
    }

    public void setTravel(com.sawatruck.loader.entities.Travel travel) {
        Travel = travel;
    }

    public int getTravelStatus() {
        return TravelStatus;
    }

    public void setTravelStatus(int travelStatus) {
        TravelStatus = travelStatus;
    }

    public String getOfferPrice() {
        return OfferPrice;
    }

    public void setOfferPrice(String offerPrice) {
        OfferPrice = offerPrice;
    }

    public int getTravelNumber() {
        return TravelNumber;
    }

    public void setTravelNumber(int travelNumber) {
        TravelNumber = travelNumber;
    }

    public Truck getTruck() {
        return Truck;
    }

    public void setTruck(Truck truck) {
        this.Truck = truck;
    }

    public String getTruckTypeName1() {
        return TruckTypeName1;
    }

    public void setTruckTypeName1(String truckTypeName1) {
        TruckTypeName1 = truckTypeName1;
    }

    public String getTruckTypeName2() {
        return TruckTypeName2;
    }

    public void setTruckTypeName2(String truckTypeName2) {
        TruckTypeName2 = truckTypeName2;
    }
}

