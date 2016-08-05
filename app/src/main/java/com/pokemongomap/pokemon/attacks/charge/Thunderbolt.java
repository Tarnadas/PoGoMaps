package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class Thunderbolt extends ChargeAttack {

    public Thunderbolt() {
        super(79, 55, 2700, 50, 5.f, TypeModifier.Type.ELECTRIC);
    }

}
