package SatelliteRelayServer.Models;

import java.io.File;

import SatelliteRelayServer.Models.ProductInfo.TARGETPATH_TYPE;

public class ProductInfoAppendix {
	public int		SEQ	= -1;			// ex) 10142335
	public String	IDENTIFIER	= "";	// From File ex) 2014.0127.0331.aqua-1.modis_Rrs_551.KOR.tif
	public String	SURVEY_DATE	= ""; // From File
	public String	COORD_UL		= ""; // PRODUCTINFO
	public String	COORD_LR		= ""; // PRODUCTINFO
	public int		PIXEL_ROW	= -1;   // PRODUCTINFO
	public int		PIXEL_COL	= -1;
	public String	DATA_TYPE	= "";
	public String	DATA_FORMAT	= "";
	public String	PROJECTION	= "";
	public String	QUICK_LOOK	= "";
	public String	DATA_GBN		= "";
	public String	DATA_AN_GBN	= "";
	public String	SATELLITE	= "";
	public String	RESOLUTION	= "";
	public String	FILE_SIZE	= "";
	public String	FILE_STATUS	= "";
	public String	FILE_PATH	= "";
	public String	REG_DATE		= "";
	public String	MOUNT_POINT	= "";
	public String	DATA_OPEN	= "";
	public String	SURVEY_TIME	= "";
	
	public boolean  setFromFile(File afile, TARGETPATH_TYPE targetPath_type, String oriTargetPath) {
		if (afile == null) {
			return false;
		}
		String fileName = afile.getName();
		String ext = "";
		int index = fileName.lastIndexOf(".");
		if (index != -1) {
			ext  = fileName.substring(index + 1);
        }

		IDENTIFIER = fileName;	// 2014.0127.0331.aqua-1.modis_Rrs_551.KOR.tif	
		SURVEY_DATE = getSurveryDate(afile);	// From File. ex) 20140127
		DATA_FORMAT = ext;		// From File. ex) tif
		FILE_SIZE = Double.toString(afile.length());		// 9002213	
		FILE_PATH = getFilePath(afile, targetPath_type, oriTargetPath);		// /MODIS/2014/01/	
		SURVEY_TIME = getSurveyTime(afile);	// 0331
		return true;
	}
	
	private String getSurveyTime(File afile) {
		String[] tokens = afile.getName().split(".");
		if (tokens.length > 1) {
			return tokens[1];
		}
		return "";
	}

	private String getFilePath(File afile, TARGETPATH_TYPE targetPath_type, String oriTargetPath) {
		if (oriTargetPath.substring(oriTargetPath.length() - 1).compareTo("/") != 0) {
			oriTargetPath += "/";
		}
		String fileName = afile.getName();
		String YYYY = fileName.substring(0,4).trim();
		String MM = fileName.substring(4,6).trim();
		String DD = fileName.substring(6,8).trim();
		switch (targetPath_type) {
		default:
		case PATH_ONLY:
			return oriTargetPath;
		case YYYY_MM:
			return oriTargetPath + YYYY + "/" + MM + "/";
		case YYYY_MM_DD:
			return oriTargetPath + YYYY + "/" + MM + "/" + DD + "/";
		case YYYYMM:
			return oriTargetPath + YYYY + MM + "/";
		}
	}

	private String getSurveryDate(File afile) {
		return afile.getName().substring(0, 8);
	}
};