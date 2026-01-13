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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ViewGuestsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    ViewGuestAdapter adapter;
    ArrayList<Map<String, String>> flattenedList;
    HashMap<String, String> roomMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_guests);

        recyclerView = findViewById(R.id.rvGuestList);
        database = FirebaseDatabase.getInstance("https://bitnest-auth-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        flattenedList = new ArrayList<>();
        roomMap = new HashMap<>();

        adapter = new ViewGuestAdapter(this, flattenedList);
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
                flattenedList.clear();

                for (DataSnapshot guestSnap : snapshot.getChildren()) {
                    String guestId = guestSnap.getKey();
                    String name = guestSnap.child("name").getValue(String.class);
                    String phone = guestSnap.child("phone").getValue(String.class);
                    String checkIn = guestSnap.child("checkIn").getValue(String.class);
                    String checkOut = guestSnap.child("checkOut").getValue(String.class);

                    ArrayList<String> bookedRooms = new ArrayList<>();
                    if (guestSnap.hasChild("bookedRooms")) {
                        for (DataSnapshot roomSnap : guestSnap.child("bookedRooms").getChildren()) {
                            bookedRooms.add(roomSnap.getValue(String.class));
                        }
                    }

                    for (String rId : bookedRooms) {
                        Map<String, String> entry = new HashMap<>();
                        entry.put("guestId", guestId);
                        entry.put("name", name);
                        entry.put("phone", phone);
                        entry.put("checkIn", checkIn);
                        entry.put("checkOut", checkOut);

                        String rNum = roomMap.get(rId);
                        entry.put("roomNum", rNum != null ? rNum : "Unknown");

                        flattenedList.add(entry);
                    }
                }

                Collections.sort(flattenedList, new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> o1, Map<String, String> o2) {
                        String id1 = o1.get("guestId");
                        String id2 = o2.get("guestId");


                        int idCompare = id1.compareTo(id2);
                        if (idCompare != 0) {
                            return idCompare;
                        }


                        String r1 = o1.get("roomNum");
                        String r2 = o2.get("roomNum");
                        if (r1 == null) return -1;
                        if (r2 == null) return 1;
                        return r1.compareTo(r2);
                    }
                });


                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewGuestsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}