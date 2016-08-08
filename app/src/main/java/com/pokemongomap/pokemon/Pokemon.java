package com.pokemongomap.pokemon;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.attacks.Attacks;
import com.pokemongomap.pokemon.attacks.BasicAttack;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Pokemon {

    private static final double STAB_MODIFIER = 1.25;
    private static final double CRIT_MODIFIER = 1.5; // unsure
    private static final double CHARGE_TIME = 500;
    private static final double CHARGE_TIME_DEFENSE = 2000;
    private static final double ENERGY_PENALTY_DEFENSE = 1.d;

    private LatLng mLoc;
    private Date mDisappearTime;
    protected int mResource;


    private int mId;
    protected String mName;
    protected int HP_RATIO;
    protected int ATTACK_RATIO;
    protected int DEFENSE_RATIO;
    protected int MIN_CP;
    protected int MAX_CP;
    protected TypeModifier.Type TYPE;
    protected TypeModifier.Type TYPE_SECONDARY;

    protected List<BasicAttack> BASE_ATTACKS;
    protected List<ChargeAttack> CHARGE_ATTACKS;

    private Map<Attacks, Double> mDpsOffense;
    private Map<Attacks, Double> mDpsDefense;



    protected int mHp;
    protected int mAttack;
    protected int mDefense;
    protected int mCp;

    protected Attacks mAttacks;

    public Pokemon() {

    }

    public Pokemon(Context context, int id) {
        mId = id;
        String identifier = Thread.currentThread().getStackTrace()[3].getClassName().split("\\.")[4].toLowerCase();
        mName = context.getString(context.getResources().getIdentifier(identifier, "string", context.getPackageName()));
    }

    public Pokemon(int id, LatLng loc, Date disappearTime) {
        mId = id;
        mLoc = loc;
        mDisappearTime = disappearTime;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public LatLng getLocation() {
        return  mLoc;
    }

    public Date getDisappearTime() {
        return mDisappearTime;
    }

    public int getResource() {
        return mResource;
    }

    public int getHp() {
        return mHp;
    }

    public int getAttack() {
        return mAttack;
    }

    public int getDefense() {
        return mDefense;
    }

    public int getCp() {
        return mCp;
    }

    public BasicAttack getBasicAttack() {
        return mAttacks.getBasicAttack();
    }

    public ChargeAttack getChargeAttack() {
        return mAttacks.getChargeAttack();
    }

    public int getHpRatio() {
        return HP_RATIO;
    }

    public int getAttackRatio() {
        return ATTACK_RATIO;
    }

    public int getDefenseRatio() {
        return DEFENSE_RATIO;
    }

    public int getMinCp() {
        return MIN_CP;
    }

    public int getMaxCp() {
        return MAX_CP;
    }

    public TypeModifier.Type getType() {
        return TYPE;
    }

    public TypeModifier.Type getTypeSecondary() {
        return TYPE_SECONDARY;
    }

    public List<BasicAttack> getBaseAttacks() {
        return BASE_ATTACKS;
    }

    public List<ChargeAttack> getChargeAttacks() {
        return CHARGE_ATTACKS;
    }

    public void createDpsOffense() {
        mDpsOffense = new HashMap<>();
        for (BasicAttack basicAttack : BASE_ATTACKS) {
            double basicDamage = (double)basicAttack.getPower();
            if (basicAttack.getType().equals(TYPE) || basicAttack.getType().equals(TYPE_SECONDARY)) {
                basicDamage *= STAB_MODIFIER;
            }
            double damagePerFight = 100000.f / (double)basicAttack.getSpeed() * basicDamage;
            mDpsOffense.put(new Attacks(basicAttack, null), damagePerFight);
            for (ChargeAttack chargeAttack : CHARGE_ATTACKS) {
                basicDamage = (double)basicAttack.getPower();
                if (basicAttack.getType().equals(TYPE) || basicAttack.getType().equals(TYPE_SECONDARY)) {
                    basicDamage *= STAB_MODIFIER;
                }
                double chargeDamage = (double)chargeAttack.getPower() * (1 + CRIT_MODIFIER * (chargeAttack.getCritChance() / 100));
                if (chargeAttack.getType().equals(TYPE) || chargeAttack.getType().equals(TYPE_SECONDARY)) {
                    chargeDamage *= STAB_MODIFIER;
                }
                double avgMovesToFillEnergy = (double)chargeAttack.getEnergyCost() / (double)basicAttack.getEnergy();
                double damagePerCycle = basicDamage * avgMovesToFillEnergy + chargeDamage;
                double timePerCycleInMillis = (double)basicAttack.getSpeed() * avgMovesToFillEnergy + (double)chargeAttack.getSpeed() + CHARGE_TIME;
                double cyclesPerFight = 100000.d / timePerCycleInMillis;
                damagePerFight = cyclesPerFight * damagePerCycle;
                mDpsOffense.put(new Attacks(basicAttack, chargeAttack), damagePerFight);
            }
        }
    }

    public double getMaxDpsOffense() {
        double maxDps = 0.f;
        for (Double dps : mDpsOffense.values()) {
            if (dps > maxDps) {
                maxDps = dps;
            }
        }
        return maxDps;
    }

    public int getGymOffense() {
        return ((Double)((getMaxDpsOffense() * ATTACK_RATIO * HP_RATIO * DEFENSE_RATIO) / 1000000)).intValue();
    }

    public void createDpsDefense() {
        mDpsDefense = new HashMap<>();
        for (BasicAttack basicAttack : BASE_ATTACKS) {
            for (ChargeAttack chargeAttack : CHARGE_ATTACKS) {
                double basicDamage = (double)basicAttack.getPower();
                if (basicAttack.getType().equals(TYPE) || basicAttack.getType().equals(TYPE_SECONDARY)) {
                    basicDamage *= STAB_MODIFIER;
                }
                double chargeDamage = (double)chargeAttack.getPower();
                if (chargeAttack.getType().equals(TYPE) || chargeAttack.getType().equals(TYPE_SECONDARY)) {
                    chargeDamage *= STAB_MODIFIER;
                }
                double avgMovesToFillEnergy = ((double)chargeAttack.getEnergyCost() * ENERGY_PENALTY_DEFENSE) / (double)basicAttack.getEnergy();
                double damagePerCycle = basicDamage * avgMovesToFillEnergy + chargeDamage;
                double timePerCycleInMillis = ((double)basicAttack.getSpeed() + CHARGE_TIME_DEFENSE) * avgMovesToFillEnergy + (double)chargeAttack.getSpeed() + CHARGE_TIME_DEFENSE;
                double cyclesPerFight = 100000.d / timePerCycleInMillis;
                double damagePerFight = cyclesPerFight * damagePerCycle;
                mDpsDefense.put(new Attacks(basicAttack, chargeAttack), damagePerFight);
            }
        }
    }

    public double getMaxDpsDefense() {
        double maxDps = 0.f;
        for (Double dps : mDpsDefense.values()) {
            if (dps > maxDps) {
                maxDps = dps;
            }
        }
        return maxDps;
    }

    public int getGymDefense() {
        return ((Double)((getMaxDpsDefense() * ATTACK_RATIO * HP_RATIO * DEFENSE_RATIO) / 1000000)).intValue();
    }

    @Override
    public boolean equals(Object object) {
        try {
            Pokemon pokemon = (Pokemon) object;
            return this.mId == pokemon.mId && this.mName.equals(pokemon.mName) && this.mLoc.equals(pokemon.mLoc) && this.mDisappearTime.equals(pokemon.mDisappearTime);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDisappearTime);
        int hours = cal.get(Calendar.HOUR_OF_DAY) << 12;
        int minutes = cal.get(Calendar.MINUTE) << 6;
        int seconds = cal.get(Calendar.SECOND);
        return (this.mId << 17) + hours + minutes + seconds;
    }

}
