package br.unifesp.maritaca.access.operation;

public class Delete implements Operation {
	private static Delete instance;

	private Delete() {
	}

	public static Delete getInstance() {
		if (instance == null) {
			instance = new Delete();
		}
		return instance;
	}
}
