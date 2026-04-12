package com.dsc.spos.service.imp.json;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_DingBaseSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_DingBaseSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DingBaseSetGetDCP
 * 服务说明： 钉钉基础设置查询
 * @author jinzma
 * @since  2019-10-28
 */
public class DCP_DingBaseSetQuery extends SPosBasicService<DCP_DingBaseSetQueryReq,DCP_DingBaseSetQueryRes > {

	@Override
	protected boolean isVerifyFail(DCP_DingBaseSetQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_DingBaseSetQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DingBaseSetQueryReq>(){};
	}

	@Override
	protected DCP_DingBaseSetQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DingBaseSetQueryRes();
	}

	@Override
	protected DCP_DingBaseSetQueryRes processJson(DCP_DingBaseSetQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		DCP_DingBaseSetQueryRes res = this.getResponse();
		try
		{
			String sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);

			String appKey= "";
			String appSecret ="";
			String status ="";
			String corpId ="";
			String callBackUrl ="";
			String isRegister ="";
			String agentId="";

			if (getQData != null && getQData.isEmpty() == false) 
			{
				appKey = getQData.get(0).getOrDefault("APPKEY", "").toString() ;
				appSecret = getQData.get(0).getOrDefault("APPSECRET", "").toString() ;
				status = getQData.get(0).getOrDefault("STATUS", "").toString() ;
				corpId = getQData.get(0).getOrDefault("CORPID", "").toString() ;
				callBackUrl = getQData.get(0).getOrDefault("CALLBACKURL", "").toString() ;
				isRegister = getQData.get(0).getOrDefault("ISREGISTER", "").toString() ;	
				agentId = getQData.get(0).getOrDefault("AGENTID", "").toString() ;	 
			}

			res.setAppKey(appKey);
			res.setAppSecret(appSecret);
			res.setStatus(status);
			res.setCorpId(corpId);
			res.setCallBackUrl(callBackUrl);
			res.setIsRegister(isRegister);
			res.setAgentId(agentId);

			return res;					

		}
		catch (Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}



	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_DingBaseSetQueryReq req) throws Exception {
		// TODO 自动生成的方法存根

		String eId = req.geteId();
		String sql = " select  * from DCP_DING_BASESET where EID='"+eId+"' ";
		return sql;
	}

}
