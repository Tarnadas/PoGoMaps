package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class PoisonFang extends ChargeAttack {

    public PoisonFang() {
        super(50, 25, 2400, 20, 5.f, TypeModifier.Type.POISON);
    }

}
