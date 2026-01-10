package com.example.bitnest_android_2207086;

public class Guest {
    public String guestId;
    public String fullName;
    public String phoneNumber;
    public String email;

    public Guest() { }

    public Guest(String guestId, String fullName, String phoneNumber, String email) {
        this.guestId = guestId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}