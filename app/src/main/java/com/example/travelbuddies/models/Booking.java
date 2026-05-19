package com.example.travelbuddies.models;

import java.io.Serializable;

public class Booking implements Serializable {
    private String bookingId;
    private String userId;
    private String tripId;
    private String bookingDate;
    private int numberOfPeople;
    private String status;

    public Booking() {
    }

    public Booking(String bookingId, String userId, String tripId, String bookingDate, int numberOfPeople, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.tripId = tripId;
        this.bookingDate = bookingDate;
        this.numberOfPeople = numberOfPeople;
        this.status = status;
    }

    // Getters and Setters

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}