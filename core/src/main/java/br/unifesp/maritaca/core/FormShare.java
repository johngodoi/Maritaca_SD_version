package br.unifesp.maritaca.core;

import java.util.Calendar;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;
import br.unifesp.maritaca.util.AccessLevel;
import br.unifesp.maritaca.util.AccessLevelFactory;

@Entity
public class FormShare {
	@Id
	private UUID key;
	@Column(indexed = true)
	@Minimal
	private Form form;
	@Column(indexed = true)
	@Minimal
	private String url;
	@Column
	private Calendar expDate;
	@Column
	private AccessLevel publicAccess;

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public void setKey(String ks) {
		this.key = UUID.fromString(ks);
	}

	@Override
	public String toString() {
		if (getKey() != null) {
			return getKey().toString();
		}
		return super.toString();
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}
	
	public void setForm(String fuid){
		Form f = new Form();
		f.setKey(fuid);
		this.setForm(f);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Calendar getExpDate() {
		return expDate;
	}

	public void setExpDate(Calendar expDate) {
		this.expDate = expDate;
	}

	public AccessLevel getPublicAccess() {
		return publicAccess;
	}

	public void setPublicAccess(AccessLevel publicAccess) {
		this.publicAccess = publicAccess;
	}

	public void setPublicAccess(String access) {
		setPublicAccess(AccessLevelFactory.getAccessLevelFromString(access));
	}

}
