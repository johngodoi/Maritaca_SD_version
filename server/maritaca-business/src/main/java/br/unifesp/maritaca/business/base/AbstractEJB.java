package br.unifesp.maritaca.business.base;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import br.unifesp.maritaca.business.base.dao.ConfigurationDAO;
import br.unifesp.maritaca.core.Answer;
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
		if(lstUUID.contains(userUUID))
			return true;
		return false;
	}
	
	/**
	 * 
	 * @param obj
	 * @param userKey
	 * @return Permission
	 */
	protected <T> Permission getPermission(T obj, UUID userKey) {
		if(obj instanceof Form) {
			Form form = (Form)obj;
			if(obj != null && configurationDAO.isRootUser(userKey)) {
				return rules.getPermission(form.getPolicy(), Document.FORM, Accessor.OWNER);
			}
			
			else if(obj != null && userKey == form.getUser().getKey()) {
				return rules.getPermission(form.getPolicy(), Document.FORM, Accessor.OWNER);			
			}
			
			else if(obj != null && isMemberOfTheList(form.getLists(), userKey)) {
				return rules.getPermission(form.getPolicy(), Document.FORM, Accessor.LIST);
			}
			
			else if(obj != null && form.isPublic()) {
				return rules.getPermission(form.getPolicy(), Document.FORM, Accessor.ALL);
			}
		}
		else if(obj instanceof Answer) {
			return null;
		}
		return null;
	}
}