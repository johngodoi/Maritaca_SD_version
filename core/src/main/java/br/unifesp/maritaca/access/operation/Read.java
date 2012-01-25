package br.unifesp.maritaca.access.operation;

public class Read implements Operation {
	private static Read instance;

	private Read() {
	}

	public static Read getInstance() {
		if (instance == null) {
			instance = new Read();
		}
		return instance;
	}
}
