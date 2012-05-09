package br.unifesp.maritaca.business.oauth;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.base.AbstractEJB;
import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.business.util.UtilsBusiness;
import br.unifesp.maritaca.core.OAuthClient;
import br.unifesp.maritaca.core.OAuthCode;
import br.unifesp.maritaca.core.OAuthToken;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.business.account.edit.dto.UserDTO;

@Stateless
public class OAuthEJB extends AbstractEJB {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private OAuthDAO oauthDAO;
	
	@Inject
	private UserDAO userDAO;
	
	public OAuthClientDTO findOAuthClientByClientId(String clientId) {
		OAuthClient oauthClient = oauthDAO.findAuthClientByClientId(clientId);  
		if(oauthClient != null)
			return UtilsBusiness.convertToClass(oauthClient, OAuthClientDTO.class);
		
		return null;
	}

	public void saveAuthorizationCode(OAuthCodeDTO oauthCodeDTO) {
		OAuthCode oauthCode = UtilsBusiness.convertToClass(oauthCodeDTO, OAuthCode.class);
		
		oauthDAO.persisteOAuthCode(oauthCode);
	}

	public DataAccessTokenDTO findOAuthCodeAndClient(String code, String clientId) {
		DataAccessTokenDTO dataDTO = new DataAccessTokenDTO();
		
		OAuthCode authCode = oauthDAO.findOAuthCodeByCode(code);

		if(authCode != null)
			dataDTO.setOauthCodeDTO(UtilsBusiness.convertToClass(authCode, OAuthCodeDTO.class));
		else 
			dataDTO.setOauthCodeDTO(null);
		
		dataDTO.setOauthClientDTO(this.findOAuthClientByClientId(clientId));
		
		return dataDTO;
	}
	
	public void saveOAuthToken(OAuthTokenDTO tokenDTO) {
		OAuthToken oauthToken = UtilsBusiness.convertToClass(tokenDTO, OAuthToken.class);
		
		oauthDAO.persisteOAuthToken(oauthToken);
	}

	public OAuthTokenDTO findOAuthTokenByToken(String accessToken) {
		OAuthToken oauthToken = oauthDAO.findOAuthTokenByToken(accessToken);
		if(oauthToken != null)
			return UtilsBusiness.convertToClass(oauthToken, OAuthTokenDTO.class);
		
		return null;
	}

	public UserDTO findUserByUserKey(String userKey) {
		User user = userDAO.findUserByKey(UUID.fromString(userKey));
		
		return UtilsBusiness.convertToClass(user, UserDTO.class);
	}
	
}