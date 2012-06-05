package br.unifesp.maritaca.persistence.util;

import org.apache.commons.lang.RandomStringUtils;

/**
 * This class has generic methods that are use in the persistence 
 * layer
 * 
 * @author Maritaca team
 */
public class UtilsPersistence {
	
	private static final int STRING_LENGTH = 10;

	/**
	 * This method generates a random string, default size is 10
	 * @return a random String
	 */
	public static final String randomString() {
		return randomString(STRING_LENGTH);
	}

	/**
	 * This method generates a random string
	 * 
	 * @param length size of the random string
	 * @return the random string
	 */
	public static final String randomString(int length) {
		if (length >= 0)
			return RandomStringUtils.randomAlphanumeric(length);
		else
			return RandomStringUtils.randomAlphanumeric(STRING_LENGTH);
	}

	/**
	 * This method sets all the string's characters in uppercase. 
	 * 
	 * @param value is the string to be set
	 * @return a string with all the elements in uppercase.
	 */
	public static String toUpperFirst(String value) {
		StringBuilder result = new StringBuilder(value);
		result.setCharAt(0, new String(Character.toString(result.charAt(0)))
				.toUpperCase().charAt(0));

		return result.toString();
	}
}
