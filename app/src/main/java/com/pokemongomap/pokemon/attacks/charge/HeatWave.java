package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class HeatWave extends ChargeAttack {

    public HeatWave() {
        super(42, 80, 3800, 100, 5.f, TypeModifier.FIRE);
    }

}
