package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ServerCurDateQueryReq;
import com.dsc.spos.json.cust.res.DCP_ServerCurDateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_ServerCurDateQuery extends SPosBasicService<DCP_ServerCurDateQueryReq,DCP_ServerCurDateQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_ServerCurDateQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_ServerCurDateQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ServerCurDateQueryReq>(){};
	}

	@Override
	protected DCP_ServerCurDateQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ServerCurDateQueryRes();
	}

	@Override
	protected DCP_ServerCurDateQueryRes processJson(DCP_ServerCurDateQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String curTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
		
		DCP_ServerCurDateQueryRes res = this.getResponse();
		res.setCurDate(curDate);
		res.setCurTime(curTime);
		
	  return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_ServerCurDateQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
