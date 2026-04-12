package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.utils.DispatchService;

//电商订单确认发货和物流取件
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class EcOrderConfirmDelivery extends InitJob
{

	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	Logger logger = LogManager.getLogger(EcOrderConfirmDelivery.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public EcOrderConfirmDelivery() 
	{		

	}

	public EcOrderConfirmDelivery(String eId,String shopId,String organizationNO, String billNo) 
	{
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}


	public String doExe()
	{
		//返回信息
		String sReturnInfo="";
		//此服务是否正在执行中
		if (bRun && pEId.equals(""))
		{		
			logger.info("\r\n*********订单发货和物流取件EcOrderConfirmDelivery正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-订单发货和物流取件EcOrderConfirmDelivery正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********订单发货和物流取件EcOrderConfirmDelivery定时调用Start:************\r\n");

		ParseJson pj=new ParseJson();

		try
		{
			//这里只查询待发货和待物流取件的单据
			String sqlShipment="select t.eid,t.SHOPID,t.shipmentno,t.ecplatformno,t.status from DCP_shipment t "
					+ "where  t.status='100' "
					+ "and t.ECPLATFORMNO in ('shopee','yahoosuper','91app','shopee','letian','pchome','momo','general','3','4') "
					+ "and (t.status=1 or t.status=2)";
			List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sqlShipment, null);

			if (getQData != null || getQData.isEmpty() == false ) 
			{				
				for (Map<String, Object> par : getQData) 
				{
					String json="";
					try 
					{
						Map<String,Object> jsonMap=new HashMap<String,Object>();

						List<String> arrShipmentno = new  ArrayList<String>();
						arrShipmentno.add(par.get("SHIPMENTNO").toString());

						jsonMap.put("serviceId", "DCP_OrderECShippingCreate");
						//这个token是无意义的
						jsonMap.put("token", "abecbc7b42eb286a0d1f8587a9df97e5");
						jsonMap.put("eEId", par.get("EID").toString());//企业编码
						jsonMap.put("eShop", par.get("SHOPID").toString());//门店编号
						jsonMap.put("eOrganizationNO", par.get("SHOPID").toString());//组织编码

						String sShipmentStatus=par.get("STATUS").toString();

						if (sShipmentStatus.equals("1")) 
						{
							jsonMap.put("opType", "1");//1:发货 2：安排取件
						}
						else 
						{
							jsonMap.put("opType", "2");//1:发货 2：安排取件
						}

						jsonMap.put("shipmentNo", arrShipmentno);//发货单号
						jsonMap.put("ecPlatformNo", par.get("ECPLATFORMNO").toString());//发货单号
						jsonMap.put("Jobway", "1");//1：JOB定时器调用的 2：中台前端页面调用的

						//json
						json=pj.beanToJson(jsonMap);			

						DispatchService ds = DispatchService.getInstance();
						String resXml = ds.callService(json, StaticInfo.dao);
					} 
					catch (Exception e) 
					{
						logger.error("\r\n***********订单发货和物流取件EcOrderConfirmDelivery异常:EID=" + par.get("EID").toString() + "SHOPID=" +par.get("SHOPID").toString() +"SHOPID="+ par.get("SHOPID").toString() + "\r\n Json=" + json + "\r\n"+ e.getMessage());
					}
					finally 
					{
						logger.error("\r\n***********订单发货和物流取件EcOrderConfirmDelivery处理出货单号=:"+par.get("SHIPMENTNO").toString() +"\r\n");
					}

				}
			}

		}
		catch (Exception e) 
		{				
			try 
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);		
				
				pw.flush();
				pw.close();			
				
				errors.flush();
				errors.close();

				logger.error("\r\n******订单发货和物流取件EcOrderConfirmDelivery报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******订单发货和物流取件EcOrderConfirmDelivery报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();

		}
		finally 
		{
			pj=null;
			bRun=false;//
			logger.info("\r\n*********订单发货和物流取件EcOrderConfirmDelivery定时调用End:************\r\n");
		}
		return sReturnInfo;

	}



}
