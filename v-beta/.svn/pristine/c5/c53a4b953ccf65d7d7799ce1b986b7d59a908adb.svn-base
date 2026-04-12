package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderPlatformAccountShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformAccountShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderPlatformAccountShopQuery extends SPosBasicService<DCP_OrderPlatformAccountShopQueryReq,DCP_OrderPlatformAccountShopQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderPlatformAccountShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		
		boolean isFalse = false;
		StringBuilder error = new StringBuilder();
		if(Check.Null(req.getAppKey()))
		{
			isFalse = true;
			error.append("应用的账号appKey不能为空，");
		}
		
		if(Check.Null(req.getLoadDocType()))
		{
			isFalse = true;
			error.append("应用类型loadDocType不能为空，");
		}
		
		if (isFalse)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, error.toString());
		}
		
	return isFalse;
	}

	@Override
	protected TypeToken<DCP_OrderPlatformAccountShopQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderPlatformAccountShopQueryReq>(){};
	}

	@Override
	protected DCP_OrderPlatformAccountShopQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderPlatformAccountShopQueryRes();
	}

	@Override
	protected DCP_OrderPlatformAccountShopQueryRes processJson(DCP_OrderPlatformAccountShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String loadDocType = req.getLoadDocType();
		String eId = req.geteId();
		String appKey = req.getAppKey();
		String langType = req.getLangType();
		String sql = " select * from (" ;
		sql += " select A.*,B.ORG_NAME as shopname from ta_outsaleset A left join DCP_ORG_lang B on A.EID=B.EID and A.SHOPID=B.ORGANIZATIONNO where A.SHOPID<>'ALL' and A.EID='"+eId+"' and B.LANG_TYPE='"+langType+"'";
		sql +=" and A.LOAD_DOCTYPE='"+loadDocType+"' AND A.APPKEY='"+appKey+"'";
		
		sql += ")";
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		DCP_OrderPlatformAccountShopQueryRes res = this.getResponse();
		res.setDatas(new ArrayList<DCP_OrderPlatformAccountShopQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false)
		{
			
			for (Map<String, Object> map : getQData) 
			{
				DCP_OrderPlatformAccountShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setShopId(map.get("SHOPID").toString());
				oneLv1.setShopName(map.get("SHOPNAME").toString());
				res.getDatas().add(oneLv1);		
				oneLv1 = null;
		  }
		}
		
		
	  return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_OrderPlatformAccountShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
