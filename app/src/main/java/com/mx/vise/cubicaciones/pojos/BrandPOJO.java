package com.mx.vise.cubicaciones.pojos;

import java.io.Serializable;

public class BrandPOJO implements Serializable {

	private static final long serialVersionUID = 9122813842945674923L;
	
	private String name;
	
	private Integer id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
