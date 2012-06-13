package br.com.maritaca.utils;

public class LocationHolder {
	public Double lat;
	public Double log;

	private static LocationHolder locationHolder = null;

	public static LocationHolder getInstanceOfLocationHolder() {
		if (locationHolder == null) {
			locationHolder = new LocationHolder();
		}
		return locationHolder;
	}

	private LocationHolder() {
		this.log = null;
		this.lat = null;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLog() {
		return log;
	}

	public void setLog(Double log) {
		this.log = log;
	}
}