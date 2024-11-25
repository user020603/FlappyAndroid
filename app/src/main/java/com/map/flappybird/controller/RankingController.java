package com.map.flappybird.controller;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.map.flappybird.activity.RankingActivity;
import com.map.flappybird.http.HttpClient;
import com.map.flappybird.model.Score;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RankingController {

    private final Context context;
    private final RankingControllerCallback callback;
    private final HttpClient httpClient;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public RankingController(Context context, RankingControllerCallback callback) {
        this.context = context;
        this.callback = callback;
        this.httpClient = new HttpClient(context);
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void fetchTopThreeData() {
        executorService.execute(() -> {
            try {
                JSONObject response = httpClient.getRankingData(0);
                if (response != null) {
                    JSONArray scoresArray = response.getJSONArray("scores");
                    List<Score> topScores = new ArrayList<>();

                    for (int i = 0; i < Math.min(3, scoresArray.length()); i++) {
                        JSONObject scoreObject = scoresArray.getJSONObject(i);
                        String username = scoreObject.getString("username");
                        int score = scoreObject.getInt("score");
                        String createdAt = scoreObject.getString("createdAt");
                        int userId = scoreObject.getInt("userId");
                        topScores.add(new Score(username, score, createdAt, userId));
                    }

                    // Chuyển kết quả về luồng chính
                    mainHandler.post(() -> callback.onTopThreeFetched(topScores));
                } else {
                    mainHandler.post(() ->
                            Toast.makeText(context, "Failed to fetch top 3 data", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() ->
                        Toast.makeText(context, "Error fetching top 3 data", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fetchRankingData(int page) {
        executorService.execute(() -> {
            try {
                JSONObject response = httpClient.getRankingData(page);
                if (response != null) {
                    int totalPages = response.getInt("totalPages");
                    JSONArray scoresArray = response.getJSONArray("scores");

                    List<Score> scores = new ArrayList<>();
                    for (int i = 0; i < scoresArray.length(); i++) {
                        JSONObject scoreObject = scoresArray.getJSONObject(i);
                        String username = scoreObject.getString("username");
                        int score = scoreObject.getInt("score");
                        String createdAt = RankingActivity.DateConverter(scoreObject.getString("createdAt"));
                        int userId = scoreObject.getInt("userId");

                        scores.add(new Score(username, score, createdAt, userId));
                    }

                    // Chuyển kết quả về luồng chính
                    mainHandler.post(() -> callback.onRankingDataFetched(scores, totalPages));
                } else {
                    mainHandler.post(() ->
                            Toast.makeText(context, "Failed to fetch ranking data", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() ->
                        Toast.makeText(context, "Error fetching ranking data", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // Callback interface for data updates
    public interface RankingControllerCallback {
        void onTopThreeFetched(List<Score> topScores);
        void onRankingDataFetched(List<Score> scores, int totalPages);
    }
}
