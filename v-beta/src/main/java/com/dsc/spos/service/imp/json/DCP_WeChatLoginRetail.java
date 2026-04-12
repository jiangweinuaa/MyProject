package com.dsc.spos.service.imp.json;

import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import com.dsc.spos.json.cust.req.DCP_LoginRetailReq;
import com.dsc.spos.json.cust.req.DCP_WeChatLoginRetailReq;
import com.dsc.spos.json.cust.res.DCP_WeChatLoginRetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.google.gson.reflect.TypeToken;

public class DCP_WeChatLoginRetail extends SPosBasicService< DCP_WeChatLoginRetailReq,DCP_WeChatLoginRetailRes > {

	@Override
	protected boolean isVerifyFail(DCP_WeChatLoginRetailReq req) throws Exception {
		// TODO 自动生成的方法存根

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String code = req.getCode();

		if (Check.Null(code)) { 
			errMsg.append("微信登录凭证不可为空值, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_WeChatLoginRetailReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_WeChatLoginRetailReq>(){};
	}

	@Override
	protected DCP_WeChatLoginRetailRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_WeChatLoginRetailRes();
	}

	@Override
	protected DCP_WeChatLoginRetailRes processJson(DCP_WeChatLoginRetailReq req) throws Exception {
		// TODO 自动生成的方法存根
		Logger logger = LogManager.getLogger(DCP_WeChatLoginRetail.class.getName());
		DCP_WeChatLoginRetailRes res = this.getResponse();
		int totalRecords=0;								//总笔数
		int totalPages=0;									//总页数
		String code = req.getCode();
		try
		{
			String weChatTakeAppID = "";
			String weChatTakeSecret = "";
			
			String sql= " select item,EID,def from platform_basesettemp where item='weChatTakeAppID' or item='weChatTakeSecret'  ";
			List<Map<String, Object>> getQData = this.doQueryData(sql,null);
			if (getQData != null && getQData.isEmpty() == false)  // 有資料，取得詳細內容
			{
				for (Map<String, Object> oneQData : getQData)
				{
					String item = oneQData.get("ITEM").toString();
					String def = oneQData.get("DEF").toString();
					if (item.equals("weChatTakeAppID"))
					{
						weChatTakeAppID = def;    //小程序唯一标识
					}
					else
					{
						weChatTakeSecret = def;   //小程序的 app secret 
					}
				}
				if (Check.Null(weChatTakeAppID) || Check.Null(weChatTakeSecret))
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "中台参数：微信移动盘点AppID或Secret未设置,");	
				}
				
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "中台参数：微信移动盘点AppID或Secret未设置,");		
			}
			
			//授权（必填）
			String grant_type = "authorization_code";
			////////////////1、向微信服务器 使用登录凭证 code 获取 session_key 和 openid ////////////////
			String params = "appid=" + weChatTakeAppID + "&secret=" + weChatTakeSecret + "&js_code=" + code + "&grant_type=" + grant_type;
			//发送请求
			//String resbody = HttpSend.Sendhttp("GET", "", "https://api.weixin.qq.com/sns/jscode2session?"+params);
			//JSONObject jsonres = new JSONObject(resbody);
			//String session_key = jsonres.optString("session_key");
		  String session_key = "1234567890";
			//String openid = jsonres.optString("openid");   
			//String unionid = jsonres.optString("unionid");  
			//String errcode = jsonres.optString("errcode");  
			//String errmsg = jsonres.optString("errmsg");  
//
//			if ( !Check.Null(errcode) && !errcode.equals("0") )
//			{
//				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "调用微信jscode2session接口失败:"+errmsg);		
//			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			//微信建议Session_key加密后传给小程序，由于小程序没有用到Session_key，所以保存在内存中，传假的给前端
			res.setSession_key("1234567890"); //session_key
			//LoginRetailReq.setS_session_key(session_key);
			res.setWeChatTakeSecret(weChatTakeSecret);
			
			//记录日志，方便调试
			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"*************微信移动盘点登录Session_key: " + session_key + " ***********\r\n");
			
			return res;		
			
		}
		catch(Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_WeChatLoginRetailReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

}
