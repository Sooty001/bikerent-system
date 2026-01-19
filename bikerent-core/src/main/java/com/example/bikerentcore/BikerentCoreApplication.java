package com.example.bikerentcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
		scanBasePackages = {"com.example.bikerentcore", "com.example.bikerentapi"}
)
@EnableScheduling
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class BikerentCoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(BikerentCoreApplication.class, args);
	}
}
