package com.dsc.spos.service.imp.json;
import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DingBaseSetUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DingBaseSetUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DingBaseSetUpdateDCP
 * 服务说明： 钉钉基础设置修改
 * @author jinzma
 * @since  2019-10-28
 */

public class DCP_DingBaseSetUpdate extends SPosAdvanceService<DCP_DingBaseSetUpdateReq,DCP_DingBaseSetUpdateRes >{

	@Override
	protected void processDUID(DCP_DingBaseSetUpdateReq req, DCP_DingBaseSetUpdateRes res) throws Exception {
	// TODO 自动生成的方法存根
		String eId = req.geteId();
		String appKey = req.getRequest().getAppKey();
		String appSecret = req.getRequest().getAppSecret();
		String status = req.getRequest().getStatus();
		String corpId = req.getRequest().getCorpId();
		String callBackUrl = req.getRequest().getCallBackUrl();
		String isRegister = req.getRequest().getIsRegister();
		String agentId = req.getRequest().getAgentId();
		
		
		try
		{
			//删除DCP_DING_BASESET
			DelBean db = new DelBean("DCP_DING_BASESET");
			db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db));
			
		  //新增DCP_DING_BASESET
			String[] columns = {"EID", "APPKEY","APPSECRET","STATUS","CORPID","CALLBACKURL","ISREGISTER","AGENTID" };       
			
			DataValue[] insValue = new DataValue[]{
					new DataValue(eId, Types.VARCHAR), 
					new DataValue(appKey, Types.VARCHAR), 
					new DataValue(appSecret, Types.VARCHAR),
					new DataValue(status, Types.VARCHAR),	
					new DataValue(corpId, Types.VARCHAR),	
					new DataValue(callBackUrl, Types.VARCHAR),	
					new DataValue(isRegister, Types.VARCHAR),	
					new DataValue(agentId, Types.VARCHAR),	
			};
			InsBean ib = new InsBean("DCP_DING_BASESET", columns);
			ib.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib)); 
			
			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		}
		catch(Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DingBaseSetUpdateReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DingBaseSetUpdateReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DingBaseSetUpdateReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DingBaseSetUpdateReq req) throws Exception {
	// TODO 自动生成的方法存根
	return false;
	}

	@Override
	protected TypeToken<DCP_DingBaseSetUpdateReq> getRequestType() {
	// TODO 自动生成的方法存根
		return new TypeToken<DCP_DingBaseSetUpdateReq>(){};
	}

	@Override
	protected DCP_DingBaseSetUpdateRes getResponseType() {
	// TODO 自动生成的方法存根
		return new DCP_DingBaseSetUpdateRes();
	}

}
