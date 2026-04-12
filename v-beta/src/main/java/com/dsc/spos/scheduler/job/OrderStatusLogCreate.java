package com.dsc.spos.scheduler.job;

import java.io.IOException;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.waimai.HelpTools;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class OrderStatusLogCreate extends InitJob
{

	Logger logger = LogManager.getLogger(OrderStatusLogCreate.class.getName());

	String jddjLogFileName = "OrderStatusLogCreate";
	
	static boolean bRun=false;//标记此服务是否正在执行中
	
	public String doExe() throws IOException 
	{
	  //返回信
	  String sReturnInfo="";
	  //此服务是否正在执行中
		if (bRun )
		{		
			logger.info("\r\n*********第三方消息通知OrderStatusGWCreate正在执行中************\r\n");
			
			sReturnInfo="第三方消息通知正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********第三方消息通知OrderStatusGWCreate定时调用Start:************\r\n");
	  
		HelpTools.writelog_fileName("第三方消息通知OrderStatusGWCreate定时调用Start " ,jddjLogFileName);
		//查找订单表，如果已经下单通知物流接口了的，并且已经取货了的，直接通知第三方,目前主要用于通知舞像以及味多美官网
		try 
		{
						String sql="select A.*,b.org_name from OC_ORDER_STATUSLOG A left join DCP_ORG_lang B on A.EID=B.EID and A.SHOPID=B.organizationno and B.lang_type='zh_CN' "
					+ "  where ( A.LOAD_DOCTYPE='3' or A.LOAD_DOCTYPE='9' ) and (A.CALLBACK_STATUS='0' or A.CALLBACK_STATUS is null ) and ( ( A.STATUSTYPE='1'  and A.STATUS!='0'  and A.STATUS!='1' ) or A.STATUSTYPE!='1' )  "
					+ "  and STATUSTYPE!='3' order by A.UPDATE_TIME   ";	
			
			List<Map<String, Object>> listdorder=this.doQueryData(sql, null);
			if(listdorder!=null&&!listdorder.isEmpty())
			{
				//开始组JSON
				String code="";
			  String message="";
			  
				for (Map<String, Object> map : listdorder) 
				{
					String ORDERNO=map.get("ORDERNO").toString();
					String STATUSTYPE=map.get("STATUSTYPE").toString();//1 订单、2配送、3退单、4其他
					String STATUSTYPENAME=map.get("STATUSTYPENAME").toString();
					String STATUS=map.get("STATUS").toString();// 参考方法 HelpTools.GetOrderStatusName()
					String STATUSNAME=map.get("STATUSNAME").toString();
					String OPNO=map.get("OPNO").toString();
					String OPNAME=map.get("OPNAME").toString();
					String memo=map.get("MEMO").toString();
					String shopname=map.get("ORG_NAME").toString();
					String shopId=map.get("SHOPID").toString();
					String eId = map.get("EID").toString();
					String customerNO = map.get("CUSTOMERNO").toString();
					String load_DocType = map.get("LOAD_DOCTYPE").toString();
					memo="门店编号："+shopId +" 门店名称："+ shopname+memo;
					
					String LOAD_DOCTYPE=map.get("LOAD_DOCTYPE").toString();
					
					String UPDATE_TIME=map.get("UPDATE_TIME").toString();
					Calendar cal = Calendar.getInstance();   
	        cal.setTime(new SimpleDateFormat("yyyyMMddHHmmssSSS").parse(UPDATE_TIME));   
	        // cal.add(Calendar.HOUR, -8);// 24小时制   
	        String shipping_time=cal.getTime().getTime()/1000 +"";
	        String updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
	        
	        String channelId = "";
	        try 
	        {
	        	channelId = map.get("CHANNELID").toString();		
			
	        } catch (Exception e) {
				
	        }
	        
	        String statusType_crm = "3";//微商城订单类型  除了配送状态 ， 其他都写死 3 2020-01-16
	        String status_crm =STATUS;	
	        
					if(LOAD_DOCTYPE.equals("3"))
					{	
							try
							{
								JSONObject js=new JSONObject();
								js.put("serviceId", "DCP_OrderStatusUpdate");
								js.put("orderNo", ORDERNO);
								/*
								 * 订单中心statusType：1 =订单、2=配送、3=退单、4=其他
								 * 微商城statusType： 1=交易状态变更、2=物流状态变更、3=其他、4= 退单状态变更
								 */
								
								if(STATUSTYPE.equals("1"))//订单状态
								{
									/*
									 * 订单中心状态：1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单
									 * 微商城交易状态：3=已接单、4=已发货、5=已收货未回款、6=交易成功（确认收货，货款两清）、7=交易关闭（取消订单、全额退款、超时未支付、拒绝接单
									 */
									statusType_crm = "3";
									if(STATUS.equals("2"))//已接单状态的，要改写下CRM的状态
									{
										status_crm="3";
										//statusType_crm = "1";
									}
									else	if(STATUS.equals("10"))
									{
										status_crm="4";
									}
									else	if(STATUS.equals("11"))
									{
										status_crm="6";
										//statusType_crm = "1";
									}
									else	if(STATUS.equals("12")||STATUS.equals("3"))
									{
										status_crm="7";
									}
									//可能有其他状态 咋搞？
								}															
								else if(STATUSTYPE.equals("2"))//配送状态类型
								{
									statusType_crm="2";//
									
								}
								else if(STATUSTYPE.equals("3"))//退单状态类型
								{
									statusType_crm="3";//
								}
							  else if(STATUSTYPE.equals("4"))//其他
								{
							  	statusType_crm="3";
								}
								else 
								{
					
									statusType_crm = "3";//万一查询出来有其他的，
				        }
															
								js.put("statusType", statusType_crm);//除了配送状态,其他都写死 3=其他  ，只填写操作描述即可，不影响状态。供流程追踪																						
								js.put("status", status_crm);
								js.put("description", memo);
								js.put("oprId", OPNO);
								js.put("orgType", "2");
								js.put("orgId", shopId);
								js.put("updateTime", updateTime);
								
								 String request = js.toString();
				         String serviceName = eId;		         
				         String microMarkServiceName = "OrderStatusUpdate";
				         HelpTools.writelog_fileName("【同步任务任开始：" + request.toString(),jddjLogFileName);
				         String result = HttpSend.MicroMarkSend(request, serviceName, microMarkServiceName,channelId);
				         HelpTools.writelog_fileName("【同步任务任返回：" + result,jddjLogFileName);
				         
				         JSONObject json = new JSONObject(result);          
			           String success = json.get("success").toString();
			           String serviceDescription = json.getString("serviceDescription");
			           if(success.equals("false")){
			           	message=serviceDescription;
			            }
			           else
			           {
			          	 code="1";
			           }
								
							}
							catch (Exception e) 
							{
								
								String memoStr = e.getMessage();
								if (memoStr != null && memoStr.length() > 255)
								{
									memoStr = memoStr.substring(0, 255);
								}
								
								Map<String, DataValue> values = new HashMap<String, DataValue>();
						  	DataValue v = new DataValue("2", Types.VARCHAR);
								values.put("CALLBACK_STATUS", v);
								
							  // condition
								Map<String, DataValue> conditions = new HashMap<String, DataValue>();
								DataValue c1 = new DataValue(ORDERNO, Types.VARCHAR);
								conditions.put("ORDERNO", c1);
								DataValue c2 = new DataValue(map.get("LOAD_DOCTYPE").toString(), Types.VARCHAR);
								conditions.put("LOAD_DOCTYPE", c2);
								DataValue c3 = new DataValue(map.get("STATUSTYPE").toString(), Types.VARCHAR);
								conditions.put("STATUSTYPE", c3);
								DataValue c4 = new DataValue(map.get("STATUS").toString(), Types.VARCHAR);
								conditions.put("STATUS", c4);
								
								//this.doUpdate("OC_ORDER_STATUSLOG", values, conditions);
						  	logger.info("\r\n*********物流消息通知DeliveryInfoCreate定时调用失败:"+e.toString()+"************\r\n");
								
								logger.info("\r\n*********物流消息通知DeliveryInfoCreate定时调用失败:************\r\n");
							}
							
					}
					if(LOAD_DOCTYPE.equals("9"))
					{
						try
						{
							
						  JSONObject jsReq=new JSONObject();
						  jsReq.put("customerNO", customerNO);
						  jsReq.put("oEId", eId);
						  jsReq.put("o_organizationNO", shopId);
						  jsReq.put("oShopId", shopId);
						  jsReq.put("orderNO", ORDERNO);
						  jsReq.put("loadDocType", load_DocType);
						  jsReq.put("o_opNO", OPNO);
						  jsReq.put("o_opName", OPNAME);
						  jsReq.put("statusType", STATUSTYPE);
						  jsReq.put("statusTypeName", STATUSTYPENAME);
						  jsReq.put("status", STATUS);
						  jsReq.put("statusName", STATUSNAME);
						  jsReq.put("memo", memo);
						  jsReq.put("update_time", UPDATE_TIME);
						  
						  String request = jsReq.toString();
			        String serviceName = "";
			        String microMarkServiceName = "OrderStatusUpdate";
			        HelpTools.writelog_fileName("【同步任务任开始：" + request.toString(),jddjLogFileName);
			        String result =""; //HttpSend.MicroMarkSend(request, serviceName, microMarkServiceName);
			        HelpTools.writelog_fileName("【同步任务任返回：" + result,jddjLogFileName);
			        
			        JSONObject json = new JSONObject(result);          
		          String success = json.get("success").toString();
		          String serviceDescription = json.getString("serviceDescription");
				      if (success.equals("false"))
				      {			      	
		           	message=serviceDescription;
		          }
		          else
		          {		          	
		          	code="1";
		          }
						  
						}
						catch(Exception ex)
						{
							
						}
					}
						
					
					if(code.equals("1"))
				  {
				  	//成功需要更新一下订单的物流回写状态
				  	Map<String, DataValue> values = new HashMap<String, DataValue>();
				  	DataValue v = new DataValue("1", Types.VARCHAR);
						values.put("CALLBACK_STATUS", v);						
						
					  // condition
						Map<String, DataValue> conditions = new HashMap<String, DataValue>();
						DataValue c1 = new DataValue(ORDERNO, Types.VARCHAR);
						conditions.put("ORDERNO", c1);
						DataValue c2 = new DataValue(map.get("LOAD_DOCTYPE").toString(), Types.VARCHAR);
						conditions.put("LOAD_DOCTYPE", c2);
						DataValue c3 = new DataValue(map.get("STATUSTYPE").toString(), Types.VARCHAR);
						conditions.put("STATUSTYPE", c3);
						DataValue c4 = new DataValue(map.get("STATUS").toString(), Types.VARCHAR);
						conditions.put("STATUS", c4);
						
						
						this.doUpdate("OC_ORDER_STATUSLOG", values, conditions);
				  }
				  else
				  {
				  	
				  	Map<String, DataValue> values = new HashMap<String, DataValue>();
				  	DataValue v = new DataValue("2", Types.VARCHAR);
						values.put("CALLBACK_STATUS", v);
					  // condition
						Map<String, DataValue> conditions = new HashMap<String, DataValue>();
						DataValue c1 = new DataValue(ORDERNO, Types.VARCHAR);
						conditions.put("ORDERNO", c1);
						DataValue c2 = new DataValue(map.get("LOAD_DOCTYPE").toString(), Types.VARCHAR);
						conditions.put("LOAD_DOCTYPE", c2);
						DataValue c3 = new DataValue(map.get("STATUSTYPE").toString(), Types.VARCHAR);
						conditions.put("STATUSTYPE", c3);
						DataValue c4 = new DataValue(map.get("STATUS").toString(), Types.VARCHAR);
						conditions.put("STATUS", c4);
						
						this.doUpdate("OC_ORDER_STATUSLOG", values, conditions);
				  	logger.info("\r\n*********物流消息通知DeliveryInfoCreate定时调用失败:"+message+"************\r\n");
				  }
					
					
		    }
				
			}
			else 
			{
				//
				sReturnInfo="无符合要求的数据！";
				HelpTools.writelog_fileName("【第三方消息通知OrderStatusGWCreate定时调用】没有需要处理的记录！",jddjLogFileName);
				logger.info("\r\n******J第三方消息通知OrderStatusGWCreate没有需要处理的记录******\r\n");
			}
		
	  }
		catch (Exception e) 
		{
		// TODO: handle exception
	  	
	  	bRun=false;
	  	logger.error("\r\n***************第三方消息通知OrderStatusGWCreate异常"+e.getMessage()+"****************\r\n");
	  	HelpTools.writelog_fileName("第三方消息通知OrderStatusGWCreate异常： "+e.getMessage() ,jddjLogFileName);
	  	sReturnInfo="错误信息:" + e.getMessage();
	  }
		finally 
		{
			bRun=false;//
		}
		logger.info("\r\n***************第三方消息通知OrderStatusGWCreate定时调用END****************\r\n");
		return sReturnInfo;
	}
	

	/**
	 * 字符串转换unicode
	 */
	public String string2Unicode(String string) {
	 
	    StringBuffer unicode = new StringBuffer();
	 
	    for (int i = 0; i < string.length(); i++) {
	 
	        // 取出每一个字符
	        char c = string.charAt(i);
	        // 转换为unicode
	        unicode.append("\\u" + Integer.toHexString(c));
	    }
	 
	    return unicode.toString();
	}
	
	

}
