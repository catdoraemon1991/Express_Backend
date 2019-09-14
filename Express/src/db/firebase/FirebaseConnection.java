package db.firebase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Iterator;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import db.DBConnection;
import entity.Location;
import entity.Machine;
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
				, order.toJSONString(),HTTPUtil.PUT);
		
		if (postOrder.equals(HTTPUtil.STATUSOK)) {
			String userStatus = HTTPHelper.doHTTP(
					String.format("%s/user/%s/orderId/%s.json", FirebaseUtil.host,order.getUserId(),order.getOrderId())
					, FirebaseUtil.empty,HTTPUtil.PUT);
			String machineOrderStatus = HTTPHelper.doHTTP(
					String.format("%s/machine/%s/orderId/%s.json", FirebaseUtil.host, order.getMachineId(), order.getOrderId())
					, FirebaseUtil.empty,HTTPUtil.PUT);
			String changeStatus = HTTPHelper.doHTTP(
					String.format("%s/machine/%s/status.json", FirebaseUtil.host, order.getMachineId()) 
					, FirebaseUtil.statusOnUse,HTTPUtil.PUT);
		}	
		return postOrder;
	}

	@Override
	public List<Machine> getMachine(String stationId) {
		List<Machine> machines = new ArrayList<>();
		String allMachine = HTTPHelper.doHTTP(String.format("%s.json", FirebaseUtil.machineUrl),null,HTTPUtil.GET) ;
		Type machinesMapType =  new TypeToken<HashMap<String, Machine>>(){}.getType();
		HashMap<String, Machine> machinesMap = new Gson().fromJson(allMachine, machinesMapType);
		if (stationId != null) {
			Iterator<String> mapIt = machinesMap.keySet().iterator();
			while(mapIt.hasNext()) {
				Machine machine = machinesMap.get(mapIt.next());
				if (!machine.getStationId().equals(stationId) || machine.getStatus().getOK() == null){
					mapIt.remove();
				}
			}
		}
		machines = new ArrayList<>(machinesMap.values());	
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
		String allStation = HTTPHelper.doHTTP(String.format("%s.json", FirebaseUtil.stationUrl),null,HTTPUtil.GET) ;
		Type stationsMapType =  new TypeToken<HashMap<String, Station>>(){}.getType();
		HashMap<String, Station> stationsMap = new Gson().fromJson(allStation, stationsMapType);
		stations = new ArrayList<>(stationsMap.values());
		return stations;
	}

	@Override
	public void updateStatus(String orderId, String machineId) {
		String deleteStatus = HTTPHelper.doHTTP(String.format("%s/%s/orderId/%s.json", FirebaseUtil.machineUrl, machineId, orderId)
				, null,HTTPUtil.DELETE);
		String orderIds = HTTPHelper.doHTTP(String.format("%s/%s/orderId.json",FirebaseUtil.machineUrl, machineId),null,HTTPUtil.GET);
		if (orderIds == null) {
			String changeStatus = HTTPHelper.doHTTP(String.format("%s/%s/status.json", FirebaseUtil.machineUrl, machineId)
					, FirebaseUtil.statusOK,HTTPUtil.PUT);
		}			
	}

	@Override
	public void machineOccupied(String machineId) {
		// TODO Auto-generated method stub
		String changeStatus = HTTPHelper.doHTTP(String.format("%s/%s/status.json", FirebaseUtil.machineUrl, machineId)
				, FirebaseUtil.statusOnUse, HTTPUtil.PUT);
	}

	@Override
	public Station getStationById(String stationId) {
		String stationStr = HTTPHelper.doHTTP(String.format("%s/%s.json", FirebaseUtil.stationUrl, stationId)
				, null,HTTPUtil.GET);
		return new Gson().fromJson(stationStr, Station.class);
	}

	@Override
	public Machine getMachineById(String machineId) {
		String machineStr = HTTPHelper.doHTTP(String.format("%s/%s.json", FirebaseUtil.machineUrl, machineId)
				, null,HTTPUtil.GET);
		return new Gson().fromJson(machineStr, Machine.class);
	}

	@Override
	public Order getOrderById(String orderId) {
		String getOrder = HTTPHelper.doHTTP(String.format("%s/order/%s.json", FirebaseUtil.host, orderId)
				, null,HTTPUtil.GET);
		return new Gson().fromJson(getOrder, Order.class);
	}

}
