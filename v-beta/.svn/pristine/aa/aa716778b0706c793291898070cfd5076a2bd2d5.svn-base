package com.dsc.spos.dao;

import java.util.ArrayList;
import java.util.List;

public class InsBean {
	private String tableName;
	private String[] columns;
	private List<DataValue[]> values;
	
	public InsBean(String tableName, String[] columns) {
		this.tableName = tableName;
		this.columns = columns;
		this.values = new ArrayList<DataValue[]>();
	}

	public String getTableName() {
		return tableName;
	}

	public String[] getColumns() {
		return columns;
	}

	public List<DataValue[]> getValues() {
		return values;
	}

	public void addValues(DataValue[] row) {
		this.values.add(row);
	}
}

