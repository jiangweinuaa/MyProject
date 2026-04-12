package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MemberPay_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq;
import com.dsc.spos.json.cust.req.DCP_MemberPay_OpenReq.reqJson;
import com.dsc.spos.json.cust.req.DCP_MemberPay_OpenReq.level3Elm;
import com.dsc.spos.json.cust.req.DCP_MemberPay_OpenReq.levelPayElm;
import com.dsc.spos.json.cust.res.DCP_MemberPay_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

/**
 * 会员支付： 调用 CRM ： MemberPay 接口
 * 此接口专用于会员支付，支付成功后即处理中台单据。
 * @author Huawei
 *
 */
public class DCP_MemberPay_Open extends SPosAdvanceService<DCP_MemberPay_OpenReq, DCP_MemberPay_OpenRes> {

	@Override
	protected void processDUID(DCP_MemberPay_OpenReq req, DCP_MemberPay_OpenRes res) throws Exception {
		// TODO Auto-generated method stub

		try {

			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
			String bdate = df.format(cal.getTime());

			String orderNo = req.getRequest().getOrderNo(); //交易订单号
			String eId = req.getRequest().getoEId();
			String shopId = req.getRequest().getOrgId();

			String srcBillNo = "";//订单中心订单号
			String srcBillType = " ";
			if(!Check.Null(req.getRequest().getSrcBillNo())){
				srcBillNo = req.getRequest().getSrcBillNo();
			}
			if(!Check.Null(req.getRequest().getSrcBillType())){
				srcBillType = req.getRequest().getSrcBillType();
			}

			List<level3Elm> payDetail = req.getRequest().getPayDetail();

			List<levelPayElm> payElm = req.getPay();
			reqJson reqJson = req.getRequest();

			//会员系统接入服务地址
			String memberUrl = PosPub.getPARA_SMS(dao, eId, "", "YCUrl");
			String key = PosPub.getPARA_SMS(dao, eId, "", "YCKey");
			String signKey = PosPub.getPARA_SMS(dao, eId, "", "YCSignKey");

			if(Check.Null(memberUrl)){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "会员系统接入服务地址未设置");
			}
			if(Check.Null(key)){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "会员系统接入帐号未设置");
			}
			if(Check.Null(signKey)){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "会员系统接入帐号密钥未设置");
			}

			if(!Check.Null(memberUrl) && !Check.Null(key)){

				String memberPayReqStr = JSON.toJSONString(reqJson);//对象转String
				JSONObject memberPayReqJson = new JSONObject();
				memberPayReqJson = JSON.parseObject(memberPayReqStr);//String转json

				JSONObject memberPayReq = new JSONObject();	
				memberPayReq.put("serviceId", "MemberPay");
				memberPayReq.put("request", memberPayReqJson);

				String reqStr = memberPayReqJson.toString();
				String sign = PosPub.encodeMD5(reqStr + signKey); 

				JSONObject signJson = new JSONObject();
				signJson.put("sign", sign);
				signJson.put("key", key);

				memberPayReq.put("sign", signJson);

				//********** 已经准备好MemberPay的json，开始调用 *************
				String payResStr = HttpSend.Sendcom(memberPayReq.toString(), memberUrl).trim();

				JSONObject payResJson = new JSONObject();
				payResJson = JSON.parseObject(payResStr);//String转json

				String paySuccess = payResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
				//CRM 接口提示： 返回false时，并不表示一定是创建支付失败，有可能顾客还在支付中，需判断serviceStatus
				String payServiceStatus = payResJson.getString("serviceStatus").toUpperCase();
				String payServiceDescription = payResJson.getString("serviceDescription").toUpperCase();

				boolean isSuc = false;
				if(!Check.Null(paySuccess) && paySuccess.equals("TRUE")){
					isSuc = true;
					double totAmt = 0;
					String payMemo = "";
					String sqlString = "select * from (select nvl(max(item), '0') + 1  as MAXITEM from OC_order_pay where EID='"
							+ eId + "'  and orderno='" + srcBillNo + "' )";

					List<Map<String, Object>> itemList = dao.executeQuerySQL(sqlString, null);
					int maxItem = 1;
					if (itemList != null && !itemList.isEmpty()) {
						try {
							maxItem = Integer.parseInt(itemList.get(0).get("MAXITEM").toString());

						} catch (Exception e) {

						}
					}


					if(payDetail != null && !payDetail.isEmpty()){

						if(payElm != null && payElm.size() > 0){
							String curdate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
							double realPay_tot = 0;//实际付款金额(录入金额-找零-溢收)合计

							for (levelPayElm map : payElm) {

								//付款金额
								double realPay = 0;//实际付款金额=录入金额-找零-溢收=pay-change-extra

								double payAmount = 0;
								try {
									payAmount = Double.parseDouble(map.getPay());
								} catch (Exception e) {

								}

								//找零
								double changed = 0;
								try {
									changed = Double.parseDouble(map.getChanged());
								} catch (Exception e) {

								}

								//溢收
								double extra = 0;
								try {
									extra = Double.parseDouble(map.getExtra());
								} catch (Exception e) {

								}

								realPay = payAmount-changed-extra;
								if(realPay<0)
								{
									realPay = 0;
								}
								realPay_tot += realPay;

								//积分
								double descore = 0;
								try {

									descore = Double.parseDouble(map.getDescore());

								} catch (Exception e) {

								}

								//								String bdate = map.getBdate();
								if(bdate==null||bdate.trim().isEmpty())
								{
									bdate = curdate;
								}

								String payShop = map.getPayShop();
								if(payShop==null||payShop.trim().isEmpty())
								{
									payShop = shopId;
								}

								String payLoadDocType = map.getPayLoadDocType();
								if(payLoadDocType==null||payLoadDocType.trim().isEmpty())
								{
									payLoadDocType = "";
								}

								String payCode = map.getPayCode();
								String payCodeerp = map.getPayCodeerp();
								String payName = map.getPayName();
								String payCardNO = map.getCardNO();
								String ctType = map.getCtType();
								String paySernum = map.getPaySernum();
								String serialNO = map.getSerialNO();		
								String isOrderpay = map.getIsOnlinePay();
								String isOnlinePay = map.getIsOnlinePay();
								String refNO = map.getRefNO();
								//								String teriminalNO = map.getTeriminalNO();
								String teriminalNO = map.getTeriminalNO();
								payMemo+="项次："+map.getItem() +" 付款方式："+map.getPayName() +" 金额 ："+map.getPay()+" 找零："+map.getChanged()+" 溢收："+map.getExtra()+"<br>";

								if (payCode != null && payCode.length() > 10) {
									payCode = payCode.substring(0, 10);
								}
								if (payCodeerp != null && payCodeerp.length() > 10) {
									payCodeerp = payCodeerp.substring(0, 10);
								}
								if (payName != null && payName.length() > 80) {
									payName = payName.substring(0, 80);
								}
								if (payCardNO != null && payCardNO.length() > 40) {
									payCardNO = payCardNO.substring(0, 40);
								}
								if (ctType != null && ctType.length() > 40) {
									ctType = ctType.substring(0, 40);// 数据库最长40
								}
								if (paySernum != null && paySernum.length() > 100) {
									paySernum = paySernum.substring(0, 100);// 数据库最长100
								}
								if (serialNO != null && serialNO.length() > 40) {
									serialNO = serialNO.substring(0, 40);// 数据库最长40
								}
								if (refNO != null && refNO.length() > 40) {
									refNO = refNO.substring(0, 40);// 数据库最长120
								}
								if (teriminalNO != null && teriminalNO.length() > 40) {
									teriminalNO = teriminalNO.substring(0, 40);// 数据库最长120
								}


								if (payShop != null && payShop.length() > 20) {
									payShop = payShop.substring(0, 20);// 数据库最长20
								}
								if (payLoadDocType != null && payLoadDocType.length() > 30) {
									payLoadDocType = payLoadDocType.substring(0, 30);// 数据库最长30
								}

								if (bdate != null && bdate.length() > 8) {
									bdate = bdate.substring(0, 8);// 数据库最长8
								}
								if (isOrderpay != null && isOrderpay.length() > 1) {
									isOrderpay = isOrderpay.substring(0, 1);// 数据库最长1
								}

								isOrderpay="Y";//炮哥说写死，不取传的值

								if (isOnlinePay != null && isOnlinePay.length() > 1) {
									isOnlinePay = isOnlinePay.substring(0, 1);// 数据库最长1
								}

								String iSSendErp_status = "N";//订金增加的付款方式是否上传ERP


								String[] columns2 = { "EID", "ORGANIZATIONNO", "SHOPID", "ORDERNO", "ITEM", "LOAD_DOCTYPE",
										"PAYCODE", "PAYCODEERP", "PAYNAME", "CARDNO", "CTTYPE", "PAYSERNUM", "SERIALNO", "REFNO",
										"TERIMINALNO", "DESCORE", "PAY", "EXTRA", "CHANGED", "BDATE", "ISORDERPAY", "STATUS",
										"ISONLINEPAY", "ORDER_PAYCODE", "RCPAY", "SHOP_PAY", "LOAD_DOCTYPE_PAY","ORDERPAYID","PROCESS_STATUS" };
								DataValue[] insValue2 = null;

								insValue2 = new DataValue[] { new DataValue(eId, Types.VARCHAR), // EID
										new DataValue(shopId, Types.VARCHAR), // ORGANIZATIONNO 数据库下单门店与单头一致
										new DataValue(shopId, Types.VARCHAR), // SHOPID
										new DataValue(srcBillNo, Types.VARCHAR), // ORDERNO
										new DataValue(maxItem, Types.INTEGER), // ITEM
										new DataValue(srcBillType, Types.VARCHAR), // LOAD_DOCTYPE
										new DataValue(payCode, Types.VARCHAR), // PAYCODE
										new DataValue(payCodeerp, Types.VARCHAR), // PAYCODEERP
										new DataValue(payName, Types.VARCHAR), // PAYNAME
										new DataValue(payCardNO, Types.VARCHAR), // CARDNO
										new DataValue(ctType, Types.VARCHAR), // CTTYPE
										new DataValue(paySernum, Types.VARCHAR), // PAYSERNUM
										new DataValue(serialNO, Types.VARCHAR), // SERIALNO
										new DataValue(refNO, Types.VARCHAR), // REFNO
										new DataValue(teriminalNO, Types.VARCHAR), // TERIMINALNO
										new DataValue(descore, Types.VARCHAR), // DESCORE
										new DataValue(payAmount, Types.VARCHAR), // PAY
										new DataValue(extra, Types.VARCHAR), // EXTRA
										new DataValue(changed, Types.VARCHAR), // CHANGED
										new DataValue(bdate, Types.VARCHAR), // BDATE
										new DataValue(isOrderpay, Types.VARCHAR), // ISORDERPAY
										new DataValue("100", Types.VARCHAR), // STATUS
										new DataValue(isOnlinePay, Types.VARCHAR), // ISONLINEPAY
										new DataValue("", Types.VARCHAR), // ORDER_PAYCODE
										new DataValue("0", Types.VARCHAR), // RCPAY
										new DataValue(payShop, Types.VARCHAR), // SHOP_PAY
										new DataValue(payLoadDocType, Types.VARCHAR), // LOAD_DOCTYPE_PAY
										new DataValue(orderNo, Types.VARCHAR), //ORDERPAYID
										new DataValue(iSSendErp_status, Types.VARCHAR) //
								};

								InsBean ib2 = new InsBean("OC_ORDER_PAY", columns2);
								ib2.addValues(insValue2);
								this.addProcessData(new DataProcessBean(ib2));
								maxItem++;

							}

						}else{


							String payInfoSql = " select a.EID , a.SHOPID , a.orderNo , a.payCode,  a.payCodeERP , a.payName , a.cardNO, a.ctType , a.paySerNum ,  "
									+ "  a.serialNo , a.refNo , a.teriminalNo , a.descore, a.pay , a.extra , a.changed , a.bDate , a.isOrderPay , a.status , "
									+ "  a.load_docType , a.order_payCode , a.ISONLINEPAY , a.rcPay , a.shop_pay, a.load_doctype_Pay , a.orderpayId ,"
									+ "  a.canInvoice, a.invoiceNo , a.isInvoice , a.invoiceLoadType , a.prepay_id  , b.payStatus,  b.payAmt  "
									+ "  from DCP_PreOrder_pay  a "
									+ "  LEFT JOIN DCP_PreOrder b  ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.orderNo = b.orderNo AND a.prepay_id = b.prepay_id "
									+ " where a.EID = '"+eId+"' and a.SHOPID = '"+shopId+"' "
									+ " and a.prepay_id = '"+orderNo+"' ";

							List<Map<String, Object>> crmPayDatas = dao.executeQuerySQL(payInfoSql, null);

							if(crmPayDatas != null && !crmPayDatas.isEmpty()){ 
								// 外卖点餐支付只会有一种支付方式， 也就是一条信息。 
								// 用循环是因为开发测试的时候会有重复的数据，防止报错

								for (Map<String, Object> map : crmPayDatas) {
									String payStatus = map.get("PAYSTATUS").toString() == null ? "" : map.get("PAYSTATUS").toString();
									String ctOrderNo =  map.get("ORDERNO").toString() == null ? "" : map.get("ORDERNO").toString();

									if(Check.Null(ctOrderNo)){
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
									ubec.addCondition("ORDERNO", new DataValue(ctOrderNo, Types.VARCHAR));

									ubec.addUpdateValue("PAYSTATUS", new DataValue("3", Types.VARCHAR)); // 3：已付清
									ubec.addUpdateValue("PAYAMT", new DataValue(PAY, Types.VARCHAR));

									UptBean ubec2 = new UptBean("DCP_PREORDER");
									ubec2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
									ubec2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
									ubec2.addCondition("ORDERNO", new DataValue(ctOrderNo, Types.VARCHAR));
									//									ubec2.addCondition("PREPAY_ID", new DataValue(tradeNo, Types.VARCHAR));
									ubec2.addCondition("PREPAY_ID", new DataValue(orderNo, Types.VARCHAR));

									ubec2.addUpdateValue("PAYSTATUS", new DataValue("3", Types.VARCHAR)); // 3：已付清
									this.addProcessData(new DataProcessBean(ubec));
									this.addProcessData(new DataProcessBean(ubec2));

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
											new DataValue(ctOrderNo, Types.VARCHAR), // ORDERNO
											new DataValue(maxItem+"", Types.INTEGER), // ITEM
											new DataValue(loadDocType, Types.VARCHAR), // LOAD_DOCTYPE
											new DataValue(payCode, Types.VARCHAR), // PAYCODE
											new DataValue(payCodeERP, Types.VARCHAR), // PAYCODEERP
											new DataValue(PAYNAME, Types.VARCHAR), // PAYNAME
											new DataValue(CARDNO, Types.VARCHAR), // CARDNO
											new DataValue(CTTYPE, Types.VARCHAR), // CTTYPE
											new DataValue(orderNo, Types.VARCHAR), // PAYSERNUM 支付订单号
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
											new DataValue(orderNo, Types.VARCHAR), // ORDERPAYID //定金补录单号
											new DataValue("N", Types.VARCHAR) 
									};

									InsBean ib2 = new InsBean("OC_ORDER_PAY", columns2);
									ib2.addValues(insValue2);
									this.addProcessData(new DataProcessBean(ib2));

									this.doExecuteDataToDB();

									//写日志结束

								}

							}


						}

						// 订单支付成功写日志
						try
						{/*
							DCP_OrderStatusLogCreateReq req_log = new DCP_OrderStatusLogCreateReq();
							req_log.setDatas(new ArrayList<DCP_OrderStatusLogCreateReq.level1Elm>());

							DCP_OrderStatusLogCreateReq.level1Elm onelv1 = req_log.new level1Elm();
							onelv1.setCallback_status("0");
							onelv1.setLoadDocType(srcBillType);

							onelv1.setNeed_callback("N");
							onelv1.setNeed_notify("N");

							onelv1.setoEId(eId);

							String opNO = "";
							//							
							String o_opName = "";

							onelv1.setO_opName(o_opName);
							onelv1.setO_opNO(opNO);

							onelv1.setO_organizationNO(shopId);//下单门店
							onelv1.setoShopId(shopId);
							onelv1.setOrderNO(srcBillNo);
							String statusType = "4";//其他状态
							String updateStaus = "2";//订金补录

							onelv1.setStatusType(statusType);				 					
							onelv1.setStatus(updateStaus);
							StringBuilder statusTypeNameObj = new StringBuilder();
							String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
							String statusTypeName = statusTypeNameObj.toString();
							onelv1.setStatusTypeName(statusTypeName);
							onelv1.setStatusName(statusName);

							String memo = "";
							memo += statusTypeName+"-->" + statusName+"(付清)<br>";

							memo += payMemo;
							onelv1.setMemo(memo);
							String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
							onelv1.setUpdate_time(updateDatetime);
							req_log.getDatas().add(onelv1);

							String req_log_json ="";
							try
							{
								ParseJson pj = new ParseJson();					
								req_log_json = pj.beanToJson(req_log);
							}
							catch(Exception e)
							{

							}			   			   			  	
							StringBuilder errorMessage = new StringBuilder();
							boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, req_log_json, errorMessage);
							if(nRet)
							{		  		 
								HelpTools.writelog_waimai("【写表OC_orderStatuslog保存成功】"+" 订单号orderNO:"+srcBillNo);
							}
							else
							{			  		 
								HelpTools.writelog_waimai("【写表OC_orderStatuslog异常】"+errorMessage.toString()+" 订单号orderNO:"+srcBillNo);
							}
							this.pData.clear();


						*/}
						catch (Exception  e)
						{
							HelpTools.writelog_waimai("【写表OC_orderStatuslog异常】 异常报错 "+e.toString()+" 订单号orderNO:"+srcBillNo);
						}			
						//endregion

					}


				}

				res.setSuccess(isSuc);
				res.setServiceStatus(payServiceStatus);
				res.setServiceDescription(payServiceDescription);

				JSONObject resDatas = new JSONObject();
				resDatas = payResJson.getJSONObject("datas");
				res.setDatas(resDatas);

			}

		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MemberPay_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MemberPay_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MemberPay_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MemberPay_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;

		return isFail;
	}

	@Override
	protected TypeToken<DCP_MemberPay_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MemberPay_OpenReq>(){};
	}

	@Override
	protected DCP_MemberPay_OpenRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MemberPay_OpenRes();
	}


}
