package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Tauros extends Pokemon {

    public Tauros(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Tauros.class.getName();
        super.mResource = R.drawable.p128;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Tauros;
    }
}
