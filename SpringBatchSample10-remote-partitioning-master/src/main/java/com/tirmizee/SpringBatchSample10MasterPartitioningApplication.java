package com.tirmizee;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableBatchIntegration
@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchSample10MasterPartitioningApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchSample10MasterPartitioningApplication.class, args);
	}

}
