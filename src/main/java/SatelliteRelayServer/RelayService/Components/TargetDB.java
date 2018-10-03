package SatelliteRelayServer.RelayService.Components;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import SatelliteRelayServer.SatelliteRelayDBManager;
import SatelliteRelayServer.DB.SatelliteDTO;
import SatelliteRelayServer.Models.ProductInfo;

public class TargetDB {
	static Logger logger = Logger.getLogger(TargetDB.class);
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	SatelliteRelayDBManager serviceDB = null;
	
	public boolean init(SatelliteRelayDBManager serviceDB) {
		this.serviceDB = serviceDB;
		try {
			logger.info("[INIT DB] JDBC Drive Loading");
			// 드라이버를 로딩한다.
			Class.forName("oracle.jdbc.driver.OracleDriver");
			logger.info("[INIT DB] Completed JDBC Drive Loading");
		} catch (ClassNotFoundException e) {
			logger.info(" Error Exception(init ClassNotFoundException) : " + e.toString());
			e.printStackTrace();
			return false;
		}
		// 데이터베이스의 연결을 설정한다.
		try {
			logger.info("[INIT DB] Start Get DB Connection");
			String url = "jdbc:oracle:thin:@" + serviceDB.getDBURL();
			String user = serviceDB.getDBUSER();
			String pw = serviceDB.getDBPassword();
			logger.info("[INIT DB] DB url : " + url + " \t USER : " + user);
			conn = DriverManager.getConnection(url, user, pw);
			logger.info("[INIT DB] Completed Get DB Connection");
		} catch (SQLException e) {
			logger.info(" Error Exception(init SQLException) : " + e.toString());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean existFileNameInDB(String filename) {
		boolean ret = false;
		try {
			stmt = conn.createStatement();
			// SQL문을 실행한다.
			String query = "SELECT count(*) as cnt FROM TB_IDENTITY_LIST WHERE IDENTIFIER='" + filename + "'";
			logger.info(" Execute Query : " + query);
			rs = stmt.executeQuery(query);
			logger.info("[check Exist FileName in Target DB(TB_IDENTITY_LIST)] " + query );

			if (rs.next() && rs.getInt("cnt") > 0) {
				ret = true;
			}
		} catch (Exception e) {
			logger.info(" Error Exception(existFileNameInDB Exception) : " + e.toString());
			e.printStackTrace();
			ret = false;
		} finally {
			try {
				rs.close(); // ResultSet를 닫는다.
				stmt.close();
			} catch (SQLException e) {
				logger.info(" Error Exception(existFileNameInDB SQLException) : " + e.toString());
				ret = false;
			}
		}
		return ret;
	}
	public boolean insert(ProductInfo productInfo, File afile) {
		try {
			// Statement를 가져온다.
			stmt = conn.createStatement();
			SatelliteDTO satelliteInfo = serviceDB.getSatelliteInfo(productInfo.satelliteID);
			
			// SQL문을 실행한다.
			productInfo.appendixColumns.SEQ = getNewSEQ(stmt);
			String query = productInfo.getDBInsertQuery(afile, satelliteInfo);
			logger.info(" Execute Query : " + query);
			rs = stmt.executeQuery(query);
			logger.info("[Insert Target DB(TB_IDENTITY_LIST)] " + query );
			if (rs.next()) {
				logger.info(" DBINSERT !!!");
			}
		} catch (Exception e) {
			logger.info(" Error Exception(insert Exception) : " + e.toString());
			e.printStackTrace();
			return false;
		} finally {
			try {
				rs.close(); // ResultSet를 닫는다.
				stmt.close();
			} catch (SQLException e) {
				logger.info(" Error Exception(insert SQLException) : " + e.toString());
				return false;
			}
		}
		return true;
	}

	private int getNewSEQ(Statement stmt) {
		int seq = -1;
		try {
			// SQL문을 실행한다.
			String query = "SELECT MAX(SEQ) as SEQ FROM TB_IDENTITY_LIST";
			logger.info(" Execute Query : " + query);
			
			rs = stmt.executeQuery(query);
			logger.info("[get New SEQ in Target DB(TB_IDENTITY_LIST)] " + query );

			if (rs.next()) {
				seq = rs.getInt("SEQ") + 1;
			}
		} catch (Exception e) {
			logger.info(" Error Exception(getNewSEQ Exception) : " + e.toString());
			e.printStackTrace();
		} finally {
			try {
				rs.close(); // ResultSet를 닫는다.
			} catch (SQLException e) {
				logger.info(" Error Exception(getNewSEQ SQLException) : " + e.toString());
			}
		}
		return seq;
	}

	public void close() throws SQLException {
		if (conn != null) {
			conn.close();
		}
	}
	public boolean update(ProductInfo productInfo, File afile) {
		try {
			// Statement를 가져온다.
			stmt = conn.createStatement();

			// SQL문을 실행한다.
			SatelliteDTO satelliteInfo = serviceDB.getSatelliteInfo(productInfo.satelliteID);
			String query = productInfo.getDBUpdateQuery(afile, satelliteInfo);
			logger.info(" Execute Query : " + query);
			logger.info("[Update Target DB(TB_IDENTITY_LIST)] " + query );
			
			rs = stmt.executeQuery(query);

			if (rs.next()) {
				logger.info(" DB Update !!!");
			}
		} catch (Exception e) {
			logger.info(" Error Exception(update Exception) : " + e.toString());
			e.printStackTrace();
			return false;
		} finally {
			try {
				rs.close(); // ResultSet를 닫는다.
				stmt.close();
			} catch (SQLException e) {
				logger.info(" Error Exception(update SQLException) : " + e.toString());
				return false;
			}
		}
		return true;
	}
}
