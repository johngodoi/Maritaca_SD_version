package br.unifesp.maritaca.access;

import java.util.ArrayList;
import java.util.List;

import br.unifesp.maritaca.access.operation.Operation;

public enum AccessLevel {
	
	PRIVATE_ACCESS("private"),
	READ_ONLY("read-only"),
	READ_WRITE("read-write");
	
	AccessLevel(String str, Operation... ops){
		this.description = str;
		enabledOperations = new ArrayList<Operation>(0);
		for(Operation op : ops){
			enabledOperations.add(op);
		}
	}
	
	private String description;
	
	private List<Operation> enabledOperations;
	
	public List<Operation> getEnabledOperations() {
		return enabledOperations;
	}

	public boolean isOperationEnabled(Operation op) {
		for(Operation operation : getEnabledOperations()){
			return op.equals(operation);
		}
		return false;
	}
	
	@Override
	public String toString(){
		return description;
	}
	
	public static AccessLevel getAccessLevelFromString(String str){
		for(AccessLevel al : values()){
			if(al.toString().equals(str)){
				return al;
			}
		}
		return null;
	}
}
