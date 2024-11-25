package com.map.flappybird.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.map.flappybird.R;
import com.map.flappybird.controller.ForgotPasswordController;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailInput = findViewById(R.id.emailInput);
        Button sendTokenButton = findViewById(R.id.sendTokenButton);

        sendTokenButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show();
            } else {
                new ForgotPasswordController().requestForgotPassword(email, this);
            }
        });
    }
}
