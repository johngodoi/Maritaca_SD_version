package br.unifesp.maritaca.web.commons.msg;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageManager {
	private static MessageManager  instance;
	private ResourceBundle         resourceBundle;
	private Locale                 usedLocale;
	private Locale[]               supportedLocales = { Locale.ENGLISH, Locale.ROOT };
	private static final String    MESSAGES_FILE    = "messages.properties";
	
	static {
		setInstance(new MessageManager());
	}
	
	private MessageManager(){
		String bundle = MESSAGES_FILE;		
		setResourceBundle(ResourceBundle.getBundle(bundle, usedLocale));
	}
	
	public String getMessage(String msgKey){
		return resourceBundle.getString(msgKey);				
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public void setResourceBundle(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	public Locale getUsedLocale() {
		return usedLocale;
	}

	public void setUsedLocale(Locale usedLocale) {
		this.usedLocale = usedLocale;
	}

	public static MessageManager getInstance() {
		return instance;
	}

	public static void setInstance(MessageManager instance) {
		MessageManager.instance = instance;
	}
}
