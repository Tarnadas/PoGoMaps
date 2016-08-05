package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class Discharge extends ChargeAttack {

    public Discharge() {
        super(35, 35, 2500, 33, 5.f, TypeModifier.Type.ELECTRIC);
    }

}
