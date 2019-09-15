package db.firebase;

public class FirebaseUtil {
	public static final String host = "https://express-1c6b7.firebaseio.com";
	public static final String machineUrl = String.format("%s/machine",FirebaseUtil.host);
	public static final String stationUrl = String.format("%s/station",FirebaseUtil.host);
	public static final String statusOK = "{\"OK\": \" \"}";
	public static final String statusOnUse = "{\"onUse\": \" \"}";
	public static final String empty = "{\" \": \" \"}";
}
