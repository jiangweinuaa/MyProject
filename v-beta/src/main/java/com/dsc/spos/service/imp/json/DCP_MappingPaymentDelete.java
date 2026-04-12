package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MappingPaymentDeleteReq;
import com.dsc.spos.json.cust.res.DCP_MappingPaymentDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_MappingPaymentDelete extends SPosAdvanceService<DCP_MappingPaymentDeleteReq, DCP_MappingPaymentDeleteRes> {

	@Override
	protected void processDUID(DCP_MappingPaymentDeleteReq req, DCP_MappingPaymentDeleteRes res) throws Exception 
	{
		// TODO Auto-generated method stub

		String eId = req.geteId();
		String docType = req.getRequest().getLoadDocType();
		String ERP_payCode = req.getRequest().getERP_payCode();
		String order_payCode = req.getRequest().getOrder_payCode();

		DelBean db1 = new DelBean("DCP_MAPPINGPAYMENT");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("LOAD_DOCTYPE", new DataValue(docType, Types.VARCHAR));
		db1.addCondition("PAYCODE", new DataValue(ERP_payCode, Types.VARCHAR));
		db1.addCondition("ORDER_PAYCODE", new DataValue(order_payCode, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MappingPaymentDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MappingPaymentDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MappingPaymentDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MappingPaymentDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String docType = req.getRequest().getLoadDocType();
		String ERP_payCode = req.getRequest().getERP_payCode();		
		String order_payCode = req.getRequest().getOrder_payCode();
		

		if (Check.Null(docType)) 
		{
			errMsg.append("平台类型不可为空值, ");
			isFail = true;
		}
		if (Check.Null(ERP_payCode) ) 
		{
			errMsg.append("ERP支付方式不可为空值, ");
			isFail = true;
		}		
		if (Check.Null(order_payCode) ) 
		{
			errMsg.append("平台支付方式不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_MappingPaymentDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MappingPaymentDeleteReq>(){};
	}

	@Override
	protected DCP_MappingPaymentDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MappingPaymentDeleteRes();
	}
	
}
