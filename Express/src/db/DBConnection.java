package db;

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
	
}
