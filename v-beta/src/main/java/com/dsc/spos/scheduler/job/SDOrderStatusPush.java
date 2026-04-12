package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.net.URLEncoder;
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
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.waimai.HelpTools;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SDOrderStatusPush extends InitJob
{
	Logger logger = LogManager.getLogger(OrderStatusGWCreate.class.getName());

	String jddjLogFileName = "SDOrderStatusPush";
	
	static boolean bRun=false;//标记此服务是否正在执行中
	
	public String doExe() throws IOException 
	{
	  //返回信
	  String sReturnInfo="";
	  //此服务是否正在执行中
		if (bRun )
		{		
			logger.info("\r\n*********物流系统物流消息通知正在执行中************\r\n");
			
			sReturnInfo="物流系统物流消息通知正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********物流系统物流消息通知OrderStatusGWCreate定时调用Start:************\r\n");
	  
		HelpTools.writelog_fileName("物流系统物流消息通知OrderStatusGWCreate定时调用Start " ,jddjLogFileName);
		//查找订单表，如果已经下单通知物流接口了的，并且已经取货了的，直接通知第三方,目前主要用于通知舞像以及味多美官网
		try {
					Calendar calendar = Calendar.getInstance();
	        calendar.add(Calendar.DATE, -1);
			String curdate=new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
			
			String sql="select A.*,b.org_name from OC_ORDER_STATUSLOG A left join DCP_ORG_lang B on A.EID=B.EID and A.SHOPID=B.organizationno and B.lang_type='zh_CN' "
					+ " left join OC_order C on A.EID=C.EID and A.load_doctype=C.Load_Doctype and A.Orderno=C.Orderno  "
					+ "  where A.UPDATE_TIME>='"+curdate+"' and (A.LOAD_DOCTYPE='6'  or A.LOAD_DOCTYPE='9' or A.LOAD_DOCTYPE='7' or A.LOAD_DOCTYPE='10' )  and (A.CALLBACK_DELSTATUS='0' or A.CALLBACK_DELSTATUS is null ) and  A.STATUSTYPE!='2'    and A.SHOPID like 'BJ%'  "
					+ " and C.Shiptype='2'    order by A.UPDATE_TIME   ";
			
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
					memo="门店编号："+shopId +" 门店名称："+ shopname+memo;
					
					String UPDATE_TIME=map.get("UPDATE_TIME").toString();
					Calendar cal = Calendar.getInstance();   
	        cal.setTime(new SimpleDateFormat("yyyyMMddHHmmssSSS").parse(UPDATE_TIME));   
	        cal.add(Calendar.HOUR, -8);// 24小时制   
	        String shipping_time=cal.getTime().getTime()/1000 +"";
	        
	        OrderStatusPushmodel osp=new OrderStatusPushmodel();
	        osp.setCmd("action.create");
	        osp.setVersion("1.0");
	        osp.setTimestamp(System.currentTimeMillis()+"");
	        osp.setAppid("wdmcake_kd");
	        osp.setNeed_sign("no");
	        Body bd=new Body();
	        bd.setOrder_sn(ORDERNO);
	        Action ac=new Action();
	        ac.setAction_note(memo);
	        ac.setAction_user(OPNAME);
	        ac.setLog_time(shipping_time);
	        bd.setAction(ac);
	        osp.setBody(bd);
	        
	        ParseJson pj = new ParseJson();
	        String sbody=pj.beanToJson(bd);
	        
	        String req=pj.beanToJson(osp);
	        
					String urlcode=URLEncoder.encode(sbody, "utf-8" );
					String url="http://kd.wdmcake.cn/api/mos/index.php?";
					url+="cmd=action.create&appid=wdmcake_kd&need_sign=no"+"&timestamp="+osp.getTimestamp()+"&version=1.0"+"&sign=aaa&body="+urlcode;
					
					try
					{
						HelpTools.writelog_fileName("【同步任务任开始：" + req.toString(),jddjLogFileName);
						String res=HttpSend.Sendhttp("POST", "", url);
					  HelpTools.writelog_fileName("【同步任务任返回：" + res,jddjLogFileName);
					  
					  JSONObject reqobject=new JSONObject(res);
						JSONObject resbody=reqobject.getJSONObject("body");
						
						if(resbody.get("errno").toString().equals("0"))
					  {
					  	//成功需要更新一下订单的物流回写状态
					  	Map<String, DataValue> values = new HashMap<String, DataValue>();
					  	DataValue v = new DataValue("1", Types.VARCHAR);
							values.put("CALLBACK_DELSTATUS", v);						
							
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
							values.put("CALLBACK_DELSTATUS", v);
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
					  	logger.info("\r\n*********物流消息通知DeliveryInfoCreate定时调用失败:"+resbody.getString("msg")+"************\r\n");
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
				  	logger.info("\r\n*********物流系统物流消息通知DeliveryInfoCreate定时调用失败:"+e.toString()+"************\r\n");
						
						logger.info("\r\n*********物流系统物流消息通知DeliveryInfoCreate定时调用失败:************\r\n");
					}
					
					pj=null;
					
		    }
				
			}
		
	  } catch (Exception e) 
		{
		// TODO: handle exception
	  	
	  	bRun=false;
	  }
		bRun=false;
		
		return "";
	}
	

 class OrderStatusPushmodel {
	    private String cmd;
	    private String version;
	    private String timestamp;
	    private String appid;
	    private String need_sign;
	    private Body body;
	    public void setCmd(String cmd) {
	         this.cmd = cmd;
	     }
	     public String getCmd() {
	         return cmd;
	     }

	    public void setVersion(String version) {
	         this.version = version;
	     }
	     public String getVersion() {
	         return version;
	     }

	    public void setTimestamp(String timestamp) {
	         this.timestamp = timestamp;
	     }
	     public String getTimestamp() {
	         return timestamp;
	     }

	    public void setAppid(String appid) {
	         this.appid = appid;
	     }
	     public String getAppid() {
	         return appid;
	     }

	    public void setNeed_sign(String need_sign) {
	         this.need_sign = need_sign;
	     }
	     public String getNeed_sign() {
	         return need_sign;
	     }

	    public void setBody(Body body) {
	         this.body = body;
	     }
	     public Body getBody() {
	         return body;
	     }

	}
	

 class Body {

    private String order_sn;
    private Action action;
    public void setOrder_sn(String order_sn) {
         this.order_sn = order_sn;
     }
     public String getOrder_sn() {
         return order_sn;
     }

    public void setAction(Action action) {
         this.action = action;
     }
     public Action getAction() {
         return action;
     }

}
 
 class Action {

    private String action_note;
    private String action_user;
    private String log_time;
    public void setAction_note(String action_note) {
         this.action_note = action_note;
     }
     public String getAction_note() {
         return action_note;
     }

    public void setAction_user(String action_user) {
         this.action_user = action_user;
     }
     public String getAction_user() {
         return action_user;
     }

    public void setLog_time(String log_time) {
         this.log_time = log_time;
     }
     public String getLog_time() {
         return log_time;
     }

}
	
}

