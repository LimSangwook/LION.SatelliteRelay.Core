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
			// 드라이버를 로딩한다.
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		// 데이터베이스의 연결을 설정한다.
		try {
			String url = "jdbc:oracle:thin:@" + serviceDB.getDBURL();
			String user = serviceDB.getDBUSER();
			String pw = serviceDB.getDBPassword();
			conn = DriverManager.getConnection(url, user, pw);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
			e.printStackTrace();
			ret = false;
		} finally {
			try {
				rs.close(); // ResultSet를 닫는다.
				stmt.close();
			} catch (SQLException e) {
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
			e.printStackTrace();
			return false;
		} finally {
			try {
				rs.close(); // ResultSet를 닫는다.
				stmt.close();
			} catch (SQLException e) {
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
			e.printStackTrace();
		} finally {
			try {
				rs.close(); // ResultSet를 닫는다.
			} catch (SQLException e) {
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
			e.printStackTrace();
			return false;
		} finally {
			try {
				rs.close(); // ResultSet를 닫는다.
				stmt.close();
			} catch (SQLException e) {
				return false;
			}
		}
		return true;
	}
}
