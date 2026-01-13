package com.example.bitnest_android_2207086;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyBookingActivity extends AppCompatActivity {

    TextView tvGuestName, tvGuestPhone, tvRoomNumber, tvDates, tvNoBooking;
    CardView cardBooking;
    DatabaseReference database;
    String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);

        tvGuestName = findViewById(R.id.tvGuestName);
        tvGuestPhone = findViewById(R.id.tvGuestPhone);
        tvRoomNumber = findViewById(R.id.tvRoomNumber);
        tvDates = findViewById(R.id.tvDates);
        tvNoBooking = findViewById(R.id.tvNoBooking);
        cardBooking = findViewById(R.id.cardBooking);

        database = FirebaseDatabase.getInstance("https://bitnest-auth-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUsername = prefs.getString("username", "");

        if (currentUsername.isEmpty()) {
            Toast.makeText(this, "User not identified. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        findBooking();
    }

    private void findBooking() {
        database.child("guests").orderByChild("username").equalTo(currentUsername)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String name = ds.child("name").getValue(String.class);
                                String phone = ds.child("phone").getValue(String.class);
                                String checkIn = ds.child("checkIn").getValue(String.class);
                                String checkOut = ds.child("checkOut").getValue(String.class);

                                ArrayList<String> roomIds = (ArrayList<String>) ds.child("bookedRooms").getValue();

                                tvGuestName.setText("Name: " + name);
                                tvGuestPhone.setText("Phone: " + phone);
                                tvDates.setText("In: " + checkIn + "  /  Out: " + checkOut);

                                if (roomIds != null && !roomIds.isEmpty()) {
                                    fetchRoomNumbers(roomIds);
                                } else {
                                    tvRoomNumber.setText("No Room Assigned");
                                    cardBooking.setVisibility(View.VISIBLE);
                                }
                                return;
                            }
                        } else {
                            cardBooking.setVisibility(View.GONE);
                            tvNoBooking.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MyBookingActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchRoomNumbers(ArrayList<String> roomIds) {
        database.child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder roomsStr = new StringBuilder();
                for (String id : roomIds) {
                    if (snapshot.hasChild(id)) {
                        String num = snapshot.child(id).child("roomNumber").getValue(String.class);
                        if (roomsStr.length() > 0) roomsStr.append(", ");
                        roomsStr.append(num);
                    }
                }
                tvRoomNumber.setText("Room: " + roomsStr.toString());
                cardBooking.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}