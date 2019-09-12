package external;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Location;
import entity.Station;

public class GoogleAPI {
	// method 1: convert address string to latitude and longitude
	public static double[] addr_to_lonlat(String address) {
		double[] res = {37.7319638,-122.4508133};
		return res;
	}
	
	// method 2: calculate time for spherical distance of two locations based on latitude and longitude
	public static double sphr_time(String startLat, String startLon,String desLat, String desLon) {
		//in minutes
		return 30.0;
	}
	
	// method 3: calculate time for road directions of two locations based on latitude and longitude
	public static double road_time(String startLat, String startLon,String desLat, String desLon) {
		return 25.0;
	}
	// method 4: 
	public static double duration(String startLat, String startLon,String desLat, String desLon, String machineType) {
		if (machineType.equals("robot")) {
			return road_time(startLat, startLon, desLat, desLon);
		}else {
			return sphr_time(startLat, startLon, desLat, desLon);
		}
	}
	// method 5: calculate price
	public static double price(double durationTime, double dimensionL, double dimensionW, 
			double dimensionH, double weightLB, double weightOC) {
		double volume = dimensionL * dimensionW * dimensionH;
		double finalPrice = durationTime / 2.0 +  volume * 0.01 + weightLB * 0.5 + weightOC * 0.5;
		return finalPrice;	
	}
	
	
	
	public static void main(String[] args) {
		DBConnection db = DBConnectionFactory.getConnection();
		Station station = db.getStationById(db.getStation(new Location()), "B");
		System.out.println("location: " + station.getLocation().getLatitude() + "," + station.getLocation().getLongitude());
	}
	
}
