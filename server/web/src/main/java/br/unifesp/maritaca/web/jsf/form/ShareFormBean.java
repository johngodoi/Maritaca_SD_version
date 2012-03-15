package br.unifesp.maritaca.web.jsf.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.web.jsf.util.ItemListBean;

/**
 * Managedbean for the Form sharing service the bean loads the url for sharing.
 * In the future, it must load and update access levels of the form
 * 
 * @author emiguel, tiagobarabasz
 * 
 */
@ManagedBean
@ViewScoped
public class ShareFormBean extends ItemListBean {
	private static final long serialVersionUID = 1L;	
	private static final String ROOT_FOR_SHARING      = "/ws/form/share/";
	
	private Form form;

	public ShareFormBean() {
		super(true, true);
	}
	
	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		if (form.getUrl() == null) {
			form = formAnswCtrl.getForm(form.getKey(), true);
		}
		this.form = form;
	}

	public void setForm(String formKey) {
		if (formKey == null)
			return;
		Form form = new Form();
		form.setKey(formKey);
		setForm(form);
	}

	public String getUrl() {
		if (form == null)
			return "";
		return form.getUrl();
	}

	public void setUrl(String newUrl) {
		// formShare.setUrl(newUrl);
	}
	
	public String cancel(){
		return "/faces/views/home";
	}

	public String getRootForSharing() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();
 
		return request.getScheme() + "://" + request.getServerName() + ":"
				+ request.getLocalPort() + request.getContextPath()
				+ ROOT_FOR_SHARING;
	}

	public void setRootForSharing(String root) {
	}

	@Override
	protected boolean saveObject(Object formObj) {
		if(! (formObj instanceof Form) ){
			return false;
		}
		
		Form form = (Form) formObj;
		
		if(!super.formAnswCtrl.saveForm(form)){
			return false;
		}
				
		return true;
	}

	@Override
	protected Object createObjectFromItem() {		
		return form;
	}

	@Override
	protected String searchItemInDatabase(String selectedItem) {
		Group group = super.userCtrl.searchGroupByName(getSelectedItem());
		
		if(group==null){
			return null;
		} else {
			return group.getName();
		}
	}

	@Override
	public List<String> autoComplete(String prefix) {
		Collection<Group> groups       = userCtrl.groupsStartingWith(prefix);
		List<String>      groupsNames  = new ArrayList<String>();
			
		for(Group gr : groups){
			//set data of the owner
			gr.setOwner(userCtrl.getOwnerOfGroup(gr));
			groupsNames.add(gr.getName());
		}
		return groupsNames;
	}

	@Override
	protected boolean newItem(Object form) {
		//Forms being shared are already saved in the database
		return false;
	}
}