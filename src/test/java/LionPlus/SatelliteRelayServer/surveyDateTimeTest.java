package LionPlus.SatelliteRelayServer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class surveyDateTimeTest {
	public static void main(String[] args) {
//		int SURVEY_DATE_START_INDEX = 1;
//		int SURVEY_DATE_END_INDEX = 8;
//		int SURVEY_TIME_START_INDEX = 10;
//		int SURVEY_TIME_END_INDEX = 17;
		
		int SURVEY_DATE_START_INDEX = 0;
		int SURVEY_DATE_END_INDEX = 0;
		int SURVEY_TIME_START_INDEX = 0;
		int SURVEY_TIME_END_INDEX = 0;
		
		String afile = "2017.12.28 11:11:33";
		String surveyDateTime = "unknown";
		
		String Date = "";
		String Time = "";
		
		if (SURVEY_DATE_START_INDEX > 0  && SURVEY_DATE_END_INDEX > 0  ) {
			Date = afile.substring(SURVEY_DATE_START_INDEX - 1 , SURVEY_DATE_END_INDEX );
		}
		if (SURVEY_TIME_START_INDEX > 0  && SURVEY_TIME_END_INDEX > 0  ) {
			Time += " " + afile.substring(SURVEY_TIME_START_INDEX - 1 , SURVEY_TIME_END_INDEX);
		}
		if (Date.length() > 0 ) {
			surveyDateTime = Date;
			if (Time.length() > 0 ) {
				surveyDateTime += " " + Time;
			}
		} else { // 지정하지 않았을 경우 자동으로 찾아본.
			List<String> dateList = extractDate(afile);
			if (dateList.size() > 0) surveyDateTime = dateList.get(0);
		}
		
		
		System.out.println(surveyDateTime);
	}
	
	/* 날짜 형식을 추출하는 함수. 아래 형태의 숫자를 추출
     * 0000-00-00
     * 0000.00.00
     * 0000/00/00
     */
	
	public static List<String> extractDate(String str) {
        List<String> list = new ArrayList<String>();
        Matcher matcher ;
        
        if (str.isEmpty()) {
            matcher = null;
        } else {
            String patternStr = "(19|20)\\d{2}[-/\\.]*(0[1-9]|1[012])[-/\\.]*(0[1-9]|[12][0-9]|3[01])"; // 날자를 패턴으로 지정
            
            int flags = Pattern.MULTILINE | Pattern.CASE_INSENSITIVE;
            Pattern pattern = Pattern.compile(patternStr, flags);
            matcher = pattern.matcher(str);

            while (matcher.find()) {
                list.add(matcher.group());
            }
        }

        return list;
    }
}
