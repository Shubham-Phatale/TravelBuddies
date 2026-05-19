package com.example.travelbuddies.utils;

public class Constants {

    // Database
    public static final String DATABASE_NAME = "travel.db";
    public static final int DATABASE_VERSION = 2;

    // Users Table
    public static final String TABLE_USERS = "users";
    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_PASSWORD = "password";
    public static final String KEY_USER_PHONE = "phone_number";

    // Trips Table
    public static final String TABLE_TRIPS = "trips";
    public static final String KEY_TRIP_ID = "id";
    public static final String KEY_TRIP_TITLE = "title";
    public static final String KEY_TRIP_DESCRIPTION = "description";
    public static final String KEY_TRIP_IMAGE_URL = "image_url";
    public static final String KEY_TRIP_LOCATION = "location";
    public static final String KEY_TRIP_PRICE = "price";
    public static final String KEY_TRIP_DAYS = "days";

    // Bookings Table
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String KEY_BOOKING_ID = "id";
    public static final String KEY_BOOKING_USER_ID = "user_id";
    public static final String KEY_BOOKING_TRIP_ID = "trip_id";
    public static final String KEY_BOOKING_TRIP_TITLE = "trip_title";
    public static final String KEY_BOOKING_DATE = "booking_date";
    public static final String KEY_BOOKING_PEOPLE = "people";
    public static final String KEY_BOOKING_TOTAL_COST = "total_cost";

    // Session Management (SharedPreferences)
    public static final String PREF_NAME = "TravelBuddiesPref";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_LOGGED_IN_USER_EMAIL = "loggedInUserEmail";
}