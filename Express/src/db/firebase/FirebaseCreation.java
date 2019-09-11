package db.firebase;

import org.json.JSONObject;

import rpc.HTTPHelper;

public class FirebaseCreation {
	public static void main(String[] args) {
		addRobot(9);
		addDrone(9);
		addStation();
	}
	
	public static void addRobot (int numsRobot) {
		try {
			//add Robot
			for (int i=0; i<numsRobot; i++) {
				String putUrl;
				JSONObject json = new JSONObject();	
				JSONObject jsonStatus= new JSONObject();
				jsonStatus.put("OK" ,"");
				json.put("type", "robot");
				json.put("status", jsonStatus);
				if(i%3 == 0) {
					json.put("latitude", 37.7919638);
					json.put("longitude", -122.4108133);
					json.put("stationId", "A");
					json.put("machineId", "robotA" + Integer.toString(i));
					putUrl = FirebaseUtil.host + "/machine/robotA" +Integer.toString(i) + ".json";
				}
				else if (i%3 == 1) {
					json.put("latitude", 37.777575);
					json.put("longitude", -122.473499);
					json.put("stationId", "B");
					json.put("machineId", "robotB" + Integer.toString(i));
					putUrl = FirebaseUtil.host + "/machine/robotB" +Integer.toString(i) + ".json";
				}
				else {
					json.put("latitude", 37.744574);
					json.put("longitude", -122.409114);
					json.put("stationId", "C");
					json.put("machineId", "robotC" + Integer.toString(i));
					putUrl = FirebaseUtil.host + "/machine/robotC" + Integer.toString(i) + ".json";
				}
				String jsonInputString = json.toString();
				String resPut = HTTPHelper.doHTTP(putUrl, jsonInputString,"PUT");
				System.out.println(resPut);
			}	
		}
		catch (Exception e) {
			
		}
	}
	
	public static void addDrone (int numsDrone) {
		try {
			//add Drone
			for (int i=0; i<numsDrone; i++) {
				String putUrl;
				JSONObject json = new JSONObject();
				JSONObject jsonStatus= new JSONObject();
				jsonStatus.put("OK" ,"");
				json.put("type", "drone");
				json.put("status", jsonStatus);
				if(i%3 == 0) {
					json.put("latitude", 37.7919638);
					json.put("longitude", -122.4108133);
					json.put("stationId", "A");
					json.put("machineId", "droneA" + Integer.toString(i));
					putUrl = FirebaseUtil.host + "/machine/droneA" +Integer.toString(i) + ".json";
				}
				else if (i%3 == 1) {
					json.put("latitude", 37.777575);
					json.put("longitude", -122.473499);
					json.put("stationId", "B");
					json.put("machineId", "droneB" + Integer.toString(i));
					putUrl = FirebaseUtil.host + "/machine/droneB" +Integer.toString(i) + ".json";
				}
				else {
					json.put("latitude", 37.744574);
					json.put("longitude", -122.409114);
					json.put("stationId", "C");
					json.put("machineId", "droneC" + Integer.toString(i));
					putUrl = FirebaseUtil.host + "/machine/droneC" +Integer.toString(i) + ".json";
				}
				String jsonInputString = json.toString();
				String resPut = HTTPHelper.doHTTP(putUrl, jsonInputString,"PUT");
				System.out.println(resPut);
			}
		}
		catch (Exception e) {
			
		}
	}
	
	public static void addStation () {
		try {
			//add Station	
			for (int i=1; i<=3; i++) {
				String putUrl = "";
				String putLocUrl = "";
				JSONObject json = new JSONObject();
				JSONObject jsonLoc = new JSONObject();
				if (i==1) {
					jsonLoc.put("latitude", 37.7919638);
					jsonLoc.put("longitude", -122.4108133);
					json.put("stationId", "A");
					putUrl = FirebaseUtil.host + "/station/A.json";
					putLocUrl = FirebaseUtil.host + "/station/A/location.json";
				}
				else if(i==2) {
					jsonLoc.put("latitude", 37.777575);
					jsonLoc.put("longitude", -122.473499);
					json.put("stationId", "B");
					putUrl = FirebaseUtil.host + "/station/B.json";
					putLocUrl = FirebaseUtil.host + "/station/B/location.json";
				}
				else {
					jsonLoc.put("latitude", 37.744574);
					jsonLoc.put("longitude", -122.409114);
					json.put("stationId", "C");
					putUrl = FirebaseUtil.host + "/station/C.json";
					putLocUrl = FirebaseUtil.host + "/station/C/location.json";
				}
				String resPut = HTTPHelper.doHTTP(putUrl, json.toString(),"PUT");
				String resLocPut = HTTPHelper.doHTTP(putLocUrl, jsonLoc.toString(),"PUT");
				System.out.println(resPut);
			}
		}
		catch (Exception e) {
			
		}
	}
}
