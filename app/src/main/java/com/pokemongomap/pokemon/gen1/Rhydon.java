package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Rhydon extends Pokemon {

    public Rhydon(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Rhydon.class.getName();
        super.mResource = R.drawable.p112;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Rhydon;
    }
}
