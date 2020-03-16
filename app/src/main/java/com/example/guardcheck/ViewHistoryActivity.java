package com.example.guardcheck;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewHistoryActivity extends AppCompatActivity {

    ListView listViewHistory;
    private ArrayList<History> historyList;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        listViewHistory = findViewById(R.id.listViewHistory);
        db = new DatabaseHelper(getApplicationContext());
        historyList = db.getGuardHistory();
        Log.d("Test", historyList.toString());

        HistoryListAdapter adapter = new HistoryListAdapter(this,R.layout.adapter_view_layout,historyList);
        listViewHistory.setAdapter(adapter);
    }
}
