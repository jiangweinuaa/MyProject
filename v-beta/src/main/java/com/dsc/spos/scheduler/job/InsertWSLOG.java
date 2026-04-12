package com.dsc.spos.scheduler.job;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.utils.Check;

public class InsertWSLOG {
	
	//查询WS日志档
	public static String getQuerySql_WSLOG() {
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(""
				+ " select NUM "
				+ " from ("
				+ " SELECT count(*) as num "
				+ " FROM DCP_WSLOG A"
				+ " WHERE A.DOCNO = ? AND A.EID = ? AND A.ORGANIZATIONNO = ? and A.service_type = ? ) TBL"
		);
		return sqlbuf.toString();
	}
	
	//插入WS日志档
	public static void insert_WSLOG(String serviceName,String docNO,String eId,String organizationNO,String serviceType,String str,String resbody,String code,String description) throws Exception {
		
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
		
		//避免保存异常 docNO为主键  BY JZMA 20200401
		if (Check.Null(docNO)) {
			docNO = " ";
		}
		/////服务类型 1.中台传ERP 2.ERP传中台    BY JZMA 20200212
		if (Check.Null(serviceType) || !serviceType.equals("2")) {
			serviceType="1";  ///服务类型 1.中台传ERP 2.ERP传中台
		}
		
		if(description.length()>255) {
			description = description.substring(0,255);
		}
		if(Check.Null(organizationNO))
		{
			organizationNO=" ";
		}
		String sql_WSLOG = getQuerySql_WSLOG();
		String[] conditionValues_WSLOG = {docNO, eId, organizationNO,serviceType }; // 查詢條件
		List<Map<String, Object>> getQData_WSLOG = StaticInfo.dao.executeQuerySQL(sql_WSLOG, conditionValues_WSLOG);
		String processStatus="N";
		if (serviceType.equals("2") && code.equals("0")) {
			processStatus="Y";
		}
		
		if (getQData_WSLOG != null && getQData_WSLOG.isEmpty() == false ) {
			////System.out.println(getQData_WSLOG.get(0).get("NUM").toString());
			if (getQData_WSLOG.get(0).get("NUM").toString().equals("0")){
				String[] columns1 = {
						"EID", "ORGANIZATIONNO","DOCNO","SERVICE_TYPE","SERVICE_NAME", "REQUEST_XML",  "RESPONSE_XML",
						"ERROR_CODE","ERROR_MSG","PROCESS_STATUS","CREATE_DATE", "CREATE_TIME", "MODIFY_DATE","MODIFY_TIME"
				};
				if(Check.Null(organizationNO))
				{
					organizationNO=" ";
				}
				DataValue[] insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(organizationNO, Types.VARCHAR),
						new DataValue(docNO, Types.VARCHAR),
						new DataValue(serviceType, Types.VARCHAR),
						new DataValue(serviceName, Types.VARCHAR),
						new DataValue(str, Types.VARCHAR),
						new DataValue(resbody, Types.VARCHAR),
						new DataValue(code, Types.VARCHAR),
						new DataValue(description, Types.VARCHAR),
						new DataValue(processStatus, Types.VARCHAR),
						new DataValue(dfDate.format(cal.getTime()), Types.VARCHAR),
						new DataValue(dfTime.format(cal.getTime()), Types.VARCHAR),
						new DataValue(dfDate.format(cal.getTime()), Types.VARCHAR),
						new DataValue(dfTime.format(cal.getTime()), Types.VARCHAR)
				};
				
				StaticInfo.dao.insert("DCP_WSLOG", columns1, insValue1);
			} else {
				// values
				Map<String, DataValue> values = new HashMap<String, DataValue>() ;
				values.put("SERVICE_NAME", new DataValue(serviceName, Types.VARCHAR));
				values.put("REQUEST_XML", new DataValue(str, Types.VARCHAR));
				values.put("RESPONSE_XML", new DataValue(resbody, Types.VARCHAR));
				values.put("ERROR_CODE", new DataValue(code, Types.VARCHAR));
				values.put("ERROR_MSG", new DataValue(description, Types.VARCHAR));
				values.put("PROCESS_STATUS", new DataValue(processStatus, Types.VARCHAR));
				values.put("MODIFY_DATE", new DataValue(dfDate.format(cal.getTime()), Types.VARCHAR));
				values.put("MODIFY_TIME", new DataValue(dfTime.format(cal.getTime()), Types.VARCHAR));
				if(Check.Null(organizationNO))
				{
					organizationNO=" ";
				}
				// condition
				Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
				conditions.put("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
				conditions.put("EID", new DataValue(eId, Types.VARCHAR));
				conditions.put("DOCNO",new DataValue(docNO, Types.VARCHAR));
				conditions.put("SERVICE_TYPE",new DataValue(serviceType, Types.VARCHAR));
				
				StaticInfo.dao.update("DCP_WSLOG", values, conditions);
			}
		}
	}
	
