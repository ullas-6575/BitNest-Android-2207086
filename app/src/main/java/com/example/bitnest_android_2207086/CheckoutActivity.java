package com.example.bitnest_android_2207086;

import android.os.Bundle;
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
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity implements GuestAdapter.OnCheckoutListener {

    RecyclerView recyclerView;
    DatabaseReference database;
    GuestAdapter adapter;
    ArrayList<Map<String, Object>> guestList;
    HashMap<String, String> roomMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        recyclerView = findViewById(R.id.rvCheckoutList);
        database = FirebaseDatabase.getInstance("https://bitnest-auth-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        guestList = new ArrayList<>();
        roomMap = new HashMap<>();

        adapter = new GuestAdapter(this, guestList, roomMap, this);
        recyclerView.setAdapter(adapter);

        loadRooms();
        loadGuests();
    }

    private void loadRooms() {
        database.child("rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomMap.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String roomId = dataSnapshot.getKey();
                    String roomNum = dataSnapshot.child("roomNumber").getValue(String.class);
                    if (roomId != null && roomNum != null) {
                        roomMap.put(roomId, roomNum);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadGuests() {
        database.child("guests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                guestList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String, Object> guest = (Map<String, Object>) dataSnapshot.getValue();
                    if (guest != null) {
                        guestList.add(guest);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckoutActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCheckoutClick(Map<String, Object> guestData) {
        String guestId = (String) guestData.get("guestId");
        ArrayList<String> rooms = (ArrayList<String>) guestData.get("bookedRooms");

        Map<String, Object> updates = new HashMap<>();

        updates.put("guests/" + guestId, null);

        if (rooms != null) {
            for (String roomId : rooms) {
                updates.put("rooms/" + roomId + "/isAvailable", true);
            }
        }

        database.updateChildren(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Guest Checkout Complete", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}