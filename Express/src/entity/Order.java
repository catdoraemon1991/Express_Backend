package entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Order {

	// builder patter
	private String userId;
	private String shippingAddress;
	private String destination;
	private String shippingMethod;
	private String machineId;
	private Long departTime;
	private Long pickupTime;
	private Long deliveryTime;
	private Long shippingTime;
	public String getUserId() {
		return userId;
	}
	public String getShippingAddress() {
		return shippingAddress;
	}
	public String getDestination() {
		return destination;
	}
	public String getShippingMethod() {
		return shippingMethod;
	}
	public String getMachineId() {
		return machineId;
	}
	public Long getDepartTime() {
		return departTime;
	}
	public Long getPickupTime() {
		return pickupTime;
	}
	public Long getDeliveryTime() {
		return deliveryTime;
	}
	public Long getShippingTime() {
		return shippingTime;
	}
	
	private Order(OrderBuilder builder) {
		this.userId = builder.userId;
		this.shippingAddress = builder.shippingAddress;
		this.destination = builder.destination;
		this.shippingMethod = builder.shippingMethod;
		this.machineId = builder.machineId;
		this.departTime = builder.departTime;
		this.pickupTime = builder.pickupTime;
		this.deliveryTime = builder.deliveryTime;
		this.shippingTime = builder.shippingTime;
	}

	// consider google Gson library
	public String toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("userId", userId);
			obj.put("shippingAddress", shippingAddress);
			obj.put("destination", destination);
			obj.put("shippingMethod", shippingMethod);
			obj.put("machineId", machineId);
			obj.put("departTime", departTime.toString());
			obj.put("pickupTime", pickupTime.toString());
			obj.put("deliveryTime", deliveryTime.toString());
			obj.put("shippingTime", shippingTime.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj.toString();
	}
	
	public static class OrderBuilder {
		// nice one.
		private String userId;
		private String shippingAddress;
		private String destination;
		private String shippingMethod;
		private String machineId;
		private Long departTime;
		private Long pickupTime;
		private Long deliveryTime;
		private Long shippingTime;
		
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public void setShippingAdress(String shippingAddress) {
			this.shippingAddress = shippingAddress;
		}
		public void setDestination(String destination) {
			this.destination = destination;
		}
		public void setShippingMethod(String shippingMethod) {
			this.shippingMethod = shippingMethod;
		}
		public void setMachineId(String machineId) {
			this.machineId = machineId;
		}
		public void setDepartTime(Long departTime) {
			this.departTime = departTime;
		}
		public void setPickupTime(Long pickupTime) {
			this.pickupTime = pickupTime;
		}
		public void setDeliveryTime(Long deliveryTime) {
			this.deliveryTime = deliveryTime;
		}
		public void setShippingTime(Long shippingTime) {
			this.shippingTime = shippingTime;
		}		
		
		public Order build() {
			return new Order(this);
		}
	}
	
}
