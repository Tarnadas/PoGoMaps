package com.pokemongomap.pokemon;


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

    private static final float STAB_MODIFIER = 1.25f;

    private int mId;
    protected String mName;
    private LatLng mLoc;
    private Date mDisappearTime;
    protected int mResource;



    protected int HP_RATIO;
    protected int ATTACK_RATIO;
    protected int DEFENSE_RATIO;
    protected int MIN_CP;
    protected int MAX_CP;
    protected TypeModifier TYPE;
    protected TypeModifier TYPE_SECONDARY;

    protected List<BasicAttack> BASE_ATTACKS;
    protected List<ChargeAttack> CHARGE_ATTACKS;

    private Map<Attacks, Float> mDpsList;



    protected int mHp;
    protected int mAttack;
    protected int mDefense;
    protected int mCp;

    protected Attacks mAttacks;

    public Pokemon() {
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

    public List<BasicAttack> getBaseAttacks() {
        return BASE_ATTACKS;
    }

    public List<ChargeAttack> getChargeAttacks() {
        return CHARGE_ATTACKS;
    }

    public void createDpsMap() {
        mDpsList = new HashMap<>();
        for (BasicAttack basicAttack : BASE_ATTACKS) {
            for (ChargeAttack chargeAttack : CHARGE_ATTACKS) {
                float avgMovesToFillEnergy = (float)basicAttack.getEnergy() / (float)chargeAttack.getEnergyCost();
                float basicDamage = (float)basicAttack.getPower();
                if (basicAttack.getType().equals(TYPE) || basicAttack.getType().equals(TYPE_SECONDARY)) {
                    basicDamage *= STAB_MODIFIER;
                }
                float chargeDamage = (float)chargeAttack.getPower();
                if (chargeAttack.getType().equals(TYPE) || chargeAttack.getType().equals(TYPE_SECONDARY)) {
                    chargeDamage *= STAB_MODIFIER;
                }
                float damagePerCycle = basicDamage * avgMovesToFillEnergy + chargeDamage;
                float timePerCycleInMillis = (float)basicAttack.getSpeed() * avgMovesToFillEnergy + (float)chargeAttack.getSpeed();
                float cyclesPerFight = 100000.f / timePerCycleInMillis;
                float damagePerFight = cyclesPerFight * damagePerCycle;
                mDpsList.put(new Attacks(basicAttack, chargeAttack), damagePerFight);
            }
        }
    }

    public float getMaxDps() {
        float maxDps = 0.f;
        for (Float dps : mDpsList.values()) {
            if (dps > maxDps) {
                maxDps = dps;
            }
        }
        return maxDps;
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
