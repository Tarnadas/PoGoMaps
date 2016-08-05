package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class HydroPump extends ChargeAttack {

    public HydroPump() {
        super(107, 90, 3800, 100, 5.f, TypeModifier.Type.WATER);
    }

}
