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
import rpc.HTTPUtil;

public class FirebaseConnection implements DBConnection {

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public String saveOrder(String userID, Order order) {
		String postOrder = HTTPHelper.doHTTP(String.format("%s/order/%s.json", FirebaseUtil.host, order.getOrderId())
				, order.toJSONString(),HTTPUtil.put);
		
		if (postOrder.equals(HTTPUtil.StatusOK)) {
			String userStatus = HTTPHelper.doHTTP(
					String.format("%s/user/%s/orderId/%s.json", FirebaseUtil.host,order.getUserId(),order.getOrderId())
					, FirebaseUtil.empty,HTTPUtil.put);
			String machineOrderStatus = HTTPHelper.doHTTP(
					String.format("%s/machine/%s/orderId/%s.json", FirebaseUtil.host, order.getMachineId(), order.getOrderId())
					, FirebaseUtil.empty,HTTPUtil.put);
			String changeStatus = HTTPHelper.doHTTP(
					String.format("%s/machine/%s/status.json", FirebaseUtil.host, order.getMachineId()) 
					, FirebaseUtil.statusOnUse,HTTPUtil.put);
		}	
		return postOrder;
	}

	@Override
	public List<Machine> getMachine(String stationId) {
		List<Machine> machines = new ArrayList<>();
		String allMachine = HTTPHelper.doHTTP(String.format("%s.json", FirebaseUtil.machineUrl),null,HTTPUtil.get) ;
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
		String stationString = HTTPHelper.doHTTP(String.format("%s.json", FirebaseUtil.stationUrl),null,HTTPUtil.get) ;
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
		String deleteStatus = HTTPHelper.doHTTP(String.format("%s/%s/orderId/%s.json", FirebaseUtil.machineUrl, machineId, orderId)
				, null,HTTPUtil.delete);
		String orderIds = HTTPHelper.doHTTP(String.format("%s/%s/orderId.json",FirebaseUtil.machineUrl, machineId),null,HTTPUtil.get);
		System.out.println(orderIds);
		if (orderIds == null) {
			String changeStatus = HTTPHelper.doHTTP(String.format("%s/%s/status.json", FirebaseUtil.machineUrl, machineId)
					, FirebaseUtil.statusOK,HTTPUtil.put);
		}			
	}

	@Override
	public void machineOccupied(String machineId) {
		// TODO Auto-generated method stub
		String changeStatus = HTTPHelper.doHTTP(String.format("%s/%s/status.json", FirebaseUtil.machineUrl, machineId)
				, FirebaseUtil.statusOnUse,HTTPUtil.put);
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
