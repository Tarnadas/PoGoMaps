package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Diglett extends Pokemon {

    public Diglett(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Diglett.class.getName();
        super.mResource = R.drawable.p50;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Diglett;
    }
}
