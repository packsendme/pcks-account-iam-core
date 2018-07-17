package com.packsendme.microservice.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MicroserviceIAMServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceIAMServerApplication.class, args);
	}
}
