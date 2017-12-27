package SatelliteRelayServer.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import SatelliteRelayServer.Models.ProductInfoAppendix;
import SatelliteRelayServer.Models.ProductInfo;
import SatelliteRelayServer.Models.ProductInfo.PROCESS_TYPE;
import SatelliteRelayServer.Models.ProductInfo.SCHEDULE_TYPE;
import spark.QueryParamsMap;

public class ProductDAO extends BaseDAO {
	static Logger logger = Logger.getLogger(ProductDAO.class);
	String TABLE_SATELLITE_NAME;

	public ProductDAO(Connection conn, String satelliteTBName) {
		super(conn);
		TABLE_NAME = "TB_PRODUCT";
		TABLE_SATELLITE_NAME = satelliteTBName;
		DDL = "CREATE TABLE `TB_PRODUCT` ( "
				+ "    `ID`	INT NOT NULL, "
				+ "    `SATELLITE_ID`	INT NOT NULL, "
				+ "    `NAME`	TEXT NOT NULL, "
				+ "    `PROCESS_TYPE`	TEXT NOT NULL, " //DB and FTP, DB Only, FTP Only				
				+ "    `SOURCEPATH`	TEXT, "
				+ "    `TARGETPATH`	TEXT, "
				+ "    `TARGETPATH_TYPE`	TEXT, " //PATH_ONLY, YYYYMM, YYYY/MM, YYYY/MM/DD
				+ "    `FILTER_TYPE`	TEXT, " //SIMPLE, REGEXP
				+ "    `FILTER_SIMPLE`	TEXT, "
				+ "    `FILTER_REGEXP`	TEXT NOT NULL, "
				+ "    `SCHEDULE_TYPE`	TEXT NOT NULL, " //EVERY_DAY,11:00:00
				+ "    `SCHEDULE_TIME`	TEXT NOT NULL, " //EVERY_DAY,11:00:00
				+ "    `COORD_UL`	TEXT, " //여기서부터 아래는 단순 입력용
				+ "    `COORD_LR`	TEXT, "
				+ "    `REG_DATE`	TEXT, "
				+ "    `PIXEL_ROW`	INTEGER, "
				+ "    `PIXEL_COL`	INTEGER, "
				+ "    `DATA_TYPE`	TEXT, "
				+ "    `DATA_FORMAT`	TEXT, "
				+ "    `PROJECTION`	TEXT, "
				+ "    `QUICK_LOOK`	TEXT, "
				+ "    `DATA_GBN`	TEXT, "
				+ "    `DATA_AN_GBN`	TEXT, "
				+ "    `FILE_STATUS`	TEXT, "
				+ "    `MOUNT_POINT`	TEXT, "
				+ "    `DATA_OPEN`	TEXT, "
				+ "    'SURVEY_DATE_START_INDEX`	INTEGER DEFAULT -1, "
				+ "    `SURVEY_DATE_END_INDEX`	INTEGER DEFAULT -1, "
				+ "    `SURVEY_TIME_START_INDEX`	INTEGER DEFAULT -1, "
				+ "    `SURVEY_TIME_END_INDEX`	INTEGER DEFAULT -1, "
				+ "    PRIMARY KEY(`ID`) "
				+ "  );";
	}
	public JSONArray getListJsonArray(int listPerPage, int nowPage, boolean bASC) {
		String query = "SELECT A.*, B.NAME AS SATELLITE_NAME FROM "+TABLE_NAME + " as A, "+  TABLE_SATELLITE_NAME + " as B "
				+ " WHERE A.SATELLITE_ID = B.ID "
				+ " ORDER BY ID " + (bASC?"ASC":"DESC") + " LIMIT 15 OFFSET " + (15 * (nowPage-1)) + ";";
		return executeQueryNGetJSONResult(query);
	}
	
