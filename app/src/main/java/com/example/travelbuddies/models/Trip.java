package com.example.travelbuddies.models;

import java.io.Serializable;

public class Trip implements Serializable {
    private String tripId;
    private String title;
    private String location;
    private String description;
    private String duration;
    private double price;
    private double rating;
    private String imageUrl;

    public Trip() {
    }

    public Trip(String tripId, String title, String location, String description, String duration, double price, double rating, String imageUrl) {
        this.tripId = tripId;
        this.title = title;
        this.location = location;
        this.description = description;
        this.duration = duration;
        this.price = price;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}