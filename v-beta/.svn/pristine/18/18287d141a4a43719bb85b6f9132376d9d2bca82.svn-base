package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_WarningCheckReq;
import com.dsc.spos.json.cust.req.DCP_WarningCheckReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_WarningCheckRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_WarningCheck extends SPosAdvanceService<DCP_WarningCheckReq,DCP_WarningCheckRes> {

	@Override
	protected void processDUID(DCP_WarningCheckReq req, DCP_WarningCheckRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String lastmodiname = req.getOpName();
		String lastmodiid = req.getOpNO();
		level1Elm requestModel =	req.getRequest();
		String billNo = requestModel.getBillNo();
		String actionType = requestModel.getActionType();
		String status = "0";
		
		if(actionType.equals("valid"))
		{
			status = "100";
		}
		else if(actionType.equals("invalid"))
		{
			status = "0";
		}
		
		UptBean ub1 = null;	
		ub1 = new UptBean("DCP_Warning");		
	  // condition
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
		
		ub1.addUpdateValue("STATUS",new DataValue(status, Types.VARCHAR));
		ub1.addUpdateValue("LASTMODIOPID",new DataValue(lastmodiid, Types.VARCHAR));
		ub1.addUpdateValue("LASTMODIOPNAME",new DataValue(lastmodiname, Types.VARCHAR));
		ub1.addUpdateValue("LASTMODITIME",new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE));
		
		this.addProcessData(new DataProcessBean(ub1));
					
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_WarningCheckReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_WarningCheckReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_WarningCheckReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_WarningCheckReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_WarningCheckReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_WarningCheckReq>(){};
	}

	@Override
	protected DCP_WarningCheckRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_WarningCheckRes();
	}

}
