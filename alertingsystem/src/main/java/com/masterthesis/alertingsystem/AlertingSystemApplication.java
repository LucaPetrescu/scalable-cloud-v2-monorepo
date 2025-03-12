package com.masterthesis.alertingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlertingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlertingSystemApplication.class, args);
	}

}
