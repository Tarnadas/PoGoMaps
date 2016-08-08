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

public class Tentacool extends Pokemon {

    private static int HP_RATIO = 80;
    private static int ATTACK_RATIO = 106;
    private static int DEFENSE_RATIO = 136;
    private static int MIN_CP = 691;
    private static int MAX_CP = 905;

    private static List<BasicAttack> BASE_ATTACKS = new ArrayList<>();
    private static BasicAttack BASE_ATTACK_1 = new Bubble();
    private static BasicAttack BASE_ATTACK_2 = new PoisonSting();
    private static List<ChargeAttack> CHARGE_ATTACKS = new ArrayList<>();
    private static ChargeAttack CHARGE_ATTACK_1 = new BubbleBeam();
    private static ChargeAttack CHARGE_ATTACK_2 = new WaterPulse();
    private static ChargeAttack CHARGE_ATTACK_3 = new Wrap();

    public Tentacool(Context context, int id) {
        super(context, id);
        super.mResource = R.drawable.p72;
        super.HP_RATIO = HP_RATIO;
        super.ATTACK_RATIO = ATTACK_RATIO;
        super.DEFENSE_RATIO = DEFENSE_RATIO;
        super.MIN_CP = MIN_CP;
        super.MAX_CP = MAX_CP;
        super.TYPE = TypeModifier.Type.WATER;
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

    public Tentacool(Context context, int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = context.getString(R.string.tentacool);
        super.mResource = R.drawable.p72;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Tentacool;
    }
}
