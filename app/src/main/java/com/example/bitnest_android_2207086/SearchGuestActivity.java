package com.example.bitnest_android_2207086;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.Map;

public class SearchGuestActivity extends AppCompatActivity {

    Spinner spinnerRooms;
    Button btnSearch;
    CardView cardResult;
    TextView tvResultStatus, tvGuestName, tvGuestPhone, tvCheckDates;
    DatabaseReference database;

    ArrayList<String> roomDisplayList = new ArrayList<>();
    ArrayList<String> roomIds = new ArrayList<>();
    ArrayList<Boolean> roomAvailability = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_guest);

        spinnerRooms = findViewById(R.id.spinnerRooms);
        btnSearch = findViewById(R.id.btnSearch);
        cardResult = findViewById(R.id.cardResult);
        tvResultStatus = findViewById(R.id.tvResultStatus);
        tvGuestName = findViewById(R.id.tvGuestName);
        tvGuestPhone = findViewById(R.id.tvGuestPhone);
        tvCheckDates = findViewById(R.id.tvCheckDates);

        database = FirebaseDatabase.getInstance("https://bitnest-auth-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        loadAllRooms();

        btnSearch.setOnClickListener(v -> performSearch());
    }

    private void loadAllRooms() {
        database.child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomDisplayList.clear();
                roomIds.clear();
                roomAvailability.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String rId = ds.getKey();
                    String rNum = ds.child("roomNumber").getValue(String.class);
                    Boolean isAvail = ds.child("isAvailable").getValue(Boolean.class);

                    if (rId != null && rNum != null) {
                        roomDisplayList.add("Room " + rNum);
                        roomIds.add(rId);
                        roomAvailability.add(Boolean.TRUE.equals(isAvail));
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchGuestActivity.this, android.R.layout.simple_spinner_dropdown_item, roomDisplayList);
                spinnerRooms.setAdapter(adapter);


                if (getIntent().hasExtra("ROOM_NUMBER")) {
                    String targetNum = getIntent().getStringExtra("ROOM_NUMBER");
                    for(int i=0; i<roomDisplayList.size(); i++){
                        if(roomDisplayList.get(i).contains(targetNum)){
                            spinnerRooms.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void performSearch() {
        int pos = spinnerRooms.getSelectedItemPosition();
        if (pos < 0) {
            Toast.makeText(this, "No room selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedRoomId = roomIds.get(pos);
        String selectedRoomName = roomDisplayList.get(pos);
        boolean isAvailable = roomAvailability.get(pos);

        cardResult.setVisibility(View.GONE);

        if (isAvailable) {
            showAvailable(selectedRoomName);
        } else {
            findGuestByRoomId(selectedRoomId, selectedRoomName);
        }
    }

    private void showAvailable(String roomName) {
        cardResult.setVisibility(View.VISIBLE);
        tvResultStatus.setText(roomName + " is currently Available");
        tvResultStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

        tvGuestName.setText("No Guest");
        tvGuestPhone.setText("");
        tvCheckDates.setText("Ready for booking");
    }

    private void findGuestByRoomId(String roomId, String roomName) {
        database.child("guests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> guest = (Map<String, Object>) ds.getValue();
                    ArrayList<String> rooms = (ArrayList<String>) guest.get("bookedRooms");

                    if (rooms != null && rooms.contains(roomId)) {
                        displayGuest(guest, roomName);
                        found = true;
                        break;
                    }
                }
                if (!found) {

                    Toast.makeText(SearchGuestActivity.this, "Room marked occupied but no guest found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void displayGuest(Map<String, Object> guest, String roomName) {
        cardResult.setVisibility(View.VISIBLE);
        tvResultStatus.setText(roomName + " is Occupied");
        tvResultStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

        tvGuestName.setText("Guest: " + guest.get("name"));
        tvGuestPhone.setText("Phone: " + guest.get("phone"));
        tvCheckDates.setText("In: " + guest.get("checkIn") + "\nOut: " + guest.get("checkOut"));
    }
}