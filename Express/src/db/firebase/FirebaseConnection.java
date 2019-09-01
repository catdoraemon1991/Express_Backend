package db.firebase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import entity.Location;
import entity.Machine;
import entity.Order;
import entity.Station;

public class FirebaseConnection implements DBConnection {

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public String saveOrder(String userID, Order order) {
		String newOrderId = FirebaseHelper.doPost(FirebaseUtil.host + "order.json", order.toJSONObject());
		String addUserUrl = FirebaseUtil.host + "user/" + order.getUserId() + "/orderId/" + newOrderId + ".json";
		Integer userStatus = FirebaseHelper.doPut(addUserUrl, "{\" \": \" \"}");
		String addMachineUrl = FirebaseUtil.host + "machine/" + order.getMachineId() + "/orderId/" + newOrderId + ".json";
		Integer machineOrderStatus = FirebaseHelper.doPut(addMachineUrl, "{\" \": \" \"}");
		String newStatus = "{\"onUse\": \" \"}";
		Integer changeStatus = FirebaseHelper.doPut(FirebaseUtil.host + "machine/" + order.getMachineId() + "/status.json", newStatus);
		return newOrderId;
	}

	@Override
	public List<Machine> getMachine(String stationId) {
		List<Machine> machines = new ArrayList<>();
		String allMachine = FirebaseHelper.doGet(FirebaseUtil.host + "/machine.json") ;
		try {
			JSONObject machinesJSON =  new JSONObject(allMachine);
			Iterator<String> keys = machinesJSON.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				if (machinesJSON.get(key) instanceof JSONObject) {
			           JSONObject location = (JSONObject) machinesJSON.get(key); 
			           String stationIdOrg = location.getString("stationId");
			           JSONObject statusJSON = (JSONObject) location.get("status");			           
			           if (statusJSON.isNull("OK") && stationIdOrg.equals(stationId)) {
			        	   String machineId = key;
			        	   String type = location.getString("type");
			        	   Double latitude = Double.valueOf(location.getString("latitude"));
			        	   Double longitude = Double.valueOf(location.getString("longitude"));
			        	   
			        	   Machine machine = new Machine();
			        	   machine.setLocation(new Location(latitude, longitude));
			        	   machine.setMachineId(machineId);
			        	   machine.setStation(stationId);
			        	   machine.setType(type);
			        	   
			        	   Set<String>  orderIds = new HashSet<>();
			        	   if (!location.isNull("orderId")) {
			        		   JSONObject orderIdsJSON = (JSONObject) location.get("orderId");		        	    
				        	   Iterator<String> orderId = orderIdsJSON.keys();
				        	   while (orderId.hasNext()) {
				        		   orderIds.add(orderId.next());
				        	   }
				        	   machine.setOrderId(orderIds);
			        	   }			        	   
			        	   machines.add(machine);
			           }
			    }
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return machines;
	}

	@Override
	public List<Machine> getMachineByType(List<Machine> machines, String type) {
		List<Machine> machinesType = new ArrayList<>();
		for(Machine machine : machines) {
			if(machine.getType().equals(type)) {
				machinesType.add(machine);
			}
		}
		return machinesType;
	}

	@Override
	public List<Station> getStation(Location location) {
		List<Station> stations = new ArrayList<>();
		String stationString = FirebaseHelper.doGet(FirebaseUtil.host + "/station.json") ;
		try {
			JSONObject stationJSON = new JSONObject(stationString);
			Iterator<String> keys = stationJSON.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				if (stationJSON.get(key) instanceof JSONObject) {
			           JSONObject locationJSON = (JSONObject) stationJSON.get(key);
			           Double latitude = Double.valueOf(locationJSON.getString("latitude"));
			           Double longitude = Double.valueOf(locationJSON.getString("longitude"));
			           Station station = new Station(key ,new Location(latitude, longitude));
			           stations.add(station);
			    }
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stations;
	}

	@Override
	public void updateStatus(String orderId, String machineId) {
		String orderIdUrl = FirebaseUtil.host + "machine/" + machineId + "/orderId/" + orderId + ".json";
		Integer deleteStatus = FirebaseHelper.doDelete(orderIdUrl);
		String machine = FirebaseHelper.doGet(FirebaseUtil.host + "machine/" + machineId + ".json");
		JSONObject machineJSON;
		try {
			machineJSON = new JSONObject(machine);
			if (machineJSON.isNull("orderId")) {
				String newStatus = "{\"OK\": \" \"}";
				Integer changeStatus = FirebaseHelper.doPut(FirebaseUtil.host + "machine/" + machineId + "/status.json", newStatus);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}

}
