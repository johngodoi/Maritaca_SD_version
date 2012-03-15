package br.unifesp.maritaca.access;

import java.util.ArrayList;
import java.util.List;

import br.unifesp.maritaca.access.operation.Operation;

public enum AccessLevel {

	NO_ACCESS,
	READ_AND_LIST(Operation.LIST, Operation.READ),
	WRITE_ONLY(Operation.WRITE),
	FULL_ACCESS(Operation.values());

	AccessLevel(Operation... ops) {
		enabledOperations = new ArrayList<Operation>(0);
		for (Operation op : ops) {
			enabledOperations.add(op);
		}
	}

	private List<Operation> enabledOperations;

	public List<Operation> getEnabledOperations() {
		return enabledOperations;
	}

	public boolean isOperationEnabled(Operation op) {
		for (Operation operation : getEnabledOperations()) {
			if (op.equals(operation)) {
				return true;
			}
		}
		return false;
	}

	public static AccessLevel getAccessLevelFromString(String str) {
		for (AccessLevel al : values()) {
			if (al.toString().equals(str)) {
				return al;
			}
		}
		return null;
	}
}
