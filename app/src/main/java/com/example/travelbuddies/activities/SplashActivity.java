package com.example.travelbuddies.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.example.travelbuddies.R;
import com.example.travelbuddies.database.DBHelper;
import com.example.travelbuddies.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 2000;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);
        dbHelper = new DBHelper(this);

        // Run all heavy operations in a background thread
        new Thread(() -> {
            // This flag ensures sample data is only added once
            if (sessionManager.isFirstLaunch()) {
                dbHelper.addSampleTrips();
                sessionManager.setFirstLaunch(false);
            }

            // Post a runnable to the main thread to handle UI and navigation
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent;
                if (sessionManager.isLoggedIn()) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }, SPLASH_TIME_OUT);
        }).start();
    }
}