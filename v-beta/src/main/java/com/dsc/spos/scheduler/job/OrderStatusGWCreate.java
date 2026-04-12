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
public class OrderStatusGWCreate extends InitJob
{
	Logger logger = LogManager.getLogger(OrderStatusGWCreate.class.getName());

	String jddjLogFileName = "OrderStatusGWCreate";

	static boolean bRun=false;//标记此服务是否正在执行中

	public String doExe() throws IOException 
	{
		//返回信
		String sReturnInfo="";
		//此服务是否正在执行中
		if (bRun )
		{		
			logger.info("\r\n*********官网物流消息通知正在执行中************\r\n");

			sReturnInfo="官网物流消息通知正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********官网物流消息通知OrderStatusGWCreate定时调用Start:************\r\n");

		HelpTools.writelog_fileName("官网物流消息通知OrderStatusGWCreate定时调用Start " ,jddjLogFileName);
		//查找订单表，如果已经下单通知物流接口了的，并且已经取货了的，直接通知第三方,目前主要用于通知舞像以及味多美官网
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			String curdate=new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());

			String sql="select A.*,b.org_name from OC_ORDER_STATUSLOG A left join DCP_ORG_lang B on A.EID=B.EID and A.SHOPID=B.organizationno and B.lang_type='zh_CN' "
					+ "  where A.UPDATE_TIME>='"+curdate+"' and ( A.LOAD_DOCTYPE='6' or A.LOAD_DOCTYPE='9'  or A.LOAD_DOCTYPE='10' ) and (A.CALLBACK_STATUS='0' or A.CALLBACK_STATUS is null ) and ( ( A.STATUSTYPE='1'  and A.STATUS!='0' ) or A.STATUSTYPE!='1' ) "
					+ " and A.STATUSTYPE!='3'   order by A.UPDATE_TIME   ";	

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


					//JSONObject payload = new JSONObject();
					com.alibaba.fastjson.JSONObject payload = new com.alibaba.fastjson.JSONObject(true);
					String method="";
					method="salesDeliver";
					if(STATUSTYPE.equals("3"))
					{
						payload.put("return_stutas", STATUS);
						payload.put("cmd", "middle_change_order_refund");
					}
					//payload.put("key", "85bef47403878a047af08b65b77b9e8d");
					payload.put("msg", memo);
					payload.put("opno", OPNO+OPNAME);
					payload.put("optionType", STATUSTYPE);
					payload.put("order_no", ORDERNO);
					if(STATUSTYPE.equals("1"))
					{
						if(STATUS.equals("12") || STATUS.equals("3"))
						{
							payload.put("optionType", "3");
							payload.put("return_stutas", "6");
							payload.put("cmd", "middle_change_order_refund");
						}
						else
						{
							payload.put("order_stutas", STATUS);
							payload.put("cmd", "middle_change_order_status");
						}
					}
					if(STATUSTYPE.equals("2"))
					{
						payload.put("delivery_stutas", STATUS);
						payload.put("cmd", "middle_change_order_logistics");
					}
					if(STATUSTYPE.equals("4"))
					{
						payload.put("change_stutas", STATUS);
						payload.put("cmd", "middle_order_change");
					}

					payload.put("platform_type", "middle");
					payload.put("shipping_time", shipping_time);

					payload.put("erp_code", shopId);

					//					//json里面key顺序排序
					//					String	signdata = payload.toString();
					//					//signdata=string2Unicode(signdata);
					//					
					//					//对signdata MD5签名
					//					String md5date=PosPub.encodeMD5(signdata).toUpperCase();
					//					payload.put("sign", md5date);
					//					payload.remove("key");

					try
					{
						HelpTools.writelog_fileName("【同步任务任开始：" + payload.toString(),jddjLogFileName);
						String resbody=HttpSend.SendWuXiang(method, payload.toString(), "http://www.wdmcake.cn/api/erp.php");
						HelpTools.writelog_fileName("【同步任务任返回：" + resbody,jddjLogFileName);

						JSONObject jsonres = new JSONObject(resbody);
						String code= jsonres.getString("code");
						String message= jsonres.getString("msg");
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

		} catch (Exception e) 
		{
			// TODO: handle exception

			bRun=false;
		}
		bRun=false;

		return "";
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


