package com.example.bitnest_android_2207086;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ConfirmBookingActivity extends AppCompatActivity {

    private EditText etName, etPhone, etIdProof, etCheckIn, etCheckOut;
    private Button btnConfirmFinal;
    private ArrayList<String> selectedRoomIds;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        database = FirebaseDatabase.getInstance("https://bitnest-auth-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        if (getIntent().hasExtra("ROOM_IDS")) {
            selectedRoomIds = getIntent().getStringArrayListExtra("ROOM_IDS");
        } else {
            selectedRoomIds = new ArrayList<>();
        }

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etIdProof = findViewById(R.id.etIdProof);
        etCheckIn = findViewById(R.id.etCheckIn);
        etCheckOut = findViewById(R.id.etCheckOut);
        btnConfirmFinal = findViewById(R.id.btnConfirmFinal);
    }

    private void setupListeners() {
        etCheckIn.setOnClickListener(v -> showDatePicker(etCheckIn));
        etCheckOut.setOnClickListener(v -> showDatePicker(etCheckOut));
        btnConfirmFinal.setOnClickListener(v -> processToPayment());
    }

    private void showDatePicker(EditText targetField) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    targetField.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void processToPayment() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String idProof = etIdProof.getText().toString().trim();
        String checkIn = etCheckIn.getText().toString().trim();
        String checkOut = etCheckOut.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(idProof)
                || TextUtils.isEmpty(checkIn) || TextUtils.isEmpty(checkOut)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedRoomIds.isEmpty()) {
            Toast.makeText(this, "No rooms selected", Toast.LENGTH_SHORT).show();
            return;
        }

        btnConfirmFinal.setEnabled(false);
        btnConfirmFinal.setText("Processing...");

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "unknown_user");

        calculateTotalAndProceed(0, 0.0, new StringBuilder(), name, phone, username, checkIn, checkOut);
    }

    private void calculateTotalAndProceed(int index, double currentTotalPrice, StringBuilder roomNumbers,
                                          String name, String phone, String username, String checkIn, String checkOut) {

        if (index >= selectedRoomIds.size()) {
            Intent intent = new Intent(ConfirmBookingActivity.this, PaymentActivity.class);
            intent.putStringArrayListExtra("ROOM_IDS", selectedRoomIds);
            intent.putExtra("NAME", name);
            intent.putExtra("PHONE", phone);
            intent.putExtra("USERNAME", username);
            intent.putExtra("ROOM_NUM", roomNumbers.toString());
            intent.putExtra("CHECK_IN", checkIn);
            intent.putExtra("CHECK_OUT", checkOut);
            intent.putExtra("PRICE", currentTotalPrice);

            startActivity(intent);
            btnConfirmFinal.setEnabled(true);
            btnConfirmFinal.setText("Confirm Booking");
            return;
        }

        String roomId = selectedRoomIds.get(index);
        database.child("rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double roomPrice = 0;
                String roomNum = "Unknown";

                if (snapshot.exists()) {
                    try {
                        Object val = snapshot.child("price").getValue();
                        if (val instanceof Long) roomPrice = ((Long) val).doubleValue();
                        else if (val instanceof String) roomPrice = Double.parseDouble((String) val);
                        else if (val instanceof Double) roomPrice = (Double) val;
                    } catch (Exception e) { roomPrice = 0; }

                    if (snapshot.hasChild("roomNumber")) {
                        roomNum = snapshot.child("roomNumber").getValue(String.class);
                    }
                }

                if (roomNumbers.length() > 0) roomNumbers.append(", ");
                roomNumbers.append(roomNum);

                calculateTotalAndProceed(index + 1, currentTotalPrice + roomPrice, roomNumbers,
                        name, phone, username, checkIn, checkOut);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConfirmBookingActivity.this, "Error fetching room data", Toast.LENGTH_SHORT).show();
                btnConfirmFinal.setEnabled(true);
            }
        });
    }
}