package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class AirCutter extends ChargeAttack {

    public AirCutter() {
        super(121, 30, 3300, 25, 25.f, TypeModifier.Type.FLYING);
    }

}
