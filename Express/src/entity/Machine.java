package entity;


import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import entity.Order.OrderBuilder;

public class Machine {
	private String machineId;
	private Status status;
	private String stationId;
	private String type;
	private Location location;
	private Set<String>  orderId;
	
	public class Status{
		private String OK;
		private String OnUse;
		public String getOK() {
			return OK;
		}
		public String getOnUse() {
			return OnUse;
		}	
		
	}
	
	public String toJSONString() {
		return new Gson().toJson(this);
	}
	
	public String getMachineId() {
		return machineId;
	}
	public Status getStatus() {
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
	private Machine(MachineBuilder builder) {
		this.machineId = builder.machineId;
		this.status = builder.status;
		this.stationId = builder.stationId;
		this.type = builder.type;
		this.location = builder.location;
	}
	
	public static class MachineBuilder {
		private String machineId;
		private Status status;
		private String stationId;
		private String type;
		private Location location;
		private Set<String>  orderId;
		
		public void setMachineId(String machineId) {
			this.machineId = machineId;
		}
		public void setStatus(Status status) {
			this.status = status;
		}

		public void setStationId(String stationId) {
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
		
		public Machine build() {
			return new Machine(this);
		}
	}
	
}
