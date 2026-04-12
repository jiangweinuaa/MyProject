package com.dsc.spos.service.imp.json;

import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_CCBEnDecryptReq;
import com.dsc.spos.json.cust.res.DCP_CCBEnDecryptRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_CCBEnDecrypt  extends SPosBasicService<DCP_CCBEnDecryptReq,DCP_CCBEnDecryptRes>
{

	@Override
	protected boolean isVerifyFail(DCP_CCBEnDecryptReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_CCBEnDecryptReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_CCBEnDecryptReq>(){};
	}

	@Override
	protected DCP_CCBEnDecryptRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_CCBEnDecryptRes();
	}

	@Override
	protected DCP_CCBEnDecryptRes processJson(DCP_CCBEnDecryptReq req) throws Exception
	{
	 // TODO Auto-generated method stub
		//默认0 表示 加密，1-解密
		String doType=req.getGetType();
		String strSrcParas=req.getStrSrcParas();
		String strKey=req.getStrKey();
	  return null;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_CCBEnDecryptReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
