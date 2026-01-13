package com.example.bitnest_android_2207086;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboard extends AppCompatActivity {

    private Button btnSeeGuests, btnModify, btnCheckout, btnSearch, btnlogout, btnAddRoom;

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
        btnAddRoom = findViewById(R.id.btnAddRoom);
        btnlogout = findViewById(R.id.btnLogout);
    }

    private void setupListeners() {

        btnSeeGuests.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, ViewGuestsActivity.class);
            startActivity(intent);
        });

        btnModify.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, ModifyBookingActivity.class);
            startActivity(intent);
        });

        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, CheckoutActivity.class);
            startActivity(intent);
        });


        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, SearchGuestActivity.class);
            startActivity(intent);
        });

        btnAddRoom.setOnClickListener(v -> {
            handleAddRoom();
        });
        btnlogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void handleAddRoom() {
        Intent intent = new Intent(AdminDashboard.this, AddRoom.class);
        startActivity(intent);
    }
}