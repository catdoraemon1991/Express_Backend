package db;

import java.util.List;

import entity.Order;
import entity.Location;
import entity.Machine;
import entity.Station;

public interface DBConnection {
	/**
	 * Close the connection.
	 */
	public void close();
	/**
	 * save order information into database.
	 * @param userID
	 * @param order
	 * @return orderID
	 */
	public String saveOrder(String userID, Order order );
	/**
	 * Get all available machines from a certain station
	 * @param stationId
	 * @return machines
	 */
	public List<Machine> getMachine(String stationId);	
	/**
	 * Get all available machines of a certain type from a certain station
	 * @param machines
	 * @param type
	 * @return machines
	 */
	public List<Machine> getMachineByType(List<Machine> machines, String type);
	/**
	 * Get all stations
	 * @param List<Machine>
	 * @param type
	 * @return List<Machine>
	 */
	public List<Station> getStation(Location location);
	/**
	 * Get all stations
	 * @param List<Machine>
	 * @param stationId
	 * @return Station
	 */
	public Station getStationById(List<Station> stations,String stationId);
	/**
	 * Update order status to machine
	 * @param orderId
	 * @param machineId
	 */
	public void updateStatus(String orderId,String machineId) ;
	/**
	 * Mark machine as occupied
	 * @param machineId
	 */
	public void machineOccupied(String machineId) ;
}
