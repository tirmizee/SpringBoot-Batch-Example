package com.tirmizee.configuration;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import com.tirmizee.model.Account;

@Configuration
public class JobConfiguration {
	
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
			System.out.println(String.format("account reader %s ", account));
			return account;
		};
	}
	
	@StepScope
	@Bean(name = "accountReader")
	public JdbcCursorItemReader<Account> accountReader() {
		JdbcCursorItemReader<Account> jdbcCursorItemReader = new JdbcCursorItemReader<>();
		jdbcCursorItemReader.setSql("SELECT * FROM ACCOUNTS");
		jdbcCursorItemReader.setDataSource(dataSource);
		jdbcCursorItemReader.setConnectionAutoCommit(false);
		jdbcCursorItemReader.setRowMapper(rowMapper());
//		jdbcCursorItemReader.setRowMapper(BeanPropertyRowMapper.newInstance(Account.class));
		return jdbcCursorItemReader;
	}
	
	@Bean(name = "accountWriter")
	public ItemWriter<Account> accountWriter() {
		return items -> {
			items.forEach(item -> System.out.println(String.format("account writer item %s", item)));
		};
	}
	
	@Bean(name = "accountStep")
	public Step accountStep(
			@Qualifier("accountWriter") ItemWriter<Account> accountWriter,
			@Qualifier("accountReader") JdbcCursorItemReader<Account> accountReader) {
		return stepBuilderFactory.get("accountStep")
				.<Account, Account>chunk(5)
				.reader(accountReader)
				.writer(accountWriter)
				.build();
	}
	
	@Bean(name = "accountJob")
	public Job accountJob(@Qualifier("accountStep") Step accountStep) {
		return jobBuilderFactory.get("accountJob")
				.start(accountStep)
				.build();
	}

}
