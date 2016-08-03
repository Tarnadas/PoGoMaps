package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class Thunder extends ChargeAttack {

    public Thunder() {
        super(100, 4300, 100, 5.f, TypeModifier.ELECTRIC);
    }

}
