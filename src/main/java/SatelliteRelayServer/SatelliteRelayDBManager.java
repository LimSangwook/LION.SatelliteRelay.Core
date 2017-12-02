package SatelliteRelayServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import SatelliteRelayServer.DB.BaseDAO;
import SatelliteRelayServer.DB.HistoryDAO;
import SatelliteRelayServer.DB.ProductDAO;
import SatelliteRelayServer.DB.SatelliteDTO;
import SatelliteRelayServer.DB.SatelliteDAO;
import SatelliteRelayServer.DB.ServerInfoDAO;
import SatelliteRelayServer.Models.ProductInfo;
import SatelliteRelayServer.Models.SendDBInfo;
import SatelliteRelayServer.Models.SendFTPInfo;
import spark.QueryParamsMap;

public class SatelliteRelayDBManager {
	static Logger logger = Logger.getLogger(SatelliteRelayDBManager.class);
	Connection conn = null;
	
	SatelliteDAO satelliteDAO = null;
	ProductDAO productDAO = null;
	HistoryDAO historyDAO = null;
	ServerInfoDAO serverInfoDAO = null;
//	DataGBNDAO dataGBNDAO = null;
//	DataANGBNDAO dataANGBNDAO = null;

	SendDBInfo sendDBInfo = new SendDBInfo();
	SendFTPInfo sendFTPInfo = new SendFTPInfo();

	public boolean init() {
		logger.debug("[SatelliteRelayServiceDB] Init");
		if (connectDB() == false || checkDefaultTables() == false) {
			logger.error("[SatelliteRelayServiceDB] Init");
			return false;
		}
		return true;
	}

