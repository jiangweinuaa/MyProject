package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.entity.*;
import org.apache.commons.codec.digest.DigestUtils;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderModify_PayAdd_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderModify_PayAdd_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

public class DCP_OrderModify_PayAdd_Open extends SPosAdvanceService<DCP_OrderModify_PayAdd_OpenReq,DCP_OrderModify_PayAdd_OpenRes>
{
	
	boolean bMemberPay=false;//调用会员支付标记
	String memberPayNo = "";
	@Override
	protected void processDUID(DCP_OrderModify_PayAdd_OpenReq req, DCP_OrderModify_PayAdd_OpenRes res) throws Exception {
		bMemberPay = false;
		memberPayNo = "";
		String eId = req.geteId();
		if (eId==null||eId.trim().isEmpty())
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取eId失败！");
		}
		String orderNo = req.getRequest().getOrderNo();
		String loadDocType = req.getRequest().getLoadDocType();
		if(loadDocType.equals(orderLoadDocType.ELEME)||loadDocType.equals(orderLoadDocType.MEITUAN)||loadDocType.equals(orderLoadDocType.JDDJ)||loadDocType.equals(orderLoadDocType.MTSG)||loadDocType.equals(orderLoadDocType.DYWM)||loadDocType.equals(orderLoadDocType.OWNCHANNEL))
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "不支持该类型订单！(loadDocType="+loadDocType+")");
		}
		//查询下数据库 原单信息
		order dcpOrder = HelpTools.GetOrderInfoByOrderNO(this.dao, eId, loadDocType, orderNo);
		if(dcpOrder==null)
		{
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PayAdd接口，订金增加】查询完成：该订单不存在！ 单号orderNo="+orderNo);
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "订单不存在！");
		}
		
		
		
		//【ID1037136】【阿哆诺斯3310】订单追加定金------订金追加支持券找零---服务  by jinzma 20231114
		if (!CollectionUtils.isEmpty(req.getRequest().getCouponChangeList())) {
			for (order.CouponChange couponChange : req.getRequest().getCouponChangeList()) {
				if (Check.Null(couponChange.getCouponCode())) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "找零券的券流水号不可为空值, ");
				}
				if (Check.Null(couponChange.getCouponType())) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "找零券的券类型不可为空值, ");
				}
				if (!PosPub.isNumeric(couponChange.getQuantity())) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "找零券的券数量不可为空值或非数值, ");
				}
				if (!PosPub.isNumericType(couponChange.getFaceAmount())) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "找零券的券金额不可为空值或非数值, ");
				}
				
				String sql = " SELECT EID,COUPONCODE,COUPONTYPEID,FACEAMOUNT,STATUS"
						+ " FROM CRM_COUPON"
						+ " WHERE EID='" + req.geteId() + "' AND COUPONCODE='" + couponChange.getCouponCode() + "' ";
				List<Map<String, Object>> getQData = this.doQueryData(sql, null);
				if (CollectionUtils.isEmpty(getQData)) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "找零券:" + couponChange.getCouponCode() + " 此券号不存在 ");
				} else {
					if (!"2".equals(getQData.get(0).get("STATUS").toString())) {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "存在不可找零的券,券号:" + couponChange.getCouponCode() + ",请重试 ");
					}
				}
			}
		}
		
		
		String orderStatus = "";//订单状态
		String orderShop = "";//数据库里面下单门店		
		orderStatus = dcpOrder.getStatus();
		orderShop = dcpOrder.getShopNo();
		if(orderStatus.equals("3")||orderStatus.equals("11")||orderStatus.equals("12"))//已经退单
		{
			String ss = "已退单";
			if(orderStatus.equals("3"))
			{
				ss = "已取消";
			}
			else if(orderStatus.equals("11"))
			{
				ss = "已完成";
			}
			else if(orderStatus.equals("12"))
			{
				ss = "已退单";
			}
			
			res.setSuccess(false);
			res.setServiceDescription("该订单状态是"+ss+"，无法增加订金！");
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PayAdd_Open接口，订金增加】该订单状态是"+ss+"，无法增加订金！单号orderNo="+orderNo+" 数据库中下单门店shop="+orderShop+" 订单状态status="+orderStatus);
			return;
		}
		
		StringBuilder errorMsg = new StringBuilder();
		boolean nRet_updateDB =	this.UpdateOrderInfoByPayAdd(dcpOrder,req,  errorMsg);
		res.setSuccess(nRet_updateDB);
		res.setServiceDescription("服务执行成功！");
		if(!nRet_updateDB)
		{
			res.setServiceDescription("服务执行失败，"+errorMsg.toString());
		}
		else
		{
			//鼎捷外卖如果付款完成了，需要写下缓存
			if (orderLoadDocType.WAIMAI.equals(dcpOrder.getLoadDocType())&&"3".equals(dcpOrder.getPayStatus()))
			{
				try
				{
					order order_new = HelpTools.GetOrderInfoByOrderNO(this.dao, eId, loadDocType, orderNo);
					ParseJson pj = new ParseJson();
					String Response_json = pj.beanToJson(order_new);
					String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + order_new.getShopNo();
					String hash_key = order_new.getOrderNo();
					HelpTools.writelog_waimai(
							"渠道类型loadDocType="+loadDocType+",【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
					RedisPosPub redis = new RedisPosPub();
					boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
					if (nret) {
						HelpTools.writelog_waimai("【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
					} else {
						HelpTools.writelog_waimai("【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
					}
					
				}
				catch (Exception e)
				{
					HelpTools.writelog_waimai(
							"单号orderNo="+ req.getRequest().getOrderNo()+",渠道类型loadDocType="+loadDocType+",【写缓存】异常:"+e.getMessage());
					
				}
				
			}
			
			//【ID1037136】【阿哆诺斯3310】订单追加定金------订金追加支持券找零---服务  by jinzma 20231114
			if (bMemberPay && memberPayNo != null && !memberPayNo.trim().isEmpty()) {
				if (!CollectionUtils.isEmpty(req.getRequest().getCouponChangeList())) {
					try {
						DCP_OrderCreate orderCreate = new DCP_OrderCreate();
						orderCreate.callMemberPayCouponChange(req.getApiUserCode(), req.getApiUser().getUserKey(),
								eId, memberPayNo, req.getRequest().getCouponChangeList());
						
						/**********************************找零券保存*********************************/
						String[] columns = {"EID","SHOPID","ORDERNO","COUPONCODE","COUPONNO","QTY","AMT","BDATE","SDATE","STIME","OPNO","TRAN_TIME"};
						
						for (order.CouponChange couponChange : req.getRequest().getCouponChangeList()) {
							DataValue[] insValue = new DataValue[] {
									new DataValue(eId, Types.VARCHAR),
									new DataValue(orderShop, Types.VARCHAR),
									new DataValue(orderNo, Types.VARCHAR),
									new DataValue(couponChange.getCouponCode(), Types.VARCHAR),
									new DataValue(couponChange.getCouponNo(), Types.VARCHAR),
									new DataValue(couponChange.getQuantity(), Types.VARCHAR),
									new DataValue(couponChange.getFaceAmount(), Types.VARCHAR),
									new DataValue(new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()), Types.VARCHAR), //BDATE订金补录此处保存系统日期
									new DataValue(new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()), Types.VARCHAR),
									new DataValue(new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime()), Types.VARCHAR),
									new DataValue(req.getOpNO(), Types.VARCHAR),
									new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()), Types.VARCHAR),   //2023-08-24 16:54:39
							};
							
							InsBean ib = new InsBean("DCP_ORDER_COUPON", columns);
							ib.addValues(insValue);
							this.addProcessData(new DataProcessBean(ib));
						}
						this.doExecuteDataToDB();
						
					} catch (Exception ignored) {
					}
					
				}
			}


            //建行实物券，有价券
            //【ID1039103】[3.0]金贝儿--建行开发接口评估---POS服务
            List<order.otherCoupnPay> otherCoupnPayList=req.getRequest().getOtherCoupnPayList();
            if (otherCoupnPayList != null && otherCoupnPayList.size()>0)
            {
                try
                {
                    HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PayAdd_Open接口，订金增加】需要写【建行实物券，有价券 DCP_OTHERCOUPON_PAY】，单号OrderNO="+orderNo);

                    String[] columns = {"EID","SHOP","SALENO","ITEM","CARDNO"};

                    for (order.otherCoupnPay other : otherCoupnPayList)
                    {
                        DataValue[] insValue = new DataValue[] {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(orderShop, Types.VARCHAR),
                                new DataValue(orderNo, Types.VARCHAR),
                                new DataValue(other.getItem(), Types.VARCHAR),
                                new DataValue(other.getCouponCode(), Types.VARCHAR),
                        };

                        InsBean ib = new InsBean("DCP_OTHERCOUPON_PAY", columns);
                        ib.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib));
                    }
                    this.doExecuteDataToDB();
                }
                catch (Exception ignored)
                {
                    HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PayAdd_Open接口，订金增加】需要写【建行实物券，有价券 DCP_OTHERCOUPON_PAY】，单号OrderNO="+orderNo +",报错："+ ignored.getMessage());
                }
            }

			
		}
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderModify_PayAdd_OpenReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderModify_PayAdd_OpenReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderModify_PayAdd_OpenReq req) throws Exception {
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_OrderModify_PayAdd_OpenReq req) throws Exception {
		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		if(req.getRequest()==null)
		{
			errCt++;
			errMsg.append("请求节点request不存在, ");
			isFail = true;
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		if(Check.Null(req.getRequest().getOrderNo()))
		{
			errCt++;
			errMsg.append("请求节点orderNo不可为空, ");
			isFail = true;
		}
		if(Check.Null(req.getRequest().getLoadDocType()))
		{
			errCt++;
			errMsg.append("请求节点loadDocType不可为空, ");
			isFail = true;
		}
		List<DCP_OrderModify_PayAdd_OpenReq.level2Pay> pays = req.getRequest().getPay();
		if(pays==null||pays.isEmpty())
		{
			errCt++;
			errMsg.append("请求节点pay不可为空, ");
			isFail = true;
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		for (DCP_OrderModify_PayAdd_OpenReq.level2Pay level2Pay : pays)
		{
			if(Check.Null(level2Pay.getPayName()))
			{
				errCt++;
				errMsg.append("请求节点payName不可为空, ");
				isFail = true;
			}
			if(Check.Null(level2Pay.getPay()))
			{
				errCt++;
				errMsg.append("请求节点pay不可为空, ");
				isFail = true;
			}
			if(Check.Null(level2Pay.getbDate()))
			{
				errCt++;
				errMsg.append("请求节点bDate不可为空, ");
				isFail = true;
			}
		}
		
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}
	
	@Override
	protected TypeToken<DCP_OrderModify_PayAdd_OpenReq> getRequestType() {
		return new TypeToken<DCP_OrderModify_PayAdd_OpenReq>(){};
	}
	
	@Override
	protected DCP_OrderModify_PayAdd_OpenRes getResponseType() {
		return new DCP_OrderModify_PayAdd_OpenRes();
	}
	
	/**
	 *
	 * @param dcpOrder 原订单详细信息
	 * @param req 请求内容
	 * @param errorMsg
	 * @return
	 * @throws Exception
	 */
	private boolean UpdateOrderInfoByPayAdd(order dcpOrder,DCP_OrderModify_PayAdd_OpenReq req,StringBuilder errorMsg) throws Exception
	{
		boolean nResult = false;
		
		String eId = req.geteId();
		String orderNo = req.getRequest().getOrderNo();
		String loadDocType = req.getRequest().getLoadDocType();
		
		
		HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PayAdd_Open接口，订金增加】【新增付款档】开始，单号OrderNO="+orderNo);
		
		try
		{
			
			// 查询原单支付最大item
			String sqlString = "select * from (select max(item) as MAXITEM from dcp_order_pay_detail where EID='" + eId
					+ "'  and LOADDOCTYPE='" + loadDocType + "' and SOURCEBILLNO='" + orderNo + "' )";
			List<Map<String, Object>> listpayList = this.doQueryData(sqlString, null);
			int payItem = 1;
			if (listpayList != null && !listpayList.isEmpty())
			{
				int maxItem = 0;
				try
				{
					maxItem = Integer.parseInt(listpayList.get(0).get("MAXITEM").toString());
					
				} catch (Exception e)
				{
				
				}
				
				payItem = maxItem + 1;
				
			}
			String curdate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
			double realPay_tot = 0;// 实际付款金额(录入金额-找零-溢收)合计
			String payMemo = "";
			List<orderPay> payList_Add = new ArrayList<orderPay>();
			for (DCP_OrderModify_PayAdd_OpenReq.level2Pay map : req.getRequest().getPay())
			{
				// 付款金额
				double realPay = 0;// 实际付款金额=录入金额-找零-溢收=pay-change-extra
				double payAmount = 0;
				try
				{
					
					payAmount = Double.parseDouble(map.getPay());
					
				} catch (Exception e)
				{
				
				}
				
				// 找零
				double changed = 0;
				try
				{
					
					changed = Double.parseDouble(map.getChanged());
					
				} catch (Exception e)
				{
				
				}
				
				// 溢收
				double extra = 0;
				try
				{
					
					extra = Double.parseDouble(map.getExtra());
					
				} catch (Exception e)
				{
				
				}
				realPay = payAmount - changed - extra;
				if (realPay < 0)
				{
					realPay = 0;
				}
				realPay_tot += realPay;
				
				// 积分
				double descore = 0;
				try
				{
					
					descore = Double.parseDouble(map.getDescore());
					
				} catch (Exception e)
				{
				
				}
				
				String bDate = map.getbDate();
				if (bDate == null || bDate.trim().isEmpty())
				{
					bDate = curdate;
				}
				
				
				String funcNo = map.getFuncNo();
				String payType = map.getPayType();
				String payCode = map.getPayCode();
				String payCodeErp = map.getPayCodeErp();
				String payName = map.getPayName();
				String order_payCode = map.getOrder_payCode();
				String mobile = map.getMobile();
				String cardNo = map.getCardNo();
				String ctType = map.getCtType();
				String paySerNum = map.getPaySerNum();
				String serialNo = map.getSerialNo();
				
				String refNo = map.getRefNo();
				String teriminalNo = map.getTeriminalNo();
				String authCode = map.getAuthCode();

				//【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服务 by jinzma 20231205
				String payChannelCode = map.getPayChannelCode();
				String ecardSign = map.getEcardSign();   //年初做的有BUG，没有赋值，后面其实都取不到
				
				
				String isOrderPay = "Y";//map.getIsOnlinePay();
				String isOnlinePay = map.getIsOnlinePay();
				
				double couponQty = 0;
				try
				{
					couponQty = Double.parseDouble(map.getCouponQty());
				} catch (Exception e)
				{
				
				}
				String isVerification = map.getIsVerification();
				String canInvoice = map.getCanInvoice();
				
				
				double merDiscount = 0;
				try
				{
					merDiscount = Double.parseDouble(map.getMerDiscount());
				} catch (Exception e)
				{
				
				}
				
				double merReceive = 0;
				try
				{
					merReceive = Double.parseDouble(map.getMerReceive());
				} catch (Exception e)
				{
				
				}
				
				double thirdDiscount = 0;
				try
				{
					thirdDiscount = Double.parseDouble(map.getThirdDiscount());
				} catch (Exception e)
				{
				
				}
				
				double custPayReal = 0;
				try
				{
					custPayReal = Double.parseDouble(map.getCustPayReal());
				} catch (Exception e)
				{
				
				}
				
				double couponMarketPrice = 0;
				try
				{
					couponMarketPrice = Double.parseDouble(map.getCouponMarketPrice());
				} catch (Exception e)
				{
				
				}
				
				double couponPrice = 0;
				try
				{
					couponPrice = Double.parseDouble(map.getCouponPrice());
				} catch (Exception e)
				{
				
				}
				
				payMemo += "项次：" + payItem + " 付款方式：" + map.getPayName() + " 金额 ：" + map.getPay() + " 找零："
						+ map.getChanged() + " 溢收：" + map.getExtra() + "<br>";
				
				
				if (payName != null && payName.length() > 80)
				{
					payName = payName.substring(0, 80);
				}
				
				
				if (paySerNum != null && paySerNum.length() > 100)
				{
					paySerNum = paySerNum.substring(0, 100);// 数据库最长100
				}
				if (serialNo != null && serialNo.length() > 40)
				{
					serialNo = serialNo.substring(0, 40);// 数据库最长40
				}
				if (refNo != null && refNo.length() > 40)
				{
					refNo = refNo.substring(0, 40);// 数据库最长120
				}
				if (teriminalNo != null && teriminalNo.length() > 40)
				{
					teriminalNo = teriminalNo.substring(0, 40);// 数据库最长120
				}
				
				if (bDate != null && bDate.length() > 8)
				{
					bDate = bDate.substring(0, 8);// 数据库最长8
				}
				
				
				if (isOnlinePay != null && isOnlinePay.length() > 1)
				{
					isOnlinePay = isOnlinePay.substring(0, 1);// 数据库最长1
				}
				
				orderPay entityPay = new orderPay();
				entityPay.setAuthCode(authCode);
				entityPay.setbDate(bDate);
				entityPay.setCanInvoice(canInvoice);
				entityPay.setCardNo(cardNo);
				entityPay.setChanged(changed+"");
				entityPay.setCouponMarketPrice(couponMarketPrice);
				entityPay.setCouponPrice(couponPrice);
				entityPay.setCouponQty(couponQty);
				entityPay.setCtType(ctType);
				entityPay.setCustPayReal(custPayReal);
				entityPay.setDescore(descore+"");
				entityPay.setExtra(extra+"");
				entityPay.setFuncNo(funcNo);
				entityPay.setIsOnlinePay(isOnlinePay);
				entityPay.setIsOrderPay(isOrderPay);
				entityPay.setIsVerification(isVerification);
				entityPay.setItem(payItem+"");
				entityPay.setMerReceive(merReceive);
				entityPay.setMerDiscount(merDiscount);
				entityPay.setMobile(mobile);
				entityPay.setOrder_payCode(order_payCode);
				entityPay.setPay(payAmount+"");
				entityPay.setPayCode(payCode);
				entityPay.setPayCodeErp(payCodeErp);
				entityPay.setPayName(payName);
				entityPay.setPaySerNum(paySerNum);
				entityPay.setPayType(payType);
				entityPay.setRefNo(refNo);
				entityPay.setSerialNo(serialNo);
				entityPay.setTeriminalNo(teriminalNo);
				entityPay.setThirdDiscount(thirdDiscount);
				//【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服务 by jinzma 20231205
				entityPay.setPayChannelCode(payChannelCode);
				entityPay.setEcardSign(ecardSign);   //年初做的有BUG，没有赋值，后面其实都取不到
				//更新下 商户实收，顾客实付金额
				if(Math.abs(entityPay.getMerDiscount())<0.01&&Math.abs(entityPay.getThirdDiscount())<0.01)
				{
					//商家折扣 和 平台折扣都是0 ，给下默认值，
					BigDecimal pay_process = new BigDecimal("0");
					try
					{
						pay_process = new BigDecimal(entityPay.getPay());
					} catch (Exception e)
					{
						// TODO: handle exception
					}
					BigDecimal changed_process = new BigDecimal("0");
					try
					{
						changed_process = new BigDecimal(entityPay.getChanged());
					} catch (Exception e)
					{
						// TODO: handle exception
					}
					BigDecimal extra_process = new BigDecimal("0");
					try
					{
						extra_process = new BigDecimal(entityPay.getExtra());
					} catch (Exception e)
					{
						// TODO: handle exception
					}
					
					if(entityPay.getMerReceive()<0.01)
					{
						entityPay.setMerReceive(pay_process.subtract(changed_process).subtract(extra_process).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					if(entityPay.getCustPayReal()<0.01)//顾客实付可能真是0
					{
						
						entityPay.setCustPayReal(pay_process.subtract(changed_process).subtract(extra_process).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
				}
				entityPay.setGainChannel(map.getGainChannel()==null?"":map.getGainChannel());
				entityPay.setGainChannelName(map.getGainChannelName()==null?"":map.getGainChannelName());
				payList_Add.add(entityPay);
				
				payItem++;
			}
			
			
			//支付金额=单身合计tot_amt-抹零金额erase_AMT
			double tot_AMT = dcpOrder.getTot_Amt();//订单金额
			double orderPayAmt = dcpOrder.getPayAmt();//已支付金额
			double erase_AMT =  dcpOrder.getEraseAmt();//抹零金额
			
			BigDecimal r1 = new BigDecimal(realPay_tot+orderPayAmt);
			BigDecimal r2 = new BigDecimal(tot_AMT-erase_AMT);
			double sub1 = r1.subtract(r2).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
			if (sub1>=0.01)
			{
				
				errorMsg.append("(订金增加的总金额+已支付金额)="+r1.setScale(2,BigDecimal.ROUND_HALF_UP)+"大于订单应付金额="+r2.setScale(2,BigDecimal.ROUND_HALF_UP));
				HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PayAdd_Open接口，订金增加】异常:"+errorMsg.toString()+",单号OrderNO="+orderNo);
				return false;
			}
			/*********************调用memberpay**************************/
			StringBuffer errorMemberPay = new StringBuffer();
			boolean callMemberPayFlag = this.callMemerPay(req, dcpOrder,payList_Add, errorMemberPay);
			if(!callMemberPayFlag)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorMemberPay.toString());
			}
			
			
			/**************************************添加支付记录到数据库*******************************************/
			ArrayList<DataProcessBean> DPB_insert = this.insertPayAddSql(req, dcpOrder, payList_Add, errorMsg);
			if(DPB_insert==null||DPB_insert.isEmpty())
			{
				HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PayAdd_Open接口，订金增加】异常:"+errorMsg.toString()+",单号OrderNO="+orderNo);
				if (bMemberPay && memberPayNo != null && memberPayNo.trim().isEmpty() == false)
				{
					StringBuffer errorMemberPayReverse = new StringBuffer("");
					callMemerPayReverse(req, dcpOrder,memberPayNo,errorMemberPayReverse);
				}
				return false;
			}
			
			ParseJson pj = new ParseJson();
			
			String json_order = pj.beanToJson(dcpOrder);
			
			HelpTools.writelog_waimai("【更新前】查询数据库中订单order详细信息json="+json_order+",单号orderNo="+orderNo);
			
			String payStatus = "1";//1.未支付 2.部分支付 3.付清			
			if(Math.abs(sub1)<0.01)//原支付金额+当前支付金额小于订单金额=部分支付
			{
				payStatus = "3";
			}
			else
			{
				payStatus = "2";
			}
			dcpOrder.setPayStatus(payStatus);//更新下
			dcpOrder.setPayAmt(r1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
			/**************************************开始更新原单的商户实收等*******************************************/
			
			ArrayList<DataProcessBean> DPB_update = this.updatePayAddSql(req, dcpOrder, payList_Add, errorMsg);
			if(DPB_update==null||DPB_update.isEmpty())
			{
				HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PayAdd_Open接口，订金增加】异常:"+errorMsg.toString()+",单号OrderNO="+orderNo);
				if (bMemberPay && memberPayNo != null && memberPayNo.trim().isEmpty() == false)
				{
					StringBuffer errorMemberPayReverse = new StringBuffer("");
					callMemerPayReverse(req, dcpOrder,memberPayNo,errorMemberPayReverse);
				}
				return false;
			}
			
			for (DataProcessBean dataProcessBean : DPB_insert)
			{
				this.addProcessData(dataProcessBean);
			}
			
			for (DataProcessBean dataProcessBean : DPB_update)
			{
				this.addProcessData(dataProcessBean);
			}
			
			this.doExecuteDataToDB();
			
			nResult = true;
			
			HelpTools.writelog_waimai(
					"【第三方调用DCP_OrderModify_PayAdd_Open接口，订金增加】【新增付款档】新增成功，单号OrderNO=" + orderNo + " 付款状态payStatus=" + payStatus);
			
			// region 写下日志
			try
			{
				List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
				orderStatusLog onelv1 = new orderStatusLog();
				
				onelv1.setLoadDocType(loadDocType);
				onelv1.setChannelId(dcpOrder.getChannelId());
				
				onelv1.setNeed_callback("N");
				onelv1.setNeed_notify("N");
				
				onelv1.seteId(eId);
				
				String opNo = req.getRequest().getOpNo() == null ? "" : req.getRequest().getOpNo();
				
				String opName = req.getRequest().getOpName() == null ? "" : req.getRequest().getOpName();
				
				onelv1.setOpNo(opNo);
				onelv1.setOpName(opName);
				onelv1.setOrderNo(orderNo);
				onelv1.setLoadDocBillType(dcpOrder.getLoadDocBillType());
				onelv1.setLoadDocOrderNo(dcpOrder.getLoadDocOrderNo());
				
				String statusType_log = "99";// 其他状态
				String updateStaus_log = "99";// 订单修改
				
				onelv1.setStatusType(statusType_log);
				onelv1.setStatus(updateStaus_log);
				String statusName_log = "订金增加";
				String statusTypeName_log = "其他状态";
				onelv1.setStatusTypeName(statusTypeName_log);
				onelv1.setStatusName(statusName_log);
				
				StringBuffer memo = new StringBuffer("");
				if (payStatus.equals("3"))
				{
					memo.append(statusTypeName_log + "-->" + statusName_log + "(付清)<br>");
				} else
				{
					memo.append(statusTypeName_log + "-->" + statusName_log + "<br>");
				}
				memo.append(payMemo);
				onelv1.setMemo(memo.toString());
				
				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);
				orderStatusLogList.add(onelv1);
				
				StringBuilder errorMessage = new StringBuilder();
				boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorMessage);
				if (nRet)
				{
					HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
				} else
				{
					HelpTools.writelog_waimai(
							"【写表DCP_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNO:" + orderNo);
				}
				this.pData.clear();
				
			}
			catch (Exception e)
			{
				HelpTools.writelog_waimai("【写表DCP_orderStatuslog异常】 异常报错 " + e.toString() + " 订单号orderNo:" + orderNo);
			}
			// endregion
			
		}
		catch (Exception e)
		{
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PayAdd_Open接口，订金增加】【新增付款档】异常："+e.getMessage()+"，单号orderNo="+orderNo);
			
			errorMsg.append("新增付款方式失败！异常："+e.getMessage());
			this.pData.clear();
			if (bMemberPay && memberPayNo != null && memberPayNo.trim().isEmpty() == false)
			{
				StringBuffer errorMemberPayReverse = new StringBuffer("");
				callMemerPayReverse(req, dcpOrder,memberPayNo,errorMemberPayReverse);
			}
		}
		
		return nResult;
		
	}
	
	/**
	 * 订金增加插入相应的付款档（同订单创建逻辑）
	 * @param req
	 * @param dcpOrder
	 * @param payList
	 * @param errorMessage
	 * @return
	 * @throws Exception
	 */
	private ArrayList<DataProcessBean> insertPayAddSql (DCP_OrderModify_PayAdd_OpenReq req,order dcpOrder,List<orderPay> payList,StringBuilder errorMessage) throws Exception
	{
		ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
		try
		{
			/**********************************订单付款明细*********************************/
			String eId = dcpOrder.geteId();
			String loadDocType = dcpOrder.getLoadDocType();//渠道类型
			String channelId = dcpOrder.getChannelId();//渠道编码
			String billType = dcpOrder.getBillType();//单据类型（1：订单；-1：退订单）			
			if(billType==null||billType.isEmpty())
			{
				billType = "1";
			}
			
			String orderNo = dcpOrder.getOrderNo();//订单中心生成的订单号=外部传入的单号
			String shop = dcpOrder.getShopNo();
			String companyId = dcpOrder.getBelfirm();//公司别
			String sellNo = dcpOrder.getSellNo();//大客户编号
			
			String opNo = req.getRequest().getOpNo();
			String workNo = req.getRequest().getWorkNo();
			String squadNo = req.getRequest().getSquadNo();
			String machineNo = req.getRequest().getMachineNo();
			String opShopId = req.getRequest().getShopId();
			if (opShopId==null||opShopId.trim().isEmpty())
            {
                opShopId = shop;
            }

			
			String sourcebilltype = "Order";//来源单据类型：Order-订单TableRsv-桌台预订
			
			String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			String curDateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			String curTime = new SimpleDateFormat("HHmmss").format(new Date());
			String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//数据库DATE类型
			
			if(payList!=null&&payList.size()>0)
			{
				boolean isNeedInsert_DCP_STATISTIC_INFO = false;
				if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID))
				{
					isNeedInsert_DCP_STATISTIC_INFO = true;
					HelpTools.writelog_waimai("【开始生成insert语句】渠道类型loadDocType="+loadDocType+"，需要写【交班统计信息表DCP_STATISTIC_INFO】，单号="+orderNo);
				}
				
				String payBillNo = UUID.randomUUID().toString().replace("-", "");//收款单号
				String bDate = curDate;
				
				BigDecimal pay_tot = new BigDecimal("0");
				BigDecimal extra_tot = new BigDecimal("0");
				BigDecimal changed_tot = new BigDecimal("0");
				BigDecimal writeoffamt_tot = new BigDecimal("0");//冲销金额
				BigDecimal lackamt_tot = new BigDecimal("0");//未冲销金额
				
				String[] columns_pay_detail =
						{ "eid", "billno", "item", "billdate", "bdate", "sourcebilltype", "sourcebillno", "loaddoctype",
								"channelid", "paycode", "paycodeerp", "payname", "order_paycode", "isonlinepay", "pay",
								"paydiscamt", "payamt1", "payamt2", "descore", "cttype", "cardno", "cardbeforeamt", "cardremainamt",
								"couponqty", "isverification", "extra", "changed", "paysernum", "serialno", "refno", "teriminalno",
								"caninvoice", "writeoffamt", "authcode","FUNCNO","PAYDOCTYPE","SENDPAY","paytype",
								"MERDISCOUNT","MERRECEIVE","THIRDDISCOUNT","CUSTPAYREAL","COUPONMARKETPRICE",
								"COUPONPRICE","mobile","PARTITION_DATE","PAYCHANNELCODE","GAINCHANNEL","GAINCHANNELNAME"};
				for (orderPay payItem : payList)
				{
					try
					{
						if(payItem.getPayName()!=null&&payItem.getPayName().length()>120)
						{
							payItem.setPayName(payItem.getPayName().substring(0, 120));
						}
						if(payItem.getOrder_payCode()!=null&&payItem.getOrder_payCode().length()>100)
						{
							payItem.setOrder_payCode(payItem.getOrder_payCode().substring(0, 100));
						}
					}
					catch (Exception e)
					{
						// TODO: handle exception
					}
					BigDecimal pay = new BigDecimal("0");
					try
					{
						pay = new BigDecimal(payItem.getPay());
					} catch (Exception e)
					{
					}
					BigDecimal extra = new BigDecimal("0");
					try
					{
						extra = new BigDecimal(payItem.getExtra());
					} catch (Exception e)
					{
					}
					BigDecimal changed= new BigDecimal("0");
					try
					{
						changed = new BigDecimal(payItem.getChanged());
					} catch (Exception e)
					{
					}
					BigDecimal writeoffamt= new BigDecimal("0");
					try
					{
						//writeoffamt = new BigDecimal(payItem.());
					} catch (Exception e)
					{
					}
					BigDecimal lackamt= new BigDecimal("0");
					try
					{
						//lackamt = new BigDecimal(payItem.getl());
					} catch (Exception e)
					{
					}
					
					//收款金额 写交班流水表
					BigDecimal p_amt=pay.subtract(changed);
					
					pay_tot = pay_tot.add(pay);
					extra_tot = extra_tot.add(extra);
					changed_tot = changed_tot.add(changed);
					writeoffamt_tot = writeoffamt_tot.add(writeoffamt);
					lackamt_tot = lackamt_tot.add(lackamt);
					bDate = payItem.getbDate();
					
					DataValue[] insValue_pay_detail = new DataValue[] {
							new DataValue(eId, Types.VARCHAR),
							new DataValue(payBillNo, Types.VARCHAR),
							new DataValue(payItem.getItem(), Types.INTEGER),
							new DataValue(curDate, Types.VARCHAR),
							new DataValue(payItem.getbDate(), Types.VARCHAR),
							new DataValue(sourcebilltype, Types.VARCHAR),//sourcebilltype 来源单据类型：Order-订单TableRsv-桌台预订
							new DataValue(orderNo, Types.VARCHAR),
							new DataValue(loadDocType, Types.VARCHAR),
							new DataValue(channelId, Types.VARCHAR),
							new DataValue(payItem.getPayCode(), Types.VARCHAR),
							new DataValue(payItem.getPayCodeErp(), Types.VARCHAR),
							new DataValue(payItem.getPayName(), Types.VARCHAR),//ordershop
							new DataValue(payItem.getOrder_payCode(), Types.VARCHAR),//ordershopname
							new DataValue(payItem.getIsOnlinePay(), Types.VARCHAR),
							new DataValue(payItem.getPay(), Types.VARCHAR),
							new DataValue(payItem.getPayDiscAmt(), Types.VARCHAR),
							new DataValue(payItem.getPayAmt1(), Types.VARCHAR),
							new DataValue(payItem.getPayAmt2(), Types.VARCHAR),
							new DataValue(payItem.getDescore(), Types.VARCHAR),
							new DataValue(payItem.getCtType(), Types.VARCHAR),
							new DataValue(payItem.getCardNo(), Types.VARCHAR),
							new DataValue(payItem.getCardBeforeAmt(), Types.VARCHAR),
							new DataValue(payItem.getCardRemainAmt(), Types.VARCHAR),
							new DataValue(payItem.getCouponQty(), Types.VARCHAR),
							new DataValue(payItem.getIsVerification(), Types.VARCHAR),
							new DataValue(payItem.getExtra(), Types.VARCHAR),
							new DataValue(payItem.getChanged(), Types.VARCHAR),
							new DataValue(payItem.getPaySerNum(), Types.VARCHAR),
							new DataValue(payItem.getSerialNo(), Types.VARCHAR),
							new DataValue(payItem.getRefNo(), Types.VARCHAR),
							new DataValue(payItem.getTeriminalNo(), Types.VARCHAR),
							new DataValue(payItem.getCanInvoice(), Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),//writeoffamt								
							new DataValue(payItem.getAuthCode(), Types.VARCHAR),
							new DataValue(payItem.getFuncNo(), Types.VARCHAR),
							new DataValue(payItem.getPaydoctype(), Types.VARCHAR),
							new DataValue(payItem.getCardSendPay(), Types.VARCHAR),
							new DataValue(payItem.getPayType(), Types.VARCHAR),
							new DataValue(payItem.getMerDiscount(), Types.VARCHAR),//商户优惠金额，移动支付用，例如支付宝，微信等
							new DataValue(payItem.getMerReceive(), Types.VARCHAR),//商家实收金额，移动支付用，例如支付宝，微信等
							new DataValue(payItem.getThirdDiscount(), Types.VARCHAR),//第三方优惠金额：移动支付用，例如支付宝，微信等
							new DataValue(payItem.getCustPayReal(), Types.VARCHAR),//客户实付金额：移动支付用，例如支付宝，微信等
							new DataValue(payItem.getCouponMarketPrice(), Types.VARCHAR),//券面值
							new DataValue(payItem.getCouponPrice(), Types.VARCHAR),//券售价
							new DataValue(payItem.getMobile(), Types.VARCHAR),//会员卡付款对应的手机号
							//【TAPD 需求】内容更新“【阿哆诺斯3.0】订单801880180120231107115835080，11月7日做的订单，
							// 11月10日追加的订金，DCP_ORDER_PAY_DETAIL表，BDATE记录11月10日，PARTITION_DATE记录的是11月7日，记录错了吧，
							// PARTITION_DATE也应该记录11月10日吧，目前金凤收银汇总表取的日期字段是”  by taorp 20231114
							new DataValue(bDate, Types.NUMERIC),//分区字段
							new DataValue(payItem.getPayChannelCode(), Types.VARCHAR),//支付通道，鼎捷、企迈
							new DataValue(payItem.getGainChannel()==null?"":payItem.getGainChannel(), Types.VARCHAR),
							new DataValue(payItem.getGainChannelName()==null?"":payItem.getGainChannelName(), Types.VARCHAR),
					};
					
					InsBean ib_pay_detail = new InsBean("DCP_ORDER_PAY_DETAIL", columns_pay_detail);//分区字段已处理
					ib_pay_detail.addValues(insValue_pay_detail);
					DataPB.add(new DataProcessBean(ib_pay_detail));
					
					
					if (isNeedInsert_DCP_STATISTIC_INFO)
					{
						//交班统计信息表DCP_STATISTIC_INFO
						String[] Columns_DCP_STATISTIC_INFO = {
								"EID","SHOPID","MACHINE","OPNO","SQUADNO","ORDERNO","ITEM","PAYCODE",
								"PAYNAME","AMT","SDATE","STIME","ISORDERPAY","WORKNO","TYPE","BDATE","CARDNO",
								"CUSTOMERNO","CHANGED","EXTRA","ISTURNOVER","STATUS","APPTYPE","CHANNELID","PAYTYPE","MERDISCOUNT","THIRDDISCOUNT","DIRECTION"
						};
						DataValue[] insValue_DCP_STATISTIC_INFO = new DataValue[]{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(opShopId, Types.VARCHAR),
								new DataValue(machineNo, Types.VARCHAR),
								new DataValue(opNo, Types.VARCHAR),
								new DataValue(squadNo, Types.VARCHAR),
								new DataValue(orderNo, Types.VARCHAR),
								new DataValue(payItem.getItem(), Types.VARCHAR),
								new DataValue(payItem.getPayCode(), Types.VARCHAR),
								new DataValue(payItem.getPayName(), Types.VARCHAR),
								new DataValue(p_amt, Types.DECIMAL),
								new DataValue(curDate, Types.VARCHAR),
								new DataValue(curTime, Types.VARCHAR),
								new DataValue("N", Types.VARCHAR),//固定写N，这样才交班单能统计
								new DataValue(workNo, Types.VARCHAR),
								new DataValue("3", Types.VARCHAR),//TYPE 注意给值
								new DataValue(payItem.getbDate(), Types.VARCHAR),
								new DataValue(dcpOrder.getCardNo(), Types.VARCHAR),//会员卡号
								new DataValue(dcpOrder.getCustomer(), Types.VARCHAR),
								new DataValue("0", Types.VARCHAR),
								new DataValue(payItem.getExtra(), Types.VARCHAR),
								new DataValue("Y", Types.VARCHAR),//ISTURNOVER
								new DataValue("100", Types.VARCHAR),
								new DataValue(loadDocType, Types.VARCHAR),//
								new DataValue(channelId, Types.VARCHAR),//
								new DataValue(payItem.getPayType(), Types.VARCHAR),//
								new DataValue(payItem.getMerDiscount(), Types.VARCHAR),//
								new DataValue(payItem.getThirdDiscount(), Types.VARCHAR),//
								new DataValue("1", Types.VARCHAR),
						};
						InsBean ib_DCP_STATISTIC_INFO = new InsBean("DCP_STATISTIC_INFO", Columns_DCP_STATISTIC_INFO);
						ib_DCP_STATISTIC_INFO.addValues(insValue_DCP_STATISTIC_INFO);
						DataPB.add(new DataProcessBean(ib_DCP_STATISTIC_INFO));
						
						if(payItem.getFuncNo()!=null&&payItem.getFuncNo().equals("601"))
						{
							HelpTools.writelog_waimai("【开始生成insert语句】渠道类型loadDocType="+loadDocType+"，需要写【写赊销明细表 DCP_CUSTOMER_CREDIT_DETAIL】，单号="+orderNo);
							//交班统计信息表DCP_CUSTOMER_CREDIT_DETAIL
							String[] Columns_DCP_CUSTOMER_CREDIT_DETAIL = {
									"EID","SHOPID","MACHNO","OPNO","BDATE","CUSTOMERNO","CREDITNAME","SOURCENO",
									"SOURCETYPE","CREDITAMT","RETURNAMT","LACKAMT","UPDATE_TIME"
							};
							DataValue[] insValue_DCP_CUSTOMER_CREDIT_DETAIL  = new DataValue[]{
									new DataValue(eId, Types.VARCHAR),
									new DataValue(opShopId, Types.VARCHAR),
									new DataValue(machineNo, Types.VARCHAR),
									new DataValue(opNo, Types.VARCHAR),
									new DataValue(payItem.getbDate(), Types.VARCHAR),
									new DataValue(dcpOrder.getCustomer(), Types.VARCHAR),
									new DataValue(dcpOrder.getCustomerName(), Types.VARCHAR),// 赊销人creditName == 传入参数 customerName 
									new DataValue(orderNo, Types.VARCHAR),//来源单号 sourceNo == 订单号 orderNo ，
									new DataValue("3", Types.VARCHAR),//来源类型 sourceType == 3 订单
									new DataValue(payItem.getPay(), Types.VARCHAR),//赊销金额 creditAmt == 付款方式601的 pay 付款金额
									new DataValue("0", Types.VARCHAR),//已核销金额 returnAmt == 0，
									new DataValue(payItem.getPay(), Types.VARCHAR),//未核销金额 lackAmt == 赊销金额 creditAmt 。									
									new DataValue(curDateTime, Types.VARCHAR),
							};
							InsBean ib_DCP_CUSTOMER_CREDIT_DETAIL = new InsBean("DCP_CUSTOMER_CREDIT_DETAIL", Columns_DCP_CUSTOMER_CREDIT_DETAIL);
							ib_DCP_CUSTOMER_CREDIT_DETAIL.addValues(insValue_DCP_CUSTOMER_CREDIT_DETAIL);
							DataPB.add(new DataProcessBean(ib_DCP_CUSTOMER_CREDIT_DETAIL));
						}
						
					}
					
					
					
				}
				
				/*****************************收款汇总表************************************/
				String sourceheadbillno = orderNo;
				String direction = billType;//金额方向:1、-1
				String usetype = "front";//款项用途：front-预付款 refund-退款 final-尾款
				if(direction.equals("-1"))
				{
					usetype ="refund";
					sourceheadbillno ="";
				}
				
				
				String dcp_order_pay_status = "100";//收款状态：-1不成功 100成功
				BigDecimal payrealamt_tot = pay_tot.subtract(changed_tot).subtract(extra_tot);//实付金额=付款金额-找零-溢收
				String[] columns_pay =
						{ "eid", "billno", "billdate", "bdate", "sourcebilltype", "sourcebillno", "companyid", "shopid",
								"CHANNELID", "LOADDOCTYPE", "machineid", "customerno", "squadno", "workno", "direction", "payrealamt",
								"writeoffamt", "usetype", "status", "memo", "createopid", "createopname",
								"createtime","SOURCEHEADBILLNO","PARTITION_DATE","UPDATE_TIME","TRAN_TIME"
						};
				
				DataValue[] insValue_pay = new DataValue[] {
						new DataValue(eId, Types.VARCHAR),
						new DataValue(payBillNo, Types.VARCHAR),
						new DataValue(curDate, Types.VARCHAR),
						new DataValue(bDate, Types.VARCHAR),
						new DataValue(sourcebilltype, Types.VARCHAR),//sourcebilltype 来源单据类型：Order-订单TableRsv-桌台预订
						new DataValue(orderNo, Types.VARCHAR),
						new DataValue(companyId, Types.VARCHAR),
						new DataValue(opShopId, Types.VARCHAR),
						new DataValue(channelId, Types.VARCHAR),
						new DataValue(loadDocType, Types.VARCHAR),
						new DataValue(machineNo, Types.VARCHAR),
						new DataValue(sellNo, Types.VARCHAR),
						new DataValue(squadNo, Types.VARCHAR),
						new DataValue(workNo, Types.VARCHAR),
						new DataValue(direction, Types.VARCHAR),//direction  金额方向:1、-1
						new DataValue(payrealamt_tot, Types.VARCHAR),
						new DataValue(writeoffamt_tot, Types.VARCHAR),
						new DataValue(usetype, Types.VARCHAR),//usetype 款项用途：front-预付款 refund-退款 
						new DataValue(dcp_order_pay_status, Types.VARCHAR),//status 收款状态：-1不成功 100成功
						new DataValue("", Types.VARCHAR),//memo	
						new DataValue(opNo, Types.VARCHAR),//createopid	
						new DataValue("", Types.VARCHAR),//createopname	
						new DataValue(lastmoditime, Types.DATE),//createtime	
						new DataValue(sourceheadbillno, Types.VARCHAR),//sourceheadbillno
						new DataValue(bDate, Types.NUMERIC),//分区字段
						new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
						new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
					
				};
				
				InsBean ib_pay = new InsBean("DCP_ORDER_PAY", columns_pay);//分区字段已处理
				ib_pay.addValues(insValue_pay);
				DataPB.add(new DataProcessBean(ib_pay));
				
			}
			
			
			
			
		}
		catch (Exception e)
		{
			//有异常全部清空
			errorMessage.append("添加支付方式sql语句异常:"+e.getMessage());
			
			DataPB.clear();
		}
		return DataPB;
	}
	
	
	private ArrayList<DataProcessBean> updatePayAddSql (DCP_OrderModify_PayAdd_OpenReq req,order dcpOrder,List<orderPay> payList,StringBuilder errorMessage) throws Exception
	{
		ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
		try
		{
			//给下默认值
			dcpOrder.setTotDisc_custPayReal(0);
			dcpOrder.setTotDisc_merReceive(0);
			dcpOrder.setTot_Amt_custPayReal(dcpOrder.getTot_Amt());
			dcpOrder.setTot_Amt_merReceive(dcpOrder.getTot_Amt());
			
			//添加付款档
			if(dcpOrder.getPay()==null)
			{
				dcpOrder.setPay(new ArrayList<orderPay>());
			}
			for (orderPay payItem : payList)
			{
				dcpOrder.getPay().add(payItem);
			}
			
			//给下付款档默认值，后面分摊支付折扣 同订单创建逻辑
			for (orderPay payInfo : dcpOrder.getPay())
			{
				if(Math.abs(payInfo.getMerDiscount())<0.01&&Math.abs(payInfo.getThirdDiscount())<0.01)
				{
					//商家折扣 和 平台折扣都是0 ，给下默认值，
					BigDecimal pay = new BigDecimal("0");
					try
					{
						pay = new BigDecimal(payInfo.getPay());
					} catch (Exception e)
					{
						// TODO: handle exception
					}
					BigDecimal changed = new BigDecimal("0");
					try
					{
						changed = new BigDecimal(payInfo.getChanged());
					} catch (Exception e)
					{
						// TODO: handle exception
					}
					BigDecimal extra = new BigDecimal("0");
					try
					{
						extra = new BigDecimal(payInfo.getExtra());
					} catch (Exception e)
					{
						// TODO: handle exception
					}
					
					//if(payInfo.getMerReceive()<0.01)
					{
						payInfo.setMerReceive(pay.subtract(changed).subtract(extra).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					//if(payInfo.getCustPayReal()<0.01)//顾客实付可能真是0
					{
						
						payInfo.setCustPayReal(pay.subtract(changed).subtract(extra).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
				}
			}
			
			
			
			//给下商品单身默认值，并且删除之前的type=60的支付折扣，重新分摊
			for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
			{
				//给下默认值，后面还会计算
				goodsItem.setDisc_custPayReal(0);
				goodsItem.setDisc_merReceive(0);
				goodsItem.setAmt_custPayReal(goodsItem.getAmt());
				goodsItem.setAmt_merReceive(goodsItem.getAmt());
				
				List<orderGoodsItemAgio> agioList = goodsItem.getAgioInfo();
				if(agioList!=null&&agioList.isEmpty()==false)
				{
					for(int i = agioList.size()-1;i>=0;i--)
					{
						String dcType = agioList.get(i).getDcType();
						if("60".equals(dcType))
						{
							agioList.remove(i);
						}
					}
				}
			}
			
			
			//下面走 订单创建逻辑里面的 支付折扣
			StringBuffer error = new StringBuffer();
			
			HelpTools.posOrderPayDiscShareProcess(dcpOrder,error);
			
			//pos抹零分摊
			HelpTools.posOrderEraseAmtShareProcess(dcpOrder, error);
			
			//pos存在订单没有完全付款的情况，重新计算单头的商户实收和顾客实付
			HelpTools.posOrderTotAmtMerReceiveProcess(dcpOrder, error);
			
			
			
			//生成更新语句
			UptBean ub1 = null;
			ub1 = new UptBean("DCP_ORDER");
			
			// condition
			ub1.addCondition("EID", new DataValue(dcpOrder.geteId(), Types.VARCHAR));
			ub1.addCondition("ORDERNO", new DataValue(dcpOrder.getOrderNo(), Types.VARCHAR));
			
			ub1.addUpdateValue("PAYAMT", new DataValue(dcpOrder.getPayAmt(), Types.VARCHAR));
			ub1.addUpdateValue("PAYSTATUS", new DataValue(dcpOrder.getPayStatus(), Types.VARCHAR));
			ub1.addUpdateValue("TOT_AMT_MERRECEIVE", new DataValue(dcpOrder.getTot_Amt_merReceive(), Types.VARCHAR));
			ub1.addUpdateValue("TOT_AMT_CUSTPAYREAL", new DataValue(dcpOrder.getTot_Amt_custPayReal(), Types.VARCHAR));
			ub1.addUpdateValue("TOT_DISC_MERRECEIVE", new DataValue(dcpOrder.getTotDisc_merReceive(), Types.VARCHAR));
			ub1.addUpdateValue("TOT_DISC_CUSTPAYREAL", new DataValue(dcpOrder.getTotDisc_custPayReal(), Types.VARCHAR));
			ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			if (bMemberPay&&memberPayNo!=null&&!memberPayNo.trim().isEmpty())
			{
				ub1.addUpdateValue("MEMBERPAYNO", new DataValue(memberPayNo, Types.VARCHAR));
			}
			DataPB.add(new DataProcessBean(ub1));
			
			for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
			{
				UptBean ub_goods = null;
				ub_goods = new UptBean("DCP_ORDER_DETAIL");
				
				// condition
				ub_goods.addCondition("EID", new DataValue(dcpOrder.geteId(), Types.VARCHAR));
				ub_goods.addCondition("ORDERNO", new DataValue(dcpOrder.getOrderNo(), Types.VARCHAR));
				ub_goods.addCondition("ITEM", new DataValue(goodsItem.getItem(), Types.VARCHAR));
				
				ub_goods.addUpdateValue("AMT_MERRECEIVE", new DataValue(goodsItem.getAmt_merReceive(), Types.VARCHAR));
				ub_goods.addUpdateValue("AMT_CUSTPAYREAL", new DataValue(goodsItem.getAmt_custPayReal(), Types.VARCHAR));
				ub_goods.addUpdateValue("DISC_MERRECEIVE", new DataValue(goodsItem.getDisc_merReceive(), Types.VARCHAR));
				ub_goods.addUpdateValue("DISC_CUSTPAYREAL", new DataValue(goodsItem.getDisc_custPayReal(), Types.VARCHAR));
				
				DataPB.add(new DataProcessBean(ub_goods));
				
				
				List<orderGoodsItemAgio> agioList = goodsItem.getAgioInfo();
				boolean isExistPosPayAgio = false;//是否存在60的支付折扣
				
				if(agioList!=null&&agioList.isEmpty()==false)
				{
					for(orderGoodsItemAgio agio : agioList)
					{
						String dcType = agio.getDcType();
						if("60".equals(dcType))
						{
							isExistPosPayAgio = true;
							break;
						}
					}
					
					
					if(isExistPosPayAgio)
					{
						DelBean db1 = new DelBean("DCP_ORDER_DETAIL_AGIO");
						// condition
						db1.addCondition("EID", new DataValue(dcpOrder.geteId(), Types.VARCHAR));
						db1.addCondition("ORDERNO", new DataValue(dcpOrder.getOrderNo(), Types.VARCHAR));
						db1.addCondition("MITEM", new DataValue(goodsItem.getItem(), Types.VARCHAR));
						db1.addCondition("DCTYPE", new DataValue("60", Types.VARCHAR));
						
						DataPB.add(new DataProcessBean(db1));
						
						String[] columns_goodsAgio =
								{ "eid", "orderno", "MITEM", "ITEM", "QTY", "AMT","INPUTDISC","REALDISC","DISC","DCTYPE",
										"DCTYPENAME","PMTNO","GIFTCTF","GIFTCTFNO","BSNO",
										"DISC_MERRECEIVE","DISC_CUSTPAYREAL","PARTITION_DATE"};
						
						int goodsAgioItem = this.getGoodsAgioListMaxItem(dcpOrder.geteId(), dcpOrder.getOrderNo(), goodsItem.getItem());
						for(orderGoodsItemAgio agio : agioList)
						{
							String dcType = agio.getDcType();
							if("60".equals(dcType))
							{
								agio.setItem(goodsAgioItem+"");
								
								try
								{
									if(agio.getDcType()!=null&&agio.getDcType().length()>32)
									{
										agio.setDcType(agio.getDcType().substring(0,32));
									}
									if(agio.getDcTypeName()!=null&&agio.getDcTypeName().length()>64)
									{
										agio.setDcTypeName(agio.getDcTypeName().substring(0,64));
									}
									if(agio.getPmtNo()!=null&&agio.getPmtNo().length()>32)
									{
										agio.setPmtNo(agio.getPmtNo().substring(0,32));
									}
									if(agio.getGiftCtf()!=null&&agio.getGiftCtf().length()>32)
									{
										agio.setGiftCtf(agio.getGiftCtf().substring(0,32));
									}
									if(agio.getGiftCtfNo()!=null&&agio.getGiftCtfNo().length()>32)
									{
										agio.setGiftCtfNo(agio.getGiftCtfNo().substring(0,32));
									}
									if(agio.getBsNo()!=null&&agio.getBsNo().length()>32)
									{
										agio.setBsNo(agio.getBsNo().substring(0,32));
									}
									
								}
								catch (Exception e)
								{
									// TODO: handle exception
								}
								
								DataValue[] insValue_goodsAgio = new DataValue[] {
										new DataValue(dcpOrder.geteId(), Types.VARCHAR),
										new DataValue(dcpOrder.getOrderNo(), Types.VARCHAR),
										new DataValue(goodsItem.getItem(), Types.VARCHAR),
										new DataValue(goodsAgioItem, Types.VARCHAR),
										new DataValue(agio.getQty(), Types.VARCHAR),
										new DataValue(agio.getAmt(), Types.VARCHAR),
										new DataValue(agio.getInputDisc(), Types.VARCHAR),
										new DataValue(agio.getRealDisc(), Types.VARCHAR),
										new DataValue(agio.getDisc(), Types.VARCHAR),
										new DataValue(agio.getDcType(), Types.VARCHAR),
										new DataValue(agio.getDcTypeName(), Types.VARCHAR),
										new DataValue(agio.getPmtNo(), Types.VARCHAR),
										new DataValue(agio.getGiftCtf(), Types.VARCHAR),
										new DataValue(agio.getGiftCtfNo(), Types.VARCHAR),
										new DataValue(agio.getBsNo(), Types.VARCHAR),
										new DataValue(agio.getDisc_merReceive(), Types.VARCHAR),
										new DataValue(agio.getDisc_custPayReal(), Types.VARCHAR),
										new DataValue(dcpOrder.getbDate(), Types.NUMERIC),//分区字段
								};
								
								InsBean ib_goodsAgio = new InsBean("DCP_ORDER_DETAIL_AGIO", columns_goodsAgio);//分区字段已处理
								ib_goodsAgio.addValues(insValue_goodsAgio);
								DataPB.add(new DataProcessBean(ib_goodsAgio));
								goodsAgioItem++;
							}
						}
						
					}
					
				}
				
			}
			
		}
		catch (Exception e)
		{
			// TODO: handle exception
			errorMessage.append(e.getMessage());
			errorMessage.append("更新订单商户实收，顾客实付等sql语句异常:"+e.getMessage());
			DataPB.clear();
		}
		return DataPB;
	}
	
	private int getGoodsAgioListMaxItem (String eId,String orderNo,String MItem) throws Exception
	{
		int agioItem = 1;
		try
		{
			// 查询原单支付最大item
			String sqlString = "select * from ( select max(item) as MAXITEM from dcp_order_detail_agio " + "where eid='"
					+ eId + "' and orderno='" + orderNo + "' and MITEM=" + MItem + " and dctype<>'60' )";
			List<Map<String, Object>> listpayList = this.doQueryData(sqlString, null);
			
			if (listpayList != null && !listpayList.isEmpty())
			{
				int maxItem = 0;
				try
				{
					maxItem = Integer.parseInt(listpayList.get(0).get("MAXITEM").toString());
					
				} catch (Exception e)
				{
				
				}
				agioItem = maxItem + 1;
			}
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		return agioItem;
	}
	
	/**
	 *会员支付
	 * @param req
	 * @param dcpOrder
	 * @param orderPayList
	 * @param error
	 * @return
	 * @throws Exception
	 */
	public boolean callMemerPay (DCP_OrderModify_PayAdd_OpenReq req,order dcpOrder,List<orderPay> orderPayList,StringBuffer error) throws Exception
	{
		String loadDocType = dcpOrder.getLoadDocType();
		if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID)||loadDocType.equals(orderLoadDocType.WAIMAI)||loadDocType.equals(orderLoadDocType.OWNCHANNEL))
		{
		
		}
		else
		{
			error.append("渠道类型loadDocType="+loadDocType+"无需调用！");
			return true;
		}
		
		if(orderPayList==null||orderPayList.isEmpty())
		{
			error.append("没有付款方式无需调用！");
			return true;
		}
		String eId = dcpOrder.geteId();
		String orderNo = dcpOrder.getOrderNo();
		String opShopId = req.getRequest().getShopId();
		HelpTools.writelog_waimai("【订单增加订金】检查是否使用会员付款方式开始 ，单号orderNo="+orderNo);
		memberPayNo= PosPub.getGUID(false);//调用积分memberpay的orderno
		//尾款处理,这个只是记录付款
		com.alibaba.fastjson.JSONArray payslistArray=new com.alibaba.fastjson.JSONArray();
		//这里才会扣款
		com.alibaba.fastjson.JSONArray cardlistArray=new com.alibaba.fastjson.JSONArray();
		//券列表
		com.alibaba.fastjson.JSONArray couponlistArray=new com.alibaba.fastjson.JSONArray();
		BigDecimal payTot=new BigDecimal("0");//付款总额
		
		String partnerMember = dcpOrder.getPartnerMember();
		
		for (orderPay pay : orderPayList)
		{
			if(pay.getFuncNo()==null)
			{
				continue;
			}
			
			//POS专用
			if (orderLoadDocType.POS.equals(dcpOrder.getLoadDocType())||orderLoadDocType.POSANDROID.equals(dcpOrder.getLoadDocType()))
			{
				if (Check.Null(pay.getPaydoctype())) pay.setPaydoctype("4");
			}
			
			BigDecimal p_pay=new BigDecimal(pay.getPay());
			BigDecimal p_changed=new BigDecimal(pay.getChanged());
			BigDecimal p_extra=new BigDecimal(pay.getExtra());
			
			//pay-changed-extra累加起来
			BigDecimal p_realpay=p_pay.subtract(p_changed).subtract(p_extra).setScale(2, BigDecimal.ROUND_HALF_UP);
			
			payTot=payTot.add(p_realpay);
			
			//券面额
			BigDecimal faceAmt=p_pay;//.add(p_extra);
			
			
			//****会员卡扣款****
			if (pay.getFuncNo().equals("301"))
			{
				pay.setPaySerNum(memberPayNo);
				
				com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				tempPay.put("payType",pay.getPayType());//收款方式代号
				tempPay.put("payName",pay.getPayName());//收款方式名称
				tempPay.put("payAmount",p_realpay);//付款金额
				tempPay.put("noCode",pay.getCardNo());//卡号
				tempPay.put("isCardPay",1);//
				payslistArray.add(tempPay);
				
				//
				com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				tempCard.put("cardNo",pay.getCardNo());
				tempCard.put("password",pay.getPassword());  // 卡密码，企迈会员卡支付需要密码
				tempCard.put("amount",p_realpay);//0只处理积分
				tempCard.put("getPoint","0");//
				
				//【ID1030898】//【菲尔雪3.0】会员及卡券接企迈-POS服务 by jinzma 2023
				tempCard.put("ecardSign",pay.getEcardSign());  //企迈 ecardSign字段，0实体卡，1电子卡
				//【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服务  by jinzma 20231205
				tempCard.put("partnerMember", pay.getPayChannelCode());
				
				cardlistArray.add(tempCard);
			}
			else if (pay.getFuncNo().equals("302"))//积分扣减
			{
				pay.setPaySerNum(memberPayNo);
				
				if("qimai".equals(partnerMember)){
					com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
					tempPay.put("payType",pay.getPayType());//收款方式代号
					tempPay.put("payName",pay.getPayName());//收款方式名称
					tempPay.put("payAmount",p_realpay);//付款金额
					tempPay.put("noCode",pay.getCardNo());//卡号
					tempPay.put("isCardPay",0);//
					payslistArray.add(tempPay);
				}
				
				//
				com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				tempCard.put("cardNo",pay.getCardNo());
				tempCard.put("password",pay.getPassword());  // 卡密码，企迈会员卡支付需要密码
				tempCard.put("usePoint",pay.getDescore());//积分扣减
				tempCard.put("amount","0");//0只处理积分
				tempCard.put("getPoint","0");//0只处理积分
				//【ID1030898】//【菲尔雪3.0】会员及卡券接企迈-POS服务 by jinzma 2023
				tempCard.put("ecardSign",pay.getEcardSign());  //企迈 ecardSign字段，0实体卡，1电子卡
				//【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服务  by jinzma 20231205
				tempCard.put("partnerMember", pay.getPayChannelCode());
				
				cardlistArray.add(tempCard);
			}
			else if (pay.getFuncNo().equals("304") || pay.getFuncNo().equals("305")|| pay.getFuncNo().equals("307"))//现金券/折扣券/换购
			{
				pay.setPaySerNum(memberPayNo);
				
				if("qimai".equals(partnerMember)){
					com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
					tempPay.put("payType",pay.getPayType());//收款方式代号
					tempPay.put("payName",pay.getPayName());//收款方式名称
					tempPay.put("payAmount",p_realpay);//付款金额
					tempPay.put("noCode",pay.getCardNo());//卡号
					tempPay.put("isCardPay",0);//
					payslistArray.add(tempPay);
					
					com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
					tempCard.put("cardNo",dcpOrder.getCardNo());
					tempCard.put("password",pay.getPassword());  // 卡密码，企迈会员卡支付需要密码
					tempCard.put("amount","0");//0
					tempCard.put("usePoint","0");//积分扣减
					tempCard.put("getPoint","0");//
					//【ID1030898】//【菲尔雪3.0】会员及卡券接企迈-POS服务 by jinzma 2023
					tempCard.put("ecardSign",pay.getEcardSign());  //企迈 ecardSign字段，0实体卡，1电子卡
					//【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服务  by jinzma 20231205
					tempCard.put("partnerMember", pay.getPayChannelCode());
					cardlistArray.add(tempCard);
				}
				
				//
				com.alibaba.fastjson.JSONObject tempCoupon=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				tempCoupon.put("couponCode",pay.getCardNo());//券号
				tempCoupon.put("couponType","");//券类型
				tempCoupon.put("quantity",pay.getCouponQty());//使用张数
				tempCoupon.put("faceAmount",faceAmt);//总面额
				tempCoupon.put("buyAmount",p_realpay);//抵账金额
				//【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服务  by jinzma 20231205
				tempCoupon.put("partnerMember", pay.getPayChannelCode());
				couponlistArray.add(tempCoupon);
			}
			else if (pay.getFuncNo().equals("3011"))//禄品电影卡
			{
				//
				com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				tempCard.put("cardNo",pay.getCardNo());
				tempCard.put("amount",p_realpay);//0只处理积分
				tempCard.put("getPoint","0");//0只处理积分
				tempCard.put("cardType","LPDY");//
				tempCard.put("cardPwd",pay.getPassword());//
				cardlistArray.add(tempCard);
			}
			else if (pay.getFuncNo().equals("3012"))//四威
			{
				//
				com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				tempCard.put("cardNo",pay.getCardNo());
				tempCard.put("amount",p_realpay);//0只处理积分
				tempCard.put("getPoint","0");//0只处理积分
				tempCard.put("cardType","SIWEI_CARD");//
				tempCard.put("cardPwd",pay.getPassword());//
				cardlistArray.add(tempCard);
			}
			else if (pay.getFuncNo().equals("3013"))//乐享支付
			{
				//
				com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				tempCard.put("cardNo",pay.getCardNo());
				tempCard.put("amount",p_realpay);//0只处理积分
				tempCard.put("getPoint","0");//0只处理积分
				tempCard.put("cardType","LXYS");//
				tempCard.put("cardPwd",pay.getPassword());//
				tempCard.put("rnd1",pay.getRnd1());//
				tempCard.put("rnd2",pay.getRnd2());//
				
				cardlistArray.add(tempCard);
			}
			else if (pay.getFuncNo().equals("3014"))//聚优福利卡
			{
				//
				com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				tempCard.put("cardNo",pay.getCardNo());
				tempCard.put("amount",p_realpay);//0只处理积分
				tempCard.put("getPoint","0");//0只处理积分
				tempCard.put("cardType","JYFL");//
				cardlistArray.add(tempCard);
			}
			else
			{
              /*  //pay.setPaySerNum(memberPayNo);
                //【ID1028552】【大连大万V3.0.1.6】下单有礼的活动，零售业务场景送券，但订单业务从下订到订转销，都没有送券
                com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
                tempPay.put("payType",pay.getPayType());//收款方式代号
                tempPay.put("payName",pay.getPayName());//收款方式名称
                tempPay.put("payAmount",p_realpay);//付款金额
                tempPay.put("noCode",pay.getCardNo());//卡号
                tempPay.put("trade_no", pay.getPaySerNum());
                tempPay.put("isCardPay",0);//
                payslistArray.add(tempPay);*/
			}
		}
		if (cardlistArray.size()==0 && couponlistArray.size()==0 && payslistArray.size()==0)
		{
			error.append("没有使用会员付款方式无需调用！");
			HelpTools.writelog_waimai("【订单增加订金】没有使用会员付款方式无需调用MemberPay ，单号orderNo="+orderNo);
			return true;
		}
		
		String Yc_Url="";
		String Yc_Key=req.getApiUserCode();
		String Yc_Sign_Key=req.getApiUser().getUserKey();
		
		Yc_Url=PosPub.getCRM_INNER_URL(eId);
		if(Yc_Url.trim().equals("") || Yc_Key.trim().equals("") ||Yc_Sign_Key.trim().equals(""))
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "CrmUrl、apiUserCode、userKey移动支付接口参数未设置!");
		}
		
		com.alibaba.fastjson.JSONObject payReq=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
		com.alibaba.fastjson.JSONObject reqheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
		com.alibaba.fastjson.JSONObject signheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
		com.alibaba.fastjson.JSONArray goodslistArray=new com.alibaba.fastjson.JSONArray();
		for (orderGoodsItem detail : dcpOrder.getGoodsList())
		{
			com.alibaba.fastjson.JSONObject goods=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
			goods.put("goods_id",detail.getPluBarcode());
			goods.put("goods_name",detail.getPluName());
			goods.put("price",detail.getPrice());
			goods.put("quantity",detail.getQty());
			goods.put("amount",detail.getAmt());
			goods.put("allowPoint","0");
			goodslistArray.add(goods);
		}
		
		reqheader.put("orderNo", memberPayNo);//需唯一
		reqheader.put("businessType", "1");//业务类型0.其他1.订单下订2.订单提货3.零售支付
		reqheader.put("srcBillType", "订单下订");//实际业务单别
		reqheader.put("srcBillNo", "");//实际业务单号
		reqheader.put("orderAmount", payTot.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString());//
		reqheader.put("pointAmount", payTot.setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString());//
		reqheader.put("memberId",dcpOrder.getMemberId() );//
		
		reqheader.put("orgType", "2");//组织类型：1=公司 2=门店 3=渠道 	 总部：填公司 门店：填门店 第三方：填渠道
		reqheader.put("orgId", dcpOrder.getShopNo());//订单改成取下订门店
        if (opShopId!=null&&!opShopId.trim().isEmpty())
        {
            reqheader.put("orgId", opShopId);//订单改成操作门店
        }
		reqheader.put("oprId", dcpOrder.getOpNo());//
		reqheader.put("goodsdetail", goodslistArray);
		reqheader.put("cards", cardlistArray);
		reqheader.put("coupons", couponlistArray);
		reqheader.put("payDetail", payslistArray);
		
		
		//digiwin  鼎捷    qimai企迈   空为鼎捷
		reqheader.put("partnerMember", dcpOrder.getPartnerMember());
		reqheader.put("disc", dcpOrder.getTotDisc());
		reqheader.put("province", dcpOrder.getProvince());
		reqheader.put("city", dcpOrder.getCity());
		reqheader.put("county", dcpOrder.getCounty());
		reqheader.put("address", dcpOrder.getAddress());
		reqheader.put("getMan", dcpOrder.getGetMan());
		reqheader.put("getManTel", dcpOrder.getGetManTel());
		reqheader.put("delMemo", dcpOrder.getDelMemo());
		reqheader.put("packageFee", dcpOrder.getPackageFee());
		reqheader.put("tot_shipFee", dcpOrder.getTot_shipFee());
		reqheader.put("contTel", dcpOrder.getContTel());
		
		//
		String req_sign=reqheader.toString() + Yc_Sign_Key;
		
		req_sign= DigestUtils.md5Hex(req_sign);
		
		signheader.put("key", Yc_Key);//
		signheader.put("sign", req_sign);//md5
		
		payReq.put("serviceId", "MemberPay");
		
		payReq.put("request", reqheader);
		payReq.put("sign", signheader);
		
		HelpTools.writelog_waimai("【订单增加订金】调用会员积分接口MemberPay请求地址："+Yc_Url +"，请求key："+Yc_Key+",请求sign："+req_sign+"，单号orderNo="+orderNo);
		String str = payReq.toString();
		
		HelpTools.writelog_waimai("【订单增加订金】调用会员积分接口MemberPay请求内容："+str +" ，单号orderNo="+orderNo);
		
		String	resbody = "";
		
		//编码处理
		str= URLEncoder.encode(str,"UTF-8");
		
		resbody= HttpSend.Sendcom(str, Yc_Url);
		
		HelpTools.writelog_waimai("【订单增加订金】调用会员积分接口MemberPay返回："+resbody +"，单号orderNo="+orderNo);
		
		if (resbody.equals("")==false)
		{
			com.alibaba.fastjson.JSONObject jsonres = JSON.parseObject(resbody);
			
			String serviceDescription=jsonres.get("serviceDescription").toString();
			String serviceStatus=jsonres.get("serviceStatus").toString();
			
			//单号重复,直接查询积分
			if (serviceStatus.equals("900"))
			{
				reqheader.clear();
				signheader.clear();
				payReq.clear();
				
				reqheader.put("orderNo", memberPayNo);
				req_sign=reqheader.toString() + Yc_Sign_Key;
				req_sign=DigestUtils.md5Hex(req_sign);
				
				//
				signheader.put("key", Yc_Key);//
				signheader.put("sign", req_sign);//md5
				
				payReq.put("serviceId", "MemberPayQuery");
				
				payReq.put("request", reqheader);
				payReq.put("sign", signheader);
				
				str = payReq.toString();
				
				HelpTools.writelog_waimai("【订单增加订金】【serviceStatus=900】继续调用会员积分查询接口MemberPayQuery请求内容："+str +" ，单号orderNo="+orderNo);
				
				//编码处理
				str=URLEncoder.encode(str,"UTF-8");
				
				resbody=HttpSend.Sendcom(str, Yc_Url);
				
				HelpTools.writelog_waimai("【订单增加订金】【serviceStatus=900】继续调用会员积分查询接口MemberPayQuery返回："+resbody +" ，单号orderNo="+orderNo);
				
				if (resbody.equals("")==false)
				{
					jsonres = JSON.parseObject(resbody);
					
					serviceDescription=jsonres.get("serviceDescription").toString();
					serviceStatus=jsonres.get("serviceStatus").toString();
					if (jsonres.get("success").toString().equals("true"))
					{
						//会员支付
						bMemberPay=true;
						
						dcpOrder.setMemberPayNo(memberPayNo);//赋值 会员支付请求单号
						
						return true;
					}
					else
					{
						error.append("调用会员积分查询接口MemberPayQuery失败:" +serviceDescription );
						HelpTools.writelog_waimai("【订单增加订金】【serviceStatus=900】继续调用会员积分查询接口MemberPayQuery失败："+serviceDescription +" ，单号orderNo="+orderNo);
						return false;
					}
				}
				else
				{
					error.append("调用会员积分查询接口MemberPayQuery失败:返回为空");
					HelpTools.writelog_waimai("【订单增加订金】【serviceStatus=900】继续调用会员积分查询接口MemberPayQuery失败：返回为空 ，单号orderNo="+orderNo);
					return false;
				}
			}
			else
			{
				if (jsonres.get("success").toString().equals("true"))
				{
					//会员支付
					bMemberPay=true;
					dcpOrder.setMemberPayNo(memberPayNo);//赋值 会员支付请求单号
					return true;
				}
				else
				{
					error.append("调用会员积分查询接口MemberPay失败:" + serviceDescription);
					HelpTools.writelog_waimai("【订单增加订金】调用会员积分查询接口MemberPay失败："+serviceDescription +" ，单号orderNo="+orderNo);
					return false;
				}
			}
		}
		else
		{
			error.append("调用会员积分接口MemberPay失败:返回为空！");
			HelpTools.writelog_waimai("【订单增加订金】调用会员积分接口MemberPay失败：返回为空 ，单号orderNo="+orderNo);
			return false;
		}
		
	}
	
	/**
	 * 会员支付撤销
	 * @param req
	 * @param dcpOrder
	 * @param memberPayOrderNo
	 * @param error
	 * @return
	 * @throws Exception
	 */
	public  boolean callMemerPayReverse(DCP_OrderModify_PayAdd_OpenReq req,order dcpOrder,String memberPayOrderNo,StringBuffer error) throws Exception
	{
		if(dcpOrder == null)
		{
			error.append("order对象为空！");
			return true;
		}
		String eId = dcpOrder.geteId();
		String orderNo = dcpOrder.getOrderNo();
		HelpTools.writelog_waimai("【订单增加订金】【撤销】会员付款方式开始 ，单号orderNo="+orderNo);
		if(memberPayOrderNo==null||memberPayOrderNo.trim().isEmpty())
		{
			HelpTools.writelog_waimai("【订单增加订金】【撤销】会员付款方式,支付单号memberPayNo为空，无需调用，单号orderNo="+orderNo);
			return true;
		}
		
		String Yc_Url="";
		String Yc_Key=req.getApiUserCode();
		String Yc_Sign_Key=req.getApiUser().getUserKey();
		
		Yc_Url=PosPub.getCRM_INNER_URL(eId);
		
		if(Yc_Url.trim().isEmpty() ||  Yc_Key==null|| Yc_Key.trim().isEmpty() || Yc_Sign_Key==null|| Yc_Sign_Key.trim().isEmpty())
		{
			//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "CrmUrl、apiUserCode、userKey移动支付接口参数未设置!");
			HelpTools.writelog_waimai("【订单增加订金】【撤销】会员付款方式,CrmUrl、apiUserCode、userKey移动支付接口参数未设置，单号orderNo="+orderNo);
			error.append("CrmUrl、apiUserCode、userKey移动支付接口参数未设置");
			return false;
		}
		
		try
		{
			com.alibaba.fastjson.JSONObject payReq=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
			com.alibaba.fastjson.JSONObject reqheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
			com.alibaba.fastjson.JSONObject signheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
			
			reqheader.put("orderNo", memberPayOrderNo);//需唯一
			//
			String req_sign=reqheader.toString() + Yc_Sign_Key;
			
			req_sign=DigestUtils.md5Hex(req_sign);
			
			//
			signheader.put("key", Yc_Key);//
			signheader.put("sign", req_sign);//md5
			
			payReq.put("serviceId", "MemberPayReverse");
			
			payReq.put("request", reqheader);
			payReq.put("sign", signheader);
			
			String str = payReq.toString();
			
			HelpTools.writelog_waimai("会员撤销付款接口MemberPayReverse请求内容："+str +"，单号orderNo="+orderNo);
			
			//编码处理
			str=URLEncoder.encode(str,"UTF-8");
			
			String resbody=HttpSend.Sendcom(str, Yc_Url);
			
			HelpTools.writelog_waimai("会员撤销付款接口MemberPayReverse返回："+resbody +"，单号orderNo="+orderNo);
			
			if (resbody.equals("")==false)
			{
				com.alibaba.fastjson.JSONObject jsonres = JSON.parseObject(resbody);
				String serviceDescription=jsonres.get("serviceDescription")==null||jsonres.get("serviceDescription").toString().equals("null")?"null":jsonres.get("serviceDescription").toString();
				
				if (jsonres.get("success").toString().equals("true")==false)
				{
					error.append("调用会员撤销付款接口MemberPayReverse失败返回：" +serviceDescription);
					return false;
				}
			}
			else
			{
				error.append("调用会员撤销付款接口MemberPayReverse失败返回为空：");
				return false;
			}
			
			return true;
		}
		catch (Exception e)
		{
			error.append("调用会员撤销付款接口MemberPayReverse失败返回为空：");
			return false;
		}
	}
}
