package com.mx.vise.uhf.tag;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el lunes 04 de marzo del 2019 a las 09:17
 *
 * @author Angelo de Jesus Loza Martinez
 * @version cubicaciones
 */
public class Sector {
    private String data;
    private int offset;
    private int length;
    private boolean successWriting;

    public Sector(){}

    public Sector(String data, int offset, int length){
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public String getData() {
        return data;
    }

    public Sector setData(String data) {
        this.data = data;
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public Sector setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public int getLength() {
        return length;
    }

    public Sector setLength(int length) {
        this.length = length;
        return this;
    }


    public boolean isSuccessWriting() {
        return successWriting;
    }

    public void setSuccessWriting(boolean successWriting) {
        this.successWriting = successWriting;
    }
}
