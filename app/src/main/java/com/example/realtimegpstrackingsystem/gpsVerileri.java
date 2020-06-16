package com.example.realtimegpstrackingsystem;

public class gpsVerileri {
    public float enlem, boylam;
    public int hiz;
    public String tarih, zaman;
    public gpsVerileri() {

    }
    public gpsVerileri(float enlem, float boylam, int hiz, String tarih, String zaman) {
        this.enlem = enlem;
        this.boylam = boylam;
        this.hiz = hiz;
        this.tarih = tarih;
        this.zaman = zaman;
    }
}
