package com.example.firebasemessages.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

public class Message {

    private String id;
    private String firstname;
    private String lastname;
    private String message;
    private GeoPoint position;

    public Message() {
    }

    public Message(String firstname, String lastname, double latitude, double longitude, String message) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.position = new GeoPoint(latitude, longitude);
        this.message = message;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLatitude() {
        return String.valueOf(position.getLatitude());
    }

    public String getLongitude() {
        return String.valueOf(position.getLongitude());
    }

    public GeoPoint getPosition() {
        return position;
    }

    public void setPosition(GeoPoint position) {
        this.position = position;
    }
}