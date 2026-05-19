package com.example.travelbuddies.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.travelbuddies.R;
import com.example.travelbuddies.activities.TripDetailActivity;
import com.example.travelbuddies.adapters.TripMiniAdapter;
import com.example.travelbuddies.database.DBHelper;
import com.example.travelbuddies.databinding.FragmentHomeBinding;
import com.example.travelbuddies.models.Trip;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DBHelper dbHelper;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        dbHelper = new DBHelper(getContext());

        ViewCompat.setOnApplyWindowInsetsListener(binding.headerLayout, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(v.getPaddingLeft(), statusBarHeight + 40, v.getPaddingRight(), v.getPaddingBottom());
            ViewCompat.setOnApplyWindowInsetsListener(v, null);
            return insets;
        });

        setupFeaturedTrip();
        setupPopularTrips();
        setupQuickActions();

        binding.cardSearch.setOnClickListener(v -> switchTab(R.id.navigation_trips));

        return binding.getRoot();
    }

    private void switchTab(int tabId) {
        BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(tabId);
    }

    private void setupFeaturedTrip() {
        List<Trip> trips = dbHelper.getAllTrips();
        if (trips != null && !trips.isEmpty()) {
            Trip featured = trips.get(0);
            binding.tvFeaturedTitle.setText(featured.getTitle());
            binding.tvFeaturedLocation.setText(featured.getLocation());
            binding.tvFeaturedPrice.setText(String.format(Locale.US, "$%,.0f", featured.getPrice()));
            Glide.with(this)
                    .load(featured.getImageUrl())
                    .placeholder(R.drawable.featured_destination_image)
                    .centerCrop()
                    .into(binding.ivFeaturedImage);
            binding.cardFeatured.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), TripDetailActivity.class);
                intent.putExtra("trip", featured);
                startActivity(intent);
            });
        }
    }

    private void setupPopularTrips() {
        List<Trip> trips = dbHelper.getAllTrips();
        if (trips != null && !trips.isEmpty()) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    getContext(), LinearLayoutManager.HORIZONTAL, false);
            binding.rvPopularTrips.setLayoutManager(layoutManager);
            TripMiniAdapter adapter = new TripMiniAdapter(getContext(), trips, trip -> {
                Intent intent = new Intent(getActivity(), TripDetailActivity.class);
                intent.putExtra("trip", trip);
                startActivity(intent);
            });
            binding.rvPopularTrips.setAdapter(adapter);
        }

        binding.tvSeeAll.setOnClickListener(v -> switchTab(R.id.navigation_trips));
    }

    private void setupQuickActions() {
        binding.actionExplore.ivActionIcon.setImageResource(R.drawable.ic_explore);
        binding.actionExplore.tvActionTitle.setText(R.string.explore_trips);
        binding.actionExplore.getRoot().setOnClickListener(v -> switchTab(R.id.navigation_trips));

        binding.actionBookings.ivActionIcon.setImageResource(R.drawable.ic_bookings);
        binding.actionBookings.tvActionTitle.setText(R.string.my_bookings);
        binding.actionBookings.getRoot().setOnClickListener(v -> switchTab(R.id.navigation_dashboard));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
