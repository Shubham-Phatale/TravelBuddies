package com.example.travelbuddies.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.travelbuddies.R;
import com.example.travelbuddies.databinding.ActivityMainBinding;
import com.example.travelbuddies.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        // This check is still correct and important
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // --- Modern Navigation Setup ---

        // 1. Find the NavHostFragment from your layout
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        // 2. Get the NavController from the NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // 3. This one line replaces your entire OnNavigationItemSelectedListener!
            // It automatically handles clicks and fragment switching.
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }

        int openTab = getIntent().getIntExtra("openTab", -1);
        if (openTab != -1) {
            binding.bottomNavigation.setSelectedItemId(openTab);
        }
    }
}