package com.example.travelbuddies.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.travelbuddies.R;
import com.example.travelbuddies.adapters.BookingAdapter;
import com.example.travelbuddies.database.DBHelper;
import com.example.travelbuddies.databinding.FragmentDashboardBinding;
import com.example.travelbuddies.models.Booking;
import com.example.travelbuddies.utils.SessionManager;
import java.util.Collections;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private SessionManager sessionManager;
    private DBHelper dbHelper;
    private BookingAdapter bookingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        sessionManager = new SessionManager(getContext());
        dbHelper = new DBHelper(getContext());

        ViewCompat.setOnApplyWindowInsetsListener(binding.headerLayout, (v, insets) -> {
            int statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(v.getPaddingLeft(), statusBar + 16, v.getPaddingRight(), v.getPaddingBottom());
            ViewCompat.setOnApplyWindowInsetsListener(v, null);
            return insets;
        });

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        binding.swipeRefreshLayout.setOnRefreshListener(this::loadBookings);

        setupRecyclerView();
        setupClickListeners();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookings();
    }

    private void setupRecyclerView() {
        binding.bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookingAdapter = new BookingAdapter(getContext(), Collections.emptyList(), dbHelper, this::loadBookings);
        binding.bookingsRecyclerView.setAdapter(bookingAdapter);
    }

    private void setupClickListeners() {
        binding.btnExploreTrips.setOnClickListener(v -> {
            BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_navigation);
            nav.setSelectedItemId(R.id.navigation_trips);
        });
    }

    private void loadBookings() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.bookingsRecyclerView.setVisibility(View.GONE);
        binding.layoutNoBookings.setVisibility(View.GONE);

        String userId = sessionManager.getUserId();
        binding.swipeRefreshLayout.setRefreshing(false);

        if (userId != null) {
            List<Booking> bookingList = dbHelper.getBookingsByUserId(userId);
            binding.progressBar.setVisibility(View.GONE);
            if (bookingList.isEmpty()) {
                binding.layoutNoBookings.setVisibility(View.VISIBLE);
            } else {
                binding.bookingsRecyclerView.setVisibility(View.VISIBLE);
                bookingAdapter = new BookingAdapter(getContext(), bookingList, dbHelper, this::loadBookings);
                binding.bookingsRecyclerView.setAdapter(bookingAdapter);
            }
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.layoutNoBookings.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
