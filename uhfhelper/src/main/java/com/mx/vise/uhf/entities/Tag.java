package com.mx.vise.uhf.entities;


/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el jueves 26 de julio del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version UHFTAGREAD
 */

public class Tag {

    private String wel;
    private String killKey;
    private String epc;
    private String tid;
    private String userData;
    private UserDataStatus tagReadStatus;
    private String tagFlags;


    public String getAP() {
        return wel;
    }

    public Tag setAP(String wel) {
        this.wel = wel;
        return this;
    }

    public Tag setAP(Key join) {
        this.wel = join.key;
        return this;
    }

    public String getKillKey() {
        return killKey;
    }

    public Tag setKillKey(String killKey) {
        this.killKey = killKey;
        return this;
    }

    public String getEpc() {
        return epc;
    }

    public Tag setEPC(String epc) {
        this.epc = epc;
        return this;
    }

    public String getTid() {
        return tid;
    }

    public Tag setTid(String tid) {
        this.tid = tid;
        return this;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public UserDataStatus getTagReadStatus() {
        return tagReadStatus;
    }

    public void setTagReadStatus(UserDataStatus tagReadStatus) {
        this.tagReadStatus = tagReadStatus;
    }

    public String getTagFlags() {
        return tagFlags;
    }

    public void setTagFlags(String tagFlags) {
        this.tagFlags = tagFlags;
    }
}
