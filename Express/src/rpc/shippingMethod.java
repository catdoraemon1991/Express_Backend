package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.google.gson.Gson;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Station;
import external.GoogleAPI;
import entity.Location;
import entity.Machine;
import entity.ShippingInfo;

/**
 * Servlet implementation class shippingMethod
 */
@WebServlet("/shippingMethod")
public class shippingMethod extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public shippingMethod() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// step 1: read JSON object from HTTP request 
		// step 2: From the JSON object, read
		// shipping address and destination, and package size 
		// step 3: read station
		// information from database using the getStation method in db/DBConnection.java
		// step 4: read robot information from database using the getMachine and
		// getMachineByType method in db/DBConnection.java 
		// step 5: calculate distance &
		// time from GoogleAPI from each station using each machine type 
		// step 6:
		// calculate prices based on package size and duration for each station using
		// each machine type
		// step 7: convert what you get from step 5&6 into JSON and write into response
		JSONObject shippingDetails = RpcHelper.readJSONObject(request);
		JSONObject resJSON = new JSONObject();
		try {
			String destination = RpcUtil.EMPTY;
			String shippingAddress = RpcUtil.EMPTY;
			String itemSize = RpcUtil.EMPTY;
			Long shippingTime = null;
			if (!shippingDetails.isNull(RpcUtil.DESTINATION)) {
				destination = RpcHelper.deduplicate(shippingDetails.getString(RpcUtil.DESTINATION));
				destination = RpcHelper.replaceBlank(destination);
			}
			if (!shippingDetails.isNull(RpcUtil.SHIPPING_ADDRESS)) {
				shippingAddress = RpcHelper.deduplicate(shippingDetails.getString(RpcUtil.SHIPPING_ADDRESS));
				shippingAddress = RpcHelper.replaceBlank(shippingAddress);
			}
			if (!shippingDetails.isNull(RpcUtil.SHIPPING_TIME)) {
				shippingTime = shippingDetails.getLong(RpcUtil.SHIPPING_TIME);
			}
			if (!shippingDetails.isNull(RpcUtil.ITEM_SIZE)) {
				itemSize = shippingDetails.getString(RpcUtil.ITEM_SIZE);
			}
			if (destination.isEmpty()|| shippingAddress.isEmpty() || itemSize.isEmpty()|| shippingTime==null) {
				resJSON.put("Error", RpcUtil.ENTER_ERROR);
				RpcHelper.writeJsonObject(response, resJSON);
				return;
			}
			DBConnection db = DBConnectionFactory.getConnection();
			List<Station> stations = db.getStation(new Location());
			Location shippingAddressLatLng = GoogleAPI.addr_to_latlng(shippingAddress);
			Location destinationLatLng = GoogleAPI.addr_to_latlng(destination);
			if (shippingAddressLatLng.getLatitude()<-400D || destinationLatLng.getLatitude()<-400D ) {
				resJSON.put("Error", RpcUtil.Address_ERROR);
				RpcHelper.writeJsonObject(response, resJSON);
				return;
			}
			for (Station station : stations) {
				List<Machine> machines = db.getMachine(station.getStationId());
				List<Machine> robotMachines = db.getMachineByType(machines, "robot");
				List<Machine> droneMachines = db.getMachineByType(machines, "drone");
				ShippingInfo.Robot robot = new ShippingInfo.Robot();
				ShippingInfo.Drone drone = new ShippingInfo.Drone();
				double robotDuration = GoogleAPI.duration(shippingAddressLatLng, destinationLatLng, "robot");
				double droneDuration = GoogleAPI.duration(shippingAddressLatLng, destinationLatLng, "drone");
				int robotNum = robotMachines.size();
				int droneNum = droneMachines.size();
				double robotPrice = GoogleAPI.price(robotDuration, itemSize);
				double dronePrice = GoogleAPI.price(droneDuration, itemSize);
				robot.setDuration(robotDuration).setPrice(robotPrice).setQuantity(robotNum);
				drone.setDuration(droneDuration).setPrice(dronePrice).setQuantity(droneNum);
				ShippingInfo shippingInfo = new ShippingInfo().setDrone(drone).setRobot(robot);
				Gson gson = new Gson();
				String resString = gson.toJson(shippingInfo);
				resJSON.put(station.getStationId(), new JSONObject(resString));
			}
			RpcHelper.writeJsonObject(response, resJSON);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
