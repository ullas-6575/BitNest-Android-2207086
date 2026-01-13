package com.example.bitnest_android_2207086;

import java.util.ArrayList;

public class Booking {
    public String guestId;
    public String name;
    public String phone;
    public String checkIn;
    public String checkOut;
    public ArrayList<String> bookedRooms;

    public Booking() {

    }

    public Booking(String guestId, String name, String phone, String checkIn, String checkOut, ArrayList<String> bookedRooms) {
        this.guestId = guestId;
        this.name = name;
        this.phone = phone;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.bookedRooms = bookedRooms;
    }
}