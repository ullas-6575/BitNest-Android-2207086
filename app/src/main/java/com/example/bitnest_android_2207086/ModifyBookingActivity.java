package com.example.bitnest_android_2207086;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModifyBookingActivity extends AppCompatActivity {

    Spinner spinnerSourceRoom, spinnerTargetRoom;
    Button btnSwapRoom;
    DatabaseReference database;

    ArrayList<String> sourceListDisplay = new ArrayList<>();
    ArrayList<String> targetListDisplay = new ArrayList<>();

    ArrayList<String> sourceGuestIds = new ArrayList<>();
    ArrayList<String> sourceRoomIds = new ArrayList<>();
    ArrayList<String> targetRoomIds = new ArrayList<>();

    HashMap<String, String> roomMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_booking);

        database = FirebaseDatabase.getInstance("https://bitnest-auth-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        spinnerSourceRoom = findViewById(R.id.spinnerSourceRoom);
        spinnerTargetRoom = findViewById(R.id.spinnerTargetRoom);
        btnSwapRoom = findViewById(R.id.btnSwapRoom);

        loadRoomsAndGuests();

        btnSwapRoom.setOnClickListener(v -> performSwap());
    }

    private void loadRoomsAndGuests() {
        database.child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomMap.clear();
                targetListDisplay.clear();
                targetRoomIds.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String rId = ds.getKey();
                    String rNum = ds.child("roomNumber").getValue(String.class);
                    Boolean isAvail = ds.child("isAvailable").getValue(Boolean.class);

                    if (rId != null && rNum != null) {
                        roomMap.put(rId, rNum);

                        if (Boolean.TRUE.equals(isAvail)) {
                            targetListDisplay.add("Room " + rNum);
                            targetRoomIds.add(rId);
                        }
                    }
                }

                ArrayAdapter<String> adapterTarget = new ArrayAdapter<>(ModifyBookingActivity.this, android.R.layout.simple_spinner_dropdown_item, targetListDisplay);
                spinnerTargetRoom.setAdapter(adapterTarget);

                loadActiveBookings();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadActiveBookings() {
        database.child("guests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sourceListDisplay.clear();
                sourceGuestIds.clear();
                sourceRoomIds.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String gId = ds.getKey();
                    String gName = ds.child("name").getValue(String.class);
                    ArrayList<String> rooms = (ArrayList<String>) ds.child("bookedRooms").getValue();

                    if (rooms != null && !rooms.isEmpty()) {
                        for (String rId : rooms) {
                            if (roomMap.containsKey(rId)) {
                                String rNum = roomMap.get(rId);
                                sourceListDisplay.add("Room " + rNum + " (" + gName + ")");
                                sourceGuestIds.add(gId);
                                sourceRoomIds.add(rId);
                            }
                        }
                    }
                }

                ArrayAdapter<String> adapterSource = new ArrayAdapter<>(ModifyBookingActivity.this, android.R.layout.simple_spinner_dropdown_item, sourceListDisplay);
                spinnerSourceRoom.setAdapter(adapterSource);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void performSwap() {
        int srcPos = spinnerSourceRoom.getSelectedItemPosition();
        int tgtPos = spinnerTargetRoom.getSelectedItemPosition();

        if (srcPos < 0 || tgtPos < 0) {
            Toast.makeText(this, "Select both rooms", Toast.LENGTH_SHORT).show();
            return;
        }

        String guestId = sourceGuestIds.get(srcPos);
        String oldRoomId = sourceRoomIds.get(srcPos);
        String newRoomId = targetRoomIds.get(tgtPos);

        Map<String, Object> updates = new HashMap<>();

        updates.put("rooms/" + oldRoomId + "/isAvailable", true);
        updates.put("rooms/" + newRoomId + "/isAvailable", false);

        database.child("guests").child(guestId).child("bookedRooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> currentRooms = (ArrayList<String>) snapshot.getValue();
                if (currentRooms != null) {
                    for (int i = 0; i < currentRooms.size(); i++) {
                        if (currentRooms.get(i).equals(oldRoomId)) {
                            currentRooms.set(i, newRoomId);
                            break;
                        }
                    }
                    updates.put("guests/" + guestId + "/bookedRooms", currentRooms);

                    database.updateChildren(updates).addOnSuccessListener(aVoid -> {
                        Toast.makeText(ModifyBookingActivity.this, "Room Swapped", Toast.LENGTH_SHORT).show();
                        loadRoomsAndGuests();
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
