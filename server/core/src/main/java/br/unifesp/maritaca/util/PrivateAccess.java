package br.unifesp.maritaca.util;

public class PrivateAccess implements AccessLevel {

	@Override
	public int toInt() {
		return 0;
	}
	
	@Override
	public String toString() {
		return "private";
	}

}
