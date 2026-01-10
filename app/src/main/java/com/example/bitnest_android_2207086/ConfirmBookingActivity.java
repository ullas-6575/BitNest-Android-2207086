package com.example.bitnest_android_2207086;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class ConfirmBookingActivity extends AppCompatActivity {

    private EditText etName, etPhone, etIdProof, etCheckIn, etCheckOut;
    private Button btnConfirmFinal, btnBack;
    private ArrayList<String> selectedRoomIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

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
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

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

        Intent intent = new Intent(ConfirmBookingActivity.this, PaymentActivity.class);
        intent.putStringArrayListExtra("ROOM_IDS", selectedRoomIds);
        intent.putExtra("NAME", name);
        intent.putExtra("PHONE", phone);
        intent.putExtra("ID_PROOF", idProof);
        intent.putExtra("CHECK_IN", checkIn);
        intent.putExtra("CHECK_OUT", checkOut);
        startActivity(intent);
    }
}