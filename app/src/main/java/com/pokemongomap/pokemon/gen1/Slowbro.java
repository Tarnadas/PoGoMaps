package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Slowbro extends Pokemon {

    public Slowbro(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Slowbro.class.getName();
        super.mResource = R.drawable.p80;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Slowbro;
    }
}
