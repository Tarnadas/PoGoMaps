package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Onix extends Pokemon {

    public Onix(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Onix.class.getName();
        super.mResource = R.drawable.p95;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Onix;
    }
}
