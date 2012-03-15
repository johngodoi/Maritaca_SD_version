package br.unifesp.maritaca.web.jsf.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.web.jsf.AbstractBean;
import br.unifesp.maritaca.web.utils.Utils;

public abstract class ItemListBean extends AbstractBean{
	
	private static final long serialVersionUID        = 1L;
	
	private static final String ITEM_LIST_EMPTY_FIELD = "item_list_add_empty_field";
	private static final String ITEM_LIST_ADD_SUCESS  = "item_list_add_sucess";
	private static final String ITEM_LIST_ADD_FAILURE = "item_list_add_failure";
	
	private static final Log log = LogFactory.getLog(ItemListBean.class);

	private List<String> usedItens;
	private String       selectedItem;
	private String       addItemError;
	private String       successReturnString;
	
	protected abstract boolean saveObject(Object itemObject);
	protected abstract Object  createObjectFromItem();
	protected abstract String  searchItemInDatabase(String selectedItem);
	protected abstract boolean newItem(Object itemObject);
	
	public ItemListBean(boolean useFormAnsw, boolean useUser) {
		super(useFormAnsw, useUser);
		usedItens = new ArrayList<String>();
	}

	public List<String> getUsedItens() {
		return usedItens;
	}
	
	public abstract List<String> autoComplete(String prefix);


	public void setUsedItens(List<String> usedItens) {
		this.usedItens = usedItens;
	}


	public String getSelectedItem() {
		return selectedItem;
	}


	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	public void addSelectedItem(){
		if(getSelectedItem()==null||getSelectedItem().equals("")){
			setAddItemError(Utils
					.getMessageFromResourceProperties(ITEM_LIST_EMPTY_FIELD));
			return;
		}
		
		String item = searchItemInDatabase(getSelectedItem());
		if(item!=null){
			getUsedItens().add(item);
			return;
		} else {
			return;
		}		
	}

	public void setRemoveItem(String item){
		getUsedItens().remove(item);
	}

	public String getAddItemError() {
		return addItemError;
	}

	public void setAddItemError(String addItemError) {
		this.addItemError = addItemError;
	}

	public String save(){
		String    returnString = null;

		Object itemObject = createObjectFromItem();
		if (saveObject(itemObject)) {
			addMessage(ITEM_LIST_ADD_SUCESS, FacesMessage.SEVERITY_INFO);
		} else {
			addMessage(ITEM_LIST_ADD_FAILURE, FacesMessage.SEVERITY_ERROR);
			log.error("Error saving: " + itemObject.toString());			
		}
		
		clearItens();
		return returnString;
	}

	private void clearItens() {
		getUsedItens().clear();
		setSelectedItem(null);
	}

	public String cancel(){
		return "";
	}

	public String getSuccessReturnString() {
		return successReturnString;
	}

	public void setSuccessReturnString(String successReturnString) {
		this.successReturnString = successReturnString;
	}
}
