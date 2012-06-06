package br.unifesp.maritaca.business.util;

/**
 * This class is to set all the constants used in business layer
 * 
 * @author Maritaca team
 */
public class ConstantsBusiness {
	public static final String ALL_USERS 		= "all_users"; 	// public group
	public static final String WS_USER_KEY 		= "userKey";	
	public static final String EMAIL_REG_EXP 	= "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[a-zA-Z]{2,4}$";
	public static final String ENCODING_UTF8 	= "UTF-8";
	
	/* App Mobile */
	public static final String MOB_SCRIPT_LOCATION	= "MOB_SCRIPT_LOCATION";
	public static final String MOB_MARITACA_PATH	= "MOB_MARITACA_PATH";
	public static final String MOB_PROJECTS_PATH	= "MOB_PROJECTS_PATH";
	public static final String MOB_FORM_XML_PATH	= "/maritaca-mobile/assets/sample.xml";
	public static final String MOB_BIN_PATH			= "/maritaca-mobile/bin/";	
	public static final String MOB_MIMETYPE			= "application/vnd.android.package-archive";
}