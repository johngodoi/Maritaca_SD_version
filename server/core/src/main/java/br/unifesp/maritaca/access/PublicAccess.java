package br.unifesp.maritaca.access;

import java.util.ArrayList;

import br.unifesp.maritaca.access.operation.Operation;

public class PublicAccess extends AccessLevel {
	private static PublicAccess instance;

	private PublicAccess() {
		setEnabledOperations(new ArrayList<Operation>());
		// none operation
	}

	public static PublicAccess getInstance() {
		if (instance == null) {
			instance = new PublicAccess();
		}
		return instance;
	}

	@Override
	public String toString() {
		return AccessLevelFactory.PUBLIC;
	}

}
