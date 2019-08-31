package db;

import java.util.List;

import entity.Order;
import entity.Robot;

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
	 * Get all available robots from a certain station
	 * @param stationId
	 * @return List<Robot>
	 */
	public List<Machine> getMachine(String stationId);	
	/**
	 * Get all available robots from a certain station
	 * @param List<Robot>
	 * @param type
	 * @return List<Robot>
	 */
	public List<Machine> getMachineByType(List<Machine>, String type);
	
	public List<Station> getStation(Location location);
	//huyufeiceshi
	
}
