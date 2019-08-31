package db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class testDelete {
	public static void main(String[] args)  {
		try {
			URL url = new URL("https://matrix-d2958.firebaseio.com/user/user_account/-LncVArseRrHeqh3Guu0.json");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("DELETE");
			con.setRequestProperty("Content-Type", "application/json");
		
			
			int status = con.getResponseCode();
			System.out.println("Status: "+status);
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			if (status == 200) {
				while ((inputLine = in.readLine()) != null) {
				    content.append(inputLine);
				}
				in.close();				
			}					
			con.disconnect();
			System.out.println(content.toString());
		} catch(Exception e) {
			
		}
		
	}
}
