package com.pokemongomap.pokemon.attacks;

import com.pokemongomap.pokemon.TypeModifier;

public class ChargeAttack {

    private int mId;
    private int mPower;
    private int mSpeed;
    private int mEnergyCost;
    private float mCritChance;
    private TypeModifier mType;

    public ChargeAttack(int id, int power, int speed, int energyCost, float critChance, TypeModifier type) {
        mId = id;
        mPower = power;
        mSpeed = speed;
        mEnergyCost = energyCost;
        mCritChance = critChance;
        mType = type;
    }

    public int getId() {
        return mId;
    }

    public int getPower() {
        return mPower;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public int getEnergyCost() {
        return mEnergyCost;
    }

    public float getCritChance() {
        return mCritChance;
    }

    public TypeModifier getType() {
        return mType;
    }
}