	public List<ProductInfo> getProducts() {
		List<ProductInfo> list = new ArrayList<ProductInfo>();

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String query = "SELECT A.*, B.NAME as SATE_NAME FROM TB_PRODUCT as A, TB_SATELLITE as B WHERE A.SATELLITE_ID == B.ID;";
			rs = stmt.executeQuery(query);
			logger.info("[get Products] " + query );

			while (rs.next()) {
				ProductInfoAppendix appendixColumns = getProductAppendixColumns(rs);
				ProductInfo info = getRelayProductInfo(rs, appendixColumns);
				list.add(info);
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
		return list;
	}
	protected String getInsertQuery(QueryParamsMap map) {
		StringBuilder sb = new StringBuilder();
		StringBuilder values = new StringBuilder();
		int productID = getNewID();
		values.append(" (").append(productID)
			.append(",").append(map.value("SATELLITE_ID"))
			.append(",\"").append(map.value("NAME")).append("\"")
			.append(",\"").append(map.value("PROCESS_TYPE")).append("\"")
			.append(",\"").append(map.value("SOURCEPATH")).append("\"")
			.append(",\"").append(map.value("TARGETPATH")).append("\"")
			.append(",\"").append(map.value("TARGETPATH_TYPE")).append("\"")
			.append(",\"").append(map.value("FILTER_TYPE")).append("\"")
			.append(",\"").append(map.value("FILTER_SIMPLE")).append("\"")
			.append(",\"").append(map.value("FILTER_REGEXP")).append("\"")
			.append(",\"").append(map.value("SCHEDULE_TYPE")).append("\"")
			.append(",\"").append(map.value("SCHEDULE_TIME")).append("\"")
			.append(",\"").append(map.value("COORD_UL")).append("\"")
			.append(",\"").append(map.value("COORD_LR")).append("\"")
			.append(",\"").append(map.value("REG_DATE")).append("\"")
			.append(",").append(map.value("PIXEL_ROW"))
			.append(",").append(map.value("PIXEL_COL"))
			.append(",\"").append(map.value("DATA_TYPE")).append("\"")
			.append(",\"").append(map.value("DATA_FORMAT")).append("\"")
			.append(",\"").append(map.value("PROJECTION")).append("\"")
			.append(",\"").append(map.value("QUICK_LOOK")).append("\"")
			.append(",\"").append(map.value("DATA_GBN")).append("\"")
			.append(",\"").append(map.value("DATA_AN_GBN")).append("\"")
			.append(",\"").append(map.value("FILE_STATUS")).append("\"")
			.append(",\"").append(map.value("MOUNT_POINT")).append("\"")
			.append(",\"").append(map.value("DATA_OPEN")).append("\"")
			.append(",").append(map.value("SURVEY_DATE_START_INDEX"))
			.append(",").append(map.value("SURVEY_DATE_END_INDEX"))
			.append(",").append(map.value("SURVEY_TIME_START_INDEX"))
			.append(",").append(map.value("SURVEY_TIME_END_INDEX")).append(")");
		sb.append("INSERT INTO ").append(TABLE_NAME).append(" VALUES ").append(values);
		return sb.toString();
	}
	
	protected String getUpdateQuery(QueryParamsMap map) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ").append(TABLE_NAME).append(" SET ");
		
		sb.append(" SATELLITE_ID=").append(map.value("SATELLITE_ID")).append("")
			.append(", NAME=\"").append(map.value("NAME")).append("\"")
			.append(", PROCESS_TYPE=\"").append(map.value("PROCESS_TYPE")).append("\"")
			.append(", SOURCEPATH=\"").append(map.value("SOURCEPATH")).append("\"")
			.append(", TARGETPATH=\"").append(map.value("TARGETPATH")).append("\"")
			.append(", TARGETPATH_TYPE=\"").append(map.value("TARGETPATH_TYPE")).append("\"")
			.append(", FILTER_TYPE=\"").append(map.value("FILTER_TYPE")).append("\"")
			.append(", FILTER_SIMPLE=\"").append(map.value("FILTER_SIMPLE")).append("\"")
			.append(", FILTER_REGEXP=\"").append(map.value("FILTER_REGEXP")).append("\"")
			.append(", SCHEDULE_TYPE=\"").append(map.value("SCHEDULE_TYPE")).append("\"")
			.append(", SCHEDULE_TIME=\"").append(map.value("SCHEDULE_TIME")).append("\"")
			.append(", COORD_UL=\"").append(map.value("COORD_UL")).append("\"")
			.append(", COORD_LR=\"").append(map.value("COORD_LR")).append("\"")
			.append(", REG_DATE=\"").append(map.value("REG_DATE")).append("\"")
			.append(", PIXEL_ROW=").append(map.value("PIXEL_ROW"))
			.append(", PIXEL_COL=").append(map.value("PIXEL_COL"))
			.append(", DATA_TYPE=\"").append(map.value("DATA_TYPE")).append("\"")
			.append(", DATA_FORMAT=\"").append(map.value("DATA_FORMAT")).append("\"")
			.append(", PROJECTION=\"").append(map.value("PROJECTION")).append("\"")
			.append(", QUICK_LOOK=\"").append(map.value("QUICK_LOOK")).append("\"")
			.append(", DATA_GBN=\"").append(map.value("DATA_GBN")).append("\"")
			.append(", DATA_AN_GBN=\"").append(map.value("DATA_AN_GBN")).append("\"")
			.append(", FILE_STATUS=\"").append(map.value("FILE_STATUS")).append("\"")
			.append(", MOUNT_POINT=\"").append(map.value("MOUNT_POINT")).append("\"")
			.append(", DATA_OPEN=\"").append(map.value("DATA_OPEN")).append("\"")
			.append(", SURVEY_DATE_START_INDEX=").append(map.value("SURVEY_DATE_START_INDEX"))
			.append(", SURVEY_DATE_END_INDEX=").append(map.value("SURVEY_DATE_END_INDEX"))
			.append(", SURVEY_TIME_START_INDEX=").append(map.value("SURVEY_TIME_START_INDEX"))
			.append(", SURVEY_TIME_END_INDEX=").append(map.value("SURVEY_TIME_END_INDEX"))	;	

		sb.append(" WHERE ID = ").append(map.value("ID")).append(";");
		return sb.toString();
	}
	
