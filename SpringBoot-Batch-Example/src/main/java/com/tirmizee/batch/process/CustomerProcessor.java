package com.tirmizee.batch.process;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.tirmizee.batch.model.Customer;

@Component
public class CustomerProcessor implements ItemProcessor<Customer, String> {

	@Override
	public String process(Customer item) throws Exception {
		String firstName = item.getFirstName() + "0";
		String lastName = item.getLastName() + "0";
		return firstName + lastName;
	}

}
