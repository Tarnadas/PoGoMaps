package com.pokemongomap.pokemongomap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pokemongomap.helpers.BitmapHelper;
import com.pokemongomap.helpers.Constants;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemon.PokemonData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener {

    private static final float ZOOM_SHRINK = 1.2f;
    private static final float ZOOM_MULT = 20.f;
    private static final float ZOOM_MIN = 18.f;
    private static final float ZOOM_LEVEL_TIMER = 15.f;
    private static final float MARKER_OFFSET = -1.5f;

    private static MapFragment mMapFragment;

    private static boolean appStart = true;

    private OnFragmentInteractionListener mListener;

    private GoogleMap mMap;
    private float currentZoom = -1;
    private LatLng currentTarget = new LatLng(0,0);
    private Timer mTimer;

    private Map<LatLng, GroundOverlay> mOverlays;
    private Map<Pokemon, Marker> mCountdowns;

    private List<Pokemon> mVisibles;

    public MapFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    public static MapFragment getInstance() {
        return mMapFragment;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapFragment = this;
        mOverlays = Collections.synchronizedMap(new HashMap<LatLng, GroundOverlay>());
        mCountdowns = new ConcurrentHashMap<>();
        mVisibles = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Timer();
        TimerUpdateTask task = new TimerUpdateTask();
        mTimer.schedule(task, 1000, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mTimer.cancel();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapFragment = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        IntentFilter statusIntentFilter = new IntentFilter(Constants.LOCATION_BROADCAST);
        ResponseReceiver responseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(responseReceiver, statusIntentFilter);

        if (appStart) {
            Intent serviceIntent = new Intent(getActivity(), LocationService.class);
            getActivity().startService(serviceIntent);
            appStart = false;
        } else {
            LatLng loc = DatabaseConnection.getInstance().getLocation();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
        }
        mMap.setOnCameraChangeListener(this);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onCameraChange(CameraPosition pos) {
        if (pos.zoom != currentZoom || pos.target != currentTarget){
            currentZoom = pos.zoom;
            currentTarget = pos.target;
            float dim = Math.max((1 + mMap.getMaxZoomLevel() - (currentZoom * ZOOM_SHRINK)) * ZOOM_MULT, ZOOM_MIN);
            double offset = MARKER_OFFSET * (180/Math.PI)*(dim/(6378137*2));
            Date currentTime = new Date();

            LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            Iterator<Pokemon> it = PokemonData.getPokemon().iterator();
            synchronized (PokemonData.getPokemon().iterator()) {
                while (it.hasNext()) {
                    Pokemon pokemon = it.next();
                    LatLng loc = pokemon.getLocation();
                    long seconds = (pokemon.getDisappearTime().getTime() - currentTime.getTime())/1000;
                    if (seconds < 0) {
                        continue;
                    }

                    if (bounds.contains(loc)) {
                        // Pokemon in map bounds
                        GroundOverlay overlay = mOverlays.get(loc);
                        if (overlay != null) {
                            overlay.setDimensions(dim);
                            Marker timer = mCountdowns.get(pokemon);
                            LatLng newLoc = new LatLng(loc.latitude + offset, loc.longitude);
                            timer.setPosition(newLoc);
                            if (!overlay.isVisible()) {
                                overlay.setVisible(true);
                            }
                            if (currentZoom >= ZOOM_LEVEL_TIMER && !timer.isVisible()) {
                                timer.setVisible(true);
                                mVisibles.add(pokemon);
                            } else if (currentZoom < ZOOM_LEVEL_TIMER && timer.isVisible()) {
                                timer.setVisible(false);
                                mVisibles.remove(pokemon);
                            }
                        } else {
                            if (!mOverlays.keySet().contains(loc)) {
                                GroundOverlayOptions options = new GroundOverlayOptions().position(loc, dim).image(BitmapDescriptorFactory.fromResource(pokemon.getResource()))
                                        .transparency(0);
                                overlay = mMap.addGroundOverlay(options);
                                mOverlays.put(loc, overlay);
                                Bitmap text = BitmapHelper.getBitmap(seconds);
                                LatLng markerLoc = new LatLng(loc.latitude + offset, loc.longitude);
                                MarkerOptions markerOptions = new MarkerOptions().position(markerLoc).
                                        icon(BitmapDescriptorFactory.fromBitmap(text)).draggable(false);
                                Marker timer = mMap.addMarker(markerOptions);
                                mCountdowns.put(pokemon, timer);
                                if (currentZoom >= ZOOM_LEVEL_TIMER) {
                                    mVisibles.add(pokemon);
                                } else if (currentZoom < ZOOM_LEVEL_TIMER) {
                                    timer.setVisible(false);
                                }
                            }
                        }
                    } else {
                        // Pokemon not in map bounds
                        GroundOverlay overlay = mOverlays.get(loc);
                        if (overlay != null) {
                            Marker timer = mCountdowns.get(pokemon);
                            if (overlay.isVisible()) {
                                overlay.setVisible(false);
                                timer.setVisible(false);
                                mVisibles.remove(pokemon);
                            }
                        } else {
                            Log.d("","");
                        }
                    }
                }
            }
        }
    }

    public void removeOverlay(final Pokemon pokemon) {
        final GroundOverlay overlay = mOverlays.get(pokemon.getLocation());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mOverlays.remove(overlay.getPosition());
                    overlay.remove();

                    Marker timer = mCountdowns.get(pokemon);
                    timer.remove();
                    mCountdowns.remove(overlay);
                } catch (NullPointerException e) {
                    // ignore
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class TimerUpdateTask extends TimerTask {
        @Override
        public void run() {
            for (Pokemon pokemon : mVisibles) {
                final Marker marker = mCountdowns.get(pokemon);
                Date currentTime = new Date();
                long seconds = (pokemon.getDisappearTime().getTime() - currentTime.getTime())/1000;
                if (seconds < 0) {
                    removeOverlay(pokemon);
                    continue;
                }
                final Bitmap text = BitmapHelper.getBitmap(seconds);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(text));
                    }
                });
            }
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {
        // Prevents instantiation
        public ResponseReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            String locString[] = intent.getExtras().getString(Constants.LOCATION_STATUS).split(" ");
            LatLng loc = new LatLng(Double.parseDouble(locString[0]), Double.parseDouble(locString[1]));

            GoogleMap map = MapFragment.getInstance().getMap();
            map.addMarker(new MarkerOptions().position(loc).title("Standort"));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
        }
    }
}
