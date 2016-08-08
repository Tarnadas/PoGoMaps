package com.pokemongomap.pokemon.gen1;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.BasicAttack;
import com.pokemongomap.pokemon.attacks.ChargeAttack;
import com.pokemongomap.pokemon.attacks.basic.*;
import com.pokemongomap.pokemon.attacks.charge.*;
import com.pokemongomap.pokemongomap.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Oddish extends Pokemon {

    private static int HP_RATIO = 90;
    private static int ATTACK_RATIO = 134;
    private static int DEFENSE_RATIO = 130;
    private static int MIN_CP = 905;
    private static int MAX_CP = 1148;

    private static List<BasicAttack> BASE_ATTACKS = new ArrayList<>();
    private static BasicAttack BASE_ATTACK_1 = new Acid();
    private static BasicAttack BASE_ATTACK_2 = new RazorLeaf();
    private static List<ChargeAttack> CHARGE_ATTACKS = new ArrayList<>();
    private static ChargeAttack CHARGE_ATTACK_1 = new Moonblast();
    private static ChargeAttack CHARGE_ATTACK_2 = new SeedBomb();
    private static ChargeAttack CHARGE_ATTACK_3 = new SludgeWave();

    public Oddish(Context context, int id) {
        super(context, id);
        super.mResource = R.drawable.p43;
        super.HP_RATIO = HP_RATIO;
        super.ATTACK_RATIO = ATTACK_RATIO;
        super.DEFENSE_RATIO = DEFENSE_RATIO;
        super.MIN_CP = MIN_CP;
        super.MAX_CP = MAX_CP;
        super.TYPE = TypeModifier.Type.GRASS;
        super.TYPE_SECONDARY = TypeModifier.Type.POISON;

        BASE_ATTACKS = new ArrayList<>();
        BASE_ATTACKS.add(BASE_ATTACK_1);
        if (BASE_ATTACK_2 != null) {
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

    public Oddish(Context context, int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = context.getString(R.string.oddish);
        super.mResource = R.drawable.p43;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Oddish;
    }
}
