package org.yyama.twittorej.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Controller
@RequestMapping("/http")
public class HttpAPI {

	@ResponseBody
	@RequestMapping("/getOg")
	public ResponseEntity<String> getOg(@PathVariable("url") String url) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type:", "application/json");
		headers.add("Access-Control-Allow-Origin", "*");
		HttpStatus status;
		String resultJson = "";
		try {
			HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
			http.setRequestMethod("GET");
			InputStream is = null;
			is = http.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String s;
			StringBuilder sbBody = new StringBuilder();
			while ((s = reader.readLine()) != null) {
				sbBody.append(s);
				sbBody.append("\n");
			}

			Document doc = Jsoup.parse(sbBody.toString());
			Elements elements = doc.select("meta[property^=og:]");
			Map<String, String> ogs = new HashMap<>();
			ogs = elements.stream().collect(Collectors.toMap(e -> e.attr("property"), e -> e.attr("content")));
			ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
			resultJson = mapper.writeValueAsString(ogs);
			System.out.println(resultJson);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(resultJson, headers, status);
	}

	public static void main(String[] args) {
		new HttpAPI().getOg("http://radiko.jp/#!/live/TBS");
	}

}
