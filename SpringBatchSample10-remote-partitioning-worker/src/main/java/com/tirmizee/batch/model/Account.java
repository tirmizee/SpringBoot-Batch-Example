package com.tirmizee.batch.model;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Account {
	
	private Integer userId;
	private String username;
	private String password;
	private String email;
	private Timestamp createOn;
	
}
