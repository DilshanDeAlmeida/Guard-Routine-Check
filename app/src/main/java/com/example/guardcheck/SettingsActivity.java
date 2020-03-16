package com.example.guardcheck;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
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
    ImageButton btnPhone;
    ImageButton btnSaveToDB;
    Button btnAlarmdo;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnLocations = findViewById(R.id.btnLocations);
        btnPhone = findViewById(R.id.btnPhone);
        btnSaveToDB = findViewById(R.id.btnSaveToDB);
        btnAlarmdo = findViewById(R.id.btnDoIt);

        btnLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ViewDataActivity.class));
            }
        });

        btnPhone.setOnClickListener(new View.OnClickListener() {
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
                try {

//                    ArrayList<Calendar> notifyHrs = new ArrayList<Calendar>();
//                    notifyHrs = getAlarmTimes();
//
//                    for (Calendar calendar : notifyHrs) {
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmManager.INTERVAL_DAY, pendingIntent);
//                      }

                    //===========================================================================================================================================

                    mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    //Set up the Notification Broadcast Intent
                    Intent notifyIntent = new Intent(getApplicationContext(), SMSmessenger.class);

                    //Check if the Alarm is already set, and check the toggle accordingly
                    boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), 0, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);

                    //Set up the PendingIntent for the AlarmManager
                    final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    if (!alarmUp) {

                        Log.d("Test", alarmUp + " : Reached Here..");
                        long triggerTime = SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES;

                        long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

                        //If the Toggle is turned on, set the repeating alarm with a 15 minute interval
                        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, repeatInterval, notifyPendingIntent);

                    } else {

                        Log.d("Test", alarmUp + " : Reached Here.. [-Cancel]");
                        //Cancel the alarm and notification if the alarm is turned off
                        alarmManager.cancel(notifyPendingIntent);
                        mNotificationManager.cancelAll();
                    }

                    Toast.makeText(getApplicationContext(), "The Lords' work is DONE ... !", Toast.LENGTH_LONG).show();
                } catch (Exception exception) {
                    Toast.makeText(getApplicationContext(), "Error :" + exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private ArrayList<Calendar> getAlarmTimes() {
        ArrayList<Calendar> notifyHrs = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 05);
        notifyHrs.add(calendar);
        Log.d("Test", notifyHrs.get(0).toString());

//        calendar.set(Calendar.HOUR_OF_DAY, 18);
//        calendar.set(Calendar.MINUTE, 58);
//        notifyHrs.add(calendar);
//        Log.d("Test", notifyHrs.get(0).toString());
//
//        calendar.set(Calendar.HOUR_OF_DAY, 20);
//        calendar.set(Calendar.MINUTE, 28);
//        notifyHrs.add(calendar);
//        Log.d("Test", notifyHrs.get(1).toString());
//
//        calendar.set(Calendar.HOUR_OF_DAY, 21);
//        calendar.set(Calendar.MINUTE, 58);
//        notifyHrs.add(calendar);
//        Log.d("Test", notifyHrs.get(2).toString());
//
//        calendar.set(Calendar.HOUR_OF_DAY, 23);
//        calendar.set(Calendar.MINUTE, 28);
//        notifyHrs.add(calendar);
//        Log.d("Test", notifyHrs.get(3).toString());
//
//        calendar.set(Calendar.HOUR_OF_DAY, 00);
//        calendar.set(Calendar.MINUTE, 58);
//        notifyHrs.add(calendar);
//        Log.d("Test", notifyHrs.get(4).toString());
//
//        calendar.set(Calendar.HOUR_OF_DAY, 02);
//        calendar.set(Calendar.MINUTE, 28);
//        notifyHrs.add(calendar);
//        Log.d("Test", notifyHrs.get(5).toString());
//
//        calendar.set(Calendar.HOUR_OF_DAY, 03);
//        calendar.set(Calendar.MINUTE, 58);
//        notifyHrs.add(calendar);
//        Log.d("Test", notifyHrs.get(6).toString());
//
//        calendar.set(Calendar.HOUR_OF_DAY, 05);
//        calendar.set(Calendar.MINUTE, 28);
//        notifyHrs.add(calendar);
//        Log.d("Test", notifyHrs.get(7).toString());

        return notifyHrs;
    }
}
