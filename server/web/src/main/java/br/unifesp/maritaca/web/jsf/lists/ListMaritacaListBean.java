package br.unifesp.maritaca.web.jsf.lists;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import br.unifesp.maritaca.business.list.ManagerListEJB;
import br.unifesp.maritaca.business.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;
import br.unifesp.maritaca.web.utils.Utils;

/**
 * Bean used to present lists to the user.
 * @author tiagobarabasz
 */
@ManagedBean
@RequestScoped
public class ListMaritacaListBean extends MaritacaJSFBean {
	
	private static final long serialVersionUID = 1L;
	
	@Inject ManagerListEJB managerListEJB;
	
	private Collection<MaritacaListDTO> myLists;
	
	public Collection<MaritacaListDTO> getMyLists() {
		return myLists;
	}

	public void setMyLists(Collection<MaritacaListDTO> myLists) {
		this.myLists = myLists;
	}
	
	public void removeList(MaritacaListDTO list) {
		if(managerListEJB.removeMaritacaList(list)) {
			getMyLists().remove(list);
		} else {
			throw new RuntimeException(Utils.getMessageFromResourceProperties("list_remove_error"));
		}	
	}

	@PostConstruct
	public void updateMyLists() {
		setMyLists(managerListEJB.getMaritacaListsByOwner(getCurrentUser()));
	}		
}