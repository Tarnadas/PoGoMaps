package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Weezing extends Pokemon {

    public Weezing(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Weezing.class.getName();
        super.mResource = R.drawable.p110;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Weezing;
    }
}
