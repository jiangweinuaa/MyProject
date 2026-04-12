package com.dsc.spos.ninetyone.util;

import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.utils.Check;

//20240918 由com/dsc/spos/utils/ColumnDataValue.java替换
 
public class ColumnDataValue {

	
	public List<String> Columns = new ArrayList<String>();
	
	public List<DataValue> DataValues = new ArrayList<DataValue>();


	public void Add(String ColumnName,Object Value, int DataType )
	{
		Columns.add(ColumnName);
		if (null==Value){  //20241010 modi by 01029
			DataValues.add(new DataValue("",DataType));
		}else {
			DataValues.add(new DataValue(Value,DataType));
		}
		
	}
	
}
