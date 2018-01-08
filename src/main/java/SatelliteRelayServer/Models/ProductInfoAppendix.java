package SatelliteRelayServer.Models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public int		SURVEY_DATE_START_INDEX = -1;
	public int		SURVEY_DATE_END_INDEX = -1;
	public int		SURVEY_TIME_START_INDEX = -1;
	public int		SURVEY_TIME_END_INDEX = -1;
	
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
		SURVEY_DATE = getSurveyDate(afile);	// From File. ex) 20140127
		DATA_FORMAT = ext;		// From File. ex) tif
		FILE_SIZE = Double.toString(afile.length());		// 9002213	
		FILE_PATH = getFilePath(afile, targetPath_type, oriTargetPath);		// /MODIS/2014/01/
		
		if (DATA_TYPE.compareTo("S") == 0 || DATA_TYPE.compareTo("SG") == 1) { // 수신영상일 경우에만 시간을 넣는다. S:수신영상(기하보정X) SG:수신영상
			SURVEY_TIME = getSurveyTime(afile);	// 0331
		} else {
			SURVEY_TIME = "";
		}

		return true;
	}
	
	private String getSurveyTime(File afile) {
		String Time = "";
		// SURVEY_TIME_START_INDEX, SURVEY_TIME_END_INDEX : UI Input값으로 1Base로 시작함.
		if (SURVEY_TIME_START_INDEX > 0 && SURVEY_TIME_END_INDEX > 0 ) {
			Time += " " + afile.getName().substring(SURVEY_TIME_START_INDEX - 1 , SURVEY_TIME_END_INDEX);
		}
		
		return Time;
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

	private String getSurveyDate(File afile) {
		String Date = "";
		// SURVEY_DATE_START_INDEX, SURVEY_DATE_END_INDEX : UI Input값으로 1Base로 시작함.
		if (SURVEY_DATE_START_INDEX > 0 && SURVEY_DATE_END_INDEX > 0 ) {
			Date = afile.getName().substring(SURVEY_DATE_START_INDEX - 1 , SURVEY_DATE_END_INDEX);
		}
		
//		if (Date.length() == 0 ) { // 지정하지 않았을 경우 자동으로 차아다.
//			List<String> dateList = extractDate(afile.getName());
//			if (dateList.size() > 0) Date = dateList.get(0);
//		}
		
		return Date;
	}
	
    public List<String> extractDate(String str) {
        List<String> list = new ArrayList<String>();
        Matcher matcher ;
        
        if (str.isEmpty()) {
            matcher = null;
        } else {
            String patternStr = "(19|20)\\d{2}[- /.]*(0[1-9]|1[012])[- /.]*(0[1-9]|[12][0-9]|3[01])"; // 날자를 패턴으로 지정
            
            int flags = Pattern.MULTILINE | Pattern.CASE_INSENSITIVE;
            Pattern pattern = Pattern.compile(patternStr, flags);
            matcher = pattern.matcher(str);

            while (matcher.find()) {
                list.add(matcher.group());
            }
        }

        return list;
    }
};