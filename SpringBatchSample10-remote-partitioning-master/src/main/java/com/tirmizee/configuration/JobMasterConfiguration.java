package com.tirmizee.configuration;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.scheduling.annotation.Scheduled;

import com.tirmizee.batch.domain.ColumnRangePartitioner;

@Configuration
public class JobMasterConfiguration {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DataSource dataSource;
	
    @Autowired
    private JobLauncher jobLauncher;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
    private RemotePartitioningManagerStepBuilderFactory masterStepBuilderFactory;
	
	 /*
     * Configure outbound flow (requests going to workers)
     */
    @Bean
    public DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(requests())
                .handle(Jms.outboundAdapter(connectionFactory).destination("requests"))
                .get();
    }
    
    /*
     * Configure inbound flow (replies coming from workers)
     */
    @Bean
    public DirectChannel replies() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow(ActiveMQConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("replies"))
                .channel(replies())
                .get();
    }
    
    @Bean
	public ColumnRangePartitioner partitioner() {
		ColumnRangePartitioner partitioner = new ColumnRangePartitioner();
		partitioner.setColumn("USER_ID");
		partitioner.setTable("ACCOUNTS");
		partitioner.setDataSource(this.dataSource);
		return partitioner;
	}
    
    /*
     * Configure the master step
     */
    @Bean
    public Step masterStep() {
        return this.masterStepBuilderFactory.get("masterStep")
                .partitioner("workerStep", this.partitioner())
                .gridSize(4)
                .outputChannel(requests())
                .inputChannel(replies())
                .build();
    }

    @Bean
    public Job remotePartitioningJob() {
        return this.jobBuilderFactory.get("remotePartitioningJob")
                .incrementer(new RunIdIncrementer())
                .start(masterStep())
                .build();
    }
    
    @Scheduled(cron = "${job.master.cron}")
    public void triggerDecryptPwdJob() throws Exception {
        String jobID = String.valueOf(System.currentTimeMillis());
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("JobID", jobID)
                .toJobParameters();
        JobExecution jobExecution = this.jobLauncher.run(remotePartitioningJob(), jobParameters);
        log.info("JobID {} status {}", jobID, jobExecution.getStatus());
    }

}
