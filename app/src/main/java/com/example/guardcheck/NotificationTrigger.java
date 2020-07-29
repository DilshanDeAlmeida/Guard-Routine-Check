package com.example.guardcheck;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationTrigger {

    private Context context;
    private NotificationManager mNotificationManager;

    NotificationTrigger(Context context) {
        this.context = context;
    }

    public void CreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RoutineNotifyChannel";
            String description = "Channel for Routine Notification to pop up";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyRoutine", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void SetNotificationTrigger() {
        try {

            CancelPendingIntents("MessagePop");
            Log.d("Test", " SetNotificationTrigger Pending Intent Set..");
            mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            ArrayList<Calendar> notifyHrs = getMessagePopUpTimes();
            //ArrayList<Calendar> notifyHrs = getMessagePopUpTimes();

            for (Calendar calendar : notifyHrs) {

                //Set up the Notification Broadcast Intent
                Intent notifyIntent = new Intent(context, SMSmessenger.class);
                //Set up the PendingIntent for the AlarmManager
                PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(context, 0, notifyIntent, 0);

                long triggerTime = calendar.getTimeInMillis() - System.currentTimeMillis();
                Log.d("Test", triggerTime+"");
                long repeatInterval = 10 * 1000; // 10 seconds

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                //alarmManager.set(AlarmManager.RTC_WAKEUP,triggerTime,notifyPendingIntent);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, alarmManager.INTERVAL_DAY, notifyPendingIntent);
            }
        } catch (Exception exception) {
            Toast.makeText(context, "Error :" + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void SetReportSendTrigger() {
        try {

            CancelPendingIntents("Report");
            Log.d("Test", " SetReportSendTrigger Pending Intent Set..");
            mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            ArrayList<Calendar> notifyHrs = getReportSendTimes();

            for (Calendar calendar : notifyHrs) {

                //Set up the Notification Broadcast Intent
                Intent notifyIntent = new Intent(context, SendAutomaticReport.class);
                //Set up the PendingIntent for the AlarmManager
                PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(context, 0, notifyIntent, 0);

                long triggerTime = calendar.getTimeInMillis() - System.currentTimeMillis();
                long repeatInterval = 10 * 1000; // 10 seconds
                long repeatIntervalDay = 24 * 60 * 60 * 1000;

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, alarmManager.INTERVAL_DAY, notifyPendingIntent);

            }
        } catch (Exception exception) {
            Toast.makeText(context, "Error :" + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<Calendar> getMessagePopUpTimes() {
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

    private ArrayList<Integer> getMessagePopUpTimesInMillies() {

        ArrayList<Integer> notifyHrs = new ArrayList<>();

        int x = 17 * 50 * 60 * 1000;
        notifyHrs.add(x);

        // 21: 58
        int ninefiftyeight = 21 * 58 * 60 * 1000;
        notifyHrs.add(ninefiftyeight);

        // 23: 58
        int twentythreefiftyeight = 23 * 58 * 60 * 1000;
        notifyHrs.add(twentythreefiftyeight);

        // 01: 58
        int onefiftyeight = 01 * 58 * 60 * 1000;
        notifyHrs.add(onefiftyeight);

        // 03: 58
        int threefiftyeight = 03 * 58 * 60 * 1000;
        notifyHrs.add(threefiftyeight);

        return notifyHrs;
    }

    private ArrayList<Calendar> getReportSendTimes() {

        ArrayList<Calendar> notifyHrs = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 00);
        notifyHrs.add(calendar);
        return notifyHrs;
    }

    private void CancelPendingIntents(String type) {

        try {

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            if (type.equals("MessagePop")) {

                Intent notifyIntent = new Intent(context, SMSmessenger.class);
                PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(context, 0, notifyIntent, 0);
                alarmManager.cancel(notifyPendingIntent);
                Log.d("Test", "Previously set alarms manager canceled...");

            } else {

                Intent notifyIntentt = new Intent(context, SendAutomaticReport.class);
                PendingIntent notifyPendingIntentt = PendingIntent.getBroadcast(context, 0, notifyIntentt, 0);
                alarmManager.cancel(notifyPendingIntentt);
                Log.d("Test", "Previously set alarms manager canceled...");

            }
        } catch (Exception e) {
            Log.d("Test", "Alarm Manager Cancel Fail");
        }

    }

}
