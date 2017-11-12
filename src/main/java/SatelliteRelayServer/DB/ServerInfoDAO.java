package SatelliteRelayServer.DB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import spark.QueryParamsMap;

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

	public boolean UpdateFTPInfo(QueryParamsMap queryMap) {
		Statement stmt = null;
		boolean ret =false;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(getUpdateFTPQuery(queryMap));
			conn.commit();
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ret;	
	}

	private String getUpdateFTPQuery(QueryParamsMap map) {
		int INTOPT1 = 0;
		if (map.value("INTOPT1") !=null && map.value("INTOPT1").compareTo("on") == 0) INTOPT1 = 1;
		
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ").append(TABLE_NAME).append(" SET ");
		sb.append(" URL=\"").append(map.value("URL")).append("\"");
		sb.append(", PORT=").append(map.value("PORT")).append("");
		sb.append(", USER=\"").append(map.value("USER")).append("\"");
		sb.append(", PASSWORD=\"").append(map.value("PASSWORD")).append("\"");
		sb.append(", INTOPT1=").append(INTOPT1).append("");
		sb.append(" WHERE SERVERTYPE = \"FTP\";");
		return sb.toString();
	}

	public boolean UpdateDBInfo(QueryParamsMap queryMap) {
		Statement stmt = null;
		boolean ret =false;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(getUpdateDBQuery(queryMap));
			conn.commit();
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ret;	
		
	}
	
	private String getUpdateDBQuery(QueryParamsMap map) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ").append(TABLE_NAME).append(" SET ");
		sb.append(" URL=\"").append(map.value("URL")).append("\"");
		sb.append(" ,USER=\"").append(map.value("USER")).append("\"");
		sb.append(" ,PASSWORD=\"").append(map.value("PASSWORD")).append("\"");
		sb.append(" WHERE SERVERTYPE = \"ORACLE\";");
		return sb.toString();
	}
}
