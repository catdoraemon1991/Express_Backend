package db.firebase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import db.DBConnection;
import entity.Location;
import entity.Machine;
import entity.Machine.MachineBuilder;
import entity.Order;
import entity.Station;
import rpc.HTTPHelper;

public class FirebaseConnection implements DBConnection {

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public String saveOrder(String userID, Order order) {
		String postOrder = HTTPHelper.doHTTP(FirebaseUtil.host + "order.json", order.toJSONObject(),"POST");
		
		String newOrderId = "";
		try {
			JSONObject orderJSON = new JSONObject(postOrder);
			if (! orderJSON.isNull("name")) {
				newOrderId = orderJSON.getString("name");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (newOrderId != "") {
			String addUserUrl = FirebaseUtil.host + "user/" + order.getUserId() + "/orderId/" + newOrderId + ".json";
			String userStatus = HTTPHelper.doHTTP(addUserUrl, "{\" \": \" \"}","PUT");
			String addMachineUrl = FirebaseUtil.host + "machine/" + order.getMachineId() + "/orderId/" + newOrderId + ".json";
			String machineOrderStatus = HTTPHelper.doHTTP(addMachineUrl, "{\" \": \" \"}","PUT");
			String newStatus = "{\"onUse\": \" \"}";
			String changeStatus = HTTPHelper.doHTTP(FirebaseUtil.host + "machine/" + order.getMachineId() + "/status.json", newStatus,"PUT");
		}	
		return postOrder;
	}

	@Override
	public List<Machine> getMachine(String stationId) {
		List<Machine> machines = new ArrayList<>();
		String allMachine = HTTPHelper.doHTTP(FirebaseUtil.host + "/machine.json",null,"GET") ;
		try {
			JSONObject machinesJSON =  new JSONObject(allMachine);
			Iterator<String> keys = machinesJSON.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				if (machinesJSON.get(key) instanceof JSONObject) {
			           JSONObject machineJSON = (JSONObject) machinesJSON.get(key); 
			           Machine machine = new Gson().fromJson(machineJSON.toString(),Machine.class);
			           if (machine.getStatus().getOK() != null && machine.getStationId().equals(stationId) ) {       	   
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
		String stationString = HTTPHelper.doHTTP(FirebaseUtil.host + "/station.json",null,"GET") ;
		try {
			JSONObject stationsJSON = new JSONObject(stationString);
			Iterator<String> keys = stationsJSON.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				if (stationsJSON.get(key) instanceof JSONObject) {
			           JSONObject stationJSON = (JSONObject) stationsJSON.get(key);
			           Station station = new Gson().fromJson(stationJSON.toString(), Station.class);
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
		String deleteStatus = HTTPHelper.doHTTP(orderIdUrl,null,"DELETE");
		String machine = HTTPHelper.doHTTP(FirebaseUtil.host + "machine/" + machineId + ".json",null,"GET");
		JSONObject machineJSON;
		try {
			machineJSON = new JSONObject(machine);
			if (machineJSON.isNull("orderId")) {
				String newStatus = "{\"OK\": \" \"}";
				String changeStatus = HTTPHelper.doHTTP(FirebaseUtil.host + "machine/" + machineId + "/status.json", newStatus,"PUT");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}

	@Override
	public void machineOccupied(String machineId) {
		// TODO Auto-generated method stub
		String newStatus = "{\"onUse\": \" \"}";
		String changeStatus = HTTPHelper.doHTTP(FirebaseUtil.host + "machine/" + machineId + "/status.json", newStatus,"PUT");
	}

	@Override
	public Station getStationById(List<Station> stations, String stationId) {
		if (stations.size() > 0) {
			for (Station station : stations) {
				if (station.getStationId().equals(stationId)) {
					return station;
				}
			}
		}
		return null;
	}

}
