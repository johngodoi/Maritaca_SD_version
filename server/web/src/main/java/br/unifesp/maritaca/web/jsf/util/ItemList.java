package br.unifesp.maritaca.web.jsf.util;

import java.util.ArrayList;
import java.util.List;

import br.unifesp.maritaca.web.utils.Utils;

/**
 * Class used together with the itemList.xhtml file to create
 * a search field and a box to add itens of a specified type. 
 * It also implements an auto complete feature to choose the
 * itens to be added.<br>
 * Part of the code needed to create the search and add
 * box is implemented here and some of it is delegated to its
 * descendents.
 * @author tiagobarabasz
 * @param <T>
 */
public abstract class ItemList<T>{
	
	private static final String ITEM_LIST_EMPTY_FIELD   = "item_list_add_empty_field";
	private static final String ITEM_LIST_REPEATED_ITEM = "item_list_repeated_item";
	
	private List<T> usedItens;
	private List<T> cachedItens;
	private String  selectedItem;
	private String  addItemError;
	private String  successReturnString;
	
	protected abstract T       searchItemInDatabase(String selectedItem);
	protected abstract List<T> searchAutoCompleteItens(String prefix);
	protected abstract String  itemToString(T item);
	
	public List<String> autoComplete(String prefix){
		List<String> autoCompleteList = new ArrayList<String>();
		for(T item : searchAutoCompleteItens(prefix)){
			autoCompleteList.add(itemToString(item));
		}
		return autoCompleteList;
	}
	
	public ItemList() {
		usedItens   = new ArrayList<T>();
		cachedItens = new ArrayList<T>();
	}

	public List<T> getUsedItens() {
		return usedItens;
	}


	public void setUsedItens(List<T> usedItens) {
		this.usedItens = usedItens;
	}


	public String getSelectedItem() {
		return selectedItem;
	}


	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	public void addSelectedItem(){
		clearAddItemError();
		
		if(getSelectedItem()==null||getSelectedItem().equals("")){
			setAddItemError(Utils
					.getMessageFromResourceProperties(ITEM_LIST_EMPTY_FIELD));
			return;
		}
		
		if(searchItemByString(getSelectedItem())!=null){
			setAddItemError(Utils
					.getMessageFromResourceProperties(ITEM_LIST_REPEATED_ITEM));
			return;
		}
		
		T   item = searchCachedItemByString(getSelectedItem());				
		if(item==null){
			item = searchItemInDatabase(getSelectedItem());
		}
		
		getUsedItens().add(item);
	}

	protected void clearAddItemError(){
		setAddItemError("");
	}

	public void clearItens() {
		getUsedItens().clear();
		setSelectedItem(null);
	}
	
	public void setRemoveItem(String itemStr){
		T itemToRemove = searchItemByString(itemStr);		
		getUsedItens().remove(itemToRemove);
	}

	private T searchCachedItemByString(String itemStr){
		return searchItemInList(itemStr, getCachedItens());
	}
	
	private T searchItemByString(String itemStr){
		return searchItemInList(itemStr, getUsedItens());
	}
	
	private T searchItemInList(String itemStr, List<T> list){
		for(T item : list){
			if(item.toString().equals(itemStr)){
				return item;
			}
		}
		return null;		
	}

	public String getAddItemError() {
		return addItemError;
	}

	public void setAddItemError(String addItemError) {
		this.addItemError = addItemError;
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
	public List<T> getCachedItens() {
		return cachedItens;
	}
	public void setCachedItens(List<T> cachedItens) {
		this.cachedItens = cachedItens;
	}
}