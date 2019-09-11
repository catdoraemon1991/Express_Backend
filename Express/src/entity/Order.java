package entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class Order {
	private String orderId;
	private String userId;
	private String shippingAddress;
	private String destination;
	private String shippingMethod;
	private String machineId;
	private Long departTime;
	private Long pickupTime;
	private Long deliveryTime;
	private Long shippingTime;
	public String getOrderId() {
		return orderId;
	}
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
		this.orderId = builder.orderId;
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
	
	public String toJSONString() {
		return new Gson().toJson(this);
	}
	
	public static class OrderBuilder {
		private String orderId;
		private String userId;
		private String shippingAddress;
		private String destination;
		private String shippingMethod;
		private String machineId;
		private Long departTime;
		private Long pickupTime;
		private Long deliveryTime;
		private Long shippingTime;
		
		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}
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
