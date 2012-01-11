package br.unifesp.maritaca.util;

import org.apache.commons.lang.RandomStringUtils;

public class Utils {
	private static final int STRING_LENGTH = 10;

	public static final String randomString(){
		return RandomStringUtils.randomAlphanumeric(STRING_LENGTH);
	}
}
