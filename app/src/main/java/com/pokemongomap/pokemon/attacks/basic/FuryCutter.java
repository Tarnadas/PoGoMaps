package com.pokemongomap.pokemon.attacks.basic;

import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.BasicAttack;

public class FuryCutter extends BasicAttack {

    public FuryCutter() {
        super(200, 3, 400, 6, TypeModifier.Type.BUG);
    }

}
