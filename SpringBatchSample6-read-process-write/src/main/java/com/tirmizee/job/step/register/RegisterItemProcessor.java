package com.tirmizee.job.step.register;

import org.springframework.batch.item.ItemProcessor;

public class RegisterItemProcessor implements ItemProcessor<String, Integer>{

	@Override
	public Integer process(String item) throws Exception {
		System.out.println(String.format("processor %s ", item));
		return Integer.valueOf(item);
	}

}
