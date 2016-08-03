package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemon.attacks.BasicAttack;
import com.pokemongomap.pokemon.attacks.ChargeAttack;
import com.pokemongomap.pokemon.attacks.basic.RazorLeaf;
import com.pokemongomap.pokemon.attacks.basic.Tackle;
import com.pokemongomap.pokemon.attacks.basic.VineWhip;
import com.pokemongomap.pokemon.attacks.charge.PetalBlizzard;
import com.pokemongomap.pokemon.attacks.charge.PowerWhip;
import com.pokemongomap.pokemon.attacks.charge.SeedBomb;
import com.pokemongomap.pokemon.attacks.charge.SludgeBomb;
import com.pokemongomap.pokemon.attacks.charge.SolarBeam;
import com.pokemongomap.pokemongomap.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Venusaur extends Pokemon {

    private static int HP_RATIO = 160;
    private static int ATTACK_RATIO = 198;
    private static int DEFENSE_RATIO = 200;
    private static int MIN_CP = 2212;
    private static int MAX_CP = 2580;

    private static List<BasicAttack> BASE_ATTACKS = new ArrayList<>();
    private static BasicAttack BASE_ATTACK_1 = new RazorLeaf();
    private static BasicAttack BASE_ATTACK_2 = new VineWhip();
    private static List<ChargeAttack> CHARGE_ATTACKS = new ArrayList<>();
    private static ChargeAttack CHARGE_ATTACK_1 = new PetalBlizzard();
    private static ChargeAttack CHARGE_ATTACK_2 = new SludgeBomb();
    private static ChargeAttack CHARGE_ATTACK_3 = new SolarBeam();

    public Venusaur() {
        super();
        super.HP_RATIO = HP_RATIO;
        super.ATTACK_RATIO = ATTACK_RATIO;
        super.DEFENSE_RATIO = DEFENSE_RATIO;
        super.MIN_CP = MIN_CP;
        super.MAX_CP = MAX_CP;

        BASE_ATTACKS.add(BASE_ATTACK_1);
        if (BASE_ATTACK_1 != null) {
            BASE_ATTACKS.add(BASE_ATTACK_2);
        }
        super.BASE_ATTACKS = BASE_ATTACKS;
        CHARGE_ATTACKS.add(CHARGE_ATTACK_1);
        if (CHARGE_ATTACK_2 != null) {
            CHARGE_ATTACKS.add(CHARGE_ATTACK_2);
        }
        if (CHARGE_ATTACK_3 != null) {
            CHARGE_ATTACKS.add(CHARGE_ATTACK_3);
        }
        super.BASE_ATTACKS = BASE_ATTACKS;
    }

    public Venusaur(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = "Venusaur";
        super.mResource = R.drawable.p3;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Venusaur;
    }
}
