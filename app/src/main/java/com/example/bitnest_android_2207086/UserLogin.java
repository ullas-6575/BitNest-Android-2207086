package com.example.bitnest_android_2207086;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserLogin extends AppCompatActivity {
    private TextInputEditText txtEmail, txtPassword;
    private Button btnLogin, btnRegister, btnBack;
    private TextView lblError;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlogin);

        mAuth = FirebaseAuth.getInstance();


        txtEmail = findViewById(R.id.txtUserEmail);
        txtPassword = findViewById(R.id.txtUserPassword);
        btnLogin = findViewById(R.id.btnUserLogin);
        btnRegister = findViewById(R.id.btnUserRegister);
        btnBack = findViewById(R.id.btnBack);
        lblError = findViewById(R.id.lblError);


        btnBack.setOnClickListener(v -> finish());


        btnLogin.setOnClickListener(v -> handleLogin());


        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleLogin() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (validateInput(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(UserLogin.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            navigateToDashboard();
                        } else {
                            lblError.setText("Authentication failed: " + task.getException().getMessage());
                        }
                    });
        }
    }

    private void handleRegister() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (validateInput(email, password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserLogin.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                            navigateToDashboard();
                        } else {
                            lblError.setText("Registration failed: " + task.getException().getMessage());
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
        Intent intent = new Intent(UserLogin.this, UserDashboard.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}