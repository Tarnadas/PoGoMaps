package com.pokemongomap.searcher;


import android.app.Activity;
import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.device.DeviceInfo;
import com.pokegoapi.api.device.SensorInfo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.AsyncPokemonGoException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokemongomap.helpers.PokemonHelper;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.DatabaseConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.pokemongomap.pokemongomap.MapFragment;
import com.pokemongomap.pokemongomap.RemoteDatabaseConnection;

import okhttp3.OkHttpClient;

public final class PokemonData {

    //private static final long SLEEP_TIME_FETCH = 15000;
    private static final long SLEEP_TIME_UPDATE = 1000;
    private static final long SLEEP_TIME_SEARCH = 10500;
    private static final long SLEEP_TIME_WALKER = 1000;

    private static final int SCAN_THREADS = 10;
    private static final int RADIUS = 4;
    private static final int ATTEMPT_THRESHOLD = 5;
    private static final long TIME_TO_RELOGIN = 1800000;

    private static final double MOVE_TO_LOCATION_DISTANCE = 10;

    private static final double mDisplacement = getDisplacement();

    private static final Cell[] CELLS = Cell.createCells(RADIUS);

    private static String[] ACCOUNTS;

    private static PokemonData mPokemonData = new PokemonData();

    private static volatile Queue<Pokemon> mPokemon;

    private static Toast mToast;

    private static Context mContext;
    private static Activity mActivity;

    private static PokemonGo[] mPokemonGo;
    private static Date[] mLastLogin;
    private static boolean[] mIsBroken;

    //private static Queue<LatLng> mLocationQueue = new ConcurrentLinkedQueue<>();

    private static List<Cell> mCells = new LinkedList<>();

    private static LatLng mCurrentLocation;

    private static Cell mCurrentCell;

    private static int mConnectedWorkers;

    public static void init(Context context, Activity activity) {
        mPokemon = new ConcurrentLinkedQueue<>();
        mContext = context;
        mActivity = activity;
        mConnectedWorkers = 0;
        mToast = Toast.makeText(context , mConnectedWorkers + " worker(s) connected", Toast.LENGTH_LONG);

        //Timer timerFetch = new Timer();
        //PokemonDataFetchTask fetchTask = mPokemonData.new PokemonDataFetchTask();
        //timerFetch.schedule(fetchTask, 0, SLEEP_TIME_FETCH);

        Timer timerUpdate = new Timer();
        PokemonDataUpdateTask udpateTask = mPokemonData.new PokemonDataUpdateTask();
        timerUpdate.schedule(udpateTask, 0, SLEEP_TIME_UPDATE);

        Timer timerSearch = new Timer();
        PokemonSearchTask searchTask = mPokemonData.new PokemonSearchTask();
        timerSearch.schedule(searchTask, 0, SLEEP_TIME_SEARCH);
    }

    public static Queue<Pokemon> getPokemon() {
        return mPokemon;
    }

    private static String[] getAccounts() {
        try {
            return RemoteDatabaseConnection.getAccounts(SCAN_THREADS);
        } catch (NullPointerException e) {
            System.exit(1);
            return null;
        }
    }

    private class PokemonDataUpdateTask extends TimerTask {
        @Override
        public void run() {

            Date currentTime = new Date();

            Iterator<Pokemon> it = mPokemon.iterator();
            MapFragment fragment = MapFragment.getInstance();
            synchronized (mPokemon.iterator()) {
                while (it.hasNext()) {
                    Pokemon pokemon = it.next();
                    if (pokemon.getDisappearTime().before(currentTime)) {
                        if (fragment != null) {
                            fragment.removeOverlay(pokemon);
                        }
                        it.remove();
                    } else if (fragment != null) {
                        int seconds = (int) (pokemon.getDisappearTime().getTime() - currentTime.getTime())/1000;
                        float dim = fragment.getDim();
                        fragment.updatePokemon(pokemon, seconds, dim, fragment.getOffset(dim));
                    }
                }
            }
        }
    }

    private class PokemonDataFetchTask extends TimerTask {

