package br.unifesp.maritaca.mobile.activities;

import static br.unifesp.maritaca.mobile.util.Constants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import net.smartam.leeloo.client.OAuthClient;
import net.smartam.leeloo.client.URLConnectionClient;
import net.smartam.leeloo.client.request.OAuthClientRequest;
import net.smartam.leeloo.client.response.OAuthJSONAccessTokenResponse;
import net.smartam.leeloo.common.message.types.GrantType;
import net.smartam.leeloo.common.message.types.ResponseType;
import android.os.Handler;
import android.util.Log;

public class OAuthPortListener implements Runnable {
	
	private String        oAuthToken;	
	private int           tokenExpires;
	private Handler       handler;
	
	public OAuthPortListener(Handler handler) {
		setHandler(handler);
	}

	public void run() {
		try{
			Log.d(OAuthPortListener.class.getName(), "Opening server port: " + SERVER_PORT);			
			ServerSocket serversocket = new ServerSocket(SERVER_PORT);
			
			if (serversocket != null) {
				Socket connectionSocket = serversocket.accept();
				Log.d(OAuthPortListener.class.getName(), "Received connection on server port: " + SERVER_PORT);
				if (connectionSocket != null) {
			
					BufferedReader    inFromClient;
					InputStreamReader inputStreamReader;
					String            clientSentence; 
					
					inputStreamReader = new InputStreamReader(connectionSocket.getInputStream());
					inFromClient      = new BufferedReader(inputStreamReader);
					clientSentence    = inFromClient.readLine();
					
					if (clientSentence != null) {
						Log.d(OAuthPortListener.class.getName(),"Received in server socket: "+clientSentence);
						clientSentence = clientSentence.split(" ")[1];
						clientSentence = clientSentence.substring(2);
						String code = clientSentence.split("=")[1];
						retrieveAccessToken(code);
					}
				}
			}
		}catch(IOException e){
			throw new RuntimeException(e);
		}		
	}

	private void retrieveAccessToken(String code) {
		try {
			OAuthClientRequest request = OAuthClientRequest
					.tokenLocation(SERVER_URL + ACCESS_TOKEN_REQUEST)
					.setGrantType(GrantType.AUTHORIZATION_CODE)
					.setClientId(MARITACA_MOBILE)
					.setClientSecret(MARITACA_SECRET)
					.setRedirectURI(CLIENT_URL).setCode(code)
					.setParameter(RESPONSE_TYPE, ResponseType.TOKEN.toString())
					.buildBodyMessage();

			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			
			Log.d(OAuthPortListener.class.getName(),
					"Retrieving access token from: "+request.getLocationUri());
			OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient
					.accessToken(request, OAuthJSONAccessTokenResponse.class);

			String TOKEN   = oAuthResponse.getAccessToken();
			String EXPIRES = oAuthResponse.getExpiresIn();
			
			Log.i(OAuthPortListener.class.getName(), "Received token: " + TOKEN
					+ ", expires in: " + EXPIRES + " seconds.");
						
			setOAuthToken(TOKEN);
			setTokenExpires(Integer.parseInt(EXPIRES));
			
			getHandler().sendEmptyMessage(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int getTokenExpires() {
		return tokenExpires;
	}

	public void setTokenExpires(int tokenExpires) {
		this.tokenExpires = tokenExpires;
	}

	public String getOAuthToken() {
		return oAuthToken;
	}

	public void setOAuthToken(String oAuthToken) {
		this.oAuthToken = oAuthToken;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
