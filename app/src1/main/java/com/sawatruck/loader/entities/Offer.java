package com.sawatruck.loader.entities;

/**
 * Created by royal on 9/15/2017.
 */

public class Offer {
    private String ID;
    private String LoadID;
    private String LoadUserID;
    private String TruckID;
    private int TrucksNumber;
    private int CurrencyID;
    private Double Price;
    private String PickupDate;
    private String ExpireDate;
    private String DeliveryDate;
    private String ApprovedDate;
    private String CreationDate;
    private int NumberOfManpOwer;
    private String InsuranceDescription;
    private boolean NeedToDismantle;
    private boolean NeedToMount;
    private boolean WithPackaging;
    private boolean WithTempStorage;
    private String Currency;
    private String FullName;
    private String CarPhoto;
    private String VehicleBrand;
    private String TruckType;
    private String TruckBodyworkType;
    private String CoverLetter;
    private String OfferMessages;
    private Customer User;
    private int Status;
    private String StatusText;
    private Load Load;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLoadID() {
        return LoadID;
    }

    public void setLoadID(String loadID) {
        LoadID = loadID;
    }

    public String getLoadUserID() {
        return LoadUserID;
    }

    public void setLoadUserID(String loadUserID) {
        LoadUserID = loadUserID;
    }

    public String getTruckID() {
        return TruckID;
    }

    public void setTruckID(String truckID) {
        TruckID = truckID;
    }

    public int getTrucksNumber() {
        return TrucksNumber;
    }

    public void setTrucksNumber(int trucksNumber) {
        TrucksNumber = trucksNumber;
    }

    public int getCurrencyID() {
        return CurrencyID;
    }

    public void setCurrencyID(int currencyID) {
        CurrencyID = currencyID;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public String getPickupDate() {
        return PickupDate;
    }

    public void setPickupDate(String pickupDate) {
        PickupDate = pickupDate;
    }

    public String getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(String expireDate) {
        ExpireDate = expireDate;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        DeliveryDate = deliveryDate;
    }

    public String getApprovedDate() {
        return ApprovedDate;
    }

    public void setApprovedDate(String approvedDate) {
        ApprovedDate = approvedDate;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(String creationDate) {
        CreationDate = creationDate;
    }

    public int getNumberOfManpOwer() {
        return NumberOfManpOwer;
    }

    public void setNumberOfManpOwer(int numberOfManpOwer) {
        NumberOfManpOwer = numberOfManpOwer;
    }

    public String getInsuranceDescription() {
        return InsuranceDescription;
    }

    public void setInsuranceDescription(String insuranceDescription) {
        InsuranceDescription = insuranceDescription;
    }

    public boolean isNeedToDismantle() {
        return NeedToDismantle;
    }

    public void setNeedToDismantle(boolean needToDismantle) {
        NeedToDismantle = needToDismantle;
    }

    public boolean isNeedToMount() {
        return NeedToMount;
    }

    public void setNeedToMount(boolean needToMount) {
        NeedToMount = needToMount;
    }

    public boolean isWithPackaging() {
        return WithPackaging;
    }

    public void setWithPackaging(boolean withPackaging) {
        WithPackaging = withPackaging;
    }

    public boolean isWithTempStorage() {
        return WithTempStorage;
    }

    public void setWithTempStorage(boolean withTempStorage) {
        WithTempStorage = withTempStorage;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getCarPhoto() {
        return CarPhoto;
    }

    public void setCarPhoto(String carPhoto) {
        CarPhoto = carPhoto;
    }

    public String getVehicleBrand() {
        return VehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        VehicleBrand = vehicleBrand;
    }

    public String getTruckType() {
        return TruckType;
    }

    public void setTruckType(String truckType) {
        TruckType = truckType;
    }

    public String getTruckBodyworkType() {
        return TruckBodyworkType;
    }

    public void setTruckBodyworkType(String truckBodyworkType) {
        TruckBodyworkType = truckBodyworkType;
    }

    public String getCoverLetter() {
        return CoverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        CoverLetter = coverLetter;
    }

    public String getOfferMessages() {
        return OfferMessages;
    }

    public void setOfferMessages(String offerMessages) {
        OfferMessages = offerMessages;
    }

    public Customer getUser() {
        return User;
    }

    public void setUser(Customer user) {
        User = user;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getStatusText() {
        return StatusText;
    }

    public void setStatusText(String statusText) {
        StatusText = statusText;
    }

    public com.sawatruck.loader.entities.Load getLoad() {
        return Load;
    }

    public void setLoad(com.sawatruck.loader.entities.Load load) {
        Load = load;
    }
}
