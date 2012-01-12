package br.unifesp.maritaca.web.jsf.form;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
public class EditFormBean extends AbstractBean {
	@ManagedProperty("#{currentUserBean.user}")
	private User user;
	private Form form;
	private String saveStatus;

	private boolean newForm;
	private boolean editForm;

	public EditFormBean() {
		super(true, false);
		form = new Form();
		newForm = true;
		editForm = true;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isNewForm() {
		return newForm;
	}

	public void setNewForm(boolean newForm) {
		this.newForm = newForm;
		setEditForm(newForm);
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		if (form == null)
			return;
		this.form = form;
		// if form has key, it is not a new form
		if (form.getKey() == null) {
			setNewForm(true);
		} else {
			if (form.getXml() == null) {
				this.form = formAnswCtrl.getForm(form.getKey());
			}
		}
	}

	public String saveForm() {
		if (form.getXml() != null && form.getXml().length() > 0) {
			form.setUser(user);
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

	public boolean isEditForm() {
		return editForm;
	}

	public void setEditForm(boolean editForm) {
		this.editForm = editForm;
	}

	public String deleteForm() {
		if (getForm().getKey() != null) {
			formAnswCtrl.deleteForm(getForm());
		}
		return null;
	}

}
