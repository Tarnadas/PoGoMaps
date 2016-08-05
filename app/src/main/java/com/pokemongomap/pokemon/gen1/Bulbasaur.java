package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.model.people.Person;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.BasicAttack;
import com.pokemongomap.pokemon.attacks.ChargeAttack;
import com.pokemongomap.pokemon.attacks.basic.Tackle;
import com.pokemongomap.pokemon.attacks.basic.VineWhip;
import com.pokemongomap.pokemon.attacks.charge.PowerWhip;
import com.pokemongomap.pokemon.attacks.charge.SeedBomb;
import com.pokemongomap.pokemon.attacks.charge.SludgeBomb;
import com.pokemongomap.pokemongomap.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Bulbasaur extends Pokemon {

    private static int HP_RATIO = 90;
    private static int ATTACK_RATIO = 126;
    private static int DEFENSE_RATIO = 126;
    private static int MIN_CP = 838;
    private static int MAX_CP = 1072;

    private static List<BasicAttack> BASE_ATTACKS;
    private static BasicAttack BASE_ATTACK_1 = new Tackle();
    private static BasicAttack BASE_ATTACK_2 = new VineWhip();
    private static List<ChargeAttack> CHARGE_ATTACKS = new ArrayList<>();
    private static ChargeAttack CHARGE_ATTACK_1 = new PowerWhip();
    private static ChargeAttack CHARGE_ATTACK_2 = new SeedBomb();
    private static ChargeAttack CHARGE_ATTACK_3 = new SludgeBomb();

    public Bulbasaur() {
        super();
        super.mResource = R.drawable.p1;
        super.HP_RATIO = HP_RATIO;
        super.ATTACK_RATIO = ATTACK_RATIO;
        super.DEFENSE_RATIO = DEFENSE_RATIO;
        super.MIN_CP = MIN_CP;
        super.MAX_CP = MAX_CP;
        super.TYPE = TypeModifier.Type.GRASS;
        super.TYPE_SECONDARY = TypeModifier.Type.POISON;

        BASE_ATTACKS = new ArrayList<>();
        BASE_ATTACKS.add(BASE_ATTACK_1);
        if (BASE_ATTACK_1 != null) {
            BASE_ATTACKS.add(BASE_ATTACK_2);
        }
        super.BASE_ATTACKS = BASE_ATTACKS;
        CHARGE_ATTACKS = new ArrayList<>();
        CHARGE_ATTACKS.add(CHARGE_ATTACK_1);
        if (CHARGE_ATTACK_2 != null) {
            CHARGE_ATTACKS.add(CHARGE_ATTACK_2);
        }
        if (CHARGE_ATTACK_3 != null) {
            CHARGE_ATTACKS.add(CHARGE_ATTACK_3);
        }
        super.CHARGE_ATTACKS = CHARGE_ATTACKS;
    }

    public Bulbasaur(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = "Bulbasaur";
        super.mResource = R.drawable.p1;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Bulbasaur;
    }
}
