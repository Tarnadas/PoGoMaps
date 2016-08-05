package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class ShadowBall extends ChargeAttack {

    public ShadowBall() {
        super(70, 45, 3080, 33, 5.f, TypeModifier.Type.GHOST);
    }

}
