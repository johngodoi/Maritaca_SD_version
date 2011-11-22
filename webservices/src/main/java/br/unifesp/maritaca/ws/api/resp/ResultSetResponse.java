package br.unifesp.maritaca.ws.api.resp;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="resultset")
public class ResultSetResponse<E> extends MaritacaResponse {
	private int size;
	private  Collection<E> list;
	
	public ResultSetResponse() {
		setStatus(Response.Status.OK);
	}
	
	@XmlAttribute
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	@XmlElement(name="element")
	public Collection<E> getList() {
		return list;
	}
	public void setList(Collection<E> list) {
		if(list == null) return;
		this.list = list;
		setSize(list.size());
	}
	
}
