package br.unifesp.maritaca.business.list.list;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.list.list.dao.ListMaritacaListDAO;
import br.unifesp.maritaca.business.list.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.business.util.UtilsBusiness;
import br.unifesp.maritaca.core.MaritacaList;

@Stateless
public class ListMaritacaListEJB {

	@Inject
	private ListMaritacaListDAO maritacaListDAO;	

	/**
	 * Returns all the lists owned by the given user including its
	 * user list (which is normally only accessed for internal operations).
	 * @param user
	 * @return A list containing each list of the giver user.
	 * @author tiagobarabasz
	 */
	public List<MaritacaListDTO> getMaritacaListsByOwner(UUID user) {
		List<MaritacaList>    list    = maritacaListDAO.getMaritacaListsByOwner(user);
		List<MaritacaListDTO> listDto = new ArrayList<MaritacaListDTO>();
		for(MaritacaList listItem : list){
			listDto.add(UtilsBusiness.convertToClass(listItem, MaritacaListDTO.class));
		}		
		return listDto;
	}
	
	public void removeMaritacaList(UUID listToRemoveKey) {
		MaritacaList listToRemove = new MaritacaList();
		listToRemove.setKey(listToRemoveKey);
		getMaritacaListDAO().removeMaritacaList(listToRemove);
	}
	
	public List<MaritacaListDTO> listsStartingWith(String startingString) {
		List<MaritacaList>    lists;
		List<MaritacaListDTO> foundLists;
		
		lists      = getMaritacaListDAO().maritacaListsStartingWith(startingString);
		foundLists = new ArrayList<MaritacaListDTO>();
		
		for(MaritacaList list : lists){
			MaritacaListDTO listDto;
			listDto = UtilsBusiness.convertToClass(list, MaritacaListDTO.class);
			foundLists.add(listDto);			
		}		
		return foundLists; 
	}
	
	public ListMaritacaListDAO getMaritacaListDAO() {
		return maritacaListDAO;
	}

	public void setMaritacaListDAO(ListMaritacaListDAO maritacaListDAO) {
		this.maritacaListDAO = maritacaListDAO;
	}
}