package com.map.flappybird.controller;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.map.flappybird.activity.HistoryActivity;
import com.map.flappybird.http.HttpClient;
import com.map.flappybird.model.Score;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryController {

    private final Context context;
    private final HistoryControllerCallback callback;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public HistoryController(Context context, HistoryControllerCallback callback) {
        this.context = context;
        this.callback = callback;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fetchUserHistory(int userId) {
        executorService.execute(() -> {
            List<Score> historyList = fetchHistoryFromServer(userId, null); // Fetch latest 6 scores
            mainHandler.post(() -> {
                if (historyList != null) {
                    callback.onHistoryDataFetched(historyList);
                } else {
                    Toast.makeText(context, "Failed to fetch history data", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fetchUserHistoryByDate(int userId, String date) {
        executorService.execute(() -> {
            List<Score> historyList = fetchHistoryFromServer(userId, date); // Fetch scores for a specific date
            mainHandler.post(() -> {
                if (historyList != null) {
                    callback.onHistoryDataFetched(historyList);
                } else {
                    Toast.makeText(context, "Failed to fetch history for the selected date", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Score> fetchHistoryFromServer(int userId, String date) {
        try {
            HttpClient httpClient = new HttpClient(context);
            JSONArray response = (date == null) ? httpClient.getUserHistory(userId) : httpClient.getUserHistoryByDate(userId, date);
            List<Score> historyList = new ArrayList<>();

            if (response != null) {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject historyObject = response.getJSONObject(i);
                    String username = historyObject.getString("username");
                    int score = historyObject.getInt("score");
                    String createdAt = HistoryActivity.DateConverter(historyObject.getString("createdAt"));
                    historyList.add(new Score(username, score, createdAt));
                }
                return historyList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Callback interface for data updates
    public interface HistoryControllerCallback {
        void onHistoryDataFetched(List<Score> historyList);
    }
}
