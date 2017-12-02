package SatelliteRelayServer.Models;

import spark.QueryParamsMap;

public class SendFTPInfo {
	public String 	FTP_URL = null;	
	public int 		FTP_PORT = -1;	
	public String 	FTP_USER = null;	
	public String 	FTP_PW = null;	
	public int 		FTP_isSFTP = -1;
	public void setFromParamsMap(QueryParamsMap map) {
		FTP_URL = map.value("URL");
		FTP_PORT = Integer.parseInt(map.value("PORT"));
		FTP_USER = map.value("USER");
		FTP_PW = map.value("PASSWORD");
		if (map.value("INTOPT1") !=null && map.value("INTOPT1").compareTo("on") == 0) FTP_isSFTP = 1 ;
		else FTP_isSFTP = 0;
	}
}
