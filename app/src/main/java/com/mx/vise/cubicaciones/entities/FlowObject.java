package com.mx.vise.cubicaciones.entities;

import com.mx.vise.cubicaciones.pojos.BrandPOJO;
import com.mx.vise.cubicaciones.pojos.SyndicatePOJO;
import com.mx.vise.login.pojos.EmployeePojo;

import java.io.Serializable;
import java.util.Date;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el lunes 01 de octubre del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreosandroid
 */
public class FlowObject implements Serializable {

    private EmployeePojo session;
    private SyndicatePOJO selectedSyndicate;
    private String writedOperator;
    private String selectedBuilding;
    private String rearPlatePhotoPath;
    private String frontPlatePhotoPath;
    private String rearPlateByOCR;
    private String frontPlateByOCR;


    private boolean rearPhotoWasCaptured;

    private String licenceCardPhotoPath;
    private String frontPlateManual;
    private String rearPlateManual;

    private BrandPOJO selectedBrand;
    private String selectedUnitType;
    private String selectedBoxColor;
    private String selectedTractorColor;

    private String unitFrontalPhotoPath;
    private String unitSidePhotoPath;
    private String unitBackPhotoPath;
    private String circulationCardInserted;
    private String circulationCardPhotoPath;
    private boolean haveAnIncrease;

    private float[] basicMeasures;
    private float[] hydraulicJackDimensions;

    private boolean hasDimensions;
    private String imei;
    private float totalVolume;
    private float adjustment;
    private String sheetNumber;
    private float increase;
    private String operatorSignaturePath;
    private String userSignature;
    private String observations;
    private String tag;
    private Date startCaptureDate;
    private Date endCaptureDate;


    public EmployeePojo getSession() {
        return session;
    }

    public void setSession(EmployeePojo session) {
        this.session = session;
    }

    public SyndicatePOJO getSelectedSyndicate() {
        return selectedSyndicate;
    }

    public void setSelectedSyndicate(SyndicatePOJO selectedSyndicate) {
        this.selectedSyndicate = selectedSyndicate;
    }

    public String getWritedOperator() {
        return writedOperator;
    }

    public void setWritedOperator(String writedOperator) {
        this.writedOperator = writedOperator;
    }

    public String getSelectedBuilding() {
        return selectedBuilding;
    }

    public void setSelectedBuilding(String selectedBuilding) {
        this.selectedBuilding = selectedBuilding;
    }

    public String getRearPlatePhotoPath() {
        return rearPlatePhotoPath;
    }

    public void setRearPlatePhotoPath(String rearPlatePhotoPath) {
        this.rearPlatePhotoPath = rearPlatePhotoPath;
    }

    public String getFrontPlatePhotoPath() {
        return frontPlatePhotoPath;
    }

    public void setFrontPlatePhotoPath(String frontPlatePhotoPath) {
        this.frontPlatePhotoPath = frontPlatePhotoPath;
    }

    public boolean isRearLicencePlate() {
        return rearPhotoWasCaptured;
    }

    public void isRearLicencePlate(boolean isRearPlate) {
        this.rearPhotoWasCaptured = isRearPlate;
    }

    public void setLicenceCardPhotoPath(String mLicenceCardPhotoPath) {
        this.licenceCardPhotoPath = mLicenceCardPhotoPath;
    }

    public void setRearPlateWritedManual(String rearPlate) {
        this.rearPlateManual = rearPlate;
    }

    public void setFrontPlateWritedManual(String frontPlateManual) {
        this.frontPlateManual = frontPlateManual;
    }

    public String getLicenceCardPhotoPath() {
        return licenceCardPhotoPath;
    }

    public String getFrontPlateManual() {
        return frontPlateManual;
    }

    public void setFrontPlateManual(String frontPlateManual) {
        this.frontPlateManual = frontPlateManual;
    }

    public String getRearPlateManual() {
        return rearPlateManual;
    }

    public void setSelectedBrand(BrandPOJO selectedBrand) {
        this.selectedBrand = selectedBrand;
    }

    public BrandPOJO getSelectedBrand() {
        return selectedBrand;
    }

