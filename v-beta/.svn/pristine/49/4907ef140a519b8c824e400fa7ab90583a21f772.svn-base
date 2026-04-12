package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.waimai.HelpTools;

public class OrderStockCreate extends InitJob
{


	Logger logger = LogManager.getLogger(OrderStockCreate.class.getName());

	String jddjLogFileName = "OrderStockCreate";
	
	static boolean bRun=false;//标记此服务是否正在执行中
	
	public String doExe() throws IOException 
	{
	  //返回信
	  String sReturnInfo="";
	  //此服务是否正在执行中
		if (bRun )
		{		
			logger.info("\r\n*********手机商城库存同步正在执行中************\r\n");
			
			sReturnInfo="手机商城库存同步正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********手机商城库存同步定时调用Start:************\r\n");
	  
		HelpTools.writelog_fileName("手机商城库存同步定时调用Start " ,jddjLogFileName);
		//查找订单表，如果已经下单通知物流接口了的，并且已经取货了的，直接通知第三方,目前主要用于通知舞像以及味多美官网
		try 
		{
			
			String sql="select * from dcp_ecommerce_stock where ECPLATFORMNO='3' ";	
			
			List<Map<String, Object>> listdorder=this.doQueryData(sql, null);
			if(listdorder!=null&&!listdorder.isEmpty())
			{
				//开始组JSON
				String code="";
			  String message="";
			  com.alibaba.fastjson.JSONObject js=new com.alibaba.fastjson.JSONObject();
				js.put("serviceId", "MallGoodsStockUpdate");
				js.put("OprId", "admin");
				js.put("oprType", "modify");
				js.put("thirdTransNo", "");
				js.put("terminalId", "");
				js.put("orgType", "1");
				js.put("orgId", "");
				js.put("branchType", "1");
				com.alibaba.fastjson.JSONArray jsarry=new com.alibaba.fastjson.JSONArray();
				js.put("goodsList", jsarry);
				String eId = "";
				
				for (Map<String, Object> map : listdorder) 
				{
					eId=map.get("EID").toString();
					com.alibaba.fastjson.JSONObject jsdetail=new com.alibaba.fastjson.JSONObject();
					jsdetail.put("goodsId", map.get("PLUNO").toString());
					jsdetail.put("stock", map.get("ECPLATFORMSTOCK").toString());
					jsdetail.put("oprType", "modify");
					jsarry.add(jsdetail);
					
				String request = js.toString();		         
		         String microMarkServiceName = "MallGoodsStockUpdate";
		         HelpTools.writelog_fileName("【同步任务任开始：" + request.toString(),jddjLogFileName);
		         String result = HttpSend.MicroMarkSend(request, eId, microMarkServiceName);
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
