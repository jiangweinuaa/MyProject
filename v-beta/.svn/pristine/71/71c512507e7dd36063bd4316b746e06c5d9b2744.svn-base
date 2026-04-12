package com.dsc.spos.waimai;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;

public class WMJDDJService extends SWaimaiBasicService {

	public static String jddjLogFileName = "jddjMessagelog";
	String messageType ="";
	public WMJDDJService(String messageType)
	{
		this.messageType = messageType;
	}

	@Override
	public String execute(String json) throws Exception {
		// TODO Auto-generated method stub
		if(messageType.equals("1"))
		{
			String res_json = GetJDDJnewOrderResponse(json);
			HelpTools.writelog_fileName("【JDDJ订单消息内容】" + res_json, jddjLogFileName);
			if(res_json ==null ||res_json.length()==0)
			{
				return null;
			}	
			Map<String, Object> res = new HashMap<String, Object>();
			this.processDUID(res_json, res);
		}
		else if (messageType.equals("2")) //订单配送消息
		{
			UpdateJDDJOrderShippingResponse(json);		
		}
		else
		{
			HelpTools.writelog_fileName("【JDDJ订单其他消息内容】" + json, jddjLogFileName);
		}

		return null;
	}

	@Override
	protected void processDUID(String req, Map<String, Object> res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			JSONObject obj = new JSONObject(req);
			String billId = obj.get("billId").toString();//消息单据ID
			String statusId = obj.get("statusId").toString();//消息状态ID
			String timestamp = obj.get("timestamp").toString();//日期格式为"yyyy-MM-dd HH:mm:ss"
			String remark = "";
			if(!obj.isNull("remark"))
			{
				remark = obj.get("remark").toString();//订单锁定原因(fengkong：风控，reject：用户拒收)
			}
			HelpTools.writelog_fileName("【JDDJ订单消息内容】单据billId=" +billId+" 消息状态statusId="+statusId , jddjLogFileName);

			String loadDocType = "8";//8京东到家 
			String mesType = messageType;//1：订单消息
			String process_status = "N";
			String[] columns1 = {"COMPANYNO","CUSTOMERNO","ORGANIZATIONNO","SHOP","LOAD_DOCTYPE",
					"MESSAGETYPE","BILLID","STATUSID","TIMESTAMP","PROCESS_STATUS","REMARK","CNFFLG"};

			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(" ", Types.VARCHAR),
					new DataValue(" ", Types.VARCHAR),//组织编号=门店编号
					new DataValue(" ", Types.VARCHAR),//映射后的门店
					new DataValue(" ", Types.VARCHAR),//
					new DataValue(loadDocType, Types.VARCHAR),//
					new DataValue(mesType, Types.VARCHAR),//
					new DataValue(billId, Types.VARCHAR),//
					new DataValue(statusId, Types.VARCHAR),//
					new DataValue(timestamp, Types.VARCHAR),//
					new DataValue(process_status, Types.VARCHAR),//
					new DataValue(remark, Types.VARCHAR),//
					new DataValue("Y", Types.VARCHAR)	
			};

