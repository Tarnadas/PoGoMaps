package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class HyperBeam extends ChargeAttack {

    public HyperBeam() {
        super(14, 120, 5000, 100, 5.f, TypeModifier.Type.NORMAL);
    }

}
