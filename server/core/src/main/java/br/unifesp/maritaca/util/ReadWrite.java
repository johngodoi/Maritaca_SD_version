package br.unifesp.maritaca.util;

public class ReadWrite implements AccessLevel {

	@Override
	public int toInt() {
		return 3;
	}
	
	@Override
	public String toString() {
		return "read-write";
	}

}
