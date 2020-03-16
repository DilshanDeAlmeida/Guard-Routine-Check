package com.example.guardcheck;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private LocationsActivity locationActivity;
    String guardName;
    DatabaseHelper db;
    ArrayList<String> putstrings;
    private ArrayList<String> locationsOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        db = new DatabaseHelper(getApplicationContext());
        locationsOrder = db.getAllLocations();
        guardName = getIntent().getStringExtra("guardName");
        locationActivity = new LocationsActivity(locationsOrder);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(ScannerActivity.this, "Permission is granted!", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        } else {
            Toast.makeText(ScannerActivity.this, "Build v:" + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            Toast.makeText(ScannerActivity.this, "Build vc:" + Build.VERSION_CODES.M, Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(ScannerActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permission[], int grantResults[]) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Toast.makeText(ScannerActivity.this, "Permission Granted!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ScannerActivity.this, "Permission Denied!", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                            }
                            displayAlertMessage("You need to allow access for both permissions", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                }
                            });
                            return;
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(ScannerActivity.this).setMessage(message).setPositiveButton("OK", listener).setNegativeButton("Cancel", null).create().show();
    }

    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();

        if (locationActivity.verifyLocation(scanResult)) {

            putstrings = new ArrayList<>();
            putstrings.add(guardName);
            putstrings.add(scanResult);
            startActivity(new Intent(ScannerActivity.this, RemarksActivity.class).putExtra("results", putstrings));

        } else {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Wrong");
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scannerView.resumeCameraPreview(ScannerActivity.this);
                }
            });
            builder.setMessage("Wrong Location : " + scanResult);
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