	private boolean connectDB() {
		logger.debug("[SatelliteRelayServiceDB] connectDB");
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:./conf/SatelliteRelayV0.1.db");
			conn.setAutoCommit(false);
			satelliteDAO = new SatelliteDAO(conn);
			productDAO = new ProductDAO(conn, satelliteDAO.getTableName());
			historyDAO = new HistoryDAO(conn, productDAO.getTableName());
			serverInfoDAO = new ServerInfoDAO(conn);
//			dataGBNDAO = new DataGBNDAO(conn);
//			dataANGBNDAO = new DataANGBNDAO(conn);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			logger.error("[SatelliteRelayServiceDB] ConnectDB - " + e.getMessage());
			return false;
		}
		return true;
	}

	private boolean checkDefaultTables() {
		logger.debug("[SatelliteRelayServiceDB] checkDefaultTables");
		try {
			satelliteDAO.checkNCreateTable();
			productDAO.checkNCreateTable();
			historyDAO.checkNCreateTable();
			serverInfoDAO.checkNCreateTable();
//			dataGBNDAO.checkNCreateTable();
//			dataANGBNDAO.checkNCreateTable();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			logger.error("[SatelliteRelayServiceDB] CheckDefaultTables - " + e.getMessage());
			return false;
		}
		return true;
	}

	public List<ProductInfo> getProducts() {
		logger.info("[SatelliteRelayServiceDB] getProducts");
		return productDAO.getProducts();
	}

	public SatelliteDTO getSatelliteInfo(int satelliteID) {
		logger.info("[SatelliteRelayServiceDB] getSatelliteInfo");
		return satelliteDAO.getSatelliteInfo(satelliteID);
	}

	private boolean getFTPInfo() {
		logger.debug("[SatelliteRelayServiceDB] getFTPInfo");
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM TB_SERVER_INFO WHERE SERVERTYPE='FTP';");
			if (rs.next()) {
				sendFTPInfo.FTP_URL = rs.getString("URL");
				sendFTPInfo.FTP_PORT = rs.getInt("PORT");
				sendFTPInfo.FTP_USER = rs.getString("USER");
				sendFTPInfo.FTP_PW = rs.getString("PASSWORD");
				sendFTPInfo.FTP_isSFTP = rs.getInt("INTOPT1");
			}
			rs.close(); // ResultSet를 닫는다.
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			logger.error("TB_SERVER_INFO 테이블 내의 FTP Server 정보를 확인하세요 ");
			logger.error("[SatelliteRelayServiceDB] getFTPInfo(Check TB_SERVER_INFO Table) - " + e.getMessage());
			return false;
		} finally {
			try {
				stmt.close(); // Statement를 닫는다.
			} catch (SQLException e) {
				logger.error(e.getMessage());
				return false;
			}
		}
		return true;
	}
	
	public String getFTPServer() {	if (sendFTPInfo.FTP_URL == null)		{ getFTPInfo();	}	return sendFTPInfo.FTP_URL;	}
	public int getFTPPort() {		if (sendFTPInfo.FTP_PORT == -1) 		{ getFTPInfo();	}	return sendFTPInfo.FTP_PORT; }
	public String getFTPID() {		if (sendFTPInfo.FTP_USER == null) 	{ getFTPInfo();	}	return sendFTPInfo.FTP_USER;}
	public String getFTPPassword() {	if (sendFTPInfo.FTP_PW == null) 		{ getFTPInfo();	}	return sendFTPInfo.FTP_PW;	}
	public boolean getIsSFTP() {	if (sendFTPInfo.FTP_isSFTP == -1)	{ getFTPInfo();	}	return sendFTPInfo.FTP_isSFTP== 1 ? true : false;	}

	private boolean getDBInfo() {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM TB_SERVER_INFO WHERE SERVERTYPE='ORACLE';");
			if (rs.next()) {
				sendDBInfo.DB_URL = rs.getString("URL");
				sendDBInfo.DB_USER = rs.getString("USER");
				sendDBInfo.DB_PW = rs.getString("PASSWORD");
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			logger.error("[SatelliteRelayServiceDB] getDBInfo(Check TB_SERVER_INFO Table) - " + e.getMessage());
		} finally {
			try {
				rs.close(); // ResultSet를 닫는다.
				stmt.close(); // Statement를 닫는다.
			} catch (SQLException e) {
				return false;
			}
		}
		return true;
	}

	public String getDBURL() {		if (sendDBInfo.DB_URL == null) {		getDBInfo();		}	return sendDBInfo.DB_URL;	}
	public String getDBUSER() {		if (sendDBInfo.DB_USER == null) {	getDBInfo();		}	return sendDBInfo.DB_USER;	}
	public String getDBPassword() {	if (sendDBInfo.DB_PW == null) {		getDBInfo();		}	return sendDBInfo.DB_PW;	}

	public int getNewHistoryID() {
		return historyDAO.getNewID();
	}
	public int createHistoryLog(int productID) {
		return historyDAO.createHistoryLog(productID);
	}
	public void addHistoryLog(int historyID,  String log) {
		historyDAO.addHistoryLog(historyID, log);
	}

	public void setHistoryFile(int historyID, String fileNames) {
		historyDAO.setHistoryFile(historyID, fileNames);
	}

	public void setHistoryStatus(int historyID, String string) {
		historyDAO.setHistoryStatus(historyID, string);
	}

	public JSONArray getHistoryListJsonArray(int listPerPage, int nowPage) {
		return historyDAO.getListJsonArray(listPerPage, nowPage, false);
	}

	public int getCount(String string) {
		return getDAO(string).getCount();
	}
	
	public JSONArray getSatelliteListJsonArray(int listPerPage, int nowPage) {
		return satelliteDAO.getListJsonArray(listPerPage, nowPage, true);
	}

	public boolean delete(String string, int id) {
		BaseDAO aDAO = getDAO(string);
		try {
			aDAO.delete(id);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private BaseDAO getDAO(String string) {
		switch(string) {
		case "Satellite":
			return satelliteDAO;
		case "Product":
			return productDAO;
		case "History":
			return historyDAO;
		case "ServerInfo":
			return serverInfoDAO;
		}
		return null;
	}

	public JSONArray getProductListJsonArray(int listPerPage, int nowPage) {
		return productDAO.getListJsonArray(listPerPage, nowPage, true);
	}

	public void setHistoryFileCount(int historyID, int count) {
		historyDAO.setFileCount(historyID, count);
	}

	public void setStatusRunningToFail() {
		historyDAO.setStatusRunningToFail();
	}

	public boolean Create(String table, QueryParamsMap queryMap) {
		return getDAO(table).Insert(queryMap);
	}

	public boolean Update(String table, QueryParamsMap queryMap) {
		return getDAO(table).Update(queryMap);
	}

	public boolean UpdateFTPInfo(QueryParamsMap queryMap) {
		if (serverInfoDAO.UpdateFTPInfo(queryMap) == true) {
			sendFTPInfo.setFromParamsMap(queryMap);
			return true;
		}
		return false; 
	}

	public boolean UpdateDBInfo(QueryParamsMap queryMap) {
		if (serverInfoDAO.UpdateDBInfo(queryMap) == true) {
			sendDBInfo.setFromParamsMap(queryMap);
			return true;
		}
		return false; 
	}

	public ProductInfo getProduct(int productID) {
		logger.info("[SatelliteRelayServiceDB] getProduct(" + productID + ")");
		return productDAO.getProduct(productID);
	}

	public Integer getNewID(String string) {
		return getDAO(string).getNewID();
	}
}
