package com.pokemongomap.searcher;

import com.pokegoapi.api.device.SensorInfos;

import java.util.Date;

public final class SensorFake implements SensorInfos {
    private static SensorFake mInstance = new SensorFake();
    private double mAccelNormX, mAccelNormY, mAccelNormZ, mAccelX, mAccelY, mAccelZ;
    private double mAngleNormX, mAngleNormY, mAngleNormZ;
    private double mGyroX, mGyroY, mGyroZ;
    private SensorFake() {
        mAccelNormX = Math.random();
        mAccelNormY = Math.random();
        mAccelNormZ = Math.random() * 0.01;
        mAccelX = Math.random();
        mAccelY = Math.random();
        mAccelZ = Math.random();
        mAngleNormX = Math.random();
        mAngleNormY = Math.random();
        mAngleNormZ = Math.random();
        mGyroX = Math.random();
        mGyroY = Math.random();
        mGyroZ = Math.random();
    }
    public static SensorFake getInstance() {
        return mInstance;
    }
    public static void update(double alpha, double distance) {
        mInstance.mAccelNormX = Math.random();
        mInstance.mAccelNormY = Math.random();
        mInstance.mAccelNormZ = Math.random() * 0.01;
        mInstance.mAccelX = Math.random();
        mInstance.mAccelY = Math.random();
        mInstance.mAccelZ = Math.random();
        mInstance.mAngleNormX = Math.random();
        mInstance.mAngleNormY = Math.random();
        mInstance.mAngleNormZ = Math.random();
        mInstance.mGyroX = Math.random();
        mInstance.mGyroY = Math.random();
        mInstance.mGyroZ = Math.random();
    }
    @Override
    public long getTimestampSnapshot() {
        return new Date().getTime();
    }
    @Override
    public long getAccelerometerAxes() {
        return 3;
    }
    @Override
    public double getAccelNormalizedX() {
        return mAccelNormX;
    }
    @Override
    public double getAccelNormalizedY() {
        return mAccelNormY;
    }
    @Override
    public double getAccelNormalizedZ() {
        return mAccelNormZ;
    }
    @Override
    public double getAccelRawX() {
        return mAccelX;
    }
    @Override
    public double getAccelRawY() {
        return mAccelY;
    }
    @Override
    public double getAccelRawZ() {
        return mAccelZ;
    }
    @Override
    public double getAngleNormalizedX() {
        return mAngleNormX;
    }
    @Override
    public double getAngleNormalizedY() {
        return mAngleNormY;
    }
    @Override
    public double getAngleNormalizedZ() {
        return mAngleNormZ;
    }
    @Override
    public double getGyroscopeRawX() {
        return mGyroX;
    }
    @Override
    public double getGyroscopeRawY() {
        return mGyroY;
    }
    @Override
    public double getGyroscopeRawZ() {
        return mGyroZ;
    }
}
