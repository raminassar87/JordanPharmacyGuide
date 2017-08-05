package com.javawy.jordanpharmacyguide.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONObject;
/**
 * @auther Rami Nassar on 7/29/2017.
 * Pharmacy Guide SQL Lite Helper
 */
public class PharmacyGuideSQLLitehelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "JordanPharmacyGuide"; // the name of our database
    private static final int DB_VERSION = 1; // the version of the database

    private Context context;

    public PharmacyGuideSQLLitehelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public PharmacyGuideSQLLitehelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public PharmacyGuideSQLLitehelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, DB_NAME, null, DB_VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String stringSql = "CREATE TABLE PHARMACY (_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "                NAME TEXT,\n" +
                "                CITY TEXT,\n" +
                "                ADDRESS TEXT,\n" +
                "                CONTACT_NUMBER TEXT,\n" +
                "                EMAIL TEXT," +
                "                LATITUDE TEXT," +
                "                LONGITUDE TEXT)";
        db.execSQL(stringSql);

        // Add Record..
        addRecords(db);
    }

    /**
     * Add Records
     */
    private void addRecords(SQLiteDatabase db) {

        //String filePath="C:\\rami-data\\Rami\\Programming\\NetBeans Projects\\user.json";
        String filePath="pharmacies.json";
        InputStream is = null;
        try {
            //is = new FileInputStream(new File(filePath));
            AssetManager manager = context.getAssets();
            is = manager.open(filePath);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONObject myJSON = new org.json.JSONObject(json);
            JSONArray jsonArray = myJSON.getJSONArray("pharmacies");

            int arraysize = jsonArray.length();
            for (int i = 0; i < arraysize; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                addRecord(db,jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Add Record
     *
     * @param db : SQLiteDatabase
     * @param jsonObject : JSON Object
     */
    private void addRecord(SQLiteDatabase db,JSONObject jsonObject) {
        ContentValues drinkValues = new ContentValues();
        try {
            drinkValues.put("NAME", jsonObject.getString("pharmacyName"));
            drinkValues.put("CITY", jsonObject.getString("city"));
            drinkValues.put("ADDRESS", jsonObject.getString("location"));
            drinkValues.put("CONTACT_NUMBER", jsonObject.getString("contactNumber"));
            drinkValues.put("EMAIL", jsonObject.getString("email"));
            drinkValues.put("LATITUDE", "");
            drinkValues.put("LONGITUDE", "");

            db.insert("PHARMACY",null,drinkValues);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do Nothing..
    }
}