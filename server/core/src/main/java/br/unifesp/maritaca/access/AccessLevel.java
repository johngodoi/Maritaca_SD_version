package br.unifesp.maritaca.access;

import java.util.List;

import br.unifesp.maritaca.access.operation.Operation;

public abstract class AccessLevel {
	
	private List<Operation> enabledOperations;
	
	public abstract String toString();
	public List<Operation> getEnabledOperations() {
		return enabledOperations;
	}

	public boolean isOperationEnabled(Operation op) {
		for(Operation operation : getEnabledOperations()){
			return op.equals(operation);
		}
		return false;
	}
	protected void setEnabledOperations(List<Operation> enabledOperations) {
		this.enabledOperations = enabledOperations;
	}
}
