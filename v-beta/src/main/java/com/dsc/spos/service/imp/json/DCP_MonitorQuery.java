package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_MonitorQueryReq;
import com.dsc.spos.json.cust.res.DCP_MonitorQueryRes;
import com.dsc.spos.json.cust.req.DCP_MonitorQueryReq.level1Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DCP_MonitorQuery
 * 服务说明：监控查询
 * @author jinzma 
 * @since  2020-03-18
 */
public class DCP_MonitorQuery extends SPosBasicService<DCP_MonitorQueryReq,DCP_MonitorQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_MonitorQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_MonitorQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_MonitorQueryReq>(){} ;
	}

	@Override
	protected DCP_MonitorQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MonitorQueryRes();
	}

	@Override
	protected DCP_MonitorQueryRes processJson(DCP_MonitorQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;
		DCP_MonitorQueryRes res = this.getResponse();
		try
		{
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;				
			res.setDatas(new ArrayList<DCP_MonitorQueryRes.level1Elm>());
			if (getQData != null && getQData.isEmpty() == false) 
			{
			//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				for (Map<String, Object> oneData : getQData) 
				{
					DCP_MonitorQueryRes.level1Elm oneLv1 = res.new level1Elm();
					
					String customerId = oneData.get("CUSTOMERID").toString();
					String customerName = oneData.get("CUSTOMERNAME").toString();
					String dcpVersion = oneData.get("DCPVERSION").toString();
					String shopQty = oneData.get("SHOPQTY").toString();
					String status = oneData.get("STATUS").toString();
					String eId = oneData.get("EID").toString();
					
					oneLv1.setCustomerId(customerId);
					oneLv1.setCustomerName(customerName);
					oneLv1.setDcpVersion(dcpVersion);
					oneLv1.setShopQty(shopQty);
					oneLv1.setStatus(status);
					oneLv1.seteId(eId);
					
					res.getDatas().add(oneLv1);
				}
			}
			else
			{
				totalRecords = 0;
				totalPages = 0;			
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
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
	protected String getQuerySql(DCP_MonitorQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;	
		StringBuffer sqlbuf=new StringBuffer("");
		level1Elm request = req.getRequest();
		String customerName = request.getCustomerName();
		String status=request.getStatus();
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		
		sqlbuf.append(" SELECT * FROM ( "
			+ " select count(*) over() num,row_number() over (order by a.CUSTOMERNAME) rn,a.* from dcp_monitor a "
			+ " where a.ISHEAD='Y'  " );
		if (!Check.Null(customerName))
		{
			sqlbuf.append(" and CUSTOMERNAME like '%%"+customerName+"%%'");
		}
		if (!Check.Null(status))
		{
			sqlbuf.append(" and status='"+status+"' ");
		}   
		sqlbuf.append(") a"
			+ " WHERE rn>"+startRow+" and rn<="+(startRow+pageSize) );
		
		sql=sqlbuf.toString();
		return sql;		 
	}

}
