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

public class ViewGuestsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    ViewGuestAdapter adapter;
    ArrayList<Map<String, Object>> guestList;
    HashMap<String, String> roomMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_guests);

        recyclerView = findViewById(R.id.rvGuestList);
        database = FirebaseDatabase.getInstance("https://bitnest-auth-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        guestList = new ArrayList<>();
        roomMap = new HashMap<>();

        adapter = new ViewGuestAdapter(this, guestList, roomMap);
        recyclerView.setAdapter(adapter);

        loadRoomsAndGuests();
    }

    private void loadRoomsAndGuests() {
        database.child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
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
                loadGuests();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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
                Toast.makeText(ViewGuestsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}