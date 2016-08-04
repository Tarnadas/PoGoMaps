package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class Psybeam extends ChargeAttack {

    public Psybeam() {
        super(30, 40, 3800, 25, 5.f, TypeModifier.PSYCHIC);
    }

}
