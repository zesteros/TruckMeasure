package com.mx.vise.cubicaciones.entities;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Signature implements Serializable {
    private byte[] signature;

    public Signature(byte[] bitmap){
        this.signature = bitmap;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}
