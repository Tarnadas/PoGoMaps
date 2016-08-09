package com.pokemongomap.pokemongomap;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class RemoteDatabaseConnection {

    public static String KEY = "dsa78adhU9410ASd4lkO";

    public static String SERVER_IP = "89.163.173.74";

    public static String SERVER_PHP_PAGE = "app/pogo.php";

    /**
     *
     * @param getVars Http Get variables
     * @param getRequest Http Get variable values
     * @return Http Get response
     */
    public static String HttpGet(String[] getVars, String[] getRequest) {
        URL url;
        HttpURLConnection urlConnection = null;
        String response = "";
        try {
            String urlString = "http://" + SERVER_IP + "/" + SERVER_PHP_PAGE;
            for (int i = 0; i < getVars.length; i++) {
                if (i == 0) {
                    urlString += "?";
                } else {
                    urlString += "&";
                }
                urlString += getVars[i] + "=" + getRequest[i];
            }
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Key", KEY);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            response = "ERROR:MalformedURLException";
        } catch (IOException e) {
            e.printStackTrace();
            response = "ERROR:IOException";
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return response;
    }

    public static String getPokemonInRange(LatLng loc, float range) {
        String locString = loc.latitude + "_" + loc.longitude;

        URL url;
        HttpURLConnection urlConnection = null;
        String response = "";
        try {
            String urlString = "http://" + SERVER_IP + "/" + SERVER_PHP_PAGE + "?getlist=1&location=" + locString + "&range=" + Float.toString(range);
            url = new URL(urlString);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Key", KEY);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            response = "ERROR:MalformedURLException";
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            response = "ERROR:IOException";
            return null;
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return response;
    }

    public static String[] getAccounts(int amount) {
        URL url;
        HttpURLConnection urlConnection = null;
        String[] response;
        try {
            String urlString = "http://" + SERVER_IP + "/" + SERVER_PHP_PAGE + "?getaccounts=1&amount=" + amount;
            url = new URL(urlString);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Key", KEY);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            response = convertStreamToString(in).split(" ");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        if (response == null) {
            throw new NullPointerException();
        }
        return response;
    }

    public static void updateAccounts(String[] accounts) {
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            String a = accounts[0].split(":")[0];
            for (int i = 1; i < accounts.length; i++) {
                a += "_" + accounts[i].split(":")[0];
            }
            String urlString = "http://" + SERVER_IP + "/" + SERVER_PHP_PAGE + "?updateaccounts=1&accounts=" + a;
            url = new URL(urlString);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Key", KEY);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
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