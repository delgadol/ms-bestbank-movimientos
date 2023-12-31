package com.bestbank.movimientos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsBestbankMovimientosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsBestbankMovimientosApplication.class, args);
	}

}
