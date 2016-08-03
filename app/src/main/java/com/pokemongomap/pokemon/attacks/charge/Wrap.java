package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.BasicAttack;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class Wrap extends ChargeAttack {

    public Wrap() {
        super(25, 4000, 20, 5.f, TypeModifier.NORMAL);
    }

}
