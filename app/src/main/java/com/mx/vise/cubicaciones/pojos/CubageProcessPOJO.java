package com.mx.vise.cubicaciones.pojos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mx.vise.androidwscon.webservice.CustomCalendarDeserializer;
import com.mx.vise.androidwscon.webservice.CustomDateSerializer;
import com.mx.vise.androidwscon.webservice.JsonDate;
import com.mx.vise.androidwscon.webservice.UtilDate;

import java.io.Serializable;
import java.util.Date;

public class CubageProcessPOJO implements Serializable {
    private static final long serialVersionUID = -489630362837921187L;
    private String sheetNumber;
    private Integer idSyndicate;
    private String operator;
    private String idBuilding;
    private Byte[] rearPlatePhoto;
    private Byte[] frontPlatePhoto;
    private Byte[] licenseCardPhoto;
    private String frontalPlate;
    private String rearPlate;
    private String unitType;
    private String boxColor;
    private String tractorColor;
    private Integer idBrand;
    private Byte[] frontalUnitPhoto;
    private Byte[] sideUnitPhoto;
    private Byte[] backUnitPhoto;
    private String circulationCard;
    private Byte[] circulationCardPhoto;
    private Boolean hasIncrease;
    private Float l1;
    private Float a1;
    private Float h1;
    private Float l2;
    private Float a2;
    private Float h2;
    private Float lc;
    private Float l;
    private Float a;
    private Float h;
    private Float adjustment;
    private Float increase;
    private Byte[] operatorSignaturePhoto;
    private Byte[] userSignaturePhoto;
    private Integer addUser;
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomCalendarDeserializer.class)
    @JsonDate(formatKey = UtilDate.FORMAT_STANDAR_DATE_WITH_HR_MIN_SS_SSS)
    private Date addDate;
    private String syndicateName;
    private String observations;
    private String tag;
    private String brandName;
    private String frontPlateReadByOCR;
    private String rearPlateReadByOCR;
    private Float volumePipeOrGondola;
    //private Date startCaptureDate;
    //private Date endCaptureDate;

    public String getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(String sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    public Integer getIdSyndicate() {
        return idSyndicate;
    }

    public void setIdSyndicate(Integer idSyndicate) {
        this.idSyndicate = idSyndicate;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getIdBuilding() {
        return idBuilding;
    }

    public void setIdBuilding(String idBuilding) {
        this.idBuilding = idBuilding;
    }

    public Byte[] getRearPlatePhoto() {
        return rearPlatePhoto;
    }

    public void setRearPlatePhoto(Byte[] rearPlatePhoto) {
        this.rearPlatePhoto = rearPlatePhoto;
    }

    public Byte[] getFrontPlatePhoto() {
        return frontPlatePhoto;
    }

    public void setFrontPlatePhoto(Byte[] frontPlatePhoto) {
        this.frontPlatePhoto = frontPlatePhoto;
    }

    public Byte[] getLicenseCardPhoto() {
        return licenseCardPhoto;
    }

    public void setLicenseCardPhoto(Byte[] licenseCardPhoto) {
        this.licenseCardPhoto = licenseCardPhoto;
    }

    public String getFrontalPlate() {
        return frontalPlate;
    }

    public void setFrontalPlate(String frontalPlate) {
        this.frontalPlate = frontalPlate;
    }

    public String getRearPlate() {
        return rearPlate;
    }

    public void setRearPlate(String rearPlate) {
        this.rearPlate = rearPlate;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getBoxColor() {
        return boxColor;
    }

    public void setBoxColor(String boxColor) {
        this.boxColor = boxColor;
    }

    public String getTractorColor() {
        return tractorColor;
    }

    public void setTractorColor(String tractorColor) {
        this.tractorColor = tractorColor;
    }

    public Integer getIdBrand() {
        return idBrand;
    }

    public void setIdBrand(Integer idBrand) {
        this.idBrand = idBrand;
    }

    public Byte[] getFrontalUnitPhoto() {
        return frontalUnitPhoto;
    }

    public void setFrontalUnitPhoto(Byte[] frontalUnitPhoto) {
        this.frontalUnitPhoto = frontalUnitPhoto;
    }

    public Byte[] getSideUnitPhoto() {
        return sideUnitPhoto;
    }

    public void setSideUnitPhoto(Byte[] sideUnitPhoto) {
        this.sideUnitPhoto = sideUnitPhoto;
    }

    public Byte[] getBackUnitPhoto() {
        return backUnitPhoto;
    }

    public void setBackUnitPhoto(Byte[] backUnitPhoto) {
        this.backUnitPhoto = backUnitPhoto;
    }

    public String getCirculationCard() {
        return circulationCard;
    }

    public void setCirculationCard(String circulationCard) {
        this.circulationCard = circulationCard;
    }

    public Byte[] getCirculationCardPhoto() {
        return circulationCardPhoto;
    }

    public void setCirculationCardPhoto(Byte[] circulationCardPhoto) {
        this.circulationCardPhoto = circulationCardPhoto;
    }

    public Boolean getHasIncrease() {
        return hasIncrease;
    }

    public void setHasIncrease(Boolean hasIncrease) {
        this.hasIncrease = hasIncrease;
    }

    public Float getL1() {
        return l1;
    }

    public void setL1(Float l1) {
        this.l1 = l1;
    }

    public Float getA1() {
        return a1;
    }

    public void setA1(Float a1) {
        this.a1 = a1;
    }

    public Float getH1() {
        return h1;
    }

    public void setH1(Float h1) {
        this.h1 = h1;
    }

    public Float getL2() {
        return l2;
    }

    public void setL2(Float l2) {
        this.l2 = l2;
    }

    public Float getA2() {
        return a2;
    }

    public void setA2(Float a2) {
        this.a2 = a2;
    }

    public Float getH2() {
        return h2;
    }

    public void setH2(Float h2) {
        this.h2 = h2;
    }

    public Float getLc() {
        return lc;
    }

    public void setLc(Float lc) {
        this.lc = lc;
    }

    public Float getL() {
        return l;
    }

    public void setL(Float l) {
        this.l = l;
    }

    public Float getA() {
        return a;
    }

    public void setA(Float a) {
        this.a = a;
    }

    public Float getH() {
        return h;
    }

    public void setH(Float h) {
        this.h = h;
    }

    public Float getAdjustment() {
        return adjustment;
    }

    public void setAdjustment(Float adjustment) {
        this.adjustment = adjustment;
    }

    public Float getIncrease() {
        return increase;
    }

    public void setIncrease(Float increase) {
        this.increase = increase;
    }

    public Byte[] getOperatorSignaturePhoto() {
        return operatorSignaturePhoto;
    }

    public void setOperatorSignaturePhoto(Byte[] operatorSignaturePhoto) {
        this.operatorSignaturePhoto = operatorSignaturePhoto;
    }

    public Byte[] getUserSignaturePhoto() {
        return userSignaturePhoto;
    }

    public void setUserSignaturePhoto(Byte[] userSignaturePhoto) {
        this.userSignaturePhoto = userSignaturePhoto;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Integer getAddUser() {
        return addUser;
    }

    public void setAddUser(Integer addUser) {
        this.addUser = addUser;
    }
    public String getSyndicateName() {
        return syndicateName;
    }

    public void setSyndicateName(String syndicateName) {
        this.syndicateName = syndicateName;
    }


    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getFrontPlateReadByOCR() {
        return frontPlateReadByOCR;
    }

    public void setFrontPlateReadByOCR(String frontPlateReadByOCR) {
        this.frontPlateReadByOCR = frontPlateReadByOCR;
    }

    public String getRearPlateReadByOCR() {
        return rearPlateReadByOCR;
    }

    public void setRearPlateReadByOCR(String rearPlateReadByOCR) {
        this.rearPlateReadByOCR = rearPlateReadByOCR;
    }

    public Float getVolumePipeOrGondola() {
        return volumePipeOrGondola;
    }

    public void setVolumePipeOrGondola(Float volumePipeOrGondola) {
        this.volumePipeOrGondola = volumePipeOrGondola;
    }
}
