package SatelliteRelayServer.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import SatelliteRelayServer.Models.ProductInfoAppendix;
import SatelliteRelayServer.Models.ProductInfo;
import SatelliteRelayServer.Models.ProductInfo.ScheduleTYPE;

public class ProductDAO extends BaseDAO {
	
	public ProductDAO(Connection conn) {
		super(conn);
		TABLE_NAME = "TB_PRODUCT";
		DDL = "CREATE TABLE `TB_PRODUCT` ( "
				+ "    `ID`	INT NOT NULL, "
				+ "    `SATELLITE_ID`	INT NOT NULL, "
				+ "    `NAME`	TEXT NOT NULL, "
				+ "    `SOURCEPATH`	TEXT, "
				+ "    `TARGETPATH`	TEXT, "
				+ "    `TARGETPATH_TYPE`	TEXT, " //PATH_ONLY, YYYYMM, YYYY/MM, YYYY/MM/DD
				+ "    `FILTER_TYPE`	TEXT, " //SIMPLE, REGEXP
				+ "    `FILTER_SIMPLE`	TEXT, "
				+ "    `FILTER_REGEXP`	TEXT NOT NULL, "
				+ "    `SCHEDULE_TIME`	TEXT NOT NULL, " //EVERY_DAY,11:00:00
				+ "    `COORD_UL`	TEXT, " //여기서부터 아래는 단순 입력용
				+ "    `COORD_LR`	TEXT, "
				+ "    `REG_DATE`	TEXT, "
				+ "    `PIXEL_ROW`	INTEGER, "
				+ "    `PIXEL_COL`	INTEGER, "
				+ "    `DATA_TYPE`	TEXT, "
				+ "    `PROJECTION`	TEXT, "
				+ "    `QUICK_LOOK`	TEXT, "
				+ "    `DATA_GBN`	TEXT, "
				+ "    `DATA_AN_GBN`	TEXT, "
				+ "    `RESOLUTION`	TEXT, "
				+ "    `FILE_STATUS`	TEXT, "
				+ "    `MOUNT_POINT`	TEXT, "
				+ "    `DATA_OPEN`	TEXT, "
				+ "    PRIMARY KEY(`ID`) "
				+ "  );";
	}
	
	public List<ProductInfo> getProducts() {
		List<ProductInfo> list = new ArrayList<ProductInfo>();

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT A.*, B.NAME as SATE_NAME FROM TB_PRODUCT as A, TB_SATELLITE as B WHERE A.SATELLITE_ID == B.ID;");
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
	
	private ProductInfo getRelayProductInfo(ResultSet rs, ProductInfoAppendix tbIdemtityColumns) throws SQLException {
		String productName = rs.getString("NAME");
		int productID = rs.getInt("ID");
		int satelliteID = rs.getInt("SATELLITE_ID");
		String sourcePath = rs.getString("SOURCEPATH");
		String targetPath = rs.getString("TARGETPATH");
		String targetPathType = rs.getString("TARGETPATH_TYPE");
		String filterType = rs.getString("FILTER_TYPE");
		String filterSimple = rs.getString("FILTER_SIMPLE");
		String filterRegexp = rs.getString("FILTER_REGEXP");
		String schedule = rs.getString("SCHEDULE_TIME");
		String token[] = schedule.split(",");
		
		ProductInfo info = new ProductInfo(productName, productID, satelliteID,
				getScheduleType(token[0]), filterType, filterSimple, filterRegexp, token[1], sourcePath, targetPath, targetPathType,  tbIdemtityColumns);
		return info;
	}

	private ScheduleTYPE getScheduleType(String string) {
		switch(string.toUpperCase()) {
		default:
		case "EVERY_DAY":
			return ProductInfo.ScheduleTYPE.EVERY_DAY;
		case "EVERY_HOUR":
			return ProductInfo.ScheduleTYPE.EVERY_HOUR;
		case "EVERY_MIN":
			return ProductInfo.ScheduleTYPE.EVERY_MIN;
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
		tbColumns.DATA_FORMAT = null;
		tbColumns.PROJECTION = rs.getString("PROJECTION");
		tbColumns.QUICK_LOOK = rs.getString("QUICK_LOOK");
		tbColumns.DATA_GBN = rs.getString("DATA_GBN");
		tbColumns.DATA_AN_GBN = rs.getString("DATA_AN_GBN");
		tbColumns.RESOLUTION = rs.getString("RESOLUTION");
		tbColumns.FILE_SIZE = null;
		tbColumns.FILE_STATUS = rs.getString("FILE_STATUS");
		tbColumns.FILE_PATH = null;
		tbColumns.REG_DATE = rs.getString("REG_DATE");
		tbColumns.MOUNT_POINT = rs.getString("MOUNT_POINT");
		tbColumns.DATA_OPEN = rs.getString("DATA_OPEN");
		tbColumns.SURVEY_TIME = null;
		tbColumns.SATELLITE = rs.getString("SATE_NAME");
		return tbColumns;
	}
}
