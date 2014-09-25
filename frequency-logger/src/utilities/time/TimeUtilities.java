package utilities.time;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtilities
{
	// Number of seconds between UNIX and GPS epochs (01/01/1970 and 01/06/1980 respectively)
	public static final long GPS_UNIX_EPOCH_OFFSET = 328665600;
	
	/**
	 * Takes a date string of the form dd-mm-yyyy hh:mm:ss and converts it into a GPS time
	 * representing the number of seconds since 01-06-1980 00:00:00.
	 * 
	 * @param date
	 * @return
	 */
	public static long calendarToGPSTime(Calendar calendar)
	{
		// Return the time since 01/06/1980 00:00:00
		return calendar.getTimeInMillis() / 1000L - GPS_UNIX_EPOCH_OFFSET;
	}
	
	/**
	 * Takes a time (of the form hh:mm:ss) and a reference date (of the form
	 * dd-mm-yyyy hh:mm:ss), and uses the reference date to work out the full date for
	 * the specified time, with the following assumptions:
	 * 
	 * The reference date is more up to date than the specified time;
	 * The specified time and reference date are within 1 day of each other.
	 * 
	 * @param time
	 * @param referenceDate
	 * @return
	 */
	public static Calendar parseTimeUsingAccurateDate(String time, String accurateDate)
	{
		// Set up the accurate calendar
		Calendar calendar = stringToCalendar(accurateDate);
		
		// Make a copy
		Calendar timeToParse = (Calendar) calendar.clone();
		
		// Get the hour, minute and second the demand was updated
		int timeToParseHour = Integer.parseInt(time.substring(0, 2));
		int timeToParseMinute = Integer.parseInt(time.substring(3, 5));
		int timeToParseSecond = Integer.parseInt(time.substring(6, 8));
		
		// Check if midnight has passed for the accurate date but not the time to parse
		if(timeToParseHour > calendar.get(Calendar.HOUR_OF_DAY))
		{
			// Make calendar lenient
			calendar.setLenient(true);
			
			// Midnight has passed but the demand time still refers to yesterday
			timeToParse.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		// Finally reset the time, whilst leaving the date alone
		timeToParse.set(Calendar.HOUR_OF_DAY, timeToParseHour);
		timeToParse.set(Calendar.MINUTE, timeToParseMinute);
		timeToParse.set(Calendar.SECOND, timeToParseSecond);
		
		return timeToParse;
	}
	
	/**
	 * Converts a date of the form dd-mm-yyyy hh:mm:ss to a Calendar object.
	 * @param date
	 * @return
	 */
	public static Calendar stringToCalendar(String date)
	{
		// Set up a new calendar
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-0"), Locale.UK);
		
		// Set the time
		calendar.set(
				Integer.parseInt(date.substring(6, 10)),
				Integer.parseInt(date.substring(3, 5)) - 1,
				Integer.parseInt(date.substring(0, 2)),
				Integer.parseInt(date.substring(11, 13)),
				Integer.parseInt(date.substring(14, 16)),
				Integer.parseInt(date.substring(17, 19))
		);
		
		return calendar;
	}
	
	public static long getGPSTime()
	{		
		return System.currentTimeMillis() / 1000L - GPS_UNIX_EPOCH_OFFSET;
	}
}
