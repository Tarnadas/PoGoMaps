package com.pokemongomap.pokemon.attacks;

public class Attacks {

    private BasicAttack mBasicAttack;
    private ChargeAttack mChargeAttack;

    public Attacks(BasicAttack basicAttack, ChargeAttack chargeAttack) {
        mBasicAttack = basicAttack;
        mChargeAttack = chargeAttack;
    }

    public BasicAttack getBasicAttack() {
        return mBasicAttack;
    }

    public ChargeAttack getChargeAttack() {
        return mChargeAttack;
    }

    @Override
    public int hashCode() {
        return mBasicAttack.getId() + (mChargeAttack.getId() << 16);
    }
}
