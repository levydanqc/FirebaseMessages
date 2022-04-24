package com.example.geomessages.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message {

    private String firstname;
    private String lastname;
    @NonNull
    @PrimaryKey
    private String picture;
    private String latitude;
    private String longitude;
    private String message;

    public Message() {
    }

    public Message(String firstname, String lastname, @NonNull String picture, double latitude, double longitude, String message) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.picture = picture;
        this.latitude = Double.toString(latitude);
        this.longitude = Double.toString(longitude);
        this.message = message;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}