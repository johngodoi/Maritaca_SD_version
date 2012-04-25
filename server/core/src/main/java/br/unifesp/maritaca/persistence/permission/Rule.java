package br.unifesp.maritaca.persistence.permission;

import static br.unifesp.maritaca.persistence.permission.Accessor.*;
import static br.unifesp.maritaca.persistence.permission.Document.*;
import static br.unifesp.maritaca.persistence.permission.Policy.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author jimvalsan
 *
 */
public class Rule {
	
	private static final Log log = LogFactory.getLog(Rule.class);
	
	private static Rule instance;
	
	private Permission[][][] permission = new Permission[Policy.values().length][Document.values().length][Accessor.values().length];
	
	public static Rule getInstance() {
		if(instance == null)
			instance = new Rule();		
		return instance;		
	}
	
	public Rule() {
		this.createRules();
	}
	
	private void createRules() {
																						//PERMISSION(READ, UPDATE, DELETE, SHARE)
		//TODO: Use AccessLevel and Operation
		permission[PRIVATE.getIdPolicy()][FORM.getIdDocument()][OWNER.getIdAccessor()]  = new Permission(true, true, true, true);
        permission[PRIVATE.getIdPolicy()][FORM.getIdDocument()][LIST.getIdAccessor()]   = new Permission(false, false, false, false);
        permission[PRIVATE.getIdPolicy()][FORM.getIdDocument()][ALL.getIdAccessor()] 	= new Permission(false, false, false, false);
        
		permission[SHARED_HIERARCHICAL.getIdPolicy()][FORM.getIdDocument()][OWNER.getIdAccessor()]  = new Permission(true, true, true, true);
        permission[SHARED_HIERARCHICAL.getIdPolicy()][FORM.getIdDocument()][LIST.getIdAccessor()]   = new Permission(true, false, false, false);
        permission[SHARED_HIERARCHICAL.getIdPolicy()][FORM.getIdDocument()][ALL.getIdAccessor()] 	= new Permission(false, false, false, false);
        
        permission[SHARED_SOCIAL.getIdPolicy()][FORM.getIdDocument()][OWNER.getIdAccessor()]  = new Permission(true, true, true, true);
        permission[SHARED_SOCIAL.getIdPolicy()][FORM.getIdDocument()][LIST.getIdAccessor()]   = new Permission(true, false, false, false);
        permission[SHARED_SOCIAL.getIdPolicy()][FORM.getIdDocument()][ALL.getIdAccessor()]	  = new Permission(false, false, false, false);
        
        permission[PUBLIC.getIdPolicy()][FORM.getIdDocument()][OWNER.getIdAccessor()]  = new Permission(true, true, true, true);
        permission[PUBLIC.getIdPolicy()][FORM.getIdDocument()][LIST.getIdAccessor()]   = new Permission(true, false, false, false);
        permission[PUBLIC.getIdPolicy()][FORM.getIdDocument()][ALL.getIdAccessor()]    = new Permission(true, false, false, false);
        //
        permission[PRIVATE.getIdPolicy()][ANSWER.getIdDocument()][OWNER.getIdAccessor()]  = new Permission(true, true, true, true);
        permission[PRIVATE.getIdPolicy()][ANSWER.getIdDocument()][LIST.getIdAccessor()]   = new Permission(false, false, false, false);
        permission[PRIVATE.getIdPolicy()][ANSWER.getIdDocument()][ALL.getIdAccessor()] 	  = new Permission(false, false, false, false);
        
        permission[SHARED_HIERARCHICAL.getIdPolicy()][ANSWER.getIdDocument()][OWNER.getIdAccessor()]  = new Permission(true, true, true, true);
        permission[SHARED_HIERARCHICAL.getIdPolicy()][ANSWER.getIdDocument()][LIST.getIdAccessor()]   = new Permission(true, false, false, false);
        permission[SHARED_HIERARCHICAL.getIdPolicy()][ANSWER.getIdDocument()][ALL.getIdAccessor()] 	  = new Permission(false, false, false, false);
        
        permission[SHARED_SOCIAL.getIdPolicy()][ANSWER.getIdDocument()][OWNER.getIdAccessor()]  = new Permission(true, true, true, true);
        permission[SHARED_SOCIAL.getIdPolicy()][ANSWER.getIdDocument()][LIST.getIdAccessor()]   = new Permission(true, true, true, true);//
        permission[SHARED_SOCIAL.getIdPolicy()][ANSWER.getIdDocument()][ALL.getIdAccessor()] 	= new Permission(false, false, false, false);
        
        permission[PUBLIC.getIdPolicy()][ANSWER.getIdDocument()][OWNER.getIdAccessor()]  = new Permission(true, true, true, true);//
        permission[PUBLIC.getIdPolicy()][ANSWER.getIdDocument()][LIST.getIdAccessor()]   = new Permission(true, true, true, true);//
        permission[PUBLIC.getIdPolicy()][ANSWER.getIdDocument()][ALL.getIdAccessor()] 	 = new Permission(true, true, true, true);//
	}
	
	/**
	 * Gets a permission by idPolicy
	 * @param idPolicy
	 * @param idDocument
	 * @param idAccessor
	 * @return Permission
	 */
	public Permission getPermission(Policy policy, Document document, Accessor accessor) {
		if(policy != null && document != null && accessor != null)
			return permission[policy.getIdPolicy()][document.getIdDocument()][accessor.getIdAccessor()];
		return null;
	}
}