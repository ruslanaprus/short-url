package org.goit.urlshortener;

import org.springframework.boot.SpringApplication;

public class TestUrlShortenerApplication {

	public static void main(String[] args) {
		SpringApplication.from(UrlShortener::main).with(TestcontainersConfiguration.class).run(args);
	}

}
