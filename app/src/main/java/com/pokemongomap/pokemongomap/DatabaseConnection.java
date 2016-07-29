package com.pokemongomap.pokemongomap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.ExecutionException;

public final class DatabaseConnection {

    private static DatabaseConnection mConnection = new DatabaseConnection();

    private static Context mContext;

    private SQLiteDatabase mDatabase;

    public static void init(Context context) {
        mContext = context;
        SQLiteDB mDbHelper = new SQLiteDB(context);
        mConnection.mDatabase = mDbHelper.getReadableDatabase();
    }

    public static DatabaseConnection getInstance() {
        return mConnection;
    }

    public void saveLocation(Location location) {
        synchronized (mConnection) {
            ContentValues values = new ContentValues();
            values.put(LocationContract.LocationEntry.COLUMN_NAME, location.getLatitude() + " " + location.getLongitude());

            mDatabase.insert(LocationContract.LocationEntry.TABLE_NAME, null, values);
            mConnection.notifyAll();
        }
    }

    public LatLng getLocation() {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LocationContract.LocationEntry.COLUMN_NAME
        };

        Cursor c = mDatabase.query(LocationContract.LocationEntry.TABLE_NAME, projection, null, null, null, null, null);

        c.moveToFirst();
        String loc[] = c.getString(c.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_NAME)).split(" ");
        return new LatLng(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));
    }

    public String getLocationAsString() {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LocationContract.LocationEntry.COLUMN_NAME
        };

        String[] loc;
        Cursor c = mDatabase.query(LocationContract.LocationEntry.TABLE_NAME, projection, null, null, null, null, null);

        c.moveToFirst();
        loc = c.getString(c.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_NAME)).split(" ");
        return Double.parseDouble(loc[0]) + "_" + Double.parseDouble(loc[1]);
    }
}
