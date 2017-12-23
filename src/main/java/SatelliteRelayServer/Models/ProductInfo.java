package SatelliteRelayServer.Models;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import SatelliteRelayServer.DB.SatelliteDTO;


public class ProductInfo {
	
	static Logger logger = Logger.getLogger(ProductInfo.class);
	public enum SCHEDULE_TYPE {EVERY_DAY,EVERY_HOUR,EVERY_MIN};
	public enum TARGETPATH_TYPE {PATH_ONLY, YYYYMM, YYYY_MM, YYYY_MM_DD};
	public enum FILTER_TYPE {NONE, SIMPLE, REGEXP};
	public enum PROCESS_TYPE {DB_FTP, DB_ONLY, FTP_ONLY, REMOVE_LOCAL_ONLY};

	public String productName;
	public int productID;
	public int satelliteID;
	public String satelliteName;
	public PROCESS_TYPE processTYPE;
	public String sourcePath;
	public String oriTargetPath;
	public TARGETPATH_TYPE targetPath_type;
	public FILTER_TYPE filterType;//1:Simple FIlter, 2:Regexp
	public String filterSimple;
	public String filterRegexp;
	public SCHEDULE_TYPE scheduleTYPE = SCHEDULE_TYPE.EVERY_DAY;
	public String scheduleTime = null;
	public SatelliteDTO satelliteInfo = null;
	public ProductInfoAppendix appendixColumns = null;
	
	public SatelliteDTO getSatelliteInfo() {
		return satelliteInfo;
	}

	public void setSatelliteInfo(SatelliteDTO satelliteInfo) {
		this.satelliteInfo = satelliteInfo;
	}

	public ProductInfo(String productName, int productID, int satelliteID, String satelliteName, PROCESS_TYPE processType, SCHEDULE_TYPE scheduleTYPE, String filterTypeStr, String filterSimple, String filterRegexp, String scheduleTimeString, String sourcePath, String targetPath, String targetPathTypeStr, ProductInfoAppendix tbColumns) {
		this.productName = productName;
		this.productID = productID;
		this.satelliteID = satelliteID;
		this.satelliteName = satelliteName;
		this.processTYPE = processType;
		this.scheduleTYPE = scheduleTYPE;
		this.sourcePath = sourcePath;
		this.oriTargetPath = targetPath;
		this.targetPath_type = getTargetPathType(targetPathTypeStr);
		this.filterType = getFilterType(filterTypeStr);
		this.filterSimple = filterSimple;
		this.filterRegexp = filterRegexp;
		this.appendixColumns = tbColumns;
		this.scheduleTime = scheduleTimeString;
	}

	private TARGETPATH_TYPE getTargetPathType(String targetPathTypeStr) {
		switch(targetPathTypeStr.toUpperCase()) {
		case "1":
		case "PATH_ONLY":
			return TARGETPATH_TYPE.PATH_ONLY;
		case "2":
		case "YYYYMM":
			return TARGETPATH_TYPE.YYYYMM;
		case "3":
		case "YYYY/MM":
			return TARGETPATH_TYPE.YYYY_MM;
		case "4":
		case "YYYY/MM/DD":
			return TARGETPATH_TYPE.YYYY_MM_DD;
		default:
			return TARGETPATH_TYPE.PATH_ONLY;
		}
	}

	private FILTER_TYPE getFilterType(String filterTypeStr) {
		switch (filterTypeStr.toUpperCase()) {
		case "SIMPLE":
		case "1":
			return FILTER_TYPE.SIMPLE;
		case "REGEXP":
		case "2":
			return FILTER_TYPE.REGEXP;
		default:
			logger.warn("RelayProductInfo : filterType is unknown... set default value -SIMPLE");
			return FILTER_TYPE.SIMPLE;
		}
	}

