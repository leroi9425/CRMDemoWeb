package com.crm.BackendCrm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BackendCrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendCrmApplication.class, args);
	}

}
