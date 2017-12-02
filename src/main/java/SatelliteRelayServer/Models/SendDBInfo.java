package SatelliteRelayServer.Models;

import spark.QueryParamsMap;

public class SendDBInfo {
	public String DB_URL = null;	
	public String DB_USER = null;	
	public String DB_PW = null;
	public void setFromParamsMap(QueryParamsMap map) {
		DB_URL = map.value("URL");
		DB_USER = map.value("USER");
		DB_PW = map.value("PASSWORD");
	}
}
