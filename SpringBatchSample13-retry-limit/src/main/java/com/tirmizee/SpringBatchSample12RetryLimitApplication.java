package com.tirmizee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBatchSample12RetryLimitApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchSample12RetryLimitApplication.class, args);
	}

//	@Bean
//	protected Step step() {
//		return steps.get("step").<Trade, Object> chunk(1).reader(reader()).writer(writer()).faultTolerant()
//				.retry(Exception.class).retryLimit(3).build();
//	}
	
}
