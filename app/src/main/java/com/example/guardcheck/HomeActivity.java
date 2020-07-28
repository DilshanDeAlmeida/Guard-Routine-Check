package com.example.guardcheck;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class HomeActivity extends AppCompatActivity {

    Spinner cmbGuards;
    ImageButton btnScanner;
    ImageButton btnSettings;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialzeElements();

        if (cmbGuards.getSelectedItemId() > -1) {
            btnScanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, ScannerActivity.class).putExtra("guardName", cmbGuards.getSelectedItem().toString()));
                }
            });

            btnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                }
            });
        }

        startService(new Intent(this, RSSPullService.class));
    }

    void initialzeElements() {

        cmbGuards = findViewById(R.id.cmbUsers);
        btnScanner = findViewById(R.id.btnScanner);
        btnSettings = findViewById(R.id.btnSettings);

        ArrayList<String> guards = new ArrayList<>();
        guards.add("Guard-1");
        guards.add("Guard-2");

        Collections.sort(guards, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, guards);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbGuards.setAdapter(adapter);
    }
}
