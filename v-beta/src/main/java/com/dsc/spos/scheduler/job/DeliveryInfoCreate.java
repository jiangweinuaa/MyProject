package com.dsc.spos.scheduler.job;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.utils.HttpSend;

//用于订单物流信息通知第三方网站
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DeliveryInfoCreate extends InitJob
{
	Logger logger = LogManager.getLogger(DeliveryInfoCreate.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中
	
	public String doExe() 
	{
	  //返回信息
	  String sReturnInfo="";
	  //此服务是否正在执行中
		if (bRun )
		{		
			logger.info("\r\n*********物流消息通知正在执行中************\r\n");
			
			sReturnInfo="物流消息通知正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********物流消息通知DeliveryInfoCreate定时调用Start:************\r\n");
	  
		//查找订单表，如果已经下单通知物流接口了的，并且已经取货了的，直接通知第三方,目前主要用于通知舞像以及味多美官网
		try {
			String sql="select * from OC_ORDER where DeliveryStutas>'1' and (ISCallback is null  or ISCallback='N') and (LOAD_DOCTYPE='6' or LOAD_DOCTYPE='7' ) ";
			List<Map<String, Object>> listdorder=this.doQueryData(sql, null);
			if(listdorder!=null&&listdorder.isEmpty())
			{
				//开始组JSON
				for (Map<String, Object> map : listdorder) 
				{
					String SHIPTYPE=map.get("SHIPTYPE").toString();
					String LOAD_DOCTYPE=map.get("LOAD_DOCTYPE").toString();
					String orderno=map.get("ORDERNO").toString();
					String DeliveryNO=map.get("DeliveryNO").toString();
					
					JSONObject payload = new JSONObject();
					String method="";
					if(SHIPTYPE.equals("2"))
					{
						payload.put("order_no", orderno);
						payload.put("logistics_code", "");
						payload.put("logistics_no", DeliveryNO);
						method="salesDeliver";
					}
					if(SHIPTYPE.equals("3"))
					{
						payload.put("order_no", orderno);
						payload.put("optionType", "2");
						method="selfInviteSync";
					}
					try
					{
					  String resbody=HttpSend.SendWuXiang(method, payload.toString(), "http");
					  JSONObject jsonres = new JSONObject(resbody);
					  String code= jsonres.getString("code");
					  String message= jsonres.getString("message");
					  if(code.equals("1"))
					  {
					  	//成功需要更新一下订单的物流回写状态
					  	Map<String, DataValue> values = new HashMap<String, DataValue>();
					  	DataValue v = new DataValue("Y", Types.VARCHAR);
							values.put("ISCallback", v);
						  // condition
							Map<String, DataValue> conditions = new HashMap<String, DataValue>();
							DataValue c1 = new DataValue(orderno, Types.VARCHAR);
							conditions.put("ORDERNO", c1);
							this.doUpdate("OC_ORDER", values, conditions);
							
					  }
					  else
					  {
					  	logger.info("\r\n*********物流消息通知DeliveryInfoCreate定时调用失败:"+message+"************\r\n");
					  }
					}
					catch (Exception e) 
					{
						logger.info("\r\n*********物流消息通知DeliveryInfoCreate定时调用失败:************\r\n");
					}
					
		    }
				
			}
		
	  } catch (Exception e) 
		{
		// TODO: handle exception
	  	bRun=false;
	  }
		
		return "";
	}

}


