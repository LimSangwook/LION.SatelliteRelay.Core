package SatelliteRelayServer.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import spark.QueryParamsMap;

public class BaseDAO {
	static Logger logger = Logger.getLogger(BaseDAO.class);
	String TABLE_NAME = "";
	String DDL = "";
	Connection conn = null;
	protected String columnName[] = null;
	protected String initData[][] = null;
	public BaseDAO(Connection conn) {
		this.conn = conn;
	} // BaseDAO
	
	public boolean checkNCreateTable() throws Exception {
		String query = "SELECT name FROM sqlite_master WHERE type='table' AND name ='" + TABLE_NAME + "'";
		boolean bRet = true;
		JSONArray jsonArray = executeQueryNGetJSONResult(query);
		if (jsonArray.length() == 0) {
			bRet = executeUpdate(DDL);
			logger.info("[Check and Create Table] " + DDL );
			insertInitData();
		}
		return bRet;
	} // checkNCreateTable
	
	public String getTableName() {
		return TABLE_NAME;
	}
	private void insertInitData() {
		if (columnName == null || initData == null) {
			return;
		}
		
		StringBuilder columnsSB = new StringBuilder();
		columnsSB.append(" (");
		for (String aColumnName : columnName) {
			if (aColumnName.compareTo(columnName[0]) != 0) {
				columnsSB.append(", ");
			}
			columnsSB.append("\"" + aColumnName+"\"");
		}
		columnsSB.append(")");
		
		for (int i=0 ;i < initData.length ; i++) {
			StringBuilder valuesSB = new StringBuilder();
			StringBuilder querySB = new StringBuilder();
			valuesSB.append(" (");
			for (String aData : initData[i]) {
				if (aData.compareTo(initData[i][0]) != 0) {
					valuesSB.append(", ");
				}
				valuesSB.append("\"" + aData+"\"");
			}
			valuesSB.append(")");		
			querySB.append("INSERT INTO ").append(TABLE_NAME).append(columnsSB).append(" VALUES ").append(valuesSB);
			System.out.println(querySB.toString());
			executeUpdate(querySB.toString());
		}
	} // insertInitData

	private boolean executeUpdate(String query) {
		Statement stmt = null;
		boolean ret =false;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			logger.info("[Execute Update] " + query );

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

	public int getCount() {
		int cnt = -1;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// SQL문을 실행한다.
			stmt = conn.createStatement();
			String query = "SELECT COUNT(ID) as COUNT FROM "+TABLE_NAME;
			rs = stmt.executeQuery(query);
			logger.info("[get Count] " + query );
			if (rs.next()) {
				cnt = rs.getInt("COUNT");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close(); // ResultSet를 닫는다.
			} catch (SQLException e) {
			}
		}
		return cnt;
	}

	public int getNewID() {
		int id = -1;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// SQL문을 실행한다.
			stmt = conn.createStatement();
			String query = "SELECT MAX(ID) as id FROM "+TABLE_NAME;
			rs = stmt.executeQuery(query);
			logger.info("[get New ID] " + query );


			if (rs.next()) {
				id = rs.getInt("id") + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close(); // ResultSet를 닫는다.
			} catch (SQLException e) {
			}
		}
		return id;
	}
	
	public JSONArray getListJsonArray(int listPerPage, int nowPage, boolean bASC) {
		String query = "SELECT * FROM "+TABLE_NAME + "  ORDER BY ID " + (bASC?"ASC":"DESC") + " LIMIT 15 OFFSET " + (15 * (nowPage-1)) + ";";
		return executeQueryNGetJSONResult(query);
	}
	
	public JSONArray executeQueryNGetJSONResult(String query) {
		JSONArray jsonArray = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// SQL문을 실행한다.
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			logger.info("[execute Query and get JSON] " + query );
			
			jsonArray = ConvertRS2JSON(rs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close(); // ResultSet를 닫는다.
			} catch (SQLException e) {
			}
		}
		return jsonArray;
	}

	private JSONArray ConvertRS2JSON(ResultSet rs) throws SQLException {
		JSONArray json = new JSONArray();
		ResultSetMetaData rsmd = rs.getMetaData();
		while (rs.next()) {
			int numColumns = rsmd.getColumnCount();
			JSONObject obj = new JSONObject();
			for (int i = 1; i <= numColumns; i++) {
				String column_name = rsmd.getColumnName(i);
				obj.put(column_name, rs.getObject(column_name));
			}
			json.put(obj);
		}
		return json;
	}
	
	public void delete(int id) throws Exception {
		executeUpdate("DELETE FROM " + TABLE_NAME + " where ID = " + id);
	}

	public boolean Insert(QueryParamsMap queryMap) {
		return executeUpdate(getInsertQuery(queryMap));
	}

	public boolean Update(QueryParamsMap queryMap) {
		return executeUpdate(getUpdateQuery(queryMap));
	}

	protected String getInsertQuery(QueryParamsMap queryMap) {
		System.out.println("@@@@@@ Error this is BaseDAO.getInsertQuery()");
		return "";
	}

	protected String getUpdateQuery(QueryParamsMap queryMap) {
		System.out.println("@@@@@@ Error this is BaseDAO.getUpdateQuery()");
		return "";
	}
}
