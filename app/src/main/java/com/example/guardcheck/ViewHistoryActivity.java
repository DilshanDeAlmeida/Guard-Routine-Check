package com.example.guardcheck;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewHistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ListView listViewHistory;
    private ArrayList<History> historyList;
    DatabaseHelper db;
    String fromDate = "";
    String toDate = "";
    String tripStartTimes = "";

    Button btnViewDayHistory;
    Button btnEmailLog;
    TextView btnDatePicker;
    TextView txtCompTripCount;
    TextView txtIncompTripCount;
    TextView txtTotTripCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        btnViewDayHistory = findViewById(R.id.btnViewDayHistory);
        btnEmailLog = findViewById(R.id.btnEmailLog);
        btnDatePicker = findViewById(R.id.txtvDatepicker);
        txtCompTripCount = findViewById(R.id.txtvCompTripCount);
        txtIncompTripCount = findViewById(R.id.txtvIncompTripCount);
        txtTotTripCount = findViewById(R.id.txtvTotTripCount);

        listViewHistory = findViewById(R.id.listViewHistory);
        db = new DatabaseHelper(getApplicationContext());

//        historyList = db.getGuardHistory();
//        HistoryListAdapter adapter = new HistoryListAdapter(this, R.layout.adapter_view_layout, historyList);
//        listViewHistory.setAdapter(adapter);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnViewDayHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Test", "Getting guard history data from Database...");
                historyList = db.getGuardHistory(fromDate, toDate);

                listViewHistory.setAdapter(null);
                HistoryListAdapter adapter = new HistoryListAdapter(getApplicationContext(), R.layout.adapter_view_layout, historyList);
                listViewHistory.setAdapter(adapter);
                CalculateCompletedTrips(historyList);
                tripStartTimes = GetTripStartTimes(historyList);
            }
        });

        btnEmailLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (historyList.isEmpty() == false) {
                    try {
                        String emailTo = "charith@3slk.com";
                        String emailSubject = "3S Guard Check App Report " + fromDate + " to " + toDate;
                        String emailMessage = "\n" + "Report " + fromDate + " to " + toDate + "\n" + "\n" + "No of completed trips : " + txtCompTripCount.getText() + '\n' + "No of incomplete trips : " + txtIncompTripCount.getText() + '\n' + "Total no of trips : " + txtTotTripCount.getText() + '\n' + '\n' + tripStartTimes;

                        JavaMailAPI javamailAPI = new JavaMailAPI(ViewHistoryActivity.this, emailTo, emailSubject, emailMessage);
                        Log.d("Test", "\n" + emailTo + "\n" + emailSubject + "\n" + emailMessage);
                        javamailAPI.execute();
                        Log.d("Test", "Email sent...");
                    } catch (Exception e) {
                        Log.d("Test", "View History Activity: " + e.getMessage());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No history to email", Toast.LENGTH_LONG).show();
                }
            }
        });
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

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        fromDate = year + "-" + "0" + (month + 1) + "-" + (dayOfMonth - 1);
        toDate = year + "-" + "0" + (month + 1) + "-" + dayOfMonth;
        btnDatePicker.setText(fromDate + " to " + toDate);
        Log.d("Test", "View log from " + fromDate + " to " + toDate);
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
            txtCompTripCount.setText(trips_completed + "");
            txtIncompTripCount.setText((trips_started - trips_completed) + "");
            txtTotTripCount.setText(trips_started + "");
        } else {
            txtCompTripCount.setText("000");
            txtIncompTripCount.setText("000");
            txtTotTripCount.setText("000");
        }
    }
}
