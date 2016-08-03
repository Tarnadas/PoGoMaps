package com.pokemongomap.pokemon;


import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.attacks.BasicAttack;
import com.pokemongomap.pokemon.attacks.ChargeAttack;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class Pokemon {

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

    protected List<BasicAttack> BASE_ATTACKS;
    protected List<ChargeAttack> CHARGE_ATTACKS;



    protected int mHp;
    protected int mAttack;
    protected int mDefense;
    protected int mCp;

    protected BasicAttack mBaseAttack;
    protected ChargeAttack mChargeAttack;

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

    public BasicAttack getBaseAttack() {
        return mBaseAttack;
    }

    public ChargeAttack getChargeAttack() {
        return mChargeAttack;
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
