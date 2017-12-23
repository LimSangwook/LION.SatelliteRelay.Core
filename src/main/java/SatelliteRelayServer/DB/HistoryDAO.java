package SatelliteRelayServer.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;
import org.json.JSONArray;

public class HistoryDAO extends BaseDAO{
	static Logger logger = Logger.getLogger(HistoryDAO.class);
	String PRODUCT_TABLE_NAME;
	public HistoryDAO(Connection conn, String ProductTBName) {
		super(conn);
		PRODUCT_TABLE_NAME = ProductTBName;
		TABLE_NAME = "TB_RELAY_HISTORY";
		DDL = "CREATE TABLE TB_RELAY_HISTORY " 
				+ "(ID INT PRIMARY KEY     	NOT NULL, "
				+ " PRODUCT_ID		INT    	NOT NULL, " // FTP ,ORACLE
				+ " STATE  		    TEXT   	NOT NULL, " // Completed, Failed
				+ " START_DATETIME	TEXT   	NOT NULL, " // 
				+ " FILENAME		    TEXT   	NOT NULL, "
				+ " FILE_COUNT		INTEGER, "
				+ " LOGS           	TEXT);";
	}
	public JSONArray getListJsonArray(int listPerPage, int nowPage, boolean bASC) {
		String query = "SELECT A.*, B.NAME as PRODUCT_NAME FROM "+TABLE_NAME + " as A,"+PRODUCT_TABLE_NAME+" as B WHERE A.PRODUCT_ID=B.ID ORDER BY ID " + (bASC?"ASC":"DESC") + " LIMIT 15 OFFSET " + (15 * (nowPage-1)) + ";";
		return executeQueryNGetJSONResult(query);
	}
	public synchronized int createHistoryLog(int productID) {
		int id = getNewID();
		String state = "RUNNING";
		
		LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String StartDateTime = now.format(formatter);
        
		Statement stmt = null;
		try {
			// SQL문을 실행한다.
			stmt = conn.createStatement();
			String query = "INSERT INTO TB_RELAY_HISTORY " 
						+ "(ID,PRODUCT_ID,STATE,START_DATETIME,FILENAME,LOGS) " 
						+ "VALUES " 
						+ "("+id+",'"+productID+"','"+state+"','"+StartDateTime+"','','')";
			stmt.execute(query);
			logger.info("[cteate History Log] " + query );

			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				stmt.close(); // ResultSet를 닫는다.
			} catch (SQLException e) {
			}
		}
		return id;
	}

	public synchronized void addHistoryLog(int historyID, String log) {
		String oldLog = getHistoryLog(historyID);
		String newLog = oldLog + log +  "\n" ; 
		Statement stmt = null;
		try {
			// SQL문을 실행한다.
			stmt = conn.createStatement();
			String query = "UPDATE TB_RELAY_HISTORY " 
					+ "SET logs = '"+ newLog + "' "
					+ "WHERE ID="+historyID;
			boolean ret = stmt.execute(query);
			logger.info("[add History Log] " + query );

			conn.commit();
			if (ret == false) {
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close(); // ResultSet를 닫는다.
			} catch (SQLException e) {
			}
		}
	}

	public void setStatusRunningToFail() {
		Statement stmt = null;
		try {
			// SQL문을 실행한다.
			stmt = conn.createStatement();
			String query = "UPDATE TB_RELAY_HISTORY " 
					+ "SET STATE = 'FAIL' "
					+ "WHERE STATE = 'RUNNING' ";
			boolean ret = stmt.execute(query);
			logger.info("[set Status Running -> Fail] " + query );

			conn.commit();
			if (ret == false) {
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close(); // ResultSet를 닫는다.
			} catch (SQLException e) {
			}
		}
	}
	
	private String getHistoryLog(int historyID) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String query = "SELECT LOGS FROM TB_RELAY_HISTORY WHERE ID =" + historyID;
			rs = stmt.executeQuery(query );
			logger.info("[get History Log] " + query );

			while (rs.next()) {
				return rs.getString("LOGS");
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		} finally {
			try {
				rs.close(); // ResultSet를 닫는다.
				stmt.close(); // Statement를 닫는다.
			} catch (SQLException e) {
			}
		}
		return "";
	}

	public void setHistoryFile(int historyID, String fileNames) {
		Statement stmt = null;
		try {
			// SQL문을 실행한다.
			stmt = conn.createStatement();
			String query = "UPDATE TB_RELAY_HISTORY " 
					+ "SET FILENAME = '"+ fileNames + "' "
					+ "WHERE ID="+historyID;
			boolean ret = stmt.execute(query);
			logger.info("[set History File] " + query );

			conn.commit();
			if (ret == false) {
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close(); // ResultSet를 닫는다.
			} catch (SQLException e) {
			}
		}
	}

	public void setHistoryStatus(int historyID, String string) {
		Statement stmt = null;
		try {
			// SQL문을 실행한다.
			stmt = conn.createStatement();
			String query = "UPDATE TB_RELAY_HISTORY " 
					+ "SET STATE = '"+ string + "' "
					+ "WHERE ID="+historyID;
			boolean ret = stmt.execute(query);
			logger.info("[set History Status] " + query );

			conn.commit();
			if (ret == false) {
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close(); // ResultSet를 닫는다.
			} catch (SQLException e) {
			}
		}
	}
	
	public void setFileCount(int historyID, int count) {
		Statement stmt = null;
		try {
			// SQL문을 실행한다.
			stmt = conn.createStatement();
			String query = "UPDATE TB_RELAY_HISTORY " 
					+ "SET FILE_COUNT = "+ count + " "
					+ "WHERE ID="+historyID;
			boolean ret = stmt.execute(query);
			logger.info("[set History File Count] " + query );

			conn.commit();
			if (ret == false) {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close(); // ResultSet를 닫는다.
			} catch (SQLException e) {
			}
		}
	}

}
