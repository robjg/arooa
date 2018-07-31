package org.oddjob.arooa.utils;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationFormatter {

	private static Pattern COLON_PATTERN = Pattern.compile(
			"\\s*((\\d+)d)?\\s*((\\d+)h)?\\s*((\\d+)m)?\\s*((\\d+)s)?\\s*((\\d+)(ms)?)?\\s*");
	
	public static final long ONE_SECOND = 1000L;
	
	public static final long ONE_MINUTE = ONE_SECOND * 60;
	
	public static final long ONE_HOUR = ONE_MINUTE * 60;
	
	public static final long ONE_DAY = ONE_HOUR * 24;
	
	private static final int[] unitGroupNums = { 2, 4, 6, 8, 10 };
	
	private static final long[] msPerUnit = 
		{ ONE_DAY, ONE_HOUR, ONE_MINUTE, ONE_SECOND, 1L};
	
	public long parseTime(String time) throws ParseException {
		
		Matcher matcher = COLON_PATTERN.matcher(time);
		
		if (!matcher.matches()) {
			throw new ParseException(time, 0);
		}
		
		long duration = 0;
		
		System.out.println(matcher.groupCount() + " groups.");
		for (int i = 0; i <= matcher.groupCount(); ++i) {
			System.out.println(i + ") " + matcher.group(i));
		}
		
		for (int i = 0; i < unitGroupNums.length; ++i) {
			
			String unitString = matcher.group(unitGroupNums[i]);
			
			if (unitString == null) {
				continue;
			}
			
			long unit = Integer.parseInt(unitString);
			
			duration += unit * msPerUnit[i];
		}
		
		return  duration;
	}
	
}
