package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MappingPaymentUpdateReq;
import com.dsc.spos.json.cust.res.DCP_MappingPaymentUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_MappingPaymentUpdate extends SPosAdvanceService<DCP_MappingPaymentUpdateReq, DCP_MappingPaymentUpdateRes> {

	@Override
	protected void processDUID(DCP_MappingPaymentUpdateReq req, DCP_MappingPaymentUpdateRes res) throws Exception 
	{
		// TODO Auto-generated method stub

		String eId = req.geteId();
		String docType = req.getRequest().getLoadDocType();
		String ERP_payCode = req.getRequest().getERP_payCode();
		String order_payCode = req.getRequest().getOrder_payCode();
		
		//规格文档上写的只可修改：status，order_payType,order_payName 
		String status = req.getRequest().getStatus();
		//String ERP_payName = req.getRequest().getERP_payName();
		//String ERP_payType = req.getRequest().getERP_payType();
		String order_payName = req.getRequest().getOrder_payName();
		String order_payType = req.getRequest().getERP_payType();

		UptBean up1 = new UptBean("DCP_MAPPINGPAYMENT");
		up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		up1.addCondition("LOAD_DOCTYPE", new DataValue(docType, Types.VARCHAR));
		up1.addCondition("PAYCODE", new DataValue(ERP_payCode, Types.VARCHAR));
		up1.addCondition("ORDER_PAYCODE", new DataValue(order_payCode, Types.VARCHAR));
		boolean isNeedUpdate = false;
		if(status!=null&&status.isEmpty()==false)
		{
			up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			isNeedUpdate = true;
		}
		if(order_payName!=null)
		{
			up1.addUpdateValue("ORDER_PAYNAME", new DataValue(order_payName, Types.VARCHAR));
			isNeedUpdate = true;
		}
		if(order_payType!=null)
		{
			up1.addUpdateValue("ORDER_PAYTYPE", new DataValue(order_payType, Types.VARCHAR));
			isNeedUpdate = true;
		}
		
		if(isNeedUpdate)
		{
			this.addProcessData(new DataProcessBean(up1));
			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		else
		{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("没有传可更新的节点值，请检查传值！");
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MappingPaymentUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MappingPaymentUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MappingPaymentUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MappingPaymentUpdateReq req) throws Exception {
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
	protected TypeToken<DCP_MappingPaymentUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MappingPaymentUpdateReq>(){};
	}

	@Override
	protected DCP_MappingPaymentUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MappingPaymentUpdateRes();
	}
	
}
