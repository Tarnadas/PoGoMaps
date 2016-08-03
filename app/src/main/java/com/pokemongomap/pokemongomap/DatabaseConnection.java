package com.pokemongomap.pokemongomap;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

public final class DatabaseConnection extends Service {

    private static DatabaseConnection mConnection = new DatabaseConnection();

    private SQLiteDatabase mDatabase;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public static void init(Context context) {
        SQLiteDB mDbHelper = new SQLiteDB(context);
        mConnection.mDatabase = mDbHelper.getReadableDatabase();
        mConnection.mDatabase.delete(LocationContract.LocationEntry.LOCATION_TABLE_NAME, null, null);
    }

    public static DatabaseConnection getInstance() {
        return mConnection;
    }

    public void saveLocation(Location location) {
        synchronized (mConnection) {
            ContentValues values = new ContentValues();
            values.put(LocationContract.LocationEntry.LOCATION_COLUMN_NAME, location.getLatitude() + " " + location.getLongitude());

            mDatabase.delete(LocationContract.LocationEntry.LOCATION_TABLE_NAME, null, null);
            mDatabase.insert(LocationContract.LocationEntry.LOCATION_TABLE_NAME, null, values);
            mConnection.notifyAll();
        }
    }

    public LatLng getLocation() throws CursorIndexOutOfBoundsException {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LocationContract.LocationEntry.LOCATION_COLUMN_NAME
        };

        Cursor c = mDatabase.query(LocationContract.LocationEntry.LOCATION_TABLE_NAME, projection, null, null, null, null, null);

        c.moveToFirst();
        String loc[] = c.getString(c.getColumnIndexOrThrow(LocationContract.LocationEntry.LOCATION_COLUMN_NAME)).split(" ");
        return new LatLng(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));
    }

    public String getLocationAsString() throws CursorIndexOutOfBoundsException {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LocationContract.LocationEntry.LOCATION_COLUMN_NAME
        };

        String[] loc;
        Cursor c = mDatabase.query(LocationContract.LocationEntry.LOCATION_TABLE_NAME, projection, null, null, null, null, null);

        c.moveToFirst();
        loc = c.getString(c.getColumnIndexOrThrow(LocationContract.LocationEntry.LOCATION_COLUMN_NAME)).split(" ");
        return Double.parseDouble(loc[0]) + "_" + Double.parseDouble(loc[1]);
    }
}
