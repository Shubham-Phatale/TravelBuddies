package com.example.travelbuddies.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.travelbuddies.R;
import com.example.travelbuddies.activities.TripDetailActivity;
import com.example.travelbuddies.adapters.TripAdapter;
import com.example.travelbuddies.database.DBHelper;
import com.example.travelbuddies.databinding.FragmentTripsBinding;
import com.example.travelbuddies.models.Trip;
import java.util.ArrayList;
import java.util.List;

public class TripFragment extends Fragment implements TripAdapter.OnTripClickListener {

    private FragmentTripsBinding binding;
    private DBHelper dbHelper;
    private List<Trip> allTrips = new ArrayList<>();
    private TripAdapter tripAdapter;
    private int activeFilter = 0;

    private TextView[] filterPills;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTripsBinding.inflate(inflater, container, false);
        dbHelper = new DBHelper(getContext());

        ViewCompat.setOnApplyWindowInsetsListener(binding.headerLayout, (v, insets) -> {
            int statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(v.getPaddingLeft(), statusBar + 16, v.getPaddingRight(), v.getPaddingBottom());
            ViewCompat.setOnApplyWindowInsetsListener(v, null);
            return insets;
        });

        binding.etSearch.setHintTextColor(0x99ffffff);

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        binding.swipeRefreshLayout.setOnRefreshListener(this::reloadAll);

        setupRecyclerView();
        setupSearch();
        setupFilterPills();
        loadTrips();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        tripAdapter = new TripAdapter(getContext(), new ArrayList<>(), this);
        binding.tripsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.tripsRecyclerView.setAdapter(tripAdapter);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.ivClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                applyFilter();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        binding.ivClearSearch.setOnClickListener(v -> {
            binding.etSearch.setText("");
            binding.ivClearSearch.setVisibility(View.GONE);
        });
    }

    private void setupFilterPills() {
        filterPills = new TextView[]{
            binding.filterAll,
            binding.filterBudget,
            binding.filterPremium,
            binding.filterShort,
            binding.filterLong
        };
        for (int i = 0; i < filterPills.length; i++) {
            final int index = i;
            filterPills[i].setOnClickListener(v -> selectFilter(index));
        }
        selectFilter(0);
    }

    private void selectFilter(int index) {
        activeFilter = index;
        for (int i = 0; i < filterPills.length; i++) {
            if (i == index) {
                filterPills[i].setBackgroundResource(R.drawable.bg_chip_selected);
                filterPills[i].setTextColor(requireContext().getColor(R.color.primary));
                filterPills[i].setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                filterPills[i].setBackgroundResource(R.drawable.bg_chip_unselected);
                filterPills[i].setTextColor(0xffffffff);
                filterPills[i].setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
        applyFilter();
    }

    private void loadTrips() {
        allTrips = dbHelper.getAllTrips();
        binding.progressBar.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
        applyFilter();
    }

    private void reloadAll() {
        binding.etSearch.setText("");
        selectFilter(0);
        loadTrips();
    }

    private void applyFilter() {
        String query = binding.etSearch.getText().toString().trim().toLowerCase();
        List<Trip> result = new ArrayList<>();
        for (Trip t : allTrips) {
            if (!matchesFilter(t)) continue;
            if (!query.isEmpty()) {
                if (!t.getTitle().toLowerCase().contains(query)
                        && !t.getLocation().toLowerCase().contains(query)) continue;
            }
            result.add(t);
        }
        showResults(result);
    }

    private boolean matchesFilter(Trip t) {
        switch (activeFilter) {
            case 1: return t.getPrice() < 1500;
            case 2: return t.getPrice() > 2000;
            case 3: return parseDays(t.getDuration()) <= 4;
            case 4: return parseDays(t.getDuration()) >= 6;
            default: return true;
        }
    }

    private int parseDays(String duration) {
        try { return Integer.parseInt(duration.replaceAll("[^0-9]", "")); }
        catch (NumberFormatException e) { return 0; }
    }

    private void showResults(List<Trip> trips) {
        if (trips.isEmpty()) {
            binding.tripsRecyclerView.setVisibility(View.GONE);
            binding.layoutNoTrips.setVisibility(View.VISIBLE);
            binding.tvNoTripsMessage.setText(
                    allTrips.isEmpty() ? "No trips available" : "No trips match your search");
        } else {
            binding.layoutNoTrips.setVisibility(View.GONE);
            binding.tripsRecyclerView.setVisibility(View.VISIBLE);
            tripAdapter.updateList(trips);
        }
    }

    @Override
    public void onTripClick(Trip trip) {
        Intent intent = new Intent(getActivity(), TripDetailActivity.class);
        intent.putExtra("trip", trip);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
