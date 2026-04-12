package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderPlatformAccountUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformAccountUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderPlatformAccountUpdate
		extends SPosAdvanceService<DCP_OrderPlatformAccountUpdateReq, DCP_OrderPlatformAccountUpdateRes> {

	@Override
	protected void processDUID(DCP_OrderPlatformAccountUpdateReq req, DCP_OrderPlatformAccountUpdateRes res) throws Exception {
		// TODO Auto-generated method stub

		String eId = req.geteId();
		String appKey = req.getAppKey();
		String appSecret = req.getAppSecret();
		String isTest = req.getIsTest();
		String appName = req.getAppName();
		String loadDocType = req.getLoadDocType();
		String appsignkey = req.getAppsignkey();
		String gyShopCode = req.getGyShopCode() == null ? "" : req.getGyShopCode();
		UptBean ub1 = new UptBean("DCP_OUTSALESET");

		ub1.addUpdateValue("APPNAME", new DataValue(appName, Types.VARCHAR));
		ub1.addUpdateValue("APPSECRET", new DataValue(appSecret, Types.VARCHAR));
		ub1.addUpdateValue("ISTEST", new DataValue(isTest, Types.VARCHAR));
		ub1.addUpdateValue("APPSIGNKEY", new DataValue(appsignkey, Types.VARCHAR));
		ub1.addUpdateValue("GYSHOPCODE", new DataValue(gyShopCode, Types.VARCHAR));
		
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
		ub1.addCondition("APPKEY", new DataValue(appKey, Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(ub1));
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderPlatformAccountUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderPlatformAccountUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderPlatformAccountUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderPlatformAccountUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (Check.Null(req.getLoadDocType())) {
			isFail = true;
			errMsg.append("应用类型loadDocType不能为空，");
		}
		if (Check.Null(req.getAppKey())) {
			isFail = true;
			errMsg.append("开发者账号 AppKey不能为空，");
		}
		if (Check.Null(req.getAppSecret())) {
			isFail = true;
			errMsg.append("开发者秘钥AppSecret不能为空，");
		}
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;

	}

	@Override
	protected TypeToken<DCP_OrderPlatformAccountUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderPlatformAccountUpdateReq>() {
		};
	}

	@Override
	protected DCP_OrderPlatformAccountUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderPlatformAccountUpdateRes();
	}

}
