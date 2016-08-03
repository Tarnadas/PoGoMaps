package com.pokemongomap.pokemon.attacks;

import com.pokemongomap.pokemon.TypeModifier;

public class BasicAttack {

    private int mPower;
    private int mSpeed;
    private int mEnergy;
    private TypeModifier mType;
    private float mDamagePerSecond;
    private float mEnergyPerSecond;

    public BasicAttack(int power, int speed, int energy, TypeModifier type) {
        mPower = power;
        mSpeed = speed;
        mEnergy = energy;
        mType = type;
        mDamagePerSecond = mPower * 1000 / mSpeed;
        mEnergyPerSecond = mEnergy * 1000 / mSpeed;
    }

    public int getPower() {
        return mPower;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public TypeModifier getType() {
        return mType;
    }

    public float getDamagePerSecond() {
        return mDamagePerSecond;
    }

    public float getEnergyPerSecond() {
        return mEnergyPerSecond;
    }
}
