package br.unifesp.maritaca.access.operation;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public enum Operation {
	CREATE,
	READ,
	UPDATE,	
	DELETE,
	SHARE,
	LIST;
	
	public static Operation[] allExceptShare(){
		List<Operation> ops = new ArrayList<Operation>();
		for(Operation op : Operation.values()){
			if(!op.equals(SHARE)){
				ops.add(op);
			}
		}
		return ops.toArray(new Operation[ops.size()]);
	}
	
	public static Operation fromString(String str){
		for(Operation op : Operation.values()){
			if(op.name().equals(str)){
				return op;
			}
		}
		throw new IllegalArgumentException("Invalid string for enum operation: " +str);
	}
}