	private ProductInfo getRelayProductInfo(ResultSet rs, ProductInfoAppendix tbIdemtityColumns) throws SQLException {
		String productName = rs.getString("NAME");
		int productID = rs.getInt("ID");
		int satelliteID = rs.getInt("SATELLITE_ID");
		String processType = rs.getString("PROCESS_TYPE");
		String sourcePath = rs.getString("SOURCEPATH");
		String targetPath = rs.getString("TARGETPATH");
		String targetPathType = rs.getString("TARGETPATH_TYPE");
		String filterType = rs.getString("FILTER_TYPE");
		String filterSimple = rs.getString("FILTER_SIMPLE");
		String filterRegexp = rs.getString("FILTER_REGEXP");
		String scheduleType = rs.getString("SCHEDULE_TYPE");
		String scheduleTime = rs.getString("SCHEDULE_TIME");
		String satelliteName = rs.getString("SATE_NAME");
		
		ProductInfo info = new ProductInfo(productName, productID, satelliteID, satelliteName, getProcessType(processType),
				getScheduleType(scheduleType), filterType, filterSimple, filterRegexp, scheduleTime, sourcePath, targetPath, targetPathType,  tbIdemtityColumns);
		return info;
	}

	private PROCESS_TYPE getProcessType(String processType) {
		switch(processType.toUpperCase()) {
		default:
		case "DB_FTP":
			return ProductInfo.PROCESS_TYPE.DB_FTP;
		case "DB_ONLY":
			return ProductInfo.PROCESS_TYPE.DB_ONLY;
		case "FTP_ONLY":
			return ProductInfo.PROCESS_TYPE.FTP_ONLY;
		case "REMOVE_LOCAL_ONLY":
			return ProductInfo.PROCESS_TYPE.REMOVE_LOCAL_ONLY;
		}
	}
	private SCHEDULE_TYPE getScheduleType(String string) {
		switch(string.toUpperCase()) {
		default:
		case "EVERY_DAY":
			return ProductInfo.SCHEDULE_TYPE.EVERY_DAY;
		case "EVERY_HOUR":
			return ProductInfo.SCHEDULE_TYPE.EVERY_HOUR;
		case "EVERY_MIN":
			return ProductInfo.SCHEDULE_TYPE.EVERY_MIN;
		}
	}
	
	private ProductInfoAppendix getProductAppendixColumns(ResultSet rs) throws SQLException {
		ProductInfoAppendix tbColumns = new ProductInfoAppendix();
		tbColumns.IDENTIFIER = null;
		tbColumns.SURVEY_DATE = null;
		tbColumns.COORD_UL = rs.getString("COORD_UL");
		tbColumns.COORD_LR = rs.getString("COORD_LR");
		tbColumns.PIXEL_ROW = rs.getInt("PIXEL_ROW");
		tbColumns.PIXEL_COL = rs.getInt("PIXEL_COL");
		tbColumns.DATA_TYPE = rs.getString("DATA_TYPE");
		tbColumns.DATA_FORMAT = rs.getString("DATA_FORMAT");
		tbColumns.PROJECTION = rs.getString("PROJECTION");
		tbColumns.QUICK_LOOK = rs.getString("QUICK_LOOK");
		tbColumns.DATA_GBN = rs.getString("DATA_GBN");
		tbColumns.DATA_AN_GBN = rs.getString("DATA_AN_GBN");
		tbColumns.FILE_SIZE = null;
		tbColumns.FILE_STATUS = rs.getString("FILE_STATUS");
		tbColumns.FILE_PATH = null;
		tbColumns.REG_DATE = rs.getString("REG_DATE");
		tbColumns.MOUNT_POINT = rs.getString("MOUNT_POINT");
		tbColumns.DATA_OPEN = rs.getString("DATA_OPEN");
		tbColumns.SURVEY_DATE_START_INDEX = rs.getInt("SURVEY_DATE_START_INDEX");
		tbColumns.SURVEY_DATE_END_INDEX = rs.getInt("SURVEY_DATE_END_INDEX");
		tbColumns.SURVEY_TIME_START_INDEX = rs.getInt("SURVEY_TIME_START_INDEX");
		tbColumns.SURVEY_TIME_END_INDEX = rs.getInt("SURVEY_TIME_END_INDEX");
		
		
		tbColumns.SURVEY_TIME = null;
		tbColumns.SATELLITE = rs.getString("SATE_NAME");
		return tbColumns;
	}
	
	public ProductInfo getProduct(int productID) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String query = "SELECT A.*, B.NAME as SATE_NAME FROM TB_PRODUCT as A, TB_SATELLITE as B WHERE A.SATELLITE_ID == B.ID AND A.ID = "+productID+" ;";
			rs = stmt.executeQuery(query);
			logger.info("[get Product] " + query );

			while (rs.next()) {
				ProductInfoAppendix appendixColumns = getProductAppendixColumns(rs);
				ProductInfo info = getRelayProductInfo(rs, appendixColumns);
				return info;
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
		return null;
	}
}
