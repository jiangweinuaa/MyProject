package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dsc.spos.json.cust.req.DCP_PosProcessTaskQueryReq;
import com.dsc.spos.json.cust.res.DCP_PFOrderOldQueryRes;
import com.dsc.spos.json.cust.res.DCP_PosProcessTaskQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * POS 查询加工任务
 * @author yuanyy 2019-09-17
 *
 */
public class DCP_PosProcessTaskQuery extends SPosBasicService<DCP_PosProcessTaskQueryReq, DCP_PosProcessTaskQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PosProcessTaskQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PosProcessTaskQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PosProcessTaskQueryReq>(){};
	}

	@Override
	protected DCP_PosProcessTaskQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PosProcessTaskQueryRes();
	}

	@Override
	protected DCP_PosProcessTaskQueryRes processJson(DCP_PosProcessTaskQueryReq req) throws Exception {
		// TODO Auto-generated method stub
	
		return null;
		
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PosProcessTaskQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
