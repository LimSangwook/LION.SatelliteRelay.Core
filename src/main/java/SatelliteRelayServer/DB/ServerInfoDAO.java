package SatelliteRelayServer.DB;

import java.sql.Connection;

public class ServerInfoDAO extends BaseDAO {
	public ServerInfoDAO(Connection conn) {
		super(conn);
		TABLE_NAME = "TB_SERVER_INFO";
		DDL = "CREATE TABLE TB_SERVER_INFO " 
				+ "(ID INT PRIMARY KEY     	NOT NULL,"
				+ " SERVERTYPE		TEXT    	NOT NULL," // FTP ,ORACLE
				+ " URL				TEXT   	NOT NULL," 
				+ " PORT           	INT    	NOT NULL,"
				+ " USER		    		TEXT    NOT NULL," 
				+ " PASSWORD       	TEXT    NOT NULL,"
				+ " INTOPT1			INT     NOT NULL);"; //FTP passive
	}

}
