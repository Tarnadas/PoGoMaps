package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class Hurricane extends ChargeAttack {

    public Hurricane() {
        super(122, 80, 3200, 100, 5.f, TypeModifier.Type.FLYING);
    }

}
