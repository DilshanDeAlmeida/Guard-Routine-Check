package com.example.guardcheck;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SettingsActivity extends AppCompatActivity {


    ImageButton btnLocations;
    ImageButton btnHistoryView;
    ImageButton btnSaveToDB;
    Button btnAlarmdo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnLocations = findViewById(R.id.btnLocations);
        btnHistoryView = findViewById(R.id.btnViewHistory);
        btnSaveToDB = findViewById(R.id.btnSaveToDB);
        btnAlarmdo = findViewById(R.id.btnDoIt);


        btnLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ViewDataActivity.class));
            }
        });

        btnHistoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ViewHistoryActivity.class));
            }
        });

        btnSaveToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    File sd = Environment.getExternalStorageDirectory();
                    Date currentTime = Calendar.getInstance().getTime();

                    if (sd.canWrite()) {
                        String currentDBPath = "/data/data/" + getPackageName() + "/databases/tsguard";
                        String backupDBPath = currentTime.toString() + "tsguard.db";
                        File currentDB = new File(currentDBPath);
                        File backupDB = new File(sd, backupDBPath);

                        if (currentDB.exists()) {
                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();

                            // This line of code will clear the previous Check History after backing up
                            //DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                            //dbHelper.ClearHistoryFromPhoneDatabase();

                            Toast.makeText(getApplicationContext(), "Backup Success", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error :" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnAlarmdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do Nothing ...
            }
        });
    }
}
