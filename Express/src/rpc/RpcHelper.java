package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.ShippingInfo;

public class RpcHelper {
	// Writes a JSONArray to http response.
			public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException{
				response.setContentType("application/json");
				response.setHeader("Access-Control-Allow-Origin", "*");
				PrintWriter out = response.getWriter();	
				out.print(array);
				out.close();
			}

		              // Writes a JSONObject to http response.
			public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {		
				response.setContentType("application/json");
				response.setHeader("Access-Control-Allow-Origin", "*");
				PrintWriter out = response.getWriter();	
				out.print(obj);
				out.close();
			}
			public static JSONObject readJSONObject(HttpServletRequest request) {
				StringBuilder sBuilder = new StringBuilder();
				try {
					BufferedReader reader = request.getReader();
					String line = null;
			  		while((line = reader.readLine()) != null) {
			  			 sBuilder.append(line);
			  		}
			  		return new JSONObject(sBuilder.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("111");
					e.printStackTrace();
				}
				return new JSONObject();
			}
			public static String deduplicate(String address) {
				char[] addArray = address.toCharArray();
				int slow = 0;
				int fast = 0;
				while (fast < address.length() && addArray[fast] == ' ')  fast++;
				while (fast < address.length()) {
					if (addArray[fast] != ' ') {
						addArray[slow++] = addArray[fast++];
					}else {
						while (fast < address.length() && addArray[fast] == ' ')  fast++;
						if (fast < address.length() && addArray[fast] != ',') addArray[slow++] = ' ';					
					}
				}
				if (addArray[slow - 1] == ' ') slow -= 1;
				char[] res = new char[slow];
				for(int i = 0; i < res.length; i++)  res[i] = addArray[i];
				return String.valueOf(res);
			}
			public static String replaceBlank(String address) {
				char[] addArray = address.toCharArray();
				for(int i = 0; i < addArray.length; i++) {
					if (addArray[i] == ' ') addArray[i] = '+';
				}
				return String.valueOf(addArray);
			}
}
