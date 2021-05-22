package com.tirmizee.configuration;

import java.util.HashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

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
			log.info(String.format("account reader %s ", account));
			return account;
		};
	}
	
	@Bean(name = "accountReader")
	public JdbcPagingItemReader<Account> accountReader() {
		
		HashMap<String, Order> sortKeys = new HashMap<>(2);
		sortKeys.put("USER_ID", Order.ASCENDING);
		
		PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
		queryProvider.setSelectClause("*");
		queryProvider.setFromClause("ACCOUNTS");
		queryProvider.setWhereClause("USER_ID IS NOT NULL");
		queryProvider.setSortKeys(sortKeys);
		
		JdbcPagingItemReader<Account> pagingItemReader = new JdbcPagingItemReader<>();
		pagingItemReader.setPageSize(5);
		pagingItemReader.setDataSource(this.dataSource);
		pagingItemReader.setRowMapper(this.rowMapper());
		pagingItemReader.setQueryProvider(queryProvider);
		
		return pagingItemReader;
	}
	
	@Bean(name = "accountWriter")
	public ItemWriter<Account> accountWriter() {
		return items -> {
			items.forEach(item -> log.info(String.format("account writer item %s", item)));
		};
	}
	
	@Bean(name = "accountPagingStep")
	public Step accountPagingStep(
			@Qualifier("accountWriter") ItemWriter<Account> accountWriter,
			@Qualifier("accountReader") JdbcPagingItemReader<Account> accountReader) {
		return stepBuilderFactory.get("accountPagingStep")
				.<Account, Account>chunk(5)
				.reader(accountReader)
				.writer(accountWriter)
				.build();
	}
	
	@Bean(name = "accountPagingJob")
	public Job accountPagingJob(@Qualifier("accountPagingStep") Step accountPagingStep) {
		return jobBuilderFactory.get("accountPagingJob")
				.start(accountPagingStep)
				.build();
	}

}
