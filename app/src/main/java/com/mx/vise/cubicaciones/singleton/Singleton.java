package com.mx.vise.cubicaciones.singleton;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el jueves 27 de septiembre del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreosandroid
 */

public class Singleton {

    private static Singleton instance;
    private String rearLicencePlateByOCR;
    private String frontLicencePlateByOCR;
    private String imei;

    public static synchronized Singleton getInstance(){
        return instance == null ? instance = new Singleton() : instance;
    }

    public String getRearLicencePlateByOCR() {
        return rearLicencePlateByOCR;
    }

    public void setRearLicencePlateByOCR(String rearLicencePlateByOCR) {
        this.rearLicencePlateByOCR = rearLicencePlateByOCR;
    }

    public String getFrontLicencePlateByOCR() {
        return frontLicencePlateByOCR;
    }

    public void setFrontLicencePlateByOCR(String frontLicencePlateByOCR) {
        this.frontLicencePlateByOCR = frontLicencePlateByOCR;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
