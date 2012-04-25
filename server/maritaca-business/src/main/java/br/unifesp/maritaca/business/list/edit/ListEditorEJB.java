package br.unifesp.maritaca.business.list.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.account.edit.dao.AccountEditorDAO;
import br.unifesp.maritaca.business.list.edit.dao.ListEditorDAO;
import br.unifesp.maritaca.business.list.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.business.util.UtilsBusiness;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.dto.UserDTO;

@Stateless
public class ListEditorEJB {	
	
	@Inject
	private ListEditorDAO listEditorDAO;
	
	@Inject
	private AccountEditorDAO accountEditorDAO;

	public ListEditorEJB() {
	}

	public UserDTO searchUserByEmail(String email) {
		return UtilsBusiness.convertToClass(accountEditorDAO.findUserByEmail(email),UserDTO.class);
	}

	public List<UserDTO> usersStartingWith(String prefix) {
		List<UserDTO> listUsers = new ArrayList<UserDTO>();		
		for(User usr : getListEditorDAO().usersStartingWith(prefix)){
			listUsers.add(UtilsBusiness.convertToClass(usr, UserDTO.class));
		}				
		return listUsers;
	}
	
	public void saveMaritacaList(MaritacaListDTO listDto) {
		MaritacaList list = UtilsBusiness.convertToClass(listDto, MaritacaList.class);
		getListEditorDAO().saveMaritacaList(list);
		listDto.setKey(list.getKey());
	}
	
	public List<UserDTO> searchUsersByMaritacaList(UUID searchedListKey) {
		MaritacaList list  = getListEditorDAO().getMaritacaList(searchedListKey);
		
		List<UserDTO> usersDto = new ArrayList<UserDTO>();
		for(UUID userKey : list.getUsers()){
			User    user    = accountEditorDAO.findUserByKey(userKey);
			UserDTO userDto = UtilsBusiness.convertToClass(user, UserDTO.class);
			usersDto.add(userDto);
		}
		return usersDto;
	}
	
	public MaritacaListDTO searchMaritacaListByName(String listName, UUID owner) {
		MaritacaList maritacaList = getListEditorDAO().searchMaritacaListByName(listName, owner);				
		return UtilsBusiness.convertToClass(maritacaList, MaritacaListDTO.class);
	}

	public ListEditorDAO getListEditorDAO() {
		return listEditorDAO;
	}

	public void setListEditorDAO(ListEditorDAO listEditorDAO) {
		this.listEditorDAO = listEditorDAO;
	}
}
