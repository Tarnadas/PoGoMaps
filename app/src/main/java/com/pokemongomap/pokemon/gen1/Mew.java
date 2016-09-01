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

public class Mew extends Pokemon {

    private static int HP_RATIO = 200;
    private static int ATTACK_RATIO = 220;
    private static int DEFENSE_RATIO = 220;
    private static int MIN_CP = 2882;
    private static int MAX_CP = 3299;

    private static List<BasicAttack> BASE_ATTACKS = new ArrayList<>();
    private static BasicAttack BASE_ATTACK_1 = new Pound();
    private static BasicAttack BASE_ATTACK_2;
    private static List<ChargeAttack> CHARGE_ATTACKS = new ArrayList<>();
    private static ChargeAttack CHARGE_ATTACK_1 = new DragonPulse();
    private static ChargeAttack CHARGE_ATTACK_2 = new Earthquake();
    private static ChargeAttack CHARGE_ATTACK_3 = new FireBlast();

    public Mew(Context context, int id) {
        super(context, id);
        super.mResource = R.drawable.p151;
        super.HP_RATIO = HP_RATIO;
        super.ATTACK_RATIO = ATTACK_RATIO;
        super.DEFENSE_RATIO = DEFENSE_RATIO;
        super.MIN_CP = MIN_CP;
        super.MAX_CP = MAX_CP;
        super.TYPE = TypeModifier.Type.PSYCHIC;
        super.TYPE_SECONDARY = TypeModifier.Type.NONE;

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
        CHARGE_ATTACKS.add(new HyperBeam());
        CHARGE_ATTACKS.add(new Moonblast());
        CHARGE_ATTACKS.add(new Psychic());
        CHARGE_ATTACKS.add(new SolarBeam());
        CHARGE_ATTACKS.add(new Thunderbolt());
        CHARGE_ATTACKS.add(new Hurricane());
        super.CHARGE_ATTACKS = CHARGE_ATTACKS;
    }

    public Mew(Context context, int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = context.getString(R.string.mew);
        super.mResource = R.drawable.p151;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Mew;
    }
}
