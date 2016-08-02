package com.pokemongomap.pokemongomap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener {

    private static final int DEFAULT_ZOOM = 15;

    private static final float ZOOM_SHRINK = 1.2f;
    private static final float ZOOM_MULT = 20.f;
    private static final float DIM_MIN = 18.f;
    private static final float ZOOM_MULT_TRAINER = 10.f;
    private static final float DIM_MIN_TRAINER = 7.f;
    private static final float ZOOM_LEVEL_TIMER = 15.f;
    private static final float MARKER_OFFSET = -1.5f;

    private static MapFragment mMapFragment;

    private static boolean appStart = true;

    private static LatLngBounds mLastBounds;

    private GoogleMap mMap;
    private float mMaxZoomLevel;
    private float currentZoom = -1;
    private LatLng currentTarget = new LatLng(0,0);
    private LatLngBounds mBounds;
    private GroundOverlay mTrainer;
    private LocationReceiver mLocationReceiver;

    private static boolean mMapReady = false;
    private static boolean mLocationReady = false;

    private Map<Pokemon, GroundOverlay> mOverlays;
    private Map<Pokemon, Marker> mCountdowns;

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOverlays = new ConcurrentHashMap<>();
        mCountdowns = new ConcurrentHashMap<>();

        IntentFilter statusIntentFilter = new IntentFilter(Constants.LOCATION_BROADCAST);
        mLocationReceiver = new LocationReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mLocationReceiver, statusIntentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.jump_to_location);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    LatLng loc = DatabaseConnection.getInstance().getLocation();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, DEFAULT_ZOOM));
                } catch (CursorIndexOutOfBoundsException e) {
                    // ignore
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapReady)
            mLastBounds = mBounds;
        mMapReady = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mLocationReceiver);
        mMapFragment = null;
        mTrainer = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMaxZoomLevel = mMap.getMaxZoomLevel();
        mMapReady = true;

        if (mLocationReady) {
            mMapFragment = MapFragment.this;
            LatLng loc = DatabaseConnection.getInstance().getLocation();
            mTrainer = mMap.addGroundOverlay(new GroundOverlayOptions().position(loc, getDimTrainer()).clickable(false).
                    image(BitmapDescriptorFactory.fromResource(R.drawable.trainer)));
            if (mLastBounds != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mLastBounds, 0));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, DEFAULT_ZOOM));
            }
            mMap.setOnCameraChangeListener(MapFragment.this);
        } else {
            // something went wrong
        }
    }

    public float getDim() {
        return Math.max((1 + mMaxZoomLevel - (currentZoom * ZOOM_SHRINK)) * ZOOM_MULT, DIM_MIN);
    }

    public float getDimTrainer() {
        return Math.max((1 + mMaxZoomLevel - (currentZoom * ZOOM_SHRINK)) * ZOOM_MULT_TRAINER, DIM_MIN_TRAINER);
    }

    public double getOffset(float dim) {
        return MARKER_OFFSET * (180/Math.PI)*(dim/(6378137*2));
    }

    @Override
    public void onCameraChange(CameraPosition pos) {
        if (pos.zoom != currentZoom || pos.target != currentTarget){
            currentZoom = pos.zoom;
            currentTarget = pos.target;
            mBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            float dim = getDim();
            mTrainer.setDimensions(getDimTrainer());

            for (Pokemon pokemon : PokemonData.getPokemon()) {
                int seconds = (int) (pokemon.getDisappearTime().getTime() - new Date().getTime())/1000;
                if (seconds < 0) {
                    continue;
                }
                LatLng loc = pokemon.getLocation();
                double offset = getOffset(dim);

                if (mBounds.contains(loc)) {
                    // Pokemon in map bounds
                    GroundOverlay overlay = mOverlays.get(pokemon);
                    if (overlay != null) {
                        // Pokemon was already some time in map bounds
                        Marker timer = mCountdowns.get(pokemon);
                        LatLng newLoc = new LatLng(loc.latitude + offset, loc.longitude);
                        GroundOverlay pokeOverlay = overlay;

                        pokeOverlay.setDimensions(dim);
                        timer.setPosition(newLoc);
                        if (!pokeOverlay.isVisible()) {
                            pokeOverlay.setVisible(true);
                        }
                        if (currentZoom >= ZOOM_LEVEL_TIMER && !timer.isVisible()) {
                            timer.setVisible(true);
                        } else if (currentZoom < ZOOM_LEVEL_TIMER && timer.isVisible()) {
                            timer.setVisible(false);
                        }

                    } else {
                        // first time Pokemon is in map bounds
                        GroundOverlayOptions options = new GroundOverlayOptions().position(loc, dim).image(BitmapDescriptorFactory.fromResource(pokemon.getResource()))
                                .transparency(0).visible(true);
                        Bitmap text = BitmapHelper.getBitmap(seconds);
                        LatLng markerLoc = new LatLng(loc.latitude + offset, loc.longitude);
                        MarkerOptions markerOptions = new MarkerOptions().position(markerLoc).
                                icon(BitmapDescriptorFactory.fromBitmap(text)).draggable(false).visible(true);

                        overlay = mMap.addGroundOverlay(options);
                        mOverlays.put(pokemon, overlay);
                        Marker timer = mMap.addMarker(markerOptions);
                        mCountdowns.put(pokemon, timer);
                        if (currentZoom >= ZOOM_LEVEL_TIMER) {
                        } else if (currentZoom < ZOOM_LEVEL_TIMER) {
                            timer.setVisible(false);
                        }
                    }
                } else {
                    // Pokemon not in map bounds
                    GroundOverlay overlay = mOverlays.get(pokemon);
                    if (overlay != null) {
                        Marker timer = mCountdowns.get(pokemon);
                        if (overlay.isVisible()) {
                            overlay.setVisible(false);
                            timer.setVisible(false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void updatePokemon(final Pokemon pokemon, int seconds, final float dim, final double offset) {
        final Marker marker = mCountdowns.get(pokemon);
        final Bitmap text = BitmapHelper.getBitmap(seconds);
        if (marker != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(text));
                }
            });
        }
        final LatLng loc = pokemon.getLocation();
        if (mBounds != null && mBounds.contains(loc)) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GroundOverlay overlay = mOverlays.get(pokemon);
                        if (overlay == null) {
                            // Pokemon first time in map bounds
                            final GroundOverlayOptions options = new GroundOverlayOptions().position(loc, dim).image(BitmapDescriptorFactory.fromResource(pokemon.getResource()))
                                    .transparency(0);
                            LatLng markerLoc = new LatLng(loc.latitude + offset, loc.longitude);
                            final MarkerOptions markerOptions = new MarkerOptions().position(markerLoc).
                                    icon(BitmapDescriptorFactory.fromBitmap(text)).draggable(false);
                            overlay = mMap.addGroundOverlay(options);
                            mOverlays.put(pokemon, overlay);
                            Marker timer = mMap.addMarker(markerOptions);
                            mCountdowns.put(pokemon, timer);
                            if (currentZoom < ZOOM_LEVEL_TIMER) {
                                timer.setVisible(false);
                            }
                        }
                    }
                });
            }
    }

    public void removeOverlay(final Pokemon pokemon) {
        final GroundOverlay overlay = mOverlays.get(pokemon);
        if (overlay != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mOverlays.remove(pokemon);
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
    }

    private class LocationReceiver extends BroadcastReceiver {
        // Prevents instantiation
        private LocationReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, final Intent intent) {

            while (!mMapReady) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String locString[] = intent.getExtras().getString(Constants.LOCATION_STATUS).split(" ");
            LatLng loc = new LatLng(Double.parseDouble(locString[0]), Double.parseDouble(locString[1]));

            if (appStart) {
                mMapFragment = MapFragment.this;

                mTrainer = mMap.addGroundOverlay(new GroundOverlayOptions().position(loc, getDimTrainer()).clickable(false).
                        image(BitmapDescriptorFactory.fromResource(R.drawable.trainer)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, DEFAULT_ZOOM));

                mMap.setOnCameraChangeListener(mMapFragment);
                mLocationReady = true;
            } else if (mTrainer != null) {
                mTrainer.setPosition(loc);
            }
        }
    }

}
