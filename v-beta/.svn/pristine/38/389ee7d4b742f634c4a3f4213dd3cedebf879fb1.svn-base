package com.dsc.spos.dao;

import java.util.List;
import java.util.Map;

/**
 * 1. 收集要 insert or update or delete 的 store. <br/>
 * 2. 採用這個載體, 會 用 transaction 的機制來處理<br/>
 * 3. 
 * @author Xavier
 *
 */
public final class DataProcessBean {
	
	public enum DataProcessType {INSERT, UPDATE, DELETE,EXEC,PROCEDURE}

	private DataProcessType processType;
	private Map<Integer,Object> inputParameter;
	private String procedure;
	private String tableName; 
	
	//新增時才會用到
	private String[] columns; 
	private List<DataValue[]> datas;
	
	//更新 and 刪除
	private Map<String, DataValue> values;
	private Map<String, DataValue> conditions;

	public DataProcessBean(InsBean ib) {
		this.processType = DataProcessType.INSERT;
		this.tableName = ib.getTableName();
		this.columns = ib.getColumns();
		this.datas = ib.getValues();
	}
	
	public DataProcessBean(UptBean utb) {
		this.processType = DataProcessType.UPDATE;
		this.tableName = utb.getTableName();
		this.values = utb.getUpdateValues();
		this.conditions = utb.getConditions();
	}
	
	public DataProcessBean(DelBean db) {
		this.processType = DataProcessType.DELETE;
		this.tableName = db.getTableName();
		this.conditions = db.getConditions();
	}

	public DataProcessBean(ExecBean db) {
		this.processType = DataProcessType.EXEC;
		this.tableName = db.getExecsql();
		this.conditions = null;
	}
	
	public DataProcessBean(ProcedureBean db) {
		this.processType = DataProcessType.PROCEDURE;
		this.procedure = db.getProcedure();
		this.inputParameter = db.getInputParameter();
	}
	
	
	public DataProcessType getProcessType() {
		return processType;
	}

	public String getTableName() {
		return tableName;
	}

	public String[] getColumns() {
		return columns;
	}

	public List<DataValue[]> getDatas() {
		return datas;
	}

	public Map<String, DataValue> getValues() {
		return values;
	}

	public Map<String, DataValue> getConditions() {
		return conditions;
	}

	public Map<Integer, Object> getInputParameter() {
		return inputParameter;
	}

	public String getProcedure() {
		return procedure;
	}




}
