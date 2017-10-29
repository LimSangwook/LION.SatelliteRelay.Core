package SatelliteRelayServer.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

public class BaseDAO {
	String TABLE_NAME = "";
	String DDL = "";
	Connection conn = null;
	public BaseDAO(Connection conn) {
		this.conn = conn;
	}
	
	public boolean checkNCreateTable() throws Exception {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name ='" + TABLE_NAME + "'");
			if (rs.next() == false) {
				stmt.executeUpdate(DDL);
				conn.commit();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			stmt.close();
		}
		return true;
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
		String query = "SELECT * FROM "+TABLE_NAME + "  ORDER BY ID " + (bASC?"ASC":"DESC") + " LIMIT 20 OFFSET " + (20 * (nowPage-1)) + ";";
		return getQueryResultToJSON(query);
	}
	
	public JSONArray getQueryResultToJSON(String query) {
		JSONArray jsonArray = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// SQL문을 실행한다.
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
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
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM " + TABLE_NAME + " where ID = " + id);
			conn.commit();
		} catch (Exception e) {
			throw e;
		} finally {
			stmt.close();
		}
		return;		
	}
}
