package com.example.chatapplication;

public class User {
    private String name;
    private String phoneNumber;
    private String email;
    private String idFirebase;


    public User(String name, String phoneNumber, String email, String idFirebase) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.idFirebase = idFirebase;
    }

    public User() {
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
}
