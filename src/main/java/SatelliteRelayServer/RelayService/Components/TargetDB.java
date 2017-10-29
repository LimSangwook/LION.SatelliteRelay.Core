package SatelliteRelayServer.RelayService.Components;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import SatelliteRelayServer.SatelliteRelayDBManager;
import SatelliteRelayServer.Models.ProductInfo;

public class TargetDB {
	static Logger logger = Logger.getLogger(TargetDB.class);
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;

	public boolean init(SatelliteRelayDBManager serviceDB) {
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

	public boolean insert(ProductInfo productInfo, File afile) {
		try {
			// Statement를 가져온다.
			stmt = conn.createStatement();

			// SQL문을 실행한다.
			productInfo.appendixColumns.SEQ = getNewSEQ(stmt);
			String query = productInfo.getDBInsertQuery(afile);
			rs = stmt.executeQuery(query);

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
			rs = stmt.executeQuery(query);

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
		conn.close();
	}
}
