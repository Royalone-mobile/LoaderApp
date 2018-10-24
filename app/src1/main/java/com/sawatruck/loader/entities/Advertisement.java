package com.sawatruck.loader.entities;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by royal on 8/28/2017.
 */

public class Advertisement implements Serializable{
    private String ID;
    private String TruckImageURL;
    private String PickupCity;
    private String PickupCountry;
    private String PickupDate;
    private String DeliveryCity;
    private String DeliveryCountry;
    private String DeliveryDate;
    private String Budget;
    private String Currency;
    private String Date;
    private String TruckTypeName1;
    private String TruckTypeName2;
    private String Distance;
    private SawaTruckLocation PickupLocation;
    private SawaTruckLocation DeliveryLocation;
    private int Status;
    private ArrayList<AdvertisementBooking> Bookings;
    private String ExpirationDate;
    private Travel Travel;
    private String Available;

    private ArrayList<TravelDetails> TravelDetails;

    private String RecipientId;
    private String RecipientName;
    private String RecipientPhoneNumber;
    private String SenderId;
    private String SenderName;
    private String SenderPhoneNumber;
    private String SenderImageUrl;
    private String SenderRating;

    private String TravelID;
    private String PromoCode;
    private String PaymentType;
    private String DeliveryDescription;


    private String DriverId;
    private String DriverName;
    private String DriverPhoneNumber;
    private String DriverImageUrl;
    private String DriverRating;

    private String ShareText;
    private String ShareLink;
    private int TravelStatus = 0 ;

    private int TrackingStatus;
    private int TravelNumber;

    private Permission Permissions;

    public Advertisement.Permission getPermission() {
        return Permissions;
    }

    public void setPermission(Advertisement.Permission permission) {
        Permissions = permission;
    }

    public int getTravelStatus() {
        return TravelStatus;
    }

    public void setTravelStatus(int travelStatus) {
        TravelStatus = travelStatus;
    }

    public int getTrackingStatus() {
        return TrackingStatus;
    }

    public void setTrackingStatus(int trackingStatus) {
        TrackingStatus = trackingStatus;
    }

    public int getTravelNumber() {
        return TravelNumber;
    }

    public void setTravelNumber(int travelNumber) {
        TravelNumber = travelNumber;
    }

    public  class  Permission {
        private boolean CanAddBooking;
        private boolean IsOwner;

        public boolean isCanAddBooking() {
            return CanAddBooking;
        }

        public void setCanAddBooking(boolean canAddBooking) {
            CanAddBooking = canAddBooking;
        }

        public boolean isOwner() {
            return IsOwner;
        }

        public void setOwner(boolean owner) {
            IsOwner = owner;
        }
    }
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTruckImageURL() {
        return TruckImageURL;
    }

    public void setTruckImageURL(String truckImageURL) {
        TruckImageURL = truckImageURL;
    }

    public String getPickupCity() {
        return PickupCity;
    }

    public void setPickupCity(String pickupCity) {
        PickupCity = pickupCity;
    }

    public String getPickupCountry() {
        return PickupCountry;
    }

    public void setPickupCountry(String pickupCountry) {
        PickupCountry = pickupCountry;
    }

    public String getPickupDate() {
        return PickupDate;
    }

    public void setPickupDate(String pickupDate) {
        PickupDate = pickupDate;
    }

    public String getDeliveryCity() {
        return DeliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        DeliveryCity = deliveryCity;
    }

    public String getDeliveryCountry() {
        return DeliveryCountry;
    }

    public void setDeliveryCountry(String deliveryCountry) {
        DeliveryCountry = deliveryCountry;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        DeliveryDate = deliveryDate;
    }

    public String getBudget() {
        return Budget;
    }

    public void setBudget(String budget) {
        Budget = budget;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
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

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public ArrayList<AdvertisementBooking> getBookings() {
        return Bookings;
    }

    public void setBookings(ArrayList<AdvertisementBooking> bookings) {
        Bookings = bookings;
    }

    public SawaTruckLocation getPickupLocation() {
        return PickupLocation;
    }

    public void setPickupLocation(SawaTruckLocation pickupLocation) {
        PickupLocation = pickupLocation;
    }

    public SawaTruckLocation getDeliveryLocation() {
        return DeliveryLocation;
    }

    public void setDeliveryLocation(SawaTruckLocation deliveryLocation) {
        DeliveryLocation = deliveryLocation;
    }

    public String getExpirationDate() {
        return ExpirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        ExpirationDate = expirationDate;
    }

    public String getAvailable() {
        return Available;
    }

    public void setAvailable(String available) {
        Available = available;
    }

    public String getRecipientId() {
        return RecipientId;
    }

    public void setRecipientId(String recipientId) {
        RecipientId = recipientId;
    }

    public String getRecipientName() {
        return RecipientName;
    }

    public void setRecipientName(String recipientName) {
        RecipientName = recipientName;
    }

    public String getRecipientPhoneNumber() {
        return RecipientPhoneNumber;
    }

    public void setRecipientPhoneNumber(String recipientPhoneNumber) {
        RecipientPhoneNumber = recipientPhoneNumber;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getSenderPhoneNumber() {
        return SenderPhoneNumber;
    }

    public void setSenderPhoneNumber(String senderPhoneNumber) {
        SenderPhoneNumber = senderPhoneNumber;
    }

    public String getSenderImageUrl() {
        return SenderImageUrl;
    }

    public void setSenderImageUrl(String senderImageUrl) {
        SenderImageUrl = senderImageUrl;
    }

    public String getSenderRating() {
        return SenderRating;
    }

    public void setSenderRating(String senderRating) {
        SenderRating = senderRating;
    }

    public ArrayList<com.sawatruck.loader.entities.TravelDetails> getTravelDetails() {
        return TravelDetails;
    }

    public void setTravelDetails(ArrayList<com.sawatruck.loader.entities.TravelDetails> travelDetails) {
        TravelDetails = travelDetails;
    }

    public com.sawatruck.loader.entities.Travel getTravel() {
        return Travel;
    }

    public void setTravel(com.sawatruck.loader.entities.Travel travel) {
        Travel = travel;
    }

    public String getTravelID() {
        return TravelID;
    }

    public void setTravelID(String travelID) {
        TravelID = travelID;
    }

    public String getPromoCode() {
        return PromoCode;
    }

    public void setPromoCode(String promoCode) {
        PromoCode = promoCode;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public String getDeliveryDescription() {
        return DeliveryDescription;
    }

    public void setDeliveryDescription(String deliveryDescription) {
        DeliveryDescription = deliveryDescription;
    }

    public String getDriverId() {
        return DriverId;
    }

    public void setDriverId(String driverId) {
        DriverId = driverId;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getDriverPhoneNumber() {
        return DriverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        DriverPhoneNumber = driverPhoneNumber;
    }

    public String getDriverImageUrl() {
        return DriverImageUrl;
    }

    public void setDriverImageUrl(String driverImageUrl) {
        DriverImageUrl = driverImageUrl;
    }

    public String getDriverRating() {
        return DriverRating;
    }

    public void setDriverRating(String driverRating) {
        DriverRating = driverRating;
    }

    public String getShareLink() {
        return ShareLink;
    }

    public void setShareLink(String shareLink) {
        ShareLink = shareLink;
    }

    public String getShareText() {
        return ShareText;
    }

    public void setShareText(String shareText) {
        ShareText = shareText;
    }
}
