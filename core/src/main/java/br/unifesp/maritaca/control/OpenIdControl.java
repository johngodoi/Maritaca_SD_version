package br.unifesp.maritaca.control;

import java.util.List;
import java.util.UUID;

/**
 * Interface used to manage the openids of the users. 
 * The relation is many to one: One user can have many openids.
 * A given openid should belong to only one user. This integrity
 * is not maintained by the database and must be ensured by the
 * class implementing this interface.

 * @author tiagobarabasz
 *
 */
public interface OpenIdControl {
	/**
	 * Returns the id of the user associated to the given
	 * openid or null if none is found.
	 * @param openIdUrl
	 * @return
	 */
	UUID searchUserByOpenId(String openIdUrl);	
	
	/**
	 * Returns the openids associated with the given user id;
	 * @param userId
	 * @return
	 */
	List<String> searchUserOpenIds(UUID userId);
	
	/**
	 * Associate the given openid to the user passed as parameter.
	 * @param openIdUrl
	 * @param userId
	 * @return True if successful, false otherwise (in case the
	 * given openid is already associated to another user for example).
	 */
	Boolean attachOpenId(String openIdUrl, UUID userId);
	
	/**
	 * Detaches the given openid from the user passed as parameter.
	 * @param openIdUrl
	 * @param userId
	 * @return True if successful, false otherwise.
	 */
	Boolean detachOpenId(String openIdUrl, UUID userId);
}
