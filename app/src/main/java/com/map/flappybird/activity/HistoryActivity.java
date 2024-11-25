package com.map.flappybird.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.map.flappybird.R;
import com.map.flappybird.adapter.HistoryAdapter;
import com.map.flappybird.controller.HistoryController;
import com.map.flappybird.model.Score;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements HistoryController.HistoryControllerCallback {

    private HistoryAdapter historyAdapter;
    private final List<Score> historyList = new ArrayList<>();
    private HistoryController historyController;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String DateConverter(String dateStr) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateStr);
        ZonedDateTime gmtPlus7DateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("GMT+7"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        return formatter.format(gmtPlus7DateTime);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        String userId = getIntent().getStringExtra("userId");
        if (userId == null || Integer.parseInt(userId) == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RecyclerView recyclerViewHistory = findViewById(R.id.historyRecyclerView);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        historyAdapter = new HistoryAdapter(historyList);
        recyclerViewHistory.setAdapter(historyAdapter);

        // Initialize HistoryController
        historyController = new HistoryController(this, this);

        // Fetch initial history (last 6 games)
        historyController.fetchUserHistory(Integer.parseInt(userId));

        // Set up DatePicker
        Button btnDatePicker = findViewById(R.id.btnDatePicker);
        btnDatePicker.setOnClickListener(v -> openDatePicker(Integer.parseInt(userId)));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void openDatePicker(int userId) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    @SuppressLint("DefaultLocale") String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    fetchHistoryByDate(userId, selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchHistoryByDate(int userId, String date) {
        historyController.fetchUserHistoryByDate(userId, date);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onHistoryDataFetched(List<Score> historyList) {
        this.historyList.clear();
        this.historyList.addAll(historyList);
        historyAdapter.notifyDataSetChanged();
    }
}
