package com.example.travelbuddies.models;

import java.io.Serializable;

public class ItineraryDay implements Serializable {
    private String id;
    private String tripId;
    private int dayNumber;
    private String title;
    private String description;
    private String activities; // newline-separated activity list

    public ItineraryDay(String id, String tripId, int dayNumber, String title, String description, String activities) {
        this.id = id;
        this.tripId = tripId;
        this.dayNumber = dayNumber;
        this.title = title;
        this.description = description;
        this.activities = activities;
    }

    public String getId() { return id; }
    public String getTripId() { return tripId; }
    public int getDayNumber() { return dayNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getActivities() { return activities; }
}
