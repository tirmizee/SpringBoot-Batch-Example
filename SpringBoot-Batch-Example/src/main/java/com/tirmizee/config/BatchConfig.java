package com.tirmizee.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import com.tirmizee.batch.model.Customer;
import com.tirmizee.batch.process.CustomerProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfig extends DefaultBatchConfigurer {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private CustomerProcessor customerProcessor;
	
	@Bean
	public RunIdIncrementer runIdIncrementer() {
		return new RunIdIncrementer();
	}
	
	@Bean
    public Job masterJob(RunIdIncrementer runIdIncrementer, Step masterStep) {
        return jobBuilderFactory.get("masterJob")
            .incrementer(runIdIncrementer)
            .flow(masterStep)
            .end()
            .build();
    }
 
	@Bean
	public Step masterStep() {
		return stepBuilderFactory.get("masterStep")
			.<Customer, String>chunk(1)
			.reader(customerReader())
			.processor(customerProcessor)
			.writer(customerWriter())
			.build();
	}
	
	@Bean
	public FlatFileItemReader<Customer> customerReader() {
	    return new FlatFileItemReaderBuilder<Customer>()
	        .name("personItemReader")
	        .resource(new ClassPathResource("source.txt"))
	        .delimited().delimiter(",")
	        .names(new String[]{"firstName", "lastName"})
	        .fieldSetMapper(new BeanWrapperFieldSetMapper<Customer>() {{
	            setTargetType(Customer.class);
	        }})
	        .build();
	}
	
	@Bean
	public FlatFileItemWriter<String> customerWriter() {
	    return new FlatFileItemWriterBuilder<String>()
	        .name("customerWriter")
	        .resource(new FileSystemResource("target/test-outputs/customer.txt"))
	        .lineAggregator(new PassThroughLineAggregator<>())
	        .build();
	}

}
