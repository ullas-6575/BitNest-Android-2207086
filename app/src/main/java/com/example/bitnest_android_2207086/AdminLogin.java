package com.example.bitnest_android_2207086;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminLogin extends AppCompatActivity {
    private EditText txtUsername;
    private EditText txtPassword;
    private TextView lblError;
    private Button btnLogin;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminlogin);


        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        lblError = findViewById(R.id.lblError);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginAction();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBackAction();
            }
        });
    }

    private void handleLoginAction() {

        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        lblError.setText("");


        if ("ullas".equals(username) && "86".equals(password)) {
            lblError.setTextColor(Color.GREEN);
            lblError.setText("Login Successful!");

            Intent intent = new Intent(AdminLogin.this, AdminDashboard.class);
            startActivity(intent);
            finish();

        } else {

            lblError.setTextColor(Color.parseColor("#e74c3c"));
            lblError.setText("Invalid Username or Password.");
        }
    }

    private void handleBackAction() {
        Intent intent = new Intent(AdminLogin.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}