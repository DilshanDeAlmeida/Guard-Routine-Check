package com.example.guardcheck;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class ViewDataActivity extends AppCompatActivity {

    EditText txtLocationName;
    Button btnSaveLocation;
    Spinner cmbLocations;
    Spinner cmbFacility;
    EditText txtTiming;
    Button btnSaveTime;
    CheckBox chkbxIndoor;

    ListView listViewLocations;
    AlertDialog.Builder builder;
    private ArrayList<String> locationsOrder;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        db = new DatabaseHelper(getApplicationContext());
        initialzeElements();

        // ----------------------------- Button Save Location

        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String location = String.valueOf(txtLocationName.getText());
                location = "Location" + location;
                Log.d("Test", location);
                int indoor = 0;
                if(chkbxIndoor.isChecked()){
                    indoor =1;
                }
                if (!location.equals("Location") && locationDuplicateCheck(location) == false) {

                    if (db.addNewLocation(location,indoor+"",cmbFacility.getSelectedItem().toString()) != -1) {
                        builder.setTitle("Done");
                        builder.setMessage("Successfully Inserted!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateLocationList();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error : Insert Failed !", Toast.LENGTH_LONG).show();
                    }
                } else {
                    builder.setTitle("Error");
                    builder.setMessage("Location already exists or Empty!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do Nothing
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });

        // ----------------------------- Button Save Time

        btnSaveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cmbLocations.getSelectedItem().equals("")) {
                    String location = String.valueOf(cmbLocations.getSelectedItem());
                    int waitTime = 0;
                    try {
                        waitTime = Integer.parseInt(String.valueOf(txtTiming.getText()));
                    } catch (NumberFormatException nfe) {
                        Toast.makeText(getApplicationContext(), "Error : Couldnt Parse !, " + nfe.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    Log.d("Test", location);
                    Log.d("Test", waitTime+"");
                    if(db.updateLocationWaitTime(location, waitTime) > -1)
                    {
                        builder.setTitle("Done");
                        builder.setMessage("Updated Successfully!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do Nothing
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error : Nothing was updated !", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error : Select a location first !", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void initialzeElements() {

        txtLocationName = findViewById(R.id.txtLocationName);
        btnSaveLocation = findViewById(R.id.btnSaveLocation);

        cmbLocations = findViewById(R.id.cmbLocations);
        cmbFacility = findViewById(R.id.cmbFacility);
        btnSaveTime = findViewById(R.id.btnSaveTime);
        txtTiming = findViewById(R.id.txtTiming);
        listViewLocations = findViewById(R.id.listViewLocations);
        chkbxIndoor= findViewById(R.id.checkBoxIndoor);

        locationsOrder = new ArrayList<>();
        builder = new AlertDialog.Builder(ViewDataActivity.this);
        updateLocationList();

        ArrayList<String> facilities = new ArrayList<>();
        facilities.add("Dankotuwa");
        facilities.add("Hemmathagama");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, facilities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbFacility.setAdapter(adapter);
    }

    void updateLocationList() {
        locationsOrder.clear();
        locationsOrder = db.getAllLocations();

        Collections.sort(locationsOrder, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationsOrder);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbLocations.setAdapter(adapter);

//        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locationsOrder);
//        listViewLocations.setAdapter(listAdapter);
    }

    boolean locationDuplicateCheck(String newLocation) {
        boolean result = false;
        for (String item : locationsOrder) {
            if (newLocation.equals(item)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
