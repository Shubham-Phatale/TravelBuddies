package com.example.travelbuddies.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.travelbuddies.R;
import com.example.travelbuddies.adapters.BuddyAdapter;
import com.example.travelbuddies.adapters.ItineraryAdapter;
import com.example.travelbuddies.models.ItineraryDay;
import com.example.travelbuddies.database.DBHelper;
import com.example.travelbuddies.databinding.ActivityTripDetailBinding;
import com.example.travelbuddies.models.Trip;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TripDetailActivity extends AppCompatActivity {

    // Mock travel buddies with real avatar photos
    private static final BuddyAdapter.Buddy[] MOCK_BUDDIES = {
        new BuddyAdapter.Buddy("Alex R.",   "Chicago",    "https://picsum.photos/seed/alex/200/200"),
        new BuddyAdapter.Buddy("Maria S.",  "Austin",     "https://picsum.photos/seed/maria/200/200"),
        new BuddyAdapter.Buddy("James T.",  "Seattle",    "https://picsum.photos/seed/james/200/200"),
        new BuddyAdapter.Buddy("Priya K.",  "Boston",     "https://picsum.photos/seed/priya/200/200"),
        new BuddyAdapter.Buddy("Liam W.",   "Denver",     "https://picsum.photos/seed/liam/200/200"),
    };

    private ActivityTripDetailBinding binding;
    private DBHelper dbHelper;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Fix bottom bar nav inset
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomBar, (v, insets) -> {
            int navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), navBar);
            ViewCompat.setOnApplyWindowInsetsListener(v, null);
            return insets;
        });

        trip = (Trip) getIntent().getSerializableExtra("trip");
        if (trip != null) {
            populateUi(trip);
            setupItinerary(trip);
            setupBuddies(trip.getTripId());
        } else {
            Toast.makeText(this, "Error: Trip data not found.", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.btnBookNow.setOnClickListener(v -> {
            if (trip != null) {
                Intent intent = new Intent(TripDetailActivity.this, BookingActivity.class);
                intent.putExtra("trip", trip);
                startActivity(intent);
            }
        });

        binding.btnViewMap.setOnClickListener(v -> {
            if (trip != null) openMap(trip.getLocation());
        });
    }

    private void populateUi(Trip trip) {
        binding.tvTripDetailTitle.setText(trip.getTitle());
        binding.tvTripDetailLocation.setText(trip.getLocation());
        binding.tvTripDetailDescription.setText(trip.getDescription());
        binding.tvTripDetailDuration.setText(trip.getDuration());
        binding.tvTripDetailRating.setText(String.valueOf(trip.getRating()));
        String priceText = String.format(Locale.US, "$%,.0f", trip.getPrice());
        binding.tvTripDetailPrice.setText(priceText);
        binding.tvBottomPrice.setText(priceText);
        binding.tvMapLocation.setText(trip.getLocation());

        Glide.with(this)
                .load(trip.getImageUrl())
                .centerCrop()
                .into(binding.ivTripDetailImage);
    }

    private void setupItinerary(Trip trip) {
        dbHelper.addSampleItinerary(trip.getTripId(), trip.getLocation());
        List<ItineraryDay> days = dbHelper.getItineraryForTrip(trip.getTripId());
        ItineraryAdapter adapter = new ItineraryAdapter(days);
        binding.rvItinerary.setLayoutManager(new LinearLayoutManager(this));
        binding.rvItinerary.setNestedScrollingEnabled(false);
        binding.rvItinerary.setAdapter(adapter);
    }

    private void setupBuddies(String tripId) {
        // Combine real bookers from DB with mock buddies for a social feel
        List<BuddyAdapter.Buddy> buddies = new ArrayList<>();
        List<String> bookerEmails = dbHelper.getUserEmailsByTripId(tripId);
        for (String email : bookerEmails) {
            String name = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
            String seed = name.replaceAll("[^a-zA-Z0-9]", "");
            buddies.add(new BuddyAdapter.Buddy(name, "Traveler", "https://picsum.photos/seed/" + seed + "/200/200"));
        }
        // Fill up with mock buddies so there's always someone to show
        for (BuddyAdapter.Buddy mock : MOCK_BUDDIES) {
            if (buddies.size() >= 8) break;
            buddies.add(mock);
        }

        binding.tvBuddiesCount.setText(buddies.size() + " going");
        binding.tvBuddiesCount.setVisibility(View.VISIBLE);

        BuddyAdapter adapter = new BuddyAdapter(this, buddies);
        binding.rvBuddies.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvBuddies.setAdapter(adapter);
    }

    private void openMap(String location) {
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri browserUri = Uri.parse("https://maps.google.com/?q=" + Uri.encode(location));
            startActivity(new Intent(Intent.ACTION_VIEW, browserUri));
        }
    }
}
