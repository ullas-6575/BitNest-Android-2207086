package com.example.bitnest_android_2207086;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddRoom extends AppCompatActivity {

    private EditText etRoomNumber, etRoomType, etRoomPrice;
    private Button btnSaveRoom;

    private static final String DATABASE_URL = "https://bitnest-auth-default-rtdb.asia-southeast1.firebasedatabase.app";

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_room);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL);
        databaseReference = firebaseDatabase.getReference("rooms");

        etRoomNumber = findViewById(R.id.etRoomNumber);
        etRoomType = findViewById(R.id.etRoomType);
        etRoomPrice = findViewById(R.id.etRoomPrice);
        btnSaveRoom = findViewById(R.id.btnSaveRoom);

        btnSaveRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRoomToFirebase();
            }
        });
    }

    private void saveRoomToFirebase() {
        String number = etRoomNumber.getText().toString().trim();
        String type = etRoomType.getText().toString().trim();
        String priceStr = etRoomPrice.getText().toString().trim();

        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(type) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        String roomId = databaseReference.push().getKey();

        double price = Double.parseDouble(priceStr);


        Room newRoom = new Room(roomId, number, type, price, true);


        if (roomId != null) {
            databaseReference.child(roomId).setValue(newRoom)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddRoom.this, "Room Added Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddRoom.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}