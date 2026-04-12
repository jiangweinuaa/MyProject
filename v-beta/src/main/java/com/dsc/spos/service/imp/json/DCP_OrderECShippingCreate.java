package com.dsc.spos.service.imp.json;

import java.net.URLDecoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECShippingCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECShippingCreateRes;
import com.dsc.spos.scheduler.job.EcRakuten;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.ec.Rakuten;
import com.dsc.spos.utils.ec.Shopee;
import com.dsc.spos.utils.logistics.Cvs;
import com.dsc.spos.utils.logistics.Egs;
import com.dsc.spos.utils.logistics.Htc;
import com.dsc.spos.utils.logistics.SevenEleven;
import com.google.gson.reflect.TypeToken;

/**
 * 发货和安排取件
 * @author yuanyy 2019-03-19
 *
 */
public class DCP_OrderECShippingCreate extends SPosAdvanceService<DCP_OrderECShippingCreateReq, DCP_OrderECShippingCreateRes> 
{

	Logger logger = LogManager.getLogger(DCP_OrderECShippingCreate.class.getName());

	@Override
	protected void processDUID(DCP_OrderECShippingCreateReq req, DCP_OrderECShippingCreateRes res) throws Exception 
	{
		//JOB定时器调用，因为定时器那里无法使用有效token取门店
		if (req.getJobway()==null) 
		{
			req.setJobway("");
		}

		if (req.getJobway().equals("1")) 
		{
			req.seteId(req.geteEId());	
			req.seteShopId(req.geteShopId());
			req.setOrganizationNO(req.geteOrganizationNO());
		}

		String eId = req.geteId();
		String myshop = req.getShopId();
		String opType = req.getOpType(); // 1:发货 2:安排取件 
		String list[] = req.getShipmentNo();

		for (int i = 0; i < list.length; i++) 
		{
			list[i] = list[i].replaceAll(list[i],"'" + list[i] + "'");
		}
		String shipmentNo = StringUtils.join(list,",");

		//发货失败的单号
		StringBuffer error_ShipmentNoString=new StringBuffer("");



		//电商SQL
		String sqlECommerce="select * from OC_ECOMMERCE A where A.EID='"+eId+"' and A.ECPLATFORMNO='"+req.getEcPlatformNo()+"'";
		List<Map<String, Object>> getECommerceData=this.doQueryData(sqlECommerce, null);

		//货运单查找
		StringBuffer sqlShipment=new StringBuffer("select t.*, to_date(t.shipDate , 'yyyy-mm-dd') AS deliveryDate  from DCP_shipment t "
				+ " inner join OC_order a on t.EID=a.EID and t.SHOPID=a.SHOPID and t.ec_orderno=a.orderno "
				+ " where t.EID='"+eId+"' and (a.SHOPID='"+myshop+"' or a.shippingshop='"+myshop+"') and t.ECPLATFORMNO='"+req.getEcPlatformNo()+"' "
				+ " and t.status='100' and t.shipmentno in ("+shipmentNo+") ");
		if(opType.equals("2"))//
		{
			sqlShipment.append(" and t.status=2 ");
		}
		else 
		{
			sqlShipment.append(" and t.status=1 ");
		}

		List<Map<String, Object>> getShipmentData=this.doQueryData(sqlShipment.toString(), null);

		//物流 货运厂商 SQL
		String sqlLOGISTICS="select * from OC_LOGISTICS A where A.EID='"+eId+"' ";
		List<Map<String, Object>> getLOGISTICSData=this.doQueryData(sqlLOGISTICS, null);

		// htc 
		String apiUrl = "";
		String company = "";
		String password = "";
		// egs 
		String customerId = "";

		//查找当前门店的寄件人信息		
		//先取门店参数platform_baseset，取不到就取模板参数platform_basesettemp,union会自动根据第一列排序
		String sqlSenderMsg="select 1 as ID,t.item,t.itemvalue as def from platform_baseset t where item IN ('SenderName','SenderSuDa5','SenderPhone','SenderAddress') AND status='100' " 
				+" and EID = '"+req.geteId()+"' and SHOPID='"+req.getShopId()+"' "
				+" union "
				+ " select 2 as ID,t.item,t.def from platform_basesettemp t where item IN ('SenderName','SenderSuDa5','SenderPhone','SenderAddress') AND status='100' "
				+ " and EID = '"+req.geteId()+"'";


		List<Map<String, Object>> getSenderData=this.doQueryData(sqlSenderMsg, null);
		String sendName = "";
		String sendAddress = "";
		String sendPhone = "";
		String sendSuDa5 = "";

		if(getSenderData.isEmpty() == false)
		{
			for (Map<String, Object> map : getSenderData) 
			{
				String item = map.get("ITEM").toString();
				String itemValue = map.get("DEF").toString();

				if(item.equals("SenderName")&& sendName.equals(""))
				{
					sendName = itemValue ;
					continue;
				}
				if(item.equals("SenderAddress")&& sendAddress.equals(""))
				{
					sendAddress = itemValue ;
					continue;
				}
				if(item.equals("SenderPhone")&& sendPhone.equals(""))
				{
					sendPhone = itemValue ;
					continue;
				}
				if(item.equals("SenderSuDa5")&& sendSuDa5.equals(""))
				{
					sendSuDa5 = itemValue ;
					continue;
				}

			}

		}


		//訂單日誌時間
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sysDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String orderStatusLogTimes=sysDatetime.format(calendar.getTime());

		//接單日誌
		String[] columnsORDER_STATUSLOG = 
			{ 
					"EID", 
					"ORGANIZATIONNO", 
					"SHOPID", 
					"ORDERNO", 
					"LOAD_DOCTYPE",
					"STATUSTYPE", 
					"STATUSTYPENAME", 
					"STATUS", 
					"STATUSNAME", 
					"NEED_NOTIFY", 
					"NOTIFY_STATUS",
					"NEED_CALLBACK", 
					"CALLBACK_STATUS", 
					"OPNO", 
					"OPNAME", 
					"UPDATE_TIME",
					"MEMO", 
					"STATUS" 
			};


		if(opType.equals("1"))// 发货
		{ 

			if (getECommerceData != null && getECommerceData.isEmpty() == false)
			{

				apiUrl=getECommerceData.get(0).get("API_URL").toString();
				
				//
				if (getShipmentData != null && getShipmentData.isEmpty() == false)
				{
					int countShip=getShipmentData.size();

					List<String> ecOrderNoList = new ArrayList<>();

					Shopee shopee=null;		
					Rakuten rt=null;

					for (int t = 0; t < countShip; t++) 
					{
						myshop=getShipmentData.get(t).get("SHOPID").toString();
						String sShipmentNo=getShipmentData.get(t).get("SHIPMENTNO").toString();
						String sEcplatformNo=getShipmentData.get(t).get("ECPLATFORMNO").toString();//
						String sEcOrderno=getShipmentData.get(t).get("EC_ORDERNO").toString();
						String sExpressno=getShipmentData.get(t).get("EXPRESSNO").toString();
						String sReceiver=getShipmentData.get(t).get("RECEIVER").toString();

						//樂天用到
						String sOrderpackageId="";

						String sEcPlatformno=getShipmentData.get(t).get("ECPLATFORMNO").toString();
						String sEcPlatformname=getShipmentData.get(t).get("ECPLATFORMNAME").toString();

						boolean isSuccess = true;
						try 
						{					
							if (sEcplatformNo.equals("shopee"))//虾皮
							{
								int partner_id=Integer.parseInt(getECommerceData.get(0).get("PARTNER_ID").toString());
								int shop_id= Integer.parseInt(getECommerceData.get(0).get("STORE_ID").toString());
								String partner_key=getECommerceData.get(0).get("PARTNER_KEY").toString();

								// 当 ecOrderNoList 中不包含当前货运单对应的电商原订单号（sEcOrderno）的时候， 调电商平台发货接口
								if( !ecOrderNoList.contains(sEcOrderno) )
								{

									String sMode=getShipmentData.get(t).get("SHOPEE_MODE").toString();
									if (PosPub.isNumeric(sMode)==false) 
									{
										sMode="1";
									}
									int shopee_mode=Integer.parseInt(sMode);

									String shopee_address_id=getShipmentData.get(t).get("SHOPEE_ADDRESS_ID").toString();
									String shopee_pickup_time_id=getShipmentData.get(t).get("SHOPEE_PICKUP_TIME_ID").toString();
									String shopee_branch_id=getShipmentData.get(t).get("SHOPEE_BRANCH_ID").toString();
									String shopee_sender_real_name=getShipmentData.get(t).get("SHOPEE_SENDER_REAL_NAME").toString();

									//虾皮特殊发货需要两步
									shopee=new Shopee();									

									//调用发货接口
									String resbody="";
									if (shopee_address_id.equals("")) 
									{
										shopee_address_id="0";
									}
									if (shopee_branch_id.equals("")) 
									{
										shopee_branch_id="0";
									}

									resbody=shopee.Init(apiUrl, partner_id, partner_key, shop_id, sEcOrderno, shopee_mode, Long.parseLong(shopee_address_id), shopee_pickup_time_id, sExpressno, Long.parseLong(shopee_branch_id), shopee_sender_real_name);

									JSONObject jsonres = new JSONObject(resbody);

									String request_id=jsonres.getString("request_id");
									if(jsonres.has("error"))//错误
									{
										String errorno=jsonres.getString("error");

										//发货失败的货运单
										error_ShipmentNoString.append("货运单："+sShipmentNo+"\r\n" +"虾皮错误信息:" +"_"+errorno+"\r\n") ;
										isSuccess = false;

									}
									else
									{
										//这里可能取不到托运单号
										String tracking_number="";
										if (jsonres.isNull("tracking_number")==false) 
										{
											tracking_number=jsonres.getString("tracking_number");
										}									

										//托运单号
										if (tracking_number.equals("")==false) 
										{
											sExpressno=tracking_number;	
										}

										// 每调用一次电商平台的接口， 就在该数据中记录电商原订单号 ，
										// 如果集合中已存在电商单号， 不用再调用电商发货接口
										ecOrderNoList.add(sEcOrderno);
									}

								}

								if(ecOrderNoList.contains(sEcOrderno) && isSuccess)
								{
									//最后==更新虾皮发货模式相关栏位
									UptBean ubsp = new UptBean("OC_SHIPMENT");	
									//条件
									ubsp.addCondition("EID",new DataValue(eId, Types.VARCHAR));
									ubsp.addCondition("SHOPID",new DataValue(myshop, Types.VARCHAR));
									ubsp.addCondition("SHIPMENTNO",new DataValue(sShipmentNo, Types.VARCHAR));
									//值
									ubsp.addUpdateValue("STATUS", new DataValue(2, Types.INTEGER));

									ubsp.addUpdateValue("EXPRESSNO", new DataValue(sExpressno, Types.VARCHAR));

									this.addProcessData(new DataProcessBean(ubsp));

									//更新订单状态为10已发货
									ubsp = new UptBean("OC_ORDER");	
									//条件
									ubsp.addCondition("EID",new DataValue(eId, Types.VARCHAR));
									ubsp.addCondition("ORDERNO",new DataValue(sEcOrderno, Types.VARCHAR));
									//值
									ubsp.addUpdateValue("STATUS", new DataValue("10", Types.VARCHAR));//0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作

									this.addProcessData(new DataProcessBean(ubsp));


									//接單日誌
									DataValue[] insValueOrderStatus_LOG = new DataValue[] 
											{ 
													new DataValue(eId, Types.VARCHAR),
													new DataValue(myshop, Types.VARCHAR), // 组织编号=门店编号
													new DataValue(myshop, Types.VARCHAR), // 映射后的门店
													new DataValue(sEcOrderno, Types.VARCHAR), //
													new DataValue(sEcPlatformno, Types.VARCHAR), //電商平台
													new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
													new DataValue("訂單状态", Types.VARCHAR), // 状态类型名称
													new DataValue("10", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
													new DataValue("已發貨", Types.VARCHAR), // 状态名称
													new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
													new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
													new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
													new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
													new DataValue("admin", Types.VARCHAR), //操作員編碼
													new DataValue("管理員", Types.VARCHAR), //操作員名稱
													new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
													new DataValue("訂單状态-->已發貨", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
													new DataValue("100", Types.VARCHAR) 
											};
									InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
									ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
									this.addProcessData(new DataProcessBean(ibOrderStatusLog));


									this.doExecuteDataToDB();
								}

							}
							else if (sEcplatformNo.equals("letian"))//樂天
							{

								//樂天
								String lt_Secretkey=getECommerceData.get(0).get("LTSECRETKEY").toString();
								String lt_Shopurl=getECommerceData.get(0).get("LTSHOPURL").toString();


								//樂天用到
								sOrderpackageId=getShipmentData.get(t).get("SHIP_ORDERPACKAGEID").toString();

								rt=new Rakuten();

								//更新物流单号
								String	resbody=rt.UpdateTracknumber(apiUrl, "", lt_Secretkey, lt_Shopurl, sEcOrderno, sOrderpackageId,sEcPlatformname, sExpressno);

								logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号接口返回：" +resbody+ "\r\n");

								JSONObject jsonres = new JSONObject(resbody);

								String operationId=jsonres.getString("operationId");//UUID

								logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号接口返回operationId：" +operationId+ "\r\n");

								String operationStatus=jsonres.getString("operationStatus");

								logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号接口返回operationStatus：" +operationStatus+ "\r\n");

								String operationType=jsonres.getString("operationType");

								logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号接口返回operationType：" +operationType+ "\r\n");

								String submittedDate=jsonres.getString("submittedDate");								
								logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号接口返回submittedDate：" +submittedDate+ "\r\n");

								if (jsonres.has("successCount")) 
								{
									int successCount=jsonres.getInt("successCount");//选填
									logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号接口返回successCount：" +successCount+ "\r\n");
								}

								if (jsonres.has("failureCount")) 
								{
									int failureCount=jsonres.getInt("failureCount");//选填
									logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号接口返回failureCount：" +failureCount+ "\r\n");
								}	

								//
								if (operationId.equals("")==false) 
								{
									//查詢返回結果
									String resbodyOptX=rt.OperationResponse(apiUrl, "", lt_Secretkey, operationId);
									logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号-查詢接口返回：" +resbodyOptX+ "\r\n");

									JSONObject jsonresOptX = new JSONObject(resbodyOptX);
									if (jsonresOptX.has("operations")) 
									{
										//成功
										int ssuccessCount=0;

										JSONArray operations=jsonresOptX.getJSONArray("operations");
										for(int ri=0;ri<operations.length();ri++)
										{				
											String soperationId=operations.getJSONObject(ri).getString("operationId");//
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号-查詢接口返回operationId：" +soperationId+ "\r\n");
											String soperationType=operations.getJSONObject(ri).getString("operationType");//
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号-查詢接口返回operationType：" +soperationType+ "\r\n");
											String soperationStatus=operations.getJSONObject(ri).getString("operationStatus");//
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号-查詢接口返回operationStatus：" +soperationStatus+ "\r\n");
											String ssubmittedDate=operations.getJSONObject(ri).getString("submittedDate");//
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号-查詢接口返回submittedDate：" +ssubmittedDate+ "\r\n");
											String scompletedDate=operations.getJSONObject(ri).getString("completedDate");//
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号-查詢接口返回completedDate：" +scompletedDate+ "\r\n");
											int ssubmittedCount=operations.getJSONObject(ri).getInt("submittedCount");//
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号-查詢接口返回submittedCount：" +ssubmittedCount+ "\r\n");
											ssuccessCount=operations.getJSONObject(ri).getInt("successCount");//
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号-查詢接口返回successCount：" +ssuccessCount+ "\r\n");
											int sfailureCount=operations.getJSONObject(ri).getInt("failureCount");//
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天更新物流单号-查詢接口返回failureCount：" +sfailureCount+ "\r\n");
										}

										//
										if (ssuccessCount>0) 
										{
											//配送状态
											String sConfirmRes=rt.ConfirmShipping(apiUrl, "", lt_Secretkey, lt_Shopurl, sEcOrderno, sOrderpackageId,"Shipped");
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨接口返回：" +sConfirmRes+ "\r\n");

											JSONObject jsonresConfirm = new JSONObject(sConfirmRes);

											String operationId_confirm=jsonresConfirm.getString("operationId");//UUID
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨接口operationId：" +operationId_confirm+ "\r\n");

											String operationStatus_confirm=jsonresConfirm.getString("operationStatus");								
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨接口operationStatus：" +operationStatus_confirm+ "\r\n");

											String operationType_confirm=jsonresConfirm.getString("operationType");								
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨接口operationType：" +operationType_confirm+ "\r\n");

											String submittedDate_confirm=jsonresConfirm.getString("submittedDate");								
											logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨接口submittedDate：" +submittedDate_confirm+ "\r\n");

											if (jsonresConfirm.has("successCount")) 
											{
												int successCount_confirm=jsonresConfirm.getInt("successCount");//选填									
												logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨接口successCount：" +successCount_confirm+ "\r\n");
											}

											if (jsonresConfirm.has("failureCount")) 
											{
												int failureCount_confirm=jsonresConfirm.getInt("failureCount");//选填									
												logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨接口failureCount：" +failureCount_confirm+ "\r\n");
											}

											//
											if (operationId_confirm.equals("")) 
											{
												//查詢返回結果
												String resbodyOpt=rt.OperationResponse(apiUrl, "", lt_Secretkey, operationId_confirm);
												logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨-查詢接口返回：" +resbodyOpt+ "\r\n");

												JSONObject jsonresOpt = new JSONObject(resbodyOpt);
												if (jsonresOpt.has("operations")) 
												{
													int sOptsuccessCount=0;
													
													JSONArray operationsOpt=jsonresOpt.getJSONArray("operations");
													for(int ri=0;ri<operationsOpt.length();ri++)
													{				
														String soperationId=operationsOpt.getJSONObject(ri).getString("operationId");//
														logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨-查詢接口返回operationId：" +soperationId+ "\r\n");
														String soperationType=operationsOpt.getJSONObject(ri).getString("operationType");//
														logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨-查詢接口返回operationType：" +soperationType+ "\r\n");
														String soperationStatus=operationsOpt.getJSONObject(ri).getString("operationStatus");//
														logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨-查詢接口返回operationStatus：" +soperationStatus+ "\r\n");
														String ssubmittedDate=operationsOpt.getJSONObject(ri).getString("submittedDate");//
														logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨-查詢接口返回submittedDate：" +ssubmittedDate+ "\r\n");
														String scompletedDate=operationsOpt.getJSONObject(ri).getString("completedDate");//
														logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨-查詢接口返回completedDate：" +scompletedDate+ "\r\n");
														int ssubmittedCount=operationsOpt.getJSONObject(ri).getInt("submittedCount");//
														logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨-查詢接口返回submittedCount：" +ssubmittedCount+ "\r\n");
														sOptsuccessCount=operationsOpt.getJSONObject(ri).getInt("successCount");//
														logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨-查詢接口返回successCount：" +ssuccessCount+ "\r\n");
														int sfailureCount=operationsOpt.getJSONObject(ri).getInt("failureCount");//
														logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天發貨-查詢接口返回failureCount：" +sfailureCount+ "\r\n");
													}
													
													if (sOptsuccessCount>0) 
													{
														UptBean ubsp = new UptBean("OC_SHIPMENT");	
														//条件
														ubsp.addCondition("EID",new DataValue(eId, Types.VARCHAR));
														ubsp.addCondition("SHOPID",new DataValue(myshop, Types.VARCHAR));
														ubsp.addCondition("SHIPMENTNO",new DataValue(sShipmentNo, Types.VARCHAR));
														//值
														ubsp.addUpdateValue("STATUS", new DataValue(2, Types.INTEGER));

														ubsp.addUpdateValue("EXPRESSNO", new DataValue(sExpressno, Types.VARCHAR));

														this.addProcessData(new DataProcessBean(ubsp));

														//更新订单状态为10已发货
														ubsp = new UptBean("OC_ORDER");	
														//条件
														ubsp.addCondition("EID",new DataValue(eId, Types.VARCHAR));
														ubsp.addCondition("ORDERNO",new DataValue(sEcOrderno, Types.VARCHAR));
														//值
														ubsp.addUpdateValue("STATUS", new DataValue("10", Types.VARCHAR));//0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作

														this.addProcessData(new DataProcessBean(ubsp));


														//接單日誌
														DataValue[] insValueOrderStatus_LOG = new DataValue[] 
																{ 
																		new DataValue(eId, Types.VARCHAR),
																		new DataValue(myshop, Types.VARCHAR), // 组织编号=门店编号
																		new DataValue(myshop, Types.VARCHAR), // 映射后的门店
																		new DataValue(sEcOrderno, Types.VARCHAR), //
																		new DataValue(sEcPlatformno, Types.VARCHAR), //電商平台
																		new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
																		new DataValue("訂單状态", Types.VARCHAR), // 状态类型名称
																		new DataValue("10", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
																		new DataValue("已發貨", Types.VARCHAR), // 状态名称
																		new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
																		new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
																		new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
																		new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
																		new DataValue("admin", Types.VARCHAR), //操作員編碼
																		new DataValue("管理員", Types.VARCHAR), //操作員名稱
																		new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
																		new DataValue("訂單状态-->已發貨", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
																		new DataValue("100", Types.VARCHAR) 
																};
														InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
														ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
														this.addProcessData(new DataProcessBean(ibOrderStatusLog));


														this.doExecuteDataToDB();
													}
													else 
													{
														//发货失败的货运单
														error_ShipmentNoString.append("貨運單："+sShipmentNo+"\r\n" +"樂天錯誤信息:更新配送狀態-查詢接口返回失敗\r\n") ;
														isSuccess = false;
													}
												}	
												else 
												{
													//发货失败的货运单
													error_ShipmentNoString.append("貨運單："+sShipmentNo+"\r\n" +"樂天錯誤信息:更新配送狀態接口返回失敗\r\n" );
													isSuccess = false;
												}
												
											}
											else 
											{
												//发货失败的货运单
												error_ShipmentNoString.append("貨運單："+sShipmentNo+"\r\n" +"樂天錯誤信息:更新配送狀態接口返回失敗\r\n" );
												isSuccess = false;
											}
											
										}
										else 
										{
											//发货失败的货运单
											error_ShipmentNoString.append("貨運單："+sShipmentNo+"\r\n" +"樂天錯誤信息:更新物流單號-查詢接口返回失敗\r\n") ;
											isSuccess = false;
										}

									}
									else 
									{
										//发货失败的货运单
										error_ShipmentNoString.append("貨運單："+sShipmentNo+"\r\n" +"樂天錯誤信息:更新物流單號接口返回失敗\r\n") ;
										isSuccess = false;
									}
								}
								else 
								{
									//发货失败的货运单
									error_ShipmentNoString.append("貨運單："+sShipmentNo+"\r\n" +"樂天錯誤信息:更新物流單號接口返回失敗\r\n") ;
									isSuccess = false;
								}

							}
							else //其他电商以此类推
							{		
								//那种EXCEL的直接更新单据状态
								UptBean ubsp = new UptBean("OC_SHIPMENT");	
								//条件
								ubsp.addCondition("EID",new DataValue(eId, Types.VARCHAR));
								ubsp.addCondition("SHOPID",new DataValue(myshop, Types.VARCHAR));
								ubsp.addCondition("SHIPMENTNO",new DataValue(sShipmentNo, Types.VARCHAR));
								//值
								ubsp.addUpdateValue("STATUS", new DataValue(2, Types.INTEGER));
								this.addProcessData(new DataProcessBean(ubsp));

								//更新订单状态为10已发货
								ubsp = new UptBean("OC_ORDER");	
								//条件
								ubsp.addCondition("EID",new DataValue(eId, Types.VARCHAR));
								ubsp.addCondition("ORDERNO",new DataValue(sEcOrderno, Types.VARCHAR));
								//值
								ubsp.addUpdateValue("STATUS", new DataValue("10", Types.VARCHAR));
								this.addProcessData(new DataProcessBean(ubsp));


								//接單日誌
								DataValue[] insValueOrderStatus_LOG = new DataValue[] 
										{ 
												new DataValue(eId, Types.VARCHAR),
												new DataValue(myshop, Types.VARCHAR), // 组织编号=门店编号
												new DataValue(myshop, Types.VARCHAR), // 映射后的门店
												new DataValue(sEcOrderno, Types.VARCHAR), //
												new DataValue(sEcPlatformno, Types.VARCHAR), //電商平台
												new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
												new DataValue("訂單状态", Types.VARCHAR), // 状态类型名称
												new DataValue("10", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
												new DataValue("已發貨", Types.VARCHAR), // 状态名称
												new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
												new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
												new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
												new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
												new DataValue("admin", Types.VARCHAR), //操作員編碼
												new DataValue("管理員", Types.VARCHAR), //操作員名稱
												new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
												new DataValue("訂單状态-->已發貨", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
												new DataValue("100", Types.VARCHAR) 
										};
								InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
								ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
								this.addProcessData(new DataProcessBean(ibOrderStatusLog));

								this.doExecuteDataToDB();

							}
						} 
						catch (Exception e) 
						{
							//System.out.println(e.getMessage());

							//发货失败的货运单
							error_ShipmentNoString.append("货运单："+sShipmentNo +"错误信息:" +e.getMessage()+"\r\n") ;
						}
						finally 
						{
							shopee=null;
							rt=null;
						}
					}

					//失败原因
					if (error_ShipmentNoString.toString().equals("")==false)
					{
						res.setSuccess(false);
						res.setServiceDescription("发货失败原因：\r\n" +error_ShipmentNoString.toString() );
					}
				}
				else 
				{
					res.setSuccess(false);
					res.setServiceDescription("查不到数据货运单据！");
				}
			}
			else 
			{
				res.setSuccess(false);
				res.setServiceDescription("电商API资料未设置！");
			}
		} // 发货类型处理，到此结束


		// 安排取件
		if(opType.equals("2"))
		{
			if (getShipmentData != null && getShipmentData.isEmpty() == false)
			{
				int countShip=getShipmentData.size();
				Egs egs = new Egs();
				Htc htc = new Htc();	
				Cvs cvs=new Cvs();

				SevenEleven se=new SevenEleven();				

				for (int t = 0; t < countShip; t++) 
				{
					myshop=getShipmentData.get(t).get("SHOPID").toString();
					String sShipmentNo=getShipmentData.get(t).get("SHIPMENTNO").toString(); // 货运单号 ，egs 的order_no 
					String sLgplatformNo=getShipmentData.get(t).get("LGPLATFORMNO").toString();// 货运厂商， 物流平台
					String sExpressno=getShipmentData.get(t).get("EXPRESSNO").toString(); // 托运单号    tracking_number
					String receiver_name=getShipmentData.get(t).get("RECEIVER").toString();
					String receiver_address = getShipmentData.get(t).get("RECEIVER_ADDRESS").toString();
					String receiver_suda5 = getShipmentData.get(t).get("RECEIVER_FIVECODE").toString(); 
					String receiver_mobile = getShipmentData.get(t).get("RECEIVER_MOBILE").toString(); 
					String receiver_phone = getShipmentData.get(t).get("RECEIVER_PHONE").toString(); 
					String sender_name = sendName; 
					String sender_address = sendAddress;
					String sender_suda5 = sendSuDa5;
					String sender_phone = sendPhone;
					String product_price = getShipmentData.get(t).get("COLLECTAMT").toString(); //代收货款金额
					String product_name  = getShipmentData.get(t).get("PRODUCTNAME").toString();
					String comment  = getShipmentData.get(t).get("MEMO").toString();
					String package_size  = getShipmentData.get(t).get("MEASURENO").toString();
					//数据库 1.常溫 2.冷藏 3.冷凍
					//"0001"=常溫|"0002"=冷藏|"0003"=冷凍
					//黑猫需处理一下
					String temperature  = getShipmentData.get(t).get("TEMPERATELAYERNO").toString();
					
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
					
					String distance  = getShipmentData.get(t).get("DISTANCENO").toString();
					String delivery_date  = getShipmentData.get(t).get("SHIPDATE").toString();//20190621

					
					String deliverytype=getShipmentData.get(t).get("DELIVERYTYPE").toString();
					String getshopno=getShipmentData.get(t).get("GETSHOP").toString();
					String paystatus=getShipmentData.get(t).get("PAYSTATUS").toString();
					String realamt=getShipmentData.get(t).get("REALAMT").toString();
					String order_name=getShipmentData.get(t).get("ORDERER").toString();
					String receiver_email=getShipmentData.get(t).get("RECEIVER_EMAIL").toString();

					SimpleDateFormat tempSdf = new SimpleDateFormat("yyyyMMdd");
					
					if (delivery_date.equals("")) 
					{
						//今天+7 台湾一般都是+7天
						delivery_date=tempSdf.format(calendar.getTime());
						delivery_date=PosPub.GetStringDate(delivery_date, 7);
					}
					
					Date tempdatepp = tempSdf.parse(delivery_date);
					tempSdf = new SimpleDateFormat("yyyy-MM-dd");
					//2019-06-21
					delivery_date=tempSdf.format(tempdatepp);		
					//System.out.println(delivery_date);	


					//数据库 1.不分時段 2.早上(09~12點) 3.中午(12~17點) 4.下午(17~20點)
					//"1"=9~12 時|"2"=12~17 時|"3"=17~20 時|"4"=不限時
					//黑猫需处理一下
					String delivery_timezone = getShipmentData.get(t).get("SHIPHOURTYPE").toString();
								
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
					
					String create_time = getShipmentData.get(t).get("SDATE").toString() ; // 需要加上STIME
					String sEcOrderno=getShipmentData.get(t).get("EC_ORDERNO").toString();
					String sEcPlatformno=getShipmentData.get(t).get("ECPLATFORMNO").toString();
					String sEcPlatformname=getShipmentData.get(t).get("ECPLATFORMNAME").toString();

					String print_time = ""; // 打印时间 ， 暂时设置为空
					String account_id = ""; // 托运单账号
					String member_no = ""; // 会员编号
					String taxin = "0"; // 进口关税 海外地区使用   0
					String insurance = "0"; // 报值金额   0

					String[] htcOrdersnList = {sShipmentNo};
					String[] htcExpressNOList = {sExpressno};

					boolean bCallDeliveryOK=false;

					if(sLgplatformNo.equals("htc"))
					{

						if (getLOGISTICSData != null && getLOGISTICSData.isEmpty() == false)
						{
							for (Map<String, Object> map : getLOGISTICSData) 
							{
								String lgPlatformNo = map.get("LGPLATFORMNO").toString();
								if(sLgplatformNo.equals(lgPlatformNo))
								{
									apiUrl = map.get("API_URL").toString();
									company = map.get("LGCOMPANYNO").toString();
									password = map.get("LGPASSWORD").toString();
									customerId = map.get("CUSTOMERNO").toString();
								}
							}

						}

						String htcRes = htc.TransReport_Json(apiUrl, company, password, htcOrdersnList, htcExpressNOList);
						// 这里不用写 错误日志，调用的类中已有错误日志
						JSONArray jsonres=new JSONArray(htcRes);
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
									res.setSuccess(false);
									res.setServiceDescription("HTC新竹物流接口TransReport_Json调用失败=" + ErrMsg);
								}
								else 
								{
									bCallDeliveryOK=true;
								}
							}
						}
					}

					if(sLgplatformNo.equals("egs"))
					{

						if (getLOGISTICSData != null && getLOGISTICSData.isEmpty() == false)
						{
							for (Map<String, Object> map : getLOGISTICSData) 
							{
								String lgPlatformNo = map.get("LGPLATFORMNO").toString();
								if(sLgplatformNo.equals(lgPlatformNo))
								{
									apiUrl = map.get("API_URL").toString();
									customerId = map.get("CUSTOMERNO").toString();
								}
							}
						}

						/**
						 * 以下四个日期参数，需要特殊处理， 转换为指定的格式
						 *  * @param delivery_date 指定配达日期  2011-04-25
						 * @param delivery_timezone 指定配达时段  1=9~12時  2=12~17時  3=17~20時  4=不限時
						 * @param create_time 建立时间 2011-04-24 11:27:59 
						 * @param print_time 打印时间  2011-04-24 11:27:59
						 */

						// 建立时间和打印时间传当前系统日期时间
						SimpleDateFormat dfDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String sysDate=dfDatetime.format(new Date());

						String egsRes = egs.transfer_waybill(apiUrl, customerId, sExpressno, sShipmentNo, receiver_name, receiver_address, 
								receiver_suda5, receiver_mobile, receiver_phone, sender_name, sender_address, sender_suda5, sender_phone, 
								product_price, product_name, comment, package_size, temperatureCST, distance, delivery_date, delivery_timezoneCST, 
								sysDate, sysDate, account_id, member_no, taxin, insurance);

						String[] splitStrings=egsRes.split("&");

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

								res.setSuccess(false);
								res.setServiceDescription(message);
							}
							else 
							{
								bCallDeliveryOK=true;
							}

						}	


					}

					if(sLgplatformNo.equals("cvs"))//便利达康
					{
						//便利达康
						Map<String, Object> map_condition = new HashMap<String, Object>();
						map_condition.put("LGPLATFORMNO", "cvs");		
						List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLOGISTICSData,map_condition,false);	
						if (getQHeader1!=null && getQHeader1.size()>0) 
						{
							String cvsApiUrl=getQHeader1.get(0).get("API_URL").toString();//
							String cvsEcNo=getQHeader1.get(0).get("CVS_MOTHERVENDORNO").toString();
							String cvsCollectNo=getQHeader1.get(0).get("CVS_COLLECTNO").toString();
							String cvsDcNo=getQHeader1.get(0).get("CVS_LARGELOGISTICSNO").toString();



							//
							List<Map<String, Object>> OrderList=new ArrayList<Map<String,Object>>();

							//单笔
							Map<String, Object> map1=new HashMap<String, Object>();
							map1.put("ODNO", sExpressno);//物流单号

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

							int collectAMT=0;
							if (PosPub.isNumeric(product_price)) 
							{
								collectAMT=Integer.parseInt(product_price);
							}
							map1.put("AMT", collectAMT);//代收金额， >=0 int
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
							bCallDeliveryOK=cvs.F10_OrderUpload(cvsApiUrl, cvsEcNo, cvsCollectNo, cvsDcNo, OrderList);


						}
						map_condition=null;
						getQHeader1=null;

					}

					if(sLgplatformNo.equals("dzt"))//大智通
					{

						//大智通
						Map<String, Object> map_condition = new HashMap<String, Object>();
						map_condition.put("LGPLATFORMNO", "dzt");		
						List<Map<String, Object>> getQHeader1=MapDistinct.getWhereMap(getLOGISTICSData,map_condition,false);	
						if (getQHeader1!=null && getQHeader1.size()>0) 
						{
							String dztApiUrl=getLOGISTICSData.get(0).get("API_URL").toString();//
							String dztEcNo=getLOGISTICSData.get(0).get("DZT_MOTHERVENDORNO").toString();
							String dztSonEcNo=getLOGISTICSData.get(0).get("DZT_SONVENDORNO").toString();						
							String dztFtpuid=getLOGISTICSData.get(0).get("DZT_FTPUID").toString();	
							String dztFtppwd=getLOGISTICSData.get(0).get("DZT_FTPPWD").toString();	

							//明细笔数
							int detailCount=0;


							//
							SimpleDateFormat myTempdf = new SimpleDateFormat("yyyy-MM-dd");
							Calendar myTempcal = Calendar.getInstance();
							String sDate=myTempdf.format(myTempcal.getTime());
							//System.out.println(sDate);


							//货运单上传
							JSONObject OrderDoc = new JSONObject();		
							JSONArray Order = new JSONArray();

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
							ShipmentDetailBody.put("ShipmentNo", sExpressno);//配送編號,物流单号
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


							OrderDoc.put("Order",Order);

							String resbody=OrderDoc.toString();

							//
							SimpleDateFormat Tempdf = new SimpleDateFormat("yyyyMMdd");
							Calendar Tempcal = Calendar.getInstance();
							String sfDate=Tempdf.format(Tempcal.getTime());
							//System.out.println(sfDate);

							//大智通上传托运资料
							bCallDeliveryOK=se.SIN(dztApiUrl, dztEcNo, dztSonEcNo, sfDate, dztFtpuid, dztFtppwd, resbody);

						}



					}
					
					if(sLgplatformNo.equals("1"))//自配送
					{
						bCallDeliveryOK=true;						
					}

					//接口调用成功
					if (bCallDeliveryOK) 
					{
						UptBean ubsp = new UptBean("OC_SHIPMENT");	
						//条件
						ubsp.addCondition("EID",new DataValue(eId, Types.VARCHAR));
						ubsp.addCondition("SHOPID",new DataValue(myshop, Types.VARCHAR));
						ubsp.addCondition("SHIPMENTNO",new DataValue(sShipmentNo, Types.VARCHAR));
						//值
						ubsp.addUpdateValue("STATUS", new DataValue(5, Types.INTEGER));//更新成取件
						this.addProcessData(new DataProcessBean(ubsp));

						//更新订单状态为10已发货
						ubsp = new UptBean("OC_ORDER");	
						//条件
						ubsp.addCondition("EID",new DataValue(eId, Types.VARCHAR));
						ubsp.addCondition("ORDERNO",new DataValue(sEcOrderno, Types.VARCHAR));
						//值
						ubsp.addUpdateValue("DELIVERYSTUTAS", new DataValue("2", Types.VARCHAR));//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 
						ubsp.addUpdateValue("STATUS", new DataValue("10", Types.VARCHAR));//0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
						this.addProcessData(new DataProcessBean(ubsp));

						//接單日誌
						DataValue[] insValueOrderStatus_LOG = new DataValue[] 
								{ 
										new DataValue(eId, Types.VARCHAR),
										new DataValue(myshop, Types.VARCHAR), // 组织编号=门店编号
										new DataValue(myshop, Types.VARCHAR), // 映射后的门店
										new DataValue(sEcOrderno, Types.VARCHAR), //
										new DataValue(sEcPlatformno, Types.VARCHAR), //電商平台
										new DataValue("2", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
										new DataValue("配送状态", Types.VARCHAR), // 状态类型名称
										new DataValue("9", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
										new DataValue("待配送", Types.VARCHAR), // 状态名称
										new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
										new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
										new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
										new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
										new DataValue("admin", Types.VARCHAR), //操作員編碼
										new DataValue("管理員", Types.VARCHAR), //操作員名稱
										new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
										new DataValue("配送状态-->已安排取件", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
										new DataValue("100", Types.VARCHAR) 
								};
						InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
						ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
						this.addProcessData(new DataProcessBean(ibOrderStatusLog));

						this.doExecuteDataToDB();
					}

				}				

			}
		}


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECShippingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECShippingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECShippingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECShippingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		String shipmentNoList[] = req.getShipmentNo();

		if (shipmentNoList.length == 0) 
		{
			errCt++;
			errMsg.append("货运单号不可为空值  ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECShippingCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECShippingCreateReq>(){};
	}

	@Override
	protected DCP_OrderECShippingCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECShippingCreateRes();
	}


	private String getLgPlatformDatas(String eId, String lgPlatformNo){
		String sql =null;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append("select * from OC_LOGISTICS where EID = '"+eId+"' "
				+ " and lgPlatformNo = '"+lgPlatformNo+"' ");
		sql = sqlbuf.toString();
		return sql;
	}

}
