package com.example.travelbuddies.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.example.travelbuddies.database.DBHelper;
import com.example.travelbuddies.databinding.ActivityBookingBinding;
import com.example.travelbuddies.models.Booking;
import com.example.travelbuddies.models.Trip;
import com.example.travelbuddies.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class BookingActivity extends AppCompatActivity {

    private ActivityBookingBinding binding;
    private Trip trip;
    private int numberOfPeople = 1;
    private String selectedDate = null;
    private DBHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

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

        binding.btnBack.setOnClickListener(v -> onBackPressed());

        trip = (Trip) getIntent().getSerializableExtra("trip");
        if (trip == null) {
            finish();
            return;
        }

        populateTripInfo();

        binding.llDatePicker.setOnClickListener(v -> showDatePicker());

        binding.btnIncreasePeople.setOnClickListener(v -> {
            numberOfPeople++;
            updatePeopleAndTotal();
        });

        binding.btnDecreasePeople.setOnClickListener(v -> {
            if (numberOfPeople > 1) {
                numberOfPeople--;
                updatePeopleAndTotal();
            }
        });

        binding.btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private void populateTripInfo() {
        binding.tvBookingTripName.setText(trip.getTitle());
        binding.tvBookingTripLocation.setText(trip.getLocation());
        binding.tvBookingTripDuration.setText(trip.getDuration());
        String priceText = String.format(Locale.US, "$%,.0f", trip.getPrice());
        binding.tvBookingTripPrice.setText(priceText);
        binding.tvTotalPrice.setText(priceText);

        Glide.with(this)
                .load(trip.getImageUrl())
                .centerCrop()
                .into(binding.ivBookingTripImage);
    }

    private void updatePeopleAndTotal() {
        binding.tvPeopleCount.setText(String.valueOf(numberOfPeople));
        double total = trip.getPrice() * numberOfPeople;
        binding.tvTotalPrice.setText(String.format(Locale.US, "$%,.0f", total));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        // Minimum date is today
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                    selectedDate = sdf.format(selected.getTime());
                    binding.tvSelectedDate.setText(selectedDate);
                    binding.tvSelectedDate.setTextColor(getColor(com.example.travelbuddies.R.color.text_primary));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void confirmBooking() {
        if (selectedDate == null) {
            Toast.makeText(this, "Please select a travel date", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "Please log in to book", Toast.LENGTH_SHORT).show();
            return;
        }
        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking(bookingId, userId, trip.getTripId(), selectedDate, numberOfPeople, "Confirmed");
        boolean saved = dbHelper.addBooking(booking);
        if (saved) {
            // Navigate to transport upsell screen
            Intent intent = new Intent(this, TransportActivity.class);
            intent.putExtra("bookingId", bookingId);
            intent.putExtra("trip", trip);
            intent.putExtra("travelDate", selectedDate);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to save booking. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
