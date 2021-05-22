package com.tirmizee.job.step.register;

import java.util.Iterator;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class RegisterItemReader implements ItemReader<String> {

	private Iterator<String> items;
	
	public RegisterItemReader(Iterator<String> items) {
		this.items = items;
	}
	
	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (this.items.hasNext()) {
			String item = this.items.next();
			System.out.println(String.format("reader %s ", item));
			return item;
		}
		return null;
	}

}
