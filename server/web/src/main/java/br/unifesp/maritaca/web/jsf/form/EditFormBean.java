package br.unifesp.maritaca.web.jsf.form;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.Manager;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
@ViewScoped
public class EditFormBean extends AbstractBean {
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty("#{currentUserBean.user}")
	private User currentUserBean;

	@ManagedProperty("#{manager}")
	private Manager manager;
	
	private Form form;
	private String xml;
	private String saveFormAsTitle;
	
	private boolean newForm;
	private boolean editableForm;

	public EditFormBean() {
		super(true, false);
		clean();
	}

	public void clean() {
		setForm(new Form());
		setXml("");
		setNewForm(true);
		setEditableForm(true);
	}
	
	public void saveForm() {
		if (form.getXml() != null && form.getXml().length() > 0) {
			if (form.getUser() == null) {
				form.setUser(currentUserBean);
			}
			if(form.getTitle()==null||form.getTitle().length()==0){
				addMessage("form_edit_missing_title", FacesMessage.SEVERITY_ERROR);				
				return;
			}
			if (formAnswCtrl.saveForm(form)) {
				addMessage("form_edit_save_success", FacesMessage.SEVERITY_INFO);
				return;
			}
		}

		addMessage("form_edit_save_error", FacesMessage.SEVERITY_ERROR);
	}
	
	public void saveFormAs() {
		setForm(new Form());
		setNewForm(true);
		setEditableForm(true);
		form.setXml(getXml());
		form.setTitle(getSaveFormAsTitle());
		saveForm();
	}

	public String deleteForm() {
		if (getForm().getKey() != null) {
			formAnswCtrl.deleteForm(getForm());
			clean();
		}
		return null;
	}

	////// Getters and Setters //////
	public String getXml() {
		if (form != null && form.getXml() != null) {
			return form.getXml();
		}
		return xml;
	}

	public void setXml(String xml) {
		if (getForm() != null) {
			getForm().setXml(xml);
		}
		this.xml = xml;
	}

	public Form getForm() {
		return form;
	}
	
	public void setForm(Form form) {
		if (form == null)
			return;
		this.form = form;
		// if form has key, it is not a new form
		if (this.form.getKey() == null) {
			setNewForm(true);
		} else {
			if (this.form.getXml() == null) {
				this.form = formAnswCtrl.getForm(form.getKey(), false);
			}
			xml = this.form.getXml();
		}
	}

	public boolean isNewForm() {
		return newForm;
	}
	
	public void setNewForm(boolean newForm) {
		this.newForm = newForm;
		if (newForm) {
			setEditableForm(newForm);
		}
	}	
		
	public boolean isEditableForm() {
		if (isNewForm()) {
			return true;
		} else if (form != null && form.getKey() != null) {
			return manager.isOperationEnabled(form, Operation.EDIT);
		} else {
			return editableForm;
		}
	}
	
	public void setEditableForm(boolean editableForm) {
		this.editableForm = editableForm;
	}
	
	public String getSaveFormAsTitle() {
		return saveFormAsTitle;
	}

	public void setSaveFormAsTitle(String saveFormAsTitle) {
		this.saveFormAsTitle = saveFormAsTitle;
	}
	
	public User getCurrentUserBean() {
		return currentUserBean;
	}

	public void setCurrentUserBean(User currentUserBean) {
		this.currentUserBean = currentUserBean;
	}
	
	public Manager getManager() {
		return manager;
	}
	
	public void setManager(Manager manager) {
		this.manager = manager;
	}

}
