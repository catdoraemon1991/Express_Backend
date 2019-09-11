package rpc;

import java.io.IOException;
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
		String stationId = RpcUtil.EMPTY;
		String shippingMechod = RpcUtil.EMPTY;
		String userId = RpcUtil.EMPTY;
		String machineId = RpcUtil.EMPTY;
		String destination = RpcUtil.EMPTY;
		String shippingAddress = RpcUtil.EMPTY;
		Long shippingTime = null;
		Long departTime = null;
		Long pickupTime = null;
		Long deliveryTime = null;
		try {
			if (!orderInfo.isNull(RpcUtil.USER_ID)) {
				userId = orderInfo.getString(RpcUtil.USER_ID);
			}
			if (!orderInfo.isNull(RpcUtil.SHIPPING_ADDRESS)) {
				shippingAddress = RpcHelper.deduplicate( orderInfo.getString(RpcUtil.SHIPPING_ADDRESS) );
			}
			if (!orderInfo.isNull(RpcUtil.DESTINATION)) {
				destination = RpcHelper.deduplicate( orderInfo.getString(RpcUtil.DESTINATION) );
			}
			if (!orderInfo.isNull(RpcUtil.SHIPPING_TIME)) {
				shippingTime = orderInfo.getLong(RpcUtil.SHIPPING_TIME);
			}
			if (!orderInfo.isNull(RpcUtil.SHIPPING_METHOD)) {
				shippingMechod = orderInfo.getString(RpcUtil.SHIPPING_METHOD);
			}
			if (!orderInfo.isNull(RpcUtil.STATION_ID)) {
				stationId = orderInfo.getString(RpcUtil.STATION_ID);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// step 3: calculate robot availability, select a robot and update robot status
		DBConnection db = DBConnectionFactory.getConnection();
		List<Machine> machines = db.getMachineByType(db.getMachine(stationId), shippingMechod);
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
		Location shipLatLon = GoogleAPI.addr_to_latlng(RpcHelper.replaceBlank(shippingAddress));
		Location desLatLon = GoogleAPI.addr_to_latlng(RpcHelper.replaceBlank(destination));
		Location stationLoc = db.getStationById(stationId).getLocation();
		
		double stationToShip = GoogleAPI.duration(stationLoc, shipLatLon, shippingMechod);
		double shipToDes = GoogleAPI.duration(shipLatLon,desLatLon,shippingMechod);
		double desToStation = GoogleAPI.duration(desLatLon,stationLoc,shippingMechod);
		
		Calendar ship = Calendar.getInstance(TimeZone.getTimeZone(RpcUtil.TIME_ZONE));
		Calendar back = Calendar.getInstance(TimeZone.getTimeZone(RpcUtil.TIME_ZONE));
		long currentDate = ship.getTimeInMillis();
		long processing = RpcUtil.SECS_IN_MINUTE * RpcUtil.PROCESSING_TIME_IN_MINUTE;
		
		departTime = Math.max(currentDate + processing, shippingTime);	
		pickupTime = departTime + Math.round(RpcUtil.SECS_IN_MINUTE * stationToShip); 
		deliveryTime = pickupTime + Math.round(RpcUtil.SECS_IN_MINUTE * shipToDes); 
		//Long backTime = currentDate + RpcUtil.SECS_IN_MINUTE; //
		Long backTime = deliveryTime + Math.round(RpcUtil.SECS_IN_MINUTE * desToStation); //
		
		back.setTimeInMillis(backTime);
		Date backDate = back.getTime();
		// step 5: write output from previous steps to a new Order class using builder pattern (see entity package)
		String newOrderId = generateID.randomUUID(16);
		newOrder.setOrderId(newOrderId);
		newOrder.setUserId(userId);
		newOrder.setShippingAdress(shippingAddress);
		newOrder.setDestination(destination);
		newOrder.setShippingTime(shippingTime);
		newOrder.setShippingMethod(shippingMechod);
		newOrder.setMachineId(machineId);
		newOrder.setDepartTime(departTime);
		newOrder.setPickupTime(pickupTime);
		newOrder.setDeliveryTime(deliveryTime);
		Order order = newOrder.build();
		// step 6: write the new Order into database using the saveOrder method in db/DBConnection.java
		//String newOrderId = "1234567";
		String newOrderStatus = db.saveOrder(userId, order); //uncomment this
		
		// step 7: return order Id & machine status	
		if (newOrderStatus.equals(HTTPUtil.STATUSOK)) {
			try {
				resJSON.put(RpcUtil.ORDER_ID, newOrderId);
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
				System.out.println(RpcUtil.UPDATE_MESG);
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