	//删除WS日志档   EID, ORGANIZATIONNO, DOCNO
	public static void delete_WSLOG(String eId,String organizationNO,String serviceType,String docNO) throws Exception {
		
		//避免保存异常 docNO为主键  BY JZMA 20200401
		if (Check.Null(docNO)) {
			docNO = " ";
		}
		/////服务类型 1.中台传ERP 2.ERP传中台    BY JZMA 20200212
		if (Check.Null(serviceType) || !serviceType.equals("2")) {
			serviceType="1";  ///服务类型 1.中台传ERP 2.ERP传中台
		}
		
		// condition
		if(Check.Null(organizationNO))
		{
			organizationNO=" ";
		}
		Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
		conditions.put("EID", new DataValue(eId, Types.VARCHAR));
		conditions.put("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
		conditions.put("SERVICE_TYPE",new DataValue(serviceType, Types.VARCHAR));
		conditions.put("DOCNO", new DataValue(docNO, Types.VARCHAR));
		
		StaticInfo.dao.doDelete("DCP_WSLOG", conditions) ;
	}
	
	
	//查询JOB日志档
	public static String getQuerySql_JOBLOG(){
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(""
				+ "select NUM "
				+ " from ("
				+ " SELECT count(*) as num "
				+ " FROM DCP_JOBLOG A"
				+ " WHERE A.EID = ? AND A.SHOPID=? AND A.JOB_NAME = ?   ) TBL "
		);
		return sqlbuf.toString();
	}
	
	//插入JOB日志档
	public static void insert_JOBLOG(String eId,String shopId, String jobName,String jobDiscretion,String errorMsg) throws Exception {
		
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
		String sDate = dfDate.format(cal.getTime()) ;
		String sTime = dfTime.format(cal.getTime()) ;
		
		if (!Check.Null(errorMsg) && errorMsg.length()>255) {
			errorMsg = errorMsg.substring(0, 255);
		}
		
		String sql = getQuerySql_JOBLOG();
		String[] conditionValues = {eId,shopId,jobName}; // 查詢條件
		List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false ) {
			if (getQData.get(0).get("NUM").toString().equals("0")){
				String[] columns = {
						"EID","SHOPID","JOB_NAME","JOB_DISCRETION","ERROR_MSG",
						"CREATE_DATE","CREATE_TIME","MODIFY_DATE","MODIFY_TIME"
				};
				DataValue[] insValue = null;
				insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(jobName, Types.VARCHAR),
						new DataValue(jobDiscretion, Types.VARCHAR),
						new DataValue(errorMsg,Types.VARCHAR),
						new DataValue(sDate, Types.VARCHAR),
						new DataValue(sTime, Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue("", Types.VARCHAR)
				};
				StaticInfo.dao.insert("DCP_JOBLOG", columns, insValue);
			}
			else
			{
				// values
				Map<String, DataValue> values = new HashMap<String, DataValue>() ;
				values.put("ERROR_MSG", new DataValue(errorMsg, Types.VARCHAR));
				values.put("MODIFY_DATE", new DataValue(sDate, Types.VARCHAR));
				values.put("MODIFY_TIME", new DataValue(sTime, Types.VARCHAR));
				values.put("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
				// condition
				Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
				conditions.put("EID", new DataValue(eId, Types.VARCHAR));
				conditions.put("SHOPID", new DataValue(shopId, Types.VARCHAR));
				conditions.put("JOB_NAME", new DataValue(jobName, Types.VARCHAR));
				StaticInfo.dao.update("DCP_JOBLOG", values, conditions);
			}
		}
	}
	
	//删除JOB日志档  
	public static void delete_JOBLOG(String eId,String shopId,String jobName ) throws Exception {
		// condition
		Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
		conditions.put("EID", new DataValue(eId, Types.VARCHAR));
		conditions.put("SHOPID", new DataValue(shopId, Types.VARCHAR));
		conditions.put("JOB_NAME", new DataValue(jobName, Types.VARCHAR));
		StaticInfo.dao.doDelete("DCP_JOBLOG", conditions) ;
		
	}
	
	
}
