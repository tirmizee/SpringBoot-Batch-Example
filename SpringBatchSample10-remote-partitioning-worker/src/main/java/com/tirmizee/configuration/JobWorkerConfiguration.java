package com.tirmizee.configuration;

import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jdbc.core.RowMapper;

import com.tirmizee.batch.model.Account;

@Configuration
public class JobWorkerConfiguration {
	
	private static final Logger log = LoggerFactory.getLogger(JobWorkerConfiguration.class);

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;
	
	/*
     * Configure inbound flow (requests coming from the master)
     */
    @Bean
    public DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("requests"))
                .channel(requests())
                .get();
    }
    
    /*
     * Configure outbound flow (replies going to the master)
     */
    @Bean
    public DirectChannel replies() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(replies())
                .handle(Jms.outboundAdapter(connectionFactory).destination("replies"))
                .get();
    }

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
    
    /*
     * Configure the worker step
     */
    @Bean
    public Step workerStep() {
        return this.workerStepBuilderFactory.get("workerStep")
                .inputChannel(requests())
                .outputChannel(replies())
                .<Account, Account>chunk(4)
                .reader(this.pagingItemReader(null, null, null))
                .writer(this.accountWriter())
                .build();
    }
	
}
