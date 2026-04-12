package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.logistics.Cvs;
import com.dsc.spos.utils.logistics.Egs;
import com.dsc.spos.utils.logistics.Htc;


//*****此job暫時不用，改用EcOrderConfirmDelivery*******
//*************************************************
//向物流公司上传托运资料(不包括大智通)
//大智通单独job处理，因为有固定时间点需要，放在这里不够灵活,LgUploadExpressNoDZT
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class LgUploadExpressNo extends InitJob
{

	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";
	String pLgplatformno="";

	Logger logger = LogManager.getLogger(LgUploadExpressNo.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public LgUploadExpressNo()
	{

	}

	public LgUploadExpressNo(String eId,String shopId,String organizationNO, String billNo,String platformno)
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
			logger.info("\r\n*********【物流上传托运单资料】正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-【物流上传托运单资料】正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********【物流上传托运单资料】定时调用Start:************\r\n");

		Egs egs=null;
		Htc htc=null;
		Cvs cvs=null;

		try 
		{
			//这里是 1:新建 2：已发货 3：退货 4：换货 5：已安排物流取件 6：已完成
			//逆物流
			String sql="select * from DCP_shipment t where ((t.expressno is not null and t.status=2) OR (t.GREENWORLD_RTNORDERNO is not null) ) and t.status='100' and t.LGPLATFORMNO<>'dzt' ";

			//根据条件
			if (pBillNo.equals("")==false) 
			{
				sql+=" and t.EID='"+pEId+"' and t.shipmentno='"+pBillNo+"' and t.SHOPID='"+pShop+"' ";
			}

			//查询物流厂商API信息
			String sqlLG="select * from OC_logistics  where status='100' and LGPLATFORMNO<>'dzt' ";		

			//根据条件
			if (pBillNo.equals("")==false) 
			{
				sqlLG+=" and LGPLATFORMNO='"+pLgplatformno+"' ";
			}
			List<Map<String, Object>> getLGData=this.doQueryData(sqlLG, null);			

			List<Map<String, Object>> sqllist=this.doQueryData(sql, null);
			if (sqllist != null && sqllist.isEmpty() == false)
			{
				int countSize=sqllist.size();

				for (int i = 0; i < countSize; i++) 
				{
					String lgPlatformNo=sqllist.get(i).get("LGPLATFORMNO").toString();
					String expressBiltype=sqllist.get(i).get("EXPRESSBILLTYPE").toString();//托运单别
					String address=sqllist.get(i).get("RECEIVER_ADDRESS").toString();//收货地址
					String shipCompany=sqllist.get(i).get("EID").toString();//货运单企业代码
					String shipShop=sqllist.get(i).get("SHOPID").toString();//货运单门店
					String shipmentNo=sqllist.get(i).get("SHIPMENTNO").toString();//货运单号
					String expressno=sqllist.get(i).get("EXPRESSNO").toString();//物流单号
					String rtnExpressno=sqllist.get(i).get("GREENWORLD_RTNORDERNO").toString();//逆物流单号
					
					if (lgPlatformNo.equals("htc")) //新竹物流
					{
						//
						Map<String, Object> map_condition = new HashMap<String, Object>();
						map_condition.put("LGPLATFORMNO", "htc");		
						List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLGData,map_condition,false);	
						if (getQHeader1!=null && getQHeader1.size()>0) 
						{
							for (Map<String, Object> map : getQHeader1) 
							{
								String apiUrl=map.get("API_URL").toString();//
								String eId=map.get("EID").toString();
								String lgCompanyno=map.get("LGCOMPANYNO").toString();
								String lgPassword=map.get("LGPASSWORD").toString();

								htc=new Htc();

								//
								String[] ordersnList=new String[1];
								ordersnList[0]=shipmentNo;							

								//
								String[] expressNOList=new String[1];
								
								if (rtnExpressno.equals("")) 
								{
									expressNOList[0]=expressno;
								}
								else //逆物流
								{
									expressNOList[0]=rtnExpressno;
								}							

								String resbody=htc.TransReport_Json(apiUrl, lgCompanyno, lgPassword, ordersnList, expressNOList);
								
								JSONArray jsonres=new JSONArray(resbody);
								for (int a = 0; a < jsonres.length(); a++) 
								{								
									if (jsonres.getJSONObject(a).isNull("ErrMsg")) 
									{
										//System.out.println("HTC新竹物流接口TransReport_Json调用失败");
									}
									else 
									{
										String ErrMsg=jsonres.getJSONObject(a).getString("ErrMsg");//错误信息

										if (ErrMsg.trim().equals("")==false) 
										{
											//System.out.println("HTC新竹物流接口TransReport_Json调用失败=" + ErrMsg);
										}
										else 
										{
											String Num=jsonres.getJSONObject(a).getString("Num");//序号1
											String success=jsonres.getJSONObject(a).getString("success");//新增 Y 修改 R 失敗 
											String edelno=jsonres.getJSONObject(a).getString("edelno");//托运单号
											String epino=jsonres.getJSONObject(a).getString("epino");//订单编号

											//System.out.println(epino);

											//更新新竹物流单信息
											UptBean ubec=new UptBean("OC_SHIPMENT");
											ubec.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
											ubec.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
											ubec.addCondition("SHIPMENTNO", new DataValue(shipmentNo, Types.VARCHAR));

											//安排取件
											ubec.addUpdateValue("STATUS", new DataValue(5, Types.VARCHAR));

											//执行
											List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	
											lstData.add(new DataProcessBean(ubec));
											StaticInfo.dao.useTransactionProcessData(lstData);		
										}
									}
								}
							}
		
						}
						map_condition=null;
						getQHeader1=null;
					}
					else if (lgPlatformNo.equals("egs")) //黑猫宅急便
					{
						//黑猫
						Map<String, Object> map_condition = new HashMap<String, Object>();
						map_condition.put("LGPLATFORMNO", "egs");		
						List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLGData,map_condition,false);	
						if (getQHeader1!=null && getQHeader1.size()>0) 
						{
							for (Map<String, Object> map : getQHeader1) 
							{
								String apiUrl=map.get("API_URL").toString();//
								String eId=map.get("EID").toString();
								String customer_id=map.get("CUSTOMERNO").toString();

								egs=new Egs();
								String tracking_number=expressno;
								String order_no=shipmentNo;
								String receiver_name=sqllist.get(i).get("RECEIVER").toString();
								String receiver_address=sqllist.get(i).get("RECEIVER_ADDRESS").toString();
								String receiver_suda5=sqllist.get(i).get("RECEIVER_FIVECODE").toString();
								String receiver_mobile=sqllist.get(i).get("RECEIVER_MOBILE").toString();
								String receiver_phone=sqllist.get(i).get("RECEIVER_PHONE").toString();
								String sender_name=PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "SenderName");
								String sender_address=PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "SenderAddress");
								String sender_suda5=PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "SenderSuDa5");
								String sender_phone=PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "SenderPhone");
								String product_price=sqllist.get(i).get("COLLECTAMT").toString();
								String product_name=sqllist.get(i).get("PRODUCTNAME").toString();
								String comment=sqllist.get(i).get("MEMO").toString();
								String package_size=sqllist.get(i).get("MEASURENO").toString();
								
								//数据库 1.常溫 2.冷藏 3.冷凍
								//"0001"=常溫|"0002"=冷藏|"0003"=冷凍
								//黑猫需处理一下
								String temperature=sqllist.get(i).get("TEMPERATELAYERNO").toString();
								
								String temperatureCST="0001";
								if (temperature.equals("2")) 
								{
									temperatureCST="0002";
								}
								else if (temperature.equals("3")) 
								{
									temperatureCST="0003";
								}
								else 
								{
									temperatureCST="0001";
								}
								
								String distance=sqllist.get(i).get("DISTANCENO").toString();
								String delivery_date=sqllist.get(i).get("SHIPDATE").toString();//指定配达日期  2011-04-25
								if (delivery_date.length()==8) 
								{
									delivery_date=delivery_date.substring(0,4)+"-" + delivery_date.substring(4,6)+"-" + delivery_date.substring(6,8);
								}
								
								
								//数据库 1.不分時段 2.早上(09~12點) 3.中午(12~17點) 4.下午(17~20點)
								//"1"=9~12 時|"2"=12~17 時|"3"=17~20 時|"4"=不限時
								//黑猫需处理一下
								String delivery_timezone=sqllist.get(i).get("SHIPHOURTYPE").toString();//指定配达时段  1=9~12時  2=12~17時  3=17~20時  4=不限時

								//
								String delivery_timezoneCST="4";
								
								if (delivery_timezone.equals("2")) 
								{
									delivery_timezoneCST="1";
								}
								else if (delivery_timezone.equals("3")) 
								{
									delivery_timezoneCST="2";
								}
								else if (delivery_timezone.equals("4")) 
								{
									delivery_timezoneCST="3";
								}
								else 
								{
									delivery_timezoneCST="4";
								}
								
								SimpleDateFormat myTempdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Calendar myTempcal = Calendar.getInstance();
								String sDatetime=myTempdf.format(myTempcal.getTime());
								//System.out.println(sDatetime);

								String create_time=sDatetime;//建立时间 2011-04-24 11:27:59
								String print_time=sDatetime;//打印时间  2011-04-24 11:27:59
								String account_id="";
								String member_no="";
								String taxin="0";
								String insurance="0";
								String resbody="";
								if (rtnExpressno.equals("")) 
								{
									resbody=egs.transfer_waybill(apiUrl, customer_id, tracking_number, order_no, receiver_name, receiver_address, receiver_suda5, receiver_mobile, receiver_phone, sender_name, sender_address, sender_suda5, sender_phone, product_price, product_name, comment, package_size, temperatureCST, distance, delivery_date, delivery_timezoneCST, create_time, print_time, account_id, member_no, taxin, insurance);
								}
								else 
								{
									resbody=egs.transfer_waybill(apiUrl, customer_id, tracking_number, "rtn"+order_no, sender_name, sender_address, sender_suda5, sender_phone, "", receiver_name, receiver_address, receiver_suda5, receiver_mobile, product_price, product_name, comment, package_size, temperatureCST, distance, delivery_date, delivery_timezoneCST, create_time, print_time, account_id, member_no, taxin, insurance);
								}
								
								String[] splitStrings=resbody.split("&");

								if (splitStrings.length>0) 
								{
									//OK|ERROR
									String[] splitStatus=splitStrings[0].split("=");
									String status=splitStatus[1];

									if (status.equals("ERROR")) 
									{
										//message错误信息
										String[] splitMessage=splitStrings[1].split("=");
										String message=splitMessage[1];
										message=message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");  //%
										message = message.replaceAll("\\+", "%2B");  //+
										message = URLDecoder.decode(message, "UTF-8");	
										//System.out.println(message);

										logger.info("\r\n*********【物流上传托运单资料】上传托运单接口transfer_waybill错误信息："+message+"************\r\n");
									}
									else 
									{
										//更新黑猫货运单信息
										UptBean ubec=new UptBean("OC_SHIPMENT");
										ubec.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
										ubec.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
										ubec.addCondition("SHIPMENTNO", new DataValue(shipmentNo, Types.VARCHAR));

										//安排取件
										ubec.addUpdateValue("STATUS", new DataValue(5, Types.VARCHAR));

										//执行
										List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	
										lstData.add(new DataProcessBean(ubec));
										StaticInfo.dao.useTransactionProcessData(lstData);		
									}
								}
								else 
								{
									logger.info("\r\n*********【物流上传托运单资料】上传托运单接口transfer_waybill调用失败************\r\n");
								}
							}
			
						}
						map_condition=null;
						getQHeader1=null;

					}
					else if (lgPlatformNo.equals("sfexpress")) //顺丰速达
					{


					}
					else if (lgPlatformNo.equals("cvs")) //便利达康
					{
						//便利达康
						Map<String, Object> map_condition = new HashMap<String, Object>();
						map_condition.put("LGPLATFORMNO", "cvs");		
						List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLGData,map_condition,false);	
						if (getQHeader1!=null && getQHeader1.size()>0) 
						{
							for (Map<String, Object> map : getQHeader1) 
							{
								String apiUrl=map.get("API_URL").toString();//
								String eId=map.get("EID").toString();
								String ecNo=map.get("CVS_MOTHERVENDORNO").toString();
								String collectNo=map.get("CVS_COLLECTNO").toString();
								String dcNo=map.get("CVS_LARGELOGISTICSNO").toString();

								cvs=new Cvs();

								String receiver_name=sqllist.get(i).get("RECEIVER").toString();
								String collectamt=sqllist.get(i).get("COLLECTAMT").toString();
								String deliverytype=sqllist.get(i).get("DELIVERYTYPE").toString();
								String getshopno=sqllist.get(i).get("GETSHOP").toString();
								String receiver_mobile=sqllist.get(i).get("RECEIVER_MOBILE").toString();
								String paystatus=sqllist.get(i).get("PAYSTATUS").toString();
								String realamt=sqllist.get(i).get("REALAMT").toString();


								//
								List<Map<String, Object>> OrderList=new ArrayList<Map<String,Object>>();

								//单笔
								Map<String, Object> map1=new HashMap<String, Object>();
								map1.put("ODNO", expressno);//物流单号

								if (deliverytype.equals("8")) 
								{
									map1.put("STNO", "F"+getshopno);//取货门店编号，代码(F：全家、L：萊爾富、K：OK)+门市编号
								}
								else if (deliverytype.equals("10")) 
								{
									map1.put("STNO", "L"+getshopno);//取货门店编号，代码(F：全家、L：萊爾富、K：OK)+门市编号
								}
								else if (deliverytype.equals("11")) 
								{
									map1.put("STNO", "K"+getshopno);//取货门店编号，代码(F：全家、L：萊爾富、K：OK)+门市编号
								}

								map1.put("AMT", collectamt);//代收金额， >=0 int
								map1.put("CUTKNM",receiver_name);//取货人姓名
								//
								String mobilelast="";							
								if(receiver_mobile.length()>3)
								{
									mobilelast=receiver_mobile.substring(receiver_mobile.length()-3);
								}
								map1.put("CUTKTL", mobilelast);//手机末三码，無資料代空值 
								map1.put("PRODNM", "0");//商品別代，0 : 一般商品  1 : 票券商品

								String ecname=PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "ECName");
								String ecwebsite=PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "ECWebsite");
								String echotline=PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "ECHotline");

								map1.put("ECWEB", ecname+ecwebsite);//EC網站名，例：康迅數位 www.payeasy.com.tw
								map1.put("ECSERTEL", echotline);//EC 網站客服電話，例：02-XXXXXXXX 
								map1.put("REALAMT", realamt);//商品實際金額，int 必須為 0 或正整數。 ※選擇取貨不付款與取貨付款時，需在此欄位填上貨物「實際金額」，以利後續查帳事宜
								if (paystatus.equals("1") ||paystatus.equals("2") ) 
								{
									map1.put("TRADETYPE", "1");//交易方式識別碼(货到付款标记) ，1：取貨付款 3：取貨不付款 
								}
								else 
								{
									map1.put("TRADETYPE", "3");//交易方式識別碼(货到付款标记) ，1：取貨付款 3：取貨不付款 
								}

								OrderList.add(map1);

								//上传
								boolean bRet=cvs.F10_OrderUpload(apiUrl, ecNo, collectNo, dcNo, OrderList);

								if (bRet) 
								{
									//更新便利达康货运单信息
									UptBean ubec=new UptBean("OC_SHIPMENT");
									ubec.addCondition("EID", new DataValue(shipCompany, Types.VARCHAR));
									ubec.addCondition("SHOPID", new DataValue(shipShop, Types.VARCHAR));
									ubec.addCondition("SHIPMENTNO", new DataValue(shipmentNo, Types.VARCHAR));

									//已上传托运单
									ubec.addUpdateValue("STATUS", new DataValue(5, Types.VARCHAR));

									//执行
									List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	
									lstData.add(new DataProcessBean(ubec));
									StaticInfo.dao.useTransactionProcessData(lstData);		
								}	
							}
							

						}
						map_condition=null;
						getQHeader1=null;
					}
					else if (lgPlatformNo.equals("mingjie")) //大物流十公斤以上
					{

					}
					else if (lgPlatformNo.equals("chinapost")) //中华邮政
					{

					}
					else 
					{

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

				logger.error("\r\n******【物流上传托运单资料】报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******【物流上传托运单资料】报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();
		}
		finally 
		{
			egs=null;
			htc=null;
			cvs=null;

			bRun=false;//
			logger.info("\r\n*********【物流上传托运单资料】定时调用End:************\r\n");
		}
		return sReturnInfo;		

	}



}
