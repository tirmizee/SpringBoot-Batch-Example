package com.tirmizee.configuration;

import java.util.HashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.RowMapper;

import com.tirmizee.batch.domain.ColumnRangePartitioner;
import com.tirmizee.model.Account;

@Configuration
public class JobConfiguration {

	private static final Logger log = LoggerFactory.getLogger(JobConfiguration.class);
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean(name = "accountRowMapper")
	public RowMapper<Account> rowMapper() {
		return (rs, rowNum) -> {
			Account account = new Account();
			account.setUserId(rs.getInt("user_id"));
			account.setUsername(rs.getString("username"));
			account.setPassword(rs.getString("password"));
			account.setEmail(rs.getString("email"));
			account.setCreateOn(rs.getTimestamp("created_on"));
			log.info(String.format("account thread %s reader %s ", Thread.currentThread().getName(), account));
			return account;
		};
	}
	
	@Bean
	public ColumnRangePartitioner partitioner() {
		ColumnRangePartitioner partitioner = new ColumnRangePartitioner();
		partitioner.setColumn("USER_ID");
		partitioner.setTable("ACCOUNTS");
		partitioner.setDataSource(this.dataSource);
		return partitioner;
	}
	
	@Bean
	@StepScope
	JdbcPagingItemReader<Account> pagingItemReader(
			@Value("#{stepExecutionContext['column']}") String column,
			@Value("#{stepExecutionContext['minValue']}") Long minValue,
			@Value("#{stepExecutionContext['maxValue']}") Long maxValue) {
		
		System.out.println("reading " + minValue + " to " + maxValue);
		
		HashMap<String, Order> sortKeys = new HashMap<>(2);
		sortKeys.put("USER_ID", Order.ASCENDING);

		PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
		queryProvider.setSelectClause("*");
		queryProvider.setFromClause("ACCOUNTS");
		queryProvider.setWhereClause(column + " >= " + minValue + " AND " + column + " < " + maxValue);
		queryProvider.setSortKeys(sortKeys);
		
		JdbcPagingItemReader<Account> pagingItemReader = new JdbcPagingItemReader<>();
//		pagingItemReader.setFetchSize(5);
		pagingItemReader.setDataSource(this.dataSource);
		pagingItemReader.setRowMapper(this.rowMapper());
		pagingItemReader.setQueryProvider(queryProvider);
		
		return pagingItemReader;
	}
	
	@Bean
	public ItemWriter<Account> accountWriter() {
		return items -> {
			items.forEach(item -> log.info(String.format("account thread %s writer item %s", Thread.currentThread().getName(), item)));
		};
	}
	
	@Bean
	public Step masterStep() {
		return this.stepBuilderFactory.get("masterStep")
				.partitioner(slaveStep().getName(), partitioner())
				.gridSize(3)
				.step(slaveStep())
				.taskExecutor(new SimpleAsyncTaskExecutor())
				.build();
	}
	
	@Bean
	public Step slaveStep() {
		return this.stepBuilderFactory.get("slaveStep")
				.<Account, Account>chunk(10)
				.reader(pagingItemReader(null, null, null))
				.writer(accountWriter())
				.build();
	}
	
	@Bean
	public Job partitionLocalJob() {
		return this.jobBuilderFactory.get("partitionLocalIncrementerJob")
				.incrementer(new RunIdIncrementer())
				.start(masterStep())
				.build();
	}
	
}
