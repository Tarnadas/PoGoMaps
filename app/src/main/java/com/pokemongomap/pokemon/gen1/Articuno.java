package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Articuno extends Pokemon {

    public Articuno(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Articuno.class.getName();
        super.mResource = R.drawable.p144;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Articuno;
    }
}
