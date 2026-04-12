package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MonitorCreate_OpenReq;
import com.dsc.spos.json.cust.req.DCP_MonitorCreate_OpenReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_MonitorCreate_OpenReq.level2ElmJob;
import com.dsc.spos.json.cust.req.DCP_MonitorCreate_OpenReq.level2ElmService;
import com.dsc.spos.json.cust.req.DCP_MonitorCreate_OpenReq.level2ElmShopEDate;
import com.dsc.spos.json.cust.req.DCP_MonitorCreate_OpenReq.level2ElmWsDcp;
import com.dsc.spos.json.cust.req.DCP_MonitorCreate_OpenReq.level2ElmWsErp;
import com.dsc.spos.json.cust.req.DCP_MonitorCreate_OpenReq.level3ElmWsErpError;
import com.dsc.spos.json.cust.req.DCP_MonitorCreate_OpenReq.level3ElmWsDcpError;
import com.dsc.spos.json.cust.res.DCP_MonitorCreate_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_MonitorCreate_Open
 * 服务说明：监控新增
 * @author jinzma 
 * @since  2020-03-20
 */
public class DCP_MonitorCreate_Open extends SPosAdvanceService<DCP_MonitorCreate_OpenReq,DCP_MonitorCreate_OpenRes> {

	@Override
	protected void processDUID(DCP_MonitorCreate_OpenReq req, DCP_MonitorCreate_OpenRes res) throws Exception {
		// TODO 自动生成的方法存根
		level1Elm request = req.getRequest();
		String customerId = request.getCustomerId();
		List<level2ElmWsErp> wsErp = request.getWsErp();
		List<level2ElmWsDcp> wsDcp = request.getWsDcp();
		List<level2ElmService> service = request.getService();
		List<level2ElmJob> job = request.getJob();
		List<level2ElmShopEDate> shopEDate = request.getShopEDate();

		try 
		{
			//过滤19服务器上的本地记录
			String sql = " select * from dcp_monitor where CUSTOMERID='"+customerId+"' and ISHEAD='N' " ;
			List<Map<String,Object>> getQData = this.doQueryData(sql,null);
			if (getQData == null || getQData.isEmpty())
			{
				if (checkExist(customerId))
				{
					delMonitor(customerId);                   //删除监控子表			
				}
				insertMonitor(request);                   //新增监控表  DCP_MONITOR
				insertWsErp(customerId,wsErp);            //新增监控表 DCP_MONITOR_WSERP && DCP_MONITOR_WSERP_ERROR
				insertWsDcp(customerId,wsDcp);            //新增监控表 DCP_MONITOR_WSDCP && DCP_MONITOR_WSDCP_ERROR
				insertService(customerId,service);        //新增监控表 DCP_MONITOR_SERVICE
				insertJob(customerId,job);                //新增监控表 DCP_MONITOR_JOB
				insertShopEDate(customerId,shopEDate);    //新增监控表 DCP_MONITOR_EDATE

				this.doExecuteDataToDB();
			}

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		}
		catch (Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MonitorCreate_OpenReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MonitorCreate_OpenReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MonitorCreate_OpenReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MonitorCreate_OpenReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		level1Elm request = req.getRequest();
		if (request!=null )
		{
			if (Check.Null(request.getCustomerId())) 
			{
				errMsg.append("客户ID不可为空值, ");
				isFail = true;
			}
		}
		else
		{
			errMsg.append("request不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_MonitorCreate_OpenReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_MonitorCreate_OpenReq>(){};
	}

	@Override
	protected DCP_MonitorCreate_OpenRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MonitorCreate_OpenRes();
	}

	private boolean checkExist(String customerId)  throws Exception {
		String sql=" select * from dcp_monitor a where a.customerid='"+customerId+"' and a.ISHEAD='Y'  ";
		boolean exist = false;
		List<Map<String, Object>> getQData = this.doQueryData(sql,null);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}
	private void delMonitor(String customerId) throws Exception{
		DelBean db1 = new DelBean("DCP_MONITOR_WSERP");
		db1.addCondition("CUSTOMERID", new DataValue(customerId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));

		DelBean db2 = new DelBean("DCP_MONITOR_WSERP_ERROR");
		db2.addCondition("CUSTOMERID", new DataValue(customerId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db2));

		DelBean db3 = new DelBean("DCP_MONITOR_WSDCP");
		db3.addCondition("CUSTOMERID", new DataValue(customerId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db3));

		DelBean db4 = new DelBean("DCP_MONITOR_WSDCP_ERROR");
		db4.addCondition("CUSTOMERID", new DataValue(customerId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db4));

		DelBean db5 = new DelBean("DCP_MONITOR_SERVICE");
		db5.addCondition("CUSTOMERID", new DataValue(customerId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db5));

		DelBean db6 = new DelBean("DCP_MONITOR_JOB");
		db6.addCondition("CUSTOMERID", new DataValue(customerId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db6));

		DelBean db7 = new DelBean("DCP_MONITOR_EDATE");
		db7.addCondition("CUSTOMERID", new DataValue(customerId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db7));

	}
	private void insertMonitor(level1Elm request) throws Exception {
		String customerId = request.getCustomerId();
		String customerName = request.getCustomerName();
		String dcpVersion = request.getDcpVersion();
		String eId = request.geteId();
		String shopQty = request.getShopQty();
		String status = request.getStatus();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastTrantime = sdf.format(cal.getTime());

		if (checkExist(customerId))
		{
			//dcp_monitor 资料如果存在只进行更新
			UptBean ub = new UptBean("DCP_MONITOR");
			ub.addUpdateValue("DCPVERSION", new DataValue(dcpVersion, Types.VARCHAR));
			ub.addUpdateValue("SHOPQTY", new DataValue(shopQty, Types.VARCHAR));
			ub.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			ub.addUpdateValue("EID", new DataValue(eId, Types.VARCHAR));
			ub.addUpdateValue("LASTMODITIME", new DataValue(lastTrantime, Types.DATE));
			ub.addUpdateValue("ISHEAD", new DataValue("Y", Types.VARCHAR));
			// condition
			ub.addCondition("CUSTOMERID", new DataValue(customerId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub));	
		}
		else
		{
			String[] columns_monitor = {
					"CUSTOMERID","CUSTOMERNAME","DCPVERSION","SHOPQTY","STATUS","LASTMODITIME","EID","ISHEAD"  	
			};
			DataValue[] insValue_monitor = new DataValue[] 
					{
							new DataValue(customerId, Types.VARCHAR), 
							new DataValue(customerName, Types.VARCHAR), 
							new DataValue(dcpVersion, Types.VARCHAR),
							new DataValue(shopQty, Types.VARCHAR),								
							new DataValue(status, Types.VARCHAR),
							new DataValue(lastTrantime, Types.DATE),
							new DataValue(eId, Types.VARCHAR),
							new DataValue("Y", Types.VARCHAR)
					};
			InsBean ib_monitor = new InsBean("DCP_MONITOR", columns_monitor);
			ib_monitor.addValues(insValue_monitor);
			this.addProcessData(new DataProcessBean(ib_monitor)); 
		}

	}
	private void insertWsErp(String customerId, List<level2ElmWsErp> wsErp ) throws Exception {
		for (level2ElmWsErp par :wsErp )
		{
			String wsName =par.getWsName();
			String wsExplain = par.getWsExplain();
			String status = par.getStatus();
			String notTransQty = par.getNotTransQty();
			String transQty = par.getTransQty();
			List<level3ElmWsErpError> wsErpError = par.getError();
			for ( level3ElmWsErpError error:wsErpError  )
			{
				String item = error.getItem();
				String shopId = error.getShopId();
				String shopName = error.getShopName();
				String docNo = error.getDocNo();
				String errorCode = error.getErrorCode();
				String errorMsg = error.getErrorMsg();
				String request = error.getRequest();
				String response = error.getResponse();
				String modifyDate = error.getModifyDate();
				String modifyTime = error.getModifyTime();
				String[] columns = {
						"CUSTOMERID","WSNAME","ITEM","SHOPID","SHOPNAME","DOCNO","ERRORCODE","ERRORMSG",
						"REQUEST","RESPONSE","MODIFY_DATE","MODIFY_TIME"
				};
				DataValue[] insValue = new DataValue[] 
						{
								new DataValue(customerId, Types.VARCHAR), 
								new DataValue(wsName, Types.VARCHAR), 
								new DataValue(item, Types.VARCHAR),
								new DataValue(shopId, Types.VARCHAR),								
								new DataValue(shopName, Types.VARCHAR),
								new DataValue(docNo, Types.VARCHAR),
								new DataValue(errorCode, Types.VARCHAR),
								new DataValue(errorMsg, Types.VARCHAR),
								new DataValue(request, Types.VARCHAR),
								new DataValue(response, Types.VARCHAR),
								new DataValue(modifyDate, Types.VARCHAR),
								new DataValue(modifyTime, Types.VARCHAR),
						};
				InsBean ib = new InsBean("DCP_MONITOR_WSERP_ERROR", columns);  
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib)); 	
			}
			String[] columns = {"CUSTOMERID","WSNAME","WSEXPLAIN","STATUS","TRANSQTY","NOTTRANSQTY"						
			};
			DataValue[] insValue = new DataValue[] 
					{
							new DataValue(customerId, Types.VARCHAR), 
							new DataValue(wsName, Types.VARCHAR), 
							new DataValue(wsExplain, Types.VARCHAR),
							new DataValue(status, Types.VARCHAR),								
							new DataValue(transQty, Types.VARCHAR),
							new DataValue(notTransQty, Types.VARCHAR),
					};
			InsBean ib = new InsBean("DCP_MONITOR_WSERP", columns);  
			ib.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib)); 	
		}
	}
	private void insertWsDcp(String customerId, List<level2ElmWsDcp> wsDcp ) throws Exception {
		for (level2ElmWsDcp par :wsDcp )
		{
			String wsName =par.getWsName();
			String wsExplain = par.getWsExplain();
			String status = par.getStatus();
			String notTransQty = par.getNotTransQty();
			String transQty = par.getTransQty();
			List<level3ElmWsDcpError> wsDcpError = par.getError();
			for ( level3ElmWsDcpError error:wsDcpError  )
			{
				String item = error.getItem();
				String shopId = error.getShopId();
				String shopName = error.getShopName();
				String docNo = error.getDocNo();
				String errorCode = error.getErrorCode();
				String errorMsg = error.getErrorMsg();
				String request = error.getRequest();
				String response = error.getResponse();
				String modifyDate = error.getModifyDate();
				String modifyTime = error.getModifyTime();
				String[] columns = {
						"CUSTOMERID","WSNAME","ITEM","SHOPID","SHOPNAME","DOCNO","ERRORCODE","ERRORMSG",
						"REQUEST","RESPONSE","MODIFY_DATE","MODIFY_TIME"
				};
				DataValue[] insValue = new DataValue[] 
						{
								new DataValue(customerId, Types.VARCHAR), 
								new DataValue(wsName, Types.VARCHAR), 
								new DataValue(item, Types.VARCHAR),
								new DataValue(shopId, Types.VARCHAR),								
								new DataValue(shopName, Types.VARCHAR),
								new DataValue(docNo, Types.VARCHAR),
								new DataValue(errorCode, Types.VARCHAR),
								new DataValue(errorMsg, Types.VARCHAR),
								new DataValue(request, Types.VARCHAR),
								new DataValue(response, Types.VARCHAR),
								new DataValue(modifyDate, Types.VARCHAR),
								new DataValue(modifyTime, Types.VARCHAR),
						};
				InsBean ib = new InsBean("DCP_MONITOR_WSDCP_ERROR", columns);  
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib)); 	
			}
			String[] columns = {"CUSTOMERID","WSNAME","WSEXPLAIN","STATUS","TRANSQTY","NOTTRANSQTY"						
			};
			DataValue[] insValue = new DataValue[] 
					{
							new DataValue(customerId, Types.VARCHAR), 
							new DataValue(wsName, Types.VARCHAR), 
							new DataValue(wsExplain, Types.VARCHAR),
							new DataValue(status, Types.VARCHAR),								
							new DataValue(transQty, Types.VARCHAR),
							new DataValue(notTransQty, Types.VARCHAR),
					};
			InsBean ib = new InsBean("DCP_MONITOR_WSDCP", columns);  
			ib.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib)); 	
		}
	}
	private void insertService(String customerId,List<level2ElmService> service) throws Exception {
		for (level2ElmService par :service )
		{
			String serviceId =par.getServiceId();
			String serviceName = par.getServiceName();
			String concurrencyQty = par.getConcurrencyQty();
			String errorMsg = par.getErrorMsg();
			String status = par.getStatus();
			String[] columns = {"CUSTOMERID","SERVICEID","SERVICENAME","CONCURRENCYQTY","ERRORMSG","STATUS"};
			DataValue[] insValue = new DataValue[] 
					{
							new DataValue(customerId, Types.VARCHAR), 
							new DataValue(serviceId, Types.VARCHAR), 
							new DataValue(serviceName, Types.VARCHAR),
							new DataValue(concurrencyQty, Types.VARCHAR),								
							new DataValue(errorMsg, Types.VARCHAR),
							new DataValue(status, Types.VARCHAR),
					};
			InsBean ib = new InsBean("DCP_MONITOR_SERVICE", columns);  
			ib.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib)); 	
		}
	}
	private void insertJob(String customerId,List<level2ElmJob> job) throws Exception {
		for (level2ElmJob par :job )
		{
			String jobName =par.getJobName();
			String jobDiscretion = par.getJobDiscretion();
			String errorMsg = par.getErrorMsg();
			String status = par.getStatus();
			String[] columns = {"CUSTOMERID","JOBNAME","JOBDISCRETION","ERRORMSG","STATUS"};				
			DataValue[] insValue = new DataValue[] 
					{
							new DataValue(customerId, Types.VARCHAR), 
							new DataValue(jobName, Types.VARCHAR), 
							new DataValue(jobDiscretion, Types.VARCHAR),
							new DataValue(errorMsg, Types.VARCHAR),								
							new DataValue(status, Types.VARCHAR),
					};
			InsBean ib = new InsBean("DCP_MONITOR_JOB", columns);  
			ib.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib)); 	
		}
	}
	private void insertShopEDate(String customerId,List<level2ElmShopEDate> shopEDate) throws Exception {

		for (level2ElmShopEDate par :shopEDate )
		{
			String shopId =par.getShopId();
			String shopName = par.getShopName();
			String eDate =par.geteDate();
			String errorMsg = par.getErrorMsg();
			String status = par.getStatus();
			String[] columns = {"CUSTOMERID","SHOPID","SHOPNAME","EDATE","ERRORMSG","STATUS"};		

			DataValue[] insValue = new DataValue[] 
					{
							new DataValue(customerId, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(shopName, Types.VARCHAR),
							new DataValue(eDate, Types.VARCHAR),
							new DataValue(errorMsg, Types.VARCHAR),								
							new DataValue(status, Types.VARCHAR),
					};
			InsBean ib = new InsBean("DCP_MONITOR_EDATE", columns);  
			ib.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib)); 	
		}

	}


}


