package com.dsc.spos.dao;

import java.util.HashMap;
import java.util.Map;

public class DelBean {
	private String tableName;
	private Map<String, DataValue> conditions;
	
	public DelBean(String tableName) {
		this.tableName = tableName;
		this.conditions = new HashMap<String, DataValue>();
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void addCondition(String key, DataValue value) {
		this.conditions.put(key, value);
	}

	public Map<String, DataValue> getConditions() {
		return conditions;
	}
}
