package com.xyxl.tianyingn3.bean;

/**
 * Created by Administrator on 2017/11/16 15:38
 * Version : V1.0
 * Introductions :
 */

public class MyPosition {
    public static MyPosition instance;
    public static MyPosition getInstance()
    {
        if (instance == null) {
            instance = new MyPosition();
        }
        return instance;
    }

    private double myLon, myLat;
    private float myDir, mySpeed, myAnt, myAcc;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getMyLon() {
        return myLon;
    }

    public void setMyLon(double myLon) {
        this.myLon = myLon;
    }

    public double getMyLat() {
        return myLat;
    }

    public void setMyLat(double myLat) {
        this.myLat = myLat;
    }

    public float getMyDir() {
        return myDir;
    }

    public void setMyDir(float myDir) {
        this.myDir = myDir;
    }

    public float getMySpeed() {
        return mySpeed;
    }

    public void setMySpeed(float mySpeed) {
        this.mySpeed = mySpeed;
    }

    public float getMyAnt() {
        return myAnt;
    }

    public void setMyAnt(float myAnt) {
        this.myAnt = myAnt;
    }

    public float getMyAcc() {
        return myAcc;
    }

    public void setMyAcc(float myAcc) {
        this.myAcc = myAcc;
    }
}
