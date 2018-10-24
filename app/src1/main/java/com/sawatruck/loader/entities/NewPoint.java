package com.sawatruck.loader.entities;

/**
 * Created by royal on 11/11/2017.
 */

public class NewPoint {
    private String ID;
    private String TrackingSessionID;
    private String TrackingSessionNumber;
    private String TrackingSessionStartDate;
    private String TrackingSessionEndDate;
    private int Status;
    private String TextStatus;
    private Double Lat;
    private Double Long;
    private String SenderID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTrackingSessionID() {
        return TrackingSessionID;
    }

    public void setTrackingSessionID(String trackingSessionID) {
        TrackingSessionID = trackingSessionID;
    }

    public String getTrackingSessionNumber() {
        return TrackingSessionNumber;
    }

    public void setTrackingSessionNumber(String trackingSessionNumber) {
        TrackingSessionNumber = trackingSessionNumber;
    }

    public String getTrackingSessionStartDate() {
        return TrackingSessionStartDate;
    }

    public void setTrackingSessionStartDate(String trackingSessionStartDate) {
        TrackingSessionStartDate = trackingSessionStartDate;
    }

    public String getTrackingSessionEndDate() {
        return TrackingSessionEndDate;
    }

    public void setTrackingSessionEndDate(String trackingSessionEndDate) {
        TrackingSessionEndDate = trackingSessionEndDate;
    }

    public String getTextStatus() {
        return TextStatus;
    }

    public void setTextStatus(String textStatus) {
        TextStatus = textStatus;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLong() {
        return Long;
    }

    public void setLong(Double aLong) {
        Long = aLong;
    }

    public String getSenderID() {
        return SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
