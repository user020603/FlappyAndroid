package com.map.flappybird.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.map.flappybird.activity.MenuActivity;
import com.map.flappybird.http.HttpClient;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginController {

    private final HttpClient httpClient;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private static final String LOGIN_URL = "http://10.0.2.2:8080/api/login";

    public LoginController(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void loginUser(String username, String password, Context context) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            Toast.makeText(context, "Username hoặc password không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            try {
                // Gửi yêu cầu đăng nhập
                JSONObject response = httpClient.postLoginUser(LOGIN_URL, username, password);

                // Xử lý phản hồi trong luồng chính
                mainHandler.post(() -> {
                    if (response != null) {
                        handleLoginSuccess(response, context);
                    } else {
                        Toast.makeText(context, "Username hoặc password không đúng", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> Toast.makeText(context, "Đã xảy ra lỗi khi đăng nhập", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void handleLoginSuccess(JSONObject response, Context context) {
        try {
            // Lấy thông tin từ phản hồi JSON
            String userId = response.getString("userId");
            String username = response.getString("username");
            String token = response.getString("token");

            // Lưu token vào SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            prefs.edit().putString("token", token).apply();

            // Hiển thị thông báo thành công
            Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

            // Chuyển sang MenuActivity với userId và username
            Intent intent = new Intent(context, MenuActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("username", username);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Lỗi khi xử lý phản hồi từ máy chủ", Toast.LENGTH_SHORT).show();
        }
    }
}
