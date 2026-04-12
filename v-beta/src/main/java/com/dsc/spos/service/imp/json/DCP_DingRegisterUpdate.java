package com.dsc.spos.service.imp.json;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiCallBackDeleteCallBackRequest;
import com.dingtalk.api.request.OapiCallBackGetCallBackRequest;
import com.dingtalk.api.request.OapiCallBackRegisterCallBackRequest;
import com.dingtalk.api.response.OapiCallBackDeleteCallBackResponse;
import com.dingtalk.api.response.OapiCallBackGetCallBackResponse;
import com.dingtalk.api.response.OapiCallBackRegisterCallBackResponse;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DingRegisterUpdateReq;
import com.dsc.spos.json.cust.req.DCP_DingRegisterUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_DingRegisterUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ding.DCP_DingCallBack;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_DingRegisterUpdate
 * 服务说明：钉钉回调地址注册
 * @author jinzma 
 * @since  2019-12-20
 */
public class DCP_DingRegisterUpdate extends SPosAdvanceService<DCP_DingRegisterUpdateReq,DCP_DingRegisterUpdateRes> {

	@Override
	protected void processDUID(DCP_DingRegisterUpdateReq req, DCP_DingRegisterUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		level1Elm request = req.getRequest();
//		String appKey=request.getAppKey();
//		String appSecret = request.getAppSecret();
//		String corpId = request.getCorpId();
		String callBackUrl = request.getCallBackUrl();
		String registerType =request.getRegisterType();
		String eId = req.geteId();
		
		try
		{
			//获取钉钉平台token
			DCP_DingCallBack dingCallBack = new DCP_DingCallBack();
			Map<String,String> accessTokenMap = dingCallBack.getDingAccessToken(dao, eId, true);
			String accessToken = accessTokenMap.getOrDefault("token", "").toString();
			if (Check.Null(accessToken))
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,accessTokenMap.getOrDefault("errmsg", "").toString());
			}
			
			if (registerType.equals("register"))
			{
				//回调地址注册				
				DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/call_back/register_call_back");
				OapiCallBackRegisterCallBackRequest requestReg = new OapiCallBackRegisterCallBackRequest();
				requestReg.setUrl(callBackUrl);
				requestReg.setAesKey("WbTzNwYsSGVK1spNsN1jPwkp2pFNwVpuo1WLPSkaIEA");  //目前在代码里面是写死的
				requestReg.setToken("3934028579");                           //目前在代码里面是写死的
				requestReg.setCallBackTag(Arrays.asList("bpms_task_change", "bpms_instance_change"));
				OapiCallBackRegisterCallBackResponse response = client.execute(requestReg,accessToken);
				Long errcode = response.getErrcode();
				if (errcode==88L||errcode==40014L )
				{
					//获取钉钉平台token
					accessTokenMap = dingCallBack.getDingAccessToken(dao, eId, true);
					accessToken = accessTokenMap.getOrDefault("token", "").toString();
					if (Check.Null(accessToken))
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,accessTokenMap.getOrDefault("errmsg", "").toString());
					}
					response = client.execute(requestReg,accessToken);
				}
				errcode = response.getErrcode();
				String errmsg = response.getErrmsg();
				
				if (errcode!=0L && errcode!=71006L )
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉回调地址注册失败，错误代码："+errcode+" 错误原因："+errmsg);
				}
			}
			else
			{
				
				DingTalkClient  client = new DefaultDingTalkClient("https://oapi.dingtalk.com/call_back/delete_call_back");
				OapiCallBackDeleteCallBackRequest requestReg = new OapiCallBackDeleteCallBackRequest();
				requestReg.setHttpMethod("GET");
				OapiCallBackDeleteCallBackResponse response = client.execute(requestReg,accessToken);
				Long errcode = response.getErrcode();
				if (errcode==88L||errcode==40014L )
				{
					//获取钉钉平台token
					accessTokenMap = dingCallBack.getDingAccessToken(dao, eId, false);
					accessToken = accessTokenMap.getOrDefault("token", "").toString();
					if (Check.Null(accessToken))
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,accessTokenMap.getOrDefault("errmsg", "").toString());
					}
					response = client.execute(requestReg,accessToken);
				}
				errcode = response.getErrcode();
				String errmsg = response.getErrmsg();
				
				if (errcode!=0L )
				{
					//删除接口返回异常时，再调用一次接口查询，判断接口是否存在，不存在就返回删除成功
					client = new DefaultDingTalkClient("https://oapi.dingtalk.com/call_back/get_call_back");
					OapiCallBackGetCallBackRequest requestGet = new OapiCallBackGetCallBackRequest();
					requestGet.setHttpMethod("GET");
					OapiCallBackGetCallBackResponse responseGet = client.execute(requestGet,accessToken);
					
					if (responseGet.getErrcode() == 0L)
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉回调地址删除失败，错误代码："+errcode+" 错误原因："+errmsg);
					}
				}
			}
			
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DingRegisterUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DingRegisterUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DingRegisterUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DingRegisterUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		level1Elm request = req.getRequest();
		if (request!=null )
		{
			if (Check.Null(request.getAppKey())) 
			{
				errMsg.append("应用唯一标识不可为空值, ");
				isFail = true;
			}
			if (Check.Null(request.getAppSecret())) 
			{
				errMsg.append("应用密钥不可为空值, ");
				isFail = true;
			}
			if (Check.Null(request.getCorpId())) 
			{
				errMsg.append("CorpId不可为空值, ");
				isFail = true;
			}
			if (Check.Null(request.getCallBackUrl())) 
			{
				errMsg.append("回调地址不可为空值, ");
				isFail = true;
			}
			if (Check.Null(request.getRegisterType())) 
			{
				errMsg.append("注册类型不可为空值, ");
				isFail = true;
			}
		}
		else
		{
			errMsg.append("request不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;


	}

	@Override
	protected TypeToken<DCP_DingRegisterUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DingRegisterUpdateReq>(){};
	}

	@Override
	protected DCP_DingRegisterUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DingRegisterUpdateRes();
	}

}
