package rpc;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

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
import entity.TrackingResponse.TrackingResponseBuilder;
import external.GoogleAPI;

/**
 * Servlet implementation class Tracking
 */
@WebServlet("/tracking")
public class Tracking extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Tracking() {
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
		
		// step 2: read orderId from the request
		String orderId = RpcUtil.EMPTY;
		try {
			if (!orderInfo.isNull(RpcUtil.ORDER_ID)) {
				orderId = orderInfo.getString(RpcUtil.ORDER_ID);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// step 3: read order information, station from database
		DBConnection db = DBConnectionFactory.getConnection();
		Order order = db.getOrderById(orderId);
		String shippingAddress = order.getShippingAddress();
		String destination = order.getDestination();
		Long departTime = order.getDepartTime();
		Long pickupTime = order.getPickupTime();
		Long deliveryTime = order.getDeliveryTime();
		String machineId = order.getMachineId();
		
		Machine machine = db.getMachineById(machineId);
		Location stationLoc = db.getStationById(machine.getStationId()).getLocation();
		Location desLoc = GoogleAPI.addr_to_latlng(RpcHelper.replaceBlank(destination));
		Location shipLoc = GoogleAPI.addr_to_latlng(RpcHelper.replaceBlank(shippingAddress));
		String type = machine.getType();
		
		// step 4: calculate order status based on time
		Calendar current = Calendar.getInstance(TimeZone.getTimeZone(RpcUtil.TIME_ZONE));
		Long curTime = current.getTimeInMillis();
		
		Location curLoc;
		String status;

		// for testing only;
		//curTime = 1568417256898L;
		if (curTime <= departTime) {
			//order processing
			curLoc = stationLoc;
			status = RpcUtil.BEFORE_SHIP_MESG;
		}else if (curTime <= pickupTime) {
			// departed, on its way to pickup
			curLoc = GoogleAPI.curLocOnMap(stationLoc, shipLoc, departTime, pickupTime, curTime, type);
			status = RpcUtil.DEPART_MESG;
		}else if (curTime <= deliveryTime) {
			// picked up, on its way to destination
			curLoc = GoogleAPI.curLocOnMap(shipLoc, desLoc, pickupTime, deliveryTime, curTime, type);
			status = RpcUtil.PICKUP_MESG;
		}else {
			// delivered
			curLoc = desLoc;
			status = RpcUtil.DELIVER_MESG;
		}
		
		// step 5: use builder pattern to save to the TrackingResponse object
		TrackingResponseBuilder builder = new TrackingResponseBuilder();
		builder.setCurrentLocation(curLoc);
		builder.setDestination(desLoc);
		builder.setShippingAddress(shipLoc);
		builder.setStation(stationLoc);
		builder.setStatus(status);
		String trackingResponse = builder.build().toJSONString();
		
		// step 6: save the response to JSON and give back to Frontend
		try {
			resJSON = new JSONObject(trackingResponse);
			RpcHelper.writeJsonObject(response, resJSON);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("departTime: " + departTime + ", pickupTime: " + pickupTime + ", deliveryTime:" + deliveryTime);
	}

}
