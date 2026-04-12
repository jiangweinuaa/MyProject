package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.dsc.spos.json.cust.req.DCP_OrderECBatchSale_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderECBatchSalePickupCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECBatchSale_OpenRes;
import com.dsc.spos.json.cust.res.DCP_OrderECBatchSalePickupCreateRes;
import com.dsc.spos.scheduler.job.LgGetExpressNo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderECBatchSale_Open extends SPosAdvanceService<DCP_OrderECBatchSale_OpenReq,DCP_OrderECBatchSale_OpenRes>
{
	
	Logger logger = LogManager.getLogger(DCP_OrderECBatchSale_Open.class.getName());
	
	@Override
	protected void processDUID(DCP_OrderECBatchSale_OpenReq req, DCP_OrderECBatchSale_OpenRes res) throws Exception {
		// TODO Auto-generated method stub

		//		
		res.setSuccess(false);
		res.setServiceStatus("100");

		StringBuffer errMsg = new StringBuffer("");

		String[] ecOrderno=req.getEcOrderNo();
		String sOrdernoMulti="";

		for (int i = 0; i < ecOrderno.length; i++) 
		{
			sOrdernoMulti+=ecOrderno[i]+",";
		}

		if(sOrdernoMulti.length()>0)
		{

			String opNo = req.getOpNO();
			String eId = req.geteId();
			

			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String sDate = df.format(cal.getTime());
			df = new SimpleDateFormat("HHmmss");
			String sTime = df.format(cal.getTime());

			sOrdernoMulti=sOrdernoMulti.substring(0, sOrdernoMulti.length()-1);			
			//
			String[] arrPluno=new String[] {sOrdernoMulti};		

			String sql="select A.*, "
					+ "C.ITEM AS DETAIL_ITEM,C.PLUNO,C.PLUBARCODE,C.PLUNAME,C.SPECNAME,C.UNIT,C.PRICE,C.QTY,C.DISC,C.BOXNUM,C.BOXPRICE,C.AMT,C.ORDER_SN AS DETAIL_ORDER_SN,C.ECPLUNO,C.RQTY,C.RCQTY, "
					+ "E.ITEM AS AGIO_ITEM,PROMNAME,AGIOAMT,E.SELLER_DISC AS AGIO_SELLER_DISC ,E.PLATFORM_DISC AS AGIO_PLATFORM_DISC,"
					+ "F.ITEM AS PAY_ITEM,F.PAYCODE,F.PAYCODEERP,F.PAYNAME,F.CARDNO AS PAY_CARDNO,F.CTTYPE,F.PAYSERNUM,F.SERIALNO,F.REFNO,F.TERIMINALNO,F.DESCORE,F.PAY,F.CHANGED,F.ISORDERPAY,F.ORDER_PAYCODE,F.ISONLINEPAY,F.RCPAY,F.CanInvoice,F.INVOICENO DINVSTARTNO "
					+ "from OC_order A INNER JOIN ("
					+ PosPub.getFormatSourcePluno(arrPluno)
					+ ") B ON A.ORDERNO=B.PLUNO "
					+ "INNER JOIN OC_ORDER_DETAIL C ON A.EID=C.EID AND A.SHOPID=C.SHOPID AND A.ORDERNO=C.ORDERNO AND A.LOAD_DOCTYPE=C.LOAD_DOCTYPE "
					+ "LEFT JOIN OC_Order_agio E  ON A.EID = E.EID AND A.SHOPID = E.SHOPID AND A.orderNo = E.orderNo " 
					+ "LEFT JOIN OC_order_pay F  ON A.EID = F.EID AND A.SHOPID = F.SHOPID AND A.orderNo = F.orderNo "
					+ "where A.EID='"+req.geteId()+"' "
					//0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
					+ "and (a.status = '2' or a.status = '4' or a.status = '6' or a.status = '7') ";
					//+ "and A.DELIVERYTYPE<>'0' and A.DELIVERYTYPE<>'14' and A.DELIVERYTYPE is not null " ;

			List<Map<String, Object>> getQDataDetail= this.doQueryData(sql,null);

			if(getQDataDetail!=null && getQDataDetail.isEmpty()==false)
			{
//				String reqUrl = "";
//				reqUrl=PosPub.getDCP_URL(eId);

				//这里声明托运单号，  包裹号，尺寸，温层。  		
				String[] columnsShipment = {
						"EID","SHOPID","SHIPMENTNO","LGPLATFORMNO", "LGPLATFORMNAME",
						"STATUS","SDATE","STIME","ORDERER","ORDERER_PHONE",
						"RECEIVER","RECEIVER_MOBILE","RECEIVER_PHONE","RECEIVER_ADDRESS","RECEIVER_EMAIL",
						"MEMO","GETSHOP","GETSHOPNAME","TOT_BOXES","SHIPDATE","SHIPHOURTYPE",
						"EXPRESSNO","PACKAGENO","MEASURENO",
						"MEASURENAME","TEMPERATELAYERNO","TEMPERATELAYERNAME","TRANSFEE","BOXNO","ORIGINALTYPE","EC_ORDERNO",
						"PAYSTATUS","PAYAMT","ECPLATFORMNO","ECPLATFORMNAME","DELIVERYTYPE","COLLECTAMT",
						"SHOPEE_MODE","SHOPEE_ADDRESS_ID","SHOPEE_PICKUP_TIME_ID",
						"SHOPEE_BRANCH_ID","SHOPEE_SENDER_REAL_NAME",
						"EXPRESSBILLTYPE","RECEIVER_SITENO","SENDER_SITENO","DISTANCENO","DISTANCENAME",
						"RECEIVER_FIVECODE","RECEIVER_SEVENCODE","SHIP_ORDERPACKAGEID","STATUS"
				};

				String[] columnsShipmentOrigial = {
						"EID","SHOPID","SHIPMENTNO","LGPLATFORMNO", "LGPLATFORMNAME",
						"ITEM","PLUNO","PLUNAME","PLUBARCODE","SPECNAME","QTY",
						"AMT","EC_ORDERNO","ECPLATFORMNO","ECPLATFORMNAME","ORIGINALITEM","BOXNO","STATUS"
				};


				//订转销处理
				JSONObject reqOTS = new JSONObject();
				JSONArray datasArr = new JSONArray(); 

				//记录保存的发货单栏位
				List<Map<String, Object>> listLG=new ArrayList<Map<String, Object>>();

				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("ORDERNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);

				//处理未提交事物单号产生
				List<String> lstShipmentno=new ArrayList<String>();
				
				for(Map<String, Object> par: getQHeader)
				{
					String orderno=par.get("ORDERNO").toString();
					
					//青哥要orderId=订单号
					String orderId=orderno;
					//處理訂單以配送門店為歸屬門店					
					String shopId=par.get("SHOPID").toString();
					String shipshop=par.get("SHIPPINGSHOP").toString();

					String loadtype=par.get("LOAD_DOCTYPE").toString();					
					String sDeliverytype=par.get("DELIVERYTYPE").toString();
					//String sStatus=par.get("STATUS").toString();	
					String address=par.get("ADDRESS").toString();

					String lgPlatFormNo = ""; //物流平台代号（货运厂商）
					String lgPlatFormName = "";
					//0、无 1 自配送 2 顺丰 3百度 4达达 5 人人 6 闪送 7.7-11超商 8.全家超商 9.黑猫宅急便 10.莱而富超商 11.OK超商 12.mingjie大件物流 13.中华邮政 14.卖家宅配 15.新竹物流 16.绿界7-11超商 17.绿界全家超商 18.绿界莱而富超商 19.绿界OK超商
					if(sDeliverytype.equals("8") || sDeliverytype.equals("10") || sDeliverytype.equals("11"))
					{
						lgPlatFormNo = "cvs";
						lgPlatFormName = "便利达康";
					}
					else if(sDeliverytype.equals("7"))
					{
						lgPlatFormNo = "dzt";
						lgPlatFormName = "大智通";
					}
					else if(sDeliverytype.equals("9"))
					{
						lgPlatFormNo = "egs";
						lgPlatFormName = "黑猫宅急便";
					}
					else if(sDeliverytype.equals("12"))
					{
						lgPlatFormNo = "mingjie";
						lgPlatFormName = "mingjie大物流";
					}
					else if(sDeliverytype.equals("13"))
					{
						lgPlatFormNo = "chinapost";
						lgPlatFormName = "中华邮政";
					}
					else if(sDeliverytype.equals("15"))
					{
						lgPlatFormNo = "htc";
						lgPlatFormName = "新竹物流";
					}
					else if(sDeliverytype.equals("16")||sDeliverytype.equals("17") ||sDeliverytype.equals("18") ||sDeliverytype.equals("19"))
					{
						lgPlatFormNo = "greenworld";
						lgPlatFormName = "綠界";
					}
					else if(sDeliverytype.equals("1"))
					{
						lgPlatFormNo = "1";
						lgPlatFormName = "自配送";
					}
					

					String ecPlatformNo = ""; //电商平台代号
					String ecPlatformName = "";
					if(loadtype.equals("yahoosuper"))
					{
						ecPlatformNo = "yahoosuper";
						ecPlatformName = "yahoo超级商城";
					}
					else if(loadtype.equals("91app"))
					{
						ecPlatformNo = "91app";
						ecPlatformName = "91APP";
					}
					else if(loadtype.equals("shopee"))
					{
						ecPlatformNo = "shopee";
						ecPlatformName = "虾皮";
					}
					else if(loadtype.equals("letian"))
					{
						ecPlatformNo = "letian";
						ecPlatformName = "乐天";
					}
					else if(loadtype.equals("pchome"))
					{
						ecPlatformNo = "pchome";
						ecPlatformName = "PCHome购物";
					}
					else if(loadtype.equals("momo"))
					{
						ecPlatformNo = "momo";
						ecPlatformName = "MOMO购物";
					}
					else if(loadtype.equals("3"))
					{
						ecPlatformNo = "3";
						ecPlatformName = "微商城";
					}
					else 
					{
						ecPlatformNo = loadtype;
						ecPlatformName = "其他平台";
					}

					//批量点货默认都是常温普通处理
					String expressNo = par.get("DELIVERYNO").toString();

					String packageNo = par.get("PACKAGENO").toString();
					String measureNo = par.get("MEASURENO").toString();
					if (measureNo.equals("")) 
					{
						measureNo="0001";//0001:60cm
					}
					String measureName = par.get("MEASURENAME").toString();
					if (measureName.equals("")) 
					{
						measureName="60cm";//0001:60cm
					}

					String temperatelayerNo= par.get("TEMPERATELAYERNO").toString();
					if (temperatelayerNo.equals("")) 
					{
						temperatelayerNo="1";//1.常溫 2.冷藏 3.冷凍
					}

					String temperatelayerName = par.get("TEMPERATELAYERNAME").toString();
					if (temperatelayerName.equals("")) 
					{
						temperatelayerName="常溫";//1.常溫 2.冷藏 3.冷凍
					}

					String transFee = par.get("TOTSHIPFEE").toString();
					String boxNo = par.get("BOXNO").toString();		
					if (PosPub.isNumericType(boxNo)==false) 
					{
						boxNo="1";
					}
					String expressBilltype=par.get("EXPRESSBILLTYPE").toString();
					if (expressBilltype.equals("")) 
					{
						expressBilltype="A";
					}
					String receiverSiteno=par.get("RECEIVER_SITENO").toString();
					String senderSiteno=par.get("SENDER_SITENO").toString();
					String distanceNo=par.get("DISTANCENO").toString();
					String distanceName=par.get("DISTANCENAME").toString();
					String receiverFivecode=par.get("RECEIVER_FIVECODE").toString();
					String receiverSevencode=par.get("RECEIVER_SEVENCODE").toString();
					String payAmt = par.get("PAYAMT").toString();
					String sTOT_AMT=par.get("TOT_AMT").toString();
					String sTOT_QTY=par.get("TOT_QTY").toString();
					String serviceCharge=par.get("SERVICECHARGE").toString();
					String inComeAmt=par.get("INCOMEAMT").toString();
					if (PosPub.isNumericType(inComeAmt)==false) 
					{
						inComeAmt=sTOT_AMT;
					}
					String sTOT_DISC=par.get("TOT_DISC").toString();
					String sSELLER_DISC=par.get("SELLER_DISC").toString();
					String sPLATFORM_DISC=par.get("PLATFORM_DISC").toString();
					String sellNo=par.get("SELLNO").toString();
					String sPASSPORT=par.get("PASSPORT").toString();
					String sFREECODE=par.get("FREECODE").toString();
					String sECCUSTOMERNO=par.get("ECCUSTOMERNO").toString();

					String payStatus = par.get("PAYSTATUS").toString();//1.未支付 2.部分支付 3.付清

					String collectAmt="0";
					if (payStatus.equals("3")) 
					{
						collectAmt="0";
					}
					else 
					{						

						if (PosPub.isNumericType(sTOT_AMT)==false) 
						{
							sTOT_AMT="0";
						}
						if (PosPub.isNumericType(payAmt)==false) 
						{
							payAmt="0";
						}

						BigDecimal b1=new BigDecimal(sTOT_AMT);
						BigDecimal b2=new BigDecimal(payAmt);

						BigDecimal result2 = b1.subtract(b2);
						//
						collectAmt=result2.toString();
					}


					String shipDate = par.get("SHIPDATE").toString(); //指定日期 
					String shipHourType = par.get("SHIPHOURTYPE").toString(); //配送时段类型，1:9~12 時 2:12~17 時 3:17~20 時 4:不指定时段
					String orderer = par.get("CONTMAN").toString();
					String orderPhone = par.get("CONTTEL").toString();
					String orderSN = par.get("ORDER_SN").toString();
					String isInvoice = par.get("ISINVOICE").toString();
					if (isInvoice.equals("Y")==false) 
					{
						isInvoice="N";
					}
					String receiver = par.get("GETMAN").toString();
					String receiverMobile = par.get("GETMANTEL").toString();
					String receiverPhone = par.get("GETMANTEL").toString();
					String receiverAddress = par.get("ADDRESS").toString();
					String receiverEmail = par.get("GETMANEMAIL").toString();
					String memo = par.get("MEMO").toString();
					String getShop = par.get("SHIPPINGSHOP").toString(); //取货门店
					String getShopName = par.get("SHIPPINGSHOPNAME").toString();
					String totBoxes = par.get("TOT_BOXES").toString(); //总箱数

					String shopeeMode = par.get("SHOPEE_MODE").toString();
					String shopeeAddressId = par.get("SHOPEE_ADDRESS_ID").toString();
					String shopeePickuptimeId = par.get("SHOPEE_PICKUP_TIME_ID").toString();
					String shopeeBranchId = par.get("SHOPEE_BRANCH_ID").toString();
					String shopeeSenderRealName = par.get("SHOPEE_SENDER_REAL_NAME").toString();
					//樂天用到
					String orderPackageId = par.get("SHIP_ORDERPACKAGEID").toString();

					

					if (sTOT_AMT.equals("")) 
					{
						res.setSuccess(false);
						res.setServiceStatus("100");
						res.setServiceDescription("订单总额不能为空！");	
						this.pData.clear();
						return;
					}

					//订转销带入明细		
					
					JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）

					JSONArray detailArr = new JSONArray(); 					
					JSONArray agioArr = new JSONArray(); 
					JSONArray payArr = new JSONArray(); 
					JSONArray invArr = new JSONArray(); 					

					//避免明细重复
					List<String> lstOrderDetailItem=new ArrayList<String>();

					//避免折扣重复
					List<String> lstOrderAgioItem=new ArrayList<String>();

					//避免付款重复
					List<String> lstOrderPayItem=new ArrayList<String>();

					int bi=0;
					for (Map<String, Object> oneData2 : getQDataDetail) 
					{						
						//过滤属于此单头的明细
						if(orderno.equals(oneData2.get("ORDERNO")) && lstOrderDetailItem.contains(orderno+"_"+oneData2.get("DETAIL_ITEM").toString())==false)
						{							
							bi+=1;//

							String oItem = oneData2.get("DETAIL_ITEM").toString();
							lstOrderDetailItem.add(orderno+"_"+oItem);

							String pluNo = oneData2.get("PLUNO").toString();
							String pluName = oneData2.get("PLUNAME").toString();
							String qty = oneData2.get("QTY").toString();
							String barcode = oneData2.get("PLUBARCODE").toString();
							String amt = oneData2.get("AMT").toString();
							String specName = oneData2.get("SPECNAME").toString();

							DataValue[] insValueShipmentOriginal =new DataValue[]
									{
											new DataValue(eId, Types.VARCHAR), 
											new DataValue(shopId, Types.VARCHAR), 
											new DataValue("", Types.VARCHAR), 
											new DataValue(lgPlatFormNo, Types.VARCHAR), 
											new DataValue(lgPlatFormName, Types.VARCHAR),
											new DataValue(bi, Types.INTEGER), //ITEM
											new DataValue(pluNo, Types.VARCHAR), 
											new DataValue(pluName, Types.VARCHAR), 
											new DataValue(barcode, Types.VARCHAR), 
											new DataValue(specName, Types.VARCHAR), 
											new DataValue(qty, Types.DOUBLE),
											new DataValue(amt, Types.DOUBLE),
											new DataValue(orderno, Types.VARCHAR),
											new DataValue(ecPlatformNo, Types.VARCHAR), 
											new DataValue(ecPlatformName, Types.VARCHAR), 
											new DataValue(bi, Types.INTEGER), 
											new DataValue(1, Types.INTEGER), //箱号
											new DataValue("100", Types.VARCHAR), 
									};

							InsBean ib2 = new InsBean("OC_SHIPMENT_ORIGINAL", columnsShipmentOrigial);
							ib2.addValues(insValueShipmentOriginal);
							this.addProcessData(new DataProcessBean(ib2));


							//订转销带入							
							String price = oneData2.get("PRICE").toString();							
							String disc = oneData2.get("DISC").toString();
							String isGift = "N"; //是否赠品
							String isPackage = "N"; //是否套餐商品
							String dealType = "1"; //商品交易类型 1.销售 2.退货 3.赠品 11.免单
							String giftReason = ""; //赠品原因
							String couponInvTax = "0"; 

							//商品明细
							JSONObject plu_detail = new JSONObject();
							plu_detail.put("item", oItem);
							plu_detail.put("pluNO", pluNo);
							plu_detail.put("pluBarcode", barcode);
							plu_detail.put("price", price);
							plu_detail.put("qty", qty);
							plu_detail.put("disc", disc);
							plu_detail.put("amt", amt);
							plu_detail.put("isGift", isGift);
							plu_detail.put("isPackage", isPackage);
							plu_detail.put("dealType", dealType);
							plu_detail.put("giftReason", giftReason);
							plu_detail.put("invItem", 1);//发票项次（就是要开几张发票，每张发票对应的商品明细）
							plu_detail.put("couponInvTax", couponInvTax);//礼券已开发票税额

							detailArr.put(plu_detail);

							if(orderno.equals(oneData2.get("ORDERNO")) && oneData2.get("AGIO_ITEM").toString().equals("")==false && lstOrderAgioItem.contains(orderno+"_"+oneData2.get("AGIO_ITEM").toString())==false )
							{
								//订单折扣
								String agioItem=oneData2.get("AGIO_ITEM").toString();	
								lstOrderAgioItem.add(orderno+"_"+agioItem);

								String promName=oneData2.get("PROMNAME").toString();	
								String agioAmt=oneData2.get("AGIOAMT").toString();	
								String agioSellerDisc=oneData2.get("AGIO_SELLER_DISC").toString();	
								String agioPlatformDisc=oneData2.get("AGIO_PLATFORM_DISC").toString();

								JSONObject agio_detail = new JSONObject();
								agio_detail.put("item", agioItem);
								agio_detail.put("promName", promName);
								agio_detail.put("agioAmt", agioAmt);
								agio_detail.put("sellerDisc", agioSellerDisc);
								agio_detail.put("platformDisc", agioPlatformDisc);

								agioArr.put(agio_detail);
							}

							if(orderno.equals(oneData2.get("ORDERNO")) && oneData2.get("PAY_ITEM").toString().equals("")==false && lstOrderPayItem.contains(orderno+"_"+oneData2.get("PAY_ITEM").toString())==false )
							{								
								//订单付款档
								String payItem=oneData2.get("PAY_ITEM").toString();	
								lstOrderPayItem.add(orderno+"_"+payItem);

								String payCode=oneData2.get("PAYCODE").toString();	
								String payCodeErp=oneData2.get("PAYCODEERP").toString();	
								String payName=oneData2.get("PAYNAME").toString();	
								String isOnlinePay=oneData2.get("ISONLINEPAY").toString();
								String pay=oneData2.get("PAY").toString();

								JSONObject pay_detail = new JSONObject();
								pay_detail.put("item", payItem);
								pay_detail.put("payCode", payCode);
								pay_detail.put("payCodeErp", payCodeErp);
								pay_detail.put("payName", payName);
								pay_detail.put("isOnlinePay", isOnlinePay);
								pay_detail.put("canInvoice", oneData2.get("CANINVOICE").toString());
								pay_detail.put("invstartNo", oneData2.get("DINVSTARTNO").toString());
								
								pay_detail.put("pay", pay);
								payArr.put(pay_detail);

							}							



						}						
					}			

					//
					if (isInvoice.equals("Y")) 
					{
						//发票档							
						String invoiceItem = "1";//项次
						String recordtype = "0";//注：0 开立，1 作废，2 折让单
						String buyerguino = par.get("BUYERGUINO").toString();//买家统一编号
						String gftinvamt = "0";//礼券已开发票金额
						String gftinvtax = "0";//礼券已开发票税额
						String carriercode = par.get("CARRIERCODE").toString();//载具类别编码
						String carriershowid = par.get("CARRIERSHOWID").toString();//载具显码
						String carrierhiddenid = par.get("CARRIERHIDDENID").toString();//载具隐码
						String lovecode = par.get("LOVECODE").toString();//爱心码
						String rebateno = "";//折让单单号
						String invalidOp = "";//作废人员
						String invalidcode = "";//作废理由码
						String invmemo = par.get("INVMEMO").toString();//发票内容
						String osdate = "";//原单日期

						JSONObject inv_detail = new JSONObject();
						inv_detail.put("item", invoiceItem);
						inv_detail.put("recordType", recordtype);
						inv_detail.put("buyerGuiNO", buyerguino);
						inv_detail.put("gftInvAmt", gftinvamt);
						inv_detail.put("gftInvTax", gftinvtax);
						inv_detail.put("carrierCode", carriercode);
						inv_detail.put("carrierShowID", carriershowid);
						inv_detail.put("carrierHiddenID", carrierhiddenid);
						inv_detail.put("loveCode", lovecode);
						inv_detail.put("rebateNO", rebateno);
						// 大爷规格上新增参数。invalidOp 作废人员
						inv_detail.put("invalidOp", invalidOp);
						inv_detail.put("invalidCode", invalidcode);
						inv_detail.put("invMemo", invmemo);
						inv_detail.put("osDate", osdate);

						invArr.put(inv_detail);	
					}



					//
					DataValue[] insValueShipment = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR),
							new DataValue("", Types.VARCHAR),
							new DataValue(lgPlatFormNo, Types.VARCHAR),
							new DataValue(lgPlatFormName, Types.VARCHAR),
							new DataValue("1", Types.VARCHAR),//STATUS 1:新建 2：已发货 3：退货 4：换货 5：已安排物流取件 6：已完成
							new DataValue(sDate, Types.VARCHAR),
							new DataValue(sTime, Types.VARCHAR),

							new DataValue(orderer, Types.VARCHAR), 
							new DataValue(orderPhone, Types.VARCHAR),
							new DataValue(receiver, Types.VARCHAR),
							new DataValue(receiverMobile, Types.VARCHAR),
							new DataValue(receiverPhone, Types.VARCHAR),
							new DataValue(receiverAddress, Types.VARCHAR),
							new DataValue(receiverEmail, Types.VARCHAR),
							new DataValue(memo, Types.VARCHAR),
							new DataValue(getShop, Types.VARCHAR),
							new DataValue(getShopName, Types.VARCHAR),
							new DataValue(totBoxes, Types.VARCHAR),
							new DataValue(shipDate, Types.VARCHAR),
							new DataValue(shipHourType, Types.VARCHAR),

							new DataValue(expressNo, Types.VARCHAR),
							new DataValue(packageNo, Types.VARCHAR),
							new DataValue(measureNo, Types.VARCHAR),
							new DataValue(measureName, Types.VARCHAR),
							new DataValue(temperatelayerNo, Types.VARCHAR),
							new DataValue(temperatelayerName, Types.VARCHAR),
							new DataValue(transFee, Types.VARCHAR),
							new DataValue(boxNo, Types.VARCHAR),
							new DataValue("1", Types.VARCHAR),//ORIGINALTYPE 1:订单 2：销售单
							new DataValue(orderno, Types.VARCHAR),
							new DataValue(payStatus, Types.VARCHAR),
							new DataValue(payAmt, Types.VARCHAR),
							new DataValue(ecPlatformNo, Types.VARCHAR),
							new DataValue(ecPlatformName, Types.VARCHAR),
							new DataValue(sDeliverytype, Types.VARCHAR),
							new DataValue(collectAmt, Types.VARCHAR),

							new DataValue(shopeeMode, Types.VARCHAR),
							new DataValue(shopeeAddressId, Types.VARCHAR),
							new DataValue(shopeePickuptimeId, Types.VARCHAR),
							new DataValue(shopeeBranchId, Types.VARCHAR),
							new DataValue(shopeeSenderRealName, Types.VARCHAR),

							new DataValue(expressBilltype, Types.VARCHAR),
							new DataValue(receiverSiteno, Types.VARCHAR),
							new DataValue(senderSiteno, Types.VARCHAR),
							new DataValue(distanceNo, Types.VARCHAR),
							new DataValue(distanceName, Types.VARCHAR),
							new DataValue(receiverFivecode, Types.VARCHAR),
							new DataValue(receiverSevencode, Types.VARCHAR),
							new DataValue(orderPackageId, Types.VARCHAR),
							new DataValue("100", Types.VARCHAR),

					};
					InsBean ib1 = new InsBean("OC_SHIPMENT", columnsShipment);
					ib1.addValues(insValueShipment);
					//this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
					
					
					// 更新 订单状态 为 13 : 已点货
					UptBean ub1 = null;	
					ub1 = new UptBean("OC_ORDER");
					// Value
					ub1.addUpdateValue("STATUS", new DataValue(11, Types.VARCHAR));
					// condition	
					ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub1.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(ub1));


					//托运单为空才记录
					if (expressNo.equals("")) 
					{
						if (ecPlatformNo.equals("shopee")) 
						{
							//过滤掉虾皮集成的物流厂商
							if (sDeliverytype .equals("") || sDeliverytype.equals("0") || sDeliverytype.equals("14")) 
							{
								Map<String, Object> lgMap=new HashMap<String, Object>();
								lgMap.put("eId", eId);
								lgMap.put("shopId", shopId);
								lgMap.put("organizationno", shopId);
								lgMap.put("shipmentno", "");
								lgMap.put("lgplatformno", lgPlatFormNo);

								listLG.add(lgMap);
							}
						}	
						else 
						{
							Map<String, Object> lgMap=new HashMap<String, Object>();
							lgMap.put("eId", eId);
							lgMap.put("shopId", shopId);
							lgMap.put("organizationno", shopId);
							lgMap.put("shipmentno", "");
							lgMap.put("lgplatformno", lgPlatFormNo);

							listLG.add(lgMap);
						}
					}


					//***************调用订转销服务OrderToSaleProcess*************
					String token = req.getToken();

					//重新写一个订转销售的服务，大爷的订转销需要token
					reqOTS.put("serviceId","DCP_OrderToSaleProcess_Open");
					reqOTS.put("docType","1");
					reqOTS.put("token",token);

					header.put("oEId", eId);
					header.put("oShopId", shipshop);
					header.put("bookShopNO", shopId);
					header.put("orderID", orderId);
					header.put("orderNO", orderno);
					// 单据日期不用传， 直接取系统日期即可
					header.put("o_opNO", opNo);
					header.put("type", "0");// 单据类型： 0 销售单， 1退货
					header.put("loadDocType", ecPlatformNo);
					header.put("shippingShopNO", getShop);
					header.put("contMan", orderer);
					header.put("contTel", orderPhone);
					header.put("address", address);
					header.put("memo", memo);
					header.put("orderSN", orderSN); 
					header.put("isInvoice", isInvoice);
					header.put("shipFee", transFee);
					header.put("totAmt", sTOT_AMT);
					header.put("totQty", sTOT_QTY);
					header.put("serviceCharge", serviceCharge);
					header.put("inComeAmt", inComeAmt);//实收金额 
					header.put("totDisc", sTOT_DISC);   

					header.put("sellerDisc", sSELLER_DISC); // 商户优惠总额
					header.put("platformDisc", sPLATFORM_DISC); // 平台优惠总额
					header.put("sellNO", sellNo); //订单大客户编号（赊销客户）
					header.put("deliveryType", sDeliverytype); 
					header.put("payStatus", payStatus); 
					header.put("payamt", payAmt); 
					header.put("passport", sPASSPORT); 
					header.put("freeCode", sFREECODE); 
					header.put("ecCustomerNO", sECCUSTOMERNO); 

					//
					header.put("detail", detailArr);
					header.put("agio", agioArr);
					header.put("pay", payArr);
					header.put("invoiceDetail", invArr);
					datasArr.put(header);
					reqOTS.put("datas", datasArr);
				}	

				//先调订转销的服务
				//所有订单一次性订转销
				String sReturnInfo = "";
				String str = reqOTS.toString();// 将json对象转换为字符串						

				logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******点货确定时订转销处理OrderToSaleProcessServiceImp传入参数：  " + str + "\r\n");

				//执行请求操作
				try 
				{
					//编码处理
					//str=URLEncoder.encode(str,"UTF-8");
					//String resbody = HttpSend.Sendcom(str,reqUrl);
					DispatchService ds = DispatchService.getInstance();
					String resbody = ds.callService(str, this.dao);		
					
					PosPub.WriteETLJOBLog("點貨訂轉銷返回： "+ resbody);
					
					JSONObject jsonres = new JSONObject(resbody);
					boolean success = jsonres.getBoolean("success");
					
					if(success)
					{
						logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "点货确定时 订转销处理, 转销售单成功，订单号="+sOrdernoMulti);
					}
					else
					{
						logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "点货确定时 订转销处理失败：" + "\r\n" + resbody + "\r\n");

						//订转销失败
						sReturnInfo="点货失败， 订转销处理失败："+jsonres.getString("serviceDescription");
					}
				}
				catch (Exception e) 
				{
					//
					sReturnInfo="点货失败， 请求订转销处理前异常错误，错误信息:" + e.getMessage();

					logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"服务***点货确定时，请求订转销处理前异常错误 OrderECSalePickupCreateDCP 返回参数 ：订单号=" +sOrdernoMulti+ "公司编码=" +eId+"\r\n"  + e.toString());
				}

				if (sReturnInfo.equals("")) 
				{
					//
					res.setSuccess(true);
					res.setServiceStatus("000");
				}
				else 
				{
					//订转销失败，SQL记录清空，点货都不会成功了
					this.pData.clear();
					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription(sReturnInfo);
					errMsg.append(sReturnInfo+"\r\n");
					return;					
				}
				
				
				//一次性执行
				this.doExecuteDataToDB();				

				//调用JOB处理托运单必须单据已保存，不然JOB中SQL无法查询到
				//********************************************
				//如果托运单号为空，调用API接口申请
				for (int pi = 0; pi < listLG.size(); pi++) 
				{
					String sEId=listLG.get(pi).get("eId").toString();
					String sshop=listLG.get(pi).get("shopId").toString();
					String sorganizationno=listLG.get(pi).get("organizationno").toString();
					String sshipmentno=listLG.get(pi).get("shipmentno").toString();
					String slgplatformno=listLG.get(pi).get("lgplatformno").toString();				

					LgGetExpressNo lg=new LgGetExpressNo(sEId,sshop,sorganizationno,sshipmentno,slgplatformno); 
					String sRet= lg.doExe();

				}							
			}
			else 
			{
				errMsg.append("請先進行點貨作業取物流單號\r\n");
			}
			getQDataDetail=null;
		}
		else 
		{
			errMsg.append("订单号不能为空，\r\n");
		}

		res.setServiceDescription(errMsg.toString());
		
	}
	

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECBatchSale_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECBatchSale_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECBatchSale_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECBatchSale_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECBatchSale_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECBatchSale_OpenReq>() {};
	}

	@Override
	protected DCP_OrderECBatchSale_OpenRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECBatchSale_OpenRes();
	}

}
