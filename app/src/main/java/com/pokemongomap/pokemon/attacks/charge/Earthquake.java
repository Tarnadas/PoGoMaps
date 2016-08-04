package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class Earthquake extends ChargeAttack {

    public Earthquake() {
        super(31, 100, 4200, 100, 5.f, TypeModifier.GROUND);
    }

}
