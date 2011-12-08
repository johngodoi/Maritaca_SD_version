package br.unifesp.maritaca.web.jsf.form;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.cassandra.cli.CliParser.setStatement_return;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
public class EditFormBean extends AbstractBean {
	@ManagedProperty("#{currentUserBean.user}")
	private User user;
	private String xml;
	private Form form;
	private String saveStatus;

//	@ManagedProperty("#{new}")
	private boolean newForm;

	public EditFormBean() {
		super(true, false);
		form = new Form();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public boolean isNewForm() {
		return newForm;
	}

	public void setNewForm(boolean newForm) {
		this.newForm = newForm;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public String saveForm() {
		if (xml != null && xml.length() > 0) {
			form.setUser(user);
			form.setXml(xml);
			if (formAnswCtrl.saveForm(form))
				setSaveStatus("success");
				return "success";
		}

		setSaveStatus("error");
		return "error";
	}

	public String getSaveStatus() {
		return saveStatus;
	}

	public void setSaveStatus(String saveStatus) {
		this.saveStatus = saveStatus;
	}

}
