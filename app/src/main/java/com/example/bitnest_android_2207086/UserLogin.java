package com.example.bitnest_android_2207086;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class UserLogin extends AppCompatActivity {
    private TextInputEditText txtEmail, txtPassword;
    private Button btnLogin, btnRegister;
    private TextView lblError;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlogin); // Ensure this matches your XML filename

        mAuth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.txtUserEmail);
        txtPassword = findViewById(R.id.txtUserPassword);
        btnLogin = findViewById(R.id.btnUserLogin);
        btnRegister = findViewById(R.id.btnUserRegister);
        lblError = findViewById(R.id.lblError);

        btnLogin.setOnClickListener(v -> handleLogin());

        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleLogin() {
        String email = "";
        String password = "";

        if (txtEmail.getText() != null) email = txtEmail.getText().toString().trim();
        if (txtPassword.getText() != null) password = txtPassword.getText().toString().trim();

        if (validateInput(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserLogin.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            navigateToDashboard();
                        } else {
                            String msg = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                            lblError.setText(msg);
                        }
                    });
        }
    }

    private void handleRegister() {
        String email = "";
        String password = "";

        if (txtEmail.getText() != null) email = txtEmail.getText().toString().trim();
        if (txtPassword.getText() != null) password = txtPassword.getText().toString().trim();

        if (validateInput(email, password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserLogin.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                            navigateToDashboard();
                        } else {
                            String msg = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                            lblError.setText(msg);
                        }
                    });
        }
    }

    private boolean validateInput(String email, String password) {
        lblError.setText("");
        if (TextUtils.isEmpty(email)) {
            lblError.setText("Please enter an email address.");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            lblError.setText("Please enter a password.");
            return false;
        }
        return true;
    }

    private void navigateToDashboard() {
        // Save Username for later use in Booking
        String email = txtEmail.getText().toString();
        String username = email.contains("@") ? email.split("@")[0] : email;

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.apply();

        Intent intent = new Intent(UserLogin.this, UserDashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}