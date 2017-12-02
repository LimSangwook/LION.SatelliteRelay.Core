package SatelliteRelayServer.DB;

import java.sql.Connection;

public class DataGBNDAO extends BaseDAO {

	public DataGBNDAO(Connection conn) {
		super(conn);
		TABLE_NAME = "TB_DATA_GBN";
		DDL = "CREATE TABLE TB_DATA_GBN ("
				+ " ID 		INT PRIMARY KEY     NOT NULL,"
				+ " DATA_GBN    TEXT    NOT NULL,"
				+ " DESCRIPTION TEXT)";
		columnName = new String[] {"ID", "DATA_GBN", "DESCRIPTION"};;
		initData = new String[][] {	{"1", "NO-L32", "NOAA CF SST 산출물"},
									{"2", "NO-L31", "NOAA SST 산출물"},
									{"3", "NO-L00", "NOAA PASS"},
									{"4", "MO-L31", "MODIS 산출물"},
									{"5", "MP-L00", "MODIS PASS"},
									{"6", "NP-L31", "NPP L2 산출물"},
									{"7", "NP-L32", "NPP L3 산출물"},
									{"8", "NP-L00", "NPP PASS"},
									{"9", "GO-L10", "GOCI L1 산출물(he5)"},
									{"10", "GO-L20", "GOCI L2 산출물(he5)"},
									{"11", "GO-L30", "GOCI L3 산출물(he5)"},
									{"12", "SW-L00", "SEAWIFS PASS"},
									{"13", "SW-L10", "SEAWIFS L1 산출물"},
									{"14", "SW-L20", "SEAWIFS L2 산출물"},
									{"15", "SW-L20", "SEAWIFS L3 산출물"}};
	}
}
