package com.tirmizee.job.step.invoice;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class InvoiceItemWriter implements ItemWriter<String>{

	@Override
	public void write(List<? extends String> items) throws Exception {
		for (String item : items) {
			System.out.println(String.format("writer item %s", item));
		}
	}

}
