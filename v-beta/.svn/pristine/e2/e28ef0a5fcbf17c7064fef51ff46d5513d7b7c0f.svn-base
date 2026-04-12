package com.dsc.spos.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface DsmDAO {
	
	public void closeDAO();
	
	/**
	 * 可以混合 insert, update, delete 的 sql, 並有 transaction 機制管理.
	 * @param data
	 * @return
	 */
	public boolean useTransactionProcessData(List<DataProcessBean> data) throws Exception;

	/**
	 * 執行SQL
	 * @param sql
	 * @param conditionValues
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> executeQuerySQL(final String sql, final String[] conditionValues) throws Exception;
	
	/**
	 * 執行SQL
	 * @param sql
	 * @param conditionValues
	 * @param isShowSql
	 * @return
	 * @throws Exception
	 */
	public  List<Map<String, Object>> executeQuerySQL(final String sql, final String[] conditionValues, boolean isShowSql) throws Exception;
	
	/**
	 * 刪除資料
	 * @param conn
	 * @param tableName
	 * @param conditions
	 * @return
	 * @throws Exception
	 */
	public boolean doDelete(Connection conn, String tableName, Map<String, DataValue> conditions) throws Exception;
	
	/**
	 * 刪除資料
	 * @param tableName
	 * @param conditions
	 * @return
	 * @throws Exception
	 */
	public boolean doDelete(String tableName, Map<String, DataValue> conditions) throws Exception;
	
	
	/**
	 * 新增資料
	 * @param conn
	 * @param tableName
	 * @param columns
	 * @param values
	 * @throws Exception
	 */
	public int insert(Connection conn, String tableName, String[] columns, DataValue[] values) throws Exception;
	
	/**
	 * 新增資料
	 * @param tableName
	 * @param columns
	 * @param values
	 * @return
	 * @throws Exception
	 */
	public int insert(String tableName, String[] columns, DataValue[] values) throws Exception;
	
	/**
	 * 更新資料
	 * @param conn
	 * @param tableName
	 * @param values
	 * @param conditions
	 * @throws Exception
	 */
	public boolean update(Connection conn, String tableName, Map<String, DataValue> values, Map<String, DataValue> conditions) throws Exception;
	
	/**
	 * 更新資料
	 * @param tableName
	 * @param values
	 * @param conditions
	 * @return
	 * @throws Exception
	 */
	public boolean update(String tableName, Map<String, DataValue> values, Map<String, DataValue> conditions) throws Exception;

	int exec(Connection conn, String tableName) throws Exception;

	int exec(String tableName) throws Exception;
	
	/**
	 * 存储过程
	 * @param procedure
	 * @param inputParameter
	 * @param outParameter
	 * @return Map<Integer,Object>
	 * @throws Exception
	 */
	public Map<Integer,Object> storedProcedure (String procedure, Map<Integer,Object> inputParameter,Map<Integer,Integer> outParameter) throws Exception;
	
	/**
	 * 存储过程处理
	 * @param procedure
	 * @param inputParameter
	 * @param outParameter
	 * @return Map<Integer,Object>
	 * @throws Exception
	 */
	public boolean storedProcedureProcess (Connection conn,String procedure, Map<Integer,Object> inputParameter) throws Exception;


    /**
     * 绑定变量SQL的写法(注意仅适用于简单SQL语句，较复杂的SQL不要用)
     * @param sql
     * @param values
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> executeQuerySQL_BindSQL(String sql, final List<DataValue> values) throws Exception;

	
}
