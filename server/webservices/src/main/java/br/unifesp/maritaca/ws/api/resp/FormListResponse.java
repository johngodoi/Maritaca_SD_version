package br.unifesp.maritaca.ws.api.resp;

import java.util.Collection;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import br.unifesp.maritaca.business.form.dto.FormDTO;

@XmlRootElement(name="forms")
public class FormListResponse extends MaritacaResponse {
	
	private int size;
	private Collection<FormDTO> list;
	
	public FormListResponse() {
		setStatus(Response.Status.OK);
	}
	
	@XmlAttribute
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	@XmlElement(name="form")
	public Collection<FormDTO> getList() {
		return list;
	}
	public void setList(Collection<FormDTO> list) {
		if(list == null) 
			return;
		this.list = list;
		setSize(list.size());
	}

}
