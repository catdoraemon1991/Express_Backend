package db;

import db.firebase.FirebaseConnection;

public class DBConnectionFactory {
	// This should change based on the pipeline.
			private static final String DEFAULT_DB = "firebase";
			
			public static DBConnection getConnection(String db) {
				switch (db) {
				case "firebase":
					return new FirebaseConnection();
					//return null;
				case "mysql":
					//return new MySQLConnection();
					return null;
				case "mongodb":
					//return new MongoDBConnection();
					return null;
				default:
					throw new IllegalArgumentException("Invalid db:" + db);
				}

			}

			public static DBConnection getConnection() {
				return getConnection(DEFAULT_DB);
			}
}
