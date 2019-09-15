package rpc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import db.DBConnection;
import db.DBConnectionFactory;
import db.firebase.FirebaseConnection;
import db.firebase.FirebaseUtil;
import entity.Location;
import entity.Machine;
import entity.Station;

public class HTTPHelper {
	/**
	 * Do a HTTP request.
	 * @param urlLink
	 * @param jsonInputString
	 * @param method
	 * method can only be "GET", "DELETE", "POST", or "PUT"; misspelling will give output as null
	 * when using "GET" and "DELETE", jsonInputString should be null, or your output will be null
	 * when using "POST" and "PUT", jsonInputString should be not null, or your output will be null
	 */
	public static String doHTTP(String urlLink, String jsonInputString, String method) {
		if(method.equals(HTTPUtil.GET) || method.equals(HTTPUtil.DELETE)) {
			if (jsonInputString != null) return null;
		}else if (method.equals(HTTPUtil.POST) || method.equals(HTTPUtil.PUT)) {
			if (jsonInputString == null) return null;
		}else {
			return null;
		}
		StringBuffer content = new StringBuffer();
		int status = 0;
		try {
			URL url = new URL(urlLink);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(method);
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			
			if (jsonInputString != null) {
				con.setDoOutput(true);
				try(OutputStream os = con.getOutputStream()) {
				    byte[] input = jsonInputString.getBytes("utf-8");
				    os.write(input, 0, input.length);           
				}
			}
			
			status = con.getResponseCode();
			System.out.println("do " + method + " Status: "+status);
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(con.getInputStream(), "utf-8"));
			String inputLine;			
			if (status == 200) {
				while ((inputLine = in.readLine()) != null) {
				    content.append(inputLine);
				}
				in.close();				
			}					
			con.disconnect();
		} catch(Exception e) {
			e.printStackTrace();
		}
		if (method.equals(HTTPUtil.GET) || method.equals(HTTPUtil.POST)) {
			if (content.toString().equals("null"))  return null;
			return content.toString();
		}else {
			return String.valueOf(status);
		}
		
	};
	
	
	public static void main(String[] args) throws JSONException {
		String  getUrl = "https://express-1c6b7.firebaseio.com/user2.json";
		//String jsonInputString = "{\"name\": \"Doraemon\", \"job\": \"Best idol\"}";
		String jsonInputString = "{\"user3\":[{\"name\":\"cat\"},{\"age\":18}]}";
		String postUrl = FirebaseUtil.host + "user2.json";
		String deleteUrl = FirebaseUtil.host + "user2.json";
		String putUrl = FirebaseUtil.host + "/user2.json";
		//String resPut = doHTTP(getUrl,jsonInputString,"PUT");
		//JSONObject newjson = new JSONObject(resGet);
		//JSONObject getJSON = new JSONObject(resGet);
		//JSONObject robotB1 =  (JSONObject) getJSON.get("robotB1");
		DBConnection db = DBConnectionFactory.getConnection();
		//List<Machine> machines = db.getMachine(null);
		List<Station> stations = db.getStation(new Location());
		//System.out.println(station.getLocation().getLatitude());


		
		Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("PST"));

		long currentDate = calendar1.getTimeInMillis();
		calendar1.setTimeInMillis(currentDate+1000*3);	
		//System.out.println(currentDate);
		Date date = calendar1.getTime();
		System.out.println(date);
		
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				System.out.println("Task performed on: " + new Date() + "n" +
			              "Thread's name: " + Thread.currentThread().getName());
			}
		};
		timer.schedule(task, date);
	}
}
