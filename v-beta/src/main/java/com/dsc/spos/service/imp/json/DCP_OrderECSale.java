package com.dsc.spos.service.imp.json;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECSaleReq;
import com.dsc.spos.json.cust.res.DCP_OrderECSaleRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderECSale extends SPosAdvanceService<DCP_OrderECSaleReq, DCP_OrderECSaleRes>
{

	Logger logger = LogManager.getLogger(DCP_OrderECSale.class.getName());
	
	@Override
	protected void processDUID(DCP_OrderECSaleReq req, DCP_OrderECSaleRes res) throws Exception {
		// TODO Auto-generated method stub
		//***************调用订转销服务OrderToSaleProcess*************
		String sqlpayString="select A.* from OC_order A  "
				+ " where A.EID='"+req.getRequest().geteId()+"' and A.load_doctype='"+req.getRequest().getDocType()+"' and A.orderno='"+req.getRequest().getOrderNO()+"'    ";
		List<Map<String, Object>> getQuerySql=this.doQueryData(sqlpayString, null);
		//下订门店
		String shopId="";
		//超商取货门店
		String getShop="";
		//配送门店
		String shipShop="";

		String ecOrderNo=req.getRequest().getOrderNO();
		String eId=req.getRequest().geteId();
		String opNo=req.getOpNO();
		String ecPlatformNo=req.getRequest().getDocType();
		
		if(getQuerySql==null||getQuerySql.isEmpty())
		{
			res.setSuccess(false);
			res.setServiceDescription("订单不存在，请确认后再试！");
			return;
		}
		else
		{
			String status=getQuerySql.get(0).get("STATUS").toString();
			if(status.equals("3")||status.equals("11")||status.equals("12"))
			{
				res.setSuccess(false);
				res.setServiceDescription("当前订单状态不允许出货！");
				return;
			}
			shopId=getQuerySql.get(0).get("SHOPID").toString();
			shipShop=getQuerySql.get(0).get("SHIPPINGSHOP").toString();
			getShop=getQuerySql.get(0).get("ORDERSHOP").toString();
			
			String PAYSTATUS=getQuerySql.get(0).get("PAYSTATUS").toString();
			if(!PAYSTATUS.equals("3"))
			{
				res.setSuccess(false);
				res.setServiceDescription("当前订单未付清不允许出货！");
				return;
			}
			
		}
		
		String sql = this.getOrderDetail(req.getRequest().geteId(), shopId, req.getRequest().getOrderNO(), req.getRequest().getDocType());
		String[] conditionValues = {};
		List<Map<String , Object>> getDatas = this.doQueryData(sql, conditionValues);

		//订转销成功
		boolean success =false;
		String errDesc="";
		
		if(getDatas.size() > 0)
		{

			JSONObject reqOTS = new JSONObject();
			JSONArray datasArr = new JSONArray(); 
			JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
			JSONArray detailArr = new JSONArray(); 
			JSONArray agioArr = new JSONArray(); 
			JSONArray payArr = new JSONArray(); 
			JSONArray invArr = new JSONArray(); 
			JSONObject plu_detail = new JSONObject();
			JSONObject agio_detail = new JSONObject();
			JSONObject pay_detail = new JSONObject();
			JSONObject inv_detail = new JSONObject();

			//青哥要orderId=订单号
			String orderId = ecOrderNo;//getDatas.get(0).get("ORDERID").toString();
			String contMan = getDatas.get(0).get("CONTMAN").toString();
			String contTel = getDatas.get(0).get("CONTTEL").toString();
			String address = getDatas.get(0).get("ADDRESS").toString();
			String memo = getDatas.get(0).get("MEMO").toString();
			String orderSN = getDatas.get(0).get("ORDERSN").toString();
			String isInvoice = getDatas.get(0).get("ISINVOICE").toString();

			//给个默认值即可
			if(isInvoice == null || isInvoice.trim().equals("")){
				isInvoice = "N";
			}

			String shipFee = getDatas.get(0).get("SHIPFEE").toString();
			String totAmt = getDatas.get(0).get("TOTAMT").toString();
			String totQty = getDatas.get(0).get("TOTQTY").toString();
			String serviceCharge = getDatas.get(0).get("SERVICECHARGE").toString();
			String inComeAmt = getDatas.get(0).get("INCOMEAMT").toString();
			String totDisc = getDatas.get(0).get("TOTDISC").toString();
			String sellerDisc = getDatas.get(0).get("SELLERDISC").toString();
			String platformDisc = getDatas.get(0).get("PLATFORMDISC").toString();
			String sellNo = getDatas.get(0).get("SELLNO").toString();
			String deliveryType = getDatas.get(0).get("DELIVERYTYPE").toString();
			String payStatus = getDatas.get(0).get("PAYSTATUS").toString();
			String payAmt = getDatas.get(0).get("PAYAMT").toString();
			String passport = getDatas.get(0).get("PASSPORT").toString();
			String freeCode = getDatas.get(0).get("FREECODE").toString();
			String ecCustomerNo = getDatas.get(0).get("ECCUSTOMERNO").toString();

			String token = req.getToken();

			reqOTS.put("serviceId","DCP_OrderToSaleProcess");
			reqOTS.put("docType","1");
			reqOTS.put("token",token);

			header.put("oEId", eId);
			header.put("oShopId", shipShop);
			header.put("bookShopNO", shopId);
			header.put("orderID", orderId);
			header.put("orderNO", ecOrderNo);
			// 单据日期不用传， 直接取系统日期即可
			header.put("o_opNO", opNo);
			header.put("type", "0");// 单据类型： 0 销售单， 1退货
			header.put("loadDocType", ecPlatformNo);
			header.put("shippingShopNO", getShop);
			header.put("contMan", contMan);
			header.put("contTel", contTel);
			header.put("address", address);
			header.put("memo", memo);
			header.put("orderSN", orderSN); 
			header.put("isInvoice", isInvoice);
			header.put("shipFee", shipFee);
			header.put("totAmt", totAmt);
			header.put("totQty", totQty);
			header.put("serviceCharge", serviceCharge);
			header.put("inComeAmt", inComeAmt);//实收金额 
			header.put("totDisc", totDisc);   

			header.put("sellerDisc", sellerDisc); // 商户优惠总额
			header.put("platformDisc", platformDisc); // 平台优惠总额
			header.put("sellNO", sellNo); //订单大客户编号（赊销客户）
			header.put("deliveryType", deliveryType); 
			header.put("payStatus", payStatus); 
			header.put("payamt", payAmt); 
			header.put("passport", passport); 
			header.put("freeCode", freeCode); 
			header.put("ecCustomerNO", ecCustomerNo); 

			Map<String, Boolean> conditionPluDetail = new HashMap<String, Boolean>(); 
			conditionPluDetail.put("DETAILITEM", true);	
			conditionPluDetail.put("PLUNO", true);	
			List<Map<String, Object>> pluDatas=MapDistinct.getMap(getDatas, conditionPluDetail);

			int invItemStr = 1;  /// 逻辑上 invItemStr 不应该程序设置，应该是接口查点发票的item
			for (Map<String, Object> map : pluDatas) {
				String item = map.get("DETAILITEM").toString();
				String pluNo = map.get("PLUNO").toString();
				String pluBarcode = map.get("PLUBARCODE").toString();
				String price = map.get("PRICE").toString();
				String qty = map.get("QTY").toString();
				String amt = map.get("AMT").toString();
				String disc = map.get("DISC").toString();
				String isGift = "N"; //map.get("ISGIFT").toString();
				String isPackage = "N"; //map.get("ISPACKAGE").toString();
				String dealType = "1"; //map.get("DEALTYPE").toString();
				String giftReason = ""; //map.get("GIFTREASON").toString();
				String invItem = invItemStr+""; //map.get("INVITEM").toString();
				String couponInvTax = "0"; //map.get("COUPONINVTAX").toString();

				plu_detail.put("item", item);
				plu_detail.put("pluNO", pluNo);
				plu_detail.put("pluBarcode", pluBarcode);
				plu_detail.put("price", price);
				plu_detail.put("qty", qty);
				plu_detail.put("disc", disc);
				plu_detail.put("amt", amt);
				plu_detail.put("isGift", isGift);
				plu_detail.put("isPackage", isPackage);
				plu_detail.put("dealType", dealType);
				plu_detail.put("giftReason", giftReason);
				plu_detail.put("invItem", invItem);
				plu_detail.put("couponInvTax", couponInvTax);

				detailArr.put(plu_detail);

				invItemStr +=1;
			}

			Map<String, Boolean> conditionAgio = new HashMap<String, Boolean>(); 
			conditionAgio.put("AGIOITEM", true);	
			List<Map<String, Object>> agioDatas=MapDistinct.getMap(getDatas, conditionAgio);
			for (Map<String, Object> map : agioDatas) {
				String agioItem = map.get("AGIOITEM").toString();
				String promName = map.get("PROMNAME").toString();
				String agioAmt = map.get("AGIOAMT").toString();
				String agioSellerDisc = map.get("AGIOSELLERDISC").toString();
				String agioPlatformDisc = map.get("AGIOPLATFORMDISC").toString();

				agio_detail.put("item", agioItem);
				agio_detail.put("promName", promName);
				agio_detail.put("agioAmt", agioAmt);
				agio_detail.put("sellerDisc", agioSellerDisc);
				agio_detail.put("platformDisc", agioPlatformDisc);

				agioArr.put(agio_detail);

			}

			Map<String, Boolean> conditionPay = new HashMap<String, Boolean>(); 
			conditionPay.put("PAYITEM", true);	
			List<Map<String, Object>> payDatas=MapDistinct.getMap(getDatas, conditionPay);
			for (Map<String, Object> map : payDatas) {
				String payItem = map.get("PAYITEM").toString();
				String payCode = map.get("PAYCODE").toString();
				String payCodeErp = map.get("PAYCODEERP").toString();
				// payCode 可能为空， 如果为空的话， 该节点不用再给值
				if(payCode.equals("") || payCode == null || payCodeErp.equals("") || payCodeErp == null){
					continue;
				}

				String payName = map.get("PAYNAME").toString();
				// payName 如果出现payName 为空的话，说明付款方式资料有问题。
				// 不能加验证是否为空， 要保证点货能走完。 

				String isOnlinePay = map.get("ISONLINEPAY").toString();
				String pay = map.get("PAY").toString();

				pay_detail.put("item", payItem);
				pay_detail.put("payCode", payCode);
				pay_detail.put("payCodeErp", payCodeErp);
				pay_detail.put("payName", payName);
				pay_detail.put("isOnlinePay", isOnlinePay);
				pay_detail.put("canInvoice", map.get("CANINVOICE").toString());
				pay_detail.put("invstartNo", map.get("DINVSTARTNO").toString());
				pay_detail.put("isOrderPay", map.get("ISORDERPAY").toString());
				
				pay_detail.put("pay", pay);
				payArr.put(pay_detail);
			}

			Map<String, Boolean> conditionInv = new HashMap<String, Boolean>(); 
			conditionInv.put("INVOICEITEM", true);	
			List<Map<String, Object>> invDatas=MapDistinct.getMap(getDatas, conditionInv);
			for (Map<String, Object> map : invDatas) {
				String invoiceItem = map.get("INVOICEITEM").toString();
				String recordtype = map.get("RECORDTYPE").toString();
				String taxationtype = map.get("TAXATIONTYPE").toString();
				String invtype = map.get("INVTYPE").toString();
				String invformat = map.get("INVFORMAT").toString();
				String buyerguino = map.get("BUYERGUINO").toString();
				String gftinvamt = map.get("GFTINVAMT").toString();
				String gftinvtax = map.get("GFTINVTAX").toString();
				String carriercode = map.get("CARRIERCODE").toString();
				String carriershowid = map.get("CARRIERSHOWID").toString();
				String carrierhiddenid = map.get("CARRIERHIDDENID").toString();
				String lovecode = map.get("LOVECODE").toString();
				String randomcode = map.get("RANDOMCODE").toString();
				String printcount = map.get("PRINTCOUNT").toString();
				String rebateno = map.get("REBATENO").toString();
				String invalidOp = map.get("INVALIDOP").toString();
				String sellerGuiNO = map.get("SELLERGUINO").toString();

				String invalidcode = map.get("INVALIDCODE").toString();
				String invmemo = map.get("INVMEMO").toString();
				String osdate = map.get("OSDATE").toString();


				inv_detail.put("item", invoiceItem);
				inv_detail.put("recordType", recordtype);
				inv_detail.put("taxAtionType", taxationtype);
				inv_detail.put("invType", invtype);
				inv_detail.put("invFormat", invformat);
				inv_detail.put("buyerGuiNO", buyerguino);

				inv_detail.put("gftInvAmt", gftinvamt);
				inv_detail.put("gftInvTax", gftinvtax);
				inv_detail.put("carrierCode", carriercode);
				inv_detail.put("carrierShowID", carriershowid);
				inv_detail.put("carrierHiddenID", carrierhiddenid);
				inv_detail.put("loveCode", lovecode);

				inv_detail.put("randomCode", randomcode);
				inv_detail.put("printCount", printcount);
				inv_detail.put("rebateNO", rebateno);
				// 大爷规格上新增参数。invalidOp 作废人员
				inv_detail.put("invalidOp", invalidOp);
				inv_detail.put("sellerGuiNO", sellerGuiNO);

				inv_detail.put("invalidCode", invalidcode);
				inv_detail.put("invMemo", invmemo);
				inv_detail.put("osDate", osdate);

				invArr.put(inv_detail);
			}

			header.put("detail", detailArr);
			header.put("agio", agioArr);
			header.put("pay", payArr);
			header.put("invoiceDetail", invArr);
			datasArr.put(header);
			reqOTS.put("datas", datasArr);

			String sReturnInfo = "";
			String str = reqOTS.toString();// 将json对象转换为字符串						

			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******点货确定时订转销处理OrderToSaleProcessServiceImp传入参数：  " + str + "\r\n");

//			String reqUrl = "";
//			reqUrl=PosPub.getDCP_URL(eId);
			
			//执行请求操作，并拿到结果（同步阻塞）
			try 
			{
				//编码处理
				//str=URLEncoder.encode(str,"UTF-8");
				
				DispatchService ds = DispatchService.getInstance();
				String resbody = ds.callService(str, this.dao);
				
				//String resbody = HttpSend.Sendcom(str,reqUrl);
				
				JSONObject jsonres = new JSONObject(resbody);
				success = jsonres.getBoolean("success");
				errDesc=jsonres.get("serviceDescription").toString();
				
				if(success)
				{
					logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "订单出货-订转销处理, 转销售单成功" + "\r\n 订单号="+ ecOrderNo + "\r\n" + resbody+ "\r\n");
					//这里处理一下单据状态
					// 更新 订单状态 为 13 : 已点货
					UptBean ub1 = null;	
					ub1 = new UptBean("OC_ORDER");
					// Value
					ub1.addUpdateValue("STATUS", new DataValue(11, Types.VARCHAR));
					// condition	
					ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub1.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));

					this.addProcessData(new DataProcessBean(ub1));

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
					
					//接單日誌
					DataValue[] insValueOrderStatus_LOG = new DataValue[] 
							{ 
									new DataValue(eId, Types.VARCHAR),
									new DataValue(shopId, Types.VARCHAR), // 组织编号=门店编号
									new DataValue(shopId, Types.VARCHAR), // 映射后的门店
									new DataValue(ecOrderNo, Types.VARCHAR), //
									new DataValue(ecPlatformNo, Types.VARCHAR), //電商平台
									new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
									new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
									new DataValue("11", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
									new DataValue("订单出货-已完成", Types.VARCHAR), // 状态名称
									new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
									new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
									new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
									new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
									new DataValue("admin", Types.VARCHAR), //操作員編碼
									new DataValue("管理員", Types.VARCHAR), //操作員名稱
									new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
									new DataValue("訂單狀態-->订单出货-已完成", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
									new DataValue("100", Types.VARCHAR) 
							};
					InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
					ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
					this.addProcessData(new DataProcessBean(ibOrderStatusLog));
					
					this.doExecuteDataToDB();
					
				}
				else
				{
					logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "订单出货-订转销处理失败：" + "\r\n 订单号="+ ecOrderNo + "\r\n" + resbody + "\r\n");
				}


			}
			catch (Exception e) 
			{
				//
				sReturnInfo="错误信息:" + e.getMessage();

				logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"服务***订单出货-订转销处理 DCP_OrderECSale 返回参数 ：门店=" +shopId+ "公司编码=" +eId +" \n 单号="  + ecOrderNo  + e.toString());
			}

		}

		//
		if (success) 
		{
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");				
		}
		else 
		{
			res.setSuccess(true);
			res.setServiceStatus("100");
			res.setServiceDescription("订转销失败:<br/>" + errDesc);			
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECSaleReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECSaleReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECSaleReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECSaleReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECSaleReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECSaleReq>() {};
	}

	@Override
	protected DCP_OrderECSaleRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECSaleRes();
	}
	
	private String getOrderDetail(String eId, String shopId, String orderNo, String ecPlatformNo){
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" SELECT a.EID , a.SHOPID , a.order_Id  AS orderId , a.order_SN as orderSN , a.orderNo , a.sdate AS bdate , a.load_docType , "
				+ " a.shippingshop AS shippingshopNo , a.contman , a.conttel, a.address , a.memo , a.order_sn, a.isinvoice,  "
				+ " a.shipfee , a.tot_amt AS totAmt  ,  a.tot_qty  AS totQty , a.servicecharge , "
				+ " a.incomeamt , a.tot_disc AS totDisc , a.seller_disc AS sellerDisc , a.platform_disc AS platformDisc , a.sellno , a.deliverytype , "
				+ " a.paystatus , a.payamt , a.passport   ,a.freeCode , a.eccustomerno  ,"
				+ " b.item AS detailItem , b.pluNo ,b.plubarcode , b.price ,  b.qty ,  b.disc ,  b.amt ,  "
				+ " c.item AS agioItem , c.promname , c.agioamt ,  c.seller_disc AS agioSellerDisc , c.platform_disc AS agioPlatformDisc ,"
				+ " d.item AS payItem ,  d.paycode ,   d.paycodeerp , d.payname ,  d.isonlinepay ,  d.pay , d.ISORDERPAY, "
				+ " '1' AS invoiceItem ,  '0' as recordtype , '0' as taxationtype ,  '7' as invtype ,  '' as  invformat , "
				+ " '' as buyerguino , '0' as gftinvamt ,  '0' as gftinvtax ,  a.carriercode , a.carriershowid ,  a.carrierhiddenid ,"
				+ " a.lovecode ,  '' as randomcode ,  '' as printcount,  '' as rebateno , '' as invalidop, '' as sellerguiNO,  '' as invalidcode ,  '' as invmemo , '' as osdate,d.CanInvoice,d.INVOICENO DINVSTARTNO    "
				+ " FROM  OC_order a  "
				+ " LEFT JOIN OC_order_detail b ON a.EID = b.EID AND  a.SHOPID = b.SHOPID AND a.orderno = b.orderNo "
				+ " LEFT JOIN OC_Order_agio c  ON a.EID = c.EID AND a.SHOPID = b.SHOPID AND a.orderNo = c.orderNo "
				+ " LEFT JOIN OC_order_pay d  ON a.EID = d.EID AND a.SHOPID = d.SHOPID AND a.orderNo = d.orderNo "
				+ " WHERE a.EID = '"+eId+"' AND (a.SHOPID = '"+shopId+"' or a.shippingshop='"+shopId+"' )  AND a.orderno = '"+orderNo+"' "
				+ " and a.load_docType = '"+ecPlatformNo+"' "
				+ " ORDER BY pluNo ");

		sql = sqlbuf.toString();
		return sql;
	}
	
}
