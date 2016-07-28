package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Machamp extends Pokemon {

    public Machamp(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Machamp.class.getName();
        super.mResource = R.drawable.p68;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Machamp;
    }
}
