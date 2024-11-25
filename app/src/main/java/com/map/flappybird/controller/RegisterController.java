package com.map.flappybird.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.map.flappybird.activity.LoginActivity;
import com.map.flappybird.http.HttpClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterController {

    private final HttpClient httpClient;
    private static final String REGISTER_URL = "http://10.0.2.2:8080/api/register";
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public RegisterController(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void registerUser(String username, String password, Context context) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            Toast.makeText(context, "Username or password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isPasswordStrong(password)) {
            Toast.makeText(context, "Password must be at least 8 characters long, include uppercase, lowercase, digit, and special character.", Toast.LENGTH_LONG).show();
            return;
        }

        executorService.execute(() -> {
            try {
                boolean isSuccess = httpClient.postRegisterUser(REGISTER_URL, username, password);

                // Post result to the main thread
                mainHandler.post(() -> {
                    if (isSuccess) {
                        Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> Toast.makeText(context, "An error occurred during registration", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private boolean isPasswordStrong(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*[0-9].*")) return false;
        return password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    }
}
