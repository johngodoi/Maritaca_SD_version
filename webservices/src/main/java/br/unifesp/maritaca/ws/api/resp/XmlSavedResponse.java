package br.unifesp.maritaca.ws.api.resp;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import br.unifesp.maritaca.core.Form;

@XmlRootElement(name="xmlsaved")
public class XmlSavedResponse extends MaritacaResponse{
	private String id;
	private String type;
	
	public XmlSavedResponse() {
		setStatus(MaritacaResponse.OK);
		setCode(MaritacaResponse.OK_CODE);
	}

	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}
	
	public <T> void setId( T obj){
		id = obj.toString();
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}
}
