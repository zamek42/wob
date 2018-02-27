package com.zamek.wob.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Date utils to convert between LocalDate and Date
 * 
 * @author zamek
 */
public class DateUtils {

	/**
	 * Converts LocalDate to Date
	 * 
	 * @param localDate input LocalDate
	 * @return Date representation of input LocalDate
	 */
	public static Date asDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * Converts LocalDateTime to Date
	 * 
	 * @param localDateTime input LocalDateTime
	 * @return Date representation of input LocalDateTime
	 */
	public static Date asDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * Converts Date to LocalDate
	 * 
	 * @param date input Date
	 * @return LocalDate  representation of input Date
	 */
	public static LocalDate asLocalDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * Converts Date to LocalDateTime
	 * 
	 * @param date input Date
	 * @return LocalDateTime  representation of input Date
	 */
	public static LocalDateTime asLocalDateTime(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
}
