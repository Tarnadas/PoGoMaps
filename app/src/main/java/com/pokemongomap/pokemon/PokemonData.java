package com.pokemongomap.pokemon;


import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.AsyncPokemonGoException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokemongomap.helpers.PokemonHelper;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pokemongomap.pokemongomap.MapFragment;
import com.pokemongomap.pokemongomap.RemoteDatabaseConnection;

import okhttp3.OkHttpClient;

public final class PokemonData {

    private static final long SLEEP_TIME_FETCH = 15000;
    private static final long SLEEP_TIME_UPDATE = 1000;
    private static final long SLEEP_TIME_SEARCH = 10500;

    private static final int SCAN_THREADS = 10;
    private static final int RADIUS = 5;
    private static final int MIN_QUEUE_OVERHEAD = 40;
    private static final long IN_USE_THRESHOLD = 30000;
    private static final long TIME_TO_RELOGIN = 1800000;

    private static final double mDisplacement = getDisplacement();

    private static final Path[] PATHS = createPaths(RADIUS);

    private static String[] ACCOUNTS;

    private static PokemonData mPokemonData = new PokemonData();

    private static volatile Queue<Pokemon> mPokemon;

    private static Context mContext;

    private static PokemonGo[] mPokemonGo;
    private static Date[] mLastLogin;

    private static Queue<LatLng> mLocationQueue = new ConcurrentLinkedQueue<>();

    private static Path[] mPaths;

    private static LatLng mCurrentLocation;

