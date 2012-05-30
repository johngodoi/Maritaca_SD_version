package br.unifesp.maritaca.business.oauth;

public class DataAccessTokenDTO {

	private OAuthCodeDTO oauthCodeDTO;
	
	private OAuthClientDTO oauthClientDTO;

	public OAuthCodeDTO getOauthCodeDTO() {
		return oauthCodeDTO;
	}

	public void setOauthCodeDTO(OAuthCodeDTO oauthCodeDTO) {
		this.oauthCodeDTO = oauthCodeDTO;
	}

	public OAuthClientDTO getOauthClientDTO() {
		return oauthClientDTO;
	}

	public void setOauthClientDTO(OAuthClientDTO oauthClientDTO) {
		this.oauthClientDTO = oauthClientDTO;
	}
	
}
