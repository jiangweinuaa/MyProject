package com.dsc.spos.scheduler.job;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.utils.Mail;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.ding.DCP_DingCallBack;
import com.dsc.spos.waimai.HelpTools;

public class WarningLogSend extends InitJob  
{
	Logger logger = LogManager.getLogger(WarningLogSend.class.getName());
	static boolean bRun=false;//标记此服务是否正在执行中
	String warningLogFileName = "WarningLogSend";
	String langType = "zh_CN";
	int msgRetryTimes = 1;//消息发送失败后重试次数
	
	public WarningLogSend()
	{
		
	}
	public String doExe() throws Exception
	{
	   // 返回信息
			String sReturnInfo = "";
			
			logger.info("\r\n***************WarningLogSend同步START****************\r\n");
			HelpTools.writelog_fileName("【WarningLogSend发送消息】同步START！",warningLogFileName);
			try 
			{
			//此服务是否正在执行中
				if (bRun)
				{		
					logger.info("\r\n*********WarningLogSend同步正在执行中,本次调用取消:************\r\n");
					HelpTools.writelog_fileName("【WarningLogSend循环处理】同步正在执行中,本次调用取消！",warningLogFileName);
					return sReturnInfo;
				}

				bRun=true;
				
				String sql ="";
				sql = this.getMsgLogSql();
				List<Map<String,Object>> agentIdList = new ArrayList<Map<String,Object>>();
				List<Map<String, Object>> getHeadData = this.doQueryData(sql, null);
				if(getHeadData!=null&&getHeadData.isEmpty()==false)
				{
					//region 查询下钉钉agentId
					try 
					{
						agentIdList = this.doQueryData("select * from DCP_DING_BASESET", null);
			
					} 
					catch (Exception e) 
					{
						HelpTools.writelog_fileName("【WarningLogSend发送消息】查询钉钉基础设置表DCP_DING_BASESET异常："+e.getMessage(),warningLogFileName);
					}									
					//endregion
					
					for (Map<String, Object> map : getHeadData) 
					{
						String eId = map.get("EID").toString();
						String warningNo = map.get("BILLNO").toString();
						String warningLogNo = map.get("ID").toString();
						String serialNo =  map.get("SERIALNO").toString();
						String pushWay = map.get("PUSHWAY").toString();
						String pushWayName = map.get("PUSHWAYNAME").toString();
						String email = map.get("EMAIL").toString();
						String dingUserId = map.get("USERID").toString();
						String templateTitle = map.get("TEMPLATETITLE").toString();
						String msgBegin = map.get("MSGBEGIN").toString();
						String msgMiddle = map.get("MSGMIDDLE").toString();
						String msgEnd = map.get("MSGEND").toString();
						String linkURL = map.get("LINKURL").toString();
						String batchNo ="推送批次号:"+warningLogNo;
						Map<String, Object>	resSend = new HashMap<String, Object>();
						try 
						{
							/*Mail mail=new Mail();
							String[] receiverEmail={"418056790@qq.com",""};
							String[] filenames=null;
							mail.sendMail(receiverEmail, "主题", "测试邮件", filenames);
							mail=null;*/
							if(pushWay.toUpperCase().equals("EMAIL"))
							{								
								Mail mail=new Mail();
								String[] receiverEmail={email};
								String[] filenames=null;
								msgMiddle = msgMiddle.replace("\n", "<br>");
								List<Map<String, String>> rows=new ArrayList<Map<String, String>>();
								String[] msgList = msgMiddle.split("<br>");
								if(msgList.length>1)
								{																	
									for (int i =0; i<msgList.length;i++) 
									{
										Map<String, String> mapRow=new LinkedHashMap<>();
										String msgStr = msgList[i];//xxx门店:xxxxxx
										int indexOf = msgStr.indexOf(":");
										if(indexOf>0)
										{
											mapRow.put("门店", msgStr.substring(0,indexOf));
											mapRow.put("内容", msgStr.substring(indexOf+1,msgStr.length()));
										}
										else
										{
											mapRow.put("内容", msgStr);
										}																																			
										rows.add(mapRow);
									}																
								}	
								else
								{
									Map<String, String> mapRow=new LinkedHashMap<>();
									String msgStr = msgMiddle.replace("<br>", "");//后面有个<br>
									int indexOf = msgStr.indexOf(":");//xxx门店:xxxxxx
									if(indexOf>0)
									{
										mapRow.put("门店", msgStr.substring(0,indexOf));
										mapRow.put("内容", msgStr.substring(indexOf+1,msgStr.length()));
									}
									else
									{
										mapRow.put("内容", msgStr);
									}		
									
									rows.add(mapRow);
								}
								
								MyCommon myCommon = new MyCommon();
								
								msgMiddle = myCommon.getTableFormatContent(msgBegin, rows);
								
							 	resSend = mail.sendMailRes(receiverEmail, templateTitle, msgMiddle+msgEnd+"<br>"+linkURL+"<br>"+batchNo, filenames);
															
							}
							else if(pushWay.toUpperCase().equals("DING"))
							{
								Long agentId = this.getDingAgentId(eId, agentIdList);
								if(agentId!=null)
								{
									DCP_DingCallBack dingding = new DCP_DingCallBack();
									resSend = dingding.sendDingMsg(StaticInfo.dao, eId, agentId, dingUserId, "0", templateTitle+"\n"+msgBegin+"\n"+msgMiddle+"\n"+msgEnd+"\n"+linkURL+"\n"+batchNo);																	
								}
								else
								{
									resSend.put("errmsg","钉钉agentId获取失败!");
									resSend.put("isSuccess", false);
								}
							
							}
							else if(pushWay.toUpperCase().equals("PHONE"))
							{
								resSend.put("errmsg","暂不支持消息发送方式("+pushWay+" "+pushWayName+")");
								resSend.put("isSuccess", false);
								
							}
							else 
							{
								resSend.put("errmsg","暂不支持消息发送方式("+pushWay+" "+pushWayName+")");
								resSend.put("isSuccess", false);				
							}
							
							//更新数据库
							String isSendSucess = "false";
							String error = "";
							if(resSend!=null)
							{
								Object isSuccess = resSend.get("isSuccess");
								Object errmsg = resSend.get("errmsg");
								if(isSuccess!=null)
								{
									if(isSuccess.toString().toLowerCase().equals("true"))
									{
										isSendSucess = "true";
									}
									
								}
								if(errmsg!=null)
								{
									error = errmsg.toString();									
								}
								
							}
							
					
							//更新数据库
							ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
							UptBean ub_warning = null;	
							ub_warning = new UptBean("dcp_warninglog_pushway");		
						  // condition
							ub_warning.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							ub_warning.addCondition("ID", new DataValue(warningLogNo, Types.VARCHAR));
							ub_warning.addCondition("SERIALNO", new DataValue(serialNo, Types.VARCHAR));
							
							ub_warning.addUpdateValue("PUSHFLAG",new DataValue(isSendSucess, Types.VARCHAR));
							if(isSendSucess.equals("true"))
							{
								error ="发送成功";								
							}												
							ub_warning.addUpdateValue("FAILMSG",new DataValue(error, Types.VARCHAR));
							DPB.add(new DataProcessBean(ub_warning));
							this.doExecuteDataToDB(DPB);
			
						} 
						catch (Exception e) 
						{
							HelpTools.writelog_fileName("【WarningLogSend发送消息】异常！监控号="+warningNo+" 监控日志单号="+warningLogNo,warningLogFileName);
							continue;
				
			
						}
					}
					
				}
				else 
				{
					//
					sReturnInfo="无符合要求的数据！";
					HelpTools.writelog_fileName("【WarningLogSend发送消息】没有需要处理的订单消息！",warningLogFileName);
					logger.info("\r\n******WarningLogSend没有需要获取的订单ID******\r\n");
				}
		
			} 
			catch (Exception e) 
			{
				logger.error("\r\n***************WarningLogSend异常"+e.getMessage()+"****************\r\n");
				sReturnInfo="错误信息:" + e.getMessage();
				HelpTools.writelog_fileName("【WarningLogSend发送消息】异常！"+sReturnInfo,warningLogFileName);
		
			}
			finally 
			{	
				bRun=false;//
		
			}
			
			
			
			
			logger.info("\r\n***************WarningLogSend同步END****************\r\n");
			HelpTools.writelog_fileName("【WarningLogSend循环处理】同步END！",warningLogFileName);
			return sReturnInfo;	
	}
	
