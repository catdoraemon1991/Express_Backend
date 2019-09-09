package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Location;
import entity.Machine;
import entity.Order;
import entity.Order.OrderBuilder;
import entity.Station;
import external.GoogleAPI;;

/**
 * Servlet implementation class orderConfirmation
 */
@WebServlet("/orderConfirmation")
public class orderConfirmation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public orderConfirmation() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// step 1: read JSON object from HTTP request 
		JSONObject orderInfo = RpcHelper.readJSONObject(request);
		JSONObject resJSON = new JSONObject();
		// step 2: From the JSON object, read order information
		OrderBuilder newOrder = new OrderBuilder();
		String stationId = "";
		String type = "";
		String userId = "";
		String machineId = "";
		String destination = "";
		String shippingAddress = "";
		Long shippingTime = null;
		Long departTime = null;
		Long pickupTime = null;
		Long deliveryTime = null;
		try {
			//magic string should be final static and define in constant class.
			if (! orderInfo.isNull("userId")) {
				userId = orderInfo.getString("userId");
			}
			if (! orderInfo.isNull("shippingAddress")) {
				shippingAddress = orderInfo.getString("shippingAddress");
			}
			if (! orderInfo.isNull("destination")) {
				destination = orderInfo.getString("destination");
			}
			if (! orderInfo.isNull("shippingTime")) {
				shippingTime = Long.valueOf(orderInfo.getString("shippingTime"));
			}
			if (! orderInfo.isNull("shippingMethod")) {
				type = orderInfo.getString("shippingMethod");
			}
			if (! orderInfo.isNull("stationId")) {
				stationId = orderInfo.getString("stationId");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (userId == "") {
			try {
				resJSON.put("Error", "Wrong userId input");
				RpcHelper.writeJsonObject(response, resJSON);
				return;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// step 3: calculate robot availability, select a robot and update robot status
		DBConnection db = DBConnectionFactory.getConnection();
		List<Machine> machines = new ArrayList<>();
		if (stationId != "" && type != "") {
			machines = db.getMachineByType(db.getMachine(stationId), type);
		}else {
			try {
				resJSON.put("Error", "Wrong shipping method input");
				RpcHelper.writeJsonObject(response, resJSON);
				return;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		if (machines.size() == 0) {
			try {
				resJSON.put("Back", "Machine not available, please re-select");
				RpcHelper.writeJsonObject(response, resJSON);
				return;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			machineId = machines.get(0).getMachineId();
			db.machineOccupied(machineId);  //uncomment this		
		}
		// step 4: calculate departTime, pickupTime and deliveryTime from Google API
		double[] shipLatLon = GoogleAPI.addr_to_lonlat(shippingAddress);
		double[] desLatLon = GoogleAPI.addr_to_lonlat(destination);
		Location stationLoc = db.getStationById(db.getStation(new Location()), stationId).getLocation();
		
		double stationToShip = GoogleAPI.duration(String.valueOf(stationLoc.getLatitude()), String.valueOf(stationLoc.getLongitude()),
				String.valueOf(shipLatLon[0]),String.valueOf(shipLatLon[1]),type);
		double shipToDes = GoogleAPI.duration(String.valueOf(desLatLon[0]),String.valueOf(desLatLon[1]),
				String.valueOf(shipLatLon[0]),String.valueOf(shipLatLon[1]),type);
		double desToStation = GoogleAPI.duration(String.valueOf(desLatLon[0]),String.valueOf(desLatLon[1]),
				String.valueOf(stationLoc.getLatitude()), String.valueOf(stationLoc.getLongitude()),type);
		
		Calendar ship = Calendar.getInstance(TimeZone.getTimeZone("PST"));
		Calendar back = Calendar.getInstance(TimeZone.getTimeZone("PST"));
		long currentDate = ship.getTimeInMillis();
		long processing = 1000L * 60L * 2L;
		
		departTime = Math.max(currentDate + processing, shippingTime);	
		pickupTime = departTime + 1000L * 60L * Math.round(stationToShip); 
		deliveryTime = pickupTime + 1000L * 60L * Math.round(shipToDes); 
		Long backTime = currentDate + 1000L * 30L; //
		//Long backTime = deliveryTime + 1000L * 60L * Math.round(desToStation); //
		
		back.setTimeInMillis(backTime);
		Date backDate = back.getTime();
		// step 5: write output from previous steps to a new Order class using builder pattern (see entity package)
		newOrder.setUserId(userId);
		newOrder.setShippingAdress(shippingAddress);
		newOrder.setDestination(destination);
		newOrder.setShippingTime(shippingTime);
		newOrder.setShippingMethod(type);
		newOrder.setMachineId(machineId);
		newOrder.setDepartTime(departTime);
		newOrder.setPickupTime(pickupTime);
		newOrder.setDeliveryTime(deliveryTime);
		Order order = newOrder.build();
		// step 6: write the new Order into database using the saveOrder method in db/DBConnection.java
		//String newOrderId = "1234567";
		String newOrderId = db.saveOrder(userId, order); //uncomment this
		
		// step 7: return order Id & machine status	
		if (newOrderId != "") {
			try {
				resJSON.put("orderId", newOrderId);
				RpcHelper.writeJsonObject(response, resJSON);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// step 8: set up timer for changing machine status using the updateStatus method in db/DBConnection.java
		Timer timer = new Timer();
		final String machineIdF = machineId;
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				System.out.println("This is the scheduled update");
				db.updateStatus(newOrderId, machineIdF);  //uncomment this
			}
		};
		timer.schedule(task, backDate);

		System.out.println(backDate);
		System.out.println("stationToShip:" + String.valueOf(stationToShip) 
		+ ",shipToDes: " + String.valueOf(shipToDes) 
		+ ",desToStation:" + String.valueOf(desToStation));
	}

}
