package com.example.bitnest_android_2207086;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

public class UserDashboard extends AppCompatActivity {

    private Button btnBookRoom, btnMyBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userdash);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {

        btnBookRoom = findViewById(R.id.btnBookRoom);
    }

    private void setupListeners() {


        btnBookRoom.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboard.this, AvailableRoomsActivity.class);
            startActivity(intent);
        });
    }
}