package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_PayQuery_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 支付查询， 调用CRM : Query 接口 
 * @author Huawei
 *
 */
public class DCP_PayQuery_Open extends SPosAdvanceService<DCP_PayQuery_OpenReq, DCP_PayQuery_OpenRes> {
	Logger logger = LogManager.getLogger(this.getClass().getName());
	
	@Override
	protected void processDUID(DCP_PayQuery_OpenReq req, DCP_PayQuery_OpenRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			
			String eId = req.getRequest().getoEId();
			String shopId = req.getRequest().getShop_code();
			String pay_type = req.getRequest().getPay_type() == null ? "":  req.getRequest().getPay_type();
			String order_id = req.getRequest().getOrder_id()  == null ? "": req.getRequest().getOrder_id();
			String trade_no = req.getRequest().getTrade_no()  == null ? "": req.getRequest().getTrade_no()  ;
			String shop_code = req.getRequest().getShop_code()  == null ? "":  req.getRequest().getShop_code() ;
			String pos_code = req.getRequest().getPos_code() == null ? "": req.getRequest().getPos_code();
			String channel_id = req.getRequest().getChannel_id() == null ? "":req.getRequest().getChannel_id()  ;
			String operation_id = req.getRequest().getOperation_id() == null ? "": req.getRequest().getOperation_id();
			String ip = req.getRequest().getIp() == null ? "":  req.getRequest().getIp();
			String appid = req.getRequest().getAppid() == null ? "":req.getRequest().getAppid();
			
 			String crmPayUrl = PosPub.getPAY_INNER_URL(eId);
 			
			// 先写死用 wxaf5eb822e74ad96f 测试
			String key = appid;
			
			if(Check.Null(crmPayUrl)){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "移动支付接口地址未设置！");
			}
			
			if(Check.Null(key)){
				// 会员系统接入帐号
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "会员系统接入帐号未设置！");
			}
			
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
			String bdate = df.format(cal.getTime());
			
			JSONObject QueryReq = new JSONObject();	
			QueryReq.put("serviceId", "Query");
			
			JSONObject payReq = new JSONObject();
			payReq.put("pay_type", pay_type);
			payReq.put("shop_code", shop_code);
			payReq.put("pos_code", pos_code);
			payReq.put("order_id", order_id);
			payReq.put("trade_no", trade_no);
			payReq.put("channel_id", channel_id);
			payReq.put("operation_id", operation_id);
			payReq.put("ip", ip);
			
			QueryReq.put("request", payReq);
			
			String reqStr = payReq.toString();
			String sign = PosPub.encodeMD5(reqStr + key); 
			
			JSONObject signJson = new JSONObject();
			signJson.put("sign", sign);
			signJson.put("key", key);
			
			QueryReq.put("sign", signJson);
			
			//********** 已经准备好Query的json，开始调用 *************
			String payResStr = HttpSend.Sendcom(QueryReq.toString(), crmPayUrl).trim();
			
			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"*********** DCP_PayQuery_Open 调用Query接口信息：地址（"+crmPayUrl+"）  请求Json："  + QueryReq);
			
			JSONObject payResJson = new JSONObject();
			payResJson = JSON.parseObject(payResStr);//String转json
			
			String paySuccess = payResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
			String payServiceStatus = payResJson.getString("serviceStatus").toUpperCase();
			String payServiceDescription = payResJson.getString("serviceDescription").toUpperCase();
			
			String datasStr = payResJson.getString("datas");
			
			JSONObject datasJson = new JSONObject();
			datasJson =JSONObject.parseObject(datasStr);
			
