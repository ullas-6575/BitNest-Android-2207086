package com.example.bitnest_android_2207086;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboard extends AppCompatActivity {

    private Button btnSeeGuests, btnModify, btnCheckout, btnSearch, btnBack, btnAddRoom;
    private EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admindashboard);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        btnSeeGuests = findViewById(R.id.btnSeeGuests);
        btnModify = findViewById(R.id.btnModify);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        btnAddRoom = findViewById(R.id.btnAddRoom);
        searchInput = findViewById(R.id.etSearchInput);
    }

    private void setupListeners() {

        btnSeeGuests.setOnClickListener(v -> {
            Toast.makeText(this, "Loading Guest List...", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(AdminDashboard.this, GuestListActivity.class);
            // startActivity(intent);
        });

        btnModify.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Modify Screen...", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(AdminDashboard.this, ModifyActivity.class);
            // startActivity(intent);
        });

        btnCheckout.setOnClickListener(v -> {
            Toast.makeText(this, "Proceeding to Checkout...", Toast.LENGTH_SHORT).show();
             Intent intent = new Intent(AdminDashboard.this, CheckoutActivity.class);
             startActivity(intent);
        });
        btnSearch.setOnClickListener(v -> {
            String input = searchInput.getText().toString();
            if (input.isEmpty()) {
                input = "Room 101";
            }
            Log.d("AdminDashboard", "Searching for: " + input);
            Toast.makeText(this, "Searching for: " + input, Toast.LENGTH_SHORT).show();
        });


        btnAddRoom.setOnClickListener(v -> {
            handleAddRoom();
        });

        btnBack.setOnClickListener(v -> {

            Intent intent = new Intent(AdminDashboard.this, AdminLogin.class); // Assuming your login class name
            startActivity(intent);
            finish();
        });
    }

    private void handleAddRoom() {
        Intent intent = new Intent(AdminDashboard.this, AddRoom.class);
        startActivity(intent);
    }
}