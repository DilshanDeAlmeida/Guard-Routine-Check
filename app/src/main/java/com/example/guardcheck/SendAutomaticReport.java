package com.example.guardcheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SendAutomaticReport extends BroadcastReceiver  {

    private ArrayList<History> historyList;
    private Context context;
    DatabaseHelper db;
    String fromDate = "";
    String toDate = "";
    String tripStartTimes = "";

    String txtCompTripCount;
    String txtIncompTripCount;
    String txtTotTripCount;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        db = new DatabaseHelper(context);
        SetDates();
        if(CheckIfShouldSendEmail(toDate)==true){
            GetDayHistoryRecords();
            SendEmail();
        }else{
            Log.d("Test", "No need to send email...");
        }
    }

    private void SetDates() {

        Date c = Calendar.getInstance().getTime();
        Log.d("Test", "Current time : " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedFromDate = df.format(c);

        fromDate = getPreviousDate(formattedFromDate);
        toDate = formattedFromDate;
        Log.d("Test", "View log from " + fromDate + " to " + toDate);
    }

    private String getPreviousDate(String inputDate){

        inputDate = inputDate;
        SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(inputDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            c.add(Calendar.DATE, -1);
            inputDate = format.format(c.getTime());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            inputDate ="";
        }
        return inputDate;
    }

    private void GetDayHistoryRecords() {

        Log.d("Test", "Getting guard history data from Database...");
        historyList = db.getGuardHistory(fromDate, toDate);

        CalculateCompletedTrips(historyList);
        tripStartTimes = GetTripStartTimes(historyList);
    }

    private String GetTripStartTimes(ArrayList<History> historyList) {

        if (historyList.isEmpty() == false) {
            String curLocation = "null";

            for (int i = 0; i < historyList.size(); i++) {
                curLocation = historyList.get(i).getLocation();
                if (curLocation.equals("Location01")) {
                    tripStartTimes += "\n" + historyList.get(i).getDate();
                }
            }
        }
        Log.d("Test", tripStartTimes);
        return tripStartTimes;
    }

    private void CalculateCompletedTrips(ArrayList<History> listAllTrips) {

        if (listAllTrips.isEmpty() == false) {
            int trips_started = 0;
            int trips_completed = 0;
            String curLocation = "null";

            for (int i = 0; i < listAllTrips.size(); i++) {
                curLocation = listAllTrips.get(i).getLocation();
                if (curLocation.equals("Location01")) {
                    Log.d("Test", listAllTrips.get(i).getLocation());
                    trips_started++;
                } else if (curLocation.equals("Location38")) {
                    Log.d("Test", listAllTrips.get(i).getLocation());
                    trips_completed++;
                }
            }
            txtCompTripCount=trips_completed + "";
            txtIncompTripCount=(trips_started - trips_completed) + "";
            txtTotTripCount=trips_started + "";
        } else {
            txtCompTripCount="000";
            txtIncompTripCount="000";
            txtTotTripCount="000";
        }
    }

    private void SendEmail() {

        if (historyList.isEmpty() == false) {
            if(CheckIfShouldSendEmail(toDate)==true){
                try {

                    ArrayList<String> emailList = new ArrayList<String>();
                    emailList.add("charith@3slk.com");
                    emailList.add("dilshan@3slk.com");

                    for(int i=0;i<emailList.size();i++) {

                        String emailTo = emailList.get(i);
                        String emailSubject = "3S Guard Check App Report " + fromDate + " to " + toDate;
                        String emailMessage = "\n" + "Report " + fromDate + " to " + toDate + "\n" + "\n" + "No of completed trips : " + txtCompTripCount + '\n' + "No of incomplete trips : " + txtIncompTripCount + '\n' + "Total no of trips : " + txtTotTripCount + '\n' + '\n' + tripStartTimes;

                        JavaMailAPI javamailAPI = new JavaMailAPI(context, emailTo, emailSubject, emailMessage);
                        Log.d("Test", "\n" + emailTo + "\n" + emailSubject + "\n" + emailMessage);
                        javamailAPI.execute();

                    }
                    Log.d("Test", "Email sent...");
                    if(db.addNewEmailSentRecord(toDate,1) == -1){
                        Log.d("Test", "Failed to update Email Sent Log");
                        Toast.makeText(context, "Failed to update Email Sent Log", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Log.d("Test", e.getMessage());
                }
            }else{
                Log.d("Test", "No need to send email...");
            }
        } else {
            Toast.makeText(context, "No history to email", Toast.LENGTH_LONG).show();
        }
    }

    private boolean CheckIfShouldSendEmail(String toDate) {
        boolean result = db.getEmailSentStatus(toDate);
        Log.d("Test", "getEmailSentStatus :"+result);
        return result;
    }
}
