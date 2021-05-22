package com.tirmizee.job.step.register;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class RegisterItemWriter implements ItemWriter<Integer>{

	@Override
	public void write(List<? extends Integer> items) throws Exception {
		for (Integer item : items) {
			System.out.println(String.format("writer item %s", item));
		}
	}

}
