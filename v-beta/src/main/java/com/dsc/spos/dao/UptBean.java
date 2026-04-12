package com.dsc.spos.dao;

import java.util.HashMap;
import java.util.Map;

public class UptBean extends DelBean {
	private Map<String, DataValue> updateValues;

	public UptBean(String tableName) {
		super(tableName);
		this.updateValues = new HashMap<String, DataValue>();
	}

	public void addUpdateValue(String key, DataValue value) {
		this.updateValues.put(key, value);
	}
	
	public Map<String, DataValue> getUpdateValues() {
		return updateValues;
	}
}
