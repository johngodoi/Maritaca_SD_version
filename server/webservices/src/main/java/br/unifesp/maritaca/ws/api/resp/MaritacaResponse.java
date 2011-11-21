package br.unifesp.maritaca.ws.api.resp;


public abstract class MaritacaResponse {
	public static final String FORM_TYPE = "form";
	public static final String RESPONSE_TYPE = "response";
	public static final String OK="ok";
	public static final String FAIL="fail";
	
	public static final int OK_CODE=200;
	public static final int ERROR_CODE = 500;
	public static final int SYSTEM_ERROR = 1000;
	
	
	private String status;
	private int code;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
}
