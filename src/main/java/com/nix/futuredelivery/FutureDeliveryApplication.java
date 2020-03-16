package com.nix.futuredelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FutureDeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(FutureDeliveryApplication.class, args);
	}

}
