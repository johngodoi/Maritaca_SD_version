package br.unifesp.maritaca.business.oauth;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.base.AbstractEJB;
import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.business.oauth.dto.DataAccessTokenDTO;
import br.unifesp.maritaca.business.oauth.dto.OAuthClientDTO;
import br.unifesp.maritaca.business.oauth.dto.OAuthCodeDTO;
import br.unifesp.maritaca.business.oauth.dto.OAuthTokenDTO;
import br.unifesp.maritaca.business.util.UtilsBusiness;
import br.unifesp.maritaca.persistence.entity.OAuthClient;
import br.unifesp.maritaca.persistence.entity.OAuthCode;
import br.unifesp.maritaca.persistence.entity.OAuthToken;
import br.unifesp.maritaca.persistence.entity.User;
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
			return UtilsBusiness.reflectClasses(oauthClient, OAuthClientDTO.class);
		
		return null;
	}

	public void saveAuthorizationCode(OAuthCodeDTO oauthCodeDTO) {
		OAuthCode oauthCode = UtilsBusiness.reflectClasses(oauthCodeDTO, OAuthCode.class);
		
		oauthDAO.persisteOAuthCode(oauthCode);
	}

	public DataAccessTokenDTO findOAuthCodeAndClient(String code, String clientId) {
		DataAccessTokenDTO dataDTO = new DataAccessTokenDTO();
		
		OAuthCode authCode = oauthDAO.findOAuthCodeByCode(code);

		if(authCode != null)
			dataDTO.setOauthCodeDTO(UtilsBusiness.reflectClasses(authCode, OAuthCodeDTO.class));
		else 
			dataDTO.setOauthCodeDTO(null);
		
		dataDTO.setOauthClientDTO(this.findOAuthClientByClientId(clientId));
		
		return dataDTO;
	}
	
	public void saveOAuthToken(OAuthTokenDTO tokenDTO) {
		OAuthToken oauthToken = UtilsBusiness.reflectClasses(tokenDTO, OAuthToken.class);
		
		oauthDAO.persisteOAuthToken(oauthToken);
	}

	public OAuthTokenDTO findOAuthTokenByToken(String accessToken) {
		OAuthToken oauthToken = oauthDAO.findOAuthTokenByToken(accessToken);
		if(oauthToken != null)
			return UtilsBusiness.reflectClasses(oauthToken, OAuthTokenDTO.class);
		
		return null;
	}

	public UserDTO findUserByUserKey(String userKey) {
		User user = userDAO.findUserByKey(UUID.fromString(userKey));
		
		return UtilsBusiness.reflectClasses(user, UserDTO.class);
	}
	
}
