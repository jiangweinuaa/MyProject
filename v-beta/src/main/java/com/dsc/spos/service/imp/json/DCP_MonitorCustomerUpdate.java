package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MonitorCustomerUpdateReq;
import com.dsc.spos.json.cust.req.DCP_MonitorCustomerUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_MonitorCustomerUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DCP_MonitorCustomerUpdate
 * 服务说明：客户资料修改
 * @author jinzma 
 * @since  2020-03-18
 */
public class DCP_MonitorCustomerUpdate extends SPosAdvanceService<DCP_MonitorCustomerUpdateReq,DCP_MonitorCustomerUpdateRes> {

	@Override
	protected void processDUID(DCP_MonitorCustomerUpdateReq req, DCP_MonitorCustomerUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		level1Elm request = req.getRequest();
		String customerId = request.getCustomerId();
		String customerName = request.getCustomerName();
		String contact = request.getContact();
		String telephone = request.getTelephone();
		String dcpConnInfo = request.getDcpConnInfo();
		String dcpUrl = request.getDcpUrl();
		try
		{
			if (checkExist(customerId))
			{
				UptBean ub = new UptBean("DCP_MONITOR");
				ub.addUpdateValue("CUSTOMERNAME", new DataValue(customerName, Types.VARCHAR));
				ub.addUpdateValue("CONTACT", new DataValue(contact, Types.VARCHAR));
				ub.addUpdateValue("TELEPHONE", new DataValue(telephone, Types.VARCHAR));
				ub.addUpdateValue("DCPCONNINFO", new DataValue(dcpConnInfo, Types.VARCHAR));
				ub.addUpdateValue("DCPURL", new DataValue(dcpUrl, Types.VARCHAR));

				// condition
				ub.addCondition("CUSTOMERID", new DataValue(customerId, Types.VARCHAR));
				ub.addCondition("ISHEAD", new DataValue("Y", Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub));

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
	protected List<InsBean> prepareInsertData(DCP_MonitorCustomerUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MonitorCustomerUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MonitorCustomerUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MonitorCustomerUpdateReq req) throws Exception {
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
	protected TypeToken<DCP_MonitorCustomerUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_MonitorCustomerUpdateReq>(){};
	}

	@Override
	protected DCP_MonitorCustomerUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_MonitorCustomerUpdateRes();
	}

	private boolean checkExist(String customerId)  throws Exception {
		String sql=" select * from dcp_monitor a where a.customerid='"+customerId+"'  and a.ishead='Y'  ";
		boolean exist = false;
		List<Map<String, Object>> getQData = this.doQueryData(sql,null);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}
}
