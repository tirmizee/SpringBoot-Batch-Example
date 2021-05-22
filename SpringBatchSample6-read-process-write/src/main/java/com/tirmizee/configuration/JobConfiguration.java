package com.tirmizee.configuration;

import java.util.Arrays;
import java.util.Iterator;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tirmizee.job.step.invoice.InvoiceItemReader;
import com.tirmizee.job.step.invoice.InvoiceItemWriter;
import com.tirmizee.job.step.register.RegisterItemProcessor;
import com.tirmizee.job.step.register.RegisterItemReader;
import com.tirmizee.job.step.register.RegisterItemWriter;

@Configuration
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Step invoiceStep() {
		Iterator<String> invoices = Arrays.asList(
				"01AAAAA", "02AAAAA",
				"03AAAAA", "04AAAAA",
				"05AAAAA").iterator();
		return stepBuilderFactory.get("invoiceStep")
				.<String, String>chunk(2)
				.reader(new InvoiceItemReader(invoices))
				.writer(new InvoiceItemWriter())
				.build();
	}
	
	@Bean
	public Step registerStep() {
		Iterator<String> registers = Arrays.asList(
				"11111", "22222",
				"33333").iterator();
		return stepBuilderFactory.get("registerStep")
				.<String, Integer>chunk(2)
				.reader(new RegisterItemReader(registers))
				.processor(new RegisterItemProcessor())
				.writer(new RegisterItemWriter())
				.build();
	}
	
	@Bean
	public Job invoiceJob(
			@Qualifier("invoiceStep") Step invoiceStep,
			@Qualifier("registerStep") Step registerStep) {
		return jobBuilderFactory.get("invoiceJob")
				.start(invoiceStep)
				.next(registerStep)
				.build();
	}
	
}
