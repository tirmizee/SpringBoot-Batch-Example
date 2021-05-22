package com.tirmizee.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	@StepScope
	public Tasklet task(@Value("#{jobParameters['message']}") String message) {
		return (contribution, chunkContext) -> {
			String stepName = chunkContext.getStepContext().getStepName();
			String threadName = Thread.currentThread().getName();
			System.out.println(String.format("%s has been executed on thread %s with parameters[message=%s]", stepName, threadName, message));
			return RepeatStatus.FINISHED;
		};
	}
	
	@Bean
	public Step step(@Qualifier("task") Tasklet task) {
		return stepBuilderFactory.get("step").tasklet(task).build();
	}
	
	@Bean
	public Job job(@Qualifier("step") Step step) {
		return jobBuilderFactory.get("job").start(step).build();
	}
	
}
