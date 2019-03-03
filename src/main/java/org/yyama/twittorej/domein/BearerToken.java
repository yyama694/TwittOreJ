package org.yyama.twittorej.domein;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * BearerTokenクラス
 * 
 * @author yyama
 *
 */
public class BearerToken {

	private final String token;

	/**
	 * デフォルトコンストラクタ。ベアラトークンを取得する。
	 * 
	 * @throws IOException
	 */
	public BearerToken(String key, String seacret) throws IOException {
		token = getBearerToken(key, seacret);
	}

	private String getBearerToken(String key, String seacret) throws IOException {
		final String encodedKey = URLEncoder.encode(key, "UTF-8");
		final String encodedSeacretKey = URLEncoder.encode(seacret, "UTF-8");
		final String encodedString = Base64.getEncoder()
				.encodeToString((encodedKey + ":" + encodedSeacretKey).getBytes());
		URL urlObj = new URL("https://api.twitter.com/oauth2/token");
		HttpURLConnection http = (HttpURLConnection) urlObj.openConnection();
		http.setDoOutput(true);
		http.setRequestMethod("POST");
		http.setRequestProperty("Authorization", "Basic " + encodedString);
		http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		http.setUseCaches(false);
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()));
		wr.write("grant_type=client_credentials");
		wr.flush();
		wr.close();

		// ボディ(コンテンツ)の取得
		InputStream is = http.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sbBody = new StringBuilder();
		String s;
		while ((s = reader.readLine()) != null) {
			sbBody.append(s);
			sbBody.append("\n");
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(sbBody.toString()).path("access_token").textValue();
	}

	/**
	 * ベアラトークンを返す。
	 * 
	 * @return bearer token
	 */
	public String token() {
		return token;
	}

}
