package com.tirmizee.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor("spring_batch");
	}
	
	@Bean
	public Step calculateStep1() {
		Tasklet tasklet = (contribution, chunkContext) -> {
			String stepName = chunkContext.getStepContext().getStepName();
			String threadName = Thread.currentThread().getName();
			System.out.println(String.format("%s has been executed on thread %s", stepName, threadName));
			return RepeatStatus.FINISHED;
		};
		return stepBuilderFactory.get("calculateStep1").tasklet(tasklet).build();
	}
	
	@Bean
	public Step calculateStep2() {
		Tasklet tasklet = (contribution, chunkContext) -> {
			String stepName = chunkContext.getStepContext().getStepName();
			String threadName = Thread.currentThread().getName();
			System.out.println(String.format("%s has been executed on thread %s", stepName, threadName));
			return RepeatStatus.FINISHED;
		};
		return stepBuilderFactory.get("calculateStep2").tasklet(tasklet).build();
	}
	
	@Bean
	public Step calculateStep3() {
		Tasklet tasklet = (contribution, chunkContext) -> {
			String stepName = chunkContext.getStepContext().getStepName();
			String threadName = Thread.currentThread().getName();
			System.out.println(String.format("%s has been executed on thread %s", stepName, threadName));
			return RepeatStatus.FINISHED;
		};
		return stepBuilderFactory.get("calculateStep3").tasklet(tasklet).build();
	}
	
	@Bean
	public Flow calculateflow1() {
		return new FlowBuilder<Flow>("calculateflow")
				.start(calculateStep1())
				.next(calculateStep2())
				.build();
	}
	
	@Bean
	public Flow calculateflow2() {
		return new FlowBuilder<Flow>("calculateflow")
				.start(calculateStep1())
				.next(calculateStep3())
				.build();
	}
	
	@Bean
	public Job jobNextFlow() {
		return jobBuilderFactory.get("jobNextFlow")
				.start(calculateflow1())
				.next(calculateflow2())
				.end()
				.build();
	}
	
	@Bean
	public Job jobSplitFlow() {
		return jobBuilderFactory.get("jobSplitFlow")
				.start(calculateflow1())
				.split(taskExecutor())
				.add(calculateflow2())
				.end()
				.build();
	}
	
}
