package com.dsc.spos.waimai;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.JDKDSASigner.stdDSA;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.amazonaws.partitions.model.Region;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.res.DCP_WarningMonitoringDetailRes.cardWarningData;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.dao.impl.SPosDAOImpl;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderPay;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.waimai.entity.order;
import com.dsc.spos.waimai.entity.orderGoodsItem;
import com.dsc.spos.waimai.entity.orderGoodsItemAgio;
import com.dsc.spos.waimai.entity.orderGoodsItemMessage;
import com.dsc.spos.waimai.entity.orderInvoice;

import eleme.openapi.sdk.api.entity.message.OMessage;
import eleme.openapi.sdk.api.entity.order.Item;
import eleme.openapi.sdk.api.entity.order.OGoodsGroup;
import eleme.openapi.sdk.api.entity.order.OGoodsItem;
import eleme.openapi.sdk.api.entity.order.OGroupItemAttribute;
import eleme.openapi.sdk.api.entity.order.OGroupItemSpec;
import eleme.openapi.sdk.api.entity.order.OOrder;
import eleme.openapi.sdk.api.entity.order.ODeliveryRecord;
import eleme.openapi.sdk.api.entity.order.ORefundOrder;
import eleme.openapi.sdk.api.entity.order.UserExtraInfo;
import eleme.openapi.sdk.api.entity.product.OCategory;
import eleme.openapi.sdk.api.enumeration.order.InvoiceType;
import eleme.openapi.sdk.api.enumeration.order.OOrderDetailGroupType;
import eleme.openapi.sdk.api.enumeration.order.OOrderRefundStatus;
import eleme.openapi.sdk.api.enumeration.order.OOrderStatus;
import eleme.openapi.sdk.api.enumeration.order.ORefundType;
//import eleme.openapi.ws.sdk.config.BusinessHandle;
//import eleme.openapi.ws.sdk.utils.JacksonUtils;
import eleme.openapi.sdk.utils.JacksonUtils;
import sun.rmi.runtime.Log;

import javax.print.DocFlavor;

public class WMELMService extends SWaimaiBasicService
{

	private String refundReason = "";
	private double refundMoney = 0;
	private int elmMessageType = 0;//12表示已接单不写缓存了。

	@Override
	public String execute(String json) throws Exception
	{
		// String res_json = HelpTools.GetJBPResponse(json);
		String res_json = json;
		if (res_json == null || res_json.length() == 0)
		{
			return null;
		}
		Map<String, Object> res = new HashMap<String, Object>();
		this.processDUID(res_json, res);

		return null;
	}

	@Override
	protected void processDUID(String message, Map<String, Object> res) throws Exception
	{
		try
		{
			// **************开始处理消息，自己写***********
			HelpTools.writelog_waimai("【收到饿了么的消息内容】" + message);
			long dt1 = System.currentTimeMillis();
			OMessage omessage = JacksonUtils.json2pojo(message, OMessage.class);
			long postTimestamp = omessage.getTimestamp();
			long timestamp_type217 = 1696003199000L;//2023-09-29 23:59:59,饿了么新订单消息类型type=10,20230930下线
			int elmType = omessage.getType();
			elmMessageType = elmType;
			String order_message = omessage.getMessage();
			List<Item> orderRefundGoods = new ArrayList<Item>();
			String ELM_newOrderMessageType = "";
			/*
			 * 10 订单生效，店铺可以看到新订单(2023-09-30下线)；217 订单生效，店铺可以看到新订单；12 商户已经接单；14 订单被取消（接单前）；15 订单置为无效（接单后）；17
			 * 订单强制无效（商家主动取消已接受订单、用户1分钟内取消；18 订单完结
			 * 
			 */
			if (elmType == 10) // 订单生效，店铺可以看到新订单
			{
				/*ELM_newOrderMessageType = PosPub.getPARA_SMS(this.dao,"10","","ELM_newOrderMessageType");
				if ("Y".equals(ELM_newOrderMessageType))
				{
					HelpTools.writelog_waimai("【饿了么】饿了么外卖开启了推拉模式,消息类型type=" + elmType + ",暂不处理！");
					return;
				}*/
				OOrder elemeOrder = JacksonUtils.json2pojo(order_message, OOrder.class);
				long dt2 = System.currentTimeMillis();
				long dt_spwn = dt2-dt1;
				if (dt_spwn>100)
				{
					HelpTools.writelog_waimai("【ELM的sdk解析】耗时:[" + dt_spwn+"]MS");
				}

				// 存缓存
				order dcpOrder = NewOrderProcess(elemeOrder, "1", "1");
				// 存数据库
				SaveOrder(dcpOrder);
				//处理下降级订单
				if (dcpOrder!=null&&"Y".equals(dcpOrder.getDowngraded()))
				{
					if (dcpOrder.getShopNo()!=null&&dcpOrder.getShopNo().trim().length()>0)
					{
						//开启线程
						//15秒查询一次，查询10次，之后 ，还是降级，就主动取消
						StringBuilder errorMessage = new StringBuilder();
						// 创建一个线程组
						ExecutorService exe = Executors.newFixedThreadPool(1);

						Callable callable = new thread_Monitor_HY(this.dao,dcpOrder, errorMessage);
						Future future = exe.submit(callable);

						exe.shutdown();
					}
				}
			}
			else if (elmType == 217)// 订单生效，店铺可以看到新订单,需要查询一次订单获取完整信息
			{
				//推送消息 type=217(订单生效)，推送的字段定义为 订单履约核心字段，只需要这些字段即可保证订单的履约
				//针对此前接入了 type=10 的开发者，需要注意的是 type=10 与 type=217 触发推送的时机可以理解为后者为前者的解耦、精简版，20230930 type=10 会下线
				//拉单：非核心履约场景，比如活动营销等数据的获取，可以使用现有的 eleme.order.getOrder、eleme.order.mgetOrders 接口获取所需字段。
				if (postTimestamp<timestamp_type217)
				{
					ELM_newOrderMessageType = PosPub.getPARA_SMS(this.dao,"10","","ELM_newOrderMessageType");
					if ("Y".equals(ELM_newOrderMessageType))
					{
						HelpTools.writelog_waimai("【饿了么】饿了么外卖开启了推拉模式,消息类型type=" + elmType + ",开启测试中");
					}
					else
					{
						HelpTools.writelog_waimai("【饿了么】该消息类型暂不处理(2023-09-30开始处理)： 消息类型type=" + elmType + "");
						return;
					}

				}

				OOrder elemeOrder = JacksonUtils.json2pojo(order_message, OOrder.class);
				try
				{
					String orderNo = elemeOrder.getId();
					// 查询下当前门店的对应的饿了么APPKEY
					Map<String, String> mapAppKey = HelpTools.GetELMShopIdConfig(Long.toString(elemeOrder.getShopId()));
					if (mapAppKey == null||mapAppKey.isEmpty())
					{
						HelpTools.writelog_waimai("【饿了么】【type=217】在线查询订单信息，所需要得参数值为空,单号orderNo="+orderNo);
						//return null ;
					}
					else
					{
						String elmAPPKey = "";
						String elmAPPSecret = "";
						String elmAPPName = "";
						String isJbp = "";
						String userId = "";
						boolean elmIsSandbox = false;
						elmAPPKey = mapAppKey.getOrDefault("appKey","").toString();
						elmAPPSecret = mapAppKey.getOrDefault("appSecret","").toString();
						elmAPPName = mapAppKey.getOrDefault("appName","").toString();
						String elmIsTest = mapAppKey.getOrDefault("isTest","").toString();
						if (elmIsTest != null && elmIsTest.equals("Y"))
						{
							elmIsSandbox = true;
						}
						isJbp = mapAppKey.getOrDefault("isJbp","").toString();
						userId = mapAppKey.getOrDefault("userId","").toString();
						String erpShopNo = mapAppKey.getOrDefault("erpShopNo","").toString();
						if (elmAPPKey.isEmpty()||elmAPPSecret.isEmpty()||erpShopNo==null||erpShopNo.trim().isEmpty())
						{
							//没映射的，不需要浪费效能查询一次订单详细信息
						}
						else
						{
							StringBuilder errorMessage = new StringBuilder();
							long dt2 = System.currentTimeMillis();
							OOrder elmOrder = null;
							if ("Y".equals(isJbp))
							{
								elmOrder = WMELMProductService.getOrder(userId,elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo,
										errorMessage);
							}
							else
							{
								elmOrder = WMELMProductService.getOrder(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo,
										errorMessage);
							}
							long dt3 = System.currentTimeMillis();
							long dt_spwn = dt3-dt2;
							if (dt_spwn>200)
							{
								HelpTools.writelog_waimai("【ELM在线查询订单详细】耗时:[" + dt_spwn+"]MS，单号orderNo="+orderNo);
							}
							if (elmOrder!=null)
							{
								elemeOrder = elmOrder;//重新赋值，包含了所有信息
							}
						}

					}

				}
				catch (Exception e)
				{

				}

				// 存缓存
				order dcpOrder = NewOrderProcess(elemeOrder, "1", "1");
				// 存数据库
				SaveOrder(dcpOrder);
				//处理下降级订单
				if (dcpOrder!=null&&"Y".equals(dcpOrder.getDowngraded()))
				{
					if (dcpOrder.getShopNo()!=null&&dcpOrder.getShopNo().trim().length()>0)
					{
						//开启线程
						//15秒查询一次，查询10次，之后 ，还是降级，就主动取消
						StringBuilder errorMessage = new StringBuilder();
						// 创建一个线程组
						ExecutorService exe = Executors.newFixedThreadPool(1);

						Callable callable = new thread_Monitor_HY(this.dao,dcpOrder, errorMessage);
						Future future = exe.submit(callable);

						exe.shutdown();
					}
				}
			}
			else
			{
				// 多种类型的 message里面结构不一样
				// 订单状态变更消息(12、14、15、17、18） 状态变更结构体
				// 取消单消息（20用户申请取消单、21用户取消取消单申请、22商户拒绝取消单、23商户同意取消单、24用户申请仲裁取消单、25客服仲裁取消单申请有效、26客服仲裁取消单申请无效）
				// 退单消息(30用户申请退单、31用户取消退单、32商户拒绝退单、33商户同意退单、34用户申请仲裁、35客服仲裁退单有效、36客服仲裁退单无效）
				// 取消消息与退单消息 结构体一致
				// 催单消息（45用户催单) 催单消息结构体
				String status = "";// 门店管理对应的 订单状态1.订单开立 2.已接单 3.已拒单 4.生产接单  5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单									 
				String refundstatus = "1";// 订单退单状态说明：1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功

				/*
				 * if(elmType == 10) { status = "1"; }
				 */
				if (elmType == 12)
				{
					status = "2";
				} else if (elmType == 14)
				{
					//14-订单被取消（接单前）
					status = "3";
				} 
				else if ( elmType == 15 || elmType == 17)
				{
					//15-订单置为无效（接单后）; 17-订单强制无效（商家主动取消已接订单、用户1分钟内取消）
					refundstatus = "6";
					status = "12";
				}
				else if (elmType == 18)
				{
					status = "11";
				} else if (elmType == 20 || elmType == 30)
				{
					status = "2";// 必须要设置，只有已接单的才能退
					refundstatus = "2";
					try
					{
						ORefundOrder orderRefund = JacksonUtils.json2pojo(order_message, ORefundOrder.class);
						// refundReason =
						// orderRefund.getApplyRefundReason();//返回是null						
						try
						{
							JSONObject obj_refund = new JSONObject(order_message);
							if (!obj_refund.isNull("reason"))
							{
								refundReason = obj_refund.get("reason").toString();
							}

						} catch (Exception e)
						{
							// TODO: handle exception

						}

						HelpTools.writelog_fileName("【饿了么】推送申请退款消息：" + order_message + " 单号orderNO="
								+ orderRefund.getOrderId() + " 退单原因refundReason=" + refundReason
								+ " 实体类返回orderRefund.getApplyRefundReason()=" + orderRefund.getApplyRefundReason(),
								"refunReasonLog");
						if (elmType == 30) // 申请部分退单
						{
							status = "11";
							if (orderRefund.getRefundType() == ORefundType.part)
							{
								refundstatus = "7";
								status = "11";
								orderRefundGoods = orderRefund.getGoodsList();

								HelpTools.writelog_waimai(
										"【饿了么】推送申请部分退款： orderNO=" + orderRefund.getOrderId() + " 订单状态status=" + status
												+ " 退单状态refundStatus=" + refundstatus + " 部分退单消息内容：" + order_message);

							}

						}

					} catch (Exception e)
					{

					}

				} else if (elmType == 21 || elmType == 31)
				{
					status = "11";// 必须要设置，只有已接单的才能退
					refundstatus = "1";
				} else if (elmType == 22 || elmType == 32)
				{
					status = "11";// 必须要设置，只有已接单的才能退
					refundstatus = "3";
				} else if (elmType == 23 || elmType == 33 || elmType == 25 || elmType == 35|| elmType == 39)
				{
					if (elmType == 39)
					{
						//商户售中部分取消
						refundstatus = "10";
						status = "2";
					}
					else
					{
						refundstatus = "6";
						status = "12";
					}

					try
					{
						if (elmType == 33|| elmType == 35|| elmType == 39)
						{
							ORefundOrder orderRefund = JacksonUtils.json2pojo(order_message, ORefundOrder.class);
							
							/*try
							{
								JSONObject obj_refund = new JSONObject(order_message);
								if (!obj_refund.isNull("reason"))
								{
									refundReason = obj_refund.get("reason").toString();
								}

							} catch (Exception e)
							{
								// TODO: handle exception

							}*/
							
							
							if (orderRefund.getRefundType() == ORefundType.part)
							{
								refundstatus = "10";
								status = "11";
								if (elmType == 39)
								{
									//商户售中部分取消
									refundstatus = "10";
									status = "2";
								}
								orderRefundGoods = orderRefund.getGoodsList();
								refundMoney = orderRefund.getTotalPrice();
								HelpTools.writelog_waimai(
										"【饿了么】推送部分退款成功消息： orderNO=" + orderRefund.getOrderId() + " 订单状态status=" + status
												+ " 退单状态refundStatus=" + refundstatus + " 部分退单消息内容：" + order_message);
								try
								{
									double refundMoney_goods = 0;
									for (Item item : orderRefundGoods)
									{
										refundMoney_goods +=item.getPrice()*item.getQuantity();
									}
									
									double deff_money = orderRefund.getTotalPrice()-refundMoney_goods;
									if(Math.abs(deff_money)>=0.01)
									{
										HelpTools.writelog_waimai(
												"【饿了么】推送部分退款成功消息： orderNO=" + orderRefund.getOrderId() + " 订单状态status=" + status
														+ " 退单状态refundStatus=" + refundstatus + " 饿了么平台推送的部分退，退款合计与商品明细合计存在差异金额：" + deff_money);
										//有差异，算到最后一个商品
										for (int i = 0; i<orderRefundGoods.size();i++)
										{
											if(i==orderRefundGoods.size()-1)
											{
												Item item_goods = orderRefundGoods.get(i);
												
												BigDecimal item_oldAmt = new BigDecimal(item_goods.getPrice()*item_goods.getQuantity());
												
												BigDecimal deff_b = new BigDecimal(deff_money);
												
												BigDecimal item_newAmt = item_oldAmt.add(deff_b);
												BigDecimal item_qty = new BigDecimal(item_goods.getQuantity());
												BigDecimal item_newPrice = item_newAmt.divide(item_qty,2, BigDecimal.ROUND_HALF_UP);
												
												item_goods.setPrice(item_newPrice.doubleValue());
												continue;
											}
										}
									}
									
								} catch (Exception e)
								{
									// TODO: handle exception
								}
							}

						}

					} catch (Exception e)
					{

					}

				} else if (elmType == 24 || elmType == 34)
				{
					status = "11";// 必须要设置，只有已接单的才能退
					refundstatus = "4";
				} else if (elmType == 26 || elmType == 36)
				{
					status = "11";// 必须要设置，只有已接单的才能退
					refundstatus = "5";
				} else if (elmType >= 53 && elmType <= 59) // 配送消息
				{
					try
					{
						UpdateOrderShippingStatus(elmType, order_message, omessage.getShopId());
					} catch (Exception e)
					{

					}
					return;
				} else
				{
					//HelpTools.writelog_waimai("【饿了么】其他消息类型还没对接： 消息类型type=" + elmType + " 单消息内容：" + order_message);
					return;
				}

				JSONObject obj = new JSONObject(order_message);
				String orderNO = obj.get("orderId").toString();
				String shopId = obj.get("shopId").toString();
				// 更新缓存
				order dcpOrder = UpdateOrderProcess(shopId, orderNO, status, refundstatus, orderRefundGoods);
				// 存数据库
				SaveOrder(dcpOrder);
			}
			return;
		} catch (Exception e)
		{

			try
			{

				HelpTools.writelog_waimai("【处理饿了么的消息内容异常】" + e.getMessage());
			} catch (Exception e2)
			{

			}

			return;
		}

	}

