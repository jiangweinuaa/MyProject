package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_MonitorDetailQueryReq;
import com.dsc.spos.json.cust.req.DCP_MonitorDetailQueryReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_MonitorDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DCP_MonitorDetailQuery
 * 服务说明：监控明细查询
 * @author jinzma 
 * @since  2020-03-18
 */
public class DCP_MonitorDetailQuery extends SPosBasicService<DCP_MonitorDetailQueryReq,DCP_MonitorDetailQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_MonitorDetailQueryReq req) throws Exception {
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
	protected TypeToken<DCP_MonitorDetailQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_MonitorDetailQueryReq>(){} ;
	}

	@Override
	protected DCP_MonitorDetailQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MonitorDetailQueryRes();
	}

	@Override
	protected DCP_MonitorDetailQueryRes processJson(DCP_MonitorDetailQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;
		DCP_MonitorDetailQueryRes res = this.getResponse();
		level1Elm request = req.getRequest();
		String customerId=request.getCustomerId();
		try
		{
			res.setWsErp(new ArrayList<DCP_MonitorDetailQueryRes.level1ElmWsErp>());
			res.setWsDcp(new ArrayList<DCP_MonitorDetailQueryRes.level1ElmWsDcp>());
			res.setService(new ArrayList<DCP_MonitorDetailQueryRes.level1ElmService>());
			res.setJob(new ArrayList<DCP_MonitorDetailQueryRes.level1ElmJob>());
			res.setShopEDate(new ArrayList<DCP_MonitorDetailQueryRes.level1ElmShopEDate>());

			sql=" select to_char(LASTMODITIME,'YYYY-MM-DD hh24:mi:ss') as lastTrantime,a.* from dcp_monitor a where a.customerid='"+customerId+"'  ";
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			if (getQData != null && getQData.isEmpty() == false) 
			{
				String contact = getQData.get(0).get("CONTACT").toString();
				String telephone = getQData.get(0).get("TELEPHONE").toString();
				String dcpConnInfo = getQData.get(0).get("DCPCONNINFO").toString();
				String dcpUrl = getQData.get(0).get("DCPURL").toString();
				String lastTrantime = getQData.get(0).get("LASTTRANTIME").toString();
				res.setContact(contact);
				res.setTelephone(telephone);
				res.setDcpConnInfo(dcpConnInfo);
				res.setDcpUrl(dcpUrl);
				res.setLastTrantime(lastTrantime);

				wsErp(customerId,res);   
				wsDcp(customerId,res);    
				service(customerId,res);   
				job(customerId,res);   
				shopEDate(customerId,res);  

			}
			else 
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"客户ID:"+customerId +"不存在" );
			}

			return res;		
		}
		catch(Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_MonitorDetailQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	private void wsErp(String customerId,DCP_MonitorDetailQueryRes res) throws Exception {
		String sql=" select a.CUSTOMERID,a.WSNAME,a.WSEXPLAIN,a.STATUS,a.TRANSQTY,a.NOTTRANSQTY,b.ITEM,b.SHOPID,"
				+ " b.SHOPNAME,b.DOCNO,b.ERRORCODE,b.ERRORMSG,b.REQUEST,b.RESPONSE,b.MODIFY_DATE,b.MODIFY_TIME  "
				+ " from dcp_monitor_wserp a "
				+ " left join dcp_monitor_wserp_error b on a.customerid=b.customerid and a.wsname=b.wsname and b.customerid='"+customerId+"' "
				+ " where a.customerid='"+customerId+"'  order by a.wsname,b.item ";
		List<Map<String, Object>> getQData=this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) 
		{
			Map<String, Boolean> condition = new HashMap<String, Boolean>();
			condition.put("WSNAME", true);
			List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQData, condition);
			int item=1;
			for (Map<String, Object> oneData : getQHeader) 
			{
				DCP_MonitorDetailQueryRes.level1ElmWsErp oneLv1 = res.new level1ElmWsErp();
				String wsName = oneData.get("WSNAME").toString();
				String wsExplain = oneData.get("WSEXPLAIN").toString();
				String transQty = oneData.get("TRANSQTY").toString();
				String notTransQty = oneData.get("NOTTRANSQTY").toString();
				String status = oneData.get("STATUS").toString();

				oneLv1.setError(new ArrayList<DCP_MonitorDetailQueryRes.level2ElmWsErpError >());		
				for (Map<String, Object> oneError : getQData) 
				{
					String oneError_wsName = oneError.get("WSNAME").toString();
					String oneError_item = oneError.get("ITEM").toString();
					if (wsName.equals(oneError_wsName) && !Check.Null(oneError_item))
					{
						DCP_MonitorDetailQueryRes.level2ElmWsErpError oneLv2 = res.new level2ElmWsErpError();
						String oneError_shopId = oneError.get("SHOPID").toString();
						String oneError_shopName = oneError.get("SHOPNAME").toString();
						String oneError_docNo = oneError.get("DOCNO").toString();
						String oneError_errorCode = oneError.get("ERRORCODE").toString();
						String oneError_errorMsg = oneError.get("ERRORMSG").toString();
						String oneError_modifyDate = oneError.get("MODIFY_DATE").toString();
						String oneError_modifyTime = oneError.get("MODIFY_TIME").toString();

						String oneError_request = "";
						try
						{
							if (oneError.get("REQUEST")!=null && !Check.Null(oneError.get("REQUEST").toString()))   
							{
								oneError_request =oneError.get("REQUEST").toString();
							}
						}
						catch(Exception e)
						{
							oneError_request="" ;
						}

						String oneError_response = "";
						try
						{
							if (oneError.get("RESPONSE")!=null && !Check.Null(oneError.get("RESPONSE").toString()))   
							{
								oneError_response = oneError.get("RESPONSE").toString();
							}
						}
						catch(Exception e)
						{
							oneError_response="" ;
						}

						oneLv2.setItem(oneError_item);
						oneLv2.setShopId(oneError_shopId);
						oneLv2.setShopName(oneError_shopName);
						oneLv2.setDocNo(oneError_docNo);
						oneLv2.setErrorCode(oneError_errorCode);
						oneLv2.setErrorMsg(oneError_errorMsg);
						oneLv2.setRequest(oneError_request);
						oneLv2.setResponse(oneError_response);
						oneLv2.setModifyDate(oneError_modifyDate);
						oneLv2.setModifyTime(oneError_modifyTime);

						oneLv1.getError().add(oneLv2);
					}
				}
				
				oneLv1.setItem(String.valueOf(item));
				oneLv1.setWsName(wsName);
				oneLv1.setWsExplain(wsExplain);
				oneLv1.setTransQty(transQty);
				oneLv1.setNotTransQty(notTransQty);
				oneLv1.setStatus(status);
				
				res.getWsErp().add(oneLv1);
				item++;
			}
		}
	}

	private void wsDcp(String customerId,DCP_MonitorDetailQueryRes res) throws Exception {

		String sql=" select a.CUSTOMERID,a.WSNAME,a.WSEXPLAIN,a.STATUS,a.TRANSQTY,a.NOTTRANSQTY,b.ITEM,b.SHOPID,"
				+ " b.SHOPNAME,b.DOCNO,b.ERRORCODE,b.ERRORMSG,b.REQUEST,b.RESPONSE,b.MODIFY_DATE,b.MODIFY_TIME  "
				+ " from dcp_monitor_wsdcp a "
				+ " left join dcp_monitor_wsdcp_error b on a.customerid=b.customerid and a.wsname=b.wsname and b.customerid='"+customerId+"' "
				+ " where a.customerid='"+customerId+"'  order by a.wsname,b.item ";
		List<Map<String, Object>> getQData=this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) 
		{
			Map<String, Boolean> condition = new HashMap<String, Boolean>();
			condition.put("WSNAME", true);
			List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQData, condition);
			int item=1;
			for (Map<String, Object> oneData : getQHeader) 
			{
				DCP_MonitorDetailQueryRes.level1ElmWsDcp oneLv1 = res.new level1ElmWsDcp();
				String wsName = oneData.get("WSNAME").toString();
				String wsExplain = oneData.get("WSEXPLAIN").toString();
				String transQty = oneData.get("TRANSQTY").toString();
				String notTransQty = oneData.get("NOTTRANSQTY").toString();
				String status = oneData.get("STATUS").toString();
				
				oneLv1.setError(new ArrayList<DCP_MonitorDetailQueryRes.level2ElmWsDcpError >());		
				for (Map<String, Object> oneError : getQData) 
				{
					String oneError_wsName = oneError.get("WSNAME").toString();
					String oneError_item = oneError.get("ITEM").toString();
					if (wsName.equals(oneError_wsName) && !Check.Null(oneError_item))
					{
						DCP_MonitorDetailQueryRes.level2ElmWsDcpError oneLv2 = res.new level2ElmWsDcpError();
						String oneError_shopId = oneError.get("SHOPID").toString();
						String oneError_shopName = oneError.get("SHOPNAME").toString();
						String oneError_docNo = oneError.get("DOCNO").toString();
						String oneError_errorCode = oneError.get("ERRORCODE").toString();
						String oneError_errorMsg = oneError.get("ERRORMSG").toString();
						String oneError_modifyDate = oneError.get("MODIFY_DATE").toString();
						String oneError_modifyTime = oneError.get("MODIFY_TIME").toString();

						String oneError_request = "";
						try
						{
							if (oneError.get("REQUEST")!=null && !Check.Null(oneError.get("REQUEST").toString()))   
							{
								oneError_request = oneError.get("REQUEST").toString();
							}
						}
						catch(Exception e)
						{
							oneError_request="" ;
						}

						String oneError_response = "";
						try
						{
							if (oneError.get("RESPONSE")!=null && !Check.Null(oneError.get("RESPONSE").toString()))   
							{
								oneError_response = oneError.get("RESPONSE").toString();
							}
						}
						catch(Exception e)
						{
							oneError_response="" ;
						}

						oneLv2.setItem(oneError_item);
						oneLv2.setShopId(oneError_shopId);
						oneLv2.setShopName(oneError_shopName);
						oneLv2.setDocNo(oneError_docNo);
						oneLv2.setErrorCode(oneError_errorCode);
						oneLv2.setErrorMsg(oneError_errorMsg);
						oneLv2.setRequest(oneError_request);
						oneLv2.setResponse(oneError_response);
						oneLv2.setModifyDate(oneError_modifyDate);
						oneLv2.setModifyTime(oneError_modifyTime);

						oneLv1.getError().add(oneLv2);
					}
				}
				
				oneLv1.setItem(String.valueOf(item));
				oneLv1.setWsName(wsName);
				oneLv1.setWsExplain(wsExplain);
				oneLv1.setTransQty(transQty);
				oneLv1.setNotTransQty(notTransQty);
				oneLv1.setStatus(status);
				
				res.getWsDcp().add(oneLv1);
				item++;
			}
		}
	
	}

	private void service(String customerId,DCP_MonitorDetailQueryRes res) throws Exception {
		String sql=" select * from dcp_monitor_service where customerid='"+customerId+"' ";
		List<Map<String, Object>> getQData=this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) 
		{
			int item=1;
			for (Map<String, Object> oneData : getQData) 
			{
				DCP_MonitorDetailQueryRes.level1ElmService oneLv1 = res.new level1ElmService();
				String serviceId = oneData.get("SERVICEID").toString();
				String serviceName = oneData.get("SERVICENAME").toString();
				String concurrencyQty = oneData.get("CONCURRENCYQTY").toString();
				String errorMsg = oneData.get("ERRORMSG").toString();
				String status = oneData.get("STATUS").toString();

				oneLv1.setItem(String.valueOf(item));
				oneLv1.setServiceId(serviceId);
				oneLv1.setServiceName(serviceName);
				oneLv1.setConcurrencyQty(concurrencyQty);
				oneLv1.setErrorMsg(errorMsg);
				oneLv1.setStatus(status);
				
				res.getService().add(oneLv1);
				item++;
			}
		}
	}

	private void job(String customerId,DCP_MonitorDetailQueryRes res) throws Exception {
		String sql=" select * from dcp_monitor_job where customerid='"+customerId+"' ";
		List<Map<String, Object>> getQData=this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) 
		{
			int item=1;
			for (Map<String, Object> oneData : getQData) 
			{
				DCP_MonitorDetailQueryRes.level1ElmJob oneLv1 = res.new level1ElmJob();
				String jobName = oneData.get("JOBNAME").toString();
				String jobDiscretion = oneData.get("JOBDISCRETION").toString();
				String errorMsg = oneData.get("ERRORMSG").toString();
				String status = oneData.get("STATUS").toString();

				oneLv1.setItem(String.valueOf(item));
				oneLv1.setJobName(jobName);
				oneLv1.setJobDiscretion(jobDiscretion);
				oneLv1.setErrorMsg(errorMsg);
				oneLv1.setStatus(status);
				
				res.getJob().add(oneLv1);
				item++;
			}
		}
	}

	private void shopEDate(String customerId,DCP_MonitorDetailQueryRes res) throws Exception {
		String sql=" select * from dcp_monitor_edate where customerid='"+customerId+"' ";
		List<Map<String, Object>> getQData=this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) 
		{
			int item=1;
			for (Map<String, Object> oneData : getQData) 
			{
				DCP_MonitorDetailQueryRes.level1ElmShopEDate oneLv1 = res.new level1ElmShopEDate();
				String shopId = oneData.get("SHOPID").toString();
				String shopName = oneData.get("SHOPNAME").toString();
				String eDate = oneData.get("EDATE").toString();
				String errorMsg = oneData.get("ERRORMSG").toString();
				String status = oneData.get("STATUS").toString();

				oneLv1.setItem(String.valueOf(item));
				oneLv1.setShopId(shopId);
				oneLv1.setShopName(shopName);
				oneLv1.seteDate(eDate);
				oneLv1.setErrorMsg(errorMsg);
				oneLv1.setStatus(status);
				
				res.getShopEDate().add(oneLv1);
				item++;
			}
		}
	}



}
