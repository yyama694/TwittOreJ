package org.yyama.twittorej.api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Controller
@RequestMapping("/http")
public class HttpAPI {

	@ResponseBody
	@RequestMapping("/getog")
	public ResponseEntity<String> getOg(@RequestParam("url") String url) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type:", "application/json");
		headers.add("Access-Control-Allow-Origin", "*");
//		headers.add("User-Agent", "facebookexternalhit/1.1 (+http://www.facebook.com/externalhit_uatext.php)");
		HttpStatus status;
		String resultJson = "";
		try {
			HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
			http.setRequestMethod("GET");
//			http.setRequestProperty("User-Agent","facebookexternalhit/1.1 (+http://www.facebook.com/externalhit_uatext.php)");
			InputStream is = http.getInputStream();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int availableSize = is.available();
			byte[] bytes = new byte[availableSize + 1];
			while (true) {
				int size = is.read(bytes);
				if (-1 == size) {
					break;
				}
				byteArrayOutputStream.write(bytes, 0, size);
			}

			// 文字コードがわからないので一旦、UTF-8でパースする
			String body = new String(byteArrayOutputStream.toByteArray());
			Document doc = Jsoup.parse(body);
//			System.out.println(doc.select("meta"));
			// 文字コードを取得する。
			Elements charSetElement = doc.select("meta[http-equiv=\"content-type\"]");
			String charSet = "";
			if (charSetElement.size() > 0) {
				String content = charSetElement.attr("content");
				charSet = content.substring(content.indexOf("charset=") + 8);
			} else if (charSetElement.size() == 0) {
				charSetElement = doc.select("meta[charset]");
				charSet = charSetElement.attr("charset");
			}
//			System.out.println("charset:" + charSet);

			// 文字コードが sjis だったらパースし直し
			if (charSet.equals("shift_jis")) {
				body = new String(byteArrayOutputStream.toByteArray(), "SJIS");
				doc = Jsoup.parse(body);
			}

			Elements elements = doc.select("meta[property^=og:]");
			Map<String, String> ogs = new HashMap<>();
			ogs = elements.stream()
					.collect(Collectors.toMap(e -> e.attr("property"), e -> e.attr("content"), (e1, e2) -> e1));
			ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
			resultJson = mapper.writeValueAsString(ogs);
			System.out.println(resultJson);
			status = HttpStatus.OK;
		} catch (Exception e) {
			System.err.println(e);
			status = HttpStatus.BAD_REQUEST;
		}
//		System.out.println(status);
		return new ResponseEntity<>(resultJson, headers, status);
	}

	public static void main(String[] args) {
//		new HttpAPI().getOg("https://www.itmedia.co.jp/news/articles/1908/19/news087.html");
		new HttpAPI().getOg("http://radiko.jp/#!/live/TBS");
		new HttpAPI().getOg("https://www.youtube.com/watch?v=1g2h4Yrlg5g&feature=youtu.be");
	}

}
