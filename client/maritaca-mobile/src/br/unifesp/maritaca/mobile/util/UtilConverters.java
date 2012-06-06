package br.unifesp.maritaca.mobile.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilConverters {
	
	/**
	 * This method is to convert String to Integer.
	 * @param string that will be converted
	 * @return Integer conversion
	 */
	public static Integer convertStringToInteger(String string) {
		try {
			if("".equals(string)) {
				return null;
			}
			Integer value = Integer.parseInt(string);
			return value;
		} catch (NumberFormatException e) {
			// TODO Exception
			return null;
		}
	}

	public static Date convertStringToDate(String val, String format) {		
		try {
			DateFormat formatter = new SimpleDateFormat(format);
			Date date = (Date)formatter.parse(val);
			return date;
		} catch (ParseException e) {
			return new Date();//null
		}
	}
}