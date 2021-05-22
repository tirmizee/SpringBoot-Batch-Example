package com.tirmizee.job.step.invoice;

import java.util.Iterator;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class InvoiceItemReader implements ItemReader<String> {

	private Iterator<String> datas;
	
	public InvoiceItemReader(Iterator<String> datas) {
		this.datas = datas;
	}
	
	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if(this.datas.hasNext()) {
			String item = this.datas.next();
			System.out.println(String.format("reader %s ", item));
			return item;
		}
		return null;
	}

}
