package com.senechaux.rutino.db.entities;

import java.io.Serializable;

public class MyLocation implements Serializable {
	private static final long serialVersionUID = 1L;

	private Double latitude;
	private Double longitude;
	
	public MyLocation() {
		this(0.0, 0.0);
	}

	public MyLocation(Double latitude, Double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

}
