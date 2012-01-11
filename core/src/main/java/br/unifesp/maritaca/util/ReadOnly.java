package br.unifesp.maritaca.util;

public class ReadOnly implements AccessLevel {

	@Override
	public int toInt() {
		return 2;
	}
	
	@Override
	public String toString() {
		return "read-only";
	}

}
