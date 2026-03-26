package com.example.spring_ecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringEcomLandingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringEcomLandingApplication.class, args);
	}

}
