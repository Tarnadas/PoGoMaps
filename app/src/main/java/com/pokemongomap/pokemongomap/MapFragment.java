package com.pokemongomap.pokemongomap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemon.PokemonData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    private static final float ZOOM_SHRINK = 1.2f;
    private static final float ZOOM_MULT = 20.f;
    private static final float ZOOM_MIN = 18.f;
    private static final float ZOOM_LEVEL_TIMER = 15.f;

    private static final String SERVICE_KEY = "service";

    private static MapFragment mMapFragment;

    private static boolean appStart = true;

    private OnFragmentInteractionListener mListener;

    private GoogleMap mMap;
    private float currentZoom = -1;
    private LatLng currentTarget = new LatLng(0,0);

    private Map<LatLng, GroundOverlay> mOverlays;
    private Map<GroundOverlay, Marker> mCountdowns;

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
    }

    @Override
    public void onPause() {
        super.onPause();
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

        LatLng loc = DatabaseConnection.getInstance().getLocationAsync();
        mMap.addMarker(new MarkerOptions().position(loc).title("Standort"));
        if (appStart) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
            appStart = false;
        } else {
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
            float dim = Math.max((1 + mMap.getMaxZoomLevel() - (currentZoom * ZOOM_SHRINK)) * ZOOM_MULT, ZOOM_MIN);
            Date currentTime = new Date();

            LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            List<Pokemon> pokemonList = new ArrayList<>(PokemonData.getPokemon());
            for (Pokemon pokemon : pokemonList) {
                LatLng loc = pokemon.getLocation();
                int seconds = (int) (pokemon.getDisappearTime().getTime() - currentTime.getTime())/1000;
                if (seconds < 0) {
                    break;
                }
                if (bounds.contains(loc)) {
                    // Pokemon in map bounds
                    GroundOverlay overlay = mOverlays.get(loc);
                    if (overlay != null) {
                        overlay.setDimensions(dim);
                        Marker timer = mCountdowns.get(overlay);
                        if (!overlay.isVisible()) {
                            overlay.setVisible(true);
                        }
                        if (currentZoom >= ZOOM_LEVEL_TIMER && !timer.isVisible()) {
                            timer.setVisible(true);
                        } else if (currentZoom < ZOOM_LEVEL_TIMER && timer.isVisible()) {
                            timer.setVisible(false);
                        }
                    } else {
                        if (!mOverlays.keySet().contains(loc)) {
                            GroundOverlayOptions options = new GroundOverlayOptions().position(loc, dim).image(BitmapDescriptorFactory.fromResource(pokemon.getResource()))
                                    .transparency(0);
                            overlay = mMap.addGroundOverlay(options);
                            mOverlays.put(loc, overlay);
                            Bitmap text = BitmapHelper.getBitmap(seconds);
                            MarkerOptions markerOptions = new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromBitmap(text));
                            mCountdowns.put(overlay, mMap.addMarker(markerOptions));
                        }
                    }
                } else {
                    // Pokemon not in map bounds
                    GroundOverlay overlay = mOverlays.get(loc);
                    if (overlay != null) {
                        Marker timer = mCountdowns.get(overlay);
                        if (overlay.isVisible()) {
                            overlay.setVisible(false);
                            timer.setVisible(false);
                        }
                    } else {
                        Log.d("","");
                    }
                }
            }
        }
    }

    public void removeOverlay(LatLng loc) {
        final GroundOverlay overlay = mOverlays.get(loc);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mOverlays.remove(overlay.getPosition());
                    overlay.remove();

                    Marker timer = mCountdowns.get(overlay);
                    timer.remove();
                    mCountdowns.remove(overlay);
                } catch (NullPointerException e) {
                    // ignore
                }
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
