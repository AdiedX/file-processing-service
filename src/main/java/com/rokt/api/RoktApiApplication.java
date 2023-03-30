package com.rokt.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.rokt.api.service", "com.rokt.api.controller"})
public class RoktApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(RoktApiApplication.class, args);
	}
}
