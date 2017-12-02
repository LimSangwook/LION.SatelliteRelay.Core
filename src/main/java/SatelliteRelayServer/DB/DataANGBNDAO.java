package SatelliteRelayServer.DB;

import java.sql.Connection;

public class DataANGBNDAO extends BaseDAO {

	public DataANGBNDAO(Connection conn) {
		super(conn);
		TABLE_NAME = "TB_DATA_AN_GBN";
		DDL = "CREATE TABLE TB_DATA_AN_GBN ("
				+ " ID 		INT PRIMARY KEY     NOT NULL,"
				+ " DATA_AN_GBN    TEXT    NOT NULL,"
				+ " DESCRIPTION TEXT)";
		columnName = new String[] {"ID", "DATA_AN_GBN", "DESCRIPTION"};;
		initData = new String[][] {	{"1","ANGSTROM_531","ANGSTROM_531"},
									{"2","CH32","CH32"},
									{"3","CHLOR_A","CHLOR_A"},
									{"4","DAAC_SST","DAAC_SST"},
									{"5","DAAC_SST4","DAAC_SST4"},
									{"6","EPS_78","EPS_78"},
									{"7","EVI","EVI"},
									{"8","K_490","K_490"},
									{"9","NDVI","NDVI"},
									{"10","NOAA-SST","NOAA-SST"},
									{"11","NOAA_CF_SST","NOAA_CF_SST"},
									{"12","PAR","PAR"},
									{"13","RGB","RGB"},
									{"14","RRS_412","RRS_412"},
									{"15","RRS_443","RRS_443"},
									{"16","RRS_488","RRS_488"},
									{"17","RRS_531","RRS_531"},
									{"18","RRS_551","RRS_551"},
									{"19","RRS_667","RRS_667"},
									{"20","RRS_678","RRS_678"},
									{"21","RRS_748","RRS_748"},
									{"22","RRS_869","RRS_869"},
									{"23","SEADAS_SST","SEADAS_SST"},
									{"24","SEADAS_SST4","SEADAS_SST4"},
									{"25","TAU_869","TAU_869"},
									{"26","nLw_412","nLw_412"},
									{"27","nLw_443","nLw_443"},
									{"28","nLw_488","nLw_488"},
									{"29","nLw_531","nLw_531"},
									{"30","nLw_551","nLw_551"},
									{"31","nLw_667","nLw_667"},
									{"32","nLw_678","nLw_551"},
									{"33","nLw_748","nLw_667"},
									{"34","nLw_869","nLw_869"},
									{"35","LST","LST"},
									{"36","SEADAS_CHL","SEADAS_CHL"}};
	}

}
