package com.dsc.spos.waimai.jddj;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.dsc.spos.json.cust.res.DCP_OrderQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderQueryRes.level1Elm;
import com.dsc.spos.waimai.HelpTools;

public class HelpTransferOrder 
{/*
	public static String jddjLogFileName = "jddjJoblog";
	public static DCP_OrderQueryRes.level1Elm CoverOrderEntity(OrderInfoDTO order,StringBuilder errorMessage) throws Exception
	{
		if(errorMessage == null)
		{
			errorMessage = new StringBuilder();
		}
		if (order == null)
		{
			errorMessage.append("JDDJ需转化的实体类为空！");
			return null;
		}
		try 
		{
			DCP_OrderQueryRes.level1Elm tvOrder = new DCP_OrderQueryRes().new level1Elm();
			
			String eId = "99";
			String customerNO = " ";
			String shopId =" ";
			String shopno_poi = order.getDeliveryStationNoIsv();//商家门店编码 99_1001 99-1001
			if(shopno_poi!=null)
			{
				shopno_poi = shopno_poi.replace("-", "_");//京东到家商家中心控制了 外部门店编号不能使用下划线 2020-01-06
			}
			 try 
			  {
				 int indexofSpec =  shopno_poi.indexOf("_");
				 if (indexofSpec > 0)
				 {
					 eId = shopno_poi.substring(0,indexofSpec);
					 shopId = shopno_poi.substring(indexofSpec+1);
				 }
				 else
				 {
					 shopId = shopno_poi;
				 }
					
			
		    } 
				catch (Exception e) 
				{
					 if (shopno_poi==null ||  shopno_poi.equals("") || shopno_poi.length() == 0)
					 {
						 shopId = " ";
					 }
					 else
					 {
						 shopId = shopno_poi;
					 }				 			 			 
				}
			String shopName = order.getDeliveryStationName();//配送门店名称
			String orderNO = Long.toString(order.getOrderId());
			String loadDocType = "8";//1.饿了么 2.美团外卖 3.微商城 4.门店（云pos）5.ERP 8.京东到家
			int orderStatus = order.getOrderStatus();//订单状态（20010:锁定，20020:订单取消，20030:订单取消申请，20040:超时未支付系统取消，20060:系统撤销订单，20050:暂停，31000:等待付款，31020:已付款，41000:待处理，32000:等待出库，33040:配送中，33060:已妥投，34000:京东已收款，90000:订单完成）
			String status = "2";//订单状态1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单
			String refundStatus = "1";//订单退单状态说明：1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功
			switch (orderStatus) {
			case 41000:				
				status = "1";// 41000:待处理，   未接单;新建状态 
				break;
			case 32000:
				status = "2";// 32000:等待出库，   已接单;
				break;
			case 20020:
				status = "12";// 20020:订单取消， 已拒单/取消;
				refundStatus = "6";
				break;
			case 20030:
				status = "2";
				refundStatus = "2";// 20030:订单取消申请， 用户申请退单（已接单的才需要申请）
				break;
			case 20040:
				status = "12";
				refundStatus = "6";// 20040:超时未支付系统取消，   已拒单/取消;
				break;
			case 20060:
				status = "12";
				refundStatus = "6";// 20060:系统撤销订单， 已拒单/取消;
				break;
			case 33060:
				status = "11";// 33060:已妥投，已完成；
				break;
			case 34000:
				status = "11";// 33060:已妥投，已完成；
				break;
			case 90000:
				status = "11";// 90000:订单完成，  已完成;
				break;
	
			default:
			break;
			}
			
			tvOrder.setStatus(status);
			tvOrder.setRefundStatus(refundStatus);
			tvOrder.setPlatformStatus(Integer.toString(orderStatus));
			
			tvOrder.seteId(eId);
			tvOrder.setCustomerNO(customerNO);
			tvOrder.setOrganizationNO(shopId);
			tvOrder.setShopId(shopId);
			tvOrder.setShopName(shopName);
			tvOrder.setOrderNO(orderNO);
			tvOrder.setShippingShopNO(shopId);
			tvOrder.setShippingShopName(shopName);
			tvOrder.setMachShopNO(shopId);
			tvOrder.setMachShopName(shopName);
			tvOrder.setLoadDocType(loadDocType);
			tvOrder.setContMan(order.getBuyerFullName());//收货人名称
			tvOrder.setContTel(order.getBuyerMobile());//收货人电话,
			//order.getBuyerMobile();//收货人手机号
			tvOrder.setAddress(order.getBuyerFullAddress());//收货人地址
			tvOrder.setLATITUDE(Double.toString(order.getBuyerLat()));
			tvOrder.setLONGITUDE(Double.toString(order.getBuyerLng()));
			
			String createDatetime = "";//下单时间
			String shipDate = "";//配送日期
			String shipTime = "";//配送时间		
			Date orderStartTime = order.getOrderStartTime();//下单时间
			Date orderPreStartDeliveryTime = order.getOrderPreStartDeliveryTime();//预计送达开始时间
			Date orderPreEndDeliveryTime = order.getOrderPreEndDeliveryTime();//预计送达结束时间
			try 
			{
				createDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(orderStartTime);
				shipDate = new SimpleDateFormat("yyyyMMdd").format(orderPreStartDeliveryTime); 
				shipTime = new SimpleDateFormat("HHmmss").format(orderPreStartDeliveryTime);
		
		  } 
			catch (Exception e) 
			{
		
		  }
			tvOrder.setCreateDatetime(createDatetime);
			tvOrder.setShipDate(shipDate);
			tvOrder.setShipTime(shipTime);
			String shipType = "1";//1.外卖平台配送 2.商户配送 3.顾客自提 4.顺丰配送
			String deliveryCarrierNo = order.getDeliveryCarrierNo();//承运商编号(9966:京东众包;2938:商家自送;1130:达达同城送;9999:到店自提)
      if(deliveryCarrierNo.equals("9999"))
      {
      	shipType = "3";
      }
      else if (deliveryCarrierNo.equals("2938")) 
      {
      	shipType = "2";			
		  }
      tvOrder.setShipType(shipType);
      tvOrder.setIsShipcompany("N");//是否总部配送 
      tvOrder.setMemo(order.getOrderBuyerRemark());
			tvOrder.setSn(Integer.toString(order.getOrderNum()));//当天门店订单序号
			//int orderInvoiceOpenMark = order.getOrderInvoiceOpenMark();//2 订单开发票标识 
			String isInvoice = "N";//是否开发票
			String invoiceTypeStr = "0";// 1.个人 2.企业
			String invoiceTitle = "";
			String InvoiceDutyNo = "";
						
			if (order.getOrderInvoice() != null)//发票信息
			{
				isInvoice = "Y";
				OrderInvoiceDTO InvoiceDTO = order.getOrderInvoice();
			  int	invoiceType = InvoiceDTO.getInvoiceType();//发票抬头类型(0：个人、1：企业普票、2：企业专票)		 
				if (invoiceType != 0)
			  {
					invoiceTypeStr = "2";
			  }
				invoiceTitle = InvoiceDTO.getInvoiceTitle();
				InvoiceDutyNo = InvoiceDTO.getInvoiceDutyNo();
										
			}
			
			tvOrder.setIsInvoice(isInvoice);
			tvOrder.setInvoiceType(invoiceTypeStr);//1.个人 2.企业
			tvOrder.setInvoiceTitle(invoiceTitle);//发票抬头
			tvOrder.setTaxRegnumber(InvoiceDutyNo);//发票税号	
			
			tvOrder.setGetMan(order.getOrdererName());//收货人
			tvOrder.setGetMantel(order.getOrdererMobile());//
			
			long canteenMoney = 0;// 餐盒费合计
			long foodDiscMoney = 0;//单品总折扣金额
			long jddjDiscMoney = 0;//单品京东到家平台折扣金额(补贴给商家)
			long shopDiscMoney = 0;//单品商家折扣金额
			//region 单身处理
			tvOrder.setGoods(new ArrayList<DCP_OrderQueryRes.level2Elm>());
			int goodsItem = 0;
			for (OrderProductDTO item : order.getProduct()) 
			{
				try 
				{
					goodsItem++;
					DCP_OrderQueryRes.level2Elm oneLv2 = new DCP_OrderQueryRes(). new level2Elm();
					
					long foodPrice = item.getSkuJdPrice();//实际售价
					int foodCount = item.getSkuCount();//数量
					long foodAmt = foodPrice * foodCount;//金额
					
					long foodOldPrice = item.getSkuStorePrice();//原价
					
					long foodOldAMT = foodOldPrice * foodCount;//原价金额
					
					long foodDiscAMT = foodOldAMT - foodAmt;//总折扣金额
					long foodPingtaiAMT = 0;//单品平台承担金额
					long foodshangjiaAMT = 0;//单品商家承担金额
					
					
					long foodCostPrice = item.getSkuCostPrice();//成本价;当成本价小于等于促销价时，是商家促销，平台给商家补贴为0
					long foodPingtaiPrice = 0;//平台承担的折扣价格
					
					if (foodCostPrice > foodPrice)//当成本价大于促销价时，是平台促销，则平台承担金额
					{
						foodPingtaiPrice = foodCostPrice - foodPrice;
					}
					
					foodPingtaiAMT = foodPingtaiPrice * foodCount;//单品平台承担金额
					foodshangjiaAMT = foodDiscAMT - foodPingtaiAMT;//单品商家承担金额
					
					foodDiscMoney += foodDiscAMT;//单品总折扣金额
					jddjDiscMoney += foodPingtaiAMT;//单品平台承担金额合计
					shopDiscMoney += foodshangjiaAMT;//单品商家折扣金额合计
					
					
					long skuId = item.getSkuId();//到家商品编码
					String pluNO = item.getSkuIdIsv();//商家商品编码
					String skuName = item.getSkuName();//商品的名称
					String spec = item.getSkuCostumeProperty() == null ? "" : item.getSkuCostumeProperty();//商品规格，
									
					long foodPackeFee = item.getCanteenMoney();//总的餐盒费
					canteenMoney += foodPackeFee;
					
					int boxNum = foodCount;//餐盒数量
					long boxPrice = 0;//餐盒价格
					try 
					{
						boxPrice = foodPackeFee/boxNum;
			
			    } 
					catch (Exception e) 
					{
						boxPrice = 0;			
			    }
					
					double price =(double) foodPrice/100;
					double amt =(double) foodAmt/100;
					double box_Price =(double) boxPrice/100;
					
					oneLv2.setItem(Integer.toString(goodsItem));
					oneLv2.setPluNO(pluNO);
					oneLv2.setPluBarcode(pluNO);
					oneLv2.setSkuID(Long.toString(skuId));
					oneLv2.setPluName(skuName);
					oneLv2.setGoodsGroup("");
					oneLv2.setSpecName(spec);
					oneLv2.setAttrName("");
					oneLv2.setUnit("");
					oneLv2.setPrice(Double.toString(price));
					oneLv2.setQty(Double.toString(foodCount));
					oneLv2.setAmt(Double.toString(amt));
					oneLv2.setBoxNum(Double.toString(boxNum));
					oneLv2.setBoxPrice(Double.toString(box_Price));
					oneLv2.setDisc("0");
					oneLv2.setIsMemo("N");
					
					tvOrder.getGoods().add(oneLv2);
			
					oneLv2=null;
		    } 
				catch (Exception e) 
				{
					continue;		
		    }
		
		  }
			
			//endregion
			
			到家的包装费可按门店进行设置，商家无法设置，需要找到家运营进行设置； 
			餐盒费是按商品维度设置的，只有商品到家类目为美食、外卖的商品才可设置餐盒费； 
			若门店设置了包装费，且商品同时设置了餐盒费，则用户下单时，会叠加收取包装费和餐盒费，一个订单，只收取一次包装费，餐盒费按照商品sku数收取。
			
			long packagingMoney = order.getPackagingMoney() + canteenMoney;//包装费 +餐盒费(单位：分)
			double packageFee = (double)packagingMoney/100;
			
			double jddjPackagingMoney = (double)order.getPackagingMoney()/100;//京东到家的包装费，不算商家收入
			double canteenMoney_d =(double)canteenMoney/100;
			
			HelpTools.writelog_fileName("【JDDJ的订单】到家的包装费:"+jddjPackagingMoney+" 商家餐盒费："+canteenMoney_d +"合计："+packageFee, jddjLogFileName);
			tvOrder.setPackageFee(Double.toString(packageFee));//包装费+餐盒费
			long orderTotalMoney = order.getOrderTotalMoney();//订单商品销售价总金额，等于sum（京东到家销售价skuJdPrice*商品下单数量skuCount）
			long orderDiscountMoney = order.getOrderDiscountMoney();//订单级别优惠商品金额：(不含单品促销类优惠金额及运费相关优惠金额)，等于OrderDiscountlist表中，除优惠类型7，8，12外的优惠金额discountPrice累加和
			long orderFreightMoney = order.getOrderFreightMoney();//用户支付的实际订单运费：订单应收运费（orderReceivableFreight）-运费优惠（OrderDiscountlist表中，优惠类型7，8，12，15的优惠金额。运费优惠大于应收运费时，实际支付为0
			long localDeliveryMoney = order.getLocalDeliveryMoney();//达达同城送运费(单位：分)
			long merchantPaymentDistanceFreightMoney = order.getMerchantPaymentDistanceFreightMoney();//商家支付远距离运费(单位：分)。达达配送默认只能服务半径2公里内的用户，商家可与到家运营沟通开通远距离服务，超过2公里后每1公里加收2元运费。费用承担方为商家
			long orderReceivableFreight = order.getOrderReceivableFreight();//订单应收运费：用户应该支付的订单运费，即未优惠前应付运费(不计满免运费，运费优惠券，VIP免基础运费等优惠)，包含用户小费。订单对应门店配送方式为商家自送，则订单应收运费为设置的门店自送运费；订单对应门店配送方式为达达配送，则订单应收运费为用户支付给达达的配送费（平台规则统一设置，如基础运费、重量阶梯运费、距离阶梯运费、夜间或天气等因素的附加运费）
			long orderBuyerPayableMoney = order.getOrderBuyerPayableMoney();//用户应付金额（单位为分）=商品销售价总金额orderTotalMoney -订单优惠总金额 orderDiscountMoney+实际订单运费orderFreightMoney +包装金额packagingMoney -用户积分抵扣金额
			long platformPointsDeductionMoney = order.getPlatformPointsDeductionMoney();//用户积分抵扣金额
			long tips = order.getTips();//商家给配送员加的小费，原订单配送方式为达达配送，在运单状态为待抢单且超时5分钟后，商家可以转成自己配送，转自送后，如果订单商家有小费，则商家小费清零。到家系统会下发订单转自送消息，商家需订阅转自送消息，再次调用订单列表查询接口获取订单信息
			long userTip = order.getUserTip();//用户小费（用户给配送员加小费）
			
			long orderOldAmt =	orderTotalMoney  + orderFreightMoney + packagingMoney;//订单原金额=订单商品销售价总金额+配送费+包装费;
			long orderPayAmt = orderBuyerPayableMoney;//用户应付金额	
			//订单金额=用户应付金额orderBuyerPayableMoney+用户积分抵扣金额platformPointsDeductionMoney	
			long orderAmt = orderTotalMoney - orderDiscountMoney + orderFreightMoney + packagingMoney ;//商品销售价总金额orderTotalMoney -订单优惠总金额 orderDiscountMoney+实际订单运费orderFreightMoney +包装金额packagingMoney =用户应付金额orderBuyerPayableMoney+用户积分抵扣金额platformPointsDeductionMoney			
			long orderDisc = orderOldAmt - orderAmt;//订单优惠总金额
			double tot_oldAmt =(double) orderOldAmt/100;
			double tot_payAmt =(double) orderPayAmt/100;
			double tot_Amt = (double)orderAmt/100;
			double totDisc =(double) orderDisc/100;
			double shipFee =(double) orderFreightMoney/100;
			//店铺实收，目前没有其他项，只算商品金额；实际收入=(订单商品销售价总金额+平台补贴)-(订单商品销售价总金额+平台补贴)*0.15 
			//平台服务费=(订单商品销售价总金额+平台补贴)*0.15 （必须这里四舍五入2位小数）
			long orderShopTotalMoney = orderTotalMoney + jddjDiscMoney;//商品的商家实际销售金额
			double orderShopTotalMoney_d = (double) orderShopTotalMoney/100;
			HelpTools.writelog_fileName("【JDDJ的订单】商品的商家实际销售金额=订单商品销售价总金额+平台补贴："+orderShopTotalMoney+" 订单商品销售价总金额:"+orderTotalMoney+" 平台补贴:"+jddjDiscMoney, jddjLogFileName);	
			HelpTools.writelog_fileName("【JDDJ的订单】商品的商家实际销售金额（元）："+orderShopTotalMoney_d, jddjLogFileName);	
			double serviceCharge =(double) orderShopTotalMoney*0.15/100;//服务费
			BigDecimal bg = new BigDecimal(serviceCharge);
      double serviceCharge_halfup = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//服务费四舍五入
      //京东有个最低服务费目前味多美是4块
			if (serviceCharge_halfup < 4)
      {
      	HelpTools.writelog_fileName("【JDDJ的订单】计算出来服务费小于保底服务费！ 服务费："+serviceCharge_halfup, jddjLogFileName);
      	serviceCharge_halfup = 4;
      	HelpTools.writelog_fileName("【JDDJ的订单】平台保底服务费目前味多美是（4元） 更新服务费："+serviceCharge_halfup, jddjLogFileName);
      }
      
      //经过研究发现。店铺实收还要减去 远距离配送费
      //商家承担的远距离配送费
      double merchantPaymentDistanceFreightMoney_d = (double)merchantPaymentDistanceFreightMoney/100;
			double incomeAmt = orderShopTotalMoney_d - serviceCharge_halfup - merchantPaymentDistanceFreightMoney_d;
      HelpTools.writelog_fileName("【JDDJ的订单】店铺实收："+incomeAmt+"= 商家实际销售金额（元）："+orderShopTotalMoney_d+"- 服务费："+serviceCharge_halfup+"- 商家承担的远距离配送费："+merchantPaymentDistanceFreightMoney_d, jddjLogFileName);	
			//double incomeAmt = tot_Amt - shipFee-jddjPackagingMoney;//服务费=0  实际收入=订单实际金额-配送费-京东到家的包装费
			
			tvOrder.setTot_oldAmt(Double.toString(tot_oldAmt));//订单原价
			tvOrder.setTot_Amt(Double.toString(tot_Amt));//订单金额
			tvOrder.setTotDisc(Double.toString(totDisc));//订单优惠总额
			tvOrder.setShipFee(Double.toString(shipFee));//实际配送费
			tvOrder.setServiceCharge(Double.toString(serviceCharge_halfup));//服务费 暂时不知道
			tvOrder.setIncomeAmt(Double.toString(incomeAmt));//服务费=0 实际收入=订单实际金额
			
			tvOrder.setPlatformDisc(Double.toString(totDisc));//暂时全部放到平台折扣里面
			tvOrder.setSellerDisc("0");
			tvOrder.setShopShareDeliveryFee("0");
			
			int orderPayType = order.getOrderPayType();//订单支付类型(1：货到付款，4:在线支付;)
			String payStatus = "1";//1.未支付 2.部分支付 3.付清
			String payAmt = "0";//用户已支付金额
			if (orderPayType == 4)
			{
				payStatus = "3";
				payAmt = Double.toString(tot_payAmt);			
			}
			tvOrder.setPayStatus(payStatus);
			tvOrder.setPayAmt(payAmt);
			
			String isBook = "N";
			String businessTag = order.getBusinessTag();//dj_mobile_safe_order;dj_order_pickDeadLine;dj_new_promise_v3;vender_first_order;dj_cs;dj_new_cashier;dj_aging_immediately;picking_up;
			if(businessTag.contains("dj_aging_immediately"))
			{
				isBook = "N";
			}
			else
			{
				isBook = "Y";
			}
			tvOrder.setIsBook(isBook);
			
			String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			String sTime = new SimpleDateFormat("HHmmss").format(new Date());
			tvOrder.setsDate(sDate);
			tvOrder.setsTime(sTime);
			
			return tvOrder;
		
	  } 
		catch (Exception e) 
		{
			errorMessage.append("【JDDJ的订单对象转换】异常:"+e.getMessage());
			HelpTools.writelog_fileName("【JDDJ的订单对象转换】异常:"+e.getMessage(), jddjLogFileName);	
		
	  }	
		
		try 
		{
		
	  } 
		catch (Exception e) 
		{
		
	  }	
		
		return null;
	}
	
*/}
