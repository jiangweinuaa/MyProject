package com.dsc.spos.service.imp.json;

import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderPlatformAccountDeleteReq;
import com.dsc.spos.json.cust.res.DCP_OrderPlatformAccountDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderPlatformAccountDelete extends SPosAdvanceService<DCP_OrderPlatformAccountDeleteReq,DCP_OrderPlatformAccountDeleteRes> {

	@Override
	protected void processDUID(DCP_OrderPlatformAccountDeleteReq req, DCP_OrderPlatformAccountDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String appKey = req.getAppKey();
		String loadDocType = req.getLoadDocType();
		String execsql = "delete from TA_OUTSALESET where APPKEY='"+appKey+"' and LOAD_DOCTYPE='"+loadDocType+"'";
		if(eId!=null&&eId.isEmpty()==false)
		{
			execsql+=" and EID='"+eId+"'";
		}
		ExecBean execBean = new ExecBean(execsql);
		this.addProcessData(new DataProcessBean(execBean));
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderPlatformAccountDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderPlatformAccountDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderPlatformAccountDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderPlatformAccountDeleteReq req) throws Exception {
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
	protected TypeToken<DCP_OrderPlatformAccountDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderPlatformAccountDeleteReq>(){};
	}

	@Override
	protected DCP_OrderPlatformAccountDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderPlatformAccountDeleteRes();
	}

}