	// 新订单处理，解析存缓存
	private order NewOrderProcess(OOrder order, String paraStatus, String paraRefundStatus) throws Exception
	{
		if (order == null)
		{
			return null;

		}

		try
		{

			order dcpOrder = new order();
			dcpOrder.setPay(new ArrayList<orderPay>());
			JSONObject jsonobjresponse = new JSONObject();
			String eId = "99";
			String loadDocType = orderLoadDocType.ELEME;// 渠道类型
			String channelId = "";// 渠道编码 多个饿了么应用
			String shopno = "";// 主键不能为空，所以默认空格			
			String erpShopName = "";

			Map<String, String>	mappingShopMap = HelpTools.GetELMMappingShop(Long.toString(order.getShopId()));
			eId = mappingShopMap.get("eId");
			shopno = mappingShopMap.get("erpShopNo");
			channelId = mappingShopMap.get("channelId");
			erpShopName = mappingShopMap.getOrDefault("erpShopName", "");

			dcpOrder.seteId(eId);	
			String orderNo = order.getId();
			dcpOrder.setLoadDocType(loadDocType);
			dcpOrder.setChannelId(channelId);	
			dcpOrder.setOrderNo(orderNo);//dcp单号=来源单号
			dcpOrder.setLoadDocOrderNo(orderNo);//来源单号
			String orderCodeView = "";
			orderCodeView = HelpTools.getELMOrderIdView(orderNo);
			dcpOrder.setOrderCodeView(orderCodeView);
			dcpOrder.setLoadDocBillType("");//来源单据类型
			dcpOrder.setOrderShop(order.getShopId()+"");//第三方门店ID
			dcpOrder.setOrderShopName(order.getShopName());//第三方门店名称
			if(erpShopName==null||erpShopName.isEmpty())
			{
				erpShopName = order.getShopName();
			}				
			dcpOrder.setShopNo(shopno);
			dcpOrder.setShopName(erpShopName);
			dcpOrder.setShippingShopNo(shopno);
			dcpOrder.setShippingShopName(erpShopName);
			dcpOrder.setMachShopNo(shopno);
			dcpOrder.setMachShopName(erpShopName);
		
			dcpOrder.setSn(order.getDaySn()+"");
			boolean isBook = order.getBook();
			String isBookStr = "N";
			if (isBook)
			{
				isBookStr = "Y";
			}			
			dcpOrder.setIsBook(isBookStr);// 外卖预定单

			String downgraded = "N";
			if (order.getDowngraded())
			{
				downgraded = "Y";
			}
			dcpOrder.setDowngraded(downgraded);
			/***************发票信息*******************/
			orderInvoice dcpOrderInvoiceDetail = new orderInvoice();
			boolean isinvoiced = order.getInvoiced();
			String isinvoicedStr = "N";
			if (isinvoiced)
			{
				isinvoicedStr = "Y";
			}
			//jsonobjresponse.put("isInvoice", isinvoicedStr);// 是否开发票			
			dcpOrderInvoiceDetail.setIsInvoice(isinvoicedStr);
			
			InvoiceType invoiceType = order.getInvoiceType();
			String peopleType = "2";//主体类型：1公司，2个人
			if (invoiceType != null && invoiceType == InvoiceType.company)
			{
				peopleType = "1";
			}
			dcpOrderInvoiceDetail.setPeopleType(peopleType);
			dcpOrderInvoiceDetail.setInvoiceType("");
			dcpOrderInvoiceDetail.setInvoiceTitle(order.getInvoice() == null ? "" : order.getInvoice());	
			dcpOrderInvoiceDetail.setTaxRegNumber(order.getTaxpayerId() == null ? "" : order.getTaxpayerId());
			
			dcpOrder.setInvoiceDetail(dcpOrderInvoiceDetail);
			
			
			/********************金额处理，饿了么折扣服务费都是返回的负数****************************/			
			String totalAmount = Double.toString(order.getTotalPrice());// 订单总价，用户实际支付的金额，单位：元
			dcpOrder.setTot_Amt(order.getTotalPrice());// 订单金额,用户实际支付的金额
			dcpOrder.setPayAmt(order.getTotalPrice());// 用户已支付金额
			String totalOldAmount = Double.toString(order.getOriginalPrice());// 订单优惠前的价格，即商品总价加上配送费和餐盒费，单位：元
			dcpOrder.setTot_oldAmt(order.getOriginalPrice());// 订单原价
			String incomeAmount = Double.toString(order.getIncome());// 店铺实际本单收入，订单总额扣除服务费、商户补贴金额
			dcpOrder.setIncomeAmt(order.getIncome());// 商家实收金额			
			String deliverAmount = Double.toString(order.getDeliverFee());// 用户实际支付配送费 (该配送费为用户实际支付的配送费用(originalPrice中包含的配送费即该字段所对应的用户实际支付的配送费))
			dcpOrder.setShipFee(order.getDeliverFee());// 配送费
			String payStatus = "3";// 默认都是已支付
			dcpOrder.setPayStatus(payStatus);// 支付状态 1.未支付 2.部分支付 3.付清
																			
			String packageFeeStr = Double.toString(order.getPackageFee());
			dcpOrder.setPackageFee(order.getPackageFee());// 餐盒费
			
			String serviceFeeStr = Double.toString(0 - order.getServiceFee());//
			dcpOrder.setServiceCharge(0 - order.getServiceFee());
			try
			{
				//xyPackFlag：是否使用 x+y 服务包的标识
				// true：平台服务费 = serviceFee + fulfillServiceFee;
				// false：平台服务费 = serviceFee + timeIntervalMarkUpFee + distanceIncreaseFee + pricePremiums
				if (order.getXyPackFlag())
				{
					//HelpTools.writelog_waimai("订单orderNo="+order.getId()+",服务费serviceFee="+order.getServiceFee()+",履约服务费fulfillServiceFee="+order.getFulfillServiceFee());
					dcpOrder.setServiceCharge(0 - (order.getServiceFee()+order.getFulfillServiceFee()));//// 服务费   饿了么返回的是负数
				}
				else
				{
					//HelpTools.writelog_waimai("订单orderNo="+order.getId()+",服务费serviceFee="+order.getServiceFee()+",时段加价timeIntervalMarkUpFee="+order.getTimeIntervalMarkUpFee()+
					//		",距离加价distanceIncreaseFee="+order.getDistanceIncreaseFee()+",增值服务费additionServicePrice="+order.getAdditionServicePrice()+",价格加价pricePremiums="+order.getPricePremiums());
					dcpOrder.setServiceCharge(0 - (order.getServiceFee()+order.getTimeIntervalMarkUpFee()+order.getDistanceIncreaseFee()+order.getAdditionServicePrice()+order.getPricePremiums()));//// 服务费   饿了么返回的是负数
				}

			}
			catch (Exception e)
			{

			}

			// 注：红包活动分平台红包和店铺红包两种类型，平台红包即用户所用的红包是平台发放的客户的，用的是单独的字段“hongbao”，金额全部有平台承担；商家自建的店铺红包相当于是店铺创建的代金券活动，活动信息存储在orderActivities结构体内”categoryId”为15
			double sellerDisc = 0;// 商家承担优惠
			double platformDisc = 0;// 平台优惠总额(需要加上平台 hongbao+代理商承担的折扣)
			double totDisc = 0;// 订单优惠总额(需要加上平台 hongbao)

			totDisc = order.getOriginalPrice() - order.getTotalPrice();// 总折扣=订单原价-订单实际总价
			sellerDisc = 0 - order.getShopPart();// 店铺承担活动费用 饿了么返回的是负数
			platformDisc = totDisc - sellerDisc;// 平台折扣=总折扣-商家折扣（这其中包含了代理商折扣）
			dcpOrder.setTotDisc(totDisc);// 订单优惠总额						
			dcpOrder.setSellerDisc(sellerDisc);// 商户优惠总额			
			dcpOrder.setPlatformDisc(platformDisc);// 平台优惠总额

			double merchantDeliverySubsidy = order.getMerchantDeliverySubsidy();// 商家替用户承担的配送费
			dcpOrder.setShopShareShipfee(0 - merchantDeliverySubsidy);// 商家替用户承担的配送费   返回的是负数			
			
			

			String contactName = order.getConsignee();// 联系人			
			dcpOrder.setContMan(contactName);// 联系人	
			dcpOrder.setGetMan(contactName);
			String contactTelephone = "";
			try
			{
				contactTelephone = order.getPhoneList().get(0);// 默认取第一个

			} catch (Exception e)
			{

			}			
			dcpOrder.setContTel(contactTelephone);
			dcpOrder.setGetManTel(contactTelephone);

			Date date = order.getCreatedAt();// 下单时间 2018-08-29 16:57:13
			String orderDateTime = "";
			try
			{
				orderDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
			} catch (Exception e)
			{

			}			
			dcpOrder.setCreateDatetime(orderDateTime);// 下单时间，格式yyyyMMddHHmmssSSS
			
			String sDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());		
			dcpOrder.setsTime(sDateTime);//dcp当前时间
			String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			String curTime = new SimpleDateFormat("HHmmss").format(new Date());

			date = order.getDeliverTime(); // 只有预订单的预计送达时间才有意义
			String needDate = "";//
			String needTime = "";
			try
			{
				needDate = new SimpleDateFormat("yyyyMMdd").format(date);
				needTime = new SimpleDateFormat("HHmmss").format(date);
			} catch (Exception e)
			{
				needDate = curDate;
				needTime = curTime;
			}

			String address = order.getDeliveryPoiAddress() == null ? "" : order.getDeliveryPoiAddress();
			if (address != null && address.length() > 100)
			{
				address = address.substring(0, 100);
			}

			String deliverType = "1";// 1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送
			String isMerPay ="N";//配送费是否商家结算 
			int orderBusinessType = order.getOrderBusinessType();// (0外卖单,1到店自取订单)
			if (orderBusinessType == 1)
			{
				// 到底自取配送时间=配送时间； 送餐地址=到店自取
				deliverType = "3";
				Date pickUpDate = order.getPickUpTime();
				needDate = new SimpleDateFormat("yyyyMMdd").format(pickUpDate);
				needTime = new SimpleDateFormat("HHmmss").format(pickUpDate);
				address = "到店自取";
			}			
			dcpOrder.setAddress(address);
			//jsonobjresponse.put("shipType", deliverType);// 
			dcpOrder.setShipType(deliverType);
			dcpOrder.setIsMerPay(isMerPay);//配送费是否商家结算
			
			dcpOrder.setShipDate(needDate);// 配送日期格式yyyyMMdd 20180808
			dcpOrder.setShipStartTime(needTime);// 配送开始时间 HHmmss 170000
			dcpOrder.setShipEndTime(needTime);// 配送结束时间 HHmmss 170000
			
			
			String memo = order.getDescription() == null ? "" : order.getDescription();// 订单备注			
			// 20191121添加 订购人 祝福语
			try
			{
				UserExtraInfo extraInfo = order.getUserExtraInfo();
				if (extraInfo != null)
				{
					String giverPhone = extraInfo.getGiverPhone();
					if (giverPhone != null && giverPhone.trim().isEmpty() == false)
					{
						memo += "【订购人】：" + giverPhone;
					}
					String greeting = extraInfo.getGreeting();

					if (greeting != null && greeting.trim().isEmpty() == false)
					{
						memo += "【祝福语】：" + greeting;
					}
				}
			} catch (Exception e)
			{
				// TODO: handle exception

			}
			dcpOrder.setMemo(memo);

			OOrderStatus status = order.getStatus();//
			OOrderRefundStatus refundstatus = order.getRefundStatus();
			dcpOrder.setStatus("1");// 订单状态 订单开立			
			dcpOrder.setRefundStatus("1");// 退单状态 1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功	
			// 传进来的 状态
			if (paraStatus != null && paraStatus.trim().length() > 0)
			{
				dcpOrder.setStatus(paraStatus);// 订单状态
				HelpTools.writelog_waimai("【ELM开始写缓存】" + "orderNo=" + orderNo + " 传参paraStatus=" + paraStatus);
			}
			if (paraRefundStatus != null && paraRefundStatus.trim().length() > 0)
			{
				dcpOrder.setRefundStatus(paraRefundStatus);
				HelpTools.writelog_waimai("【ELM开始写缓存】" + "orderNo=" + orderNo + " 传参paraRefundStatus=" + paraRefundStatus);
			}

			String longitude = "0";// 经度
			String latitude = "0";// 纬度
			try
			{
				// 经纬度(高德地图坐标系)
				String deliveryGeo = order.getDeliveryGeo();				
				String[] deliveryGeos = deliveryGeo.split(",");
				if (deliveryGeos.length > 1)
				{
					longitude = deliveryGeos[0];
					latitude = deliveryGeos[1];									
				}				

			} catch (Exception e)
			{
				// TODO: handle exception

			}
			dcpOrder.setLongitude(longitude);
			dcpOrder.setLatitude(latitude);

			dcpOrder.setRefundReason("");// 退单原因
			
			/***************商品单身********************/
			dcpOrder.setGoodsList(new ArrayList<orderGoodsItem>());
			JSONArray array = new JSONArray();
			List<OGoodsGroup> goodsGroups = order.getGroups();
			int item = 0;
			double tot_qty = 0;
			for (OGoodsGroup oGoodsGroup : goodsGroups)
			{
				String groupName = oGoodsGroup.getName() == null ? "" : oGoodsGroup.getName();
				OOrderDetailGroupType groupType = oGoodsGroup.getType();
				if (groupType == OOrderDetailGroupType.extra)
				{
					// 餐盒不用作为商品
					continue;
				}
				if (groupType == OOrderDetailGroupType.discount)
				{
					// 赠品不用作为商品
					continue;
				}
				List<OGoodsItem> goodsItems = oGoodsGroup.getItems();
				for (OGoodsItem oGoodsItem : goodsItems)
				{
					item++;
					orderGoodsItem goodsItem = new orderGoodsItem();
					goodsItem.setMessages(new ArrayList<orderGoodsItemMessage>());
				
					long foodId = oGoodsItem.getVfoodId();
					String mappingFoodId = oGoodsItem.getExtendCode() == null ? "" : oGoodsItem.getExtendCode();
					goodsItem.setItem(item+"");
					goodsItem.setPluNo(mappingFoodId);
					goodsItem.setPluBarcode(mappingFoodId);
					goodsItem.setSkuId(oGoodsItem.getSkuId()+"");
					goodsItem.setPluName(oGoodsItem.getName() == null ? "" : oGoodsItem.getName());
					
					List<OGroupItemSpec> specList = oGoodsItem.getNewSpecs();
					String spec = "";
					try
					{
						if (specList != null && specList.size() > 0)
						{
							for (OGroupItemSpec oGroupItemSpec : specList)
							{
								spec += oGroupItemSpec.getValue();
							}
						}
					} catch (Exception e)
					{

					}

				
					List<OGroupItemAttribute> AttrList = oGoodsItem.getAttributes();
					String attr = "";
					try
					{
						if (AttrList != null && AttrList.size() > 0)
						{
							for (OGroupItemAttribute oGroupItemAttribute : AttrList)
							{
								attr += oGroupItemAttribute.getValue();
							}
						}

					} catch (Exception e)
					{

					}
											
					goodsItem.setSpecName_origin(spec);
					goodsItem.setAttrName_origin(attr);
					// 饿了么商品名称已经拼接了规格和属性了，商品-规格[属性]
					goodsItem.setSpecName("");
					goodsItem.setAttrName("");
					goodsItem.setFeatureNo("");
					goodsItem.setFeatureName("");
					goodsItem.setsUnit("");
					goodsItem.setPrice(oGoodsItem.getPrice());
					goodsItem.setOldPrice(oGoodsItem.getPrice());
					goodsItem.setQty(oGoodsItem.getQuantity());
					goodsItem.setAmt(oGoodsItem.getTotal());
					goodsItem.setOldAmt(oGoodsItem.getTotal());
					goodsItem.setDisc(0);
					goodsItem.setBoxNum(0);
					goodsItem.setBoxPrice(0);
					goodsItem.setsUnitName("");
					goodsItem.setGoodsGroup(groupName);
					goodsItem.setIsMemo("N");
					
					tot_qty += oGoodsItem.getQuantity();
					
					dcpOrder.getGoodsList().add(goodsItem);
					
				}

			}
			dcpOrder.setTot_qty(tot_qty);
			dcpOrder.setTotQty(dcpOrder.getTot_qty());
			dcpOrder.setLoadDocTypeName("饿了么");
			dcpOrder.setChannelIdName("饿了么");
			//调用支付方式
			StringBuffer errorPayMessage = new StringBuffer();
			HelpTools.updateOrderPayByMapping(dcpOrder, errorPayMessage);
			
			errorPayMessage = new StringBuffer();
			HelpTools.updateOrderDetailInfo(dcpOrder, errorPayMessage);
			HelpTools.updateOrderWithPackage(dcpOrder, "", errorPayMessage);
			String status_json = dcpOrder.getStatus();// 获取下订单状态
				
			// 由于饿了么没有返回配送方式（平台配送，商家自配送）这里判断下
			if (deliverType.equals("1") && shopno != null && shopno.trim().length() > 0) // 排除到店自取																						// 到店自取
			{/*
				if (isOpenExpressOrderJob())
				{
					if (isSetAutoExpress(eId, shopno))
					{
						deliverType = "2";//  1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送
						jsonobjresponse.put("shipType", deliverType);// 配送方式
																		// 1.外卖平台配送
																		// 2.商户配送
																		// 3.顾客自提
						HelpTools.writelog_waimai("【ELM商家设置了自配送】【商家自配送】 订单Orderno=" + orderNo + " 门店shop=" + shopno);
					}
				}

			*/}
			ParseJson pj = new ParseJson();

			String Response_json = pj.beanToJson(dcpOrder);
			

			String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopno;			
			String hash_key = orderNo;
			String redis_key_elme = orderRedisKeyInfo.redis_OrderTableName + ":"+"ELM";// elm 写新订单写2次缓存不同主键，方便后续状态变更


			try
			{
				// 有做映射的门店才写缓存，数据库还是要存的
				//非降级订单才写缓存
				if (shopno != null && shopno.trim().length() > 0&&"N".equals(downgraded))
				{

					boolean IsUpdateRedis = true;
					RedisPosPub redis = new RedisPosPub();
					if (elmMessageType==12||elmMessageType==18)
					{
						//已接单、已完成状态不在写缓存
						IsUpdateRedis = false;
					}
					else
					{
						HelpTools.writelog_waimai(
								"【ELM开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
						boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
						if (isexistHashkey)
						{
							if (status_json != null && status_json.equals("1")) // 新订单的时候，已经存在了，说明缓存已经是最新的状态了，不需要更新缓存
							{
								IsUpdateRedis = false;
								HelpTools.writelog_waimai("【ELM订单开立状态】【ELM已经存在hash_key的缓存】【说明缓存已经最新状态不用更新缓存】！"
										+ "redis_key:" + redis_key + " hash_key:" + hash_key);
							}
						/*else
						{
							redis.DeleteHkey(redis_key, hash_key);//
							HelpTools.writelog_waimai(
									"【ELM删除存在hash_key的缓存】成功！" + " redis_key:" + redis_key + " hash_key:" + hash_key);
						}*/
						}
						else
						{
							//防止重复推送新订单
							if (status_json != null && status_json.equals("1")) // 新订单的时候，已经存在了，说明缓存已经是最新的状态了，不需要更新缓存
							{
								boolean isexistNewOrder = redis.IsExistHashKey(redis_key_elme, hash_key);
								if (isexistNewOrder)
								{
									IsUpdateRedis = false;
									HelpTools.writelog_waimai("【ELM订单开立状态】【重复推送】【之前新订单缓存已经处理被删了】！"
											+ "redis_key:" + redis_key_elme + " hash_key:" + hash_key);
								}

							}
						}
					}

					if (IsUpdateRedis)
					{
						boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
						if (nret)
						{
							HelpTools.writelog_waimai(
									"【ELM写缓存】OK" + " redis_key:" + redis_key + ",hash_key:" + hash_key);
						} else
						{
							HelpTools.writelog_waimai(
									"【ELM写缓存】Error" + " redis_key:" + redis_key + ",hash_key:" + hash_key);
						}
					}

					boolean nret_elm = redis.setHashMap(redis_key_elme, hash_key, Response_json);
					if (nret_elm)
					{
						HelpTools.writelog_waimai(
								"【ELM写ELM缓存】OK" + " redis_key:" + redis_key_elme + " hash_key:" + hash_key);
					} else
					{
						HelpTools.writelog_waimai(
								"【ELM写ELM缓存】Error" + " redis_key:" + redis_key_elme + " hash_key:" + hash_key);
					}
					// redis.Close();
				}
			} catch (Exception e)
			{
				HelpTools.writelog_waimai(
						"【ELM写缓存】Exception:" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
			}

			return dcpOrder;

		} catch (Exception e)
		{
			HelpTools.writelog_waimai("ELM解析饿了么消息内容异常！" + e.getMessage());
			return null;

		}

	}

	// 更新缓存
	private order UpdateOrderProcess(String shopid, String orderno, String status, String refundStatus,
			List<Item> orderRefundGoods) throws Exception
	{
		String Response_json = "";
		try
		{
			RedisPosPub redis = new RedisPosPub();
			String redis_key = orderRedisKeyInfo.redis_OrderTableName+":ELM";
			String hash_key = orderno;
			String ordermap = "";
			order orderDB = null;
			boolean needQueryDB = false;// 是否需要从数据库查询对应erp门店
			try
			{
				ordermap = redis.getHashMap(redis_key, hash_key);
			} catch (Exception e)
			{

			}
			ParseJson pj = new ParseJson();
			// 缓存中如果已经删除了
			if (ordermap == null || ordermap.isEmpty() || ordermap.length() == 0)
			{
				String eId = "";
				String erpshopno = "";
				String channelId = "";
				String erpShopName = "";
				Map<String, String>	mappingShopMap = HelpTools.GetELMMappingShop(shopid);
				eId = mappingShopMap.get("eId");
				erpshopno = mappingShopMap.get("erpShopNo");
				channelId = mappingShopMap.get("channelId");
				erpShopName = mappingShopMap.getOrDefault("erpShopName", "");

				// 如果WMORDER_ELM缓存没有，那么不一定有单身明细，查询下数据库
				HelpTools.writelog_waimai("【饿了么】更新订单状态查询数据库开始：eid=" + eId + " shopNo=" + erpshopno
						+ " orderNO=" + orderno + " 订单状态status=" + status + " 退单状态refundStatus=" + refundStatus
						 );
				orderDB = HelpTools.GetOrderInfoByOrderNO(StaticInfo.dao,eId,  orderLoadDocType.ELEME, orderno);
				ordermap = pj.beanToJson(orderDB);
				HelpTools.writelog_waimai("【饿了么】更新订单状态查询数据库完成：eid=" + eId + " shopNo=" + erpshopno
						+ " orderNO=" + orderno + " 订单状态status=" + status + " 退单状态refundStatus=" + refundStatus
						+ " 查询数据库返回:"+ordermap );
				
				//数据库没有，在线查询接口
				if(orderDB==null)
				{

					// 如果WMORDER_ELM缓存没有，那么不一定有单身明细，在线查询下
					try
					{
						// 如果WMORDER_ELM缓存没有，那么不一定有单身明细，在线查询下
						HelpTools.writelog_waimai(
								"【更新的单据ELM缓存"+redis_key+"不存在】准备在线查询接口" + " 订单号orderNO:" + orderno + " 订单状态status=" + status);
						// 如果不存在，接口查询下订单详情
						// 查询下当前门店的对应的饿了么APPKEY
						Map<String, Object> mapAppKey = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId, erpshopno,orderLoadDocType.ELEME,shopid);
						Boolean isGoNewFunction = false;// 是否走新的接口
						String elmAPPKey = "";
						String elmAPPSecret = "";
						String elmAPPName = "";
						boolean elmIsSandbox = false;
						String isJBP = "";//鼎捷服务商模式
						String userId = "";
						if (mapAppKey != null)
						{
							elmAPPKey = mapAppKey.get("APPKEY").toString();
							elmAPPSecret = mapAppKey.get("APPSECRET").toString();
							elmAPPName = mapAppKey.get("APPNAME").toString();
							String elmIsTest = mapAppKey.get("ISTEST").toString();
							if (elmIsTest != null && elmIsTest.equals("Y"))
							{
								elmIsSandbox = true;
							}
							isGoNewFunction = true;
							isJBP = mapAppKey.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
							userId = mapAppKey.getOrDefault("USERID","").toString();
						}

						StringBuilder errorMessage = new StringBuilder();
						HelpTools.writelog_waimai(
								"【更新的单据ELM缓存"+redis_key+"不存在】开始调用ELM订单获取接口" + " 订单号orderNO:" + orderno + " 订单状态status=" + status);
						OOrder elmOrder = null;
						if (isGoNewFunction)
						{
							if ("Y".equals(isJBP))
							{
								elmOrder = WMELMProductService.getOrder(userId,elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName,
										orderno, errorMessage);
							}
							else
							{
								elmOrder = WMELMProductService.getOrder(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName,
										orderno, errorMessage);
							}

						} else
						{
							elmOrder = WMELMProductService.getOrder(orderno, errorMessage);
						}
						String elmOrderJson = "";
						if (elmOrder != null)
						{
						   orderDB = NewOrderProcess(elmOrder, status, refundStatus);// 这个方法里面都是新建的订单
					   				  				
						   ordermap = pj.beanToJson(orderDB);
						   
							HelpTools.writelog_waimai("【更新的单据ELM缓存"+redis_key+"不存在】开始调用ELM订单获取接口" + " 订单号orderNO:" + orderno
									+ " 订单状态status=" + status + " 返回：" + ordermap);
						}

					} catch (Exception e)
					{
						// TODO: handle exception

					}
				}
				
				

				if (orderDB == null)
				{
				    orderDB = new order();
				    orderDB.seteId(eId);
				    orderDB.setLoadDocType(orderLoadDocType.ELEME);
				    orderDB.setOrderNo(orderno);
				    orderDB.setLoadDocOrderNo(orderno);
				    orderDB.setStatus(status);
				    orderDB.setRefundStatus(refundStatus);
				    orderDB.setGoodsList(new ArrayList<orderGoodsItem>());																						
				}

				


			}			
			else
			{
				orderDB = pj.jsonToBean(ordermap, new TypeToken<order>(){});
			}

			//部分退单成功

			if (refundStatus.equals("10")) // 部分退款
			{
				orderDB.setRefundAmt(refundMoney);

				if (orderRefundGoods != null && orderRefundGoods.size() > 0)
				{
					String orderDBJson = "";//GetOrderInfo(eId, erpshopno, orderno, orderLoadDocType.ELEME);
					HelpTools.writelog_waimai("【饿了么】部分退款开始：eid=" + orderDB.geteId() + " shopNo=" + orderDB.getShopNo()
							+ " orderNO=" + orderno + " 订单状态status=" + status + " 退单状态refundStatus=" + refundStatus
							+ " 查询数据库返回内容：" + ordermap);
					if (orderDB != null && orderDB.getGoodsList().isEmpty() == false)
					{
						try
						{
							//部分退单的商品
							//JSONObject foodObj =  new JSONObject("{\"food\":" + food + "}");
							//JSONArray partRefundGoodsArray = foodObj.getJSONArray("food");

							List<orderGoodsItem> goodsArray = orderDB.getGoodsList();
							List<orderGoodsItem> goodsArray_PartRefund = new ArrayList<orderGoodsItem>();
							boolean IsExistPartRefundGoods = false; // 检查是不是已经添加过部分退单商品了
							int partRefundGoodsItem = 999;
							if (goodsArray != null&&goodsArray.size()>0)
							{
								partRefundGoodsItem = goodsArray.size() + 1;
								for (int j = goodsArray.size() - 1; j >= 0; j--)
								{
									//JSONObject oldObj = goodsArray.getJSONObject(j);
									orderGoodsItem  oldObj =goodsArray.get(j);
									//String qty_str = oldObj.getString("qty").toString();
									double qty = oldObj.getQty();


									if (qty < 0)
									{
										IsExistPartRefundGoods = true;
									}
									else
									{
										goodsArray_PartRefund.add(oldObj);

									}

								}

							}
							// 循环部分退款的商品，添加到之前的商品（数量为负，金额为负）
							if (IsExistPartRefundGoods == false)
							{
								for (int i = 0; i < orderRefundGoods.size(); i++)
								{
									try {

										orderGoodsItem goodsItem = new orderGoodsItem();
										goodsItem.setMessages(new ArrayList<orderGoodsItemMessage>());
										goodsItem.setAgioInfo(new ArrayList<orderGoodsItemAgio>());

										//JSONObject job = orderRefundGoods.get(i);

										String app_food_code = orderRefundGoods.get(i).getVfoodId()+"";// APP方菜品id
										String food_name = orderRefundGoods.get(i).getName();// 菜品名称
										String sku_id = orderRefundGoods.get(i).getSkuId();// sku编码
										String quantity_str = orderRefundGoods.get(i).getQuantity()+"";// 部分退单商品数量
										// 转成负数
										String price_str = orderRefundGoods.get(i).getPrice()+"";// 退款的商品单价，此字段默认为活动折扣后价格
										String unit = "";// 单位

										if (unit == null || unit.isEmpty()) {
											unit = "份";// 默认个
										}
										// String food_discount =
										// job.get("food_discount").toString();//商品折扣，默认为1，仅美团商家可设置
										String attr = "";// 菜品属性 "中辣,微甜"

										String spec = "";// 菜品规格名称，

										String cart_id = "1号口袋";//默认，可以循环比较，没必要。

										double price = 0;
										double quantity = 0; // 部分退单的商品数量为负
										try {
											price = Double.parseDouble(price_str);
										} catch (Exception e) {
											price = 0;
										}
										try {
											quantity = 0 - Double.parseDouble(quantity_str);
										} catch (Exception e) {
											quantity = 0;
										}

										double amt = price * quantity;

										// 计算餐盒 包装费
										String box_price_str = "0";// 餐盒价格
										String box_num_str = "0";// 餐盒数量
										// 部分退单的商品数量为负
										double box_price = 0;
										double box_num = 0;// 部分退单的商品数量为负

										try {
											box_price = Double.parseDouble(box_price_str);
										} catch (Exception e) {
											box_price = 0;
										}
										try {
											box_num = 0 - Math.ceil(Double.parseDouble(box_num_str));
										} catch (Exception e) {
											box_num = 0;
										}

										goodsItem.setItem(partRefundGoodsItem+"");
										goodsItem.setPluNo(sku_id);
										goodsItem.setPluBarcode(sku_id);
										goodsItem.setSkuId(sku_id);
										goodsItem.setPluName(food_name);
										goodsItem.setSpecName(spec);
										goodsItem.setAttrName(attr);
										goodsItem.setFeatureNo("");
										goodsItem.setFeatureName("");
										goodsItem.setsUnit(unit);
										goodsItem.setPrice(price);
										goodsItem.setOldPrice(price);
										goodsItem.setQty(quantity);
										goodsItem.setAmt(amt);
										goodsItem.setOldAmt(amt);
										goodsItem.setDisc(0);
										goodsItem.setBoxNum(box_num);
										goodsItem.setBoxPrice(box_price);
										goodsItem.setsUnitName(unit);
										goodsItem.setGoodsGroup(cart_id);
										goodsItem.setIsMemo("N");
										partRefundGoodsItem++;
										goodsArray_PartRefund.add(goodsItem);

									}
									catch (Exception e)
									{
										HelpTools.writelog_waimai("解析ELM部分退款goodsList节点点失败：" + e.getMessage());
										continue;
									}

								}

								orderDB.setGoodsList(goodsArray_PartRefund);
							}


						}
						catch (Exception e)
						{

							HelpTools.writelog_waimai("添加ELM部分退款goodsList节点点失败：" + e.getMessage());
						}
					}
				}
			}



			orderDB.setStatus(status);
			orderDB.setRefundStatus(refundStatus);
			orderDB.setRefundReason(refundReason);
			
								
			if (status.equals("11") || status.equals("3") || status.equals("12"))
			{
				redis.DeleteHkey(redis_key, hash_key);//
				String sss = "订单已完成";
				boolean isUpdateRQTY = false;//是否更新
				if (status.equals("3"))
				{
					sss = "订单已取消";
					isUpdateRQTY = true;
				} else if (status.equals("12"))
				{
					sss = "订单已退款";
					isUpdateRQTY = true;
				}

				if (isUpdateRQTY)
				{
					if (orderDB.getGoodsList()!=null&&orderDB.getGoodsList().isEmpty()==false)
					{
						for (orderGoodsItem goodsItem : orderDB.getGoodsList())
						{
							goodsItem.setrQty(goodsItem.getQty());
						}
						HelpTools.writelog_waimai("【外卖取消或退单状态时】更新订单商品明细缓存里面rqty");
					}

				}

				HelpTools.writelog_waimai(redis_key + "(" + hash_key + sss + ")【ELM删除缓存】成功！");
			}

			Response_json = pj.beanToJson(orderDB);
			
			order dcpOrder = orderDB;
			
			String eId = dcpOrder.geteId();
			String shopNo = dcpOrder.getShopNo();
			
			redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" +eId + ":" + shopNo;
			if (shopNo != null && shopNo.trim().length() > 0)
			{
				if (status.equals("3") || status.equals("12"))
				{
					HelpTools.setWaiMaiOrderToSaleOrRefundRedisLock("1",eId,orderno);
				}
				/*boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
				if (isexistHashkey)
				{
					redis.DeleteHkey(redis_key, hash_key);//
					HelpTools.writelog_waimai(
							"【ELM删除存在hash_key的缓存ELM】成功！" + " redis_key:" + redis_key + " hash_key:" + hash_key);
				}*/
				if (elmMessageType==12||elmMessageType==18)
				{
					//已接单、已完成状态不在写缓存
				}
				else
				{
					HelpTools.writelog_waimai("【ELM开始写缓存】" + " redis_key:" + redis_key + " hash_key:" + hash_key
							+ " hash_value:" + Response_json);
					boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
					if (nret)
					{
						HelpTools.writelog_waimai("【ELM写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
					} else
					{
						HelpTools.writelog_waimai("【ELM写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
					}
				}


			}
			
			return dcpOrder;
		} catch (Exception e)
		{
			HelpTools.writelog_waimai("ELM更新缓存中饿了么内容异常！" + e.getMessage() + " OrderNO:" + orderno);
			return null;
		}

	}

	// 存数据库
	private void SaveOrder(order dcpOrder) throws Exception
	{
		if(dcpOrder==null)
		{
			return;
		}
		
		String orderNo = dcpOrder.getOrderNo();	
		try
		{						
			String orderstatus = dcpOrder.getStatus();// 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 9.待配送 10.已发货 11.已完成 12.已退单
								
			String refundStatus = dcpOrder.getRefundStatus();
			
			String eId = dcpOrder.geteId();
			String shopNo = dcpOrder.getShopNo();			
			String loadDocType = dcpOrder.getLoadDocType();
			
			if (orderstatus != null)
			{
				if (orderstatus.equals("1")) // 插入
				{
					StringBuffer errorMessage = new StringBuffer();
					List<order> orderList = new ArrayList<order>();					
					orderList.add(dcpOrder);
					ArrayList<DataProcessBean> DPB = HelpTools.GetInsertOrderCreat(orderList, errorMessage,null);
					if (DPB != null && DPB.size() > 0)
					{
						for (DataProcessBean dataProcessBean : DPB)
						{
							this.addProcessData(dataProcessBean);
						}
						try
						{
							this.doExecuteDataToDB();
							HelpTools.writelog_waimai("【ELM保存数据库成功】" + " 订单号orderNo:" + orderNo);

							//商品资料异常
							HelpTools.waimaiOrderAbnormalSave(dcpOrder, errorMessage);

						}
						catch (Exception e)
						{
							this.pData.clear();
							// 如果保存异常了，判断数据库有没有，如果有了，就相当于重复推送了
						}

						// 写订单日志
						List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
						orderStatusLog onelv1 = new orderStatusLog();
						onelv1.setLoadDocType(loadDocType);
						onelv1.setChannelId(dcpOrder.getChannelId());
						onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
						onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
						onelv1.seteId(eId);
						String opNO = "";
						String o_opName = "饿了么用户";



						onelv1.setOpName(o_opName);
						onelv1.setOpNo(opNO);
						onelv1.setShopNo(shopNo);
						onelv1.setOrderNo(orderNo);
						onelv1.setMachShopNo(dcpOrder.getMachShopNo());
						onelv1.setShippingShopNo(dcpOrder.getShippingShopNo());
						String statusType = "";
						String updateStaus = orderstatus;
						statusType = "1";// 订单状态
						onelv1.setStatusType(statusType);
						onelv1.setStatus(updateStaus);
						StringBuilder statusTypeNameObj = new StringBuilder();
						String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
						String statusTypeName = statusTypeNameObj.toString();
						onelv1.setStatusTypeName(statusTypeName);
						onelv1.setStatusName(statusName);

						String memo = "";
						memo += statusName;
						if ("Y".equals(dcpOrder.getDowngraded()))
						{
							memo +="<br>降级订单";
						}
						onelv1.setMemo(memo);
						onelv1.setDisplay("1");

						String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
						onelv1.setUpdate_time(updateDatetime);

						orderStatusLogList.add(onelv1);

						StringBuilder errorStatusLogMessage = new StringBuilder();
						boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
						if (nRet) {
							HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
						} else {
							HelpTools.writelog_waimai(
									"【写表tv_orderStatuslog异常】" + errorStatusLogMessage.toString() + " 订单号orderNO:" + orderNo);
						}
						this.pData.clear();


					}

				} else// 更新 数据库状态
				{
					HelpTools.writelog_waimai("【ELM开始更新数据库】" + " 订单号orderNo:" + orderNo + " 订单状态status=" + orderstatus);
					UpdateOrderStatus(dcpOrder);
				}

			}

		} catch (SQLException e)
		{
			HelpTools.writelog_waimai("【ELM执行语句】异常：" + e.getMessage() + " 订单号orderNo:" + orderNo);
		} catch (Exception e)
		{
			HelpTools.writelog_waimai("【ELM执行语句】异常：" + e.getMessage() + " 订单号orderNo:" + orderNo);
		}
	}

	private void DeleteRedis(String redis_key, String hash_key) throws Exception
	{
		try
		{
			RedisPosPub redis = new RedisPosPub();
			HelpTools.writelog_waimai("【开始删除缓存】" + " redis_key:" + redis_key + " hash_key:" + hash_key);
			redis.DeleteHkey(redis_key, hash_key);//
			HelpTools.writelog_waimai("【删除存在hash_key的缓存】成功！" + " redis_key:" + redis_key + " hash_key:" + hash_key);
			// redis.Close();

		} catch (Exception e)
		{
			HelpTools.writelog_waimai(
					"【删除存在hash_key的缓存】异常" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
		}
	}

	

	private void UpdateOrderShippingStatus(int messageType, String order_message, long shopId)
	{
		try
		{

			ODeliveryRecord orderShipping = JacksonUtils.json2pojo(order_message, ODeliveryRecord.class);

			if (orderShipping == null)
			{
				return;
			}
			String deliveryStatus = "0";// -1预下单 0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常
										// 5=手动撤销 6 到店 7重下单
			if (messageType == 53) // 53已分配给配送员，配送员取餐中
			{
				deliveryStatus = "1";
			} else if (messageType == 54) // 54 配送员已经到店
			{
				deliveryStatus = "6";
			} else if (messageType == 55) // 55 配送员已取餐，配送中
			{
				deliveryStatus = "2";
			} else if (messageType == 56) // 56 配送成功
			{
				deliveryStatus = "3";
			} else // 57 58 59配送单已取消
			{
				deliveryStatus = "4";
			}

			String orderNo = orderShipping.getOrderId();// 订单号
			String dispatcherName = orderShipping.getDeliverName();// 配送员
			String dispatcherMobile = orderShipping.getDeliverPhone();// 配送员电话
			String loadDocType = orderLoadDocType.ELEME;

			JSONObject obj = new JSONObject(order_message);
			dispatcherName = obj.get("name").toString();// 配送员
			dispatcherMobile = obj.get("phone").toString();// 配送员电话

			UptBean ub1 = null;
			ub1 = new UptBean("DCP_ORDER");
			ub1.addUpdateValue("DELIVERYSTATUS", new DataValue(deliveryStatus, Types.VARCHAR));
			ub1.addUpdateValue("DELNAME", new DataValue(dispatcherName, Types.VARCHAR));
			if (dispatcherMobile != null && dispatcherMobile.trim().length() > 0)
			{
				ub1.addUpdateValue("DELTELEPHONE", new DataValue(dispatcherMobile, Types.VARCHAR));
			}

			ub1.addUpdateValue("UPDATE_TIME", new DataValue(
					new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			ub1.addUpdateValue("TRAN_TIME", new DataValue(
					new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
			ub1.addCondition("LOADDOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

			this.doExecuteDataToDB();
			HelpTools.writelog_waimai(
					"【ELM更新配送状态DeliveryStutas成功】" + " 订单号orderNO:" + orderNo + " 配送状态DeliveryStutas=" + deliveryStatus);

			String eId = "99";
			String shopNo = "";
			String channelId = "";
			Map<String, String>	mappingShopMap = HelpTools.GetELMMappingShop(Long.toString(shopId));
			eId = mappingShopMap.get("eId");
			shopNo = mappingShopMap.get("erpShopNo");
			channelId = mappingShopMap.get("channelId");

			if (eId == null || eId.isEmpty())
			{
				eId = "99";
			}

			// region 写日志
			try
			{
				
				List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
				orderStatusLog onelv1 = new orderStatusLog();
				onelv1.setLoadDocType(loadDocType);
				onelv1.setChannelId(channelId);
				onelv1.setLoadDocBillType("");
				onelv1.setLoadDocOrderNo(orderNo);
				onelv1.seteId(eId);
				String opNO = "";				
				String o_opName = "骑士：" + dispatcherName;
				

				onelv1.setOpName(o_opName);
				onelv1.setOpNo(opNO);				
				onelv1.setShopNo(shopNo);
				onelv1.setOrderNo(orderNo);
				onelv1.setMachShopNo(shopNo);
				onelv1.setShippingShopNo(shopNo);
				String statusType = "2";//配送状态
				String updateStaus = deliveryStatus;
							
				onelv1.setStatusType(statusType);
				onelv1.setStatus(updateStaus);
				StringBuilder statusTypeNameObj = new StringBuilder();
				String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
				String statusTypeName = statusTypeNameObj.toString();
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);
				
				String memo = "";
				memo += statusName;

				if (dispatcherMobile != null && dispatcherMobile.isEmpty() == false)
				{
					memo += " 配送电话-->" + dispatcherMobile;
				}
				onelv1.setMemo(memo);
				onelv1.setDisplay("1");

				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);

				orderStatusLogList.add(onelv1);
				
				StringBuilder errorStatusLogMessage = new StringBuilder();
				boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
				if (nRet) {
					HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
				} else {
					HelpTools.writelog_waimai(
							"【写表tv_orderStatuslog异常】" + errorStatusLogMessage.toString() + " 订单号orderNO:" + orderNo);
				}
				this.pData.clear();
							
				// endregion

			} 
			catch (Exception e)
			{

			}
			// endregion

		} 
		catch (Exception e2)
		{
			//// redis.Close();
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(String req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(String req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(String req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	private void UpdateOrderStatus(order dcpOrder) throws Exception
	{
		if(dcpOrder==null)
		{
			return;
		}
		String orderNo = dcpOrder.getOrderNo();
		try
		{			
			//JSONObject obj = new JSONObject(req);
			String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			ParseJson pj = new ParseJson();
			
			String status = dcpOrder.getStatus();
			String refundStatus = dcpOrder.getRefundStatus();			
			String eId = dcpOrder.geteId();
			String shopNo = dcpOrder.getShopNo();			
			String loadDocType = dcpOrder.getLoadDocType();

			String reason = dcpOrder.getRefundReason();// 退单原因
			
			if (reason != null && reason.length() > 255)
			{
				reason = reason.substring(0, 255);
			}

			double partRefundAmt = 0;
			partRefundAmt = dcpOrder.getRefundAmt();
			
			Map<String, Object> orderHead = new HashMap<String, Object>();
			boolean IsExistOrder = IsExistOrder(eId, orderNo, orderHead);

			String status_DB = "";
			String refundStatus_DB = "";
			try
			{
				status_DB = orderHead.get("STATUS").toString();
				refundStatus_DB = orderHead.get("REFUNDSTATUS").toString();
				if (shopNo == null || shopNo.trim().isEmpty() || shopNo.trim().length() == 0)
				{
					shopNo = orderHead.get("SHOP").toString();					
				}
			} 
			catch (Exception e)
			{

			}

			if (IsExistOrder) // 存在就Update
			{
				if (refundStatus.equals("2") || refundStatus.equals("7")) // 申请了退款，或者部分退款
				{
					try
					{
						/*
						 * String status_DB =orderHead.get("STATUS").toString();
						 * String refundStatus_DB
						 * =orderHead.get("REFUNDSTATUS").toString();
						 */
						if (refundStatus.equals("2"))
						{
							if (refundStatus_DB.equals("6") || refundStatus_DB.equals("3") || refundStatus_DB.equals("5"))
							{
								HelpTools.writelog_waimai("【ELM退单重复推送退单申请】" + " 订单号orderNo:" + orderNo + " 订单状态status="
										+ status + " 退单状态refundStatus=" + refundStatus);
								// 已经处理了的退款，删除缓存
								String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
								String hash_key = orderNo;
								
								if (refundStatus_DB.equals("6")) // 不能直接删缓存，否则pos收不到退单消息																	// 
								{
									//obj.put("status", "12");
									//obj.put("refundStatus", refundStatus_DB);
									
									dcpOrder.setStatus("12");
									dcpOrder.setRefundStatus(refundStatus_DB);
									if (dcpOrder.getGoodsList()!=null&&dcpOrder.getGoodsList().isEmpty()==false)
									{
										for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
										{
											goodsItem.setrQty(goodsItem.getQty());
										}
										HelpTools.writelog_waimai("【外卖取消或退单状态时】更新订单商品明细缓存里面rqty");
									}
									String hash_value = pj.beanToJson(dcpOrder);
									WriteRedis(redis_key, hash_key, hash_value);

								} 
								else
								{
									DeleteRedis(redis_key, hash_key);
								}
								return;
							}

						} else
						{
							if (refundStatus_DB.equals("10") || refundStatus_DB.equals("8") || refundStatus_DB.equals("9"))
							{
								// 已经处理了的部分退款，删除缓存
								HelpTools.writelog_waimai("【ELM部分退单重复推送部分退单申请】" + " 订单号orderNO:" + orderNo
										+ " 订单状态status=" + status + " 退单状态refundStatus=" + refundStatus);
								String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
								String hash_key = orderNo;
								// DeleteRedis(redis_key,hash_key);
								if (refundStatus_DB.equals("10")) // 这时候
																	// 不能直接删缓存，否则pos收不到退单消息
								{
									//obj.put("refundStatus", refundStatus_DB);
									dcpOrder.setRefundStatus(refundStatus_DB);

									String hash_value = pj.beanToJson(dcpOrder);
									WriteRedis(redis_key, hash_key, hash_value);

								} else
								{
									DeleteRedis(redis_key, hash_key);
								}
								return;
							}

						}

					} catch (Exception e)
					{

					}

				}

				UptBean ub1 = null;
				ub1 = new UptBean("DCP_ORDER");
				ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));

				// 部分退单，最后还推送已完成订单refundStatus=1，如果数据库已经是退单成功状态,refundStatus就不更新了
				if (refundStatus_DB != null && refundStatus_DB.equals("10")) //
				{

				} else
				{
					ub1.addUpdateValue("REFUNDSTATUS", new DataValue(refundStatus, Types.VARCHAR));
				}

				if (refundStatus.equals("2") || refundStatus.equals("7"))
				{
					HelpTools.writelog_fileName("【饿了么】申请退款更新数据，单号orderNO=" + orderNo + " 退货原因refundReason=" + reason,
							"refunReasonLog");
					HelpTools.writelog_waimai("【饿了么】申请退款更新数据，单号orderNO=" + orderNo + " 退货原因refundReason=" + reason);
					ub1.addUpdateValue("REFUNDREASON", new DataValue(reason, Types.VARCHAR));
				}

				if (refundStatus.equals("10"))
				{
					ub1.addUpdateValue("REFUNDAMT", new DataValue(partRefundAmt, Types.VARCHAR));// 这样存就不会出现float小数点问题
					ub1.addUpdateValue("REFUNDAMT_MERRECEIVE", new DataValue(partRefundAmt,Types.VARCHAR));
					ub1.addUpdateValue("REFUNDAMT_CUSTPAYREAL", new DataValue(partRefundAmt,Types.VARCHAR));
					//ub1.addUpdateValue("REFUNDREASON", new DataValue(reason,Types.VARCHAR));
					ub1.addUpdateValue("LASTREFUNDTIME",
							new DataValue(
									new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()),
									Types.VARCHAR));
				}

				ub1.addUpdateValue("UPDATE_TIME",
						new DataValue(
								new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()),
								Types.VARCHAR));
				ub1.addUpdateValue("TRAN_TIME", new DataValue(
						new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
				if(status.equals("11")||status.equals("3")||status.equals("12"))
				{
					ub1.addUpdateValue("COMPLETE_DATETIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					if(status.equals("3")||status.equals("12"))
					{
						ub1.addUpdateValue("LASTREFUNDTIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					}
				}
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));			
				ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
				
				this.addProcessData(new DataProcessBean(ub1));

				this.doExecuteDataToDB();
				HelpTools.writelog_waimai("【更新STATUS成功】 " + " 订单号orderNO:" + orderNo + " 订单状态status=" + status);
				// 同意部分退款后，再插入退款的明细商品 不用放在同一个事务里面
				if (refundStatus.equals("10"))
				{
					HelpTools.writelog_waimai("【ELM部分退单新增单身开始】" + " 订单号orderNO:" + orderNo + " 订单状态status=" + status);
					boolean isExist = false;
					String curDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					try
					{
						
						StringBuffer error_partRefundGoods = new StringBuffer();
						List<orderGoodsItem> goodsList_partRefund = HelpTools.waimaiPartRefundGoodsProcess(dcpOrder, error_partRefundGoods);//真正的转化出来的部分退单单身(包含套餐商品处理)
												
						//更新原单
						for (orderGoodsItem goodsItem : goodsList_partRefund) 
						{
							try 
							{
												
								String oItem = goodsItem.getoItem();//原单对应的商品项次
																
								double qty = goodsItem.getQty();
																
								//先简单处理
								String execsql = "update DCP_ORDER_detail set rqty="+qty+""
										+ " where rownum =1 and eid='"+eId+"' and orderno='"+orderNo+"' and item="+oItem+"  and qty>="+qty;
								
								HelpTools.writelog_waimai("【部分退单成功后】更新原单单身已退数量sql="+execsql+",订单号orderNo="+orderNo+",原单项次item="+oItem);
								ExecBean exBean = new ExecBean(execsql);
								this.addProcessData(new DataProcessBean(exBean));																							
								isExist = true;
										
							} 
							catch (Exception e) 
							{
								HelpTools.writelog_waimai("【开始保存数据库】添加部分退单单身异常："+e.getMessage()+"(eid="+eId+" shopno="+shopNo+" orderNo="+orderNo+")");
								continue;
							}			
						}	
											
						
						if (isExist)
						{
							this.doExecuteDataToDB();
							HelpTools.writelog_waimai("【ELM部分退单单身添加执行语句成功】" + " 订单号orderNO:" + orderNo + " 订单状态status="
									+ status + " 退单状态refundStatus=" + refundStatus);
							StringBuffer error_partRefund = new StringBuffer();
							HelpTools.waimaiOrderPartRefundProcess(dcpOrder, goodsList_partRefund, "", error_partRefund);
						} 
						else
						{
							HelpTools.writelog_waimai("【ELM部分退单单身为空！】" + " 订单号orderNO:" + orderNo + " 订单状态status="
									+ status + " 退单状态refundStatus=" + refundStatus);
						}

					} 
					catch (SQLException e)
					{
						HelpTools.writelog_waimai("【ELM部分退单新增单身执行语句】异常：" + e.getMessage() + " 订单号orderNO:" + orderNo );
					} catch (Exception e)
					{
						// TODO: handle exception
						HelpTools.writelog_waimai("【ELM部分退单新增单身执行语句】异常：" + e.getMessage() + " 订单号orderNO:" + orderNo );
					}
				}
				
				

				//推送取消或者退单消息时，执行下。
				if(status.equals("3")||status.equals("12"))
				{
					StringBuffer RefundOrCancelError = new StringBuffer();
					HelpTools.OrderRefundOrCancelProcess(dcpOrder, sdate, RefundOrCancelError);
				}

			} 
			else
			{
				HelpTools.writelog_waimai("【更新的单据不存在】开始插入到数据库" + " 订单号orderNO:" + orderNo + " 订单状态status=" + status);
				// 如果不存在，接口查询下订单详情
				// 查询下当前门店的对应的饿了么APPKEY
				Map<String, Object> mapAppKey = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, eId, shopNo,loadDocType,dcpOrder.getOrderShop());
				Boolean isGoNewFunction = false;// 是否走新的接口
				String elmAPPKey = "";
				String elmAPPSecret = "";
				String elmAPPName = "";
				boolean elmIsSandbox = false;
				String isJBP = "";//鼎捷服务商模式
				String userId = "";
				if (mapAppKey != null)
				{
					elmAPPKey = mapAppKey.get("APPKEY").toString();
					elmAPPSecret = mapAppKey.get("APPSECRET").toString();
					elmAPPName = mapAppKey.get("APPNAME").toString();
					String elmIsTest = mapAppKey.get("ISTEST").toString();
					if (elmIsTest != null && elmIsTest.equals("Y"))
					{
						elmIsSandbox = true;
					}
					isGoNewFunction = true;
					isJBP = mapAppKey.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
					userId = mapAppKey.getOrDefault("USERID","").toString();
				}

				StringBuilder errorMessage = new StringBuilder();
				HelpTools.writelog_waimai(
						"【更新的单据不存在】开始调用ELM订单获取接口" + " 订单号orderNO:" + orderNo + " 订单状态status=" + status);
				OOrder elmOrder = null;
				if (isGoNewFunction)
				{
					if ("Y".equals(isJBP))
					{
						elmOrder = WMELMProductService.getOrder(userId,elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo,
								errorMessage);
					}
					else
					{
						elmOrder = WMELMProductService.getOrder(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo,
								errorMessage);
					}

				} else
				{
					//elmOrder = WMELMProductService.getOrder(orderNo, errorMessage); //不在支持配置文件
				}
				String elmOrderJson = "";
				if (elmOrder != null)
				{
				  	order getOrderByOnline = NewOrderProcess(elmOrder, status, refundStatus);// 这个方法里面都是新建的订单									
					getOrderByOnline.setStatus(status);
					getOrderByOnline.setRefundStatus(refundStatus);
					getOrderByOnline.seteId(eId);
					getOrderByOnline.setShopNo(shopNo);
					getOrderByOnline.setRefundReason(reason);
					
					elmOrderJson = pj.beanToJson(getOrderByOnline);
					
					
					HelpTools.writelog_waimai("【更新的单据不存在】开始调用ELM订单获取接口" + " 订单号orderNO:" + orderNo + " 订单状态status=" + status
							+ " 返回：" + elmOrderJson);
					HelpTools.writelog_waimai(
							"【更新的单据不存在】获取订单详情后重新开始插入到数据库" + " 订单号orderNO:" + orderNo + " 订单状态status=" + status);
					
					List<order> orderList = new ArrayList<order>();
					orderList.add(getOrderByOnline);
					StringBuffer insertMessage = new StringBuffer();
					ArrayList<DataProcessBean> DPB = HelpTools.GetInsertOrderCreat(orderList, insertMessage,null);
					if (DPB != null && DPB.size() > 0)
					{
						for (DataProcessBean dataProcessBean : DPB)
						{
							this.addProcessData(dataProcessBean);
						}

						try
						{
							this.doExecuteDataToDB();
							HelpTools.writelog_waimai(
									"【更新的单据不存在】插入数据库成功" + " 订单号orderNO:" + orderNo + " 订单状态status=" + status);
							
							//商品资料异常
							HelpTools.waimaiOrderAbnormalSave(getOrderByOnline, insertMessage);

						} catch (SQLException e)
						{
							this.pData.clear();
							HelpTools.writelog_waimai("【更新的单据不存在】插入数据库异常：" + e.getMessage() + "\r\n req请求内容:" + elmOrderJson);
						} catch (Exception e)
						{
							this.pData.clear();
							HelpTools.writelog_waimai("【更新的单据不存在】插入数据库异常：" + e.getMessage() + "\r\n req请求内容:" + elmOrderJson);

						}
						finally {
							//如果插入失败了，可能是在三方外卖APP上开启了自动接单，先推送接单，后推送开立。
							//这时候更新下即可
							if ("2".equals(status))
							{
								this.pData.clear();
								UptBean ub1 = null;
								ub1 = new UptBean("DCP_ORDER");
								ub1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));
								ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
								ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

								ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
								ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
								ub1.addCondition("STATUS", new DataValue("1",Types.VARCHAR));//只更新开立的时候，其他不能更新
								this.addProcessData(new DataProcessBean(ub1));

								this.doExecuteDataToDB();
								HelpTools.writelog_waimai("【ELM更新STATUS成功】"+" 订单号orderNO:"+orderNo+" 订单状态status="+status+" 退单状态refundStatus="+refundStatus);

							}
						}

					}
				}
				
				

			}

			// region写订单日志
			try
			{
				
				List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
				orderStatusLog onelv1 = new orderStatusLog();
				onelv1.setLoadDocType(loadDocType);
				onelv1.setChannelId(dcpOrder.getChannelId());
				onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
				onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
				onelv1.seteId(eId);
				String opNO = "";
				String o_opName = "饿了么用户";
				
				onelv1.setOpName(o_opName);
				onelv1.setOpNo(opNO);				
				onelv1.setShopNo(shopNo);
				onelv1.setOrderNo(orderNo);
				onelv1.setMachShopNo(dcpOrder.getMachShopNo());
				onelv1.setShippingShopNo(dcpOrder.getShippingShopNo());
				String statusType = "";
				String updateStaus = status;
				statusType = "1";// 订单状态				
				onelv1.setStatusType(statusType);
				onelv1.setStatus(updateStaus);
				StringBuilder statusTypeNameObj = new StringBuilder();
				String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
				String statusTypeName = statusTypeNameObj.toString();
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);

				String memo = "";
				memo += statusName;
				onelv1.setMemo(memo);
				onelv1.setDisplay("1");

				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);

				//orderStatusLogList.add(onelv1); //这里需要注意（先不添加，没有申请退单在添加，因为申请退单之前的订单状态已经存了)，
												
				// 如果有申请退单
				if (refundStatus.equals("2") || refundStatus.equals("7"))
				{
					orderStatusLog onelv2 = new orderStatusLog();
					onelv2.setLoadDocType(loadDocType);
					onelv2.setChannelId(dcpOrder.getChannelId());
					onelv2.setLoadDocBillType(dcpOrder.getLoadDocBillType());
					onelv2.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
					onelv2.seteId(eId);				
					onelv2.setOpName(o_opName);
					onelv2.setOpNo(opNO);				
					onelv2.setShopNo(shopNo);
					onelv2.setOrderNo(orderNo);
					onelv2.setMachShopNo(dcpOrder.getMachShopNo());
					onelv2.setShippingShopNo(dcpOrder.getShippingShopNo());
				
					statusType = "3";// 退单状态	
					updateStaus = refundStatus;
					onelv2.setStatusType(statusType);
					onelv2.setStatus(updateStaus);
					statusTypeNameObj = new StringBuilder();
					statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
					statusTypeName = statusTypeNameObj.toString();
					onelv2.setStatusTypeName(statusTypeName);
					onelv2.setStatusName(statusName);

					memo = "";
					memo += statusName;
					onelv2.setMemo(memo);
					onelv2.setDisplay("1");

					updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					onelv2.setUpdate_time(updateDatetime);
					
					orderStatusLogList.add(onelv2);

					
				} 
				else
				{
					orderStatusLogList.add(onelv1);
				}

				StringBuilder errorStatusLogMessage = new StringBuilder();
				boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
				if (nRet) {
					HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNo:" + orderNo);
				} else {
					HelpTools.writelog_waimai(
							"【写表tv_orderStatuslog异常】" + errorStatusLogMessage.toString() + " 订单号orderNO:" + orderNo);
				}
			} catch (Exception e)
			{

			}
			// endregion

		} 
		catch (SQLException e)
		{
			this.pData.clear();
			HelpTools.writelog_waimai("【执行语句】异常：" + e.getMessage() + " 订单号orderNo:" + orderNo);
		} 
		catch (Exception e)
		{
			this.pData.clear();
			HelpTools.writelog_waimai("【执行语句】异常：" + e.getMessage() + " 订单号orderNo:" + orderNo);
		}

	}

	/**
	 * 查询下订单是否存在，返回订单状态信息
	 * @param eid
	 * @param orderNO
	 * @param orderHead
	 * @return
	 * @throws Exception
	 */
	private boolean IsExistOrder(String eid,  String orderNO,Map<String, Object> orderHead) throws Exception
	{
		boolean nRet = false;

		String sql = "select * from DCP_order ";
		sql += " where ORDERNO='" + orderNO + "' and EID='" + eid + "'";
		
		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);

		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			nRet = true;
			String status = getQDataDetail.get(0).get("STATUS").toString();
			String refundStatus = getQDataDetail.get(0).get("REFUNDSTATUS").toString();
			String shop = getQDataDetail.get(0).get("SHOP").toString();
			String channelId = getQDataDetail.get(0).get("CHANNELID").toString();
			orderHead.put("STATUS", status);
			orderHead.put("REFUNDSTATUS", refundStatus);
			orderHead.put("SHOP", shop);
			orderHead.put("CHANNELID", channelId);
			
		}
		return nRet;

	}

	private String GetOrderInfo(String eid, String shopNo,  String orderNo,
			String loadDocType) throws Exception
	{
		try
		{			
		  order dcpOrder =	HelpTools.GetOrderInfoByOrderNO(this.dao, eid, loadDocType, orderNo);
			
			ParseJson pj = new ParseJson();
			String res = pj.beanToJson(dcpOrder);
			
			return res;
			
		}

		catch (Exception e)
		{
			HelpTools.writelog_waimai("【饿了么】申请部分退款orderNo=" + orderNo + "查询数据库异常：" + e.getMessage());
			return "";
		}

	}

	/*
	 * 是否开启自动发快递JOB
	 */
	private boolean isOpenExpressOrderJob() throws Exception
	{
		boolean nRet = false;
		try
		{
			if (this.dao == null)
			{
				//this.dao = SPosDbPoolDAOImp.getDao();
			}

			String sql = "select * from job_quartz where job_name='ExpressOrderCreate' and CNFFLG='Y'";
			List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				nRet = true;
			}

		} catch (Exception e)
		{

		}
		return nRet;
	}

	/*
	 * 判断下订单来源饿了么有没有设置自动发快递
	 */
	private boolean isSetAutoExpress(String companyNo, String shop) throws Exception
	{
		boolean nRet = false;
		try
		{
			if (this.dao == null)
			{
				//this.dao = SPosDbPoolDAOImp.getDao();
			}

			String sql = "select A.SHOP from TA_SHOP_ORDERTAKESET A inner join ta_outsaleset B on A.COMPANYNO=B.COMPANYNO and A.SHOP=B.SHOP and A.CNFFLG=B.CNFFLG ";
			sql += " where A.IS_AUTO_EXPRESS='Y' and A.load_doctype='1' and A.CNFFLG='Y' and A.companyno='" + companyNo
					+ "' and A.SHOP='" + shop + "' ";
			List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				nRet = true;
			}

		} catch (Exception e)
		{

		}
		return nRet;
	}

	private void WriteRedis(String redis_key, String hash_key, String hash_value) throws Exception
	{
		try
		{
			RedisPosPub redis = new RedisPosPub();
			HelpTools.writelog_waimai(
					"【开始写缓存】" + " redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + hash_value);
			//redis.DeleteHkey(redis_key, hash_key);//
			boolean nret = redis.setHashMap(redis_key, hash_key, hash_value);
			if (nret)
			{
				HelpTools.writelog_waimai("【ELM写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
			} else
			{
				HelpTools.writelog_waimai("【ELM写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
			}
			// redis.Close();

		} catch (Exception e)
		{
			HelpTools.writelog_waimai(
					"【开始写缓存】异常" + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
		}
	}

	private class thread_Monitor_HY implements Callable
	{
		order dcpOrder;
		StringBuilder errorMessage;
		DsmDAO dao;

		thread_Monitor_HY(DsmDAO dao,order dcpOrder,StringBuilder errorMessage)
		{
		    this.dao = dao;
			this.dcpOrder = dcpOrder;

			this.errorMessage = errorMessage;
		}

		@Override
		public Object call() throws Exception
		{
			//开始多线程
            String orderNo = dcpOrder.getOrderNo();
            String eId = dcpOrder.geteId();
            String shopNo = dcpOrder.getShopNo();
            String loadDocType = dcpOrder.getLoadDocType();
			HelpTools.writelog_waimai("【饿了么】【降级订单】在线查询订单多线程开始,单号orderNo="+orderNo+"，eId="+eId+"，shopNo="+shopNo+"，loadDocType="+loadDocType);

            // 如果不存在，接口查询下订单详情
            // 查询下当前门店的对应的饿了么APPKEY
            Map<String, Object> mapAppKey = PosPub.getWaimaiAppConfigByShopNO_New(dao, eId, shopNo,loadDocType,dcpOrder.getOrderShop());
            String elmAPPKey = "";
            String elmAPPSecret = "";
            String elmAPPName = "";
            boolean elmIsSandbox = false;
			String isJBP = "";
			String userId = "";
            if (mapAppKey != null)
            {
                elmAPPKey = mapAppKey.get("APPKEY").toString();
                elmAPPSecret = mapAppKey.get("APPSECRET").toString();
                elmAPPName = mapAppKey.get("APPNAME").toString();
                String elmIsTest = mapAppKey.get("ISTEST").toString();
                if (elmIsTest != null && elmIsTest.equals("Y"))
                {
                    elmIsSandbox = true;
                }
				isJBP = mapAppKey.getOrDefault("ISJBP","").toString();//鼎捷服务商模式
				userId = mapAppKey.getOrDefault("USERID","").toString();

            }
            else
            {
                HelpTools.writelog_waimai("【饿了么】【降级订单】在线查询订单，所需要得参数值为空,单号orderNo="+orderNo);
                return null ;
            }

            StringBuilder errorMessage = new StringBuilder();
            OOrder elmOrder = null;

            String elmOrderJson = "";
			// TODO Auto-generated method stub
			long waitime = 15000;
			int loopCount = 10;
			for (int i =1;i<=loopCount;i++)
			{
				long cur_waitime = i*waitime;
				Thread.sleep(cur_waitime);
				HelpTools.writelog_waimai("【降级订单】第【"+i+"】次查询开始，单号orderNo="+orderNo);
				try
                {
                    elmOrder = null;
                    if ("Y".equals(isJBP))
					{
						elmOrder = WMELMProductService.getOrder(userId,elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo,
								errorMessage);
					}
                    else
					{
						elmOrder = WMELMProductService.getOrder(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo,
								errorMessage);
					}

                    if (elmOrder==null)
                    {
                        HelpTools.writelog_waimai("【降级订单】第【"+i+"】次查询完成，返回订单数据为空,单号orderNo="+orderNo);
                        continue;
                    }

                    boolean downgraded = elmOrder.getDowngraded();
                    HelpTools.writelog_waimai("【降级订单】第【"+i+"】次查询完成，【降级标识】downgraded="+downgraded+",单号orderNo="+orderNo);
                   //降级标识为false时，无须查询
                    if (!downgraded)
                    {
                        break;
                    }

                }
				catch (Exception e)
                {
                    HelpTools.writelog_waimai("【降级订单】第【"+i+"】次查询异常:"+e.getMessage()+"，单号orderNo="+orderNo);
                    continue;
                }
			}

			if (elmOrder==null)
            {
                HelpTools.writelog_waimai("【饿了么】【降级订单】在线查询订单多线程结束,查询订单数据为空，单号orderNo="+orderNo+"，eId="+eId+"，shopNo="+shopNo+"，loadDocType="+loadDocType);
                return null;
            }
            OOrderStatus elmStatus =  elmOrder.getStatus();
			OOrderRefundStatus elmRefundSatus = elmOrder.getRefundStatus();
            HelpTools.writelog_waimai("【饿了么】【降级订单】在线查询订单多线程结束,查询订单数据完成，【降级标识】downgraded="+elmOrder.getDowngraded()+",外卖平台上订单状态="+elmStatus+",退单状态="+elmRefundSatus+"，单号orderNo="+orderNo);

			//查询10次完成之后，如果还是降级订单，那么，调用主动拒单接口
            //订单状态可分为pending（未生效订单）、unprocessed（未处理订单）、refunding（退单处理中）、valid（已处理的有效订单）、invalid（无效订单）、settled（已完成订单）六种
			//未生效订单指下单未支付的订单（这个目前已不支持从接口获取），无效订单是指用户下单后因某种原因导致订单无效（如：商家未接单、客户取消订单、商家拒单）。
            //开发平台参考：https://open.shop.ele.me/openapi/documents/9f84d92c-3d3b-4c92-bf4d-79773ff7c7a0
            if (elmOrder.getDowngraded())
            {
                //unprocessed（未处理订单）
                //表示未接单的。
                if (elmStatus == OOrderStatus.unprocessed)
                {
                    HelpTools.writelog_waimai("【饿了么】【降级订单】多次查询之后，还是降级订单且还未接单，调用主动拒单接口开始，单号orderNo="+orderNo);
					StringBuilder errorMeassge_cancel = new StringBuilder();
					if ("Y".equals(isJBP))
					{
						boolean nRet = WMELMOrderProcess.orderCancel(userId,elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo,
								refundReason, "", errorMeassge_cancel);
						HelpTools.writelog_waimai("【饿了么】【降级订单】多次查询之后，还是降级订单且还未接单，调用主动拒单接口结束，返回:"+nRet+"，单号orderNo="+orderNo);
					}
					else
					{
						boolean nRet = WMELMOrderProcess.orderCancel(elmIsSandbox, elmAPPKey, elmAPPSecret, elmAPPName, orderNo,
								refundReason, "", errorMeassge_cancel);
						HelpTools.writelog_waimai("【饿了么】【降级订单】多次查询之后，还是降级订单且还未接单，调用主动拒单接口结束，返回:"+nRet+"，单号orderNo="+orderNo);
					}

                }

            }
			else
            {
				String status ="1";
				String refundStatus ="1";
				String status_db = "";
				String refundStatus_db = "";
				//查询下数据库中订单状态
				String sql = "select * from dcp_order where eid='"+eId+"' and orderno='"+orderNo+"'";
				try
				{
					List<Map<String,Object>> orderMapList_db = dao.executeQuerySQL(sql,null);
					if (orderMapList_db!=null&&orderMapList_db.isEmpty()==false)
					{
						status_db = orderMapList_db.get(0).getOrDefault("STATUS","").toString();
						refundStatus_db = orderMapList_db.get(0).getOrDefault("REFUNDSTATUS","").toString();
					}

				}
				catch (Exception e)
				{

				}
				HelpTools.writelog_waimai("【降级订单】查询数据库中单据状态，订单状态status_db="+status_db+"，退单状态refundStatus_db="+refundStatus_db+"，单号orderNo="+orderNo);
				boolean needCompare = true;//是否需要取数据库中状态
				if (elmStatus == OOrderStatus.unprocessed)
				{
					needCompare = false;
					status ="1";
					refundStatus ="1";
				}
				else  if (elmStatus == OOrderStatus.valid)
				{
					//“已处理的有效订单”指订单已接单处于正常流转中的订单
					needCompare = false;
					status ="2";
					refundStatus ="1";
				}
				else  if (elmStatus == OOrderStatus.invalid)
				{
					//无效订单是指用户下单后因某种原因导致订单无效(商家未接单、客户取消订单、商家拒单)
					status ="3";
					refundStatus ="1";
				}
				else  if (elmStatus == OOrderStatus.settled)
				{
					//无效订单是指用户下单后因某种原因导致订单无效(商家未接单、客户取消订单、商家拒单)
					status ="11";
					refundStatus ="1";
				}

				if (needCompare&&!status_db.isEmpty()&&!refundStatus_db.isEmpty())
				{
					status = status_db;
					refundStatus = refundStatus_db;
				}

				HelpTools.writelog_waimai("【降级订单】单据状态经过对比，当前订单状态status="+status+"，退单状态refundStatus="+refundStatus+"，单号orderNo="+orderNo);
				if (status.equals("3")||status.equals("12"))
				{
					HelpTools.writelog_waimai("【降级订单】单据状态已取消/退单，就不在更新原单了(已经根据之前原单生成了退订单了)，为了上传ERP直接更新降级标识为N，单号orderNo="+orderNo);
					ArrayList<DataProcessBean> DPB_up = new ArrayList<>();
					UptBean up_order = new UptBean("DCP_ORDER");
					up_order.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					up_order.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));

					up_order.addUpdateValue("DOWNGRADED", new DataValue("N", Types.VARCHAR));
					up_order.addUpdateValue("UPDATE_TIME", new DataValue(
							new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					up_order.addUpdateValue("TRAN_TIME", new DataValue(
							new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					DPB_up.add(new DataProcessBean(up_order));
					try
					{
						dao.useTransactionProcessData(DPB_up);
						HelpTools.writelog_waimai("【降级订单】【更新保存数据库成功】，订单号orderNo:" + orderNo);
					}
					catch (Exception e)
					{

					}
					return null;
				}

				order order_new = NewOrderProcess(elmOrder, status, refundStatus);
				StringBuffer errorMessage_order = new StringBuffer();
				List<order> orderList = new ArrayList<order>();
				orderList.add(order_new);
				ArrayList<DataProcessBean> DPB_insert = HelpTools.GetInsertOrderCreat(orderList, errorMessage_order,null);
				if (DPB_insert != null && DPB_insert.size() > 0)
				{
					ArrayList<DataProcessBean> DPB = new ArrayList<>();
					DelBean del_order = new DelBean("DCP_ORDER");
					del_order.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					del_order.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
					DPB.add(new DataProcessBean(del_order));

					del_order = new DelBean("DCP_ORDER_DETAIL");
					del_order.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					del_order.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
					DPB.add(new DataProcessBean(del_order));

					del_order = new DelBean("DCP_ORDER_DETAIL_MEMO");
					del_order.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					del_order.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
					DPB.add(new DataProcessBean(del_order));

					del_order = new DelBean("DCP_ORDER_DETAIL_AGIO");
					del_order.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					del_order.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
					DPB.add(new DataProcessBean(del_order));

					del_order = new DelBean("DCP_ORDER_PAY_DETAIL");
					del_order.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					//del_order.addCondition("SOURCEBILLTYPE", new DataValue("Order", Types.VARCHAR));
					del_order.addCondition("SOURCEBILLNO", new DataValue(orderNo, Types.VARCHAR));
					DPB.add(new DataProcessBean(del_order));

					del_order = new DelBean("DCP_ORDER_PAY");
					del_order.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					//del_order.addCondition("SOURCEBILLTYPE", new DataValue("Order", Types.VARCHAR));
					del_order.addCondition("SOURCEBILLNO", new DataValue(orderNo, Types.VARCHAR));
					DPB.add(new DataProcessBean(del_order));

					del_order = new DelBean("DCP_STATISTIC_INFO");
					del_order.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					del_order.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
					DPB.add(new DataProcessBean(del_order));
					DPB.addAll(DPB_insert);
					try
					{
						dao.useTransactionProcessData(DPB);
						HelpTools.writelog_waimai("【降级订单】【更新保存数据库成功】，订单号orderNo:" + orderNo);

						//商品资料异常
						HelpTools.waimaiOrderAbnormalSave(order_new, errorMessage_order);

						// 写订单日志
						List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
						orderStatusLog onelv1 = new orderStatusLog();
						onelv1.setLoadDocType(loadDocType);
						onelv1.setChannelId(order_new.getChannelId());
						onelv1.setLoadDocBillType(order_new.getLoadDocBillType());
						onelv1.setLoadDocOrderNo(order_new.getLoadDocOrderNo());
						onelv1.seteId(eId);
						String opNO = "";
						String o_opName = "";

						onelv1.setOpName(o_opName);
						onelv1.setOpNo(opNO);
						onelv1.setShopNo(shopNo);
						onelv1.setOrderNo(orderNo);
						onelv1.setMachShopNo(dcpOrder.getMachShopNo());
						onelv1.setShippingShopNo(dcpOrder.getShippingShopNo());
						String statusType = "";
						String updateStaus = status;
						statusType = "1";// 订单状态
						onelv1.setStatusType(statusType);
						onelv1.setStatus(updateStaus);
						StringBuilder statusTypeNameObj = new StringBuilder();
						String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
						String statusTypeName = statusTypeNameObj.toString();
						onelv1.setStatusTypeName(statusTypeName);
						onelv1.setStatusName(statusName);

						String memo = "";
						memo += statusName;
						memo +="<br>降级订单在线查询完成";
						memo +="<br>降级订单-->正常订单";

						onelv1.setMemo(memo);
						onelv1.setDisplay("1");

						String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
						onelv1.setUpdate_time(updateDatetime);

						orderStatusLogList.add(onelv1);

						StringBuilder errorStatusLogMessage = new StringBuilder();
						boolean nRet = HelpTools.InsertOrderStatusLog(dao, orderStatusLogList, errorStatusLogMessage);
						if (nRet) {
							HelpTools.writelog_waimai("【写表tv_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
						} else {
							HelpTools.writelog_waimai(
									"【写表tv_orderStatuslog异常】" + errorStatusLogMessage.toString() + " 订单号orderNO:" + orderNo);
						}

					}
					catch (Exception e)
					{
						HelpTools.writelog_waimai("【降级订单】【更新保存数据库】异常:" +e.getMessage()+ "，订单号orderNo:" + orderNo);
					}


				}
            }

			return null;
		}

	}

}
