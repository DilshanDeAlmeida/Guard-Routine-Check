package com.example.guardcheck;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class RSSPullService extends Service {

    private NotificationManager mNotificationManager;

    RSSPullService()
    {

    }

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

        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 50);
        notifyHrs.add(calendar);
        return notifyHrs;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CreateNotificationChannel();
        SetNotificationTrigger();
        SetReportSendTrigger();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
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
}
