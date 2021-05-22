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
	public Step step() {
		Tasklet tasklet = (contribution, chunkContext) -> {
			System.out.println("step");
			return RepeatStatus.FINISHED;
		};
		return stepBuilderFactory.get("firstStep").tasklet(tasklet).build();
	}
    
    @Bean
	public Job job() {
		return jobBuilderFactory.get("demoJobOne")
				.start(step())
				.build();
	}
    
}
