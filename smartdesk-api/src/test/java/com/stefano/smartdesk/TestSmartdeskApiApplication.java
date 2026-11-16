package com.stefano.smartdesk;

import org.springframework.boot.SpringApplication;

public class TestSmartdeskApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(SmartdeskApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
