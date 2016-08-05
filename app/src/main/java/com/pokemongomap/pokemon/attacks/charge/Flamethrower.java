package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class Flamethrower extends ChargeAttack {

    public Flamethrower() {
        super(24, 55, 2900, 50, 5.f, TypeModifier.Type.FIRE);
    }

}
