package com.pokemongomap.pokemongomap;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public abstract class RemoteDatabaseConnection {

    private static String TAG = "ALTER";

    private static String WS_URI = "ws://tarnadas.ddns.net:5000/";

    private static final WebSocketConnection mConnection = new WebSocketConnection();

    private static final Object mLock = new Object();
    private static final Object mLockConnection = new Object();

    private static String[] mAccounts;

    private static boolean mConnectionEstablished = false;

    public static void init(Context context) {

        try {
            mConnection.connect(WS_URI, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + WS_URI);
                    synchronized (mLockConnection) {
                        mConnectionEstablished = true;
                        mLockConnection.notifyAll();
                    }
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d(TAG, "Got message: " + payload);
                    if (payload.contains("accounts")) {
                        accountsReceived(payload.split(" ")[1]);
                    }
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost.");
                }
            });
        } catch (WebSocketException e) {
            Log.d(TAG, e.toString());
            Toast.makeText(context, "Server unreachable. Please try again later", Toast.LENGTH_LONG).show();
        }

    }

    public static String[] getAccounts(int amount) {
        synchronized(mLock) {
            if (!mConnectionEstablished) {
                synchronized (mLockConnection) {
                    try {
                        mLockConnection.wait();
                    } catch (InterruptedException e) {}
                }
            }
            mConnection.sendTextMessage("getaccounts " + amount);
            try {
                mLock.wait();
            } catch (InterruptedException e) {}
        }
        return mAccounts;
    }

    private static void accountsReceived(String payload) {
        synchronized (mLock) {
            mAccounts = payload.split(";;");
            mLock.notifyAll();
        }
    }

    public static void updateAccounts(List<String> accounts) {
        String a = accounts.get(0).split(":")[0];
        for (int i = 1; i < accounts.size(); i++) {
            a += ";;" + accounts.get(i).split(":")[0];
        }
        mConnection.sendTextMessage("updateaccount " + a);
    }

    public static void setBroken(List<String> accounts) {
        String a = accounts.get(0).split(":")[0];
        for (int i = 1; i < accounts.size(); i++) {
            a += ";;" + accounts.get(i).split(":")[0];
        }
        mConnection.sendTextMessage("accountbroken " + a);
    }

}