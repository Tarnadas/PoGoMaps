package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemongomap.R;

import java.util.Date;

public class Vulpix extends Pokemon {

    public Vulpix(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Vulpix.class.getName();
        super.mResource = R.drawable.p37;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Vulpix;
    }
}
