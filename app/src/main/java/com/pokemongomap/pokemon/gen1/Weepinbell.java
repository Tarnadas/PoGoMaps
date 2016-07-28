package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Weepinbell extends Pokemon {

    public Weepinbell(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Weepinbell.class.getName();
        super.mResource = R.drawable.p70;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Weepinbell;
    }
}
