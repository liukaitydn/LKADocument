package com.lk.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.lk.api.annotation.LKADocument;

@SpringBootApplication
@LKADocument(basePackages="com",serverNames="127.0.0.1:8080,192.168.0.77:9010")
public class LKADemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(LKADemoApplication.class, args);
	}
}
