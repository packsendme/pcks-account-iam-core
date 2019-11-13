package com.packsendme.microservice.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import com.packsendme.microservice.iam.exception.FeignErrorDecoder;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class MicroserviceIAMServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceIAMServerApplication.class, args);
	}
	
	/*
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	} */
	
	 @Bean
	    public FeignErrorDecoder errorDecoder() {
	        return new FeignErrorDecoder();
	    }
	 
}
