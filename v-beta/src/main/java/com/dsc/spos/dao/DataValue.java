package com.dsc.spos.dao;

/**
 * 用來指定資料的型 態
 * @author Xavier
 *
 */
public final class DataValue {
	
	/**
	 * 查詢的條件. 目前只有三 種, 會使用到的情況, 目前只有 update and delete.
	 * EQ ==> 等於
	 * NE ==> 不等於
	 * OR ==> 或
	 * Less==>小于
	 * LessEQ==>小于等于
	 * Greater==>大于
	 * GreaterEQ==>大于等于
	 * UpdateSelf==>数据库字段+1
	 * SubSelf ==》 数据库字段-1
	 * OtherFieldname==>字段名称
	 * @author Xavier
	 *
	 */
	public enum DataExpression {EQ, NE, OR, IN,Less,LessEQ,Greater,GreaterEQ,UpdateSelf,SubSelf,OtherFieldname}
	
	private Object value;
	private int dataType; //use java.sql.Types
	private DataExpression ep;
	
	public DataValue(Object value, int dataType, DataExpression ep) {
		this.value = value;
		this.dataType = dataType;
		this.ep = ep;
	}
	
	public DataValue(Object value, int dataType) {
		this.value = value;
		this.dataType = dataType;
		this.ep = DataExpression.EQ; //預設都是等於
	}
	
	
	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}
	
	public int getDataType() {
		return dataType;
	}

	/**
	 * 更新 or 刪除的 condition
	 * @return
	 */
	public DataExpression getEp() {
		return ep;
	}
}
