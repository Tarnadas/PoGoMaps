package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class Stomp extends ChargeAttack {

    public Stomp() {
        super(127, 30, 2100, 25, 5.f, TypeModifier.Type.NORMAL);
    }

}
