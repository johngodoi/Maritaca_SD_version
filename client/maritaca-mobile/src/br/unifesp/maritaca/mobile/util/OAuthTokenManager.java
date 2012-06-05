package br.unifesp.maritaca.mobile.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import net.smartam.leeloo.client.response.OAuthJSONAccessTokenResponse;

import android.content.Context;
import br.unifesp.maritaca.mobile.activities.HomeActivity;

public class OAuthTokenManager {
	
	private static int    expiresIn;
	private static String token;
	private static String refreshToken;
	private static String user;
	
	private final static String TOKEN_FILE_NAME="token.dat";	
	private static final String USER_PARAM = "user";

	
	public static void loadTokenFile(){
		try {
			FileInputStream fis = HomeActivity.getContext().openFileInput(TOKEN_FILE_NAME);			
			DataInputStream in  = new DataInputStream(fis);
			BufferedReader  br  = new BufferedReader(new InputStreamReader(in));
			
			parseTokenFile(br);
									
			br.close();
			in.close();
			fis.close();			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void parseTokenFile(BufferedReader br) {
		try {
			String user         = br.readLine();		
			String token        = br.readLine();		
			String refreshToken = br.readLine();
			int    expiresIn    = Integer.parseInt(br.readLine());
		
			OAuthTokenManager.setUser(user);
			OAuthTokenManager.setToken(token);
			OAuthTokenManager.setRefreshToken(refreshToken);
			OAuthTokenManager.setExpiresIn(expiresIn);			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void setUser(String user) {
		OAuthTokenManager.user = user;		
	}
	
	public static String getUser(){
		return OAuthTokenManager.user;
	}

	private static void writeTokenFile(String user, String token, String refreshToken, int expiresIn){
		try {
			FileOutputStream fos;
			fos = HomeActivity.getContext().openFileOutput(TOKEN_FILE_NAME, Context.MODE_PRIVATE);
			
			PrintWriter pr = new PrintWriter(fos);
			pr.println(user);
			pr.println(token);
			pr.println(refreshToken);
			pr.println(expiresIn);
			pr.close();
					
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean tokenExists(){
		try {
			FileInputStream fis = HomeActivity.getContext().openFileInput(TOKEN_FILE_NAME);
			fis.close();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void deleteToken(){
		HomeActivity.getContext().deleteFile(TOKEN_FILE_NAME);
	}

	public static int getExpiresIn() {
		return expiresIn;
	}

	public static void setExpiresIn(int expiresIn) {
		OAuthTokenManager.expiresIn = expiresIn;
	}

	public static String getToken() {
		return token;
	}

	public static void setToken(String token) {
		OAuthTokenManager.token = token;
	}

	public static String getRefreshToken() {
		return refreshToken;
	}

	public static void setRefreshToken(String refreshToken) {
		OAuthTokenManager.refreshToken = refreshToken;
	}
	
	public static void saveToken(OAuthJSONAccessTokenResponse oAuthResponse) {		
		String token        = oAuthResponse.getAccessToken();
		String refreshToken = oAuthResponse.getRefreshToken();
		int    expiresIn    = Integer.parseInt(oAuthResponse.getExpiresIn());
		String user         = oAuthResponse.getParam(USER_PARAM);
		
	    OAuthTokenManager.writeTokenFile(user,token,refreshToken,expiresIn);
		
		OAuthTokenManager.setUser(user);
		OAuthTokenManager.setToken(token);
		OAuthTokenManager.setRefreshToken(refreshToken);
		OAuthTokenManager.setExpiresIn(expiresIn);			
	}
}
