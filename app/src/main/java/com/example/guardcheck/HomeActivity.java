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

        // use this to start and trigger a service
        //Intent i= new Intent(HomeActivity.this, RSSPullService.class);
        //HomeActivity.this.startService(i);

        CreateNotificationChannel();
        SetNotificationTrigger();
        SetReportSendTrigger();
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

    // ==========================================================================================================================================

    private void SetNotificationTrigger() {
        try {
            Log.d("Test", " Pending Intent Set..");
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            ArrayList<Calendar> notifyHrs = getAlarmTimes();

            for (Calendar calendar : notifyHrs) {

                //Set up the Notification Broadcast Intent
                Intent notifyIntent = new Intent(getApplicationContext(), SMSmessenger.class);
                //Set up the PendingIntent for the AlarmManager
                PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, notifyIntent, 0);

                long triggerTime = System.currentTimeMillis();
                long repeatInterval = 10 * 1000; // 10 seconds

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                //alarmManager.set(AlarmManager.RTC_WAKEUP,triggerTime+repeatInterval,notifyPendingIntent);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmManager.INTERVAL_DAY, notifyPendingIntent);
            }
        } catch (Exception exception) {
            Toast.makeText(getApplicationContext(), "Error :" + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void SetReportSendTrigger() {
        try {
            Log.d("Test", " Pending Intent Set..");
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            ArrayList<Calendar> notifyHrs = getReportSendTimes();

            for (Calendar calendar : notifyHrs) {

                //Set up the Notification Broadcast Intent
                Intent notifyIntent = new Intent(getApplicationContext(), SendAutomaticReport.class);
                //Set up the PendingIntent for the AlarmManager
                PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, notifyIntent, 0);

                long triggerTime = System.currentTimeMillis();
                long repeatInterval = 10 * 1000; // 10 seconds

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                //alarmManager.set(AlarmManager.RTC_WAKEUP,triggerTime+repeatInterval,notifyPendingIntent);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmManager.INTERVAL_DAY, notifyPendingIntent);
            }
        } catch (Exception exception) {
            Toast.makeText(getApplicationContext(), "Error :" + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<Calendar> getAlarmTimes() {
        ArrayList<Calendar> notifyHrs = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 58);
        notifyHrs.add(calendar);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 58);
        notifyHrs.add(calendar);

        calendar.set(Calendar.HOUR_OF_DAY, 01);
        calendar.set(Calendar.MINUTE, 58);
        notifyHrs.add(calendar);

        calendar.set(Calendar.HOUR_OF_DAY, 03);
        calendar.set(Calendar.MINUTE, 58);
        notifyHrs.add(calendar);

        return notifyHrs;
    }

    private void CreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RoutineNotifyChannel";
            String description = "Channel for Routine Notification to pop up";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyRoutine", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private ArrayList<Calendar> getReportSendTimes() {

        ArrayList<Calendar> notifyHrs = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 00);
        notifyHrs.add(calendar);
        return notifyHrs;
    }

}
