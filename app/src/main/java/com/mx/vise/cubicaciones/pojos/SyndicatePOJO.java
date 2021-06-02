package com.mx.vise.cubicaciones.pojos;

import java.io.Serializable;

public class SyndicatePOJO implements Serializable {

	private Integer id;
	private String name;
	private static final long serialVersionUID = 1984426388235402037L;

	public void setId(Integer idSindi) {
		this.id = idSindi;
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
