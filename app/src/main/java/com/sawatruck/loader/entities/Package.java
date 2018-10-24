package com.sawatruck.loader.entities;

/**
 * Created by royal on 9/26/2017.
 */

public class Package {
    private TruckType truckType;
    private TruckBrand truckBrand;
    private String Model;
    private MeasureUnit measureUnit;
    private String Quantity;
    private Long UnitWeight;
    private BulkType bulkType;

    public Package(){
        UnitWeight = 0L;
    }
    public TruckType getTruckType() {
        return truckType;
    }

    public void setTruckType(TruckType truckType) {
        this.truckType = truckType;
    }

    public TruckBrand getTruckBrand() {
        return truckBrand;
    }

    public void setTruckBrand(TruckBrand truckBrand) {
        this.truckBrand = truckBrand;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public MeasureUnit getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(MeasureUnit measureUnit) {
        this.measureUnit = measureUnit;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public Long getUnitWeight() {
        return UnitWeight;
    }

    public void setUnitWeight(Long unitWeight) {
        UnitWeight = unitWeight;
    }

    public BulkType getBulkType() {
        return bulkType;
    }

    public void setBulkType(BulkType bulkType) {
        this.bulkType = bulkType;
    }
}