	protected void doExecuteDataToDB(List<DataProcessBean> pData) throws Exception {
		if (pData == null || pData.size() == 0) {
			return;
		}
		StaticInfo.dao.useTransactionProcessData(pData);

	}
	
	
	private String getMsgLogSql()
	{
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from (");
		sqlbuf.append(" select a.*,b.templatetitle,b.msgbegin,b.msgmiddle,b.msgend,b.linkurl from dcp_warninglog_pushway a ");//已经启用
		sqlbuf.append(" inner join dcp_warninglog b on a.eid=b.eid and a.id=b.id");
		sqlbuf.append(" where a.pushflag='-1' order by a.id,a.serialno");
		sqlbuf.append(")");
		return sqlbuf.toString();		
	}
	
	private Long getDingAgentId(String eId,List<Map<String, Object>> agentIdList) throws Exception
	{
		Long agentId = null;
		try 
		{
			if(agentIdList!=null&&agentIdList.isEmpty()==false)
			{
				for (Map<String, Object> map : agentIdList) 
				{
					String map_eId = map.get("EID").toString();
					String id = map.get("AGENTID").toString();
					if(id!=null&&id.trim().length()>0&&map_eId!=null&&map_eId.equals(eId))
					{
						agentId = new Long(id);
						break;
					}				
				}
			}
		
	
		} 
		catch (Exception e) 
		{
		
	
		}
					
		return agentId;
	}

}
