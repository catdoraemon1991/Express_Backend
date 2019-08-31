package entity;

import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Order {
	private String userId;
	private String shippingAdress;
	private String destination;
	private String shippingMethod;
	private String robotId;
	private Double departTime;
	private Double pickupTime;
	private Double deliveryTime;
	private Double shippingTime;
	public String getUserId() {
		return userId;
	}
	public String getShippingAdress() {
		return shippingAdress;
	}
	public String getDestination() {
		return destination;
	}
	public String getShippingMethod() {
		return shippingMethod;
	}
	public String getRobotId() {
		return robotId;
	}
	public Double getDepartTime() {
		return departTime;
	}
	public Double getPickupTime() {
		return pickupTime;
	}
	public Double getDeliveryTime() {
		return deliveryTime;
	}
	public Double getShippingTime() {
		return shippingTime;
	}
	
	private Order(OrderBuilder builder) {
		this.userId = builder.userId;
		this.shippingAdress = builder.shippingAdress;
		this.destination = builder.destination;
		this.shippingMethod = builder.shippingMethod;
		this.robotId = builder.robotId;
		this.departTime = builder.departTime;
		this.pickupTime = builder.pickupTime;
		this.deliveryTime = builder.deliveryTime;
		this.shippingTime = builder.shippingTime;
	}
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("userId", userId);
			obj.put("shippingAdress", shippingAdress);
			obj.put("destination", destination);
			obj.put("shippingMethod", shippingMethod);
			obj.put("robotId", robotId);
			obj.put("departTime", departTime);
			obj.put("pickupTime", pickupTime);
			obj.put("deliveryTime", deliveryTime);
			obj.put("shippingTime", shippingTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
	public static class OrderBuilder {
		private String userId;
		private String shippingAdress;
		private String destination;
		private String shippingMethod;
		private String robotId;
		private Double departTime;
		private Double pickupTime;
		private Double deliveryTime;
		private Double shippingTime;
		
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public void setShippingAdress(String shippingAdress) {
			this.shippingAdress = shippingAdress;
		}
		public void setDestination(String destination) {
			this.destination = destination;
		}
		public void setShippingMethod(String shippingMethod) {
			this.shippingMethod = shippingMethod;
		}
		public void setRobotId(String robotId) {
			this.robotId = robotId;
		}
		public void setDepartTime(Double departTime) {
			this.departTime = departTime;
		}
		public void setPickupTime(Double pickupTime) {
			this.pickupTime = pickupTime;
		}
		public void setDeliveryTime(Double deliveryTime) {
			this.deliveryTime = deliveryTime;
		}
		public void setShippingTime(Double shippingTime) {
			this.shippingTime = shippingTime;
		}		
		
		public Order build() {
			return new Order(this);
		}
	}
	
}
