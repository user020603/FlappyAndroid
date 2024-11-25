package com.map.flappybird.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.map.flappybird.activity.VerifyTokenActivity;
import com.map.flappybird.http.HttpClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForgotPasswordController {

    private final ExecutorService executorService;
    private final Handler mainHandler;

    public ForgotPasswordController() {
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void requestForgotPassword(String email, Context context) {
        if (email == null || email.isEmpty()) {
            Toast.makeText(context, "Email không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Sending verification token...");
        progressDialog.setCancelable(false); // Prevent dismissing by tapping outside
        progressDialog.show();

        executorService.execute(() -> {
            try {
                HttpClient httpClient = new HttpClient(context);
                boolean success = httpClient.postForgotPassword("http://10.0.2.2:8080/api/forgot-password", email);

                // Update the UI on the main thread
                mainHandler.post(() -> {
                    progressDialog.dismiss(); // Hide the ProgressDialog

                    if (success) {
                        Toast.makeText(context, "Verification token sent to your email.", Toast.LENGTH_SHORT).show();

                        // Navigate to VerifyTokenActivity
                        Intent intent = new Intent(context, VerifyTokenActivity.class);
                        intent.putExtra("email", email); // Pass the email to the next screen
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Failed to send verification token.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    progressDialog.dismiss(); // Ensure the ProgressDialog is dismissed
                    Toast.makeText(context, "Error occurred while sending verification token.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
