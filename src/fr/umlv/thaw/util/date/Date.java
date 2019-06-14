package fr.umlv.thaw.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Objects;

public class Date {

	private final String day;
	private final String month;
	private final String year;
	private final String hour;
	private final String minute;
	private final String second;
	
	private Date(java.util.Date date) {
		Objects.requireNonNull(date);
		this.day = new SimpleDateFormat("dd").format(date);
		this.month = new SimpleDateFormat("MM").format(date);
		this.year = new SimpleDateFormat("yyyy").format(date);
		this.hour = new SimpleDateFormat("HH").format(date);
		this.minute = new SimpleDateFormat("mm").format(date);
		this.second = new SimpleDateFormat("ss").format(date);
	}
	
	/**
	 * Get the date of now.
	 * 
	 * @return	The date of now.
	 */
	public static Date now() {
		return new Date(java.util.Date.from(Instant.now()));
	}
	
	/**
	 * Parse the specified string of date.
	 * The date must have the format : 'year/month/day hour:minut:second',
	 * with year on 4 digits and others each variables on 2 digits.
	 * 
	 * @param 	stringOfDate the specified string
	 * @return	The date of the string.
	 * @throws 	NullPointerException if stringOfDate is null
	 * @throws 	ParseException if the string is not in the required date format
	 */
	public static Date parse(String stringOfDate) throws ParseException {
		Objects.requireNonNull(stringOfDate);
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return new Date(parser.parse(stringOfDate));
	}
	
	@Override
	public String toString() {
		return day + "/" + month + "/" + year + "-" + hour +  ":" + minute;
	}
	
	/**
	 * Get the day of the date.
	 * 
	 * @return	The day of the date.
	 */
	public String getDay() {
		return day;
	}
	
	/**
	 * Get the month of the date.
	 * 
	 * @return	The month of the date.
	 */
	public String getMonth() {
		return month;
	}
	
	/**
	 * Get the year of the date.
	 * 
	 * @return	The year of the date.
	 */
	public String getYear() {
		return year;
	}
	
	/**
	 * Get the hour of the date.
	 * 
	 * @return	The hour of the date.
	 */
	public String getHour() {
		return hour;
	}
	
	/**
	 * Get the minute of the date.
	 * 
	 * @return	The minute of the date.
	 */
	public String getMinute() {
		return minute;
	}
	
	/**
	 * Get the second of the date.
	 * 
	 * @return	The second of the date.
	 */
	public String getSecond() {
		return second;
	}
	
}
