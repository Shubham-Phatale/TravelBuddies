package com.example.travelbuddies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.travelbuddies.databinding.ActivitySignupBinding; // Import the binding class

public class SignupActivity extends AppCompatActivity {

    // Declare the binding variable
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using ViewBinding
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            int statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, statusBar, 0, 0);
            ViewCompat.setOnApplyWindowInsetsListener(v, null);
            return insets;
        });

        // Set the listener for the "Sign Up" button
        binding.btnSignup.setOnClickListener(v -> {
            // Your signup logic here
            String name = binding.etSignupName.getText().toString().trim();
            String email = binding.etSignupEmail.getText().toString().trim();
            String password = binding.etSignupPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!binding.cbTerms.isChecked()) {
                Toast.makeText(this, "Please accept the Terms of Service", Toast.LENGTH_SHORT).show();
            } else {
                // Perform signup
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                // Navigate to the main activity or login screen
            }
        });

        // Set the listener for the "Login" redirect text
        binding.tvLoginRedirect.setOnClickListener(v -> {
            // Navigate to the LoginActivity
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}