//			String out_trade_no = datasJson.getString("trade_no").toString();
			
			boolean isSuc = false;
			if(!Check.Null(paySuccess) && paySuccess.equals("TRUE")){
				isSuc = true;

				// 调用回写订单的接口 
				// ************* 写付款档开始 *****************
				
				if(Check.Null(order_id)){
					logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"未查到"+order_id+"对应的预支付订单号");
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "未查到"+order_id+"对应的预支付订单号");
				}else{
					
					String payInfoSql = " select a.EID , a.SHOPID , a.orderNo , a.payCode,  a.payCodeERP , a.payName , a.cardNO, a.ctType , a.paySerNum ,  "
							+ "  a.serialNo , a.refNo , a.teriminalNo , a.descore, a.pay , a.extra , a.changed , a.bDate , a.isOrderPay , a.status , "
							+ "  a.load_docType , a.order_payCode , a.ISONLINEPAY , a.rcPay , a.shop_pay, a.load_doctype_Pay , a.orderpayId ,"
							+ "  a.canInvoice, a.invoiceNo , a.isInvoice , a.invoiceLoadType , a.prepay_id  , b.payStatus,  b.payAmt  "
							+ "  from DCP_PreOrder_pay  a "
							+ "  LEFT JOIN DCP_PreOrder b  ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.orderNo = b.orderNo AND a.prepay_id = b.prepay_id "
							+ " where a.EID = '"+eId+"' and a.SHOPID = '"+shopId+"' "
							+ " and a.prepay_id = '"+order_id+"' ";
					
					List<Map<String, Object>> crmPayDatas = dao.executeQuerySQL(payInfoSql, null);
					
					if(crmPayDatas != null && !crmPayDatas.isEmpty()){ 
						// 外卖点餐支付只会有一种支付方式， 也就是一条信息。 
						// 用循环是因为开发测试的时候会有重复的数据，防止报错
						
						for (Map<String, Object> map : crmPayDatas) {
							String payStatus = map.get("PAYSTATUS").toString() == null ? "" : map.get("PAYSTATUS").toString();
							String orderNo =  map.get("ORDERNO").toString() == null ? "" : map.get("ORDERNO").toString();
							
							if(Check.Null(orderNo)){
								continue;
							}
							
							if(payStatus.equals("3")){ //已付清
								continue;
							}
							
							
							String loadDocType =  map.get("LOAD_DOCTYPE").toString() == null ? "" : map.get("LOAD_DOCTYPE").toString();
							String payCode = map.get("PAYCODE").toString() == null ? "" : map.get("PAYCODE").toString();
							String payCodeERP = map.get("PAYCODEERP").toString()== null  ? "" : map.get("PAYCODEERP").toString();
							String PAYNAME = map.get("PAYNAME").toString() == null ? "" : map.get("PAYNAME").toString();
							String CARDNO = map.get("CARDNO").toString() == null ? "" : map.get("CARDNO").toString();
							String CTTYPE = map.get("CTTYPE").toString() == null ? "" : map.get("CTTYPE").toString();
							String SERIALNO = map.get("SERIALNO").toString()== null ? "" : map.get("SERIALNO").toString();
							String REFNO = map.get("REFNO").toString()== null  ? "" :  map.get("REFNO").toString();
							String TERIMINALNO = map.get("TERIMINALNO").toString()== null  ? "" : map.get("TERIMINALNO").toString();
							String DESCORE = map.get("DESCORE").toString() == null ? "0" : map.get("DESCORE").toString() ;
							String PAY = map.get("PAY").toString() == null ? "0" : map.get("PAY").toString();
							String EXTRA = map.get("EXTRA").toString() == null ? "0" : map.get("EXTRA").toString();
							String CHANGED = map.get("CHANGED").toString()== null ? "0" : map.get("CHANGED").toString();
							String ISORDERPAY = map.get("ISORDERPAY").toString() == null ? "" : map.get("ISORDERPAY").toString();
							String ISONLINEPAY = map.get("ISONLINEPAY").toString() == null ? "" : map.get("ISONLINEPAY").toString();
							String ORDER_PAYCODE = map.get("ORDER_PAYCODE").toString()== null  ? "" : map.get("ORDER_PAYCODE").toString();
							String RCPAY = map.get("RCPAY").toString() == null ? "0" : map.get("RCPAY").toString();
							String SHOP_PAY = map.get("SHOP_PAY").toString()== null ? "" : map.get("SHOP_PAY").toString();
							String LOAD_DOCTYPE_PAY = map.get("LOAD_DOCTYPE_PAY").toString()== null ? "" : map.get("LOAD_DOCTYPE_PAY").toString();
							
							UptBean ubec = new UptBean("OC_ORDER");
							ubec.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							ubec.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
							ubec.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
							
							ubec.addUpdateValue("PAYSTATUS", new DataValue("3", Types.VARCHAR)); // 3：已付清
							ubec.addUpdateValue("PAYAMT", new DataValue(PAY, Types.VARCHAR));
							
							UptBean ubec2 = new UptBean("DCP_PREORDER");
							ubec2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							ubec2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
							ubec2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
							ubec2.addCondition("PREPAY_ID", new DataValue(order_id, Types.VARCHAR));
							
							ubec2.addUpdateValue("PAYSTATUS", new DataValue("3", Types.VARCHAR)); // 3：已付清
							this.addProcessData(new DataProcessBean(ubec));
							this.addProcessData(new DataProcessBean(ubec2));
							
							// 外卖点餐该sql 只会查一次 
							String sqlString = "select * from (select nvl(max(item), '0') + 1  as MAXITEM from OC_order_pay where EID='"
									+ eId + "'  and orderno='" + orderNo + "' )";
							
							List<Map<String, Object>> itemList = dao.executeQuerySQL(sqlString, null);
							int maxItem = 1;
							if (itemList != null && !itemList.isEmpty()) {
								try {
									maxItem = Integer.parseInt(itemList.get(0).get("MAXITEM").toString());
									
								} catch (Exception e) {
									
								}
							}
							
							String[] columns2 = { "EID", "ORGANIZATIONNO", "SHOPID", "ORDERNO", "ITEM", "LOAD_DOCTYPE",
									"PAYCODE", "PAYCODEERP", "PAYNAME", "CARDNO", "CTTYPE", "PAYSERNUM", "SERIALNO", "REFNO",
									"TERIMINALNO", "DESCORE", "PAY", "EXTRA", "CHANGED", "BDATE", "ISORDERPAY", "STATUS",
									"ISONLINEPAY", "ORDER_PAYCODE", "RCPAY", "SHOP_PAY", "LOAD_DOCTYPE_PAY", "ORDERPAYID",
									"PROCESS_STATUS" };
							DataValue[] insValue2 = null;
							
							insValue2 = new DataValue[] { new DataValue(eId, Types.VARCHAR), // EID
									new DataValue(shopId, Types.VARCHAR), // ORGANIZATIONNO
																		// 数据库下单门店与单头一致
									new DataValue(shopId, Types.VARCHAR), // SHOPID
									new DataValue(orderNo, Types.VARCHAR), // ORDERNO
									new DataValue(maxItem+"", Types.INTEGER), // ITEM
									new DataValue(loadDocType, Types.VARCHAR), // LOAD_DOCTYPE
									new DataValue(payCode, Types.VARCHAR), // PAYCODE
									new DataValue(payCodeERP, Types.VARCHAR), // PAYCODEERP
									new DataValue(PAYNAME, Types.VARCHAR), // PAYNAME
									new DataValue(CARDNO, Types.VARCHAR), // CARDNO
									new DataValue(CTTYPE, Types.VARCHAR), // CTTYPE
									new DataValue(order_id, Types.VARCHAR), // PAYSERNUM 支付订单号
									new DataValue(SERIALNO, Types.VARCHAR), // SERIALNO 银联卡
									new DataValue(REFNO, Types.VARCHAR), // REFNO
									new DataValue(TERIMINALNO, Types.VARCHAR), // TERIMINALNO
									new DataValue(DESCORE, Types.VARCHAR), // DESCORE
									new DataValue(PAY, Types.VARCHAR), // PAY 金额
									new DataValue(EXTRA, Types.VARCHAR), // EXTRA 溢收金额
									new DataValue(CHANGED, Types.VARCHAR), // CHANGED 找零
									new DataValue(bdate, Types.VARCHAR), // BDATE 收银营业日期
									new DataValue(ISORDERPAY, Types.VARCHAR), // ISORDERPAY 是否定金
									new DataValue("100", Types.VARCHAR), // STATUS
									new DataValue(ISONLINEPAY, Types.VARCHAR), // ISONLINEPAY 是否平台支付 
									new DataValue(ORDER_PAYCODE, Types.VARCHAR), // ORDER_PAYCODE
									new DataValue(RCPAY, Types.VARCHAR), // RCPAY
									new DataValue(SHOP_PAY, Types.VARCHAR), // SHOP_PAY
									new DataValue(LOAD_DOCTYPE_PAY, Types.VARCHAR), // LOAD_DOCTYPE_PAY 付款的来源平台类型
									new DataValue(order_id, Types.VARCHAR), // ORDERPAYID //定金补录单号
									new DataValue("N", Types.VARCHAR) 
							};
							
							InsBean ib2 = new InsBean("OC_ORDER_PAY", columns2);
							ib2.addValues(insValue2);
							this.addProcessData(new DataProcessBean(ib2));
							
							
							
							
							this.doExecuteDataToDB();
						}
						
					}
					
					// ************* 写付款档结束 *****************
				}
				
			}
			
			res.setSuccess(isSuc);
			res.setServiceStatus(payServiceStatus);
			res.setServiceDescription(payServiceDescription);
			
			JSONObject resDatas = new JSONObject();
			resDatas = payResJson.getJSONObject("datas");
			res.setDatas(resDatas);
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PayQuery_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayQuery_OpenReq>(){};
	}

	@Override
	protected DCP_PayQuery_OpenRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PayQuery_OpenRes();
	}}
