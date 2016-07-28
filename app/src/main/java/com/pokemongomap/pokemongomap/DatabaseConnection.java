package com.pokemongomap.pokemongomap;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

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

    public LatLng getLocationAsync() {
        LocationAsyncTask mLocationTask = new LocationAsyncTask();
        try {
            return mLocationTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new LatLng(49.632180, 8.360284);
    }

    public class LocationAsyncTask extends AsyncTask<Void, Void, LatLng> {

        @Override
        protected LatLng doInBackground(Void... voids) {
            SQLiteDB mDbHelper = new SQLiteDB(mContext);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    LocationContract.LocationEntry.COLUMN_NAME
            };

            Cursor c = db.query(LocationContract.LocationEntry.TABLE_NAME, projection, null, null, null, null, null);

            while (true) {
                try {
                    c.moveToFirst();
                    String loc[] = c.getString(c.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_NAME)).split(" ");
                    return new LatLng(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));
                } catch (CursorIndexOutOfBoundsException e) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
