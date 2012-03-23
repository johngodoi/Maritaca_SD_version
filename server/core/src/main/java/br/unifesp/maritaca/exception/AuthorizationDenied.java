package br.unifesp.maritaca.exception;

import java.util.UUID;

import br.unifesp.maritaca.access.operation.Operation;

public class AuthorizationDenied extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private Operation               operation;
	private Class<? extends Object> target;
	private UUID                   	targetId;
	private UUID                   	userId;
	
	public AuthorizationDenied(Class<? extends Object>  target, UUID targetId, UUID userId, Operation operation){		
		setTarget(target);
		setTargetId(targetId);
		setUserId(userId);
		setOperation(operation);
	}
	
	public Operation getOperation() {
		return operation;
	}
	
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public Class<? extends Object> getTarget() {
		return target;
	}

	public void setTarget(Class<? extends Object> target) {
		this.target = target;
	}

	public UUID getTargetId() {
		return targetId;
	}

	public void setTargetId(UUID targetId) {
		this.targetId = targetId;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}
}
