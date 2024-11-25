package com.map.flappybird.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.map.flappybird.R;
import com.map.flappybird.controller.VerifyTokenController;

public class VerifyTokenActivity extends AppCompatActivity {

    private EditText tokenInput;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_token);

        tokenInput = findViewById(R.id.tokenInput);
        Button verifyTokenButton = findViewById(R.id.verifyTokenButton);

        email = getIntent().getStringExtra("email");

        verifyTokenButton.setOnClickListener(v -> {
            String token = tokenInput.getText().toString();
            if (token.isEmpty()) {
                Toast.makeText(this, "Please enter the token.", Toast.LENGTH_SHORT).show();
            } else {
                new VerifyTokenController().verifyToken(email, token, this);
            }
        });
    }
}
