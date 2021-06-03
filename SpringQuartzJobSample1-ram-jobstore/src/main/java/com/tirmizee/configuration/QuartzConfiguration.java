package com.tirmizee.configuration;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

import com.tirmizee.quartz.domain.CustomQuartzJob;

@Configuration
public class QuartzConfiguration {
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
    private JobLocator jobLocator;
	
	@Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }
	
	@Bean(name = "jobOneDetail")
    public JobDetail jobOneDetail() {
		
		JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "demoJobOne");
        jobDataMap.put("jobLauncher", this.jobLauncher);
        jobDataMap.put("jobLocator", this.jobLocator);
       
        return JobBuilder.newJob(CustomQuartzJob.class)
                .withIdentity("demoJobOne")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }
	
	@Bean
    public CronTriggerFactoryBean jobOneTrigger(
    		@Qualifier("jobOneDetail") JobDetail jobDetail,
    		@Value("${job.invoice.cron.expression}") String cronExpression) {
        return this.createCronTrigger(jobDetail, cronExpression);
    }
	
	private CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        return factoryBean;
    }

}
