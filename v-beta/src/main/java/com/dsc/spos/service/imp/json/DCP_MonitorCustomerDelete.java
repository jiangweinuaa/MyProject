package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MonitorCustomerDeleteReq;
import com.dsc.spos.json.cust.req.DCP_MonitorCustomerDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_MonitorCustomerDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DCP_MonitorCustomerDelete
 * 服务说明：客户资料删除
 * @author jinzma 
 * @since  2020-03-18
 */
public class DCP_MonitorCustomerDelete extends SPosAdvanceService<DCP_MonitorCustomerDeleteReq,DCP_MonitorCustomerDeleteRes> {

	@Override
	protected void processDUID(DCP_MonitorCustomerDeleteReq req, DCP_MonitorCustomerDeleteRes res) throws Exception {
		// TODO 自动生成的方法存根
		level1Elm request = req.getRequest();
		String customerId = request.getCustomerId();
		try
		{
			if (checkExist(customerId))
			{
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
				
				DelBean db8 = new DelBean("DCP_MONITOR");
				db8.addCondition("CUSTOMERID", new DataValue(customerId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db8));

				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"客户ID:"+customerId +"不存在" );
			}
		}
		catch (Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MonitorCustomerDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MonitorCustomerDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MonitorCustomerDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MonitorCustomerDeleteReq req) throws Exception {
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
	protected TypeToken<DCP_MonitorCustomerDeleteReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_MonitorCustomerDeleteReq>(){};
	}

	@Override
	protected DCP_MonitorCustomerDeleteRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MonitorCustomerDeleteRes();
	}

	private boolean checkExist(String customerId)  throws Exception {
		String sql=" select * from dcp_monitor a where a.customerid='"+customerId+"' and a.ishead='Y'  ";
		boolean exist = false;
		List<Map<String, Object>> getQData = this.doQueryData(sql,null);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}
	
}
