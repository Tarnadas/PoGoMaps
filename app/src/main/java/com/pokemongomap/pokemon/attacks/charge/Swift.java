package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class Swift extends ChargeAttack {

    public Swift() {
        super(125, 30, 3000, 25, 5.f, TypeModifier.NORMAL);
    }

}
