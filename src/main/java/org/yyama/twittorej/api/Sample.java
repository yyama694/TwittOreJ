package org.yyama.twittorej.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yyama.twittorej.domein.BearerToken;

@Controller
public class Sample {

	@Autowired
	BearerToken bearerToken;

	@RequestMapping("/")
	@ResponseBody
	public String getSample() throws IOException {
		String token = bearerToken.token();
		return getTweets(token);
	}

	private String getTweets(String bearerToken) throws IOException {
		// URL urlObj = new
		// URL("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=yyama694&count=5");
		URL urlObj = new URL("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=@yyama694&count=5");
		HttpURLConnection http = (HttpURLConnection) urlObj.openConnection();
		http.setRequestMethod("GET");

		http.setRequestProperty("Authorization", "Bearer " + bearerToken);
		StringBuilder sbBody = new StringBuilder();
		String s;
		InputStream is = http.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		while ((s = reader.readLine()) != null) {
			sbBody.append(s);
			sbBody.append("\n");
		}
		return sbBody.toString();
	}

}
