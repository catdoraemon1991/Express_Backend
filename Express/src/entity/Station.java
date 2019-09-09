package entity;

public class Station {

	// stationId, location should be final
	private String stationId;
	Location location;
	public Station(String stationId, Location location) {
		this.stationId = stationId;
		this.location = location;
	}
	
//	public Station() {
//		this.stationId = "";
//		this.location = null;
//	}
	
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public String getStationId() {
		return stationId;
	}
	public Location getLocation() {
		return location;
	}
	
}
