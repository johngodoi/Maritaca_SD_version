package br.unifesp.maritaca.access;

import java.util.ArrayList;
import java.util.List;

public class AccessLevelFactory {
	public static final String PRIVATE = "private";
	public static final String PUBLIC = "public";
	public static final String READONLY = "read-only";
	public static final String READWRITE = "read-write";

	public static AccessLevel getAccessLevelFromString(String access) {
		if (access.equals(PRIVATE)) {
			return PrivateAccess.getInstance();
		} else if (access.equals(PUBLIC)) {
			return PublicAccess.getInstance();
		} else if (access.equals(READONLY)) {
			return ReadOnly.getInstance();
		} else if (access.equals(READWRITE)) {
			return ReadWrite.getInstance();
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
