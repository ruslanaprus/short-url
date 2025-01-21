package org.goit.urlshortener.common;

import org.goit.urlshortener.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class UrlShortenerApplicationTests {

	@Test
	void contextLoads() {
	}

}
