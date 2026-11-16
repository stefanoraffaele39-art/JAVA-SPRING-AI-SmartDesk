package com.stefano.smartdesk;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SmartdeskApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
