package com.mx.vise.cubicaciones.pojos;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mx.vise.androidwscon.webservice.CustomCalendarDeserializer;
import com.mx.vise.androidwscon.webservice.CustomDateSerializer;
import com.mx.vise.androidwscon.webservice.JsonDate;
import com.mx.vise.androidwscon.webservice.UtilDate;

public class TagPOJO implements Serializable {
	
	private static final long serialVersionUID = -890357692907471454L;

	private Integer idActivo;
	private String tag;

	@JsonSerialize(using = CustomDateSerializer.class)
	@JsonDeserialize(using = CustomCalendarDeserializer.class)
	@JsonDate(formatKey = UtilDate.FORMAT_STANDAR_DATE_WITH_HR_MIN_SS_SSS)
	private Date addDate;

	public Integer getIdActivo() {
		return idActivo;
	}

	public void setIdActivo(Integer idActivo) {
		this.idActivo = idActivo;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public Date getAddDate() {
		return this.addDate;
	}
	
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
}
