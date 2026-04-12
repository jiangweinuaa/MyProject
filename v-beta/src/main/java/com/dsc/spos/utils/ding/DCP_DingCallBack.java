package com.dsc.spos.utils.ding;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptException;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptor;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;

/**
 * 服务函数：DCP_DingCallBack
 * 服务说明：钉钉回调接口处理（回调接口地址注册和回调事件处理）
 * @author jinzma	 
 * @since  2019-12-27
 */

public class DCP_DingCallBack {

	//钉钉accessToken time/token
	public static Map<String,Object> dingAccessTokenMap = new HashMap<String,Object>() ;

	/**
	 * 钉钉回调接口解析
	 * @param request 
	 * @param json
	 * @param dao
	 * @return ""
	 * @throws Exception
	 */
	public Map<String,String> execute(HttpServletRequest request,String json,DsmDAO dao) throws Exception
	{
		Map<String,String> jsonMap = null;
		try
		{	
			HelpTools.writelog_fileName("\r\n 钉钉推送密文: " + json + "\r\n" , "dingding");
			/**获取 encrypt **/
			JSONObject body = new JSONObject(json);
			String encrypt = body.get("encrypt").toString();

			/**对encrypt进行解密**/
			String plainText = "";
			String timeStamp= request.getParameter("timestamp");
			String msgSignature = request.getParameter("signature");
			String nonce = request.getParameter("nonce");

			/**获取corpId**/
			String corpId=" ";
			String sql="select CORPID from DCP_DING_BASESET ";
			List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
			if (getQData != null && getQData.isEmpty() == false)
			{
				corpId=getQData.get(0).get("CORPID").toString();
				if (!Check.Null(corpId))
				{
					try 
					{
						/**对返回信息进行解密**/
						DingTalkEncryptor dingTalkEncryptor = new DingTalkEncryptor("3934028579","WbTzNwYsSGVK1spNsN1jPwkp2pFNwVpuo1WLPSkaIEA", corpId);
						plainText = dingTalkEncryptor.getDecryptMsg(msgSignature, timeStamp, nonce, encrypt);
						HelpTools.writelog_fileName("\r\n 钉钉推送明文: " + plainText + "\r\n" , "dingding");

						/**对返回信息进行加密**/
						long timeStampLong = Long.parseLong(timeStamp);
						jsonMap = dingTalkEncryptor.getEncryptedMap("success", timeStampLong, nonce);
					} 
					catch (DingTalkEncryptException e) 
					{
						// TODO Auto-generated catch block
						HelpTools.writelog_fileName("\r\n 钉钉加解密处理异常: " + e.getMessage() + "\r\n" , "dingding");
						jsonMap=null;
						return jsonMap;
					}

					try 
					{
						/**对encrypt解密出来的明文进行业务处理**/
						JSONObject plainTextJson = new JSONObject(plainText);       
						String eventType = plainTextJson.getString("EventType");
						String processId="";
						String type="";
						long finishTimeLong=0L;
						Date finishTimeDate= new Date();
						SimpleDateFormat timeSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String checkTime="";
						switch (eventType)
						{
						case "bpms_instance_change":  //审批实例开始，结束  申请方主动撤销
							processId  = plainTextJson.getString("processInstanceId");
							type  = plainTextJson.getString("type");
							if (type.equals("terminate"))  //申请人发起撤销
							{
								finishTimeLong = plainTextJson. optLong("finishTime");
								finishTimeDate = new Date(finishTimeLong);
								checkTime =  timeSDF.format(finishTimeDate);
								//查询审批单
								sql="select * from DCP_DING_APPROVE where PROCESSID='"+processId+"' " ;
								getQData.clear();
								getQData = dao.executeQuerySQL(sql, null);
								if (getQData != null && getQData.isEmpty() == false)
								{
									String eId = getQData.get(0).get("EID").toString();
									String checkOpId = getQData.get(0).get("CREATEOPID").toString();
									String checkOpName = getQData.get(0).get("CREATEOPNAME").toString();

									List<DataProcessBean> data = new ArrayList<DataProcessBean>();
									UptBean up=new UptBean("DCP_DING_APPROVE");
									up.addUpdateValue("CHECKOPID", new DataValue(checkOpId, Types.VARCHAR));
									up.addUpdateValue("CHECKOPNAME", new DataValue(checkOpName, Types.VARCHAR));
									up.addUpdateValue("CHECKTIME", new DataValue(checkTime, Types.DATE));
									up.addUpdateValue("STATUS", new DataValue("3", Types.VARCHAR));

									if (!Check.Null(eId))
									{
										up.addCondition("EID", new DataValue(eId, Types.VARCHAR));
									}
									up.addCondition("PROCESSID", new DataValue(processId, Types.VARCHAR));

									data.add(new DataProcessBean(up));
									dao.useTransactionProcessData(data);
									data.clear();
								}	
							}

						case "bpms_task_change":     //审批任务开始，结束，转交
							processId  = plainTextJson.getString("processInstanceId");
							type  = plainTextJson.getString("type");
							if (type.equals("finish"))
							{
								String result  = plainTextJson.getString("result");
								String staffId  = plainTextJson.getString("staffId");
								String remark = plainTextJson.optString("remark", ""); 
								finishTimeLong = plainTextJson.optLong("finishTime");
								finishTimeDate = new Date(finishTimeLong);
								checkTime =  timeSDF.format(finishTimeDate);
								String status="";								
								
								if (result.equals("agree")) status="1";  //状态：1审核同意
								if (result.equals("refuse")) status="2"; //状态：2审批驳回
								sql="select OPNO,USERNAME,USERID,EID from DCP_DING_USERSET where userid ='"+staffId+"' " ;
								getQData.clear();
								getQData = dao.executeQuerySQL(sql, null);
								String checkOpId="";
								String checkOpName="";
								String eId="";
								if (getQData != null && getQData.isEmpty() == false)
								{
									checkOpId=getQData.get(0).get("USERID").toString();
									checkOpName=getQData.get(0).get("USERNAME").toString();
									eId = getQData.get(0).get("EID").toString();
								}

								if (Check.Null(eId))
								{
									//查询审批单
									sql="select EID from DCP_DING_APPROVE where PROCESSID='"+processId+"' " ;
									getQData.clear();
									getQData = dao.executeQuerySQL(sql, null);
									if (getQData != null && getQData.isEmpty() == false)
									{
										eId = getQData.get(0).get("EID").toString();
									}
								}
								//回写审批单
								List<DataProcessBean> data = new ArrayList<DataProcessBean>();
								UptBean up=new UptBean("DCP_DING_APPROVE");
								up.addUpdateValue("CHECKOPID", new DataValue(checkOpId, Types.VARCHAR));
								up.addUpdateValue("CHECKOPNAME", new DataValue(checkOpName, Types.VARCHAR));
								up.addUpdateValue("CHECKTIME", new DataValue(checkTime, Types.DATE));
								up.addUpdateValue("REJECTREASON", new DataValue(remark, Types.VARCHAR));
								up.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));

								if (!Check.Null(eId))
								{
									up.addCondition("EID", new DataValue(eId, Types.VARCHAR));
								}
								up.addCondition("PROCESSID", new DataValue(processId, Types.VARCHAR));

								data.add(new DataProcessBean(up));
								dao.useTransactionProcessData(data);
								data.clear();
							}

						default : 
							break;
						}
					} 
					catch (Exception e)
					{
						HelpTools.writelog_fileName("\r\n 钉钉业务处理异常: " + e.getMessage() + "\r\n" , "dingding");
						jsonMap=null;
						return jsonMap;
					}
				}
				else
				{
					HelpTools.writelog_fileName("\r\n 钉钉CORPID未设置 \r\n" , "dingding");
					jsonMap=null;
				}
			}
			else 
			{
				HelpTools.writelog_fileName("\r\n 钉钉基础资料未设置 \r\n" , "dingding");
				jsonMap=null;
			}
			return jsonMap;
		}
		catch (Exception e)
		{
			HelpTools.writelog_fileName("\r\n 钉钉推送执行异常: " + e.getMessage() + "\r\n" , "dingding");
			jsonMap=null;
			return jsonMap;
		}
	}

	/**
	 * 获取钉钉Token
	 * @param DsmDAO 
	 * @param eId
	 * @param isUpdateToken
	 * @return Map<"errmsg":"错误描述","token":"384387434387434">
	 * @throws Exception
	 */
	public Map<String,String> getDingAccessToken(DsmDAO dao,String eId,boolean isUpdateToken)
	{
		Map<String, String> map =new HashMap<String, String>();
		try
		{
			long currentTime=System.currentTimeMillis();
			long dingAccessTokenTime = 0L ;
			if (!dingAccessTokenMap.isEmpty())
			{
				dingAccessTokenTime = Long.valueOf(dingAccessTokenMap.get("time").toString());//time/token
			}
			if  ( (dingAccessTokenTime > currentTime - (6000L * 1000L)) && !isUpdateToken )
			{
				map.put("errmsg","");
				map.put("token", dingAccessTokenMap.getOrDefault("token", "").toString());
			}
			else
			{
				//获取钉钉accessToken
				String accessToken = "";
				String sql =" select * from DCP_DING_BASESET where EID='"+eId+"' and status='100'  " ;
				List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
				if (getQData != null && getQData.isEmpty() == false) 
				{
					String appKey = getQData.get(0).get("APPKEY").toString();
					String appSecret = getQData.get(0).get("APPSECRET").toString();
					//调用钉钉平台获取accessToken
					DefaultDingTalkClient clientToken = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
					OapiGettokenRequest requestToken = new OapiGettokenRequest();
					requestToken.setAppkey(appKey);
					requestToken.setAppsecret(appSecret);
					requestToken.setHttpMethod("GET");
					OapiGettokenResponse response = clientToken.execute(requestToken);
					accessToken = response.getAccessToken();
					if (Check.Null(accessToken))
					{
						map.put("errmsg", "钉钉平台返回： access_token获取失败！原因:"+response.getErrmsg());
						map.put("token", "");
						dingAccessTokenMap.clear();
					}
					else
					{
						map.put("errmsg","");
						map.put("token", accessToken);
						dingAccessTokenMap.put("time", currentTime);
						dingAccessTokenMap.put("token", accessToken);
					}
				}
				else
				{
					map.put("errmsg", "钉钉基础设置未维护,应用标识和密钥为空！");
					map.put("token", "");
					dingAccessTokenMap.clear();
				}
			}
			return map;
		}
		catch(Exception e)
		{
			map.put("errmsg","钉钉平台获取access_token失败，原因："+ e.getMessage());
			map.put("token", "");
			dingAccessTokenMap.clear();
			return map;
		}

		finally 
		{

		}
	}

	/**
	 * 钉钉消息推送
	 * @param DsmDAO 
	 * @param eId
	 * @param agentId
	 * @param userId 
	 * @param sendType 0.text 
	 * @param text 最长2048字节
	 * @return Map<"isSuccess":true,"errmsg":"错误描述">
	 * @throws Exception
	 */
	public Map<String,Object> sendDingMsg(DsmDAO dao,String eId,Long agentId,String userId,String sendType,String text )
	{
		Map<String, Object> map =new HashMap<String, Object>();
		try
		{
			//获取钉钉平台token
			Map<String,String> accessTokenMap = getDingAccessToken(dao ,eId,false);
			String accessToken = accessTokenMap.getOrDefault("token", "").toString();
			if (Check.Null(accessToken))
			{
				map.put("errmsg","钉钉消息推送失败，原因："+ accessTokenMap.getOrDefault("errmsg", "").toString());
				map.put("isSuccess", false);
				return map;
			}

			DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
			OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
			request.setUseridList(userId);
			request.setAgentId(agentId);
			request.setToAllUser(false);

			OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
			msg.setMsgtype("text");
			msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
			msg.getText().setContent(text);
			request.setMsg(msg);
			OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request,accessToken);
			Long errcode = response.getErrcode();
			if (errcode==88L||errcode==40014L )
			{
				//获取钉钉平台token
				accessTokenMap = getDingAccessToken(dao ,eId,true);
				accessToken = accessTokenMap.getOrDefault("token", "").toString();
				if (Check.Null(accessToken))
				{
					map.put("errmsg","钉钉消息推送失败，原因："+ accessTokenMap.getOrDefault("errmsg", "").toString());
					map.put("isSuccess", false);
					return map;
				}
				response = client.execute(request,accessToken);
			}
			errcode = response.getErrcode();
			String errmsg = response.getErrmsg();
			if (errcode!=0L )
			{
				map.put("errmsg","钉钉消息推送失败，错误代码："+errcode+" 错误原因："+errmsg);
				map.put("isSuccess", false);
				return map;
			}
			else 
			{
				map.put("errmsg","");
				map.put("isSuccess", true);
			}
			return map;
		}
		catch(Exception e)
		{
			map.put("errmsg","钉钉消息推送失败，原因："+ e.getMessage());
			map.put("isSuccess", false);
			return map;
		}
	}





}
