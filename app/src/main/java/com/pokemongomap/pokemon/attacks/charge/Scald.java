package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class Scald extends ChargeAttack {

    public Scald() {
        super(106, 55, 4000, 33, 5.f, TypeModifier.Type.WATER);
    }

}
