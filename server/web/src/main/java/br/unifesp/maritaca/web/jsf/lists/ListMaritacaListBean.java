package br.unifesp.maritaca.web.jsf.lists;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import br.unifesp.maritaca.business.list.list.ListMaritacaListEJB;
import br.unifesp.maritaca.business.list.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;

/**
 * Bean used to present lists to the user.
 * @author tiagobarabasz
 */
@ManagedBean
@RequestScoped
public class ListMaritacaListBean extends MaritacaJSFBean {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ListMaritacaListEJB   maritacaListEJB;	
	private List<MaritacaListDTO> myLists;
	
	public void removeList(MaritacaListDTO list) {
		maritacaListEJB.removeMaritacaList(list.getKey());
		getMyLists().remove(list);
	}

	@PostConstruct
	public void updateMyLists() {
		setMyLists(maritacaListEJB.getMaritacaListsByOwner(getCurrentUser().getKey()));
		removeUserListFromCurrentUser();
	}
        
    private void removeUserListFromCurrentUser() {    	
    	MaritacaListDTO myUserList = null;
    	for(MaritacaListDTO list : myLists){
    		if(getCurrentUser().getMaritacaList().equals(list.getKey())){
    			myUserList = list;
    		}
    	}
		if(myUserList==null){
			throw new RuntimeException("User list not found for: " + getCurrentUser().getEmail());
		}else{
			getMyLists().remove(myUserList);
		}    	
	}

	public List<MaritacaListDTO> getMyLists() {
		return myLists;
	}

	public void setMyLists(List<MaritacaListDTO> myLists) {
		this.myLists = myLists;
	}
}