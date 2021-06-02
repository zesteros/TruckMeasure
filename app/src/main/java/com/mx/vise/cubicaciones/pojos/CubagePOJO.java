package com.mx.vise.cubicaciones.pojos;

import com.mx.vise.cubicaciones.pojos.BrandPOJO;
import com.mx.vise.cubicaciones.pojos.SyndicatePOJO;
import com.mx.vise.cubicaciones.pojos.TagPOJO;

import java.io.Serializable;
import java.util.ArrayList;


public class CubagePOJO implements Serializable{

    private static final long serialVersionUID = -3392559283980279360L;
    private ArrayList<BrandPOJO> brands;
    private ArrayList<SyndicatePOJO> syndicates;
    private ArrayList<TagPOJO> tags;

    public ArrayList<BrandPOJO> getBrands() {
        return brands;
    }
    public void setBrands(ArrayList<BrandPOJO> brands) {
        this.brands = brands;
    }
    public ArrayList<SyndicatePOJO> getSyndicates() {
        return syndicates;
    }
    public void setSyndicates(ArrayList<SyndicatePOJO> syndicates) {
        this.syndicates = syndicates;
    }
    public ArrayList<TagPOJO> getTags() {
        return tags;
    }
    public void setTags(ArrayList<TagPOJO> tags) {
        this.tags = tags;
    }

}
