package br.unifesp.maritaca.web.jsf.lists;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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
@ViewScoped
public class ListMaritacaListBean extends MaritacaJSFBean {
	
	private static final long serialVersionUID = 1L;
	
	@Inject ManagerListEJB managerListEJB;
	
	private Collection<MaritacaListDTO> myLists;
	
	public void removeList(MaritacaListDTO list) {
		if(managerListEJB.removeMaritacaList(list)) {
			getMyLists().remove(list);
		} else {
			throw new RuntimeException(Utils.getMessageFromResourceProperties("list_remove_error"));
		}	
	}

	@PostConstruct
	public void updateMyLists() {
            System.out.println("PostConstruct  - updateMyLists");
		setMyLists(managerListEJB.getMaritacaListsByOwner(getCurrentUser()));
	}
        
        /*** Setters y Getters ***/
        public Collection<MaritacaListDTO> getMyLists() {
		return myLists;
	}

	public void setMyLists(Collection<MaritacaListDTO> myLists) {
		this.myLists = myLists;
	}
}