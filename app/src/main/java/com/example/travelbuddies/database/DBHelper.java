package com.example.travelbuddies.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.travelbuddies.models.Booking;
import com.example.travelbuddies.models.ItineraryDay;
import com.example.travelbuddies.models.Trip;
import com.example.travelbuddies.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TravelBuddies.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TAG = "DBHelper";

    // User Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "userId";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_PHONE = "phoneNumber";

    // Trip Table
    private static final String TABLE_TRIPS = "trips";
    private static final String COLUMN_TRIP_ID = "tripId";
    private static final String COLUMN_TRIP_TITLE = "title";
    private static final String COLUMN_TRIP_LOCATION = "location";
    private static final String COLUMN_TRIP_DESCRIPTION = "description";
    private static final String COLUMN_TRIP_DURATION = "duration";
    private static final String COLUMN_TRIP_PRICE = "price";
    private static final String COLUMN_TRIP_RATING = "rating";
    private static final String COLUMN_TRIP_IMAGE_URL = "imageUrl";

    // Booking Table
    private static final String TABLE_BOOKINGS = "bookings";
    private static final String COLUMN_BOOKING_ID = "bookingId";
    private static final String COLUMN_BOOKING_USER_ID = "userId";
    private static final String COLUMN_BOOKING_TRIP_ID = "tripId";
    private static final String COLUMN_BOOKING_DATE = "bookingDate";
    private static final String COLUMN_BOOKING_PEOPLE = "numberOfPeople";
    private static final String COLUMN_BOOKING_STATUS = "status";

    // Itinerary Table
    private static final String TABLE_ITINERARY = "itinerary";
    private static final String COLUMN_ITIN_ID = "itinId";
    private static final String COLUMN_ITIN_TRIP_ID = "tripId";
    private static final String COLUMN_ITIN_DAY = "dayNumber";
    private static final String COLUMN_ITIN_TITLE = "title";
    private static final String COLUMN_ITIN_DESC = "description";
    private static final String COLUMN_ITIN_ACTIVITIES = "activities";

    // Transport Table
    private static final String TABLE_TRANSPORT = "transport_bookings";
    private static final String COLUMN_TRANSPORT_ID = "transportId";
    private static final String COLUMN_TRANSPORT_BOOKING_ID = "bookingId";
    private static final String COLUMN_TRANSPORT_MODE = "mode";
    private static final String COLUMN_TRANSPORT_PRICE = "price";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " TEXT PRIMARY KEY,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT,"
                + COLUMN_USER_PHONE + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Trips table
        String CREATE_TRIPS_TABLE = "CREATE TABLE " + TABLE_TRIPS + "("
                + COLUMN_TRIP_ID + " TEXT PRIMARY KEY,"
                + COLUMN_TRIP_TITLE + " TEXT,"
                + COLUMN_TRIP_LOCATION + " TEXT,"
                + COLUMN_TRIP_DESCRIPTION + " TEXT,"
                + COLUMN_TRIP_DURATION + " TEXT,"
                + COLUMN_TRIP_PRICE + " REAL,"
                + COLUMN_TRIP_RATING + " REAL,"
                + COLUMN_TRIP_IMAGE_URL + " TEXT" + ")";
        db.execSQL(CREATE_TRIPS_TABLE);

        // Create Bookings table with the new COLUMN_BOOKING_PEOPLE
        String CREATE_BOOKINGS_TABLE = "CREATE TABLE " + TABLE_BOOKINGS + "("
                + COLUMN_BOOKING_ID + " TEXT PRIMARY KEY,"
                + COLUMN_BOOKING_USER_ID + " TEXT,"
                + COLUMN_BOOKING_TRIP_ID + " TEXT,"
                + COLUMN_BOOKING_DATE + " TEXT,"
                + COLUMN_BOOKING_PEOPLE + " INTEGER,"
                + COLUMN_BOOKING_STATUS + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_BOOKING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_BOOKING_TRIP_ID + ") REFERENCES " + TABLE_TRIPS + "(" + COLUMN_TRIP_ID + ")"
                + ")";
        db.execSQL(CREATE_BOOKINGS_TABLE);

        String CREATE_ITINERARY_TABLE = "CREATE TABLE " + TABLE_ITINERARY + "("
                + COLUMN_ITIN_ID + " TEXT PRIMARY KEY,"
                + COLUMN_ITIN_TRIP_ID + " TEXT,"
                + COLUMN_ITIN_DAY + " INTEGER,"
                + COLUMN_ITIN_TITLE + " TEXT,"
                + COLUMN_ITIN_DESC + " TEXT,"
                + COLUMN_ITIN_ACTIVITIES + " TEXT)";
        db.execSQL(CREATE_ITINERARY_TABLE);

        String CREATE_TRANSPORT_TABLE = "CREATE TABLE " + TABLE_TRANSPORT + "("
                + COLUMN_TRANSPORT_ID + " TEXT PRIMARY KEY,"
                + COLUMN_TRANSPORT_BOOKING_ID + " TEXT,"
                + COLUMN_TRANSPORT_MODE + " TEXT,"
                + COLUMN_TRANSPORT_PRICE + " REAL" + ")";
        db.execSQL(CREATE_TRANSPORT_TABLE);
        Log.d(TAG, "Database created with all tables.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITINERARY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSPORT);
        onCreate(db);
        Log.d(TAG, "Database upgraded and tables recreated.");
    }

    // User methods
    public synchronized boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getUserId());
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_PHONE, user.getPhoneNumber());

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public synchronized User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USER_EMAIL + " = ?", new String[]{email}, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE))
            );
            cursor.close();
        }
        db.close();
        return user;
    }

    // New method to get a user by their ID
    public synchronized User getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USER_ID + " = ?", new String[]{userId}, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE))
            );
            cursor.close();
        }
        db.close();
        return user;
    }

    // Trip methods
    public synchronized boolean addTrip(Trip trip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRIP_ID, trip.getTripId());
        values.put(COLUMN_TRIP_TITLE, trip.getTitle());
        values.put(COLUMN_TRIP_LOCATION, trip.getLocation());
        values.put(COLUMN_TRIP_DESCRIPTION, trip.getDescription());
        values.put(COLUMN_TRIP_DURATION, trip.getDuration());
        values.put(COLUMN_TRIP_PRICE, trip.getPrice());
        values.put(COLUMN_TRIP_RATING, trip.getRating());
        values.put(COLUMN_TRIP_IMAGE_URL, trip.getImageUrl());

        long result = db.insert(TABLE_TRIPS, null, values);
        db.close();
        return result != -1;
    }

    public synchronized List<Trip> getAllTrips() {
        List<Trip> tripList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TRIPS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Trip trip = new Trip();
                trip.setTripId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_ID)));
                trip.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_TITLE)));
                trip.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_LOCATION)));
                trip.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_DESCRIPTION)));
                trip.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_DURATION)));
                trip.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TRIP_PRICE)));
                trip.setRating(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TRIP_RATING)));
                trip.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_IMAGE_URL)));
                tripList.add(trip);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tripList;
    }

    public synchronized Trip getTripById(String tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRIPS, null, COLUMN_TRIP_ID + " = ?", new String[]{tripId}, null, null, null);
        Trip trip = null;
        if (cursor != null && cursor.moveToFirst()) {
            trip = new Trip(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_LOCATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_DURATION)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TRIP_PRICE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TRIP_RATING)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRIP_IMAGE_URL))
            );
            cursor.close();
        }
        db.close();
        return trip;
    }

    public synchronized void addSampleTrips() {
        if (getAllTrips().size() == 0) {
            String[][] trips = {
                { UUID.randomUUID().toString(), "Grand Canyon Trek", "Arizona, USA", "Hike the rim trails of one of the world's most spectacular natural wonders. Witness breathtaking sunrises, towering red rock formations, and the mighty Colorado River below.", "4 Days", "1200.00", "4.9", "https://picsum.photos/seed/grandcanyon/800/500" },
                { UUID.randomUUID().toString(), "New York City Explorer", "New York, USA", "Experience the energy of the city that never sleeps. Visit Times Square, Central Park, the Statue of Liberty, world-class museums, and iconic Broadway shows.", "5 Days", "1800.00", "4.7", "https://picsum.photos/seed/newyork/800/500" },
                { UUID.randomUUID().toString(), "Yellowstone Adventure", "Wyoming, USA", "Discover America's first national park. Marvel at Old Faithful, vibrant hot springs, vast meadows, and incredible wildlife including bison, wolves, and grizzly bears.", "6 Days", "1400.00", "4.8", "https://picsum.photos/seed/yellowstone/800/500" },
                { UUID.randomUUID().toString(), "Miami Beach Escape", "Florida, USA", "Soak up the sun on the iconic white-sand beaches of Miami. Enjoy vibrant nightlife, world-class dining, Art Deco architecture, and the crystal-clear waters of South Beach.", "5 Days", "1600.00", "4.6", "https://picsum.photos/seed/miami/800/500" },
                { UUID.randomUUID().toString(), "San Francisco Bay Tour", "California, USA", "Cross the Golden Gate Bridge, explore Fisherman's Wharf, ride the historic cable cars, and discover the eclectic neighborhoods of one of America's most iconic cities.", "4 Days", "1500.00", "4.7", "https://picsum.photos/seed/sanfrancisco/800/500" },
                { UUID.randomUUID().toString(), "Hawaii Island Hopping", "Hawaii, USA", "Explore the paradise islands of Hawaii. From the volcanic landscapes of the Big Island to the lush valleys of Kauai and the golden beaches of Maui.", "7 Days", "2800.00", "4.9", "https://picsum.photos/seed/hawaii/800/500" },
            };
            for (String[] t : trips) {
                Trip trip = new Trip(t[0], t[1], t[2], t[3], t[4], Double.parseDouble(t[5]), Double.parseDouble(t[6]), t[7]);
                addTrip(trip);
                addSampleItinerary(t[0], t[2]);
            }
        }
    }

    // Booking methods
    public synchronized boolean addBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOKING_ID, booking.getBookingId());
        values.put(COLUMN_BOOKING_USER_ID, booking.getUserId());
        values.put(COLUMN_BOOKING_TRIP_ID, booking.getTripId());
        values.put(COLUMN_BOOKING_DATE, booking.getBookingDate());
        values.put(COLUMN_BOOKING_PEOPLE, booking.getNumberOfPeople());
        values.put(COLUMN_BOOKING_STATUS, booking.getStatus());
        long result = db.insert(TABLE_BOOKINGS, null, values);
        db.close();
        return result != -1;
    }

    public synchronized List<Booking> getBookingsByUserId(String userId) {
        List<Booking> bookingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKINGS, null, COLUMN_BOOKING_USER_ID + " = ?", new String[]{userId}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Booking booking = new Booking();
                booking.setBookingId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_ID)));
                booking.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_USER_ID)));
                booking.setTripId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_TRIP_ID)));
                booking.setBookingDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_DATE)));
                booking.setNumberOfPeople(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_PEOPLE)));
                booking.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKING_STATUS)));
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bookingList;
    }

    public synchronized boolean cancelBooking(String bookingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSPORT, COLUMN_TRANSPORT_BOOKING_ID + "=?", new String[]{bookingId});
        int rows = db.delete(TABLE_BOOKINGS, COLUMN_BOOKING_ID + "=?", new String[]{bookingId});
        db.close();
        return rows > 0;
    }

    public synchronized boolean deleteAllBookings() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_BOOKINGS, null, null);
        db.close();
        Log.d(TAG, "Deleted " + rowsDeleted + " rows from bookings table.");
        return rowsDeleted > 0;
    }

    // Returns null if no transport booked yet, otherwise returns the mode string
    public synchronized String getTransportModeByBookingId(String bookingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRANSPORT, new String[]{COLUMN_TRANSPORT_MODE},
                COLUMN_TRANSPORT_BOOKING_ID + " = ?", new String[]{bookingId},
                null, null, null);
        String mode = null;
        if (cursor != null && cursor.moveToFirst()) {
            mode = cursor.getString(0);
            cursor.close();
        }
        db.close();
        return mode;
    }

    public synchronized boolean updateTransportBooking(String bookingId, String mode, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRANSPORT_MODE, mode);
        values.put(COLUMN_TRANSPORT_PRICE, price);
        int rows = db.update(TABLE_TRANSPORT, values, COLUMN_TRANSPORT_BOOKING_ID + " = ?", new String[]{bookingId});
        db.close();
        return rows > 0;
    }

    public synchronized boolean addTransportBooking(String bookingId, String mode, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRANSPORT_ID, java.util.UUID.randomUUID().toString());
        values.put(COLUMN_TRANSPORT_BOOKING_ID, bookingId);
        values.put(COLUMN_TRANSPORT_MODE, mode);
        values.put(COLUMN_TRANSPORT_PRICE, price);
        long result = db.insert(TABLE_TRANSPORT, null, values);
        db.close();
        return result != -1;
    }

    // ── Itinerary ──────────────────────────────────────────────────────────────

    private void addItineraryDay(SQLiteDatabase db, String tripId, int day, String title, String desc, String activities) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_ITIN_ID, UUID.randomUUID().toString());
        v.put(COLUMN_ITIN_TRIP_ID, tripId);
        v.put(COLUMN_ITIN_DAY, day);
        v.put(COLUMN_ITIN_TITLE, title);
        v.put(COLUMN_ITIN_DESC, desc);
        v.put(COLUMN_ITIN_ACTIVITIES, activities);
        db.insert(TABLE_ITINERARY, null, v);
    }

    public synchronized void addSampleItinerary(String tripId, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor check = db.query(TABLE_ITINERARY, new String[]{COLUMN_ITIN_ID},
                COLUMN_ITIN_TRIP_ID + "=?", new String[]{tripId}, null, null, null);
        boolean exists = check.getCount() > 0;
        check.close();
        if (exists) { db.close(); return; }

        switch (location) {
            case "Arizona, USA":
                addItineraryDay(db, tripId, 1, "Arrival & South Rim",
                        "Settle in and take in your first breathtaking view of the canyon.",
                        "✈ Arrive at Flagstaff Airport\n🚌 Transfer to Grand Canyon Village\n🌅 Sunset at Mather Point\n🍽 Welcome dinner at El Tovar");
                addItineraryDay(db, tripId, 2, "Bright Angel Trail",
                        "Hike one of the most iconic trails in the world down into the canyon.",
                        "🥾 Bright Angel Trail (9-mile hike)\n🦅 Wildlife spotting: California Condors\n📸 Photography at Indian Garden\n🌙 Stargazing program at night");
                addItineraryDay(db, tripId, 3, "Colorado River Rafting",
                        "Experience the canyon from the bottom up on a guided rafting adventure.",
                        "🚣 Half-day Colorado River rafting\n🏊 Swimming at Havasu Creek\n🪨 Geology talk with park ranger\n🔥 Riverside campfire dinner");
                addItineraryDay(db, tripId, 4, "Desert View & Departure",
                        "Catch a stunning sunrise before heading home with memories to last a lifetime.",
                        "🌄 Sunrise at Desert View Watchtower\n🛍 Souvenir shopping at the village\n🚌 Transfer to Flagstaff Airport\n✈ Departure");
                break;

            case "New York, USA":
                addItineraryDay(db, tripId, 1, "Welcome to the Big Apple",
                        "Arrive and dive straight into the energy of New York City.",
                        "✈ Arrive at JFK Airport\n🚕 Transfer to Midtown hotel\n🌆 Times Square evening walk\n🎭 Broadway show");
                addItineraryDay(db, tripId, 2, "Icons of New York",
                        "Visit the landmarks that define New York City's skyline and spirit.",
                        "🗽 Statue of Liberty ferry tour\n🏛 Ellis Island Immigration Museum\n🌉 Walk the Brooklyn Bridge\n🍕 Authentic NYC pizza in Brooklyn");
                addItineraryDay(db, tripId, 3, "Culture & Parks",
                        "Explore the green heart of Manhattan and world-class art.",
                        "🌳 Central Park morning jog/walk\n🎨 Metropolitan Museum of Art\n🛶 Rowboat on Central Park Lake\n🍸 Rooftop bar in Manhattan");
                addItineraryDay(db, tripId, 4, "Neighborhoods & Food",
                        "Get off the tourist trail and explore the real New York.",
                        "🥯 Brunch in the West Village\n🧀 Chelsea Market food tour\n🎨 Street art in Bushwick, Brooklyn\n🌆 High Line park sunset walk");
                addItineraryDay(db, tripId, 5, "Shopping & Farewell",
                        "Last day to shop, explore and soak it all in.",
                        "🛍 Fifth Avenue shopping\n🏙 One World Observatory\n🌊 Hudson River waterfront walk\n✈ Departure from JFK");
                break;

            case "Wyoming, USA":
                addItineraryDay(db, tripId, 1, "Into the Wild",
                        "Arrive at Yellowstone and make your first wildlife sightings.",
                        "✈ Arrive at Jackson Hole Airport\n🚌 Transfer to Yellowstone lodge\n🦬 Lamar Valley bison herds\n🐺 Wolf watching at dusk");
                addItineraryDay(db, tripId, 2, "Geysers & Hot Springs",
                        "Witness Yellowstone's legendary geothermal wonders up close.",
                        "💦 Old Faithful geyser eruption\n🌈 Grand Prismatic Spring boardwalk\n♨ Norris Geyser Basin trail\n📸 Photography golden hour");
                addItineraryDay(db, tripId, 3, "Canyon Country",
                        "Explore the spectacular Grand Canyon of Yellowstone.",
                        "🏔 Grand Canyon of the Yellowstone\n🌊 Lower Falls viewpoint\n🎣 Fly fishing on the Yellowstone River\n🐻 Bear safety talk");
                addItineraryDay(db, tripId, 4, "Mammoth Hot Springs",
                        "Discover the terraced travertine formations of Mammoth.",
                        "🌡 Mammoth Hot Springs terraces\n🦌 Elk herd at Mammoth village\n🚴 Bike rental along the North Loop\n🌟 Astronomy night program");
                addItineraryDay(db, tripId, 5, "Grand Teton Day Trip",
                        "Venture out to the dramatic peaks of Grand Teton National Park.",
                        "🏔 Grand Teton panoramic drive\n🛶 Canoe on Jenny Lake\n🦅 Raptor spotting\n🍖 BBQ dinner back at lodge");
                addItineraryDay(db, tripId, 6, "Final Morning & Departure",
                        "One last sunrise in the wilderness before heading home.",
                        "🌅 Sunrise hike at Hayden Valley\n🛍 Souvenir shopping\n🚌 Transfer to Jackson Hole Airport\n✈ Departure");
                break;

            case "Florida, USA":
                addItineraryDay(db, tripId, 1, "Arrival in Paradise",
                        "Welcome to Miami — sun, sand, and Art Deco awaits.",
                        "✈ Arrive at Miami International Airport\n🏨 Check in at South Beach hotel\n🏖 Evening walk on Ocean Drive\n🍹 Welcome cocktails at a rooftop bar");
                addItineraryDay(db, tripId, 2, "South Beach Bliss",
                        "A full day on one of America's most famous beaches.",
                        "🏄 Morning surf lesson\n🏖 South Beach sunbathing\n🚴 Bike ride along the beachfront\n🦞 Fresh seafood dinner at Joe's Stone Crab");
                addItineraryDay(db, tripId, 3, "Art, Culture & Nightlife",
                        "Discover Miami's vibrant cultural scene.",
                        "🎨 Wynwood Walls street art district\n🏛 Pérez Art Museum Miami\n🛍 Design District shopping\n💃 Little Havana food & salsa tour");
                addItineraryDay(db, tripId, 4, "Everglades Adventure",
                        "A thrilling day trip into Florida's wild Everglades.",
                        "🐊 Everglades National Park airboat tour\n🐦 Bird watching at Anhinga Trail\n🌿 Guided mangrove kayaking\n🌅 Sunset at Flamingo Point");
                addItineraryDay(db, tripId, 5, "Island Day & Farewell",
                        "Explore Key Biscayne before your departure.",
                        "🏝 Key Biscayne island drive\n🤿 Snorkeling at Bill Baggs State Park\n🍦 Gelato on Lincoln Road\n✈ Departure from Miami");
                break;

            case "California, USA":
                addItineraryDay(db, tripId, 1, "Golden Gate Welcome",
                        "Arrive in San Francisco and fall in love with the city by the bay.",
                        "✈ Arrive at SFO Airport\n🚌 Transfer to Union Square hotel\n🌁 Golden Gate Bridge evening walk\n🦀 Dungeness crab at Fisherman's Wharf");
                addItineraryDay(db, tripId, 2, "Classic San Francisco",
                        "Ride the iconic cable cars and explore the historic waterfront.",
                        "🚃 Cable car ride on Powell-Hyde line\n⛵ Alcatraz Island tour\n🐟 Fisherman's Wharf seafood lunch\n🛍 Pier 39 & sea lions");
                addItineraryDay(db, tripId, 3, "Neighborhoods & Hills",
                        "Explore the city's eclectic neighborhoods on foot.",
                        "🌸 Lombard Street (crookedest street)\n🏘 Painted Ladies & Alamo Square\n🌉 Twin Peaks panoramic view\n🥢 Dim sum in Chinatown");
                addItineraryDay(db, tripId, 4, "Wine Country Day Trip",
                        "Escape to Napa Valley for world-class wine and scenery.",
                        "🍷 Napa Valley wine tasting tour\n🧀 Artisanal cheese & charcuterie\n🚴 Cycling through the vineyards\n🌄 Sunset over the valley");
                break;

            case "Hawaii, USA":
                addItineraryDay(db, tripId, 1, "Aloha — Oahu Arrival",
                        "Land in paradise and feel the Hawaiian spirit immediately.",
                        "✈ Arrive at Honolulu Airport\n🌺 Traditional lei greeting\n🏖 Waikiki Beach evening swim\n🍹 Sunset luau dinner");
                addItineraryDay(db, tripId, 2, "Oahu Highlights",
                        "Explore the history and natural beauty of Oahu.",
                        "🕊 Pearl Harbor & USS Arizona Memorial\n🏄 Surfing lesson at Waikiki\n🦈 Hanauma Bay snorkeling\n🌅 Diamond Head crater hike at sunset");
                addItineraryDay(db, tripId, 3, "Maui — Valley Isle",
                        "Fly to Maui and explore its stunning Road to Hana.",
                        "✈ Short flight to Maui\n🌿 Road to Hana waterfall hike\n🐢 Snorkeling with sea turtles at Makena\n🌺 Old Lahaina Town dinner");
                addItineraryDay(db, tripId, 4, "Maui Adventure",
                        "An action-packed day on the Valley Isle.",
                        "🌋 Haleakalā volcano sunrise\n🚴 Downhill bike ride from the summit\n🐋 Whale watching boat tour (seasonal)\n🌊 Big Beach sunset");
                addItineraryDay(db, tripId, 5, "Big Island — Volcanoes",
                        "Witness the raw power of Hawaii Volcanoes National Park.",
                        "✈ Fly to Big Island (Kona)\n🌋 Hawaii Volcanoes National Park\n🔥 Lava flow viewing at dusk\n⭐ Mauna Kea stargazing");
                addItineraryDay(db, tripId, 6, "Kauai — Garden Isle",
                        "Explore the most lush and dramatic of all the Hawaiian islands.",
                        "✈ Fly to Kauai\n🏔 Na Pali Coast helicopter tour\n🌊 Tunnels Beach snorkeling\n🌺 Farewell dinner at Hanalei Bay");
                addItineraryDay(db, tripId, 7, "Final Aloha",
                        "Soak in your last Hawaiian sunrise before heading home.",
                        "🌅 Sunrise yoga on the beach\n🛍 Local market shopping\n🌸 Waimea Canyon last look\n✈ Departure — Mahalo!");
                break;
        }
        db.close();
    }

    public synchronized List<ItineraryDay> getItineraryForTrip(String tripId) {
        List<ItineraryDay> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITINERARY, null,
                COLUMN_ITIN_TRIP_ID + "=?", new String[]{tripId},
                null, null, COLUMN_ITIN_DAY + " ASC");
        if (cursor.moveToFirst()) {
            do {
                list.add(new ItineraryDay(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITIN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITIN_TRIP_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITIN_DAY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITIN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITIN_DESC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITIN_ACTIVITIES))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Returns distinct user emails who booked a given trip (for Travel Buddies)
    public synchronized List<String> getUserEmailsByTripId(String tripId) {
        List<String> emails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_BOOKINGS,
                new String[]{COLUMN_BOOKING_USER_ID},
                COLUMN_BOOKING_TRIP_ID + " = ?",
                new String[]{tripId},
                COLUMN_BOOKING_USER_ID, null, null);
        if (cursor.moveToFirst()) {
            do {
                emails.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return emails;
    }
}