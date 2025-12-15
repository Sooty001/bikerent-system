package com.example.bikerentrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication(
		scanBasePackages = {"com.example.bikerentrest", "com.example.bikerentapi"}
)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class BikerentRestApplication {
	public static void main(String[] args) {
		SpringApplication.run(BikerentRestApplication.class, args);
	}
}
