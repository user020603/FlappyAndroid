package com.map.flappybird.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.map.flappybird.R;
import com.map.flappybird.controller.LoginController;
import com.map.flappybird.http.HttpClient;


public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerLinkTextView = findViewById(R.id.registerLinkTextView);
        TextView forgotPasswordLinkTextView = findViewById(R.id.forgotPassword);

        loginButton.setOnClickListener(v -> onLoginButtonClick());

        registerLinkTextView.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        forgotPasswordLinkTextView.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class))
        );
    }

    private void onLoginButtonClick() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        HttpClient httpClient = new HttpClient(this);
        LoginController loginController = new LoginController(httpClient);
        loginController.loginUser(username, password, this);
    }
}

