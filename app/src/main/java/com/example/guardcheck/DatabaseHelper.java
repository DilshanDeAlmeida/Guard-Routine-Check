package com.example.guardcheck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "tsguard";

    // Table Names
    private static final String TABLE_LOCATIONS = "locations";
    private static final String TABLE_CHECK_HISTORY = "checkhistory";
    private static final String TABLE_EMAILS_SENT = "emailssent";

    // Common column names
    private static final String KEY_ID = "sysID";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_LOCNAME = "location_name";

    // locations Table - column nmaes
    private static final String KEY_LOCTIME = "waittime";
    private static final String KEY_INDOOR = "indoor";
    private static final String KEY_FACILITY = "facility";

    // checkhistory Table - column names
    private static final String KEY_GUARD_NAME = "guard_name";
    private static final String KEY_REMARKS = "remarks";

    // checkhistory Table - column names
    private static final String KEY_SENT_STATUS = "sent_status";

    // Table Create Statements
    // Todo table locations create statement
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LOCNAME + " TEXT," + KEY_LOCTIME + " INTEGER," + KEY_CREATED_AT + " DATETIME," + KEY_INDOOR + " TEXT," + KEY_FACILITY + " TEXT" + ")";

    // Todo table Check History create statement
    private static final String CREATE_TABLE_CHECK_HISTORY = "CREATE TABLE " + TABLE_CHECK_HISTORY + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LOCNAME + " TEXT," + KEY_GUARD_NAME + " TEXT," + KEY_REMARKS + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

    // Todo table Emails Sent create statement
    private static final String CREATE_TABLE_EMAILS_SENT = "CREATE TABLE " + TABLE_EMAILS_SENT + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CREATED_AT + " DATETIME," + KEY_SENT_STATUS + " INTEGER" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("Test", "Database Helper class OnCreate Method START ...");
        // creating required tables
        db.execSQL(CREATE_TABLE_LOCATIONS);
        db.execSQL(CREATE_TABLE_CHECK_HISTORY);
        db.execSQL(CREATE_TABLE_EMAILS_SENT);
        Log.d("Test", "Database Helper class OnCreate Method END ...");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECK_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMAILS_SENT);

        // create new tables
        onCreate(db);
    }

    /*
     * Adding a Location
     */
    public long addNewLocation(String newLocation, String indoor, String facility) {

        Date currentTime = Calendar.getInstance().getTime();
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCNAME, newLocation);
        values.put(KEY_LOCTIME, 0);
        values.put(KEY_CREATED_AT, DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(currentTime));
        values.put(KEY_INDOOR, indoor);
        values.put(KEY_FACILITY, facility);

        // insert row
        long location_id = db.insert(TABLE_LOCATIONS, null, values);
        return location_id;
    }

    /*
     * Adding a Record to History
     */
    public long addNewRecordToHistory(String curLocation, String guardName, String remarks) {

        Date currentTime = Calendar.getInstance().getTime();
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCNAME, curLocation);
        values.put(KEY_GUARD_NAME, guardName);
        values.put(KEY_REMARKS, remarks);
        values.put(KEY_CREATED_AT, getDateTime());
        //values.put(KEY_CREATED_AT, DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(currentTime));

        // insert row
        long history_id = db.insert(TABLE_CHECK_HISTORY, null, values);
        return history_id;
    }

    /*
     * Adding a Email Sent Record
     */
    public long addNewEmailSentRecord(String curDate, int status) {

        Date currentTime = Calendar.getInstance().getTime();
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CREATED_AT, getDateTime());
        values.put(KEY_SENT_STATUS, status);

        // insert row
        long location_id = db.insert(TABLE_EMAILS_SENT, null, values);
        return location_id;
    }

    /*
     * getting Email Sent Status
     * */
    public boolean getEmailSentStatus(String dateTimeTo) {
        ArrayList<String> tblEmailsSent = new ArrayList<String>();
        String selectQuery = "SELECT * FROM " + TABLE_EMAILS_SENT + " WHERE " + KEY_CREATED_AT + " BETWEEN " + "'" + dateTimeTo + " 00:00:00" + "'" + " AND " + "'" + dateTimeTo + " 23:59:00" + "' ORDER BY " + KEY_ID + " ASC;";
        Log.e(LOG, selectQuery);
        Log.d("Test", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            Cursor c = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (c.moveToFirst()) {
                do {
                    // adding to todo list
                    tblEmailsSent.add(c.getString(c.getColumnIndex(KEY_SENT_STATUS)));
                } while (c.moveToNext());
            }
            if (tblEmailsSent.size() > 0) {
                if (tblEmailsSent.get(0).equals("1")) {
                    return false;
                } else {
                    return true;
                }
            }
            // true means YES YOU MUST SEND AN EMAIL TO NOTIFY...
            return true;

        } catch (Exception e) {
            Log.d("Test", e.getMessage());
            db.execSQL(CREATE_TABLE_EMAILS_SENT);
            Log.d("Test", "CREATE_TABLE_EMAILS_SENT DONE...");
        }
        return false;
    }

    /*
     * getting all Locations
     * */
    public ArrayList<String> getAllLocations() {
        ArrayList<String> locations = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS;
        //String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS + " WHERE " + KEY_LOCNAME + " = " + locationName;
        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                // adding to todo list
                if (!c.getString(c.getColumnIndex(KEY_LOCNAME)).equals("")) {
                    locations.add(c.getString(c.getColumnIndex(KEY_LOCNAME)));
                }
            } while (c.moveToNext());
        }
        return locations;
    }

    /*
     * Updating a Location Time
     */
    public int updateLocationWaitTime(String locationName, int wait_minutes) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCTIME, wait_minutes);

        // updating row
        return db.update(TABLE_LOCATIONS, values, KEY_LOCNAME + " = ?", new String[]{locationName});
    }

    public ArrayList<String> getAllRemarks() {
        ArrayList<String> remarks = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_CHECK_HISTORY;
        //String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS + " WHERE " + KEY_LOCNAME + " = " + locationName;
        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                // adding to todo list
                if (!c.getString(c.getColumnIndex(KEY_REMARKS)).equals("")) {
                    remarks.add(c.getString(c.getColumnIndex(KEY_REMARKS)));
                }
            } while (c.moveToNext());
        }
        return remarks;
    }

    public ArrayList<History> getGuardHistory() {

        String concat = "null";
        ArrayList<History> history = new ArrayList<History>();
        String Date;
        String Location;
        String Remark;
        String Guard;

        String selectQuery = "SELECT  * FROM " + TABLE_CHECK_HISTORY + " ORDER BY " + KEY_ID + " DESC";
        Log.e(LOG, selectQuery);
        History hobj;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                // adding to todo list
                if (!c.getString(c.getColumnIndex(KEY_REMARKS)).equals("")) {

                    Date = c.getString(c.getColumnIndex(KEY_CREATED_AT));
                    Location = c.getString(c.getColumnIndex(KEY_LOCNAME));
                    Remark = c.getString(c.getColumnIndex(KEY_REMARKS));
                    Guard = c.getString(c.getColumnIndex(KEY_GUARD_NAME));
                    hobj = new History(Date, Location, Remark, Guard);
                    history.add(hobj);

                } else {

                    Date = c.getString(c.getColumnIndex(KEY_CREATED_AT));
                    Location = c.getString(c.getColumnIndex(KEY_LOCNAME));
                    Remark = "No Remark";
                    Guard = c.getString(c.getColumnIndex(KEY_GUARD_NAME));
                    hobj = new History(Date, Location, Remark, Guard);
                    history.add(hobj);

                }
            } while (c.moveToNext());
        }
        return history;
    }

    public ArrayList<History> getGuardHistory(String FromDate, String ToDate) {

        String concat = "null";
        ArrayList<History> history = new ArrayList<History>();
        String Date;
        String Location;
        String Remark;
        String Guard;

        String selectQuery =
                "SELECT * FROM " + TABLE_CHECK_HISTORY + " WHERE " + KEY_CREATED_AT + " BETWEEN " + "'"
                + FromDate + " 17:00:00" + "'" + " AND " + "'" + ToDate + " 17:00:00" + "' ORDER BY " + KEY_ID + " ASC;";

//                "SELECT * FROM " + TABLE_CHECK_HISTORY + " WHERE DATE(" + KEY_CREATED_AT + ") >= " + "'"
//                + FromDate + " 17:00:00" + "'" + " AND DATE(" + KEY_CREATED_AT + ") <= " + "'" + ToDate + " 17:00:00" + "' ORDER BY " + KEY_ID + " ASC;";

//                "SELECT * FROM " + TABLE_CHECK_HISTORY + " WHERE DATE(" + KEY_CREATED_AT + ") <= " + "'"
//                + FromDate + "'" + " AND DATE(" + KEY_CREATED_AT + ") >= " + "'" + ToDate + "' ORDER BY " + KEY_ID + " ASC;";

                Log.e(LOG, selectQuery);
        History hobj;

        Log.d("Test", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                // adding to todo list
                if (!c.getString(c.getColumnIndex(KEY_REMARKS)).equals("")) {

                    Date = c.getString(c.getColumnIndex(KEY_CREATED_AT));
                    Location = c.getString(c.getColumnIndex(KEY_LOCNAME));
                    Remark = c.getString(c.getColumnIndex(KEY_REMARKS));
                    Guard = c.getString(c.getColumnIndex(KEY_GUARD_NAME));
                    hobj = new History(Date, Location, Remark, Guard);
                    history.add(hobj);

                } else {

                    Date = c.getString(c.getColumnIndex(KEY_CREATED_AT));
                    Location = c.getString(c.getColumnIndex(KEY_LOCNAME));
                    Remark = "No Remark";
                    Guard = c.getString(c.getColumnIndex(KEY_GUARD_NAME));
                    hobj = new History(Date, Location, Remark, Guard);
                    history.add(hobj);

                }
            } while (c.moveToNext());
        }
        return history;
    }

    public String getWaitTime(String location) {

        String result = "15";
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS;
        //String selectQuery = "SELECT "+KEY_LOCTIME+" FROM " + TABLE_LOCATIONS + " WHERE " + KEY_LOCNAME + " = " + location;
        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                // adding to todo list
                if (c.getString(c.getColumnIndex(KEY_LOCNAME)).equals(location)) {
                    if (!c.getString(c.getColumnIndex(KEY_LOCTIME)).equals("0")) {
                        result = c.getString(c.getColumnIndex(KEY_LOCTIME));
                    }
                }
            } while (c.moveToNext());
        }
        return result;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void ClearHistoryFromPhoneDatabase() {
        String deleteQuery = "DROP TABLE IF EXISTS " + TABLE_CHECK_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(deleteQuery);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) db.close();
    }
}