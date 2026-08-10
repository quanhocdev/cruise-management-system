package com.project.convenience;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.project")
public class ConvenienceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConvenienceApplication.class, args);
	}

}
