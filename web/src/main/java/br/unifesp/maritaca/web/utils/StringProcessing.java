package br.unifesp.maritaca.web.utils;

public class StringProcessing {
	public static final String SPACEWILDCARD="_";
	public static String getCompactedVersion(String str){
		if(str == null) return null;
		return str.replaceAll(" ", SPACEWILDCARD);
	}
}
