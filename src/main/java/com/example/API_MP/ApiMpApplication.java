package com.example.API_MP;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiMpApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiMpApplication.class, args);
	}

}