			InsBean ib1 = new InsBean("TV_ORDER_MESSAGE", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1));
			HelpTools.writelog_fileName("【JDDJ订单消息保存数据库开始】"+" 消息billId:"+billId + " 消息状态statusId=" + statusId,jddjLogFileName);		
			this.doExecuteDataToDB();
			HelpTools.writelog_fileName("【JDDJ订单消息保存数据库成功】"+" 消息billId:"+billId + " 消息状态statusId=" + statusId,jddjLogFileName);		

		} 
		catch (SQLException e) 
		{
			HelpTools.writelog_fileName("【JDDJ订单消息】异常："+e.getMessage()+ "\r\n req请求内容:" + req,jddjLogFileName);		
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【JDDJ订单消息执行语句】异常："+e.getMessage()+ "\r\n req请求内容:" + req,jddjLogFileName);		
		}

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

	public  String GetJDDJnewOrderResponse(String responseStr) throws Exception
	{
		if (responseStr==null || responseStr.length()==0) 
		{		
			return null;		
		}		

		HelpTools.writelog_fileName("【JDDJ创建新订单消息转码前】" + responseStr, jddjLogFileName);		
		//String	responseStrEncode = HelpTools.getURLEncoderString(responseStr);//JDDJ已经转过一次了，无需 必须先encode，在decode，要不然会丢失一些特殊符号
		String	responseStr_1 = HelpTools.getURLDecoderString(responseStr);//一次转码 （获取的是：%E6%B5%8B%E8%AF%95）	
		String responseStr_2 = HelpTools.getURLDecoderString(responseStr_1);//二次转码（获取为 中文）

		HelpTools.writelog_fileName("【JDDJ创建新订单消息转码后2】" + responseStr_2, jddjLogFileName);
		String[] MTResquest = responseStr_2.split("&");
		if(MTResquest==null || MTResquest.length ==0)
		{
			//writelog_waimai("聚宝盆门店绑定后Token回传消息发送的请求格式有误！");
			HelpTools.writelog_fileName("【JDDJ创建新订单消息】请求格式有误！", jddjLogFileName);
			return null;
		}

		Map<String, String> map_MTResquest = new HashMap<String, String>();

		for (String string_mt : MTResquest) 
		{
			try 
			{											  
				int indexofSpec =  string_mt.indexOf("=");
				String s1 = string_mt.substring(0,indexofSpec);
				String s2 = string_mt.substring(indexofSpec+1,string_mt.length());
				/*String[] ss = string_mt.split("="); //包含多个=会有问题
			  map_MTResquest.put(ss[0], ss[1]);	*/	
				map_MTResquest.put(s1, s2);

			} 
			catch (Exception e) 
			{
				// TODO: handle exception		
				continue;			
			}		  					
		}

		String result = "";
		try 
		{				
			result = map_MTResquest.get("jd_param_json").toString();		
		} 
		catch (Exception e) 
		{
			// TODO: handle exception		
			HelpTools.writelog_fileName("【JDDJ创建新订单消息】解析异常:"+e.getMessage(), jddjLogFileName);	
		}		
		return result;


	}


	public  void UpdateJDDJOrderShippingResponse(String responseStr) throws Exception
	{
		if (responseStr==null || responseStr.length()==0) 
		{		
			return ;		
		}		

		HelpTools.writelog_fileName("【JDDJ订单运单状态消息转码前】" + responseStr, jddjLogFileName);		
		//String	responseStrEncode = HelpTools.getURLEncoderString(responseStr);//JDDJ已经转过一次了，无需 必须先encode，在decode，要不然会丢失一些特殊符号
		String	responseStr_1 = HelpTools.getURLDecoderString(responseStr);//一次转码 （获取的是：%E6%B5%8B%E8%AF%95）	
		String responseStr_2 = HelpTools.getURLDecoderString(responseStr_1);//二次转码（获取为 中文）

		HelpTools.writelog_fileName("【JDDJ订单运单状态消息转码后2】" + responseStr_2, jddjLogFileName);
		String[] MTResquest = responseStr_2.split("&");
		if(MTResquest==null || MTResquest.length ==0)
		{
			//writelog_waimai("聚宝盆门店绑定后Token回传消息发送的请求格式有误！");
			HelpTools.writelog_fileName("【JDDJ订单运单状态消息】请求格式有误！", jddjLogFileName);
			return ;
		}

		Map<String, String> map_MTResquest = new HashMap<String, String>();

		for (String string_mt : MTResquest) 
		{
			try 
			{											  
				int indexofSpec =  string_mt.indexOf("=");
				String s1 = string_mt.substring(0,indexofSpec);
				String s2 = string_mt.substring(indexofSpec+1,string_mt.length());
				/*String[] ss = string_mt.split("="); //包含多个=会有问题
			  map_MTResquest.put(ss[0], ss[1]);	*/	
				map_MTResquest.put(s1, s2);

			} 
			catch (Exception e) 
			{
				// TODO: handle exception		
				continue;			
			}		  					
		}
		String jd_param_json = map_MTResquest.get("jd_param_json").toString();

		try 
		{				
			JSONObject obj = new JSONObject(jd_param_json);
			String result = "";
			int shippingStatus = Integer.parseInt(obj.get("deliveryStatus").toString());
			if(shippingStatus==10)//10 等待抢单
			{
				return;
			}
			String orderNO = obj.get("orderId").toString();//订单号
			String dispatcherName ="";
			if(!obj.isNull("deliveryManName"))
			{
				dispatcherName = obj.get("deliveryManName").toString();//配送员
			}
			String dispatcherMobile ="";
			if(!obj.isNull("deliveryManPhone"))
			{
				dispatcherMobile = obj.get("deliveryManPhone").toString();//配送员电话;
			}


			String loadDocType = "8";
			String companyNO = "99";
			String shopNO = " ";

			String deliveryStatus = "0";//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
			if (shippingStatus == 20) // 20 已抢单 22 更换配送员
			{
				deliveryStatus = "1";
			}
			else if(shippingStatus == 21)//21 配送员取消抢单，等待重新抢单
			{
				deliveryStatus = "7";
			}
			else if(shippingStatus == 22)//22 更换配送员
			{
				deliveryStatus = "1";
			}
			else if(shippingStatus == 23)//23 配送员已到店
			{
				deliveryStatus = "6";
			}
			else if(shippingStatus == 30)//30 取货完成
			{
				deliveryStatus = "2";
			}
			else if(shippingStatus == 40)//40 已完成
			{
				deliveryStatus = "3";
			}
			else //10 等待抢单、25 取货失败、26 取货失败审核驳回、27 取货失败待审核、 35 投递失败
			{
				deliveryStatus = "4";
			}

			UptBean ub1 = null;
			ub1 = new UptBean("TV_ORDER");
			ub1.addUpdateValue("DELIVERYSTUTAS", new DataValue(deliveryStatus, Types.VARCHAR));
			ub1.addUpdateValue("DELNAME", new DataValue(dispatcherName, Types.VARCHAR));
			if (dispatcherMobile != null && dispatcherMobile.trim().length() > 0)
			 {
				ub1.addUpdateValue("DELTELEPHONE", new DataValue(dispatcherMobile, Types.VARCHAR));
			 }			
			ub1.addUpdateValue("UPDATE_TIME", new DataValue(
					new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));


			ub1.addCondition("ORDERNO", new DataValue(orderNO, Types.VARCHAR));
			ub1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

			this.doExecuteDataToDB();
			HelpTools.writelog_waimai(
					"【JDDJ更新配送状态DeliveryStutas成功】" + " 订单号orderNO:" + orderNO + " 配送状态DeliveryStutas=" + deliveryStatus);
			this.pData.clear();

			String sql = "select * from tv_order where LOAD_DOCTYPE='"+loadDocType+"' and orderno='"+orderNO+"' and rownum=1";
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{			
				companyNO = getQDataDetail.get(0).get("COMPANYNO").toString();				
				shopNO = getQDataDetail.get(0).get("SHOP").toString();
			}

			//region 写日志
			try
			{/*			
				OrderStatusLogCreateReq req_log = new OrderStatusLogCreateReq();
				req_log.setDatas(new ArrayList<OrderStatusLogCreateReq.level1Elm>());

				//region订单状态
				OrderStatusLogCreateReq.level1Elm onelv1 = new OrderStatusLogCreateReq().new level1Elm();
				onelv1.setCallback_status("0");
				onelv1.setLoadDocType(loadDocType);

				onelv1.setNeed_callback("N");
				onelv1.setNeed_notify("N");

				onelv1.setO_companyNO(companyNO);

				String o_opName = "骑士："+ dispatcherName;

				onelv1.setO_opName(o_opName);
				onelv1.setO_opNO("");
				String o_shopNO = shopNO;
				if(o_shopNO==null||o_shopNO.isEmpty())
				{
					o_shopNO = " ";
				}
				onelv1.setO_organizationNO(o_shopNO);
				onelv1.setO_shopNO(o_shopNO);
				onelv1.setOrderNO(orderNO);
				String statusType = "2";
				String updateStaus = deliveryStatus;			 
				onelv1.setStatusType(statusType);				 					
				onelv1.setStatus(updateStaus);
				StringBuilder statusTypeNameObj = new StringBuilder();
				String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
				String statusTypeName = statusTypeNameObj.toString();
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);

				String memo = "";
				memo += statusName;

				if (dispatcherMobile != null && dispatcherMobile.isEmpty() == false)
				{
					memo += " 配送电话-->"+dispatcherMobile;
				}
				onelv1.setMemo(memo);
				onelv1.setDisplay("1");

				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);


				req_log.getDatas().add(onelv1);

				String req_log_json ="";
				try
				{			
					ParseJson pj = new ParseJson();
					req_log_json = pj.beanToJson(req_log);
					pj=null;
				}
				catch(Exception e)
				{

				}			   			   			  	
				StringBuilder errorMessage = new StringBuilder();
				boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, req_log_json, errorMessage);
				if(nRet)
				{		  		 
					HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】"+" 订单号orderNO:"+orderNO);
				}
				else
				{			  		 
					HelpTools.writelog_waimai("【写表tv_orderStatuslog异常】"+errorMessage.toString()+" 订单号orderNO:"+orderNO);
				}

				//endregion

			*/}
			catch (Exception  e)
			{
				HelpTools.writelog_fileName("【JDDJ订单运单状态消息写日志】异常:"+e.getMessage(), jddjLogFileName);	

			}
			//endregion


		} 
		catch (Exception e) 
		{
			// TODO: handle exception		
			HelpTools.writelog_fileName("【JDDJ订单运单状态消息】解析异常:"+e.getMessage(), jddjLogFileName);	
		}		



	}

}
