package com.pokemongomap.pokemon.attacks.charge;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

public class Blizzard extends ChargeAttack {

    public Blizzard() {
        super(100, 3900, 100, 5.f, TypeModifier.ICE);
    }

}
