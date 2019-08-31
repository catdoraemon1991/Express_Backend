package db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class testPut {
	public static void main(String[] args)  {
		try {
			URL url = new URL("https://matrix-d2958.firebaseio.com/user/user_account/-LncYW5zDkX4iwAWFbQE.json");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("PUT");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			
			String jsonInputString = "{\"name\": \"Doraemon\", \"job\": \"Cartoon\"}";

			
			try(OutputStream os = con.getOutputStream()) {
			    byte[] input = jsonInputString.getBytes("utf-8");
			    os.write(input, 0, input.length);           
			}
			
			int status = con.getResponseCode();
			System.out.println("Status: "+status);
			
			BufferedReader br = new BufferedReader(
					  new InputStreamReader(con.getInputStream(), "utf-8"));
			StringBuilder response = new StringBuilder();
		    String responseLine = null;
			if (status == 200) {
				while ((responseLine = br.readLine()) != null) {
			        response.append(responseLine.trim());
			    }				
			}					
			con.disconnect();
			System.out.println(response.toString());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
