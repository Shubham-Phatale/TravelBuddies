package com.example.travelbuddies.fragments;

import android.content.Intent;
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
import com.example.travelbuddies.R;
import com.example.travelbuddies.activities.LoginActivity;
import com.example.travelbuddies.database.DBHelper;
import com.example.travelbuddies.models.User;
import com.example.travelbuddies.databinding.FragmentProfileBinding;
import com.example.travelbuddies.models.User;
import com.example.travelbuddies.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        sessionManager = new SessionManager(getContext());
        dbHelper = new DBHelper(getContext());

        ViewCompat.setOnApplyWindowInsetsListener(binding.headerLayout, (v, insets) -> {
            int statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(v.getPaddingLeft(), statusBar + 16, v.getPaddingRight(), v.getPaddingBottom());
            ViewCompat.setOnApplyWindowInsetsListener(v, null);
            return insets;
        });

        loadUserProfile();

        binding.tvMyBookings.setOnClickListener(v -> {
            BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_navigation);
            nav.setSelectedItemId(R.id.navigation_dashboard);
        });

        binding.btnShareProfile.setOnClickListener(v -> shareProfile());

        binding.btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return binding.getRoot();
    }

    private void shareProfile() {
        String email = sessionManager.getUserId();
        if (email == null) return;
        User user = dbHelper.getUserByEmail(email);
        String name = user != null ? user.getName() : email;
        String shareText = "Hey! I'm " + name + " and I'm using TravelBuddies to plan my next adventure. Join me! 🌍✈️";
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
        startActivity(android.content.Intent.createChooser(intent, "Share with Buddies"));
    }

    private void loadUserProfile() {
        String email = sessionManager.getUserId(); // session stores email
        if (email != null) {
            User user = dbHelper.getUserByEmail(email);
            if (user != null) {
                binding.tvProfileName.setText(user.getName());
                binding.tvProfileEmail.setText(user.getEmail());
            } else {
                binding.tvProfileName.setText(email);
                binding.tvProfileEmail.setText(email);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
