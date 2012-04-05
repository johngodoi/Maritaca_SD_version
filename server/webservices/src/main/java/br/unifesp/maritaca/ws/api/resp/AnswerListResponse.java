package br.unifesp.maritaca.ws.api.resp;

import java.util.Collection;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import br.unifesp.maritaca.core.Answer;

@XmlRootElement(name="answers")
public class AnswerListResponse extends MaritacaResponse {
	
	private int size;
	private Collection<Answer> list;
	
	public AnswerListResponse() {
		setStatus(Response.Status.OK);
	}
	
	@XmlAttribute
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	@XmlElement(name="answer")
	public Collection<Answer> getList() {
		return list;
	}
	public void setList(Collection<Answer> list) {
		if(list == null) 
			return;
		this.list = list;
		setSize(list.size());
	}

}
