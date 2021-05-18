package com.tirmizee.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JobConfiguration1 {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Step firstStep() {
		
		Tasklet tasklet = new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Hello wrold");
				return RepeatStatus.FINISHED;
			}
		};
		
		Step step = stepBuilderFactory
				.get("firstStep")
				.tasklet(tasklet)
				.build();
		
		return step;
	}
	
	@Bean
	public Job firstJob() {
		return jobBuilderFactory.get("firstJob")
				.start(firstStep())
				.build();
	}
	
}
