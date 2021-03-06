package br.unifesp.maritaca.persistence.permission;

import static br.unifesp.maritaca.persistence.permission.Accessor.*;
import static br.unifesp.maritaca.persistence.permission.Document.*;
import static br.unifesp.maritaca.persistence.permission.Operation.*;
import static br.unifesp.maritaca.persistence.permission.Policy.*;

/**
 * 
 * @author jimvalsan
 *
 */
public class Rule {
	
	
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
		permission[PRIVATE.getId()][FORM.getId()][OWNER.getId()]  				= new Permission(READ, UPDATE, DELETE, SHARE);
        permission[PRIVATE.getId()][FORM.getId()][LIST.getId()]   				= new Permission();
        permission[PRIVATE.getId()][FORM.getId()][ALL.getId()] 					= new Permission();
        
		permission[SHARED_HIERARCHICAL.getId()][FORM.getId()][OWNER.getId()]  	= new Permission(READ, UPDATE, DELETE, SHARE);
        permission[SHARED_HIERARCHICAL.getId()][FORM.getId()][LIST.getId()]   	= new Permission(READ);
        permission[SHARED_HIERARCHICAL.getId()][FORM.getId()][ALL.getId()] 		= new Permission();
        
        permission[SHARED_SOCIAL.getId()][FORM.getId()][OWNER.getId()]  		= new Permission(READ, UPDATE, DELETE, SHARE);
        permission[SHARED_SOCIAL.getId()][FORM.getId()][LIST.getId()]   		= new Permission(READ);
        permission[SHARED_SOCIAL.getId()][FORM.getId()][ALL.getId()]	  		= new Permission();
        
        permission[PUBLIC.getId()][FORM.getId()][OWNER.getId()]  				= new Permission(READ, UPDATE, DELETE, SHARE);
        permission[PUBLIC.getId()][FORM.getId()][LIST.getId()]   				= new Permission(READ);
        permission[PUBLIC.getId()][FORM.getId()][ALL.getId()]    				= new Permission(READ);
        //
        permission[PRIVATE.getId()][ANSWER.getId()][OWNER.getId()]  			= new Permission(READ, UPDATE, DELETE, SHARE);
        permission[PRIVATE.getId()][ANSWER.getId()][LIST.getId()]   			= new Permission();
        permission[PRIVATE.getId()][ANSWER.getId()][ALL.getId()] 	  			= new Permission();
        
        permission[SHARED_HIERARCHICAL.getId()][ANSWER.getId()][OWNER.getId()]  = new Permission(READ, UPDATE, DELETE, SHARE);
        permission[SHARED_HIERARCHICAL.getId()][ANSWER.getId()][LIST.getId()]  	= new Permission(UPDATE, DELETE, SHARE);
        permission[SHARED_HIERARCHICAL.getId()][ANSWER.getId()][ALL.getId()]  	= new Permission();
       
        permission[SHARED_SOCIAL.getId()][ANSWER.getId()][OWNER.getId()]  		= new Permission(READ, UPDATE, DELETE, SHARE);
        permission[SHARED_SOCIAL.getId()][ANSWER.getId()][LIST.getId()]   		= new Permission(READ, UPDATE, DELETE, SHARE);
        permission[SHARED_SOCIAL.getId()][ANSWER.getId()][ALL.getId()] 			= new Permission();
        
        permission[PUBLIC.getId()][ANSWER.getId()][OWNER.getId()]  				= new Permission(READ, UPDATE, DELETE, SHARE);
        permission[PUBLIC.getId()][ANSWER.getId()][LIST.getId()]   				= new Permission(READ, UPDATE, DELETE, SHARE);
        permission[PUBLIC.getId()][ANSWER.getId()][ALL.getId()] 	 			= new Permission(READ, UPDATE, DELETE, SHARE);
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
			return permission[policy.getId()][document.getId()][accessor.getId()];
		return null;
	}
}