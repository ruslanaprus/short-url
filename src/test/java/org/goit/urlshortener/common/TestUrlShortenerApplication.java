package org.goit.urlshortener.common;

import org.goit.urlshortener.TestcontainersConfiguration;
import org.goit.urlshortener.UrlShortener;
import org.springframework.boot.SpringApplication;

public class TestUrlShortenerApplication {

	public static void main(String[] args) {
		SpringApplication.from(UrlShortener::main).with(TestcontainersConfiguration.class).run(args);
	}

}
