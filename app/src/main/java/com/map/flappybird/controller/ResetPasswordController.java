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

public class ResetPasswordController {

    private final ExecutorService executorService;
    private final Handler mainHandler;

    public ResetPasswordController() {
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void resetPassword(String email, String newPassword, Context context) {
        if (email == null || email.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            Toast.makeText(context, "Email và mật khẩu mới không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            try {
                HttpClient httpClient = new HttpClient(context);
                boolean isSuccess = httpClient.postResetPassword("http://10.0.2.2:8080/api/reset-password", email, newPassword);

                // Update the UI on the main thread
                mainHandler.post(() -> {
                    if (isSuccess) {
                        Toast.makeText(context, "Password reset successfully.", Toast.LENGTH_SHORT).show();

                        // Navigate to LoginActivity
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Failed to reset password.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> Toast.makeText(context, "Error occurred while resetting password.", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
