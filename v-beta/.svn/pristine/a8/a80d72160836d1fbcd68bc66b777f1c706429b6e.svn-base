package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderShipping_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderToSaleProcess2_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderToSaleProcess_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderShipping_OpenRes;
import com.dsc.spos.json.cust.res.DCP_OrderToSaleProcess_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.BigDecimalUtils;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
public class DCP_OrderShipping_Open extends SPosAdvanceService<DCP_OrderShipping_OpenReq,DCP_OrderShipping_OpenRes>
{

	@Override
	public void processDUID(DCP_OrderShipping_OpenReq req, DCP_OrderShipping_OpenRes res) throws Exception
	{
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String sDate=df.format(cal.getTime());

		String eId = req.getRequest().geteId();
		if (eId==null||eId.trim().isEmpty())
        {
            eId = req.geteId();//取接口账号
        }
		if (eId==null||eId.isEmpty())
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取eId失败！");
		}
		String opType = req.getRequest().getOpType(); // 1:发货 2:安排取件 
		String[] orderList=req.getRequest().getOrderList();
		String bDate = req.getRequest().getBdate();
        String opShopId= req.getRequest().getOpShopId();

        String machineNo= req.getRequest().getMachineNo();
        String squadNo= req.getRequest().getSquadNo();
        String workNo= req.getRequest().getWorkNo();
        String opNo= req.getRequest().getOpNo();
        String opName= req.getRequest().getOpName();
        String plantType = req.getPlantType();

		for (String orderno : orderList)
		{
			RedisPosPub rpp = new RedisPosPub();
			String checkKey="";
			checkKey="DCP_OrderShipping_Open:"+orderno;
			try{	
				if(!rpp.IsExistStringKey(checkKey))
				{
					SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String createDate = df1.format(cal.getTime());
					rpp.setEx(checkKey,300,createDate);
				}else
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "订单:"+orderno+"正在处理中...请等候");
				}
				//订单SQL
				String sqlOrder="select * from dcp_order a where a.eid='"+eId+"' and a.orderno='"+orderno+"' ";
				List<Map<String, Object>> getData_Order=this.doQueryData(sqlOrder, null);

				String loaddoctype="";
				String channelId="";
				String deliveryType="";
				String deliveryStatus="";
				String deliveryNo="";
				String detailType="";
				String headorderno="";
				String status="";
				String shopId="";
				String shippingNo="";
				String machshopNo="";
				String refundStatus="";//退单状态
				String payStatus ="";//支付状态

				if (getData_Order!=null && getData_Order.isEmpty()==false)
				{
					loaddoctype=getData_Order.get(0).get("LOADDOCTYPE").toString();
					channelId=getData_Order.get(0).get("CHANNELID").toString();
					deliveryType=getData_Order.get(0).get("DELIVERYTYPE").toString();
					deliveryStatus=getData_Order.get(0).get("DELIVERYSTATUS").toString();
					deliveryNo=getData_Order.get(0).get("DELIVERYNO").toString();
					detailType=getData_Order.get(0).get("DETAILTYPE").toString();
					headorderno=getData_Order.get(0).get("HEADORDERNO").toString();
					status=getData_Order.get(0).get("STATUS").toString();
					shopId=getData_Order.get(0).get("SHOP").toString();
					shippingNo=getData_Order.get(0).get("SHIPPINGSHOP").toString();
					machshopNo=getData_Order.get(0).get("MACHSHOP").toString();
					refundStatus=getData_Order.get(0).get("REFUNDSTATUS").toString();
					payStatus=getData_Order.get(0).get("PAYSTATUS").toString();

					//前端不传，默认取下订门店
					if (Check.Null(opShopId))
					{
						//opShopId=getData_Order.get(0).get("SHOP").toString();
                        opShopId=shippingNo;
					}
					else
					{
						if ("1".equals(opType))
						{
							if (!Check.Null(shippingNo)&&!opShopId.equals(shippingNo))
							{
								res.setSuccess(false);
								res.setServiceStatus("100");
								res.setServiceDescription("订单号："+orderno+",取货门店非本门店，请到取货门店("+shippingNo+")操作订转销");
								return;
							}
						}
					}
				}
				else
				{
					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription("找不到订单号："+orderno);
					return;
				}

				//0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
				if (status.equals("0") ||status.equals("3") ||status.equals("10") ||status.equals("12"))
				{
					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription("订单号："+orderno + "，单据状态不允许此操作！");
					return;
				}

				List<DataProcessBean> data = new ArrayList<DataProcessBean>();

