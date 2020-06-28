package com.lk.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.lk.api.annotation.LKADocument;

@SpringBootApplication
@LKADocument(basePackages="com.lk.api",projectName="LKADocument测试项目",description="智能、便捷、高效",
serverNames="192.168.0.77:9010,192.168.0.77:8888",version="1.0",enabled=true)
public class LKADemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(LKADemoApplication.class, args);
	}
}
