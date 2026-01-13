package com.example.bitnest_android_2207086;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyBookingActivity extends AppCompatActivity {

    RecyclerView recyclerViewBookings;
    TextView tvNoBooking;
    DatabaseReference database;
    String currentUsername;
    ArrayList<Booking> list;
    BookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);

        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        tvNoBooking = findViewById(R.id.tvNoBooking);

        database = FirebaseDatabase.getInstance("https://bitnest-auth-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        recyclerViewBookings.setHasFixedSize(true);
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new BookingAdapter(list);
        recyclerViewBookings.setAdapter(adapter);

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
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                // Extract data safely
                                String name = ds.child("name").getValue(String.class);
                                String phone = ds.child("phone").getValue(String.class);
                                String checkIn = ds.child("checkIn").getValue(String.class);
                                String checkOut = ds.child("checkOut").getValue(String.class);
                                String guestId = ds.getKey();

                                ArrayList<String> roomIds = new ArrayList<>();
                                if (ds.hasChild("bookedRooms")) {
                                    for (DataSnapshot roomSnap : ds.child("bookedRooms").getChildren()) {
                                        roomIds.add(roomSnap.getValue(String.class));
                                    }
                                }

                                Booking booking = new Booking(guestId, name, phone, checkIn, checkOut, roomIds);
                                list.add(booking);
                            }
                            adapter.notifyDataSetChanged();

                            if (list.isEmpty()) {
                                tvNoBooking.setVisibility(View.VISIBLE);
                            } else {
                                tvNoBooking.setVisibility(View.GONE);
                            }
                        } else {
                            tvNoBooking.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MyBookingActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}