    public static void init(Context context) {
        mPokemon = new ConcurrentLinkedQueue<>();
        mContext = context;

        Timer timerFetch = new Timer();
        PokemonDataFetchTask fetchTask = mPokemonData.new PokemonDataFetchTask();
        timerFetch.schedule(fetchTask, 0, SLEEP_TIME_FETCH);

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
                response = RemoteDatabaseConnection.getPokemonInRange(loc, 5.f);
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

            // update accounts
            if (ACCOUNTS == null) {
                ACCOUNTS = getAccounts();
                mPokemonGo = new PokemonGo[ACCOUNTS.length];
                mLastLogin = new Date[ACCOUNTS.length];
            } else {
                RemoteDatabaseConnection.updateAccounts(ACCOUNTS);
            }

            // location creation task with realignment
            synchronized (DatabaseConnection.getInstance()) {
                try {
                    mCurrentLocation = DatabaseConnection.getInstance().getLocation();
                } catch (CursorIndexOutOfBoundsException e) {
                    try {
                        DatabaseConnection.getInstance().wait();
                    } catch (InterruptedException e1) {
                        // ignore
                    }
                    mCurrentLocation = DatabaseConnection.getInstance().getLocation();
                }
                if (mPaths != null) {
                    realign(getClosest(mCurrentLocation));
                } else {
                    realign(mCurrentLocation);
                }
            }

            // populate queue
            for (int i = 0; i < MIN_QUEUE_OVERHEAD; i++) {
                if (mLocationQueue.size() < MIN_QUEUE_OVERHEAD) {
                    Path path = getClosestNotInUse();
                    if (path != null) {
                        mLocationQueue.add(path.getTarget());
                    } else {
                        break;
                    }
                }
            }

            // fetch data task
            for (int i = 0; i < ACCOUNTS.length; i++) {
                LatLng loc = mLocationQueue.poll();
                if (loc != null) {
                    String[] accountInfo = ACCOUNTS[i].split(":");
                    ExecutorService executor = Executors.newCachedThreadPool();
                    executor.submit(new PokemonGetTask(i, loc, accountInfo[0], accountInfo[1]));
                }
            }
        }
    }

    private class PokemonGetTask implements Runnable {

        private int mId;
        private LatLng mLocation;
        private String mUsername;
        private String mPassword;

        public PokemonGetTask(int id, LatLng loc, String username, String password) {
            mId = id;
            mLocation = loc;
            mUsername = username;
            mPassword = password;
        }

        @Override
        public void run() {

            OkHttpClient httpClient = new OkHttpClient();
            Date currentTime = new Date();
            if (mPokemonGo[mId] == null || currentTime.getTime() - (mLastLogin[mId].getTime()) > TIME_TO_RELOGIN) {
                boolean login = false;
                while (!login) {
                    try {
                        mPokemonGo[mId] = new PokemonGo(new PtcCredentialProvider(httpClient, mUsername, mPassword), httpClient);
                        mLastLogin[mId] = new Date();
                        login = true;
                    } catch (RemoteServerException | LoginFailedException | AsyncPokemonGoException e) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }

            synchronized (mPokemon) {
                mPokemonGo[mId].setLocation(mLocation.latitude, mLocation.longitude, 90 + Math.random() * 20);
                try {
                    List<CatchablePokemon> pokemonList = mPokemonGo[mId].getMap().getCatchablePokemon();
                    for (CatchablePokemon catchablePokemon : pokemonList) {
                        Date time = new Date(catchablePokemon.getExpirationTimestampMs());
                        Pokemon pokemon = PokemonHelper.getPokemon(mContext, catchablePokemon.getPokemonId().getNumber(),
                                new LatLng(catchablePokemon.getLatitude(), catchablePokemon.getLongitude()), time);
                        if (pokemon != null) {
                            if (!mPokemon.contains(pokemon))
                                mPokemon.add(pokemon);
                        }
                    }
                } catch (RemoteServerException | LoginFailedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }


    private static double getDisplacement() {
        return 70 * Math.sqrt(3);
    }

    public static void realign(LatLng source) {
        Path[] newPaths = PATHS.clone();
        for (int i = 0; i < newPaths.length; i++) {
            setDisplacementEstimateFromPath(source, mDisplacement, newPaths[i], newPaths);
        }
        mPaths = newPaths;
    }

    private static Path[] createPaths(int radius) {
        int size = 1;
        for (int i = 0; i < radius;) {
            i++;
            size += i * 6;
        }
        Path[] result = new Path[size];
        int a = -1, b = 0, c = 0;
        for (int i = 0; i < result.length; i++) {
            if (++a > b) {
                a = 1;
                b += 6;
                c++;
            }
            int[] bits;
            if (b == 0){
                bits = new int[1];
            } else {
                bits = new int[b];
            }
            int d = 1;
            for (int j = 0,k = 0; j < bits.length; j++) {
                bits[j] = k;
                if (d == c) {
                    d = 0;
                    k++;
                }
                d++;
            }

            int[] push = new int[radius+1];
            for (int j = 0; j < push.length; j++) {
                push[j] = -1;
            }
            for (int j = 0; j < c; j++) {
                int index = i-1+j;
                for (int k = 0; k < c-1; k++) {
                    index -= (k+1) * 6;
                }
                if (index >= bits.length) index -= bits.length;
                push[j] = bits[index];
            }
            result[i] = new Path(push);

        }
        return result;
    }

    private static void setDisplacementEstimateFromPath(LatLng loc, double displacement, Path path, Path[] paths) {

        //Earth’s radius, sphere
        double earthRadius = 6378137;

        Path preceding = path.getPreceding(paths);
        LatLng source;
        if (preceding == null) {
            path.setSource(loc);
            path.setTarget(loc);
            return;
        }
        source = preceding.getTarget();

        double alpha = path.getLast() * Math.PI / 3;

        //offsets in meters
        double dLatM = Math.sin(alpha) * displacement;
        double dLonM = Math.cos(alpha) * displacement;

        //Coordinate offsets in radians
        double dLat = dLatM / earthRadius;
        double dLon = dLonM / (earthRadius * Math.cos(Math.PI * source.latitude / 180));

        //OffsetPosition, decimal degrees
        double newLat = source.latitude + dLat * 180 / Math.PI;
        double newLon = source.longitude + dLon * 180 / Math.PI;

        path.setSource(loc);
        path.setTarget(new LatLng(newLat, newLon));
    }

    private static double distance(LatLng loc0, LatLng loc1) {
        double dLat = loc0.latitude - loc1.latitude;
        double dLon = loc0.longitude - loc1.longitude;
        return Math.sqrt(dLat * dLat + dLon * dLon);
    }

    private static LatLng getClosest(LatLng loc) {
        LatLng result = loc;
        double minDistance = -1;
        for (Path path : mPaths) {
            LatLng target = path.getTarget();
            double distance = distance(target, loc);
            if (distance < minDistance || minDistance < 0) {
                result = target;
                minDistance = distance;
            }
        }
        return result;
    }

    private static Path getClosestNotInUse() {
        Path result = null;
        double closestDistance = -1;
        for (Path path : mPaths) {
            if (path.isInUse()) continue;
            double distance = distance(path.getTarget(), mCurrentLocation);
            if (closestDistance == -1 || distance < closestDistance) {
                result = path;
                closestDistance = distance;
            }
        }
        if (result != null)
            result.use();
        return result;
    }

    private static class Path {
        private List<Integer> path = new LinkedList<>();
        private LatLng mSource, mTarget;
        private Date timeStamp;
        public Path(int[] ints) {
            for (int i = 0; i < ints.length; i++) {
                path.add(ints[i]);
            }
        }
        public List<Integer> getPath() {
            return path;
        }
        public int getLast() {
            int last = -1;
            for (int i : path) {
                if (i != -1) {
                    last = i;
                } else {
                    break;
                }
            }
            return last;
        }
        public LatLng getSource() {
            return mSource;
        }
        public LatLng getTarget() {
            return mTarget;
        }
        public void setSource(LatLng source) {
            mSource = source;
        }
        public void setTarget(LatLng target) {
            mTarget = target;
        }
        public boolean isInUse() {
            if (timeStamp == null) return false;
            Date current = new Date();
            if (current.getTime() - timeStamp.getTime() < IN_USE_THRESHOLD) {
                return true;
            } else {
                timeStamp = null;
                return false;
            }
        }
        public void use() {
            timeStamp = new Date();
        }
        public boolean isPreceding(Path path) {
            if (this.equals(path)) return false;
            int count = 0;
            boolean result = true;
            for (int i : path.getPath()) {
                if (i == -1) return false;
                int j = getPath().get(count++);
                if (j == -1 && path.getPath().get(count) == -1) {
                    return result;
                } else if (i != j) {
                    result = false;
                }
            }
            return false;
        }
        public Path getPreceding(Path[] paths) {
            if (paths == null) return null;
            for (Path path : paths) {
                if (path.isPreceding(this)) {
                    return path;
                }
            }
            return null;
        }
        @Override
        public int hashCode() {
            int result = 0;
            for (int i : path) {
                result = (result << 3) | i;
            }
            return result;
        }
    }

}
