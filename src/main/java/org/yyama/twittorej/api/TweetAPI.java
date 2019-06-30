package org.yyama.twittorej.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yyama.twittorej.domein.BearerToken;

@Controller
@RequestMapping("/tweet")
public class TweetAPI {

	@Autowired
	BearerToken bearerToken;

	@ResponseBody
	@RequestMapping("/{name}/{num}")
	public ResponseEntity<String> getSample(@PathVariable("name") String name, @PathVariable("num") Integer num,
			@RequestParam(name = "max_id", required = false) String maxId) throws IOException {
		String token = bearerToken.token();
		return getTweets(token, name, num, maxId);
	}

	private ResponseEntity<String> getTweets(String bearerToken, String name, Integer num, String maxId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type:", "application/json");
		headers.add("Access-Control-Allow-Origin", "*");
		HttpStatus status;
		StringBuilder sbBody = new StringBuilder();
		try {
			String u = "https://api.twitter.com/1.1/statuses/user_timeline.json?tweet_mode=extended&screen_name=" + name
					+ "&count=" + num;
			if (maxId != null) {
				u += "&max_id=" + (Long.valueOf(maxId) - 1);
			}
			URL urlObj = new URL(u);
			HttpURLConnection http = (HttpURLConnection) urlObj.openConnection();
			http.setRequestMethod("GET");
			http.setRequestProperty("Authorization", "Bearer " + bearerToken);
			InputStream is = null;
			is = http.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String s;
			while ((s = reader.readLine()) != null) {
				sbBody.append(s);
				sbBody.append("\n");
			}
			status = HttpStatus.OK;
		} catch (IOException e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(sbBody.toString(), headers, status);
	}
}
