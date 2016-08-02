package com.pokemongomap.pokemon;


import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pokemongomap.helpers.BitmapHelper;
import com.pokemongomap.helpers.PokemonHelper;
import com.pokemongomap.pokemongomap.DatabaseConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.pokemongomap.pokemongomap.MapFragment;

public final class PokemonData {

    private static final long SLEEP_TIME_FETCH = 5000;
    private static final long SLEEP_TIME_UPDATE = 1000;

    private static PokemonData mPokemonData = new PokemonData();

    private static final String SERVER_IP = "89.163.173.74:5000/raw_data";

    private static volatile Queue<Pokemon> mPokemon;

    public static void init() {
        mPokemon = new ConcurrentLinkedQueue<>();
        Timer timerFetch = new Timer();
        PokemonDataFetchTask fetchTask = mPokemonData.new PokemonDataFetchTask();
        timerFetch.schedule(fetchTask, 0, SLEEP_TIME_FETCH);
        Timer timerUpdate = new Timer();
        PokemonDataUpdateTask udpateTask = mPokemonData.new PokemonDataUpdateTask();
        timerUpdate.schedule(udpateTask, 0, SLEEP_TIME_UPDATE);
    }

    public static PokemonData getInstance() {
        return mPokemonData;
    }

    public static Queue<Pokemon> getPokemon() {
        return mPokemon;
    }

    private PokemonData() {
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

            URL url;
            HttpURLConnection urlConnection = null;
            String response = "";
            try {
                synchronized (DatabaseConnection.getInstance()) {
                    try {
                        String urlString = "http://" + SERVER_IP + "?location=" + DatabaseConnection.getInstance().getLocationAsString();
                        url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        response = convertStreamToString(in);
                    } catch (CursorIndexOutOfBoundsException e) {
                        try {
                            DatabaseConnection.getInstance().wait();
                        } catch (InterruptedException e1) {
                            // ignore
                        }
                        String urlString = "http://" + SERVER_IP + "?location=" + DatabaseConnection.getInstance().getLocationAsString();
                        url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        response = convertStreamToString(in);
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                response = "ERROR:MalformedURLException";
            } catch (IOException e) {
                e.printStackTrace();
                response = "ERROR:IOException";
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }

            try {
                JSONObject jObject = new JSONObject(response);
                JSONArray jPokemon = jObject.getJSONArray("pokemons");

                for (int i = 0; i < jPokemon.length(); i++) {

                    JSONObject jCurrent = jPokemon.getJSONObject(i);
                    int id = jCurrent.getInt("pokemon_id");
                    String name = jCurrent.getString("pokemon_name");
                    LatLng loc = new LatLng(jCurrent.getDouble("latitude"), jCurrent.getDouble("longitude"));
                    Date disappearTime = new Date();
                    disappearTime.setTime(jCurrent.getLong("disappear_time"));

                    // add new ones
                    try {
                        Pokemon pokemon = PokemonHelper.getPokemon(id, loc, disappearTime);
                        if (!mPokemon.contains(pokemon))
                            mPokemon.add(pokemon);
                    } catch (NullPointerException e) {
                        // ignore
                    }

                }

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }
        }
    }

    private static String convertStreamToString(InputStream is) {
        String line;
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total.toString();
    }

}
