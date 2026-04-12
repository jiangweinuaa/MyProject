package com.dsc.spos.thirdpart.youzan.util;

import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataValue;

public class ColumnDataValue {

	
	public List<String> Columns = new ArrayList<String>();
	
	public List<DataValue> DataValues = new ArrayList<DataValue>();
	
	public void Add(String ColumnName,Object Value, int DataType )
	{
		Columns.add(ColumnName);
		DataValues.add(new DataValue(Value,DataType));
	}
	
}
