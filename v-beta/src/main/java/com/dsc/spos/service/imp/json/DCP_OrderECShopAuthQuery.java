package com.dsc.spos.service.imp.json;

import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECShopAuthQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECShopAuthQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.ec.Shopee;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderECShopAuthQuery extends SPosBasicService<DCP_OrderECShopAuthQueryReq, DCP_OrderECShopAuthQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECShopAuthQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECShopAuthQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECShopAuthQueryReq>(){};
	}

	@Override
	protected DCP_OrderECShopAuthQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECShopAuthQueryRes();
	}

	@Override
	protected DCP_OrderECShopAuthQueryRes processJson(DCP_OrderECShopAuthQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderECShopAuthQueryRes res = null;
		res = this.getResponse();
		
		String authType = req.getAuthType();
	    String ecPlatformNo = req.getEcPlatformNo();
	    String apiUrl = req.getApiUrl();
	    String xp_PartnerId = req.getXp_PartnerId();
	    int partnerId = Integer.parseInt(xp_PartnerId);
	    String xp_PartnerKey = req.getXp_PartnerKey();
	    String redirectUrl = req.getRedirectUrl();
	    
	    String authUrl = null;
	    
	    Shopee shopee = new Shopee();
	    if(ecPlatformNo.equals("shopee")  && authType.equals("1")){ //授权
	    	authUrl = shopee.shopAuthPartner(apiUrl, partnerId, xp_PartnerKey, redirectUrl);
	    }
	    if(ecPlatformNo.equals("shopee")  && authType.equals("2")){ //取消授权
	    	authUrl = shopee.shopCancelAuthPartner(apiUrl, partnerId, xp_PartnerKey, redirectUrl);
	    }
	    res.setAuthUrl(authUrl);
	    
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderECShopAuthQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
