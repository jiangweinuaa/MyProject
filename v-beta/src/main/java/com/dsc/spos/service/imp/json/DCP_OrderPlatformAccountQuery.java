package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderPlatformAccountQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformAccountQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderPlatformAccountQuery extends SPosBasicService<DCP_OrderPlatformAccountQueryReq, DCP_OrderPlatformAccountQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderPlatformAccountQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderPlatformAccountQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderPlatformAccountQueryReq>() {
		};
	}

	@Override
	protected DCP_OrderPlatformAccountQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderPlatformAccountQueryRes();
	}

	@Override
	protected DCP_OrderPlatformAccountQueryRes processJson(DCP_OrderPlatformAccountQueryReq req) throws Exception {
		String keyText = req.getKeyTxt();
		String loadDocType = req.getLoadDocType();
		String eId = req.geteId();
		String accountType = req.getAccountType();// 0-外卖应用传0的时候只返回loadDocType=1~3的，
													// 1-外卖物料
													// 传1返回loadDocType=3之后的
		StringBuffer sb =new StringBuffer( " select * from ta_outsaleset where SHOPID='ALL' and EID='" + eId + "'");

		if (loadDocType != null && loadDocType.length() > 0) {
			sb.append( " and LOAD_DOCTYPE='" + loadDocType + "'");
		} else// loadoctype都给值不用判断accountType
		{
			if (accountType != null && accountType.length() > 0) {
				if (accountType.equals("0")) {
					sb.append( " and to_number(load_doctype)<=3");
				} else if (accountType.equals("1")) {
					sb.append( " and to_number(load_doctype)>3");
				} else {

				}
			}

		}

		if (keyText != null && keyText.length() > 0) {
			sb.append( " and (APPNAME like '%%" + keyText + "%%'  OR APPKEY like '%%" + keyText + "%%')");
		}
		List<Map<String, Object>> getQData = this.doQueryData(sb.toString(), null);
		DCP_OrderPlatformAccountQueryRes res = this.getResponse();
		res.setDatas(new ArrayList<DCP_OrderPlatformAccountQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) {
			for (Map<String, Object> map : getQData) {
				DCP_OrderPlatformAccountQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String loadDocType1 = map.get("LOAD_DOCTYPE").toString();
				String appKey = map.get("APPKEY").toString();
				String appSecret = map.get("APPSECRET").toString();
				String appName = map.get("APPNAME").toString();
				String isTest = map.get("ISTEST").toString();
				String appsignkey = map.get("APPSIGNKEY") == null ? "" : map.get("APPSIGNKEY").toString();
				String url = map.get("URL") == null ? "" : map.get("URL").toString();
				String gyShopCode = map.get("GYSHOPCODE") == null ? "" : map.get("GYSHOPCODE").toString();
				oneLv1.setAppKey(appKey);
				oneLv1.setAppName(appName);
				oneLv1.setAppSecret(appSecret);
				oneLv1.setLoadDocType(loadDocType1);
				oneLv1.setIsTest(isTest);
				oneLv1.setAppsignkey(appsignkey);
				oneLv1.setUrl(url);
				oneLv1.setGyShopCode(gyShopCode);
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
	protected String getQuerySql(DCP_OrderPlatformAccountQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
