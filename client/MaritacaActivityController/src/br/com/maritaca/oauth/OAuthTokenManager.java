package br.com.maritaca.oauth;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

import net.smartam.leeloo.client.response.OAuthJSONAccessTokenResponse;
import android.util.Log;
import br.com.maritaca.activity.MaritacaHomeActivity;

public class OAuthTokenManager {

	private static long expiresIn;
	private static String token;
	private static String refreshToken;
	private static String user;

	private final static String TOKEN_FILE_NAME = "token.dat";
	private static final String USER_PARAM = "user";
	private static File arquivoToken = new File(MaritacaHomeActivity.diretorio,
			TOKEN_FILE_NAME);

	public static void loadTokenFile() {
		try {
			FileInputStream fis = new FileInputStream(arquivoToken);
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

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
			String user = br.readLine();
			String token = br.readLine();
			String refreshToken = br.readLine();
			long expiresIn = Long.parseLong(br.readLine());

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

	public static String getUser() {
		return OAuthTokenManager.user;
	}

	private static void writeTokenFile(String user, String token,
			String refreshToken, long expiresIn) {
		try {
			arquivoToken.createNewFile();
			FileOutputStream fos = new FileOutputStream(arquivoToken);

			// fos = MaritacaHomeActivity.getContext().openFileOutput(
			// TOKEN_FILE_NAME, Context.MODE_PRIVATE);

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

	/**
	 * Verifica se token existe e se é valida
	 * 
	 * @return
	 */
	public static boolean isValid() {
		try {
			FileInputStream fis = new FileInputStream(arquivoToken);
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			parseTokenFile(br);
			Calendar expire = Calendar.getInstance();
			expire.setTime(new Date(getExpiresIn()));
			Log.d("OAUTHMANAGER", "" + getExpiresIn());
			// expirou
			Log.d("OAUTHMANAGER", "expira em " + expire.getTime().toString()
					+ " e agora sao: "
					+ Calendar.getInstance().getTime().toString());
			if (Calendar.getInstance().after(expire)) {
				Log.d("OAUTHMANAGER", "expirou");
				File f = new File(TOKEN_FILE_NAME);
				f.delete();
				return false;
			}
			Log.d("OAUTHMANAGER", "ehvalido");
			fis.close();
			return true;
		} catch (FileNotFoundException e) {
			Log.d("OAUTHMANAGER", "naoexiste");
			return false;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteToken() {
		MaritacaHomeActivity.getContext().deleteFile(TOKEN_FILE_NAME);
	}

	public static long getExpiresIn() {
		return expiresIn;
	}

	public static void setExpiresIn(long expiresIn) {
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
		String token = oAuthResponse.getAccessToken();
		String refreshToken = oAuthResponse.getRefreshToken();
		int expiresIn = Integer.parseInt(oAuthResponse.getExpiresIn());
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, expiresIn);
		long expireTime = calendar.getTime().getTime();
		Log.d("OAUTHMANAGER", "expira em " + calendar.getTime().toString()
				+ " " + expireTime);
		String user = oAuthResponse.getParam(USER_PARAM);

		OAuthTokenManager.writeTokenFile(user, token, refreshToken, expireTime);

		OAuthTokenManager.setUser(user);
		OAuthTokenManager.setToken(token);
		OAuthTokenManager.setRefreshToken(refreshToken);
		OAuthTokenManager.setExpiresIn(expireTime);
	}
}
