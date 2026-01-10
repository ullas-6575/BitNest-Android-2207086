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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private EditText etCardNumber, etExpiryMonth, etExpiryYear;
    private TextView tvTotalBill;
    private Button btnFinalizePayment;
    private DatabaseReference database;

    private ArrayList<String> selectedRoomIds;
    private String name, phone, idProof, checkIn, checkOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        database = FirebaseDatabase.getInstance("https://bitnest-auth-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        Intent intent = getIntent();
        selectedRoomIds = intent.getStringArrayListExtra("ROOM_IDS");
        name = intent.getStringExtra("NAME");
        phone = intent.getStringExtra("PHONE");
        idProof = intent.getStringExtra("ID_PROOF");
        checkIn = intent.getStringExtra("CHECK_IN");
        checkOut = intent.getStringExtra("CHECK_OUT");

        initializeViews();

        double estimatedTotal = selectedRoomIds.size() * 100.00;
        tvTotalBill.setText("$" + estimatedTotal);

        btnFinalizePayment.setOnClickListener(v -> handlePayment());
    }

    private void initializeViews() {
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiryMonth = findViewById(R.id.etExpiryMonth);
        etExpiryYear = findViewById(R.id.etExpiryYear);
        tvTotalBill = findViewById(R.id.tvTotalBill);
        btnFinalizePayment = findViewById(R.id.btnFinalizePayment);
    }

    private void handlePayment() {
        if (TextUtils.isEmpty(etCardNumber.getText()) ||
                TextUtils.isEmpty(etExpiryMonth.getText()) ||
                TextUtils.isEmpty(etExpiryYear.getText())) {
            Toast.makeText(this, "Please fill in all payment details", Toast.LENGTH_SHORT).show();
            return;
        }

        saveBookingToFirebase();
    }

    private void saveBookingToFirebase() {
        String guestId = database.child("guests").push().getKey();

        Map<String, Object> guestData = new HashMap<>();
        guestData.put("guestId", guestId);
        guestData.put("name", name);
        guestData.put("phone", phone);
        guestData.put("idProof", idProof);
        guestData.put("checkIn", checkIn);
        guestData.put("checkOut", checkOut);
        guestData.put("bookedRooms", selectedRoomIds);
        guestData.put("paymentStatus", "Paid");

        Map<String, Object> updates = new HashMap<>();
        updates.put("guests/" + guestId, guestData);

        for (String roomId : selectedRoomIds) {
            updates.put("rooms/" + roomId + "/isAvailable", false);
        }

        database.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PaymentActivity.this, "Payment Successful! Booking Confirmed.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(PaymentActivity.this, UserDashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PaymentActivity.this, "Payment Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}