package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Pidgey extends Pokemon {

    public Pidgey(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Pidgey.class.getName();
        super.mResource = R.drawable.p16;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Pidgey;
    }
}
