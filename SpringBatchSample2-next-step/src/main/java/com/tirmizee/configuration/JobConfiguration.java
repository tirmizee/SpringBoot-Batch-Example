package com.tirmizee.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfiguration {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Step firstStep() {
		Tasklet tasklet = (contribution, chunkContext) -> {
			System.out.println("firstStep");
			return RepeatStatus.FINISHED;
		};
		return stepBuilderFactory.get("firstStep").tasklet(tasklet).build();
	}
	
	@Bean
	public Step secondStep() {
		Tasklet tasklet = (contribution, chunkContext) -> {
			System.out.println("secondStep");
			return RepeatStatus.FINISHED;
		};
		return stepBuilderFactory.get("secondStep").tasklet(tasklet).build();
	}
	
	@Bean
	public Step thirdStep() {
		Tasklet tasklet = (contribution, chunkContext) -> {
			System.out.println("thirdStep");
			return RepeatStatus.FINISHED;
		};
		return stepBuilderFactory.get("thirdStep").tasklet(tasklet).build();
	}
	
	@Bean
	public Job jobNext() {
		return jobBuilderFactory.get("jobNExt")
				.start(firstStep())
				.next(secondStep())
				.next(thirdStep())
				.build();
	}
	
}
