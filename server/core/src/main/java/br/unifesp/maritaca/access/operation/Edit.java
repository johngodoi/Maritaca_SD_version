package br.unifesp.maritaca.access.operation;

public class Edit implements Operation {
	private static Edit instance;

	private Edit() {
	}

	public static Edit getInstance() {
		if (instance == null) {
			instance = new Edit();
		}
		return instance;
	}
}
