package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderPlatformAccountShopUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformAccountShopUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderPlatformAccountShopUpdate extends SPosAdvanceService<DCP_OrderPlatformAccountShopUpdateReq,DCP_OrderPlatformAccountShopUpdateRes> {

	@Override
	protected void processDUID(DCP_OrderPlatformAccountShopUpdateReq req, DCP_OrderPlatformAccountShopUpdateRes res)
		throws Exception {
	// TODO Auto-generated method stub
		
		String eId = req.geteId();
		String appKey = req.getAppKey();
		String appSecret = req.getAppSecret();
		String isTest = req.getIsTest();
		String appName = req.getAppName();
		String loadDocType = req.getLoadDocType();
		String appsignkey = req.getAppsignkey();
		String url = req.getUrl();
		String gyShopCode = req.getGyShopCode();
		
		String execsql = "delete from TA_OUTSALESET where APPKEY='"+appKey+"' and LOAD_DOCTYPE='"+loadDocType+"' and SHOPID<>'ALL'";
		if(eId!=null&&eId.isEmpty()==false)
		{
			execsql+=" and EID='"+eId+"'";
		}
		ExecBean execBean = new ExecBean(execsql);
		this.addProcessData(new DataProcessBean(execBean));
		String[] rows1= {
				"EID",
				"SHOPID",
				"LOAD_DOCTYPE",
				"APPKEY",
				"APPSECRET",
				"ISTEST",
				"APPNAME",
				"APPSIGNKEY",
				"STATUS",
				"URL",
				"GYSHOPCODE"
				};
		for (DCP_OrderPlatformAccountShopUpdateReq.level1Elm oneLv1 : req.getDatas())
		{
			DataValue[] insValue1 = null;
			insValue1 = new DataValue[]
				{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(oneLv1.getShopId(), Types.VARCHAR),
						new DataValue(loadDocType, Types.VARCHAR),
						new DataValue(appKey, Types.VARCHAR),
						new DataValue(appSecret, Types.VARCHAR),
						new DataValue(isTest, Types.VARCHAR),
						new DataValue(appName, Types.VARCHAR),
						new DataValue(appsignkey, Types.VARCHAR),
						new DataValue("100", Types.VARCHAR),
						new DataValue(url, Types.VARCHAR),
						new DataValue(gyShopCode, Types.VARCHAR)
				};
			InsBean ib1 = new InsBean("DCP_OUTSALESET", rows1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1));
			
		}
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");	
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderPlatformAccountShopUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderPlatformAccountShopUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderPlatformAccountShopUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderPlatformAccountShopUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(Check.Null(req.getLoadDocType()))
	  {
	  	isFail = true;
	  	errMsg.append("应用类型loadDocType不能为空，");
	  }
		if(Check.Null(req.getAppKey()))
	  {
	  	isFail = true;
	  	errMsg.append("开发者账号 AppKey不能为空，");
	  }
	  /*if(Check.Null(req.getAppSecret()))
	  {
	  	isFail = true;
	  	errMsg.append("开发者秘钥AppSecret不能为空，");
	  }*/
	  if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}	  
		
		
	return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderPlatformAccountShopUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderPlatformAccountShopUpdateReq>(){};
	}

	@Override
	protected DCP_OrderPlatformAccountShopUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderPlatformAccountShopUpdateRes();
	}

}
