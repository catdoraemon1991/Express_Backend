package db.firebase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import entity.Location;
import entity.Machine;
import entity.Station;

public class FirebaseHelper {
	public static String doGet(String urlLink) {
		StringBuffer content = new StringBuffer();
		try {
			URL url = new URL(urlLink);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			
			int status = con.getResponseCode();
			System.out.println("doGet Status: "+status);
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(con.getInputStream()));
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
		return content.toString();
	}
	public static Integer doDelete(String urlLink) {
		StringBuffer content = new StringBuffer();
		Integer status = null;
		try {
			URL url = new URL(urlLink);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("DELETE");
			con.setRequestProperty("Content-Type", "application/json");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			
			status = con.getResponseCode();
			System.out.println("doDelete Status: "+status);
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(con.getInputStream()));
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
		//return content.toString();
		return status;
	}
	public static String doPost(String urlLink, String jsonInputString) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(urlLink);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			
			//String jsonInputString = "{\"name\": \"Dora\", \"job\": \"SDE\"}";
			
			try(OutputStream os = con.getOutputStream()) {
			    byte[] input = jsonInputString.getBytes("utf-8");
			    os.write(input, 0, input.length);           
			}
			
			int status = con.getResponseCode();
			System.out.println("doPost Status: "+status);
			
			BufferedReader br = new BufferedReader(
					  new InputStreamReader(con.getInputStream(), "utf-8"));
			
		    String responseLine = null;
			if (status == 200) {
				while ((responseLine = br.readLine()) != null) {
			        response.append(responseLine.trim());
			    }				
			}					
			con.disconnect();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return response.toString();		
	}
	public static Integer doPut(String urlLink, String jsonInputString) {
		StringBuilder response = new StringBuilder();
		Integer status = null;
		try {
			URL url = new URL(urlLink);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("PUT");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			
			//String jsonInputString = "{\"name\": \"Dora\", \"job\": \"SDE\"}";
			
			try(OutputStream os = con.getOutputStream()) {
			    byte[] input = jsonInputString.getBytes("utf-8");
			    os.write(input, 0, input.length);           
			}
			
			status = con.getResponseCode();
			System.out.println("doPut Status: "+status);
			
			BufferedReader br = new BufferedReader(
					  new InputStreamReader(con.getInputStream(), "utf-8"));
			
		    String responseLine = null;
			if (status == 200) {
				while ((responseLine = br.readLine()) != null) {
			        response.append(responseLine.trim());
			    }				
			}					
			con.disconnect();
		} catch(Exception e) {
			e.printStackTrace();
		}
		//return response.toString();
		return status;
		
	}
	public static void main(String[] args) {
		String  getUrl = FirebaseUtil.host + "machine.json";
		//String jsonInputString = "{\"name\": \"Doraemon\", \"job\": \"Best idol\"}";
		String jsonInputString = "{\" \": \" \"}";
		String postUrl = FirebaseUtil.host + "user2.json";
		String deleteUrl = FirebaseUtil.host + "user/-LnddvEBLrD4P4vntNLh.json";
		String putUrl = FirebaseUtil.host + "user/" + "-LnddvEBLrD4P4vntNLh" + "/orderId/" + "456" + ".json";
		//String resGet = doGet(getUrl);
		//JSONObject newjson = new JSONObject(resGet);
		//System.out.println(resGet);
		//String resPost = doPost(postUrl, jsonInputString);
		//Integer resDelete =  doDelete(deleteUrl);
		//Integer resPut = doPut(putUrl, jsonInputString);
		
//		String[] array = {"1","2","3","4"};
//		String toArray = "1,2,3";
//		System.out.println(toArray);
//		String[] test = toArray.split(",");
//		System.out.println(test[0]);
		
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
