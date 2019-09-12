package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Location;
import entity.Station;
import rpc.HTTPHelper;
import rpc.HTTPUtil;

public class GoogleAPI {
	public static String API_KEY = "AIzaSyARtu9LC3znDAtBj1dC4SOitTCxvTLfAFc";
	public static String TEST_ADDRESS = "9500+Gilman+Dr,+La+Jolla,+CA+92093";
	public static double carrier_speed = 12.0;     //Speed in meters
	
	//https://maps.googleapis.com/maps/api/geocode/json?address=9500+Gilman+Dr,+La+Jolla,+CA+92093&key=AIzaSyCMUv0b5DrWEeTUrHmO57WR-LpaDudaxwM
	
	//Method 1: Convert address string to latitude and longitude.
	public static double[] addr_to_latlng(String address) {
		try { 
			String prefix = "https://maps.googleapis.com/maps/api/geocode/json?address=";
			String keyHead = "&key=";
			String request = prefix + address + keyHead + API_KEY;
			//Concatenate URL
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			
			int status = connection.getResponseCode();
			System.out.println("Status: "+status);
			if (status != 200) {
				return new double[2];
			}
			
		
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder content = new StringBuilder();
			
			
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();				
								
			JSONObject obj = new JSONObject(content.toString());
			if(!obj.isNull("results")) {
				JSONArray results = obj.getJSONArray("results");
				JSONObject item = results.getJSONObject(0);
				JSONObject geometry = item.getJSONObject("geometry");
				JSONObject location = geometry.getJSONObject("location");
				double latitude = location.getDouble("lat");
				double longitude = location.getDouble("lng");
				double[] latlng = new double[2];
				latlng[0] = latitude;
				latlng[1] = longitude;
				return latlng;
			} else {
				return new double[2];
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return new double[2];
	}
	
	
	//Method 2: Calculate time for spherical distance of two locations based on latitude and longitude
	// assuming the velocity of the carrier is 40 kph.
	public static double sphr_time(Location start, Location destination) {
		
		//Convert string to double
		double lat1 = start.getLatitude();
		double lat2 = destination.getLatitude();
		double lng1 = start.getLongitude();
		double lng2 = start.getLongitude();
		
		final int R = 6371; // Radius of the earth

	    double latDistance = Math.toRadians(lat2 - lat1);
	    double lonDistance = Math.toRadians(lng2 - lng1);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters
	    
	    return Math.round(distance / (carrier_speed * 60));
	}
	
	
	//origins=41.43206,-81.38992|-33.86748,151.20699
	// method 3: calculate road distance of two location based on latitude and longitude
	public static double road_time(Location start, Location destination) {
		
		try {
			String origins = "&origins=" + start.getLatitude() + "," + start.getLongitude();
			String destinations = "&destinations=" + destination.getLatitude() + "," + destination.getLongitude();
			String prefix = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
			String keyHead = "&key=";
			String request = prefix + origins + destinations + keyHead + API_KEY;
			
			//Concatenate URL
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			
			int status = connection.getResponseCode();
			System.out.println("Status: "+status);
			if (status != 200) {
				return -1.0;
			}
			
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder content = new StringBuilder();
			
			
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();				
			
			JSONObject obj = new JSONObject(content.toString());
			
			
			if(!obj.isNull("rows")) {
				JSONArray rows = obj.getJSONArray("rows");
				
				JSONObject elements = rows.getJSONObject(0);
				if (!elements.isNull("elements")) {
					JSONArray elements_embedded = elements.getJSONArray("elements");
					if (!elements_embedded.isNull(0)) {
						JSONObject embedded = elements_embedded.getJSONObject(0);
						if (!embedded.isNull("duration")) {
							JSONObject distance = embedded.getJSONObject("duration");
							double road_time_in_mins = distance.getDouble("value"); 
							return Math.round(road_time_in_mins / 60.0);
						}
						
						return -1.0;
					}
					
				}
				
				return -15.0;
				
			} else {
				return -2.0;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return -3.0;
	}
	// method 4: 
	public static double duration(Location start, Location destination, String machineType) {
		if (machineType.equals("robot")) {
			return road_time(start, destination);
		}else {
			return sphr_time(start, destination);
		}
	}
	// method 5: calculate price
	public static double price(double durationTime, double dimensionL, double dimensionW, 
			double dimensionH, double weightLB, double weightOC) {
		double volume = dimensionL * dimensionW * dimensionH;
		double finalPrice = durationTime / 2.0 +  volume * 0.01 + weightLB * 0.5 + weightOC * 0.5;
		return finalPrice;	
	}
	
	
	
	public static void main(String[] args)  {
//		double[] result = addr_to_lonlat(TEST_ADDRESS);
//		System.out.println(result[0]);
//		System.out.println(result[1]);
		//41.43206,-81.38992|-33.86748,151.20699
		double[] loc1 = addr_to_latlng("2130+Fulton+St,+San+Francisco,+CA+94117");
		double[] loc2 = addr_to_latlng("1310+Junipero+Serra+Blvd,+San+Francisco,+CA+94132");
		double res = road_time(new Location(loc1[0],loc1[1]), new Location(loc2[0],loc2[1]));
		System.out.print(res);
		//System.out.print(loc1[0] + "," + loc1[1]);
		//System.out.print(loc2[0] + "," + loc2[1]);
	}
	
}
