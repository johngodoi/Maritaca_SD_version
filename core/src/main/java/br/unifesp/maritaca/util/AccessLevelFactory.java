package br.unifesp.maritaca.util;

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
}
