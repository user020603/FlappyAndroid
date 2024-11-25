package com.map.flappybird.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.map.flappybird.R;
import com.map.flappybird.controller.ResetPasswordController;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText passwordInput, confirmPasswordInput;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        Button resetPasswordButton = findViewById(R.id.resetPasswordButton);

        email = getIntent().getStringExtra("email");

        resetPasswordButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            } else {
                new ResetPasswordController().resetPassword(email, password, this);
            }
        });
    }
}
