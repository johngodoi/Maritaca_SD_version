package br.unifesp.maritaca.web.jsf.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.web.jsf.account.CurrentUserBean;
import br.unifesp.maritaca.web.jsf.util.ItemListBean;
import br.unifesp.maritaca.web.utils.Utils;

/**
 * Managed bean for the Form sharing service the bean loads the url for sharing.
 * In the future, it must load and update access levels of the form
 * 
 * @author emiguel, tiagobarabasz
 * 
 */
//@ManagedBean
@ViewScoped
public class ShareFormBean extends ItemListBean {
	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUserBean;
	
	private static final long serialVersionUID = 1L;	
	private static final String ROOT_FOR_SHARING = "/ws/form/share/";
	
	private Form form;
	
	private static final String LIST_REMOVE_OWNER_ERROR = "list_remove_owner_error";

	public ShareFormBean() {
		super(true, true);
	}
	
	public Form getForm() {
		return form;
	}
	
	public void setForm(Form form) {
		//Creating a copy of the form in order to prevent temporary modifications
		form = formAnswCtrl.getForm(form.getKey(), true);	
		populateFormSharedList(form);
		
		this.form = form;		
	}

	private void populateFormSharedList(Form form) {
		super.getUsedItens().clear();
		for(FormPermissions fp : formAnswCtrl.getFormPermissions(form)){
			MaritacaList list = super.userCtrl.getMaritacaList(fp.getMaritacaList().getKey());
			if(!list.equals(super.userCtrl.getAllUsersList())){
				super.getUsedItens().add(list.getName());
			}			
		}
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
		
		List<MaritacaList> lists = formLists();		
		if(!super.formAnswCtrl.saveForm(form,lists)){
			return false;
		}
				
		return true;
	}
	
	private List<MaritacaList> formLists(){
		List<MaritacaList> lists = new ArrayList<MaritacaList>();
		for(String listName : getUsedItens()){
			MaritacaList list = super.userCtrl.searchMaritacaListByName(listName);
			lists.add(list);
		}
		return lists;
	}

	@Override
	protected Object createObjectFromItem() {		
		return form;
	}

	@Override
	protected String searchItemInDatabase(String selectedItem) {
		MaritacaList list = super.userCtrl.searchMaritacaListByName(getSelectedItem());
		
		if(list==null){
			return null;
		} else {
			return list.getName();
		}
	}

	@Override
	public List<String> autoComplete(String prefix) {
		Collection<MaritacaList> lists = userCtrl.maritacaListsStartingWith(prefix);
		List<String>      listsNames  = new ArrayList<String>();
			
		for(MaritacaList gr : lists){
			//set data of the owner
			gr.setOwner(userCtrl.getOwnerOfMaritacaList(gr));
			listsNames.add(gr.getName());
		}
		return listsNames;
	}

	@Override
	protected boolean newItem(Object form) {
		//Forms being shared are already saved in the database
		return false;
	}
	
	@Override
	public void setRemoveItem(String item){
		clearAddItemError();
		
		String currentUsrEmail = getCurrentUserBean().getUser().getEmail();		
		if(item.equals(currentUsrEmail)){
			setAddItemError(Utils
					.getMessageFromResourceProperties(LIST_REMOVE_OWNER_ERROR));
			return;
		}
		getUsedItens().remove(item);
	}

	public CurrentUserBean getCurrentUserBean() {
		return currentUserBean;
	}

	public void setCurrentUserBean(CurrentUserBean currentUserBean) {
		this.currentUserBean = currentUserBean;
	}
}