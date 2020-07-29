package com.example.guardcheck;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class RemarksActivity extends AppCompatActivity {

    ImageButton btnBack;
    ImageButton btnDone;
    EditText txtRemarks;
    Spinner cmbRemarks;
    TextView resultDis;
    TextView txtcountdown;

    AlertDialog.Builder builder;
    ArrayList<String> remaksList;
    DatabaseHelper db;
    String guardName;
    String scanResult;
    ArrayList<String> putstrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remarks);

        db = new DatabaseHelper(getApplicationContext());
        txtcountdown = findViewById(R.id.txtCountDown);

        remaksList = new ArrayList<>();
        putstrings = new ArrayList<>();
        putstrings = getIntent().getStringArrayListExtra("results");

        guardName = putstrings.get(0);
        scanResult = putstrings.get(1);

        String wt= db.getWaitTime(scanResult);
        Log.d("Test", "wait time db :"+wt);
        int waitTime = Integer.parseInt(wt);

        new CountDownTimer(waitTime*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                txtcountdown.setText(millisUntilFinished / 1000 + "");
            }

            public void onFinish() {
                txtcountdown.setTextColor(Color.BLUE);
                txtcountdown.setText("OK");
            }

        }.start();

        builder = new AlertDialog.Builder(RemarksActivity.this);

        resultDis = findViewById(R.id.textView15);
        txtRemarks = findViewById(R.id.txtRemarks);
        cmbRemarks = findViewById(R.id.cmbRemarks);
        btnBack = findViewById(R.id.btnBack);
        btnDone = findViewById(R.id.btnDone);
        updateLocationList();
        resultDis.setText(scanResult);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cmbRemarks.getSelectedItemId() > -1 && !txtRemarks.getText().equals("")) {

                    if (db.addNewRecordToHistory(scanResult, guardName, txtRemarks.getText().toString()) != -1) {
                        if (txtRemarks.getText().toString().equals("All Good!")) {

                            if (scanResult.equals("Location18") || scanResult.equals("Location38")) {
                                builder.setTitle("Trip Complete");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                builder.setMessage("Routine Check Complete !" + "\n" + "Please return to the Home screen.");
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                builder.setTitle("GOOD");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                builder.setMessage("Move to Next Location");
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } else {
                            SMSmessenger sms = new SMSmessenger();
                            sms.sendSMS(txtRemarks.getText()+" \n "+"3S Guard-DK", getEmergencyNumbers());
                            Toast.makeText(getApplicationContext(), "Notified", Toast.LENGTH_LONG).show();

                            if (scanResult.equals("Location18") || scanResult.equals("Location38")) {
                                builder.setTitle("Trip Complete");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                builder.setMessage("Routine Check Complete !" + "\n" + "Please return to the Home screen.");
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                finish();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error : Failed to add to Database !", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error : Remark cant be empty", Toast.LENGTH_LONG).show();
                }
            }
        });

        cmbRemarks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (remaksList.size() > 0) {
                    txtRemarks.setText(cmbRemarks.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {

            }
        });
    }

    private void endRoutine(String scanResult, String guardName) {

        if (scanResult.equals("Location38")) {
            builder.setTitle("Trip Complete");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setMessage("Routine Check Complete !" + "\n" + "Please return to the Home screen.");
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    void updateLocationList() {
        remaksList.clear();
        //remaksList = db.getAllRemarks();
        remaksList.add("All Good!");
        remaksList.add("Other");

        if (remaksList.size() > 0) {
            Collections.sort(remaksList, String.CASE_INSENSITIVE_ORDER);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, remaksList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cmbRemarks.setAdapter(adapter);
        }
    }

    ArrayList<String> getEmergencyNumbers() {
        ArrayList<String> emergencyNumbers = new ArrayList<>();
        emergencyNumbers.add("0771500224"); // Ranga
        emergencyNumbers.add("0772915832"); // Sithum
        emergencyNumbers.add("0777497571"); // Thusitha
        emergencyNumbers.add("0777293951"); // Sujith
        emergencyNumbers.add("0777565125"); // Charith
        // emergencyNumbers.add("0777398779"); // Gamini
        return emergencyNumbers;
    }
}
