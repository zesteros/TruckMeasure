package com.mx.vise.uhf.entities;


import com.mx.vise.uhf.constants.HexAP;

public class Key {

    public String key;

    public Key(String string) {
        this.key = HexAP.DEFAULT;
    }

    public Key() {
        this.key = HexAP.AP;
    }
}
