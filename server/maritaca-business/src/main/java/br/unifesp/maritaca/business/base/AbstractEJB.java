package br.unifesp.maritaca.business.base;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import br.unifesp.maritaca.business.base.dao.ConfigurationDAO;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.persistence.permission.Accessor;
import br.unifesp.maritaca.persistence.permission.Document;
import br.unifesp.maritaca.persistence.permission.Permission;
import br.unifesp.maritaca.persistence.permission.Rule;

public abstract class AbstractEJB implements Serializable {

	private static final long serialVersionUID = 1L;
	@Inject protected ConfigurationDAO configurationDAO;
	protected Rule rules = Rule.getInstance();
	
	/**
	 * 
	 * @param lstUUID
	 * @param userUUID
	 * @return true if userUUID is into lstUUID
	 */
	protected Boolean isMemberOfTheList(List<UUID> lstUUID, UUID userUUID) {
		if(lstUUID != null && !lstUUID.isEmpty()) {
			if(lstUUID.contains(userUUID))
				return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param obj
	 * @param userKey
	 * @return Permission
	 */
	protected Permission getPermission(Form form, UUID userKey, Document doc) {
		if(form != null && configurationDAO.isRootUser(userKey)) {
			return rules.getPermission(form.getPolicy(), doc, Accessor.OWNER);
		}
		
		else if(form != null && userKey.toString().equals(form.getUser().getKey().toString())) {
			return rules.getPermission(form.getPolicy(), doc, Accessor.OWNER);
		}
		
		else if(form != null && isMemberOfTheList(form.getLists(), userKey)) {
			return rules.getPermission(form.getPolicy(), doc, Accessor.LIST);
		}
		
		else if(form != null && form.isPublic()) {
			return rules.getPermission(form.getPolicy(), doc, Accessor.ALL);
		}
		return null;
	}
}