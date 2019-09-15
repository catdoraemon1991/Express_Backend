package entity;

public class ShippingInfo {
	private Robot robot;
	private Drone drone;

	public Robot getRobot() {
		return robot;
	}
	public Drone getDrone() {
		return drone;
	}
	public ShippingInfo setRobot(Robot robot) {
		this.robot = robot;
		return this;
	}
	public ShippingInfo setDrone(Drone drone) {
		this.drone = drone;
		return this;
	}
	public ShippingInfo(Robot robot, Drone drone) {
		this.robot = robot;
		this.drone = drone;
	}
	public ShippingInfo() {
		this.robot = null;
		this.drone = null;
	}
	
	public static class Robot {
		private int quantity;
		private double price;
		private double duration;
		public int getQuantity() {
			return quantity;
		}
		public double getPrice() {
			return price;
		}
		public double getDuration() {
			return duration;
		}
		public Robot setQuantity(int quantity) {
			this.quantity = quantity;
			return this;
		}
		public Robot setPrice(double price) {
			this.price = price;
			return this;
		}
		public Robot setDuration(double duration) {
			this.duration = duration;
			return this;
		}

	}

	public static class Drone {
		private int quantity;
		private double price;
		private double duration;
		public int getQuantity() {
			return quantity;
		}
		public double getPrice() {
			return price;
		}
		public double getDuration() {
			return duration;
		}
		public Drone setQuantity(int quantity) {
			this.quantity = quantity;
			return this;
		}
		public Drone setPrice(double price) {
			this.price = price;
			return this;
		}
		public Drone setDuration(double duration) {
			this.duration = duration;
			return this;
		}

		
	}
}
