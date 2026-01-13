package com.example.bitnest_android_2207086;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class UserDashboard extends AppCompatActivity {

    Button btnBookRoom, btnMyBooking, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userdash);


        btnBookRoom = findViewById(R.id.btnBookRoom);
        btnMyBooking = findViewById(R.id.btnMyBooking);
        btnLogout = findViewById(R.id.btnLogout);

        btnBookRoom.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboard.this, AvailableRoomsActivity.class);
            startActivity(intent);
        });

        btnMyBooking.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboard.this, MyBookingActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboard.this, MainActivity.class); // Or LoginActivity
            startActivity(intent);
            finish();
        });
    }
}