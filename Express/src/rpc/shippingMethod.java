package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Station;
import external.GoogleAPI;
import entity.Location;
import entity.Machine;

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
		JSONObject shippingInfo = RpcHelper.readJSONObject(request);
		JSONObject resJSON = new JSONObject();

		// step 2: From the JSON object, read shipping address and destination, and
		// package dimension & weight
		String destination = "";
		String shippingAddress = "";
		Long shippingTime = null;
		Double dimensionL = null;
		Double dimensionW = null;
		Double dimensionH = null;
		Double weightLB = null;
		Double weightOC = null;
		try {
			if (!shippingInfo.isNull("destination")) {
				destination = shippingInfo.getString("destination");
			}
			if (!shippingInfo.isNull("shippingAddress")) {
				shippingAddress = shippingInfo.getString("shippingAddress");
			}
			if (!shippingInfo.isNull("shippingTime")) {
				shippingTime = Long.valueOf(shippingInfo.getString("shippingTime"));
			}
			if (!shippingInfo.isNull("dimensionL")) {
				// dimensionL = Double.valueOf(shippingInfo.getString(""));
				dimensionL = shippingInfo.getDouble("dimensionL");
			}
			if (!shippingInfo.isNull("dimensionW")) {
				dimensionW = shippingInfo.getDouble("dimensionW");
			}
			if (!shippingInfo.isNull("dimensionH")) {
				dimensionH = shippingInfo.getDouble("dimensionH");
			}
			if (!shippingInfo.isNull("weightLB")) {
				weightLB = shippingInfo.getDouble("weightLB");
			}
			if (!shippingInfo.isNull("weightOC")) {
				weightOC = shippingInfo.getDouble("weightOC");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// step 3: read station information from database using the getStation method in
		// db/DBConnection.java
		DBConnection db = DBConnectionFactory.getConnection();
		List<Station> stations = db.getStation(new Location());

		// step 4: read robot information from database using the getMachine and
		// getMachineByType method in db/DBConnection.java
		// step 5: calculate distance & time from GoogleAPI from each station using each
		// machine type
		// step 6: calculate prices based on package dimension & weight & distance for
		// each station using each machine type
		JSONObject robot = new JSONObject();
		JSONObject drone = new JSONObject();
		JSONObject robotQuantity = new JSONObject();
		JSONObject droneQuantity = new JSONObject();
		JSONObject robotPrice = new JSONObject();
		JSONObject dronePrice = new JSONObject();
		JSONObject robotDuration = new JSONObject();
		JSONObject droneDuration = new JSONObject();
		double[] shippingAddressLatLon = GoogleAPI.addr_to_lonlat(shippingAddress);
		try {
			for (Station station : stations) {
				//calculate quantity
				List<Machine> machines = db.getMachine(station.getStationId());
				List<Machine> robotMachines = db.getMachineByType(machines, "robot");
				List<Machine> droneMachines = db.getMachineByType(machines, "drone");
				robotQuantity.put(station.getStationId(), String.valueOf(robotMachines.size()));
				droneQuantity.put(station.getStationId(), String.valueOf(droneMachines.size()));
				//calculate duration
				Double robotDurationDou = GoogleAPI.duration(
						String.valueOf(station.getLocation().getLatitude()),
						String.valueOf(station.getLocation().getLongitude()), 
						String.valueOf(shippingAddressLatLon[0]), 
						String.valueOf(shippingAddressLatLon[1]),
						"robot");
				Double droneDurationDou = GoogleAPI.duration(
						String.valueOf(station.getLocation().getLatitude()),
						String.valueOf(station.getLocation().getLongitude()), 
						String.valueOf(shippingAddressLatLon[0]), 
						String.valueOf(shippingAddressLatLon[1]),
						"drone");
				robotDuration.put(station.getStationId(), robotDurationDou);
				droneDuration.put(station.getStationId(), droneDurationDou);
				//calculate price
				Double robotPriceEach = GoogleAPI.price(robotDurationDou, dimensionL
						, dimensionW, dimensionH, weightLB, weightOC);
				Double dronePriceEach = GoogleAPI.price(droneDurationDou, dimensionL
						, dimensionW, dimensionH, weightLB, weightOC);
				robotPrice.put(station.getStationId(), String.valueOf(robotPriceEach));
				dronePrice.put(station.getStationId(), String.valueOf(dronePriceEach));
			}
			robot.put("quantity", robotQuantity);
			robot.put("price", robotPrice);
			robot.put("duration", robotDuration);
			drone.put("quantity", droneQuantity);
			drone.put("price", dronePrice);
			drone.put("duration", droneDuration);
			
			// decide whether use drone
			if (true) {
				resJSON.put("robot", robot);
				resJSON.put("drone", drone);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		// step 7: convert what you get from step 5&6 into JSON and write into response
		RpcHelper.writeJsonObject(response, resJSON);
	}

}
