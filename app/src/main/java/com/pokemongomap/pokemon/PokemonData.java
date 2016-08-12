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
    private static final int RADIUS = 4;
    private static final long IN_USE_THRESHOLD = 180000;
    private static final long TIME_TO_RELOGIN = 1800000;

    private static final double mDisplacement = getDisplacement();

    private static final Cell[] CELLS = createCells(RADIUS);

    private static String[] ACCOUNTS;

    private static PokemonData mPokemonData = new PokemonData();

    private static volatile Queue<Pokemon> mPokemon;

    private static Context mContext;

    private static PokemonGo[] mPokemonGo;
    private static Date[] mLastLogin;

    private static Queue<LatLng> mLocationQueue = new ConcurrentLinkedQueue<>();

    private static List<Cell> mCells = new LinkedList<>();

    private static LatLng mCurrentLocation;

    private static Cell mCurrentCell;

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
            }
            Cell closest = getClosest(mCurrentLocation, mCells);
            realign(mCurrentLocation, closest);
            mCurrentCell = closest;

            // populate queue
            for (int i = 0; i < SCAN_THREADS; i++) {
                if (mLocationQueue.size() < SCAN_THREADS) {
                    Cell cell = getClosestNotInUse();
                    if (cell != null) {
                        mLocationQueue.add(cell.getTarget());
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
                    executor.submit(new PokemonGetTask(i, loc, accountInfo[0], "2wsxyaq1"));
                } else {
                    break;
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

    private static void realign(LatLng location, Cell cellSource) {
        List<Cell> newCells = new LinkedList<>(mCells) ;
        if (cellSource == null) {
            for (Cell cell : CELLS) {
                newCells.add(new Cell(cell));
            }
            newCells.get(0).setSource(location);
            newCells.get(0).setTarget(location);
        } else {
            LatLng cellLocation = cellSource.getTarget();
            while (distanceInMeter(location, cellLocation) > getDisplacement()) {
                if (cellSource != null && !cellSource.equals(mCurrentCell)) {
                    for (Cell cell : newCells) {
                        if (isCellInRange(cell, cellLocation)) {
                            cell.recreatePath(cellSource);
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
                            newCells.add(new Cell(cell));
                        }
                    }
                }
                cellSource = getClosest(cellLocation, newCells);
                cellLocation = cellSource.getTarget();
            }
        }


        for (Cell cell : newCells) {
            if (cell.getPath().size() == 0) {
                cellSource = cell;
                break;
            }
        }
        for (Cell c : newCells) {
            if (c.getTarget() == null) {
                setDisplacementEstimateFromCell(c, cellSource, mDisplacement, newCells);
            }
        }
        mCells = newCells;
    }

    private static Cell[] createCells(int radius) {
        int size = 1;
        for (int i = 0; i < radius;) {
            i++;
            size += i * 6;
        }
        Cell[] result = new Cell[size];
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
            result[i] = new Cell(push);

        }
        return result;
    }

    private static LatLng getDisplacementEstimate(Cell cell, double displacement, double alpha) {

        //Earth’s radius, sphere
        double earthRadius = 6378137;

        //offsets in meters
        double dLatM = Math.sin(alpha) * displacement;
        double dLonM = Math.cos(alpha) * displacement;

        //Coordinate offsets in radians
        double dLat = dLatM / earthRadius;
        double dLon = dLonM / (earthRadius * Math.cos(Math.PI * cell.getTarget().latitude / 180));

        //OffsetPosition, decimal degrees
        double newLat = cell.getTarget().latitude + dLat * 180 / Math.PI;
        double newLon = cell.getTarget().longitude + dLon * 180 / Math.PI;

        return new LatLng(newLat, newLon);
    }

    private static void setDisplacementEstimateFromCell(Cell cell, Cell sourceCell, double displacement, List<Cell> cells) {

        //Earth’s radius, sphere
        double earthRadius = 6378137;

        Cell preceding = cell.getPreceding(cells);
        LatLng source;
        if (preceding == null) {
            cell.setSource(sourceCell.getSource());
            cell.setTarget(sourceCell.getSource());
            return;
        }
        source = preceding.getTarget();

        double alpha = cell.getPath().get(cell.getPath().size()-1) * Math.PI / 3;

        //offsets in meters
        double dLatM = Math.sin(alpha) * displacement;
        double dLonM = Math.cos(alpha) * displacement;

        //Coordinate offsets in radians
        double dLat = dLatM / earthRadius;
        double dLon = dLonM / (earthRadius * Math.cos(Math.PI * source.latitude / 180));

        //OffsetPosition, decimal degrees
        double newLat = source.latitude + dLat * 180 / Math.PI;
        double newLon = source.longitude + dLon * 180 / Math.PI;

        cell.setSource(sourceCell.getSource());
        cell.setTarget(new LatLng(newLat, newLon));
    }

    private static boolean isCellInRange(Cell cell, LatLng loc) {
        boolean inRange = distanceInMeter(cell.getTarget(), loc) < (RADIUS+1) * getDisplacement();
        cell.setInRange(inRange);
        return inRange;
    }

    private static double distance(LatLng loc0, LatLng loc1) {
        double dLat = loc0.latitude - loc1.latitude;
        double dLon = loc0.longitude - loc1.longitude;
        return Math.sqrt(dLat * dLat + dLon * dLon);
    }

    private static double distanceInMeter(LatLng loc1, LatLng loc2) {
        double R = 6378.137; // Radius of earth in KM
        double dLat = (loc2.latitude - loc1.latitude) * Math.PI / 180;
        double dLon = (loc2.longitude - loc1.longitude) * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(loc1.latitude * Math.PI / 180) * Math.cos(loc2.latitude * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return d * 1000; // meters
    }

    private static Cell getRealClosest(LatLng loc) {
        for (Cell cell : mCells) {
            LatLng target = cell.getTarget();
            if (Math.abs(target.latitude-loc.latitude) < 0.000000001 && Math.abs(target.longitude-loc.longitude) < 0.000000001) {
                return cell;
            }
        }
        return null;
    }

    private static Cell getClosest(LatLng loc, List<Cell> cells) {
        Cell result = null;
        double minDistance = Integer.MAX_VALUE;
        for (Cell cell : cells) {
            LatLng target = cell.getTarget();
            double distance = distance(target, loc);
            if (distance < minDistance || minDistance < 0) {
                result = cell;
                minDistance = distance;
            }
        }
        return result;
    }

    private static Cell getClosestNotInUse() {
        Cell result = null;
        double closestDistance = -1;
        for (Cell cell : mCells) {
            if (cell.isInUse()) continue;
            if (!cell.isInRange()) continue;
            double distance = distance(cell.getTarget(), mCurrentLocation);
            if (closestDistance == -1 || distance < closestDistance) {
                result = cell;
                closestDistance = distance;
            }
        }
        if (result != null)
            result.use();
        return result;
    }

    private static class Cell {
        private List<Integer> mPath;
        private LatLng mSource, mTarget;
        private Date timeStamp;
        private boolean mInRange = true;
        public Cell(int[] ints) {
            mPath = new LinkedList<>();
            for (int i = 0; i < ints.length; i++) {
                mPath.add(ints[i]);
            }
            Iterator<Integer> it = mPath.iterator();
            while (it.hasNext()) {
                int i = it.next();
                if (i == -1) {
                    it.remove();
                }
            }
        }
        public Cell(Cell copyCell) {
            mPath = new LinkedList<>();
            for (int i : copyCell.getPath()) {
                mPath.add(i);
            }
        }
        public List<Integer> getPath() {
            return mPath;
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
        public boolean isInRange() {
            return mInRange;
        }
        public void setInRange(boolean inRange) {
            mInRange = inRange;
        }
        public Cell getAdjacentCell(int direction) {
            return getRealClosest(getDisplacementEstimate(this, getDisplacement(), direction * Math.PI/3));
        }
        public void recreatePath(Cell cell) {

            mSource = cell.getTarget();
            List<Integer> newPath = new LinkedList<>();
            Cell currentCell = cell;

            int a = -1, b = -1;
            while (!currentCell.equals(this)) {
                int direction;
                double arc = Math.atan2(getTarget().latitude-currentCell.getTarget().latitude, getTarget().longitude-currentCell.getTarget().longitude);
                if (arc >= -Math.PI/6 && arc <= Math.PI/6) {
                    direction = 0;
                } else if (arc >= Math.PI/6 && arc <= Math.PI*1/2) {
                    direction = 1;
                } else if (arc >= Math.PI*1/2 && arc <= Math.PI*5/6) {
                    direction = 2;
                } else if (arc >= Math.PI*5/6 && arc <= Math.PI*7/6) {
                    direction = 3;
                } else if (arc >= Math.PI*7/6 && arc <= Math.PI*3/2) {
                    direction = 4;
                } else {
                    direction = 5;
                }
                if (a == -1) {
                    a = direction;
                    b = direction;
                } else if (a != direction) {
                    b = direction;
                }
                if (Math.abs(a-b) > 1 && !((a == 0 && b == 5) || (a == 5 && b == 0))) break;
                if (a != direction && b != direction)  break;
                newPath.add(direction);
                if (newPath.size() == mPath.size() + cell.getPath().size()) break;
                Cell nextCell = currentCell.getAdjacentCell(direction);
                if (nextCell == null || nextCell.equals(currentCell)) {
                    break;
                }
                currentCell = nextCell;
            }
            List<Integer> orderedPath = new LinkedList<>();
            int hi = 0;
            int lo = 5;
            if (a < lo) lo = a;
            if (b < lo) lo = b;
            if (a > hi) hi = a;
            if (b > hi) hi = b;
            for (int i : newPath) {
                if (i < lo) lo = i;
            }
            for (int i : newPath) {
                if (i > hi) hi = i;
            }
            for (int i : newPath) {
                if (lo == 0 && hi == 5) {
                    if (i == hi) {
                        orderedPath.add(0, i);
                    } else {
                        orderedPath.add(i);
                    }
                } else {
                    if (i == hi) {
                        orderedPath.add(i);
                    } else {
                        orderedPath.add(0, i);
                    }
                }
            }

            if (orderedPath.size() == 0) {
                mTarget = cell.getTarget();
            }
            mPath = orderedPath;
        }
        public boolean isPreceding(Cell cell) {
            if (this.equals(cell) || getPath().size() >= cell.getPath().size()) {
                return false;
            }
            int count = cell.getPathCount() - cell.getPath().get(cell.getPath().size()-1);
            if (count != getPathCount()) {
                return false;
            }
            return cell.getPath().size() == mPath.size()+1;
        }
        public Cell getPreceding(List<Cell> cells) {
            if (cells == null || getPath().size() == 0) return null;
            for (Cell cell : cells) {
                if (cell.isPreceding(this)) {
                    return cell;
                }
            }
            return null;
        }
        public int getPathCount() {
            int count = 0;
            for (int i : mPath) {
                count += i;
            }
            return count;
        }
        public boolean hasPath(Object obj) {
            if (!(obj instanceof  Cell)) return false;
            Cell cell = (Cell) obj;
            int pathCount = cell.getPathCount();
            if (getPathCount() == pathCount && getPath().size() == cell.getPath().size()) {
                return true;
            }
            return false;
        }
        @Override
        public boolean equals (Object obj) {
            if (!(obj instanceof  Cell)) return false;
            Cell cell = (Cell) obj;
            return cell.getTarget() != null && mTarget != null && Math.abs(cell.getTarget().latitude - mTarget.latitude) < 0.00000000000000001d &&
                    Math.abs(cell.getTarget().longitude - mTarget.longitude) < 0.000000000000000001d;
        }
        @Override
        public int hashCode() {
            int result = 0;
            for (int i : mPath) {
                result = (result << 3) | i;
            }
            return result;
        }
    }

}
