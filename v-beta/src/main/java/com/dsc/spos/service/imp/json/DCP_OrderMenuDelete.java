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
import com.dsc.spos.json.cust.req.DCP_OrderMenuDeleteReq;
import com.dsc.spos.json.cust.res.DCP_OrderMenuDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderMenuDelete extends SPosAdvanceService<DCP_OrderMenuDeleteReq,DCP_OrderMenuDeleteRes> {

	@Override
	protected void processDUID(DCP_OrderMenuDeleteReq req, DCP_OrderMenuDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		
		try 
		{
			String eId = req.geteId();		
			String menuID= req.getMenuID();
			
			DelBean db1 = new DelBean("OC_MENU");
	
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("MENUID", new DataValue(menuID,Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(db1));
					
			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		
		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription(e.getMessage());
	
		}
		
		
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderMenuDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderMenuDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderMenuDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderMenuDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if(Check.Null(req.getMenuID()))
		{
			errMsg.append("菜单ID不可为空值, ");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	  return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderMenuDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderMenuDeleteReq>(){};
	}

	@Override
	protected DCP_OrderMenuDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderMenuDeleteRes();
	}
	
}
