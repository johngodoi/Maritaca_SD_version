package br.unifesp.maritaca.util;

import java.util.ArrayList;
import java.util.List;

public class AccessLevelFactory {
	private static final String PRIVATE = "private";
	private static final String PUBLIC = "public";
	private static final String READONLY = "read-only";
	private static final String READWRITE = "read-write";

	public static AccessLevel getAccessLevelFromString(String access){
		if(access.equals(PRIVATE)){
			return new PrivateAccess();
		}else if(access.equals(PUBLIC)){
			return new PublicAccess();
		}else if(access.equals(READONLY)){
			return new ReadOnly();
		}else if(access.equals(READWRITE)){
			return new ReadWrite();
		}
		return null;
	}
	
	public static List<AccessLevel> getAccessLevels(){
		ArrayList<AccessLevel> l = new ArrayList<AccessLevel>();
		l.add(getAccessLevelFromString(PRIVATE));
		l.add(getAccessLevelFromString(PUBLIC));
		l.add(getAccessLevelFromString(READONLY));
		l.add(getAccessLevelFromString(READWRITE));
		return l;
	}
}
