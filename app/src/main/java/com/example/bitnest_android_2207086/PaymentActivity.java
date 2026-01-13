package com.example.bitnest_android_2207086;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PaymentActivity extends AppCompatActivity {

    TextView tvTotalBill;
    EditText etCardNumber, etExpiryMonth, etExpiryYear;
    Button btnFinalizePayment;
    DatabaseReference database;

    String guestName, guestPhone, guestUsername, roomNumString, checkIn, checkOut;
    ArrayList<String> allRoomIds;
    double pricePerNightTotal;
    double finalTotalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvTotalBill = findViewById(R.id.tvTotalBill);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiryMonth = findViewById(R.id.etExpiryMonth);
        etExpiryYear = findViewById(R.id.etExpiryYear);
        btnFinalizePayment = findViewById(R.id.btnFinalizePayment);

        database = FirebaseDatabase.getInstance("https://bitnest-auth-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        if (getIntentData()) {
            calculateTotalBill();
        } else {
            Toast.makeText(this, "Error loading booking data", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnFinalizePayment.setOnClickListener(v -> {
            if (validateCardDetails()) {
                saveBookingToFirebase();
            }
        });
    }

    private boolean getIntentData() {
        Intent i = getIntent();
        if (i == null) return false;

        guestName = i.getStringExtra("NAME");
        guestPhone = i.getStringExtra("PHONE");
        guestUsername = i.getStringExtra("USERNAME");
        roomNumString = i.getStringExtra("ROOM_NUM");
        checkIn = i.getStringExtra("CHECK_IN");
        checkOut = i.getStringExtra("CHECK_OUT");
        pricePerNightTotal = i.getDoubleExtra("PRICE", 0.0);

        if (i.hasExtra("ROOM_IDS")) {
            allRoomIds = i.getStringArrayListExtra("ROOM_IDS");
        }

        if (allRoomIds == null) {
            allRoomIds = new ArrayList<>();
            if (i.hasExtra("ROOM_ID")) {
                allRoomIds.add(i.getStringExtra("ROOM_ID"));
            }
        }

        return !allRoomIds.isEmpty();
    }

    private void calculateTotalBill() {
        long days = calculateDays(checkIn, checkOut);
        if (days <= 0) days = 1;
        finalTotalAmount = days * pricePerNightTotal;
        tvTotalBill.setText("tk " + String.format(Locale.US, "%.2f", finalTotalAmount));
    }

    private long calculateDays(String start, String end) {
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        try {
            Date d1 = sdf.parse(start);
            Date d2 = sdf.parse(end);
            long diff = d2.getTime() - d1.getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (Exception e) { return 1; }
    }

    private boolean validateCardDetails() {
        String cardNum = etCardNumber.getText().toString();
        String mm = etExpiryMonth.getText().toString();
        String yy = etExpiryYear.getText().toString();

        if (TextUtils.isEmpty(cardNum) || cardNum.length() < 4) {
            etCardNumber.setError("Invalid Card Number must be at least 4 digits");
            return false;
        }
        if (TextUtils.isEmpty(mm) || TextUtils.isEmpty(yy)) {
            Toast.makeText(this, "Enter Expiry Date", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveBookingToFirebase() {
        String guestId = database.child("guests").push().getKey();
        if (guestId == null) return;

        Map<String, Object> guestData = new HashMap<>();
        guestData.put("guestId", guestId);
        guestData.put("name", guestName);
        guestData.put("phone", guestPhone);
        guestData.put("username", guestUsername);
        guestData.put("checkIn", checkIn);
        guestData.put("checkOut", checkOut);
        guestData.put("bookedRooms", allRoomIds);
        guestData.put("totalPaid", finalTotalAmount);

        Map<String, Object> updates = new HashMap<>();
        updates.put("guests/" + guestId, guestData);

        for (String rId : allRoomIds) {
            String roomPath = "rooms/" + rId;
            updates.put(roomPath + "/isAvailable", false);
            updates.put(roomPath + "/bookedByGuestId", guestId);
            updates.put(roomPath + "/bookedByGuestName", guestName);
            updates.put(roomPath + "/bookedByPhone", guestPhone);
            updates.put(roomPath + "/checkInDate", checkIn);
            updates.put(roomPath + "/checkOutDate", checkOut);
        }

        database.updateChildren(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(PaymentActivity.this, "Payment Successful!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PaymentActivity.this, UserDashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(PaymentActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}