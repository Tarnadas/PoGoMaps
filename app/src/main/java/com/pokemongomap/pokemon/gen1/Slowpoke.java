package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Slowpoke extends Pokemon {

    public Slowpoke(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Slowpoke.class.getName();
        super.mResource = R.drawable.p79;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Slowpoke;
    }
}
