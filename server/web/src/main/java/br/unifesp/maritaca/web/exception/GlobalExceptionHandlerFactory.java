package br.unifesp.maritaca.web.exception;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class GlobalExceptionHandlerFactory extends ExceptionHandlerFactory {
	private ExceptionHandlerFactory parent;

	public GlobalExceptionHandlerFactory(ExceptionHandlerFactory parent) {
		this.parent = parent;
	}
	@Override
	public ExceptionHandler getExceptionHandler() {
		ExceptionHandler result = parent.getExceptionHandler();
        result = new GlobalExceptionHandler(result);
        return result;
	}

}
