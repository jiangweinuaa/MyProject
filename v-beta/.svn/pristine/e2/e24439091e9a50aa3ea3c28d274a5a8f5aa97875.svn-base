package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.logistics.SevenEleven;

//*****此job暫時不用，改用EcOrderConfirmDelivery*******
//*************************************************
//向物流公司上传托运资料===大智通专用
//(处理时间每日 03:00、05:00、10:15、11:35， 商家上传至FTP 的 SIN 目錄)
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class LgUploadExpressNoDZT extends InitJob
{

	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";
	String pLgplatformno="";

	Logger logger = LogManager.getLogger(LgUploadExpressNo.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public LgUploadExpressNoDZT()
	{

	}

	public LgUploadExpressNoDZT(String eId,String shopId,String organizationNO, String billNo,String platformno)
	{
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
		pLgplatformno=platformno;
	}

	public String doExe()
	{
		//返回信息
		String sReturnInfo="";
		//此服务是否正在执行中
		if (bRun && pEId.equals(""))
		{		
			logger.info("\r\n*********【大智通-物流上传托运单资料】正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-【大智通-物流上传托运单资料】正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********【大智通-物流上传托运单资料】定时调用Start:************\r\n");

		SevenEleven se=null;


		try 
		{
			//这里是 1:新建 2：已发货 3：退货 4：换货 5：已上传物流托运资料 6：已完成
			String sql="select a.EID, a.SHOPID, a.shipmentno,a.ORDERER,a.RECEIVER,EXPRESSNO,COLLECTAMT,DELIVERYTYPE,GETSHOP,RECEIVER_MOBILE,PAYSTATUS,REALAMT,RECEIVER_EMAIL," + 
					"COUNT(b.Shipmentno) as DETAILCOUNT from DCP_shipment a inner join DCP_shipment_original b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.shipmentno=b.shipmentno " + 
					"where a.expressno is not null and a.status=2 and a.status='100' and a.LGPLATFORMNO='dzt' ";


			//根据条件
			if (pBillNo.equals("")==false) 
			{
				sql+=" and a.EID='"+pEId+"' and a.shipmentno='"+pBillNo+"' and a.SHOPID='"+pShop+"' ";
			}

			sql+=" group by a.EID, a.SHOPID, a.shipmentno,a.ORDERER,a.RECEIVER,EXPRESSNO,COLLECTAMT,DELIVERYTYPE,GETSHOP,RECEIVER_MOBILE,PAYSTATUS,REALAMT,RECEIVER_EMAIL ";

			//查询物流厂商API信息
			String sqlLG="select * from OC_logistics  where status='100' and LGPLATFORMNO='dzt' ";		

			List<Map<String, Object>> getLGData=this.doQueryData(sqlLG, null);		

			if (getLGData==null && getLGData.size()==0) 
			{
				logger.error("\r\n******【大智通-物流上传托运单资料】货运平台基本资料未设置******\r\n");
			}
			else 
			{
				List<Map<String, Object>> sqllist=this.doQueryData(sql, null);
				if (sqllist != null && sqllist.isEmpty() == false)
				{
					int countSize=sqllist.size();

					String apiUrl="";//
					String ecNo="";//
					String sonEcNo="";//			
					String ftpuid="";//
					String ftppwd="";//

					//执行
					List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	

					//多笔托运单
					se=new SevenEleven();

					//货运单上传
					JSONObject OrderDoc = new JSONObject();		
					JSONArray Order = new JSONArray();

					for (int i = 0; i < countSize; i++) 
					{
						String shipCompany=sqllist.get(i).get("EID").toString();//货运单企业代码
						String shipShop=sqllist.get(i).get("SHOPID").toString();//货运单门店
						String shipmentNo=sqllist.get(i).get("SHIPMENTNO").toString();//货运单号
						String expressno=sqllist.get(i).get("EXPRESSNO").toString();//物流单号

						if (getLGData!=null && getLGData.size()>0) 
						{
							for (Map<String, Object> map : getLGData) 
							{
								apiUrl=map.get("API_URL").toString();//
								String eId=map.get("EID").toString();
								ecNo=map.get("DZT_MOTHERVENDORNO").toString();
								sonEcNo=map.get("DZT_SONVENDORNO").toString();						
								ftpuid=map.get("DZT_FTPUID").toString();	
								ftppwd=map.get("DZT_FTPPWD").toString();	

								//
								String order_name=sqllist.get(i).get("ORDERER").toString();
								String receiver_name=sqllist.get(i).get("RECEIVER").toString();
								String collectamt=sqllist.get(i).get("COLLECTAMT").toString();
								String deliverytype=sqllist.get(i).get("DELIVERYTYPE").toString();
								String getshopno=sqllist.get(i).get("GETSHOP").toString();
								String receiver_mobile=sqllist.get(i).get("RECEIVER_MOBILE").toString();
								String paystatus=sqllist.get(i).get("PAYSTATUS").toString();
								String realamt=sqllist.get(i).get("REALAMT").toString();
								String receiver_email=sqllist.get(i).get("RECEIVER_EMAIL").toString();
								//明细笔数
								int detailCount=0;
								String sDCount=sqllist.get(i).get("DETAILCOUNT").toString();
								if (PosPub.isNumeric(sDCount)) 
								{
									detailCount=Integer.parseInt(sDCount);
								}

								//
								SimpleDateFormat myTempdf = new SimpleDateFormat("yyyy-MM-dd");
								Calendar myTempcal = Calendar.getInstance();
								String sDate=myTempdf.format(myTempcal.getTime());
								//System.out.println(sDate);


								JSONObject OrderBody = new JSONObject();
								OrderBody.put("OPMode", "A");//通路別 A 表示7-Eleven 
								OrderBody.put("EshopOrderNo", shipmentNo);//eShop訂單編號 ，shipmentno出货单号
								OrderBody.put("EshopOrderDate", sDate);//eShop 訂單日期，出货单日期yyyy-mm-dd
								if (paystatus.equals("1")||paystatus.equals("2")) 
								{
									OrderBody.put("ServiceType", "1");//服務型態代碼， 付款取貨：1   取貨不付款：3 
								}
								else 
								{
									OrderBody.put("ServiceType", "3");//服務型態代碼， 付款取貨：1   取貨不付款：3 
								}

								OrderBody.put("ShopperName", order_name);//購買人姓名，
								OrderBody.put("ReceiverName", receiver_name);//收貨人姓名，

								//
								String mobilelast="";							
								if(receiver_mobile.length()>3)
								{
									mobilelast=receiver_mobile.substring(receiver_mobile.length()-3);
								}

								OrderBody.put("ReceiverMobilPhone", mobilelast);//收貨人行動電話，手機末三碼 (如 678) 
								OrderBody.put("ReceiverEmail", receiver_email);//收貨人 E-mail，
								OrderBody.put("OrderAmount",realamt);//訂單總金額，請務必帶入金額 

								//货运单商品明细，多笔
								JSONArray OrderDetail = new JSONArray();
								for (int b = 0; b < detailCount; b++) 
								{
									JSONObject OrderDetailBody = new JSONObject();		
									OrderDetailBody.put("ProductId", "");//品項編號
									OrderDetailBody.put("ProductName", "");//品項名稱
									OrderDetailBody.put("Quantity", "");//數量
									OrderDetailBody.put("Unit", "");//單位
									OrderDetailBody.put("UnitPrice", "");//單價
									OrderDetail.put(OrderDetailBody);
								}
								OrderBody.put("OrderDetail", OrderDetail);

								//货运单
								JSONArray ShipmentDetail  = new JSONArray();			
								JSONObject ShipmentDetailBody = new JSONObject();		
								ShipmentDetailBody.put("ShipmentNo", expressno);//配送編號,物流单号
								ShipmentDetailBody.put("ShipDate", sDate);//出貨日期,yyyy-mm-dd 包裹需於 ShipDate 當日 14:00 前送達物流中心

								//日期格式处理
								String InputDate=sDate.substring(0,4)+sDate.substring(5,7)+sDate.substring(8,10);
								String sOutDate=PosPub.GetStringDate(InputDate, 8);
								sOutDate=sOutDate.substring(0,4) + "-" + sOutDate.substring(4,6)+"-" + sOutDate.substring(6,8);

								ShipmentDetailBody.put("ReturnDate", sOutDate);//門市店退貨日期 (為 ShipDate+8 天) 
								ShipmentDetailBody.put("LastShipment", "Y");//是否為本訂單的最 後一次出貨(Y/N) 
								ShipmentDetailBody.put("ShipmentAmount", realamt);//出貨單金額 
								ShipmentDetailBody.put("StoreId", getshopno);//門市店代碼 
								ShipmentDetailBody.put("EshopType", "04");//商品型態代,請固定帶入 04 百货 				
								ShipmentDetail.put(ShipmentDetailBody);
								OrderBody.put("ShipmentDetail", ShipmentDetail);

								Order.put(OrderBody);
							}
				
						}


						//更新大智通物流单信息
						UptBean ubec=new UptBean("OC_SHIPMENT");
						ubec.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
						ubec.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
						ubec.addCondition("SHIPMENTNO", new DataValue(shipmentNo, Types.VARCHAR));

						//已上传托运单
						ubec.addUpdateValue("STATUS", new DataValue(5, Types.VARCHAR));
						lstData.add(new DataProcessBean(ubec));

					}

					OrderDoc.put("Order",Order);

					String resbody=OrderDoc.toString();

					//
					SimpleDateFormat Tempdf = new SimpleDateFormat("yyyyMMdd");
					Calendar Tempcal = Calendar.getInstance();
					String sfDate=Tempdf.format(Tempcal.getTime());
					//System.out.println(sfDate);

					//大智通上传托运资料
					boolean bRet=se.SIN(apiUrl, ecNo, sonEcNo, sfDate, ftpuid, ftppwd, resbody);
					if (bRet) 
					{
						//执行SQL
						StaticInfo.dao.useTransactionProcessData(lstData);		
					}
					else 
					{
						//清理
						lstData.clear();
						lstData=null;
					}
				}
				else 
				{
					logger.error("\r\n******【大智通-物流上传托运单资料】找不到大智通货运单资料******\r\n");
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

				logger.error("\r\n******【大智通-物流上传托运单资料】报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******【大智通-物流上传托运单资料】报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();
		}
		finally 
		{
			se=null;

			bRun=false;//
			logger.info("\r\n*********【大智通-物流上传托运单资料】定时调用End:************\r\n");
		}
		return sReturnInfo;		

	}

}
