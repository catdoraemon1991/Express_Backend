package entity;

public class Location {
	private final Double latitude;
	private final Double longitude;

	public Location(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Location() {
		this.latitude = -500D;
		this.longitude = -500D;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

}
