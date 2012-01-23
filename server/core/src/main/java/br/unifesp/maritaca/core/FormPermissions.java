package br.unifesp.maritaca.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;
import br.unifesp.maritaca.util.AccessLevel;
import br.unifesp.maritaca.util.AccessLevelFactory;

@Entity
public class FormPermissions {
	@Id
	private UUID key;
	@Column(indexed = true)
	@Minimal
	private Form form;
	@Column(indexed = true)
	@Minimal
	private Group group = new Group();
	@Column
	private Calendar expDate;
	@Column
	private AccessLevel formAccess;
	@Column
	private AccessLevel answAccess;

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

	public void setExpDate(Calendar expDate) {
		this.expDate = expDate;
	}
	
	public void setExpDate(String sdate){
		//TODO get the date correctly
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yy hh:mm a");
		Date date;
		try {
			date = sdf.parse(sdate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			setExpDate(cal);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public void setGroup(String uid) {
		Group g = new Group();
		g.setKey(uid);
		setGroup(g);
	}

	public AccessLevel getFormAccess() {
		return formAccess;
	}

	public void setFormAccess(AccessLevel formAccess) {
		this.formAccess = formAccess;
	}

	public void setFormAccess(String access) {
		setFormAccess(AccessLevelFactory.getAccessLevelFromString(access));
	}

	public AccessLevel getAnswAccess() {
		return answAccess;
	}

	public void setAnswAccess(String access) {
		setAnswAccess(AccessLevelFactory.getAccessLevelFromString(access));
	}

	public void setAnswAccess(AccessLevel answAccess) {
		this.answAccess = answAccess;
	}

	public Calendar getExpDate() {
		return expDate;
	}

}
