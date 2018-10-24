package com.sawatruck.loader.entities;

/**
 * Created by royal on 8/29/2017.
 */

public class TruckType {
    private Integer ID;
    private String Name;
    private String Photo;
    private String ClassID;



    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public String getClassID() {
        return ClassID;
    }

    public void setClassID(String classID) {
        ClassID = classID;
    }
}
