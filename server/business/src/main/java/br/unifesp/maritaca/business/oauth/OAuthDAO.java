package br.unifesp.maritaca.business.oauth;

import java.util.List;

import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.persistence.entity.OAuthClient;
import br.unifesp.maritaca.persistence.entity.OAuthCode;
import br.unifesp.maritaca.persistence.entity.OAuthToken;

public class OAuthDAO extends BaseDAO {

	public OAuthClient findAuthClientByClientId(String clientId) {
		List<OAuthClient> listClients = entityManager.
				cQuery(OAuthClient.class, "clientId", clientId, true);
		for (OAuthClient client : listClients) {
			return client;// always returns the first
		}
		return null;
	}

	public void persisteOAuthCode(OAuthCode oauthCode) {
		entityManager.persist(oauthCode);
	}

	public OAuthCode findOAuthCodeByCode(String code) {
		List<OAuthCode> listCodes = entityManager.cQuery(OAuthCode.class,
				"code", code);
		for(OAuthCode oauthCode: listCodes){
			return oauthCode; //return first
		}
		return null;
	}

	public void persistOAuthToken(OAuthToken oauthToken) {
		entityManager.persist(oauthToken);		
	}

	public OAuthToken findOAuthTokenByToken(String accessToken) {
		List<OAuthToken> listTokens = entityManager.cQuery(OAuthToken.class,
				"accessToken", accessToken);
		for (OAuthToken token : listTokens) {
			return token;// always returns the first
		}
		return null;
	}
}

