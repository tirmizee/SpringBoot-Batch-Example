package com.tirmizee.batch.domain;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

public class ColumnRangePartitioner implements Partitioner {
	
	private JdbcOperations jdbcTemplate;

	private String table;

	private String column;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		
		int min = jdbcTemplate.queryForObject("SELECT MIN(" + this.column + ") from " + this.table, Integer.class);
		int max = jdbcTemplate.queryForObject("SELECT MAX(" + this.column + ") from " + this.table, Integer.class);
		int targetSize = (max - min) / gridSize + 1;
		
		Map<String, ExecutionContext> result = new HashMap<>();
		
		int number = 0;
		int start = min;
		int end = start + targetSize - 1;
		
		while (start <= max) {
			
			if (end >= max) {
				end = max;
			}
			
			ExecutionContext value = new ExecutionContext();
			value.putInt("minValue", start);
			value.putInt("maxValue", end);
			value.putString("column", this.column);
			
			start += targetSize;
			end += targetSize;
			number++;
			
			result.put("partition" + number, value);
		}

		return result;
	}
	
	public void setTable(String table) {
		this.table = table;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
}
