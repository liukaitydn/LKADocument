package com.lk.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.lk.api.annotation.LKADocument;

@SpringBootApplication
@LKADocument(basePackages="com")
public class LKADemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(LKADemoApplication.class, args);
	}
}
