package com.example.AuthwithFirebase.FirebaseSpringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ConfigurationPropertiesScan
public class FirebaseSpringbootApplication {
	public static void main(String[] args) {
		SpringApplication.run(FirebaseSpringbootApplication.class, args);
	}

}
