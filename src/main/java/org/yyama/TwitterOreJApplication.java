package org.yyama;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.yyama.twittorej.domein.BearerToken;

@SpringBootApplication
public class TwitterOreJApplication {

	@Value("${org.yyama.twiiterorej.api.key}")
	private String API_KEY;

	@Value("${org.yyama.twiiterorej.api.seacret}")
	private String API_SEACRET_KEY;

	public static void main(String[] args) {
		SpringApplication.run(TwitterOreJApplication.class, args);
	}

	@Bean
	public BearerToken bearerToken() throws IOException {
		return new BearerToken(API_KEY, API_SEACRET_KEY);
	}
}
