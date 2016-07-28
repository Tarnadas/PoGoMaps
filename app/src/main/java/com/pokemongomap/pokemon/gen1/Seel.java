package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Seel extends Pokemon {

    public Seel(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Seel.class.getName();
        super.mResource = R.drawable.p86;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Seel;
    }
}
