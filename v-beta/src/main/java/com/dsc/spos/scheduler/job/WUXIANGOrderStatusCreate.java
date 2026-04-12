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

import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.WUXIANGUtil;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WUXIANGOrderStatusCreate extends InitJob
{
	Logger logger = LogManager.getLogger(OrderStatusGWCreate.class.getName());

	String jddjLogFileName = "WUXIANGOrderStatusCreate";
	
	static boolean bRun=false;//标记此服务是否正在执行中
	
	public String doExe() throws IOException 
	{
	  //返回信
	  String sReturnInfo="";
	  //此服务是否正在执行中
		if (bRun )
		{		
			logger.info("\r\n*********舞像物流消息通知正在执行中************\r\n");
			
			sReturnInfo="舞像物流消息通知正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********舞像物流消息通知OrderStatusGWCreate定时调用Start:************\r\n");
	  
		HelpTools.writelog_fileName("舞像物流消息通知OrderStatusGWCreate定时调用Start " ,jddjLogFileName);
		//查找订单表，如果已经下单通知物流接口了的，并且已经取货了的，直接通知第三方,目前主要用于通知舞像以及味多美官网
		try
		{
			Calendar calendar = Calendar.getInstance();
	        calendar.add(Calendar.DATE, -1);
			String curdate=new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
			
			String sql="select A.*,b.org_name,c.SHIPTYPE,D.OTHER_NO  from OC_ORDER_STATUSLOG A left join DCP_ORG_lang B on A.eId=B.eId and A.SHOPID=B.organizationno and B.lang_type='zh_CN' "
				+ " left join OC_order C on A.eId=C.eId and A.LOAD_DOCTYPE=C.LOAD_DOCTYPE and A.orderno=C.orderno "
				+ " left join OC_ECOMMERCE D on C.eId=D.eId and C.BELFIRM=D.PARTNER_ID and D.ECPLATFORMNO='7' "
					+ "  where A.UPDATE_TIME>='"+curdate+"' and  A.LOAD_DOCTYPE='7'  and (A.CALLBACK_STATUS='0' or A.CALLBACK_STATUS is null ) and   A.STATUSTYPE='1' and  (A.STATUS='11' or A.STATUS='6')  and A.SHOPID like 'BJ%'   order by A.UPDATE_TIME   ";
			List<Map<String, Object>> listdorder=this.doQueryData(sql, null);
			if(listdorder!=null&&!listdorder.isEmpty())
			{
			//开始组JSON
				for (Map<String, Object> map : listdorder) 
				{
					String ORDERNO=map.get("ORDERNO").toString();
					String STATUSTYPE=map.get("STATUSTYPE").toString();
					String STATUSTYPENAME=map.get("STATUSTYPENAME").toString();
					String STATUS=map.get("STATUS").toString();
					String STATUSNAME=map.get("STATUSNAME").toString();
					String OPNO=map.get("OPNO").toString();
					String OPNAME=map.get("OPNAME").toString();
					String memo=map.get("MEMO").toString();
					String shopname=map.get("ORG_NAME").toString();
					String shopId=map.get("SHOPID").toString();
					String SHIPTYPE=map.get("SHIPTYPE").toString();
					String OTHER_NO=map.get("OTHER_NO").toString();
					
					
					memo="门店编号："+shopId +" 门店名称："+ shopname+memo;
					
					String UPDATE_TIME=map.get("UPDATE_TIME").toString();
					Calendar cal = Calendar.getInstance();   
	        cal.setTime(new SimpleDateFormat("yyyyMMddHHmmssSSS").parse(UPDATE_TIME));   
	        cal.add(Calendar.HOUR, -8);// 24小时制   
	        String shipping_time=cal.getTime().getTime()/1000 +"";
	        
					
					//JSONObject payload = new JSONObject();
					com.alibaba.fastjson.JSONObject payload = new com.alibaba.fastjson.JSONObject(true);
					String method="";
					if(SHIPTYPE.equals("2"))
					{
						if(STATUS.equals("6"))
						{
							//配送订单 6的时候不需要通知
							continue;
						}
					  method="salesDeliver";
					  payload.put("order_no", ORDERNO);
					  payload.put("logistics_code", "other");
					  payload.put("logistics_no", "味多美自配送");
					  
					}
					else
					{
						method="selfInviteSync";
						if(STATUS.equals("6"))
						{
						  payload.put("optionType", 1);
						}
						if(STATUS.equals("11"))
						{
						  payload.put("optionType", 2);
						}
					}
					
					try
					{
						HelpTools.writelog_fileName("【同步任务任开始：" + payload.toString(),jddjLogFileName);
					  String resbody=WUXIANGUtil.SendWuXiang(method, payload.toString(), "http://www.wdmcake.cn/api/erp.php",OTHER_NO );
					  HelpTools.writelog_fileName("【同步任务任返回：" + resbody,jddjLogFileName);
					  
					  JSONObject jsonres = new JSONObject(resbody);
					  String code= jsonres.getString("code");
					  String message= jsonres.getString("message");
					  String memoStr = message;
						if (memoStr != null && memoStr.length() > 255)
						{
							memoStr = memoStr.substring(0, 255);
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
				
			}
		}
		catch (Exception e) 
		{
		// TODO: handle exception
	  }
				
		bRun=false;//	
		return "";
	}
}


