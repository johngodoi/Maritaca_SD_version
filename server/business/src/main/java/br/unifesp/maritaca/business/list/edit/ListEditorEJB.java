package br.unifesp.maritaca.business.list.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.account.edit.AccountEditorDAO;
import br.unifesp.maritaca.business.account.edit.dto.UserDTO;
import br.unifesp.maritaca.business.list.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.business.util.UtilsBusiness;
import br.unifesp.maritaca.persistence.entity.MaritacaList;
import br.unifesp.maritaca.persistence.entity.User;

@Stateless
public class ListEditorEJB {	
	
	@Inject
	private ListEditorDAO listEditorDAO;
	
	@Inject
	private AccountEditorDAO accountEditorDAO;

	public ListEditorEJB() {
	}

	public UserDTO searchUserByEmail(String email) {
		return UtilsBusiness.reflectClasses(accountEditorDAO.findUserByEmail(email),UserDTO.class);
	}

	public List<UserDTO> usersStartingWith(String prefix) {
		List<UserDTO> listUsers = new ArrayList<UserDTO>();		
		for(User usr : getListEditorDAO().usersStartingWith(prefix)){
			listUsers.add(UtilsBusiness.reflectClasses(usr, UserDTO.class));
		}				
		return listUsers;
	}
	
	public void saveMaritacaList(MaritacaListDTO listDto) {
		MaritacaList list = UtilsBusiness.reflectClasses(listDto, MaritacaList.class);
		getListEditorDAO().saveMaritacaList(list);
		listDto.setKey(list.getKey());
	}
	
	public List<UserDTO> searchUsersByMaritacaList(UUID searchedListKey) {
		MaritacaList  list     = getListEditorDAO().getMaritacaList(searchedListKey);		
		List<UserDTO> usersDto = new ArrayList<UserDTO>();
		
		if(list.getUsers().size()>0){
			for(UUID userKey : list.getUsers()){
				User    user    = accountEditorDAO.findUserByKey(userKey);
				UserDTO userDto = UtilsBusiness.reflectClasses(user, UserDTO.class);
				usersDto.add(userDto);
			}
		}
		return usersDto;
	}
	
	public MaritacaListDTO searchMaritacaListByName(String listName, UUID owner) {
		MaritacaList maritacaList = getListEditorDAO().searchMaritacaListByName(listName, owner);				
		return UtilsBusiness.reflectClasses(maritacaList, MaritacaListDTO.class);
	}

	public ListEditorDAO getListEditorDAO() {
		return listEditorDAO;
	}

	public void setListEditorDAO(ListEditorDAO listEditorDAO) {
		this.listEditorDAO = listEditorDAO;
	}
}
