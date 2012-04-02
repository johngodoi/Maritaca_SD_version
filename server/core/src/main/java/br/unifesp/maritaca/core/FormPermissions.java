package br.unifesp.maritaca.core;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.unifesp.maritaca.access.AccessLevel;
import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;

@Entity
public class FormPermissions {
	@Id
	private UUID key;
	@Column(indexed = true)
	@Minimal
	private Form form;
	@Column(indexed = true)
	@Minimal
	private MaritacaList maritacaList = new MaritacaList();
	@Column
	@Minimal
	private Long expDate;
	@Column
	@Minimal
	private AccessLevel formAccess;
	@Column
	@Minimal
	private AccessLevel answAccess;
	
	public FormPermissions() {
	}
	
	public FormPermissions(Form form, MaritacaList list, AccessLevel formAccess, AccessLevel answAccess){
		setForm(form);
		setMaritacaList(list);
		setFormAccess(formAccess);
		setAnswAccess(answAccess);		
	}

	public FormPermissions(AccessLevel formAccess, AccessLevel answAccess){
		setFormAccess(formAccess);
		setAnswAccess(answAccess);
	}

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

	public void setForm(String fuid) {
		Form f = new Form();
		f.setKey(fuid);
		this.setForm(f);
	}

	public void setExpDate(Long expDate) {
		this.expDate = expDate;
	}

	public void setExpDate(String sdate) {
		// TODO get the date correctly
		setExpDate(Long.parseLong(sdate));
	}

	public Long getExpDate() {
		return expDate;
	}

	public void setExpirationDate(Date expDate) {
		setExpDate(expDate.getTime());
	}

	public Date getExpirationDate() {
		if(getExpDate() == null)return null;
		Date d = new Date();
		d.setTime(getExpDate());
		return d;
	}

	public MaritacaList getMaritacaList() {
		return maritacaList;
	}

	public void setMaritacaList(MaritacaList list) {
		this.maritacaList = list;
	}

	public void setMaritacaList(String uid) {
		MaritacaList g = new MaritacaList();
		g.setKey(uid);
		setMaritacaList(g);
	}

	public AccessLevel getFormAccess() {
		return formAccess;
	}

	public void setFormAccess(AccessLevel formAccess) {
		this.formAccess = formAccess;
	}

	public void setFormAccess(String access) {
		setFormAccess(AccessLevel.getAccessLevelFromString(access));
	}

	public AccessLevel getAnswAccess() {
		return answAccess;
	}

	public void setAnswAccess(String access) {
		setAnswAccess(AccessLevel.getAccessLevelFromString(access));
	}

	public void setAnswAccess(AccessLevel answAccess) {
		this.answAccess = answAccess;
	}

	public FormPermissions clone(){
		return null;
	}
}
