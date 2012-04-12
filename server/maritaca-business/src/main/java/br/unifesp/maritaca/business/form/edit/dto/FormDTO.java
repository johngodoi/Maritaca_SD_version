package br.unifesp.maritaca.business.form.edit.dto;

import java.util.UUID;

import br.unifesp.maritaca.business.base.dto.BaseDTO;

public class FormDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;

	private String key;
	
	private String xml;
	
	private String title;
	
	private String url;
	
	private String policy;
	
	private UUID userKey;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public UUID getUserKey() {
		return userKey;
	}

	public void setUserKey(UUID userKey) {
		this.userKey = userKey;
	}

	
}