package com.g7.ercsystem;

import com.g7.ercsystem.rest.auth.service.DefaultDataService;
import com.g7.ercsystem.rest.auth.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ErcsystemApplication implements CommandLineRunner {

	@Autowired
	private DefaultDataService service;

	public static void main(String[] args) {

		SpringApplication.run(ErcsystemApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		service.addRoles();
	}
}
