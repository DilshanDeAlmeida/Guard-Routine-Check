package com.example.guardcheck;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

public class SMSmessenger extends BroadcastReceiver {

    void sendSMS(String message, String number) {
        String messageToSend = message;
        String sendNumber = number;
        SmsManager.getDefault().sendTextMessage(sendNumber, null, messageToSend, null, null);
    }

    void sendSMS(String message, ArrayList<String> numbers) {
        String messageToSend = message;
        String sendNumber = "";
        for (String number : numbers) {
            sendNumber = number;
            SmsManager.getDefault().sendTextMessage(sendNumber, null, messageToSend, null, null);
        }
    }

    void NotificationPop(Context context, String title, String message) {
        try {
            //Get an instance of NotificationManager//
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.noticopy).setContentTitle(title).setContentText(message);

            // Gets an instance of the NotificationManager service//
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            synchronized (mNotificationManager) {
                mNotificationManager.notify();
                mNotificationManager.notify(001, mBuilder.build());
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(context, notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Create the content intent for the notification, which launches this activity
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.noticopy)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        //Deliver the notification
        notificationManager.notify(0, builder.build());
    }
}
