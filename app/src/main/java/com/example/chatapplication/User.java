package com.example.chatapplication;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class User {
    private String name;
    private String phoneNumber;
    private String email;
    private String idFirebase;
    private String address;
    private Bitmap picture;


    public User(String name, String phoneNumber, String email, String idFirebase,String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.idFirebase = idFirebase;
        this.address = address;
    }

    public User(String name, String email, String idFirebase, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.idFirebase = idFirebase;
        this.phoneNumber = phoneNumber;
    }

    public User(String name) {
        this.name = name;
    }

    public User() {
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdFirebase() {
        return idFirebase;
    }

    public void setIdFirebase(String idFirebase) {
        this.idFirebase = idFirebase;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