	public Date getScheduleStartDate() {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		int hh = now.get(Calendar.HOUR_OF_DAY);
		int mm = now.get(Calendar.MINUTE);
		int ss = now.get(Calendar.SECOND);
		String timeTokens[] = scheduleTime.split(":");
		if (timeTokens.length < 3) {
			logger.warn(scheduleTime + " string.Split(:).length < 3 ");
			logger.warn("set Now Time");
		} else {
			hh = Integer.parseInt(timeTokens[0]);
			mm = Integer.parseInt(timeTokens[1]);
			ss = Integer.parseInt(timeTokens[2]);
		}
		Calendar cal = Calendar.getInstance();
		switch (scheduleTYPE) {
		default:
		case EVERY_DAY:
			cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), hh, mm, ss);
			cal.set(Calendar.MILLISECOND, 0);
			if (cal.getTimeInMillis() - now.getTimeInMillis() < 0) {
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			return cal.getTime();
		case EVERY_HOUR:
			cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.HOUR_OF_DAY), mm, ss);
			cal.set(Calendar.MILLISECOND, 0);
			if (cal.getTimeInMillis() - now.getTimeInMillis() < 0) {
				cal.add(Calendar.HOUR_OF_DAY, 1);
			}
			return cal.getTime();
		case EVERY_MIN:
			cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), ss);
			cal.set(Calendar.MILLISECOND, 0);
			if (cal.getTimeInMillis() - now.getTimeInMillis() < 0) {
				cal.add(Calendar.MINUTE, 1);
			}
			return cal.getTime();
		}
	}

	public long getSchduleInterval() {
		switch (scheduleTYPE) {
		default:
		case EVERY_DAY:
			return 24*60*60*1000;
		case EVERY_HOUR:
			return 60*60*1000;
		case EVERY_MIN:
			return 60*1000;
		}
	}

	public String getFilterRegularExpression() {
		return filterRegexp;
	}

	public File getSourcePath() {
		return new File(sourcePath);
	}

	public String getFTPTargetPath() {
		if (appendixColumns.FILE_PATH == null || appendixColumns.FILE_PATH == "") return this.oriTargetPath;
		return appendixColumns.FILE_PATH;
	}
	public void setAppendixColumns(File afile) {
		appendixColumns.setFromFile(afile, targetPath_type, this.oriTargetPath);
	}

	public String getDBInsertQuery(File afile) {
		setAppendixColumns(afile);
	
		String query = "INSERT INTO TB_IDENTITY_LIST "
				+ "(SEQ,IDENTIFIER,SURVEY_DATE,COORD_UL,COORD_LR,PIXEL_ROW,PIXEL_COL,DATA_TYPE, "
				+ "DATA_FORMAT, PROJECTION,QUICK_LOOK,DATA_GBN,DATA_AN_GBN,SATELLITE,RESOLUTION,"
				+ "FILE_SIZE,FILE_STATUS,FILE_PATH,REG_DATE,MOUNT_POINT,DATA_OPEN,SURVEY_TIME) "
				+ "VALUES "
				+ "('"+appendixColumns.SEQ+"','"+appendixColumns.IDENTIFIER+"','"+appendixColumns.SURVEY_DATE+"','"+appendixColumns.COORD_UL+"','"+appendixColumns.COORD_LR+"','"+
				appendixColumns.PIXEL_ROW+"','"+appendixColumns.PIXEL_COL+"','"+appendixColumns.DATA_TYPE+"','"+appendixColumns.DATA_FORMAT+"','"+appendixColumns.PROJECTION+"','"+
				appendixColumns.QUICK_LOOK+"','"+appendixColumns.DATA_GBN+"','"+appendixColumns.DATA_AN_GBN+"','"+appendixColumns.SATELLITE+"','"+appendixColumns.RESOLUTION+"',"+
				appendixColumns.FILE_SIZE+",'"+appendixColumns.FILE_STATUS+"','"+appendixColumns.FILE_PATH+"','"+appendixColumns.REG_DATE+"','"+appendixColumns.MOUNT_POINT+"','"+
				appendixColumns.DATA_OPEN+"','"+appendixColumns.SURVEY_TIME+"')";
		return query;
	}

	public FILTER_TYPE getFilterType() {
		return filterType;
	}

	public String getFilterSimple() {
		return filterSimple;
	}

	public String getDBUpdateQuery(File afile) {
		setAppendixColumns(afile);
		
		String query = "UPDATE TB_IDENTITY_LIST "
				+ " SET SURVEY_DATE='"+appendixColumns.SURVEY_DATE+"'"
				+ ",COORD_UL='" + appendixColumns.COORD_UL + "'"
				+ ",COORD_LR='" + appendixColumns.COORD_LR + "'"
				+ ",PIXEL_ROW='" + appendixColumns.PIXEL_ROW + "'"
				+ ",PIXEL_COL='" + appendixColumns.PIXEL_COL + "'"
				+ ",DATA_TYPE='" + appendixColumns.DATA_TYPE + "'"
				+ ",DATA_FORMAT='" + appendixColumns.DATA_FORMAT + "'"
				+ ",PROJECTION='" + appendixColumns.PROJECTION + "'"
				+ ",QUICK_LOOK='" + appendixColumns.QUICK_LOOK + "'"
				+ ",DATA_GBN='" + appendixColumns.DATA_GBN + "'"
				+ ",DATA_AN_GBN='" + appendixColumns.DATA_AN_GBN + "'"
				+ ",SATELLITE='" + appendixColumns.SATELLITE + "'"
				+ ",RESOLUTION='" + appendixColumns.RESOLUTION + "'"
				+ ",FILE_SIZE='" + appendixColumns.FILE_SIZE + "'"
				+ ",FILE_STATUS='" + appendixColumns.FILE_STATUS + "'"
				+ ",FILE_PATH='" + appendixColumns.FILE_PATH + "'"
				+ ",REG_DATE='" + appendixColumns.REG_DATE + "'"
//				+ ",REG_DATE=TO_CHAR(SYSDATE, 'MM-DD-YYYY HH24:MI:SS')"
				+ ",MOUNT_POINT='" + appendixColumns.MOUNT_POINT + "'"
				+ ",DATA_OPEN='" + appendixColumns.DATA_OPEN + "'"
				+ ",SURVEY_TIME='" + appendixColumns.SURVEY_TIME + "'"
				+ " WHERE IDENTIFIER='" + appendixColumns.IDENTIFIER + "'";
		return query;
	}
}
