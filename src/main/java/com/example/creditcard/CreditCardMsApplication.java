package com.example.creditcard;

import jakarta.persistence.Entity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//@EnableFeignClients(basePackages = "com.example.creditcard.client")
public class CreditCardMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditCardMsApplication.class, args);
	}

}