				if (opType.equals("1"))// 1:发货 
				{
					boolean isSetOrderToSaleLock = false;
					//判断下有没有，处理取消活退单的锁
					if (loaddoctype.equals(orderLoadDocType.ELEME)||loaddoctype.equals(orderLoadDocType.MEITUAN)||loaddoctype.equals(orderLoadDocType.JDDJ)||loaddoctype.equals(orderLoadDocType.MTSG)||loaddoctype.equals(orderLoadDocType.DYWM))
					{
						boolean isExistRefundLock = HelpTools.IsExistWaiMaiOrderToSaleOrRefundRedisLock("1",eId,orderno);
						if (isExistRefundLock)
						{
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("订单号："+orderno+"，服务端正在处理取消或退单中...");
							return;
						}
					}
					if (!"3".equals(payStatus))
					{
						res.setSuccess(false);
						res.setServiceStatus("100");
						res.setServiceDescription("订单号："+orderno+"，支付状态是部分支付(有尾款)不能订转销！");
						return;
					}
					/*String apiUrl="";
				//渠道SQL
				String sqlEcommerce="select * from dcp_ecommerce a where a.eid='"+eId+"' and a.loaddoctype='"+loaddoctype+"' and a.channelid='"+channelId+"' ";
				List<Map<String, Object>> getData_Ecommerce=this.doQueryData(sqlEcommerce, null);
				if (getData_Ecommerce!=null && getData_Ecommerce.isEmpty()==false)
				{
					apiUrl=getData_Ecommerce.get(0).get("APIURL").toString();
				}
				else
				{
					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription("找不到订单号："+orderno+"对应<br/>dcp_ecommerce表的loaddoctype=" +loaddoctype +",channelid=" +channelId+"的资料！");
					return;
				}*/
					//是否降级订单Y-是N-否
					String downgraded = getData_Order.get(0).getOrDefault("DOWNGRADED","N").toString();
					if ("Y".equals(downgraded))
					{
						res.setSuccess(false);
						res.setServiceStatus("100");
						res.setServiceDescription("订单号："+orderno + "是降级订单暂不能订转销，请稍后再试！");
						return;
					}

					if (loaddoctype.equals(orderLoadDocType.ELEME)||loaddoctype.equals(orderLoadDocType.MEITUAN)||loaddoctype.equals(orderLoadDocType.JDDJ)||loaddoctype.equals(orderLoadDocType.MTSG))
					{
						HelpTools.setWaiMaiOrderToSaleOrRefundRedisLock("0",eId,orderno);
						isSetOrderToSaleLock = true;
					}
					//订转销

					JSONObject request_ship = new JSONObject();
					JSONArray datas_goodsList = new JSONArray();

					JSONArray datas_pay = new JSONArray();
					JSONObject datas_invoiceDetail = new JSONObject();
					//datas_invoiceDetail.put("invNo", "");

					request_ship.put("eId", eId);

					//这几个op开头的可以通过加参数传入，暂时先这样吧
					request_ship.put("opShopId", opShopId);//
					request_ship.put("opMachineNo", Check.Null(machineNo)?getData_Order.get(0).get("MACHINE").toString():machineNo);
					request_ship.put("opSquadNo", Check.Null(squadNo)?getData_Order.get(0).get("SQUADNO").toString():squadNo);
					request_ship.put("opWorkNo", Check.Null(workNo)?getData_Order.get(0).get("WORKNO").toString():workNo);
					request_ship.put("opOpNo", Check.Null(opNo)?getData_Order.get(0).get("OPNO").toString():opNo);
					request_ship.put("opOpName", Check.Null(opName)?"":opName);

					request_ship.put("orderNo", orderno);
					request_ship.put("shopNo", shopId);
					request_ship.put("belfirm", getData_Order.get(0).get("BELFIRM").toString());
					request_ship.put("loadDocType", loaddoctype);
					request_ship.put("channelId", channelId);
					request_ship.put("shippingShopNo", getData_Order.get(0).get("SHIPPINGSHOP").toString());
					request_ship.put("shippingShopName", getData_Order.get(0).get("SHIPPINGSHOPNAME").toString());
					//+转换成%20
					request_ship.put("contMan", getData_Order.get(0).get("CONTMAN").toString());
					//+转换成%20
					request_ship.put("contTel", getData_Order.get(0).get("CONTTEL").toString());
					//+转换成%20
					request_ship.put("address", getData_Order.get(0).get("ADDRESS").toString());
					request_ship.put("shipDate", getData_Order.get(0).get("SHIPDATE").toString());
					request_ship.put("shipStartTime", getData_Order.get(0).get("SHIPSTARTTIME").toString());
					request_ship.put("shipEndTime", getData_Order.get(0).get("SHIPENDTIME").toString());
					request_ship.put("shipType", getData_Order.get(0).get("SHIPTYPE").toString());
					//+转换成%20
					request_ship.put("memo", getData_Order.get(0).get("MEMO").toString());
					request_ship.put("delName", getData_Order.get(0).get("DELNAME").toString());
					request_ship.put("delTelephone", getData_Order.get(0).get("DELTELEPHONE").toString());
					request_ship.put("deliveryType", getData_Order.get(0).get("DELIVERYTYPE").toString());
					request_ship.put("deliveryNo", getData_Order.get(0).get("DELIVERYNO").toString());
					request_ship.put("deliveryStatus", getData_Order.get(0).get("DELIVERYSTATUS").toString());
					request_ship.put("subDeliveryCompanyNo", getData_Order.get(0).get("SUBDELIVERYCOMPANYNO").toString());
					request_ship.put("subDeliveryCompanyName", getData_Order.get(0).get("SUBDELIVERYCOMPANYNAME").toString());
					request_ship.put("deliveryHandinput", getData_Order.get(0).get("DELIVERYHANDINPUT").toString());

					request_ship.put("sn", getData_Order.get(0).get("ORDER_SN").toString());
					request_ship.put("packageFee", getData_Order.get(0).get("PACKAGEFEE").toString());
					request_ship.put("shipFee", getData_Order.get(0).get("SHIPFEE").toString());
					request_ship.put("deliveryFeeShop", getData_Order.get(0).get("SHOPSHARESHIPFEE").toString());
					request_ship.put("tot_oldAmt", getData_Order.get(0).get("TOT_OLDAMT").toString());
					request_ship.put("tot_Amt", getData_Order.get(0).get("TOT_AMT").toString());
					request_ship.put("serviceCharge", getData_Order.get(0).get("SERVICECHARGE").toString());
					request_ship.put("rshipFee", getData_Order.get(0).get("RSHIPFEE").toString());
					request_ship.put("tot_shipFee", getData_Order.get(0).get("TOTSHIPFEE").toString());
					request_ship.put("incomeAmt", getData_Order.get(0).get("INCOMEAMT").toString());
					request_ship.put("totDisc", getData_Order.get(0).get("TOT_DISC").toString());
					request_ship.put("saleDisc", getData_Order.get(0).get("TOT_DISC").toString());
					request_ship.put("sellerDisc", getData_Order.get(0).get("SELLER_DISC").toString());
					request_ship.put("platformDisc", getData_Order.get(0).get("PLATFORM_DISC").toString());
					request_ship.put("payAmt", getData_Order.get(0).get("PAYAMT").toString());
					request_ship.put("totQty", getData_Order.get(0).get("TOT_QTY").toString());
					request_ship.put("isBook", getData_Order.get(0).get("ISBOOK").toString());
					request_ship.put("sTime", getData_Order.get(0).get("STIME").toString());
					request_ship.put("mealNumber", getData_Order.get(0).get("MEALNUMBER").toString());
					request_ship.put("manualNo", getData_Order.get(0).get("MANUALNO").toString());
					request_ship.put("getMan", getData_Order.get(0).get("GETMAN").toString());
					request_ship.put("getManTel", getData_Order.get(0).get("GETMANTEL").toString());
					request_ship.put("cardNo", getData_Order.get(0).get("CARDNO").toString());
					request_ship.put("memberId", getData_Order.get(0).get("MEMBERID").toString());
					request_ship.put("memberName", getData_Order.get(0).get("MEMBERNAME").toString());
					request_ship.put("pointQty", getData_Order.get(0).get("POINTQTY").toString());
					request_ship.put("sellNo", getData_Order.get(0).get("SELLNO").toString());
					//+转换成%20
					request_ship.put("delMemo", getData_Order.get(0).get("DELMEMO").toString());
					request_ship.put("detailType", detailType);
					request_ship.put("headOrderNo", headorderno);
					request_ship.put("machineNo", getData_Order.get(0).get("MACHINE").toString());
					request_ship.put("eraseAmt", getData_Order.get(0).get("ERASE_AMT").toString());


					//中台没有营业日期，只有库存入账日期
					//取入账日期-销售单
					String accountDate = PosPub.getAccountDate_SMS(dao, eId, shippingNo);
					if (Check.Null(bDate))
					{
						request_ship.put("bDate", accountDate);
						request_ship.put("opBDate", accountDate);
					}
					else
					{
						//不能小于入账日期
						if (bDate.compareTo(accountDate)<0)
						{
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("营业日期bdate("+bDate+")不能小于库存入账日期("+accountDate+")！");
							if (isSetOrderToSaleLock)
							{
								HelpTools.clearWaiMaiOrderToSaleOrRefundRedisLock("0",eId,orderno);
							}
							return;
						}
						request_ship.put("bDate", bDate);
						request_ship.put("opBDate", bDate);
					}

					request_ship.put("sellCredit", getData_Order.get(0).get("SELLCREDIT").toString());
					request_ship.put("refundReason", getData_Order.get(0).get("REFUNDREASON").toString());
					request_ship.put("refundReasonNo", getData_Order.get(0).get("REFUNDREASONNO").toString());
					request_ship.put("refundReasonName", getData_Order.get(0).get("REFUNDREASONNAME").toString());
					request_ship.put("verNum", getData_Order.get(0).get("VER_NUM").toString());
					request_ship.put("squadNo", getData_Order.get(0).get("SQUADNO").toString());
					request_ship.put("workNo", getData_Order.get(0).get("WORKNO").toString());
					request_ship.put("opNo", getData_Order.get(0).get("OPNO").toString());
					request_ship.put("customer", getData_Order.get(0).get("CUSTOMER").toString());
					request_ship.put("customerName", getData_Order.get(0).get("CUSTOMERNAME").toString());
					request_ship.put("memberPayNo", getData_Order.get(0).get("MEMBERPAYNO").toString());
					request_ship.put("freeCode", getData_Order.get(0).get("FREECODE").toString());
					request_ship.put("passport", getData_Order.get(0).get("PASSPORT").toString());
					request_ship.put("invMemo", getData_Order.get(0).get("INVMEMO").toString());
					request_ship.put("carrierCode", getData_Order.get(0).get("CARRIERCODE").toString());
					request_ship.put("loveCode", getData_Order.get(0).get("LOVECODE").toString());
					request_ship.put("buyerGuiNo", getData_Order.get(0).get("BUYERGUINO").toString());
					request_ship.put("isInvoice", getData_Order.get(0).get("ISINVOICE").toString());
					request_ship.put("invoiceType", getData_Order.get(0).get("INVOICETYPE").toString());
					request_ship.put("carrierShowId", getData_Order.get(0).get("CARRIERSHOWID").toString());
					request_ship.put("carrierHiddenId", getData_Order.get(0).get("CARRIERHIDDENID").toString());

					request_ship.put("tot_Amt_merReceive", getData_Order.get(0).get("TOT_AMT_MERRECEIVE").toString());
					request_ship.put("tot_Amt_custPayReal", getData_Order.get(0).get("TOT_AMT_CUSTPAYREAL").toString());
					request_ship.put("totDisc_merReceive", getData_Order.get(0).get("TOT_DISC_MERRECEIVE").toString());
					request_ship.put("totDisc_custPayReal", getData_Order.get(0).get("TOT_DISC_CUSTPAYREAL").toString());

					request_ship.put("langType", req.getLangType());
					request_ship.put("waimaiMerreceiveMode", getData_Order.get(0).get("WAIMAIMERRECEIVEMODE").toString());
					request_ship.put("partnerMember", getData_Order.get(0).get("PARTNERMEMBER").toString());

					//订单明细SQL
					String sqlOrderdetail="select * from dcp_order_detail where eid='"+eId+"' and orderno='"+orderno+"' ";
					List<Map<String, Object>> getData_Orderdetail=this.doQueryData(sqlOrderdetail, null);

					//订单折扣SQL
					String sqlOrderdetail_agio="select * from DCP_ORDER_DETAIL_AGIO where eid='"+eId+"' and orderno='"+orderno+"' ";
					List<Map<String, Object>> getData_Orderdetail_agio=this.doQueryData(sqlOrderdetail_agio, null);
					String isOrderToSaleAll="0";
					if (getData_Orderdetail!=null && getData_Orderdetail.isEmpty()==false)
					{
						for (Map<String, Object> map_detail : getData_Orderdetail)
						{
							BigDecimal bdm_qty=new BigDecimal(Check.Null(map_detail.get("QTY").toString())?"0":map_detail.get("QTY").toString());
							BigDecimal bdm_pickqty=new BigDecimal(Check.Null(map_detail.get("PICKQTY").toString())?"0":map_detail.get("PICKQTY").toString());
							BigDecimal bdm_rqty=new BigDecimal(Check.Null(map_detail.get("RQTY").toString())?"0":map_detail.get("RQTY").toString());
							BigDecimal bdm_amt=new BigDecimal(Check.Null(map_detail.get("AMT").toString())?"0":map_detail.get("AMT").toString());
							BigDecimal bdm_writeoffamt=new BigDecimal(Check.Null(map_detail.get("WRITEOFFAMT").toString())?"0":map_detail.get("WRITEOFFAMT").toString());
							BigDecimal bdm_disc=new BigDecimal(Check.Null(map_detail.get("DISC").toString())?"":map_detail.get("DISC").toString());
							BigDecimal bdm_disc_sale=new BigDecimal(Check.Null(map_detail.get("DISC_SALE").toString())?"0":map_detail.get("DISC_SALE").toString());//折扣已冲销
							BigDecimal bdm_oldamt=new BigDecimal(Check.Null(map_detail.get("OLDAMT").toString())?"0":map_detail.get("OLDAMT").toString());//原金额
							BigDecimal bdm_oldamt_sale=new BigDecimal(Check.Null(map_detail.get("OLDAMT_SALE").toString())?"0": map_detail.get("OLDAMT_SALE").toString());//原金额已冲销
							if(!BigDecimalUtils.equal(bdm_pickqty.doubleValue(), 0))
							{
								isOrderToSaleAll="1";
							}
							//退的扣除
							if (bdm_qty.subtract(bdm_pickqty).subtract(bdm_rqty).compareTo(BigDecimal.ZERO)>0)
							{
								JSONObject request_detail = new JSONObject();
								request_detail.put("item", map_detail.get("ITEM").toString());
								//+转换成%20
								request_detail.put("pluBarcode", map_detail.get("PLUBARCODE").toString());
								//+转换成%20
								request_detail.put("pluNo", map_detail.get("PLUNO").toString());
								//+转换成%20
								request_detail.put("pluName", map_detail.get("PLUNAME").toString());
								request_detail.put("featureNo", map_detail.get("FEATURENO").toString());
								//+转换成%20
								request_detail.put("featureName", map_detail.get("FEATURENAME").toString());
								request_detail.put("warehouse", map_detail.get("WAREHOUSE").toString());
								//+转换成%20
								request_detail.put("warehouseName", map_detail.get("WAREHOUSENAME").toString());
								request_detail.put("sUnit", map_detail.get("SUNIT").toString());
								//+转换成%20
								request_detail.put("sUnitName", map_detail.get("SUNITNAME").toString());
								//+转换成%20
								request_detail.put("specName", map_detail.get("SPECNAME").toString());
								//+转换成%20
								request_detail.put("attrName", map_detail.get("ATTRNAME").toString());
								request_detail.put("price", map_detail.get("PRICE").toString());
								request_detail.put("qty", bdm_qty.subtract(bdm_pickqty).subtract(bdm_rqty));
								request_detail.put("goodsGroup", map_detail.get("GOODSGROUP").toString());
								//request_detail.put("disc", map_detail.get("DISC").toString());
								request_detail.put("disc", bdm_disc.subtract(bdm_disc_sale));
								request_detail.put("boxNum", map_detail.get("BOXNUM").toString());
								request_detail.put("boxPrice", map_detail.get("BOXPRICE").toString());
								//request_detail.put("amt", map_detail.get("AMT").toString());
								request_detail.put("amt",bdm_amt.subtract(bdm_writeoffamt));
								request_detail.put("packageType", map_detail.get("PACKAGETYPE").toString());
								request_detail.put("packageMitem", map_detail.get("PACKAGEMITEM").toString());
								request_detail.put("toppingType", map_detail.get("TOPPINGTYPE").toString());
								request_detail.put("toppingMitem", map_detail.get("TOPPINGMITEM").toString());
								request_detail.put("couponType", map_detail.get("COUPONTYPE").toString());
								request_detail.put("couponCode", map_detail.get("COUPONCODE").toString());
								request_detail.put("gift", map_detail.get("GIFT").toString());
								request_detail.put("giftSourceSerialNo", map_detail.get("GIFTSOURCESERIALNO").toString());
								request_detail.put("goodsUrl", map_detail.get("GOODSURL").toString());
								request_detail.put("sellerNo", map_detail.get("SELLERNO").toString());
								//+转换成%20
								request_detail.put("sellerName", map_detail.get("SELLERNAME").toString());
								request_detail.put("accNo", map_detail.get("ACCNO").toString());
								request_detail.put("counterNo", map_detail.get("COUNTERNO").toString());
								request_detail.put("oldPrice", map_detail.get("OLDPRICE").toString());
								//request_detail.put("oldAmt", map_detail.get("OLDAMT").toString());
								request_detail.put("oldAmt",bdm_oldamt.subtract(bdm_oldamt_sale));
								//+转换成%20
								request_detail.put("giftReason", map_detail.get("GIFTREASON").toString());
								request_detail.put("sTime", map_detail.get("STIME").toString());
								request_detail.put("oItem", map_detail.get("OITEM").toString());
								request_detail.put("oReItem", map_detail.get("OREITEM").toString());
								request_detail.put("taxCode", map_detail.get("TAXCODE").toString());
								request_detail.put("taxType", map_detail.get("TAXTYPE").toString());
								//数字类型
								request_detail.put("taxRate", PosPub.isNumericType(map_detail.get("TAXRATE").toString())==false?"0":map_detail.get("TAXRATE").toString());
								request_detail.put("inclTax", map_detail.get("INCLTAX").toString());
								request_detail.put("invSplitType", map_detail.get("INVSPLITTYPE").toString());
								request_detail.put("invNo", map_detail.get("INVNO").toString());
								request_detail.put("invItem", map_detail.get("INVITEM").toString());
								request_detail.put("virtual", map_detail.get("VIRTUAL").toString());

								request_detail.put("disc_merReceive", map_detail.get("DISC_MERRECEIVE").toString());
								request_detail.put("disc_custPayReal", map_detail.get("DISC_CUSTPAYREAL").toString());
								request_detail.put("amt_merReceive", map_detail.get("AMT_MERRECEIVE").toString());
								request_detail.put("amt_custPayReal", map_detail.get("AMT_CUSTPAYREAL").toString());
                                request_detail.put("flavorStuffDetail", map_detail.getOrDefault("FLAVORSTUFFDETAIL","").toString());

								//过滤商品折扣
								Map<String, Object> condv=new HashMap<String, Object>();
								condv.put("MITEM", map_detail.get("ITEM").toString());
								List<Map<String, Object>> tempListAgio=MapDistinct.getWhereMap(getData_Orderdetail_agio, condv, true);

								JSONArray datas_agio = new JSONArray();

								for (Map<String, Object> map_detail_agio : tempListAgio)
								{
									BigDecimal bdm_agio_qty=new BigDecimal(Check.Null(map_detail_agio.get("QTY").toString())?"0":map_detail_agio.get("QTY").toString());
									BigDecimal bdm_agio_qty_sale=new BigDecimal(Check.Null(map_detail_agio.get("QTY_SALE").toString())?"0":map_detail_agio.get("QTY_SALE").toString());
									BigDecimal bdm_agio_amt=new BigDecimal(Check.Null(map_detail_agio.get("AMT").toString())?"0":map_detail_agio.get("AMT").toString());
									BigDecimal bdm_agio_amt_sale=new BigDecimal(Check.Null(map_detail_agio.get("AMT_SALE").toString())?"0":map_detail_agio.get("AMT_SALE").toString());
									BigDecimal bdm_agio_disc=new BigDecimal(Check.Null(map_detail_agio.get("DISC").toString())?"0":map_detail_agio.get("DISC").toString());
									BigDecimal bdm_agio_disc_sale=new BigDecimal(Check.Null(map_detail_agio.get("DISC_SALE").toString())?"0":map_detail_agio.get("DISC_SALE").toString());
									JSONObject request_detail_agio = new JSONObject();
									request_detail_agio.put("item", map_detail_agio.get("ITEM").toString());
									//request_detail_agio.put("qty", map_detail_agio.get("QTY").toString());
									request_detail_agio.put("qty",bdm_agio_qty.subtract(bdm_agio_qty_sale));
									//request_detail_agio.put("amt", map_detail_agio.get("AMT").toString());
									request_detail_agio.put("amt",bdm_agio_amt.subtract(bdm_agio_amt_sale));
									request_detail_agio.put("inputDisc", map_detail_agio.get("INPUTDISC").toString());
									request_detail_agio.put("realDisc", map_detail_agio.get("REALDISC").toString());
									//request_detail_agio.put("disc", map_detail_agio.get("DISC").toString());
									request_detail_agio.put("disc", bdm_agio_disc.subtract(bdm_agio_disc_sale));
									request_detail_agio.put("dcType", map_detail_agio.get("DCTYPE").toString());
									request_detail_agio.put("dcTypeName", map_detail_agio.get("DCTYPENAME").toString());
									request_detail_agio.put("pmtNo", map_detail_agio.get("PMTNO").toString());
									request_detail_agio.put("giftCtf", map_detail_agio.get("GIFTCTF").toString());
									request_detail_agio.put("giftCtfNo", map_detail_agio.get("GIFTCTFNO").toString());
									request_detail_agio.put("bsNo", map_detail_agio.get("BSNO").toString());
									request_detail_agio.put("disc_merReceive", map_detail_agio.get("DISC_MERRECEIVE").toString());
									request_detail_agio.put("disc_custPayReal", map_detail_agio.get("DISC_CUSTPAYREAL").toString());

									datas_agio.put(request_detail_agio);
								}
								request_detail.put("agioInfo", datas_agio);
								datas_goodsList.put(request_detail);
							}
						}
						request_ship.put("invoiceDetail", datas_invoiceDetail);
						request_ship.put("goodsList", datas_goodsList);
						request_ship.put("pay", datas_pay);
						request_ship.put("isOrderToSaleAll",isOrderToSaleAll);
						String str_ship=request_ship.toString();

						String resbody_ship="";

						//部分退单后，订转销
						if (refundStatus.equals("10"))
						{
							//1.深拷贝一份源服务请求
							ParseJson pj = new ParseJson();
							//先处理基类字段
							String jsonReq=pj.beanToJson(req);
							DCP_OrderToSaleProcess2_OpenReq dcp_OrderToSaleProcess2_OpenReq=pj.jsonToBean(jsonReq, new TypeToken<DCP_OrderToSaleProcess2_OpenReq>(){});
							//再处理request节点
							DCP_OrderToSaleProcess2_OpenReq.levelRequest request=pj.jsonToBean(str_ship, new TypeToken<DCP_OrderToSaleProcess2_OpenReq.levelRequest>(){});
							//有过部分退商品，清空商品，不传
							request.getGoodsList().clear();
							dcp_OrderToSaleProcess2_OpenReq.setRequest(request);
							if (plantType!=null)
							{
								dcp_OrderToSaleProcess2_OpenReq.setPlantType(plantType);
							}
							//2.目标服务部分字段需重新给值，也已经组好了
							//3.调用目标服务
							DCP_OrderToSaleProcess2_Open dcp_OrderToSaleProcess2_Open=new DCP_OrderToSaleProcess2_Open();
							DCP_OrderToSaleProcess_OpenRes dcp_OrderToSaleProcess_OpenRes=new DCP_OrderToSaleProcess_OpenRes();
							dcp_OrderToSaleProcess2_Open.setDao(this.dao);
							dcp_OrderToSaleProcess2_Open.processDUID(dcp_OrderToSaleProcess2_OpenReq,dcp_OrderToSaleProcess_OpenRes);
							//4.处理服务返回
							resbody_ship=pj.beanToJson(dcp_OrderToSaleProcess_OpenRes);
							pj=null;
						}
						else
						{
							//1.深拷贝一份源服务请求
							ParseJson pj = new ParseJson();
							//先处理基类字段
							String jsonReq=pj.beanToJson(req);
							DCP_OrderToSaleProcess_OpenReq dcp_OrderToSaleProcess_OpenReq=pj.jsonToBean(jsonReq,new TypeToken<DCP_OrderToSaleProcess_OpenReq>(){});
							//再处理request节点
							DCP_OrderToSaleProcess_OpenReq.levelRequest request=pj.jsonToBean(str_ship, new TypeToken<DCP_OrderToSaleProcess_OpenReq.levelRequest>(){});
							dcp_OrderToSaleProcess_OpenReq.setRequest(request);
							if (plantType!=null)
							{
								dcp_OrderToSaleProcess_OpenReq.setPlantType(plantType);
							}
							//2.目标服务部分字段需重新给值，也已经组好了
							//3.调用目标服务
							DCP_OrderToSaleProcess_Open dcp_OrderToSaleProcess_Open=new DCP_OrderToSaleProcess_Open();
							DCP_OrderToSaleProcess_OpenRes dcp_OrderToSaleProcess_OpenRes=new DCP_OrderToSaleProcess_OpenRes();
							dcp_OrderToSaleProcess_Open.setDao(this.dao);
							dcp_OrderToSaleProcess_Open.processDUID(dcp_OrderToSaleProcess_OpenReq,dcp_OrderToSaleProcess_OpenRes);
							//4.处理服务返回
							resbody_ship=pj.beanToJson(dcp_OrderToSaleProcess_OpenRes);
							pj=null;
						}
						if (isSetOrderToSaleLock)
						{
							HelpTools.clearWaiMaiOrderToSaleOrRefundRedisLock("0",eId,orderno);
						}

						//返回值
						if (resbody_ship.equals("")==false)
						{
							JSONObject jsonres_ship = new JSONObject(resbody_ship);
							boolean success = jsonres_ship.getBoolean("success");
							if (success)
							{
								//订转销里面有处理
								/*
							//更新订单主表DCP_ORDER
							UptBean ub_DCP_ORDER = new UptBean("DCP_ORDER");
							//更新值
							ub_DCP_ORDER.addUpdateValue("STATUS", new DataValue("10", Types.VARCHAR));

							//更新条件
							ub_DCP_ORDER.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							ub_DCP_ORDER.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));
							data.add(new DataProcessBean(ub_DCP_ORDER));

							//最后执行SQL
							dao.useTransactionProcessData(data);	

								 */
							}
							else
							{
								res.setSuccess(false);
								res.setServiceStatus("100");
								res.setServiceDescription("订单号："+orderno+"出货订转销失败返回：" +jsonres_ship.getString("serviceDescription"));
								return;
							}
						}
						else
						{
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("订单号："+orderno+"出货订转销失败返回为空：");
							return;
						}
					}
					else
					{
						res.setSuccess(false);
						res.setServiceStatus("100");
						res.setServiceDescription("找不到订单号："+orderno+"对应的资料！");
						return;
					}
				}
				else //***********2:安排取件 ******************************************************************************************
				{
					if (status.equals("10") && deliveryStatus.equals("1")==false)//已发货的才能安排取件
					{
						//更新订单主表DCP_ORDER
						UptBean ub_DCP_ORDER = new UptBean("DCP_ORDER");
						//更新值
						ub_DCP_ORDER.addUpdateValue("DELIVERYSTATUS", new DataValue("1", Types.VARCHAR));

						//更新条件
						ub_DCP_ORDER.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						ub_DCP_ORDER.addCondition("ORDERNO", new DataValue(orderno, Types.VARCHAR));
						ub_DCP_ORDER.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
						ub_DCP_ORDER.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
						data.add(new DataProcessBean(ub_DCP_ORDER));

						//写订单日志
						String LogStatus="1";
						orderStatusLog oslog=new orderStatusLog();
						oslog.setCallback_status("N");
						oslog.setChannelId(channelId);
						oslog.setDisplay("0");
						oslog.seteId(eId);
						oslog.setLoadDocBillType(loaddoctype);
						oslog.setLoadDocOrderNo(orderno);
						oslog.setLoadDocType(loaddoctype);
						oslog.setMachShopName(machshopNo);
						oslog.setMachShopNo(machshopNo);
						oslog.setMemo("");
						oslog.setNeed_callback("N");
						oslog.setNeed_notify("N");
						oslog.setNotify_status("N");
						oslog.setOpName(req.getOpName());
						oslog.setOpNo(req.getOpNO());
						oslog.setOrderNo(orderno);
						oslog.setShippingShopName(shippingNo);
						oslog.setShippingShopNo(shippingNo);
						oslog.setShopName(shopId);
						oslog.setShopNo(shopId);
						oslog.setStatus(LogStatus);
						//
						String statusType="2";
						StringBuilder statusTypeName=new StringBuilder();
						String statusName=HelpTools.GetOrderStatusName(statusType, LogStatus, statusTypeName);
						oslog.setStatusName(statusName);
						oslog.setStatusType(statusType);
						oslog.setStatusTypeName(statusTypeName.toString());
						oslog.setUpdate_time(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
						InsBean	ib_DCP_ORDER_STATUSLOG=HelpTools.InsertOrderStatusLog(oslog);
						data.add(new DataProcessBean(ib_DCP_ORDER_STATUSLOG));


						//最后执行SQL
						dao.useTransactionProcessData(data);

					}
					else
					{
						res.setSuccess(false);
						res.setServiceStatus("100");
						res.setServiceDescription("当前订单状态不允许此操作！");
						return;
					}
				}
			}
			finally
			{
                rpp.DeleteKey(checkKey);
			}
		}

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderShipping_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderShipping_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderShipping_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderShipping_OpenReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		/*if (Check.Null(req.getRequest().geteId()))
		{
			isFail = true;
			errMsg.append("企业编码eId不能为空 ");
		}*/

		if (Check.Null(req.getRequest().getOpType()))
		{
			isFail = true;
			errMsg.append("操作类型opType不能为空 ");
		}

		String[] orderList=req.getRequest().getOrderList();

		if (orderList==null || orderList.length==0)
		{
			isFail = true;
			errMsg.append("订单列表orderList不能为空 ");
		}

		for (String orderno : orderList)
		{
			if (Check.Null(orderno))
			{
				isFail = true;
				errMsg.append("订单号不能为空 ");
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderShipping_OpenReq> getRequestType()
	{
		return new TypeToken<DCP_OrderShipping_OpenReq>(){};
	}

	@Override
	protected DCP_OrderShipping_OpenRes getResponseType()
	{
		return new DCP_OrderShipping_OpenRes();
	}




}
