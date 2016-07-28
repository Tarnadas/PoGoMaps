package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Hypno extends Pokemon {

    public Hypno(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Hypno.class.getName();
        super.mResource = R.drawable.p97;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Hypno;
    }
}
