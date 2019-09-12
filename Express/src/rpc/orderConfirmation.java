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
import db.generateID;
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
		String stationId = RpcUtil.empty;
		String type = RpcUtil.empty;
		String userId = RpcUtil.empty;
		String machineId = RpcUtil.empty;
		String destination = RpcUtil.empty;
		String shippingAddress = RpcUtil.empty;
		Long shippingTime = null;
		Long departTime = null;
		Long pickupTime = null;
		Long deliveryTime = null;
		try {
			if (! orderInfo.isNull(RpcUtil.userId)) {
				userId = orderInfo.getString(RpcUtil.userId);
			}
			if (! orderInfo.isNull(RpcUtil.shippingAddress)) {
				shippingAddress = orderInfo.getString(RpcUtil.shippingAddress);
			}
			if (! orderInfo.isNull(RpcUtil.destination)) {
				destination = orderInfo.getString(RpcUtil.destination);
			}
			if (! orderInfo.isNull(RpcUtil.shippingTime)) {
				shippingTime = Long.valueOf(orderInfo.getString(RpcUtil.shippingTime));
			}
			if (! orderInfo.isNull(RpcUtil.shippingMethod)) {
				type = orderInfo.getString(RpcUtil.shippingMethod);
			}
			if (! orderInfo.isNull(RpcUtil.stationId)) {
				stationId = orderInfo.getString(RpcUtil.stationId);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (userId.equals(RpcUtil.empty)) {
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
		if (! stationId.equals(RpcUtil.empty) && ! type.equals(RpcUtil.empty)) {
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
		double[] shipLatLon = GoogleAPI.addr_to_latlng(shippingAddress);
		double[] desLatLon = GoogleAPI.addr_to_latlng(destination);
		Location stationLoc = db.getStationById(db.getStation(new Location()), stationId).getLocation();
		
		double stationToShip = GoogleAPI.duration(stationLoc, new Location(shipLatLon[0],shipLatLon[1]), type);
		double shipToDes = GoogleAPI.duration(new Location(shipLatLon[0],shipLatLon[1]),new Location(desLatLon[0],desLatLon[1]),type);
		double desToStation = GoogleAPI.duration(new Location(desLatLon[0],desLatLon[1]),stationLoc,type);
		
		Calendar ship = Calendar.getInstance(TimeZone.getTimeZone(RpcUtil.timeZone));
		Calendar back = Calendar.getInstance(TimeZone.getTimeZone(RpcUtil.timeZone));
		long currentDate = ship.getTimeInMillis();
		long processing = 1000L * 60L * 2L;
		
		departTime = Math.max(currentDate + processing, shippingTime);	
		pickupTime = departTime + 1000L * 60L * Math.round(stationToShip); 
		deliveryTime = pickupTime + 1000L * 60L * Math.round(shipToDes); 
		Long backTime = currentDate + 1000L * 15L; //
		//Long backTime = deliveryTime + 1000L * 60L * Math.round(desToStation); //
		
		back.setTimeInMillis(backTime);
		Date backDate = back.getTime();
		// step 5: write output from previous steps to a new Order class using builder pattern (see entity package)
		String newOrderId = generateID.randomUUID(16);
		newOrder.setOrderId(newOrderId);
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
		String newOrderStatus = db.saveOrder(userId, order); //uncomment this
		
		// step 7: return order Id & machine status	
		if (newOrderStatus.equals(HTTPUtil.StatusOK)) {
			try {
				resJSON.put(RpcUtil.orderId, newOrderId);
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
				System.out.println(RpcUtil.updateMesg);
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
