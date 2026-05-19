package com.example.travelbuddies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.travelbuddies.R;
import com.example.travelbuddies.database.DBHelper;
import com.example.travelbuddies.databinding.ActivityTransportBinding;
import com.example.travelbuddies.databinding.ItemTransportOptionBinding;
import com.example.travelbuddies.models.Trip;
import java.util.Locale;

public class TransportActivity extends AppCompatActivity {

    private ActivityTransportBinding binding;
    private DBHelper dbHelper;
    private String bookingId;
    private Trip trip;
    private int selectedIndex = -1;
    private ItemTransportOptionBinding[] optionBindings;

    // name, description, icon res, discount %, price multiplier
    private static final Object[][] OPTIONS = {
        { "Flight",      "Fastest route • ~3 hrs",       R.drawable.ic_explore,  "40% OFF", 0.15 },
        { "Train",       "Scenic & comfortable • ~8 hrs", R.drawable.ic_bookings, "30% OFF", 0.08 },
        { "Bus",         "Most affordable • ~12 hrs",     R.drawable.ic_people,   "20% OFF", 0.04 },
        { "Car Rental",  "Travel at your own pace",       R.drawable.ic_person,   "35% OFF", 0.10 },
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);
        bookingId = getIntent().getStringExtra("bookingId");
        trip = (Trip) getIntent().getSerializableExtra("trip");
        String travelDate = getIntent().getStringExtra("travelDate");

        if (trip == null) { finish(); return; }

        ViewCompat.setOnApplyWindowInsetsListener(binding.headerLayout, (v, insets) -> {
            int statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(v.getPaddingLeft(), statusBar + 16, v.getPaddingRight(), v.getPaddingBottom());
            ViewCompat.setOnApplyWindowInsetsListener(v, null);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomBar, (v, insets) -> {
            int navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), 14 + navBar);
            ViewCompat.setOnApplyWindowInsetsListener(v, null);
            return insets;
        });

        binding.tvTransportDestination.setText(trip.getLocation());
        if (travelDate != null) binding.tvTransportTravelDate.setText(travelDate);

        // Check if updating an existing transport selection
        String existingMode = getIntent().getStringExtra("existingMode");

        buildOptions(existingMode);

        binding.btnBookTransport.setOnClickListener(v -> bookTransport());
        binding.btnSkipTransport.setOnClickListener(v -> goToBookings());
    }

    private void buildOptions(String existingMode) {
        // Update header text if editing
        if (existingMode != null) {
            binding.btnBookTransport.setText("Update Transport");
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        optionBindings = new ItemTransportOptionBinding[OPTIONS.length];

        for (int i = 0; i < OPTIONS.length; i++) {
            final int index = i;
            ItemTransportOptionBinding ob = ItemTransportOptionBinding.inflate(inflater, binding.llTransportOptions, false);
            optionBindings[i] = ob;

            ob.tvTransportName.setText((String) OPTIONS[i][0]);
            ob.tvTransportDesc.setText((String) OPTIONS[i][1]);
            ob.ivTransportIcon.setImageResource((int) OPTIONS[i][2]);
            ob.tvDiscountBadge.setText((String) OPTIONS[i][3]);

            double price = trip.getPrice() * (double) OPTIONS[i][4];
            ob.tvTransportPrice.setText(String.format(Locale.US, "$%,.0f", price));

            ob.getRoot().setOnClickListener(v -> selectOption(index));
            binding.llTransportOptions.addView(ob.getRoot());

            // Pre-select existing mode
            if (existingMode != null && existingMode.equals(OPTIONS[i][0])) {
                // Post so the view is laid out first
                ob.getRoot().post(() -> selectOption(index));
            }
        }
    }

    private void selectOption(int index) {
        selectedIndex = index;
        for (int i = 0; i < optionBindings.length; i++) {
            ItemTransportOptionBinding ob = optionBindings[i];
            if (i == index) {
                ob.getRoot().setStrokeWidth(3);
                ob.ivSelected.setVisibility(View.VISIBLE);
                ob.iconBg.setBackgroundResource(R.drawable.bg_primary_light_circle);
            } else {
                ob.getRoot().setStrokeWidth(0);
                ob.ivSelected.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void bookTransport() {
        if (selectedIndex < 0) {
            Toast.makeText(this, "Please select a transport option", Toast.LENGTH_SHORT).show();
            return;
        }
        String mode = (String) OPTIONS[selectedIndex][0];
        double price = trip.getPrice() * (double) OPTIONS[selectedIndex][4];
        String existingMode = getIntent().getStringExtra("existingMode");
        if (existingMode != null) {
            dbHelper.updateTransportBooking(bookingId, mode, price);
            Toast.makeText(this, "Transport updated to " + mode + " ✓", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.addTransportBooking(bookingId, mode, price);
            Toast.makeText(this, mode + " booked! Enjoy your trip 🎉", Toast.LENGTH_LONG).show();
        }
        goToBookings();
    }

    private void goToBookings() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("openTab", R.id.navigation_dashboard);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
