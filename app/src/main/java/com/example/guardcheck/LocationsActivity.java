package com.example.guardcheck;

import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class LocationsActivity {

    private ArrayList<String> locationsOrder;
    private ArrayList<String> scannedOrder = new ArrayList<>(40);

    LocationsActivity(ArrayList<String> locations) {

        locationsOrder = new ArrayList<>();
        locationsOrder = locations;
    }


    public boolean verifyLocation(String curLocation) {

        String lastLocation = getLastLocation();
        int intendIndex = getIndexToLocation(curLocation);
        int lastIndex = getIndexLastLocation(lastLocation);

        Log.d("Test", "....................................................................");
        Log.d("Test", "Last Location :"+lastLocation);
        Log.d("Test", "Last Index :" + lastIndex + "");
        Log.d("Test", "Current Location :"+curLocation);
        Log.d("Test", "Intended Index :"+intendIndex + "");

        if ((lastLocation.equals("null") && curLocation.equals("Location01")) || (lastLocation.equals("null") && curLocation.equals("Location19"))) {
            scannedOrder.add(curLocation);
            return true;
        } else {
            if (intendIndex != -1 && lastIndex != -1) {
                if(intendIndex >= 18){
                    if (intendIndex - 18 == lastIndex + 1) {
                        scannedOrder.add(curLocation);
                        return true;
                    } else {
                        return false;
                    }
                }else{
                    if (intendIndex == lastIndex + 1) {
                        scannedOrder.add(curLocation);
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
    }

    public int getIndexToLocation(String curLocation) {
        int index = -1;
        for (String loopLocation : locationsOrder) {
            if (loopLocation.equals(curLocation)) {
                index = locationsOrder.indexOf(loopLocation);
                break;
            }
        }
        return index;
    }

    public int getIndexLastLocation(String lastLocation) {
        int index = -1;
        for (String loopLocation : scannedOrder) {
            if (loopLocation.equals(lastLocation)) {
                index = scannedOrder.indexOf(loopLocation);
                break;
            }
        }
        return index;
    }

    public String getLastLocation() {
        String lastLocation = "null";
        if (scannedOrder.size() > 0) {
            lastLocation = scannedOrder.get(scannedOrder.size() - 1);
        }
        Log.d("Test", "Size of scannedOrder :"+scannedOrder.size());
        return lastLocation;
    }
}
