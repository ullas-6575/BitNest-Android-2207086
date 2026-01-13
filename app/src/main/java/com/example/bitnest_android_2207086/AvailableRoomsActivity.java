package com.example.bitnest_android_2207086;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

public class AvailableRoomsActivity extends AppCompatActivity implements RoomAdapter.OnRoomListener {

    RecyclerView recyclerView;
    DatabaseReference database;
    RoomAdapter roomAdapter;
    ArrayList<Room> list;
    Button btnConfirmBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_rooms);

        recyclerView = findViewById(R.id.recyclerView);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        database = FirebaseDatabase.getInstance("https://bitnest-auth-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("rooms");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        roomAdapter = new RoomAdapter(this, list, this);
        recyclerView.setAdapter(roomAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Room room = dataSnapshot.getValue(Room.class);
                    if (room != null) {
                        list.add(room);
                    }
                }
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AvailableRoomsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirmBooking.setOnClickListener(v -> {
            ArrayList<Room> selectedRooms = roomAdapter.getSelectedRooms();

            if (selectedRooms.isEmpty()) {
                Toast.makeText(AvailableRoomsActivity.this, "Please select at least one room.", Toast.LENGTH_SHORT).show();
            } else {
                ArrayList<String> roomIds = new ArrayList<>();
                for (Room r : selectedRooms) {
                    roomIds.add(r.roomId);
                }

                Intent intent = new Intent(AvailableRoomsActivity.this, ConfirmBookingActivity.class);
                intent.putStringArrayListExtra("ROOM_IDS", roomIds);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRoomClick(int count) {
        if (count > 0) {
            btnConfirmBooking.setText("Confirm Booking (" + count + ")");
        } else {
            btnConfirmBooking.setText("Confirm Booking");
        }
    }
}