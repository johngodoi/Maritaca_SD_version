package br.unifesp.maritaca.mobile.util;

public class Constants {
	
	public static final int SAVE_DIALOG = 1;
	public static final String ANSWERS_FILENAME	= "answers9.xml";
	public static final String DATE_ISO8601FORMAT	= "yyyy-MM-dd";
	
	public static final String MIN 			= "min";
	public static final String MAX 			= "max";
	public static final String DATE_FORMAT	= "format";
	public static final String DEFAULT 		= "default";
	
	// xml form elements
	public static final String FORM_ID 			= "id";
	public static final String FORM_TITLE 		= "title";
	public static final String FORM_URL	 		= "url";
	public static final String FORM_USER	 		= "user";

	public static final String FORM_QUESTIONS 	= "questions";
	public static final String FORM_XML 			= "sas";
	
	public static final String XML_ID 			= "id";
	public static final String XML_NEXT 			= "next";
	public static final String XML_REQUIRED 		= "required";
	public static final String XML_HELP 			= "help";
	public static final String XML_LABEL 			= "label";
	public static final String XML_COMPARISON 	= "comparison";
	public static final String XML_VALUE		 	= "value";
	public static final String XML_GOTO		 	= "goto";
	public static final String XML_EQUAL 			= "equal";
	public static final String XML_LESS 			= "less";
	public static final String XML_GREATER 		= "greater";
	public static final String XML_LESSEQUAL		= "lessequal";
	public static final String XML_GREATEREQUAL 	= "greaterequal";
	public static final String XML_TIMESTAMP	 	= "timestamp";
	public static final String XML_OPTION	 		= "option";
	
	// xml answer elements
	public static final String ANSWER_RESPONSE	= "collecteddata";
	public static final String ANSWER_FORMID		= "formId";
	public static final String ANSWER_USERID		= "userId";
	public static final String ANSWER_ANSWERS		= "answers";
	public static final String ANSWER_ANSWER		= "answer";
	public static final String ANSWER_QUESTION	= "question";
	
	
	// OAuth constants
	public static final String CLIENT_URL = "http://localhost:8082/";
	public static final String MARITACA_MOBILE = "maritacamobile";
	public static final String AUTHORIZATION_REQUEST = "authorizationRequest";
	
	public static final String SERVER_ADDR  = "http://10.0.2.2:8080/maritaca-web";
	public static final String OAUTH_URL    = SERVER_ADDR + "/oauth/";
	public static final String WS_ANSW_URL  = SERVER_ADDR + "/ws/answer/add/";
	
	public static final String RESPONSE_TYPE = "response_type";
	public static final String MARITACA_SECRET = "maritacasecret";
	public static final String ACCESS_TOKEN_REQUEST = "accessTokenRequest";
	public static final int    SERVER_PORT = 8082;
}