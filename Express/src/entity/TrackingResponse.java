package entity;

import com.google.gson.Gson;


public class TrackingResponse {
	private final Location currentLocation;
	private final Location station;
	private final Location shippingAddress;
	private final Location destination;
	private final String status;
	
	public Location getCurrentLocation() {
		return currentLocation;
	}
	public Location getStation() {
		return station;
	}
	public Location getShippingAddress() {
		return shippingAddress;
	}
	public Location getDestination() {
		return destination;
	}
	public String getStatus() {
		return status;
	}
	
	public String toJSONString() {
		return new Gson().toJson(this);
	}
	
	public TrackingResponse(TrackingResponseBuilder builder) {
		this.currentLocation = builder.currentLocation;
		this.station = builder.station;
		this.shippingAddress = builder.shippingAddress;
		this.destination = builder.destination;
		this.status = builder.status;
	}
	
	public static class TrackingResponseBuilder{
		private Location currentLocation;
		private Location station;
		private Location shippingAddress;
		private Location destination;
		private String status;
		
		public void setCurrentLocation(Location currentLocation) {
			this.currentLocation = currentLocation;
		}
		public void setStation(Location station) {
			this.station = station;
		}
		public void setShippingAddress(Location shippingAddress) {
			this.shippingAddress = shippingAddress;
		}
		public void setDestination(Location destination) {
			this.destination = destination;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		
		public TrackingResponse build(){
			return new TrackingResponse(this);
		}
		
	}
	
}

