package com.dsc.spos.service.imp.json;

import java.util.List;
import java.util.Map;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dsc.spos.json.cust.req.DCP_DingPlatformUserQueryReq;
import com.dsc.spos.json.cust.res.DCP_DingPlatformUserQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ding.DCP_DingCallBack;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DingPlatformUserGetDCP
 * 服务说明： 钉钉平台用户查询
 * @author jinzma
 * @since  2019-10-28
 */

public class DCP_DingPlatformUserQuery  extends SPosBasicService<DCP_DingPlatformUserQueryReq,DCP_DingPlatformUserQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_DingPlatformUserQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String userId=req.getRequest().getUserId();
		if (Check.Null(userId)) 
		{
			errMsg.append("钉钉用户ID不可为空值, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DingPlatformUserQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DingPlatformUserQueryReq>(){};
	}

	@Override
	protected DCP_DingPlatformUserQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DingPlatformUserQueryRes();
	}

	@Override
	protected DCP_DingPlatformUserQueryRes processJson(DCP_DingPlatformUserQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		DCP_DingPlatformUserQueryRes res = this.getResponse();
		String eId = req.geteId();
		String userId=req.getRequest().getUserId();
		String userName="";
		String deptId="";
		try
		{
				//调用钉钉平台获取accessToken
			DCP_DingCallBack dingCallBack = new DCP_DingCallBack();
			Map<String,String> accessTokenMap = dingCallBack.getDingAccessToken(dao, eId, false);
			String accessToken = accessTokenMap.getOrDefault("token", "").toString();
			if (Check.Null(accessToken))
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,accessTokenMap.getOrDefault("errmsg", "").toString());
			}

				//调用钉钉平台查询 userId
				DingTalkClient userClient = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get");
				OapiUserGetRequest userRequest = new OapiUserGetRequest();
				userRequest.setUserid(userId);
				userRequest.setHttpMethod("GET");
				OapiUserGetResponse userResponse = userClient.execute(userRequest, accessToken);	
				Long errcode = userResponse.getErrcode();
				
				//accessToken异常时，强制刷新并重新调用
				if (errcode==88L ||errcode==40014L )
				{
					//获取钉钉平台token
					accessTokenMap = dingCallBack.getDingAccessToken(dao, eId, true);
					accessToken = accessTokenMap.getOrDefault("token", "").toString();
					if (Check.Null(accessToken))
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,accessTokenMap.getOrDefault("errmsg", "").toString());
					}
					userResponse = userClient.execute(userRequest, accessToken);	
				}
				
				errcode = userResponse.getErrcode();
				userName = userResponse.getName();
				if (errcode!=0L)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉平台返回： 用户:"+userId+"查询失败！错误代码："+errcode+" 错误原因:"+userResponse.getErrmsg());
				}

				List<Long> deptId_list = userResponse.getDepartment();
			  long deptId_long = deptId_list.get(0).longValue();
			  deptId = String.valueOf(deptId_long); 

				res.setUserId(userId);
				res.setUserName(userName);
				res.setDeptId(deptId);
				return res;  
				
		}
		catch  (Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}


	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_DingPlatformUserQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

}
