package com.dsc.spos.waimai;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.google.gson.reflect.TypeToken;
import eleme.openapi.sdk.api.entity.user.OAuthorizedShop;
import org.json.JSONObject;
import eleme.openapi.sdk.api.entity.user.OUser;
import eleme.openapi.sdk.config.Config;
import eleme.openapi.sdk.oauth.response.Token;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WMELM_ISV_TokenService extends SWaimaiBasicService {
	String shopLogFileName = "ISV_ELMTokenOrShopsLog";
	@Override
	public String execute(String json) throws Exception {
		// TODO Auto-generated method stub

		if (json == null || json.length() == 0) {
			HelpTools.writelog_fileName("【饿了么授权后回传token及商户信息】请求内容为空！" + json, shopLogFileName);
			return null;
		}
		HelpTools.writelog_fileName("【饿了么授权后回传token及商户信息】消息内容:" + json, shopLogFileName);
		try
		{
			JSONObject obj = new JSONObject(json);
			ParseJson pj = new ParseJson();
			String token = obj.optString("token");
			Token token1= null;
			try
			{
				token1=  pj.jsonToBean(token, new TypeToken<Token>(){});
				HelpTools.writelog_fileName("【饿了么授权后回传token及商户信息】token转换成功,accessToken="+token1.getAccessToken(),shopLogFileName);
			}
			catch (Exception e)
			{

			}

			String oUser = obj.optString("oUser");
			OUser oUser1= null;
			try {
				oUser1=  pj.jsonToBean(oUser, new TypeToken<OUser>(){});
				HelpTools.writelog_fileName("【饿了么授权后回传token及商户信息】OUser转换成功:userId="+oUser1.getUserId()+",userName="+oUser1.getUserName(),shopLogFileName);
			}
			catch (Exception e)
			{

			}
			String config = obj.optString("config");
			Config config1= null;
			try
			{
				config1=  pj.jsonToBean(config, new TypeToken<Config>(){});
				HelpTools.writelog_fileName("【饿了么授权后回传token及商户信息】Config转换成功:appKey="+config1.getApp_key()+",appSecret="+config1.getApp_secret()+",apiUrl="+config1.getApiUrl(),shopLogFileName);
			}
			catch (Exception e)
			{

			}
			String state = obj.optString("state");
			HelpTools.writelog_fileName("【饿了么授权后回传token及商户信息】客户端标识state="+state,shopLogFileName);
			String timestamp = obj.optString("timestamp");
			long timestamp_server = 0L;//服务端推送的时间戳
			try
			{
				timestamp_server = Long.parseLong(timestamp);
			}
			catch (Exception e)
			{
				timestamp_server = System.currentTimeMillis();
			}
			//保存或更新token表DCP_ISVWM_ELM_TOKEN
			saveELMTokenUserId(state,config1,token1,oUser1,timestamp_server);
			this.pData.clear();
			//保存或更新商户授权的店铺列表DCP_MAPPINGSHOP
			saveELMShopList(state,config1,oUser1);

		}
		catch (Exception e)
		{
			HelpTools.writelog_fileName("【饿了么授权后回传token及商户信息】处理异常:" + e.getMessage(), shopLogFileName);
		}


		return null;
	}

	@Override
	protected void processDUID(String req, Map<String, Object> res) throws Exception {
	// TODO Auto-generated method stub
	}

	@Override
	protected List<InsBean> prepareInsertData(String req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(String req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(String req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	/**
	 * 授权成功后保存token及商户信息
	 * @param clientNo 客户端唯一标识(对应授权时state)
	 * @param config 配置文件
	 * @param token 授权后获取的token
	 * @param oUser 授权后根据token获取授权商户信息
	 * @param timestamp 服务端请求时间戳
	 * @return
	 * @throws Exception
	 */
	private boolean saveELMTokenUserId(String clientNo,Config config,Token token,OUser oUser,long timestamp) throws Exception
	{
		boolean nRet = false;
		try
		{
			HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新token【开始】", shopLogFileName);
			String appKey = "";
			String appSecret = "";
			if (token!=null&&oUser!=null&&token.getAccessToken()!=null&&!token.getAccessToken().trim().isEmpty())
			{
				long exprieSeconds = token.getExpires();//过期时间秒
				long curTimestamp = timestamp/1000;//当前时间戳，转成秒
				long exprieTimestamp = curTimestamp + exprieSeconds;
				String accessToken = token.getAccessToken();
				String refreshToken = token.getRefreshToken();
				if (config!=null)
				{
					appKey = config.getApp_key();
					appSecret = config.getApp_secret();
				}
				long userId = oUser.getUserId();
				String userName = oUser.getUserName();
				if (userName != null && userName.length() > 255)
				{
					userName = userName.substring(0, 255);
				}
				boolean isNeedUpdateELMToken = true;//是否更新内存中,elmToken
				Map<String,Object> elmToken_DB = getELMTokenByUserId(clientNo,userId+"");
				if (elmToken_DB==null||elmToken_DB.isEmpty())
				{
					HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新token，查询本地不存在，插入DCP_ISVWM_ELM_TOKEN表【开始】", shopLogFileName);
					String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

					String[] columns1 =
							{"CLIENTNO", "USERID","USERNAME","APPKEY", "APPSECRET", "ACCESS_TOKEN", "ACCESS_TOKEN_EXPIRES_IN", "REFRESH_TOKEN", "MEMO",
									"CREATETIME"};
					DataValue[] insValue1 = null;
					insValue1 = new DataValue[]
							{
									new DataValue(clientNo, Types.VARCHAR),
									new DataValue(userId, Types.VARCHAR),
									new DataValue(userName, Types.VARCHAR),
									new DataValue(appKey, Types.VARCHAR),
									new DataValue(appSecret, Types.VARCHAR),
									new DataValue(accessToken, Types.VARCHAR),
									new DataValue(exprieTimestamp, Types.VARCHAR),
									new DataValue(refreshToken, Types.CLOB),
									new DataValue("", Types.VARCHAR),
									new DataValue(createTime, Types.DATE)
							};

					InsBean ib1 = new InsBean("DCP_ISVWM_ELM_TOKEN", columns1);
					ib1.addValues(insValue1);
					this.addProcessData(new DataProcessBean(ib1));
					this.doExecuteDataToDB();
					nRet = true;
					HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新token，查询本地不存在，插入DCP_ISVWM_ELM_TOKEN表【完成】", shopLogFileName);
				}
				else
				{
					String ACCESS_TOKEN_EXPIRES_IN = elmToken_DB.getOrDefault("ACCESS_TOKEN_EXPIRES_IN","").toString();
					long exprieTimestamp_db = 0L;
					try
					{
						exprieTimestamp_db = Long.parseLong(ACCESS_TOKEN_EXPIRES_IN);
					}
					catch (Exception e)
					{

					}
					if (exprieTimestamp_db>exprieTimestamp)
					{
						isNeedUpdateELMToken = false;
						HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新token，查询本地数据库存在，数据库中token过期时间exprieTimestamp_db="+exprieTimestamp_db+"大于服务端回传token过期时间戳exprieTimestamp="+exprieTimestamp+"，暂不更新！！", shopLogFileName);
					}
					else
					{
						HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新token，查询本地存在，更新token【开始】", shopLogFileName);
						String modiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
						String updateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
						UptBean up1 = new UptBean("DCP_ISVWM_ELM_TOKEN");
						up1.addCondition("CLIENTNO",new DataValue(clientNo, Types.VARCHAR));
						up1.addCondition("USERID",new DataValue(userId, Types.VARCHAR));

                        if (userName!=null&&!userName.isEmpty())
						{
							up1.addUpdateValue("USERNAME",new DataValue(userName, Types.VARCHAR));
						}
						if (appKey!=null&&!appKey.isEmpty())
						{
							up1.addUpdateValue("APPKEY",new DataValue(appKey, Types.VARCHAR));
							up1.addUpdateValue("APPSECRET",new DataValue(appSecret, Types.VARCHAR));
						}

						up1.addUpdateValue("ACCESS_TOKEN",new DataValue(accessToken, Types.VARCHAR));
						up1.addUpdateValue("ACCESS_TOKEN_EXPIRES_IN",new DataValue(exprieTimestamp, Types.VARCHAR));
						up1.addUpdateValue("REFRESH_TOKEN",new DataValue(refreshToken, Types.VARCHAR));
						up1.addUpdateValue("LASTMODITIME",new DataValue(modiTime, Types.DATE));
						up1.addUpdateValue("UPDATE_TIME",new DataValue(updateTime, Types.VARCHAR));
						this.addProcessData(new DataProcessBean(up1));
						this.doExecuteDataToDB();
						nRet = true;
						HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新token，查询本地存在，更新token【完成】", shopLogFileName);
					}

				}
				//更新内存中，饿了么token
				if (isNeedUpdateELMToken)
				{
					HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新token，更新内存中token【开始】", shopLogFileName);
					if (WMELMUtilTools.elmTokenListByUserId==null)
					{
						WMELMUtilTools.elmTokenListByUserId = new HashMap<>();
					}
					String userIdStr = userId+"";
					String accessTokenStr = accessToken+"&"+exprieTimestamp;
					WMELMUtilTools.elmTokenListByUserId.put(userIdStr,accessTokenStr);
					HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新token，更新内存中token【完成】", shopLogFileName);
				}
			}

		}
		catch (Exception e)
		{
			HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新token异常："+e.getMessage()+",客户端唯一标识="+clientNo, shopLogFileName);
		}
		return nRet;
	}

	/**
	 * 获取下本地token
	 * @param clientNo
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private Map<String,Object> getELMTokenByUserId(String clientNo,String userId) throws Exception
	{
		try
		{
			String sql =" select * from DCP_ISVWM_ELM_TOKEN where CLIENTNO='"+clientNo+"' and USERID='"+userId+"'";
			HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新token，查询本地sql:"+sql, shopLogFileName);
			List<Map<String, Object>> elmTokenList =this.doQueryData(sql, null);
			if (elmTokenList != null && elmTokenList.isEmpty() == false)
			{
				return elmTokenList.get(0);
			}
		}
		catch (Exception e)
		{
			return null;
		}
		return null;
	}

	/**
	 * 授权成功后保存商户授权的店铺列表
	 * @param clientNo 客户端唯一标识(对应授权时state)
	 * @param config 配置文件
	 * @param oUser 授权后根据token获取授权商户信息
	 * @return
	 * @throws Exception
	 */
	private boolean saveELMShopList(String clientNo,Config config,OUser oUser) throws Exception
	{
		boolean nRet = false;
		try
		{
			HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新授权店铺列表shop【开始】", shopLogFileName);
			String appKey = "";
			String appSecret = "";
			if (oUser!=null)
			{
				if (config!=null)
				{
					appKey = config.getApp_key();
					appSecret = config.getApp_secret();
				}
				long userId = oUser.getUserId();
				String userName = oUser.getUserName();
				if (userName != null && userName.length() > 255)
				{
					userName = userName.substring(0, 255);
				}
				List<OAuthorizedShop> authorizedShops = oUser.getAuthorizedShops();
				if (authorizedShops==null||authorizedShops.isEmpty())
				{
					HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】查询饿了么外卖授权店铺列表shops为空,客户端唯一标识="+clientNo+",授权商户userId="+userId,shopLogFileName);
					return true;
				}
				HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新饿了么外卖授权店铺列【开始】,客户端唯一标识="+clientNo+",授权商户userId="+userId,shopLogFileName);
				String eId = getEIDByClientNo(clientNo);
				if (eId==null||eId.isEmpty())
				{
					eId = " ";
				}
				String customerNo = clientNo;
				if (customerNo==null||customerNo.isEmpty())
				{
					customerNo = " ";
				}
				String erpShopNo = " ";//主键不能为空，没绑定默认空格
				String erpShopName = "";
				String loadDocType = orderLoadDocType.ELEME;
				String channelId = orderLoadDocType.ELEME+"001";//默认渠道ID
				String isTest = "N";
				String isJbp = "Y";//服务商都是Y
				String appName = "饿了么外卖(服务商)";
				String businessId = "2";//外卖默认2
				String memo = "饿了么外卖授权的店铺列表";
				String updateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				String[] columns1 =
						{ "EID","CUSTOMERNO", "ORGANIZATIONNO", "SHOPID", "LOAD_DOCTYPE", "BUSINESSID", "SHOPNAME", "ORDERSHOPNO",
								"ORDERSHOPNAME",  "APPKEY", "APPSECRET", "APPNAME", "ISTEST", "ISJBP","CHANNELID","USERID" };
				DataValue[] insValue1 = null;
				for (OAuthorizedShop elmShop : authorizedShops)
				{
					String orderShopNo = elmShop.getId()+"";
					String orderShopName = elmShop.getName();
					if (orderShopName != null && orderShopName.length() > 255)
					{
						orderShopName = orderShopName.substring(0, 255);
					}

					// 映射的门店资料保存到数据库 存在就更新，不存在就插入
					if (this.IsExistOnlineShop(eId, loadDocType, orderShopNo, businessId))
					{
						UptBean ub1 = new UptBean("DCP_MAPPINGSHOP");
						if (eId != null && eId.trim().isEmpty() == false)
						{
							ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						}
						ub1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
						ub1.addCondition("ORDERSHOPNO", new DataValue(orderShopNo, Types.VARCHAR));
						ub1.addCondition("BUSINESSID", new DataValue(businessId, Types.VARCHAR));

						ub1.addUpdateValue("ORDERSHOPNAME", new DataValue(orderShopName, Types.VARCHAR));
						if (appKey != null && !appKey.isEmpty())
						{
							ub1.addUpdateValue("APPKEY", new DataValue(appKey, Types.VARCHAR));
							ub1.addUpdateValue("APPSECRET", new DataValue(appSecret, Types.VARCHAR));
						}
						//ub1.addUpdateValue("APPNAME", new DataValue(appName, Types.VARCHAR));
						ub1.addUpdateValue("ISTEST", new DataValue(isTest, Types.VARCHAR));
						ub1.addUpdateValue("ISJBP", new DataValue(isJbp, Types.VARCHAR));
						ub1.addUpdateValue("USERID", new DataValue(userId, Types.VARCHAR));
						ub1.addUpdateValue("UPDATE_TIME", new DataValue(updateTime, Types.VARCHAR));
						this.addProcessData(new DataProcessBean(ub1));
						HelpTools.writelog_fileName("循环处理【饿了么外卖授权店铺列表】平台门店ID=" + orderShopNo+",已存在,需要更新，添加更新update语句完成", shopLogFileName);
					}
					else
					{
						insValue1 = new DataValue[]
								{
										new DataValue(eId, Types.VARCHAR),
										new DataValue(customerNo, Types.VARCHAR),
										new DataValue(erpShopNo, Types.VARCHAR), // 组织编号=门店编号
										new DataValue(erpShopNo, Types.VARCHAR), // ERP门店
										new DataValue(loadDocType, Types.VARCHAR), //渠道类型
										new DataValue(businessId, Types.VARCHAR), // 1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
										new DataValue(erpShopName, Types.VARCHAR), // ERP门店名称
										new DataValue(orderShopNo, Types.VARCHAR), // 外卖平台门店ID
										new DataValue(orderShopName, Types.VARCHAR), // 外卖平台门店名称
										new DataValue(appKey, Types.VARCHAR),
										new DataValue(appSecret, Types.VARCHAR),
										new DataValue(appName, Types.VARCHAR),
										new DataValue(isTest, Types.VARCHAR),
										new DataValue(isJbp, Types.VARCHAR),
										new DataValue(channelId, Types.VARCHAR),
										new DataValue(userId, Types.VARCHAR)
								};

						InsBean ib1 = new InsBean("DCP_MAPPINGSHOP", columns1);
						ib1.addValues(insValue1);
						this.addProcessData(new DataProcessBean(ib1));
						HelpTools.writelog_fileName("循环处理【饿了么外卖授权店铺列表】平台门店ID=" + orderShopNo+",不存在,需要新增，添加新增insert语句完成", shopLogFileName);
					}
				}
				this.doExecuteDataToDB();
				nRet = true;
				HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新饿了么外卖授权店铺列【完成】,客户端唯一标识="+clientNo+",授权商户userId="+userId,shopLogFileName);
			}
			else
			{
				HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新授权店铺列表shops【失败】【商户账号信息OUser为空】", shopLogFileName);
			}

		}
		catch (Exception e)
		{
			HelpTools.writelog_fileName("【饿了么【同意授权绑定】回传token及商户信息保存数据库】保存/更新饿了么外卖授权店铺列表【异常】："+e.getMessage()+",客户端唯一标识="+clientNo, shopLogFileName);
		}
		return nRet;
	}

	/**
	 * 获取企业编码eId
	 * @param clientNo 客户端标识
	 * @return
	 * @throws Exception
	 */
	private String getEIDByClientNo (String clientNo) throws Exception
	{
		try
		{
			String sql = " select * from dcp_isvwm_client where CLIENTNO='" + clientNo + "'";
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);
			if (queryDatas != null && queryDatas.isEmpty() == false) {
				return queryDatas.get(0).get("EID").toString();
			}

		} catch (Exception e) {

		}

		return "";
	}

	/**
	 * 线上的门店是否已经存在本地了
	 * @param loadDocType
	 * @param orderShopNO
	 * @param businessID
	 * @return
	 * @throws Exception
	 */
	private boolean IsExistOnlineShop(String eId,String loadDocType,String orderShopNO,String businessID) throws Exception
	{
		boolean isFlag = false;
		String sql = " select * from DCP_MAPPINGSHOP where ";
		sql += "  LOAD_DOCTYPE='"+loadDocType+"' and ORDERSHOPNO='"+orderShopNO+"'";
		if (eId != null && eId.trim().isEmpty() == false)
		{
			sql += " and EID='"+eId+"'";
		}
		if (businessID != null && businessID.isEmpty() == false)
		{
			sql += " and BUSINESSID='"+businessID+"'";
		}
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false)
		{
			isFlag = true;
		}

		return isFlag;

	}

}
