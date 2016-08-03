package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class FlameBurst extends ChargeAttack {

    public FlameBurst() {
        super(30, 2100, 25, 5.f, TypeModifier.FIRE);
    }

}
