package com.mx.vise.cubicaciones.entities;

public class CubagePhotos {

    private String frontalPhoto;
    private String sidePhoto;
    private String backPhoto;
    private String rearPlate;
    private String frontalPlate;
    private String circulationCardPhoto;
    private String licenceCardPath;
    private String operatorSignaturePhoto;
    private String userSignaturePhoto;
    private String sheetNumber;

    public String getFrontalPhoto() {
        return frontalPhoto;
    }

    public void setFrontalPhoto(String frontalPhoto) {
        this.frontalPhoto = frontalPhoto;
    }

    public String getSidePhoto() {
        return sidePhoto;
    }

    public void setSidePhoto(String sidePhoto) {
        this.sidePhoto = sidePhoto;
    }

    public String getBackPhoto() {
        return backPhoto;
    }

    public void setBackPhoto(String backPhoto) {
        this.backPhoto = backPhoto;
    }

    public String getRearPlate() {
        return rearPlate;
    }

    public void setRearPlate(String rearPlate) {
        this.rearPlate = rearPlate;
    }

    public String getFrontalPlate() {
        return frontalPlate;
    }

    public void setFrontalPlate(String frontalPlate) {
        this.frontalPlate = frontalPlate;
    }

    public String getCirculationCardPhoto() {
        return circulationCardPhoto;
    }

    public void setCirculationCardPhoto(String circulationCardPhoto) {
        this.circulationCardPhoto = circulationCardPhoto;
    }

    public String getLicenceCardPath() {
        return licenceCardPath;
    }

    public void setLicenceCardPath(String licenceCardPath) {
        this.licenceCardPath = licenceCardPath;
    }

    public String getOperatorSignaturePhoto() {
        return operatorSignaturePhoto;
    }

    public void setOperatorSignaturePhoto(String operatorSignaturePhoto) {
        this.operatorSignaturePhoto = operatorSignaturePhoto;
    }

    public String getUserSignaturePhoto() {
        return userSignaturePhoto;
    }

    public void setUserSignaturePhoto(String userSignaturePhoto) {
        this.userSignaturePhoto = userSignaturePhoto;
    }

    public String getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(String sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    public String[] getPhotos() {
        return new String[]{
          getFrontalPhoto(),
          getSidePhoto(),
          getBackPhoto(),
          getFrontalPlate(),
          getRearPlate(),
          getLicenceCardPath(),
          getCirculationCardPhoto(),
          getOperatorSignaturePhoto(),
          getUserSignaturePhoto()
        };
    }
}