    public void setSelectedUnitType(String selectedUnitType) {
        this.selectedUnitType = selectedUnitType;

    }

    public String getSelectedUnitType() {
        return selectedUnitType;
    }

    public void setSelectedBoxColor(String selectedBoxColor) {
        this.selectedBoxColor = selectedBoxColor;
    }

    public String getSelectedBoxColor() {
        return selectedBoxColor;
    }

    public String getSelectedTractorColor() {
        return selectedTractorColor;
    }

    public void setSelectedTractorColor(String selectedTractorColor) {
        this.selectedTractorColor = selectedTractorColor;
    }

    public void setUnitFrontalPhotoPath(String frontalPath) {
        this.unitFrontalPhotoPath = frontalPath;
    }

    public String getUnitFrontalPhotoPath() {
        return unitFrontalPhotoPath;
    }

    public void setUnitSidePhotoPath(String unitSidePhotoPath) {
        this.unitSidePhotoPath = unitSidePhotoPath;

    }

    public String getUnitSidePhotoPath() {
        return unitSidePhotoPath;
    }

    public void setUnitBackPhotoPath(String unitBackPhotoPath) {
        this.unitBackPhotoPath = unitBackPhotoPath;
    }

    public String getUnitBackPhotoPath() {
        return unitBackPhotoPath;
    }

    public void setCirculationCardInserted(String circulationCard) {
        this.circulationCardInserted = circulationCard;
    }

    public String getCirculationCardInserted() {
        return circulationCardInserted;
    }

    public void setCirculationCardPhotoPath(String circulationCardPhotoPath) {
        this.circulationCardPhotoPath = circulationCardPhotoPath;

    }

    public String getCirculationCardPhotoPath() {
        return circulationCardPhotoPath;
    }

    public void haveAnIncrease(boolean checked) {
        this.haveAnIncrease = checked;
    }

    public boolean haveAnIncrease() {
        return haveAnIncrease;
    }

    public void setHaveAnIncrease(boolean haveAnIncrease) {
        this.haveAnIncrease = haveAnIncrease;
    }

    public float[] getBasicMeasures() {
        return basicMeasures;
    }

    public void setBasicMeasures(float[] measures) {
        this.basicMeasures = measures;
    }

    public void hasDimensions(boolean hasDimensions) {
        this.hasDimensions = hasDimensions;
    }

    public boolean hasDimensions() {
        return hasDimensions;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public float getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(float totalVolume) {
        this.totalVolume = totalVolume;
    }

    public float getAdjustment() {
        return adjustment;
    }

    public void setAdjustment(float adjustment) {
        this.adjustment = adjustment;
    }

    public String getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(String sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    public void setIncrease(float increase) {
        this.increase = increase;
    }

    public float getIncrease() {
        return increase;
    }

    public void setOperatorSignaturePath(String bytes) {
        operatorSignaturePath = bytes;
    }

    public String getOperatorSignaturePath() {
        return operatorSignaturePath;
    }

    public String getUserSignaturePath() {
        return userSignature;
    }

    public void setUserSignaturePath(String userSignature) {
        this.userSignature = userSignature;
    }

    public void setHydraulicJackDimensions(float[] hydraulicJackDimensions) {
        this.hydraulicJackDimensions = hydraulicJackDimensions;
    }

    public float[] getHydraulicJackDimensions() {
        return hydraulicJackDimensions;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getRearPlateByOCR() {
        return rearPlateByOCR;
    }

    public void setRearPlateByOCR(String rearPlateByOCR) {
        this.rearPlateByOCR = rearPlateByOCR;
    }

    public String getFrontPlateByOCR() {
        return frontPlateByOCR;
    }

    public void setFrontPlateByOCR(String frontPlateByOCR) {
        this.frontPlateByOCR = frontPlateByOCR;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setStartCaptureDate(Date date) {
        this.startCaptureDate = date;
    }

    public Date getStartCaptureDate() {
        return startCaptureDate;
    }

    public void setEndCaptureDate(Date date) {
        this.endCaptureDate = date;
    }

    public Date getEndCaptureDate() {
        return endCaptureDate;
    }
}
