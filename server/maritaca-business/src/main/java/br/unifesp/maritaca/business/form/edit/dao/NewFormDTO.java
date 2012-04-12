package br.unifesp.maritaca.business.form.edit.dao;

import java.util.UUID;

import br.unifesp.maritaca.business.base.dto.BaseDTO;

public class NewFormDTO extends BaseDTO {
	
	private static final long serialVersionUID = 1L;

	private String xml;
	
	private String title;
	
	private UUID userKey;

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public UUID getUserKey() {
		return userKey;
	}

	public void setUserKey(UUID userKey) {
		this.userKey = userKey;
	}

}
