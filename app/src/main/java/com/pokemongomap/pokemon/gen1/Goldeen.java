package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Goldeen extends Pokemon {

    public Goldeen(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Goldeen.class.getName();
        super.mResource = R.drawable.p118;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Goldeen;
    }
}
