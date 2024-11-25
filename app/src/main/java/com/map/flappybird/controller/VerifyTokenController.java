package com.map.flappybird.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.map.flappybird.activity.ResetPasswordActivity;
import com.map.flappybird.http.HttpClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VerifyTokenController {

    private final ExecutorService executorService;
    private final Handler mainHandler;

    public VerifyTokenController() {
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void verifyToken(String email, String token, Context context) {
        if (email == null || email.isEmpty() || token == null || token.isEmpty()) {
            Toast.makeText(context, "Email và token không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            try {
                HttpClient httpClient = new HttpClient(context);
                boolean isSuccess = httpClient.postVerifyToken("http://10.0.2.2:8080/api/verify-token", email, token);

                // Update the UI on the main thread
                mainHandler.post(() -> {
                    if (isSuccess) {
                        Toast.makeText(context, "Token verified successfully.", Toast.LENGTH_SHORT).show();

                        // Navigate to ResetPasswordActivity
                        Intent intent = new Intent(context, ResetPasswordActivity.class);
                        intent.putExtra("email", email); // Pass email to the next screen
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Invalid or expired token.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> Toast.makeText(context, "Error occurred while verifying the token.", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
