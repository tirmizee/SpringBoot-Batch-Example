# SpringBoot-Batch

### Setup

##### database

	docker run --name postgres -p 5432:5432 -e POSTGRES_PASSWORD=password -e POSTGRES_USER=root -e POSTGRES_DB=docker -d postgres

##### dependencies

    <dependency>
	   <groupId>org.springframework.boot</groupId>
	   <artifactId>spring-boot-starter-batch</artifactId>
    </dependency>

##### Enable spring batch

	@EnableBatchProcessing
	@SpringBootApplication
	public class SpringBatchSample1Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchSample1Application.class, args);
	}



#### How can I make an item reader thread safe

- You can synchronize the read() method. you can also set saveState=false on the reader.

#### Whether configured jobs should overwrite existing job definitions.

- spring.quartz.overwrite-existing-jobs = true

### Reference

- https://docs.spring.io/spring-batch/docs/current/reference/html/readersAndWriters.html
- https://howtodoinjava.com/spring-batch/java-config-multiple-steps/
- https://javasneo.blogspot.com/2020/06/how-to-integrate-spring-boot-spring-batch-quartz.html
- https://www.jackrutorial.com/2018/03/quartz-scheduler-annotation-with-spring-batch-in-spring-boot-tutorial.html
- https://docs.spring.io/spring-batch/docs/current/reference/html/spring-batch-integration.html#asynchronous-processors
- https://github.com/spring-projects/spring-batch/tree/master/spring-batch-samples#remote-chunking-sample
- https://docs.spring.io/spring-batch/docs/4.3.x/reference/pdf/spring-batch-reference.pdf (official)
- https://alexandreesl.com/tag/joblocator (joblocator)
- https://github.com/gredwhite/spring-batch-remote-execution (integration)
- https://code.likeagirl.io/four-ways-to-scale-spring-batch-3ad5042e0266 (scaling)
- https://github.com/quartz-scheduler/quartz/blob/master/quartz-core/src/main/resources/org/quartz/impl/jdbcjobstore/tables_postgres.sql
- https://docs.spring.io/spring-batch/docs/current/reference/html/spring-batch-integration.html
- https://docs.spring.io/spring-batch/docs/current/reference/html/scalability.html#remoteChunking
- https://github.com/spring-projects/spring-batch/tree/main/spring-batch-samples/src/main/java/org/springframework/batch/sample/remotechunking
- https://docs.spring.io/spring-batch/docs/current/reference/html/index-single.html
- https://www.petrikainulainen.net/programming/spring-framework/spring-batch-tutorial-writing-information-to-a-database-with-jdbc/ (JdbcBatchItemWriter)
