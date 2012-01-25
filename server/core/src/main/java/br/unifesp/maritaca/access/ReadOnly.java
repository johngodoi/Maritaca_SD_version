package br.unifesp.maritaca.access;

import java.util.ArrayList;

import br.unifesp.maritaca.access.operation.Operation;

public class ReadOnly extends AccessLevel {

	private static ReadOnly instance;

	private ReadOnly() {
		setEnabledOperations(new ArrayList<Operation>());
		// none operation
	}

	public static ReadOnly getInstance() {
		if (instance == null) {
			instance = new ReadOnly();
		}
		return instance;
	}

	@Override
	public String toString() {
		return AccessLevelFactory.READONLY;
	}

}
