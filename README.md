# SpringBoot-Batch

### Setup

##### Add dependencies

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

} 

#### How can I make an item reader thread safe

- You can synchronize the read() method. you can also set saveState=false on the reader.

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
