package com.g7.ercsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ErcsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErcsystemApplication.class, args);
	}

}
