package br.unifesp.maritaca.web.jsf.lists;

import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.business.list.edit.ListEditorEJB;
import br.unifesp.maritaca.persistence.dto.UserDTO;
import br.unifesp.maritaca.web.jsf.util.ItemListBean;

public class ItemListUsersBean extends ItemListBean<UserDTO>{

	private ListEditorEJB listEditorEJB;
	private UUID          currentUserKey;

	@Override
	protected UserDTO searchItemInDatabase(String selectedItem) {					
		return getListEditorEJB().searchUserByEmail(selectedItem);
	}

	@Override
	protected List<UserDTO> searchAutoCompleteItens(String prefix) {
		return getListEditorEJB().usersStartingWith(prefix);
	}

	@Override
	protected String itemToString(UserDTO list) {
		return list.getEmail();
	}

	public UUID getCurrentUserKey() {
		return currentUserKey;
	}

	public void setCurrentUserKey(UUID currentUserKey) {
		this.currentUserKey = currentUserKey;
	}

	public ListEditorEJB getListEditorEJB() {
		return listEditorEJB;
	}

	public void setListEditorEJB(ListEditorEJB listEditorEJB) {
		this.listEditorEJB = listEditorEJB;
	}
}
