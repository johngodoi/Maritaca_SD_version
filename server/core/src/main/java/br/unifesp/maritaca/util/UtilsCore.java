package br.unifesp.maritaca.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.RandomStringUtils;

public class UtilsCore {
	private static final int STRING_LENGTH = 10;

	public static final String randomString() {
		return randomString(STRING_LENGTH);
	}

	public static final String randomString(int length) {
		if (length >= 0)
			return RandomStringUtils.randomAlphanumeric(length);
		else
			return RandomStringUtils.randomAlphanumeric(STRING_LENGTH);
	}

	/**
	 * Verify if object is not null and has a key
	 * 
	 * @param obj
	 *            : object Entity, must have a getKey method
	 * @throws IllegalArgumentException
	 *             is false
	 */
	@Deprecated
	public static <T> void verifyEntity(T obj) {
		boolean verify = false;
		if (obj != null) {
			try {
				Method method = obj.getClass().getDeclaredMethod("getKey");
				verify = method.invoke(obj) != null;
			} catch (SecurityException e) {
				// does not have the method
			} catch (NoSuchMethodException e) {
				// does not have the method
			} catch (IllegalArgumentException e) {
				// does not have the method
			} catch (IllegalAccessException e) {
				// does not have the method
			} catch (InvocationTargetException e) {
				// does not have the method
			}
		}
		if (!verify) {
			throw new IllegalArgumentException("Invalid entity" + obj);
		}
	}
	
	public static String toUpperFirst(String valor) {
		StringBuilder result = new StringBuilder(valor);
		result.setCharAt(0, new String(Character.toString(result.charAt(0)))
				.toUpperCase().charAt(0));

		return result.toString();
	}
}
