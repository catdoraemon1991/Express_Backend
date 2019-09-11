package entity;

public class Station {
	private final String stationId;
	private final Location location;
	public Station(String stationId, Location location) {
		this.stationId = stationId;
		this.location = location;
	}
	
//	public Station() {
//		this.stationId = "";
//		this.location = null;
//	}
	
	
	public String getStationId() {
		return stationId;
	}
	public Location getLocation() {
		return location;
	}
	
}
