package com.example.bitnest_android_2207086;

public class Room {
    public String roomId;
    public String roomNumber;
    public String type;
    public double price;
    public boolean isAvailable;

    public Room() {
    }

    public Room(String roomId, String roomNumber, String type, double price, boolean isAvailable) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.isAvailable = isAvailable;
    }
}
