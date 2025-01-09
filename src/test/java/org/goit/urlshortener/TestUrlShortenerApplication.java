package org.goit.urlshortener;

import org.springframework.boot.SpringApplication;

public class TestUrlShortenerApplication {

	public static void main(String[] args) {
		SpringApplication.from(UrlShortenerApp::main).with(TestcontainersConfiguration.class).run(args);
	}

}
