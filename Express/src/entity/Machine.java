package entity;


import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Machine {
	private String machineId;
	private String status;
	private String stationId;
	private String type;
	private Location location;
	private Set<String>  orderId;
	
	public String toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("machineId", machineId);
			obj.put("status", status);
			obj.put("stationId", stationId);
			obj.put("machineId", machineId);
			obj.put("type", type);
			obj.put("location", location);
			//obj.put("orderId", String.join(", ", orderId));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj.toString();
	}
	
	
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setStation(String stationId) {
		this.stationId = stationId;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public void setOrderId(Set<String> orderId) {
		this.orderId = orderId;
	}
	public String getMachineId() {
		return machineId;
	}
	public String getStatus() {
		return status;
	}
	public String getStationId() {
		return stationId;
	}
	public String getType() {
		return type;
	}
	public Location getLocation() {
		return location;
	}
	public Set<String> getOrderId() {
		return orderId;
	}
	
	
}
