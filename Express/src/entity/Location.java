package entity;

public class Location {
	private Double latitude;
	private Double longitude;
	
	public Location(Double latitude, Double longitude) {
		this.latitude =  latitude;
		this.longitude = longitude;
	}
	public Location() {
		this.latitude =  null;
		this.longitude = null;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	
	
}
