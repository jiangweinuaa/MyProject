package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECSellerCancelReq;
import com.dsc.spos.json.cust.req.DCP_OrderECSellerCancelReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderECSellerCancelRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ec.Rakuten;
import com.dsc.spos.utils.ec.Shopee;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderECSellerCancel extends SPosAdvanceService<DCP_OrderECSellerCancelReq, DCP_OrderECSellerCancelRes> {

	Logger logger = LogManager.getLogger(DCP_OrderECSellerCancel.class.getName());

	@Override
	protected void processDUID(DCP_OrderECSellerCancelReq req, DCP_OrderECSellerCancelRes res) throws Exception 
	{

		//取消订单失败的单号
		String error_orderno="";

		// TODO Auto-generated method stub
		String eId = req.geteId();

		List<level1Elm> datas = req.getDatas();
		for(level1Elm detail : datas)
		{
			String ecPlatformNo = detail.getEcPlatformNo();
			String ecOrderNo = detail.getEcOrderNo();
			//門店管理系統定義的代碼
			String cancelReasonCode = detail.getCancelReason();
			//門店管理系統定義的描述
			String cancelReasonDesc="";

			//處理訂單以配送門店為歸屬門店
			String myshop="";
			String sqlOrderno="select * from OC_ORDER where EID = '"+eId+"'  and ORDERNO='"+ ecOrderNo+"' ";
			List<Map<String, Object>> getOrdernoDatas=this.doQueryData(sqlOrderno, null);
			if (getOrdernoDatas != null && getOrdernoDatas.isEmpty() == false)
			{
				myshop=getOrdernoDatas.get(0).get("SHOPID").toString();
			}						
			
			// 查询电商平台基础信息： url ， key 等参数
			String sql = "select * from OC_ecommerce where EID = '"+eId+"'  and  ecPlatformNo = '"+ecPlatformNo+"'";
			List<Map<String, Object>> getECDatas=this.doQueryData(sql, null);
			if (getECDatas != null && getECDatas.isEmpty() == false)
			{				
				//訂單日誌時間
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat sysDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String orderStatusLogTimes=sysDatetime.format(calendar.getTime());

				boolean isSuccess = false;

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

				// 根据不同的电商平台调用不同的接口
				if(ecPlatformNo.equals("shopee"))
				{		
					//電商平台定義的代碼
					String ecCancelReasonCode="";

					//
					if (cancelReasonCode.equals("002")) 
					{				
						cancelReasonDesc="客戶要求";

						ecCancelReasonCode="CUSTOMER_REQUEST";
					}
					else if (cancelReasonCode.equals("003")) 
					{				
						cancelReasonDesc="無法送達的區域";

						ecCancelReasonCode="UNDELIVERABLE_AREA";
					}
					else if (cancelReasonCode.equals("004")) 
					{				
						cancelReasonDesc="不支持的付款方式";

						ecCancelReasonCode="COD_NOT_SUPPORTED";
					}
					else 
					{
						cancelReasonDesc="賣家缺貨";

						ecCancelReasonCode="OUT_OF_STOCK";
					}

					String apiUrl = getECDatas.get(0).get("API_URL").toString();
					int partnerId = Integer.parseInt(getECDatas.get(0).get("PARTNER_ID").toString());
					String partnerKey = getECDatas.get(0).get("PARTNER_KEY").toString();
					int shopId = Integer.parseInt(getECDatas.get(0).get("STORE_ID").toString());
					String ordersn = ecOrderNo;

					Shopee shopee = new Shopee();
					//(apiUrl, partner_id, partner_key, shop_id, ordersn, cancel_reason) 
					String resbody=shopee.CancelOrder(apiUrl, partnerId, partnerKey, shopId, ordersn, ecCancelReasonCode);
					JSONObject jsonres = new JSONObject(resbody);

					if(jsonres.has("error"))//错误
					{
						String errorno=jsonres.getString("error");
						String errormsg=jsonres.getString("msg");

						error_orderno+="虾皮订单：" + ecOrderNo +"<br/>取消报错："+errorno+"-" + errormsg + "<br/>";

						logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******虾皮统一取消订单接口OrderECSellerCancelDCP,报错信息error=" + errorno+",msg=" + errormsg + "******\r\n");
					}
					else
					{
						isSuccess = true;
					}
				} 
				else if(ecPlatformNo.equals("letian"))
				{
					//電商平台定義的代碼
					String ecCancelReasonCode="";

					//
					if (cancelReasonCode.equals("002")) 
					{				
						cancelReasonDesc="客戶要求";

						ecCancelReasonCode="106";
					}
					else if (cancelReasonCode.equals("003")) 
					{				
						cancelReasonDesc="無法送達的區域";

						ecCancelReasonCode="401";
					}
					else if (cancelReasonCode.equals("004")) 
					{				
						cancelReasonDesc="不支持的付款方式";

						ecCancelReasonCode="402";
					}
					else 
					{
						cancelReasonDesc="賣家缺貨";

						ecCancelReasonCode="301";
					}

					String apiUrl = getECDatas.get(0).get("API_URL").toString();					
					String secretkey= getECDatas.get(0).get("LTSECRETKEY").toString();
					String shopUrl=getECDatas.get(0).get("LTSHOPURL").toString();

					String ordersn = ecOrderNo;

					Rakuten rt = new Rakuten();
					String resbody=rt.CancelOrder(apiUrl, "", secretkey, shopUrl, ordersn, ecCancelReasonCode);

					logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天取消订单返回：" +resbody+ "\r\n");

					JSONObject jsonres = new JSONObject(resbody);

					if (jsonres.has("errors"))
					{
						JSONObject jsonErr = jsonres.getJSONObject("errors");

						//常见错误
						JSONArray common = jsonErr.getJSONArray("common");

						int errorno=0;
						String shortMessage="";
						String errormsg="";

						for (int ei = 0; ei < common.length(); ei++) 
						{
							errorno=common.getJSONObject(ei).getInt("errorCode");//错误代码
							shortMessage=common.getJSONObject(ei).getString("shortMessage");//简短描述
							errormsg=common.getJSONObject(ei).getString("longMessage");//详细错误
							//System.out.println(errorno);
							//System.out.println(shortMessage);
							//System.out.println(errormsg);
						}	

						error_orderno+="乐天订单：" + ecOrderNo +"<br/>取消报错："+errorno+"-" + errormsg + "<br/>";

						logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******乐天统一取消订单接口OrderECSellerCancelDCP,报错信息error=" + errorno+",msg=" + errormsg + "******\r\n");
					}
					else 
					{
						String operationId=jsonres.getString("operationId");//UUID
						//System.out.println(operationId);

						logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天取消订单返回operationId：" +operationId+ "\r\n");

						//
						if (operationId.equals("")==false) 
						{							
							//查詢返回結果
							String resbodyOptX=rt.OperationResponse(apiUrl, "", secretkey, operationId);
							logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天取消订单-查詢接口返回：" +resbodyOptX+ "\r\n");

							JSONObject jsonresOptX = new JSONObject(resbodyOptX);
							if (jsonresOptX.has("operations")) 
							{								
								//成功
								int ssuccessCount=0;

								JSONArray operations=jsonresOptX.getJSONArray("operations");
								for(int ri=0;ri<operations.length();ri++)
								{				
									String soperationId=operations.getJSONObject(ri).getString("operationId");//
									logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天取消订单-查詢接口返回operationId：" +soperationId+ "\r\n");
									String soperationType=operations.getJSONObject(ri).getString("operationType");//
									logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天取消订单-查詢接口返回operationType：" +soperationType+ "\r\n");
									String soperationStatus=operations.getJSONObject(ri).getString("operationStatus");//
									logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天取消订单-查詢接口返回operationStatus：" +soperationStatus+ "\r\n");
									String ssubmittedDate=operations.getJSONObject(ri).getString("submittedDate");//
									logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天取消订单-查詢接口返回submittedDate：" +ssubmittedDate+ "\r\n");
									String scompletedDate=operations.getJSONObject(ri).getString("completedDate");//
									logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天取消订单-查詢接口返回completedDate：" +scompletedDate+ "\r\n");
									int ssubmittedCount=operations.getJSONObject(ri).getInt("submittedCount");//
									logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天取消订单-查詢接口返回submittedCount：" +ssubmittedCount+ "\r\n");
									ssuccessCount=operations.getJSONObject(ri).getInt("successCount");//
									logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天取消订单-查詢接口返回successCount：" +ssuccessCount+ "\r\n");
									int sfailureCount=operations.getJSONObject(ri).getInt("failureCount");//
									logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"樂天取消订单-查詢接口返回failureCount：" +sfailureCount+ "\r\n");
								}

								//成功
								if (ssuccessCount>0) 
								{									
									isSuccess = true;
								}	
								else 
								{
									error_orderno+="乐天订单：" + ecOrderNo +"<br/>取消-查询接口失败：<br/>";
								}

							}

						}


					}
				}
				else if(ecPlatformNo.equals("pchome"))//PChome是EXCEL导入订单，没有API接口，提示一下就行了
				{			
					isSuccess = true;//EXCEL订单状态更新，但是需要提醒一下
					
					error_orderno+="pchome订单：" + ecOrderNo +"没有API接口，请到电商后台取消<br/>";					
				}
				else if(ecPlatformNo.equals("4"))//云POS订单在POS机上处理
				{
					isSuccess=false;
					
					error_orderno+="云POS订单：" + ecOrderNo +"请到POS机上取消<br/>";					
				}
				
				if (isSuccess) 
				{
					
					// 接口调用成功后，更新OC_ORDER表的字段STATUS='3'，REFUNDREASON='具体原因'
					UptBean ub1 = new UptBean("OC_ORDER");			
					ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
					ub1.addCondition("SHOPID", new DataValue(myshop, Types.VARCHAR));
					ub1.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));

					ub1.addUpdateValue("REFUNDREASON",new DataValue(cancelReasonDesc, Types.VARCHAR)); 
					ub1.addUpdateValue("STATUS",new DataValue("3", Types.VARCHAR)); // 3.已拒单
					this.addProcessData(new DataProcessBean(ub1));	

					//接單日誌
					DataValue[] insValueOrderStatus_LOG = new DataValue[] 
							{ 
									new DataValue(req.geteId(), Types.VARCHAR),
									new DataValue(myshop, Types.VARCHAR), // 组织编号=门店编号
									new DataValue(myshop, Types.VARCHAR), // 映射后的门店
									new DataValue(ecOrderNo, Types.VARCHAR), //
									new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
									new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
									new DataValue("訂單状态", Types.VARCHAR), // 状态类型名称
									new DataValue("3", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
									new DataValue("已拒單", Types.VARCHAR), // 状态名称
									new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
									new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
									new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
									new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
									new DataValue("admin", Types.VARCHAR), //操作員編碼
									new DataValue("管理員", Types.VARCHAR), //操作員名稱
									new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
									new DataValue("訂單状态-->已拒單", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
									new DataValue("100", Types.VARCHAR) 
							};
					InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
					ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
					this.addProcessData(new DataProcessBean(ibOrderStatusLog));

					this.doExecuteDataToDB();						
					
				}
			}
			else
			{ 
				// 若查不到电商平台基础信息， 提示平台信息异常
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, ecPlatformNo + "平台基础信息异常");
			}

		}
		
		//失败原因
		if (error_orderno.equals("")==false) 
		{
			res.setSuccess(false);
			res.setServiceDescription("取消订单失败原因：<br/>" +error_orderno );
		}
		else 
		{
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");		
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECSellerCancelReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECSellerCancelReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECSellerCancelReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECSellerCancelReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");  

		List<level1Elm> datas = req.getDatas();
		for(level1Elm detail : datas)
		{
			String ecPlatformNo = detail.getEcPlatformNo();
			String ecOrderNo = detail.getEcOrderNo();
			String cancelReason = detail.getCancelReason();

			if(Check.Null(ecPlatformNo))
			{
				errMsg.append("电商平台编码不可为空值, ");
				isFail = true;
			}
			if(Check.Null(ecOrderNo))
			{
				errMsg.append("订单号不可为空值, ");
				isFail = true;
			}
			if(Check.Null(cancelReason))
			{
				errMsg.append("取消原因不可为空值, ");
				isFail = true;
			}
			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECSellerCancelReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECSellerCancelReq>(){};
	}

	@Override
	protected DCP_OrderECSellerCancelRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECSellerCancelRes();
	}

}
