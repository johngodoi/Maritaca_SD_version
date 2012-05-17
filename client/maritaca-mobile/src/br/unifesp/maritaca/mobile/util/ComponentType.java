package br.unifesp.maritaca.mobile.util;

public enum ComponentType {
	TEXT		("text"),
	NUMBER		("number"),
	COMBOBOX	("combobox"),
	CHECKBOX	("checkbox"),
	RADIOBOX	("radiobox"),
	DATE		("date"),
	TIME		("time"),
	TIMESTAMP	("timestamp"),
	SLIDE		("slide"),
	PICTURE		("picture"),
	AUDIO		("audio"),
	VIDEO		("video"),
	LOCATION	("location"),
	TEMPERATURE	("temperature"),
	MOVEMENT	("movement"),
	SIGNAL		("signal");
	
	private String description;
	
	private ComponentType(String description){
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public static ComponentType getComponentTypeByDescription(String description){
		for (ComponentType type : ComponentType.values()) {
			if(type.description.equals(description))
				return type;
		}
		return null;
	}
}	
