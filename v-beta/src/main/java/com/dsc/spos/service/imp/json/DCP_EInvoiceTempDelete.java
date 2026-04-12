package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_EInvoiceTempDeleteReq;
import com.dsc.spos.json.cust.res.DCP_EInvoiceTempDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_EInvoiceTempDelete extends SPosAdvanceService<DCP_EInvoiceTempDeleteReq, DCP_EInvoiceTempDeleteRes> {

	@Override
	protected void processDUID(DCP_EInvoiceTempDeleteReq req, DCP_EInvoiceTempDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		
		String templateNo = req.getRequest().getTemplateNo();
		String eId = req.geteId();
		try 
		{
			DelBean db1 = new DelBean("DCP_einvoiceset");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("PTEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			DelBean db2 = new DelBean("DCP_einvoiceset_shop");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("PTEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			
			this.doExecuteDataToDB();	
		} 
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		
		}
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_EInvoiceTempDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_EInvoiceTempDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_EInvoiceTempDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_EInvoiceTempDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuilder errMsg = new StringBuilder();
		if(Check.Null(req.getRequest().getTemplateNo()))
		{
			isFail = true;
			errMsg.append("模板编号不能为空！");
			
		}
		
		if(isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
	return isFail;
	}

	@Override
	protected TypeToken<DCP_EInvoiceTempDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_EInvoiceTempDeleteReq>(){};
	}

	@Override
	protected DCP_EInvoiceTempDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_EInvoiceTempDeleteRes();
	}

}
