package com.pokemongomap.pokemon.gen1;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemon.TypeModifier;
import com.pokemongomap.pokemon.attacks.BasicAttack;
import com.pokemongomap.pokemon.attacks.ChargeAttack;
import com.pokemongomap.pokemon.attacks.basic.Acid;
import com.pokemongomap.pokemon.attacks.basic.Bite;
import com.pokemongomap.pokemon.attacks.basic.Bubble;
import com.pokemongomap.pokemon.attacks.basic.BugBite;
import com.pokemongomap.pokemon.attacks.basic.Confusion;
import com.pokemongomap.pokemon.attacks.basic.Ember;
import com.pokemongomap.pokemon.attacks.basic.MetalClaw;
import com.pokemongomap.pokemon.attacks.basic.MudShot;
import com.pokemongomap.pokemon.attacks.basic.Peck;
import com.pokemongomap.pokemon.attacks.basic.PoisonJab;
import com.pokemongomap.pokemon.attacks.basic.PoisonSting;
import com.pokemongomap.pokemon.attacks.basic.QuickAttack;
import com.pokemongomap.pokemon.attacks.basic.RazorLeaf;
import com.pokemongomap.pokemon.attacks.basic.Scratch;
import com.pokemongomap.pokemon.attacks.basic.Spark;
import com.pokemongomap.pokemon.attacks.basic.SteelWing;
import com.pokemongomap.pokemon.attacks.basic.Tackle;
import com.pokemongomap.pokemon.attacks.basic.ThunderShock;
import com.pokemongomap.pokemon.attacks.basic.VineWhip;
import com.pokemongomap.pokemon.attacks.basic.WaterGun;
import com.pokemongomap.pokemon.attacks.basic.WingAttack;
import com.pokemongomap.pokemon.attacks.charge.AerialAce;
import com.pokemongomap.pokemon.attacks.charge.AirCutter;
import com.pokemongomap.pokemon.attacks.charge.AquaJet;
import com.pokemongomap.pokemon.attacks.charge.AquaTail;
import com.pokemongomap.pokemon.attacks.charge.BodySlam;
import com.pokemongomap.pokemon.attacks.charge.BrickBreak;
import com.pokemongomap.pokemon.attacks.charge.BugBuzz;
import com.pokemongomap.pokemon.attacks.charge.Bulldoze;
import com.pokemongomap.pokemon.attacks.charge.DarkPulse;
import com.pokemongomap.pokemon.attacks.charge.Dig;
import com.pokemongomap.pokemon.attacks.charge.Discharge;
import com.pokemongomap.pokemon.attacks.charge.DragonClaw;
import com.pokemongomap.pokemon.attacks.charge.DrillPeck;
import com.pokemongomap.pokemon.attacks.charge.DrillRun;
import com.pokemongomap.pokemon.attacks.charge.Earthquake;
import com.pokemongomap.pokemon.attacks.charge.FireBlast;
import com.pokemongomap.pokemon.attacks.charge.FirePunch;
import com.pokemongomap.pokemon.attacks.charge.FlameBurst;
import com.pokemongomap.pokemon.attacks.charge.FlameCharge;
import com.pokemongomap.pokemon.attacks.charge.Flamethrower;
import com.pokemongomap.pokemon.attacks.charge.FlashCannon;
import com.pokemongomap.pokemon.attacks.charge.GunkShot;
import com.pokemongomap.pokemon.attacks.charge.Hurricane;
import com.pokemongomap.pokemon.attacks.charge.HydroPump;
import com.pokemongomap.pokemon.attacks.charge.HyperBeam;
import com.pokemongomap.pokemon.attacks.charge.HyperFang;
import com.pokemongomap.pokemon.attacks.charge.IceBeam;
import com.pokemongomap.pokemon.attacks.charge.PowerWhip;
import com.pokemongomap.pokemon.attacks.charge.Psychic;
import com.pokemongomap.pokemon.attacks.charge.RockSlide;
import com.pokemongomap.pokemon.attacks.charge.RockTomb;
import com.pokemongomap.pokemon.attacks.charge.SeedBomb;
import com.pokemongomap.pokemon.attacks.charge.SignalBeam;
import com.pokemongomap.pokemon.attacks.charge.SludgeBomb;
import com.pokemongomap.pokemon.attacks.charge.SludgeWave;
import com.pokemongomap.pokemon.attacks.charge.SolarBeam;
import com.pokemongomap.pokemon.attacks.charge.Struggle;
import com.pokemongomap.pokemon.attacks.charge.Thunder;
import com.pokemongomap.pokemon.attacks.charge.ThunderPunch;
import com.pokemongomap.pokemon.attacks.charge.Thunderbolt;
import com.pokemongomap.pokemon.attacks.charge.Twister;
import com.pokemongomap.pokemon.attacks.charge.WaterPulse;
import com.pokemongomap.pokemon.attacks.charge.Wrap;
import com.pokemongomap.pokemon.attacks.charge.XScissor;
import com.pokemongomap.pokemongomap.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Sandslash extends Pokemon {

    private static int HP_RATIO = 150;
    private static int ATTACK_RATIO = 150;
    private static int DEFENSE_RATIO = 172;
    private static int MIN_CP = 1505;
    private static int MAX_CP = 1810;

    private static List<BasicAttack> BASE_ATTACKS = new ArrayList<>();
    private static BasicAttack BASE_ATTACK_1 = new MetalClaw();
    private static BasicAttack BASE_ATTACK_2 = new MudShot();
    private static List<ChargeAttack> CHARGE_ATTACKS = new ArrayList<>();
    private static ChargeAttack CHARGE_ATTACK_1 = new Bulldoze();
    private static ChargeAttack CHARGE_ATTACK_2 = new Earthquake();
    private static ChargeAttack CHARGE_ATTACK_3 = new RockTomb();

    public Sandslash() {
        super();
        super.mResource = R.drawable.p28;
        super.HP_RATIO = HP_RATIO;
        super.ATTACK_RATIO = ATTACK_RATIO;
        super.DEFENSE_RATIO = DEFENSE_RATIO;
        super.MIN_CP = MIN_CP;
        super.MAX_CP = MAX_CP;
        super.TYPE = TypeModifier.Type.GROUND;
        super.TYPE_SECONDARY = TypeModifier.Type.NONE;

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

    public Sandslash(int id, LatLng loc, Date disappearTime) {
        super(id, loc, disappearTime);
        super.mName = Sandslash.class.getName();
        super.mResource = R.drawable.p28;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof Sandslash;
    }
}
