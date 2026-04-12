package com.dsc.spos.service.imp.json;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ModularWorkActiveReq;
import com.dsc.spos.json.cust.res.DCP_ModularWorkActiveRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_ModularWorkActive extends SPosBasicService<DCP_ModularWorkActiveReq,DCP_ModularWorkActiveRes> {

	@Override
	protected boolean isVerifyFail(DCP_ModularWorkActiveReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}
//	@Override
//	protected boolean AuthCheck(DCP_ModularWorkActiveReq req) throws Exception {
//	// TODO Auto-generated method stub
//	return true;
//	}
	@Override
	protected TypeToken<DCP_ModularWorkActiveReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ModularWorkActiveReq>(){};
	}

	@Override
	protected DCP_ModularWorkActiveRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ModularWorkActiveRes();
	}

	@Override
	protected DCP_ModularWorkActiveRes processJson(DCP_ModularWorkActiveReq req) throws Exception {
	// TODO Auto-generated method stub		
		DCP_ModularWorkActiveRes res = this.getResponse();
	  return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_ModularWorkActiveReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