        @Override
        public void run() {

            String response;
            synchronized (DatabaseConnection.getInstance()) {
                LatLng loc;
                try {
                    loc = DatabaseConnection.getInstance().getLocation();
                } catch (CursorIndexOutOfBoundsException e) {
                    try {
                        DatabaseConnection.getInstance().wait();
                    } catch (InterruptedException e1) {
                        // ignore
                    }
                    loc = DatabaseConnection.getInstance().getLocation();
                }
                //response = RemoteDatabaseConnection.getPokemonInRange(loc, 5.f);
                response = "";
            }

            try {
                JSONObject jObject = new JSONObject(response);
                JSONArray jPokemon = jObject.getJSONArray("pokemons");

                for (int i = 0; i < jPokemon.length(); i++) {

                    JSONObject jCurrent = jPokemon.getJSONObject(i);
                    int id = jCurrent.getInt("pokemon_id");
                    LatLng loc = new LatLng(jCurrent.getDouble("latitude"), jCurrent.getDouble("longitude"));

                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    Date disappearTime = format.parse(jCurrent.getString("disappear_time"));
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                    cal.setTime(disappearTime);
                    if (cal.getTimeZone().inDaylightTime(cal.getTime())) {
                        cal.add(Calendar.MILLISECOND, TimeZone.getDefault().getRawOffset());
                    } else {
                        cal.add(Calendar.MILLISECOND, TimeZone.getDefault().getRawOffset() + 3600000);
                    }

                    // add new ones
                    try {
                        Pokemon pokemon = PokemonHelper.getPokemon(mContext, id, loc, cal.getTime());
                        if (!mPokemon.contains(pokemon))
                            mPokemon.add(pokemon);
                    } catch (NullPointerException e) {
                        // ignore
                    }

                }

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    private class PokemonSearchTask extends TimerTask {

        @Override
        public void run() {

            // location creation task
            synchronized (DatabaseConnection.getInstance()) {
                try {
                    mCurrentLocation = DatabaseConnection.getInstance().getLocation();
                } catch (CursorIndexOutOfBoundsException e) {
                    try {
                        DatabaseConnection.getInstance().wait();
                    } catch (InterruptedException e1) {}
                    mCurrentLocation = DatabaseConnection.getInstance().getLocation();
                }
            }

            // update accounts
            if (ACCOUNTS == null) {
                ACCOUNTS = getAccounts();
                mPokemonGo = new PokemonGo[SCAN_THREADS];
                mLastLogin = new Date[SCAN_THREADS];
                mIsBroken = new boolean[SCAN_THREADS];
                for (int i = 0; i < SCAN_THREADS; i++) {
                    mIsBroken[i] = false;
                }
                for (int i = 0; i < SCAN_THREADS; i++) {
                    new PokemonGetTask(i, mCurrentLocation).start();
                }
            } else {
                List<String> brokenAccounts = new LinkedList<>();
                List<String> workingAccounts = new LinkedList<>();
                List<Integer> ind = new LinkedList<>();
                for (int i = 0; i < SCAN_THREADS; i++) {
                    try {
                        if (mIsBroken[i] == true) {
                            brokenAccounts.add(ACCOUNTS[i]);
                            ind.add(i);
                        } else {
                            workingAccounts.add(ACCOUNTS[i]);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }
                }
                if (workingAccounts.size() > 0) {
                    RemoteDatabaseConnection.updateAccounts(workingAccounts);
                }
                if (brokenAccounts.size() > 0) {
                    RemoteDatabaseConnection.setBroken(brokenAccounts);
                    String[] newAccounts = RemoteDatabaseConnection.getAccounts(brokenAccounts.size());
                    try {
                        for (int i = 0; i < newAccounts.length; i++) {
                            ACCOUNTS[ind.get(i)] = newAccounts[i];
                            mIsBroken[ind.get(i)] = false;
                        }
                    } catch (IndexOutOfBoundsException e) {}
                }
            }

            // realignment of cells
            Cell closest = Cell.getClosest(mCurrentLocation, mCells);
            realign(mCurrentLocation, closest);
            mCurrentCell = closest;

            // populate queue
            /*for (int i = 0; i < mConnectedWorkers; i++) {
                if (mLocationQueue.size() < mConnectedWorkers) {
                    Cell cell = Cell.getClosestNotInUse(mCurrentLocation, mCells);
                    if (cell != null) {
                        mLocationQueue.add(cell.getLocation());
                    } else {
                        break;
                    }
                }
            }*/
        }
    }

    private class PokemonGetTask extends Thread {

        private int mId;
        private WalkToLoc mWalkerTask;

        public PokemonGetTask(int id, LatLng location) {
            mId = id;
            mWalkerTask = new WalkToLoc(mId, location, null, null);
        }

        @Override
        public void run() {

            int attempts = 0;
            boolean firstRun = true;
            outer:
            while (true) {
                if (mIsBroken[mId]) {
                    try {
                        Thread.sleep(SLEEP_TIME_SEARCH);
                    } catch (InterruptedException e) {}
                    continue;
                }

                OkHttpClient httpClient = new OkHttpClient();
                Date currentTime = new Date();
                if (mPokemonGo[mId] == null || currentTime.getTime() - (mLastLogin[mId].getTime()) > TIME_TO_RELOGIN) {
                    boolean login = false;
                    attempts = 0;
                    while (!login) {
                        try {
                            String username, password;
                            try {
                                String[] accountInfo = ACCOUNTS[mId].split(":");
                                username = accountInfo[0];
                                password = accountInfo[1];
                            } catch (IndexOutOfBoundsException e) {
                                try {
                                    Thread.sleep(SLEEP_TIME_SEARCH);
                                } catch (InterruptedException e1) {}
                                continue;
                            }
                            mPokemonGo[mId] = new PokemonGo(new PtcCredentialProvider(httpClient, username, password), httpClient);
                            mLastLogin[mId] = new Date();
                            mPokemonGo[mId].setDeviceInfo(DeviceInfo.DEFAULT);
                            mWalkerTask.initializeStartLocation();
                            login = true;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mToast.setText(++mConnectedWorkers + " worker(s) connected");
                                    mToast.show();
                                }
                            });
                        } catch (RemoteServerException | LoginFailedException | AsyncPokemonGoException e) {
                            if (e.getMessage().equals("Failed to fetch token, body:error=invalid_request")) {
                                if (++attempts == ATTEMPT_THRESHOLD) {
                                    mIsBroken[mId] = true;
                                    try {
                                        Thread.sleep(SLEEP_TIME_SEARCH);
                                    } catch (InterruptedException e1) {}
                                    continue outer;
                                } else {
                                    try {
                                        Thread.sleep(SLEEP_TIME_SEARCH);
                                    } catch (InterruptedException e1) {}
                                    continue;
                                }
                            } else {
                                try {
                                    Thread.sleep(SLEEP_TIME_SEARCH);
                                } catch (InterruptedException e1) {}
                                continue;
                            }
                        }
                    }
                }
                //LatLng loc = mLocationQueue.poll();
                LatLng difLoc = new LatLng((mCurrentLocation.latitude + mWalkerTask.getLocation().latitude) / 2,
                        (mCurrentLocation.longitude + mWalkerTask.getLocation().longitude) / 2);
                Cell cell = Cell.getClosestNotInUse(difLoc, mCells);
                if (cell != null) {

                    // move to location
                    LatLng loc = cell.getLocation();
                    long before = new Date().getTime();
                    mWalkerTask = mWalkerTask.moveToLocation(loc);
                    synchronized (mWalkerTask) {
                        if (!mWalkerTask.isLocationReached()) {
                            try {
                                mWalkerTask.wait();
                            } catch (InterruptedException e) {}
                        }
                    }
                    long timeDiff = new Date().getTime() - before;
                    try {
                        if (!firstRun && SLEEP_TIME_SEARCH - timeDiff > 0) {
                            Thread.sleep(SLEEP_TIME_SEARCH - timeDiff);
                        }
                    } catch (InterruptedException e1) {}

                    // evaluate location
                    boolean locationChecked = false;
                    attempts = 0;
                    while (!locationChecked) {
                        mPokemonGo[mId].setLocation(loc.latitude, loc.longitude, 90 + Math.random() * 20);
                        try {
                            List<CatchablePokemon> pokemonList = mPokemonGo[mId].getMap().getCatchablePokemon();
                            for (CatchablePokemon catchablePokemon : pokemonList) {
                                long expirationTime = catchablePokemon.getExpirationTimestampMs();
                                if (expirationTime < 0) {
                                    expirationTime = 3600;
                                }
                                Date time = new Date(expirationTime);
                                Pokemon pokemon = PokemonHelper.getPokemon(mContext, catchablePokemon.getPokemonId().getNumber(),
                                        new LatLng(catchablePokemon.getLatitude(), catchablePokemon.getLongitude()), time);
                                if (pokemon != null) {
                                    if (!mPokemon.contains(pokemon))
                                        mPokemon.add(pokemon);
                                }
                            }
                            locationChecked = true;
                        } catch (RemoteServerException | LoginFailedException e) {
                            if (++attempts == ATTEMPT_THRESHOLD) {
                                mIsBroken[mId] = true;
                                //mLocationQueue.add(loc);
                                cell.reuse();

                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mToast.setText("Worker disconnected! Current active workers: " + --mConnectedWorkers);
                                        mToast.show();
                                    }
                                });
                                try {
                                    Thread.sleep(SLEEP_TIME_SEARCH);
                                } catch (InterruptedException e1) {}
                                continue outer;
                            } else {
                                try {
                                    Thread.sleep(SLEEP_TIME_SEARCH);
                                } catch (InterruptedException e1) {}
                                continue;
                            }
                        }
                    }
                    firstRun = false;
                    continue outer;
                }
                try {
                    Thread.sleep(SLEEP_TIME_SEARCH);
                } catch (InterruptedException e) {}
            }
        }
    }

    private class WalkToLoc extends TimerTask {
        private int mId;
        private LatLng mCurrentLocation, mTargetLocation;
        private Timer mTimer;
        private boolean mLocationReached;
        public WalkToLoc(int id, LatLng loc, LatLng target, Timer timer) {
            mId = id;
            mCurrentLocation = loc;
            mTargetLocation = target;
            mTimer = timer;
            mLocationReached = false;
        }
        public void initializeStartLocation() {
            mPokemonGo[mId].getMap().setLocation(mCurrentLocation.latitude, mCurrentLocation.longitude, 90 + Math.random() * 20);
        }
        public WalkToLoc moveToLocation(LatLng loc) {
            mPokemonGo[mId].setSensorInfo(new SensorInfo(SensorFake.getInstance()));
            if (Cell.distanceInMeter(mCurrentLocation, loc) < MOVE_TO_LOCATION_DISTANCE) {
                mLocationReached = true;
            } else {
                mLocationReached = false;
                mTimer = new Timer();
                WalkToLoc newTask = new WalkToLoc(mId, mCurrentLocation, loc, mTimer);
                mTimer.schedule(newTask, 0, SLEEP_TIME_WALKER);
                return newTask;
            }
            return this;
        }
        public boolean isLocationReached() {
            return mLocationReached;
        }
        public LatLng getLocation() {
            return mCurrentLocation;
        }
        @Override
        public void run() {
            synchronized (this) {
                double alpha = Math.atan2(mTargetLocation.latitude - mCurrentLocation.latitude,
                        mTargetLocation.longitude - mCurrentLocation.longitude);
                mCurrentLocation = Cell.getDisplacementEstimate(mCurrentLocation, MOVE_TO_LOCATION_DISTANCE, alpha);
                SensorFake.update(alpha, MOVE_TO_LOCATION_DISTANCE);
                mPokemonGo[mId].setSensorInfo(new SensorInfo(SensorFake.getInstance()));
                mPokemonGo[mId].setLocation(mCurrentLocation.latitude, mCurrentLocation.longitude, 90 + Math.random() * 20);
                double distance = Cell.distanceInMeter(mCurrentLocation, mTargetLocation);
                if (distance < MOVE_TO_LOCATION_DISTANCE) {
                    mLocationReached = true;
                    mTimer.cancel();
                    notifyAll();
                }
            }
        }
    }

    private static double getDisplacement() {
        return 70 * Math.sqrt(3);
    }

    private static void realign(LatLng location, Cell cellSource) {
        List<Cell> newCells = new LinkedList<>(mCells) ;
        if (cellSource == null) {
            for (Cell cell : CELLS) {
                newCells.add(new Cell(cell));
            }
            newCells.get(0).setSource(location);
            newCells.get(0).setLocation(location);
            cellSource = newCells.get(0);
            for (Cell c : newCells) {
                if (c.getLocation() == null) {
                    c.setDisplacementEstimate(cellSource, mDisplacement, newCells);
                }
            }
        } else {
            LatLng cellLocation = cellSource.getLocation();
            while (Cell.distanceInMeter(location, cellLocation) > getDisplacement()) {
                if (cellSource != null && !cellSource.equals(mCurrentCell)) {
                    for (Cell cell : newCells) {
                        if (Cell.isCellInRange(cell, cellLocation, RADIUS, mDisplacement)) {
                            cell.recreatePath(cellSource, mDisplacement, mCells);
                        }
                    }
                }
                if (mCells.size() == 0 || cellSource != null) {
                    List<Cell> compareCells = new LinkedList<>(newCells);
                    for (Cell cell : CELLS) {
                        boolean contained = false;
                        for (Cell oldCell : compareCells) {
                            if (oldCell.hasPath(cell)) {
                                contained = true;
                                break;
                            }
                        }
                        if (!contained) {
                            Cell c = new Cell(cell);
                            c.setDisplacementEstimate(cellSource, mDisplacement, newCells);
                            newCells.add(c);
                        }
                    }
                }
                cellSource = Cell.getClosest(location, newCells);
                cellLocation = cellSource.getLocation();
                /*for (Cell cell : newCells) {
                    if (cell.getPath().size() == 0) {
                        cellSource = cell;
                        break;
                    }
                }*/
            }
        }


        mCells = newCells;
    }

}
