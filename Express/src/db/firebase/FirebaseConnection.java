package db.firebase;

import java.util.List;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Machine> getMachine(String stationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Machine> getMachineByType(List<Machine> machines, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Station> getStation(Location location) {
		// TODO Auto-generated method stub
		return null;
	}

}
