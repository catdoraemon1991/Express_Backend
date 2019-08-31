package db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class test {
	public static void main(String[] args)  {
		try {
			URL url = new URL("https://matrix-d2958.firebaseio.com/events/-LmS154_-vOMRsXZNrFa.json");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			
			int status = con.getResponseCode();
			System.out.println(status);
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
					    content.append(inputLine);
					}
					in.close();
			con.disconnect();
			System.out.println(content);
		} catch(Exception e) {
			
		}
		
	}
	
	
	
}
