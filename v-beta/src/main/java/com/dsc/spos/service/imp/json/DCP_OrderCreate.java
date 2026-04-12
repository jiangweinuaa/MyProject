package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderCreateRes;
import com.dsc.spos.json.cust.res.DCP_OrderCreateRes.Card;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.model.ApiUser;
import com.dsc.spos.model.JindieGoodsDetail;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
public class DCP_OrderCreate extends SPosAdvanceService<DCP_OrderCreateReq, DCP_OrderCreateRes>
{
	
	boolean bMemberPay=false;//调用会员支付标记
	@Override
	protected void processDUID(DCP_OrderCreateReq req, DCP_OrderCreateRes res) throws Exception
	{
		bMemberPay=false;
		boolean isOpen = false;//外部接口
		String token = req.getToken();
		if(token==null||token.trim().isEmpty())
		{
			isOpen = true;
		}
		
		if(isOpen)
		{
			String apiUseCode = req.getApiUserCode();
			String loadDocType = req.getApiUser().getAppType();
			String channelId = req.getApiUser().getChannelId();
			String loadDocOrderNo = req.getRequest().getLoadDocOrderNo();
			HelpTools.writelog_waimai("【外部调用订单创建DCP_OrderCreate接口】接口账号apiUseCode="+apiUseCode+"，渠道类型loadDocType="+loadDocType+"，渠道编码channelId="+channelId+"，传入的渠道类型节点渠道类型loadDocType="+req.getRequest().getLoadDocType()+",来源单号loadDocOrderNo="+loadDocOrderNo);
			if ("KDS".equalsIgnoreCase(loadDocType))
			{
				if (req.getRequest().getLoadDocType() != null && !req.getRequest().getLoadDocType().trim().isEmpty())
				{
					if (!req.getRequest().getLoadDocType().equals(loadDocType))
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "接口账号对应的渠道类型["+loadDocType+"]与传入的渠道类型节点loadDocType["+req.getRequest().getLoadDocType()+"]不一致，请检查接口账号是否使用正确！");
					}
				}
			}
			
			req.getRequest().setLoadDocType(loadDocType);
			req.getRequest().setChannelId(channelId);
			req.getRequest().seteId(req.geteId());
		}
		
		boolean isFail = false ;
		StringBuffer errMsg = new StringBuffer("");
		if (Check.Null(req.getRequest().getLoadDocOrderNo()))
		{
			errMsg.append("订单来源单号loadDocOrderNo不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getLoadDocType()))
		{
			errMsg.append("渠道类型LoadDocType不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getChannelId()))
		{
			errMsg.append("渠道编码channelId不可为空值, ");
			isFail = true;
		}
		
		if (orderLoadDocType.POS.equals(req.getRequest().getLoadDocType())||orderLoadDocType.OWNCHANNEL.equals(req.getRequest().getLoadDocType()))
		{
			if (Check.Null(req.getRequest().getShopNo()))
			{
				errMsg.append("下订门店编号shopNo不可为空值, ");
				isFail = true;
			}
			if (Check.Null(req.getRequest().getShopName()))
			{
				errMsg.append("下订门店名称shopName不可为空值, ");
				isFail = true;
			}
		}


		
		
		//【ID1035408】【阿哆诺斯升级3.0】券找零移植3.0---POS服务  by jinzma 20230828
		if (!CollectionUtils.isEmpty(req.getRequest().getCouponChangeList())){
			for (order.CouponChange couponChange : req.getRequest().getCouponChangeList()){
				
				if (Check.Null(couponChange.getCouponCode())){
					errMsg.append("找零券的券流水号不可为空值, ");
					isFail = true;
				}
				if (Check.Null(couponChange.getCouponType())){
					errMsg.append("找零券的券类型不可为空值, ");
					isFail = true;
				}
				if (!PosPub.isNumeric(couponChange.getQuantity())){
					errMsg.append("找零券的券数量不可为空值或非数值, ");
					isFail = true;
				}
				if (!PosPub.isNumericType(couponChange.getFaceAmount())){
					errMsg.append("找零券的券金额不可为空值或非数值, ");
					isFail = true;
				}
				
				String sql = " SELECT EID,COUPONCODE,COUPONTYPEID,FACEAMOUNT,STATUS"
						+ " FROM CRM_COUPON"
						+ " WHERE EID='"+req.getRequest().geteId()+"' AND COUPONCODE='"+couponChange.getCouponCode()+"' ";
				List<Map<String, Object>> getQData = this.doQueryData(sql, null);
				if (CollectionUtils.isEmpty(getQData)){
					errMsg.append("找零券:"+couponChange.getCouponCode()+" 此券号不存在 ");
					isFail = true;
				}else{
					if(!"2".equals(getQData.get(0).get("STATUS").toString())){
						errMsg.append("存在不可找零的券,券号:"+couponChange.getCouponCode()+",请重试 ");
						isFail = true;
					}
				}
				
				if (isFail) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
				}
			}
			
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}


		
		List<order> orderList = new ArrayList<order>();
		try
		{
			String eId = req.getRequest().geteId();
			String loadDocType = req.getRequest().getLoadDocType();
			if (req.getRequest().getLoadDocTypeName()==null||req.getRequest().getLoadDocTypeName().isEmpty())
			{
				req.getRequest().setLoadDocType(loadDocType);//实体类里面有赋值
			}
			String orderNo = req.getRequest().getOrderNo();
			String loadDocOrderNo = req.getRequest().getLoadDocOrderNo();
			if(orderNo==null||orderNo.isEmpty())
			{
				orderNo = loadDocOrderNo;
				req.getRequest().setOrderNo(orderNo);
			}

			//云中台渠道类型，需要调用CRM会员接口，如果是内部调用获取下接口账号
			//判断是不是 追加订单。
			if (orderLoadDocType.OWNCHANNEL.equals(req.getRequest().getLoadDocType()))
			{
				//如果是内部调用，需要赋值下接口账号。
				if (req.getApiUser()==null)
				{
					ApiUser apiUser = PosPub.getApiUserByChannelId(this.dao,req.getRequest().geteId(),req.getRequest().getChannelId());
					if (apiUser==null)
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取渠道编码:"+req.getRequest().getChannelId()+"对应的接口账号为空！");
					}
					req.setApiUser(apiUser);
                    req.setApiUserCode(apiUser.getUserCode());
				}
				if (isFailOrderRequest(req.getRequest(),errMsg))
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
				String addOrderOriginNo = req.getRequest().getAddOrderOriginNo();
				//判断是不是 追加订单商品。
				if (addOrderOriginNo != null && !addOrderOriginNo.isEmpty())
				{
					req.getRequest().setAddOrderchildNo("");//防止前端误传值
					String IsAddOrders = PosPub.getPARA_SMS(this.dao,eId,req.getRequest().getShopNo(),"IsAddOrders");
					if (!"Y".equals(IsAddOrders))
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "下单门店:"+req.getRequest().getShopNo()+"对应的追加子单参数(IsAddOrders)未设置！");
					}
					String sql_addOrderOriginNo = "select LOADDOCTYPE,SHOP,ISSHIPCOMPANY,STATUS,PAYSTATUS,PRODUCTSTATUS,DELIVERYSTATUS,ADDORDERCHILDNO from dcp_order where eid='"+eId+"' and  orderno='"+addOrderOriginNo+"'";
					List<Map<String, Object>> getQData = this.doQueryData(sql_addOrderOriginNo, null);
					if (CollectionUtils.isEmpty(getQData))
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "需要追加的原订单:"+addOrderOriginNo+"不存在！");
					}
					String originOrder_status = getQData.get(0).get("STATUS").toString();
					String originOrder_payStatus = getQData.get(0).get("PAYSTATUS").toString();
					String originOrder_productStatus = getQData.get(0).get("PRODUCTSTATUS").toString();
					String originOrder_deliveryStatus = getQData.get(0).get("DELIVERYSTATUS").toString();
					String originOrder_addOrderChildNo = getQData.get(0).get("ADDORDERCHILDNO").toString();
					String originOrder_loadDocType = getQData.get(0).get("LOADDOCTYPE").toString();
                    String originOrder_isShopCompany = getQData.get(0).get("ISSHIPCOMPANY").toString();//是否总部（生产）
					boolean isFailAddOrder = false;
					errMsg = new StringBuffer("");
					if (!orderLoadDocType.POS.equals(originOrder_loadDocType)&&!orderLoadDocType.POSANDROID.equals(originOrder_loadDocType)&&!orderLoadDocType.QIMAI.equals(originOrder_loadDocType))
					{
						//isFailAddOrder = true;
						//errMsg.append("原订单"+addOrderOriginNo+"渠道类型是"+originOrder_loadDocType+"不可追加商品,只有POS渠道或启迈渠道类型才能追加");
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "原订单"+addOrderOriginNo+"渠道类型是"+originOrder_loadDocType+"不可追加商品,只有POS渠道或启迈渠道才能追加");
					}
                    if ("Y".equals(originOrder_isShopCompany))
                    {
                        isFailAddOrder = true;
                        errMsg.append("订单为总部生产,");
                    }
					if (!"0".equals(originOrder_status)&&!"1".equals(originOrder_status)&&!"2".equals(originOrder_status))
					{
						isFailAddOrder = true;
						errMsg.append("订单状态为"+originOrder_status+"(必须是[0,1,2]才可以),");
					}
					if (!"3".equals(originOrder_payStatus))
					{
						isFailAddOrder = true;
						errMsg.append("未全额支付，支付状态为"+originOrder_payStatus+",");
					}
					if (!originOrder_deliveryStatus.isEmpty())
					{
						isFailAddOrder = true;
						errMsg.append("已有配送状态为"+originOrder_deliveryStatus+",");
					}
					if (!originOrder_productStatus.isEmpty())
					{
						isFailAddOrder = true;
						errMsg.append("已有生产状态为"+originOrder_productStatus+",");
					}
					if (!originOrder_addOrderChildNo.isEmpty())
					{
						isFailAddOrder = true;
						errMsg.append("已追加过子单单号为"+originOrder_addOrderChildNo+",");
					}
					if (isFailAddOrder)
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "原订单"+addOrderOriginNo+errMsg.toString()+"无法追加商品！");
					}

				}

			}
			
			if(IsExistsOrderNo(eId, orderNo))
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("此订单号已存在！");
				return;
				
			}
			
			if(res.getDatas()==null)
			{
				res.setDatas(res.new responseDatas());
				res.getDatas().setVipDatas(res.new level2());
			}
			if (!Check.Null(req.getRequest().getManualNo()))
			{
				if (req.getRequest().getManualNo().length()>40)
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "录入的手工单号("+req.getRequest().getManualNo()+")超过最大长度40,请检查！");
				}
			}
			
			StringBuffer errorSB = new StringBuffer();
			if(Check.Null(req.getRequest().getPartnerMember()))
			{		
				if(orderLoadDocType.QIMAI.equals(req.getRequest().getLoadDocType()))//如果是企迈渠道订单，会员渠道赋值企迈
				{
					req.getRequest().setPartnerMember("qimai") ;
				}else
				{
					req.getRequest().setPartnerMember("digiwin") ;
				}
			}
			//有赞、晓柚渠道，根据plubarcode查询下pluno、unit等
			if (orderLoadDocType.YOUZAN.equals(req.getRequest().getLoadDocType())
					||orderLoadDocType.QIMAI.equals(req.getRequest().getLoadDocType())
					||orderLoadDocType.XIAOYOU.equals(req.getRequest().getLoadDocType()))
			{
				if (orderLoadDocType.QIMAI.equals(req.getRequest().getLoadDocType()))
				{
					if ("2".equals(req.getRequest().getStatus()))
					{
						req.getRequest().setStatus("1");
						HelpTools.writelog_waimai("渠道类型="+req.getRequest().getLoadDocType()+"订单状态status有2强制转换成1，单号orderNo="+orderNo);
					}
					
				}
				HelpTools.updateOrderDetailInfo(req.getRequest(), errorSB);
				String dcType ="27";
				String dcTypeName ="三方电商折扣";
				for (orderGoodsItem goodsItem : req.getRequest().getGoodsList())
				{
					goodsItem.setAgioInfo(new ArrayList<orderGoodsItemAgio>());//清空之前得折扣
					if (!"3".equals(goodsItem.getPackageType()))//子商品不添加
					{
						if (Math.abs(goodsItem.getDisc())>0)
						{
							orderGoodsItemAgio agio = new orderGoodsItemAgio();
							agio.setDcType(dcType);
							agio.setDcTypeName(dcTypeName);
							agio.setAmt(goodsItem.getAmt());
							agio.setDisc(goodsItem.getDisc());// 原始账
							agio.setDisc_merReceive(0);// 嘉华实收
							agio.setDisc_custPayReal(0);// 顾客实付
							agio.setQty(goodsItem.getQty());
							agio.setItem("1");
							goodsItem.getAgioInfo().add(agio);
						}
						
					}
					
				}
			}
			
			////调用下订单接入的参数设置逻辑
			
			HelpTools.updateOrderFunction(req.getRequest(), errorSB);
			StringBuffer errorMemberPay = new StringBuffer();
			boolean callMemberPayFlag = this.callMemerPay(req, res, errorMemberPay);
			if(!callMemberPayFlag)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorMemberPay.toString());
			}
			
			//台湾税额计算
			StringBuffer errorInvoiceCaculate = new StringBuffer("");
			boolean callTWInvoiceCaculate =	HelpTools.OrderInvoiceCaculate(req.getRequest(), req.getLangType(), errorInvoiceCaculate);
			
			if(!callTWInvoiceCaculate)
			{
				StringBuffer errorMemberPayReverse = new StringBuffer("");
				//这里税额计算失败，还需要调用MemberPay撤销
				String memberPayNo = req.getRequest().getMemberPayNo();
				if (bMemberPay && memberPayNo != null && memberPayNo.trim().isEmpty() == false)
				{
					callMemerPayReverse(req, res, errorMemberPayReverse);
				}
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorInvoiceCaculate.toString()+","+errorMemberPayReverse.toString());
			}
			
			//这里判断下 是不是大客户赊销订单
			boolean isSellCredit = false;
			String creditAmt = "0";
			if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID)||loadDocType.equals(orderLoadDocType.OWNCHANNEL))
			{
				if(req.getRequest().getPay()!=null)
				{
					for (orderPay payItem : req.getRequest().getPay())
					{
						if(payItem.getFuncNo()!=null&&payItem.getFuncNo().equals("601"))
						{
							isSellCredit = true;
							req.getRequest().setSellCredit("Y");
							creditAmt = payItem.getPay();
							break;
						}
					}
				}
			}
			
			if(isSellCredit||"Y".equals(req.getRequest().getSellCredit()))
			{
				if(req.getRequest().getCustomer()==null||req.getRequest().getCustomer().isEmpty()||req.getRequest().getCustomerName()==null||req.getRequest().getCustomerName().isEmpty())
				{
					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription("赊销订单，客户编码 customer不能为空，客户名称 customerName不能为空");
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "赊销订单，客户编码 customer不能为空，客户名称 customerName不能为空");
				}
			}
			
			
			//赊销订单调用CRM接口
			String errorMsg="";
			if(isSellCredit)
			{
				List<JindieGoodsDetail>  details=new ArrayList<>();
				
				for (orderGoodsItem reqGoods : req.getRequest().getGoodsList())
				{
					//普通商品或套餐子商品金额累加
					if (reqGoods.getPackageType()!=null && (reqGoods.getPackageType().equals("1") || reqGoods.getPackageType().equals("3")))
					{
						JindieGoodsDetail lv2Detail=new JindieGoodsDetail();
						lv2Detail.setItem(Integer.parseInt(reqGoods.getItem()));
						lv2Detail.setoItem(0);
						lv2Detail.setoQty(BigDecimal.ZERO);
						lv2Detail.setQty(new BigDecimal(reqGoods.getQty()).setScale(2, RoundingMode.HALF_UP));
						lv2Detail.setPluNo(reqGoods.getPluNo());
						lv2Detail.setUnitId(reqGoods.getsUnit());
						lv2Detail.setOldPrice(new BigDecimal(reqGoods.getOldPrice()).setScale(2, RoundingMode.HALF_UP));
						lv2Detail.setPrice(new BigDecimal(reqGoods.getPrice()).setScale(2, RoundingMode.HALF_UP));
						//抹零会算在支付折扣，取DISC_MERRECEIVE+DISC
						lv2Detail.setDisc(new BigDecimal(reqGoods.getDisc()).add(new BigDecimal(reqGoods.getDisc_merReceive())).setScale(2, RoundingMode.HALF_UP));
						//抹零会算在支付折扣，取AMT_MERRECEIVE
						lv2Detail.setAmt(new BigDecimal(reqGoods.getAmt_merReceive()).setScale(2, RoundingMode.HALF_UP));
						lv2Detail.setOldAmt(lv2Detail.getAmt().add(lv2Detail.getDisc()).setScale(2, RoundingMode.HALF_UP));
						lv2Detail.setMemo("");
						details.add(lv2Detail);
					}
					
				}
				
				boolean success_credit=false;
				try
				{
					double dealCreditAmount = Double.parseDouble(creditAmt);
					StringBuffer error_sellCredit = new StringBuffer("");
					
					//金蝶赊销增加营业日期
					String bdate=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					if (req.getRequest().getbDate()!=null && req.getRequest().getbDate().length()==8)
					{
						bdate=req.getRequest().getbDate().substring(0,4)+"-" + req.getRequest().getbDate().substring(4,6) +"-" +req.getRequest().getbDate().substring(6,8);
					}
					
					success_credit=HelpTools.CustomerCreditUpdate(eId, req.getApiUserCode(), req.getApiUser().getUserKey(), req.getLangType(), loadDocOrderNo, req.getRequest().getBelfirm(),
							req.getRequest().getShopNo(), "1",req.getRequest().getOpNo(),req.getRequest().getMemo(),"",details,
							req.getRequest().getCustomer(), dealCreditAmount, error_sellCredit,bdate);
				}
				catch (Exception e)
				{
					errorMsg=e.getMessage();
				}
				//赊销调用失败，不能进行下去
				if (!success_credit)
				{
					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription(errorMsg);
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorMsg);
				}
			}
			
			
			orderList.add(req.getRequest());
			
			//订单插入
			StringBuffer errorMessage=new StringBuffer();
			
			ArrayList<DataProcessBean> DPB = com.dsc.spos.waimai.HelpTools.GetInsertOrderCreat(orderList,errorMessage,res.getDatas().getVipDatas().getCardsInfo());
			if (DPB != null && DPB.size() > 0)
			{
				if(DPB.size()==1)
				{
					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription("新增订单异常：订单只有单头没有单身！");
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "订单只有单头没有单身！");
				}
				
				for (DataProcessBean dataProcessBean : DPB)
				{
					this.addProcessData(dataProcessBean);
				}
				if (orderLoadDocType.OWNCHANNEL.equals(req.getRequest().getLoadDocType()))
				{
					if (req.getRequest().getAddOrderOriginNo()!=null&&!req.getRequest().getAddOrderOriginNo().isEmpty())
					{
						//追加商品子单，更新原订单ADDORDERCHILDNO字段
						UptBean up1 = new UptBean("DCP_ORDER");
						up1.addUpdateValue("ADDORDERCHILDNO",new DataValue(req.getRequest().getOrderNo(),Types.VARCHAR));

						up1.addCondition("EID",new DataValue(eId,Types.VARCHAR));
						up1.addCondition("ORDERNO",new DataValue(req.getRequest().getAddOrderOriginNo(),Types.VARCHAR));
						this.addProcessData(new DataProcessBean(up1));
					}

				}
				try
				{
					this.doExecuteDataToDB();
					HelpTools.writelog_waimai("【保存数据库成功】，单号orderNo="+orderNo);
				}
				catch (Exception e)
				{
					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription("新增订单异常："+e.getMessage());
					HelpTools.writelog_waimai("【保存数据库失败】，单号orderNo="+orderNo+",异常:"+e.getMessage());
					StringBuffer errorMemberPayReverse = new StringBuffer("");
					//这里税额计算失败，还需要调用MemberPay撤销
					String memberPayNo = req.getRequest().getMemberPayNo();
					if (bMemberPay && memberPayNo != null && memberPayNo.trim().isEmpty() == false)
					{
						callMemerPayReverse(req, res, errorMemberPayReverse);
					}
					return;
				}
				
				
				res.getDatas().setBillNo(orderNo);
				res.getDatas().setInvoiceInfo(res.new PosInvoce());
				
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功！");
				
				//商品资料异常
				HelpTools.waimaiOrderAbnormalSave(req.getRequest(), errorMessage);
				
				//嘉华目前对接的自定义渠道类型=SELFDEFINE，写下缓存
				if(loadDocType.equals(orderLoadDocType.SELFDEFINE)
						||loadDocType.equals(orderLoadDocType.YOUZAN)
						||loadDocType.equals(orderLoadDocType.XIAOYOU)
						||orderLoadDocType.QIMAI.equals(loadDocType))
				{
					if (req.getRequest().getShopNo()!=null&&req.getRequest().getShopNo().trim().isEmpty()==false)
					{
						boolean isNeedWriteRedis = true;
						//枚举值：1、接单并打印（不传默认为1） 2、不接单不打印 3、接单不打印
						if ("2".equals(req.getRequest().getPrintSet()))
						{
							//暂时启迈渠道类型，不接单不打印 就不写缓存，POS就不会自动
							if (orderLoadDocType.QIMAI.equals(loadDocType))
							{
								isNeedWriteRedis = false;
								HelpTools.writelog_waimai(
										"渠道类型loadDocType="+loadDocType+",节点printSet=" +req.getRequest().getPrintSet()+"，【不用写缓存,pos端不接单不打印】,单号orderNo=" + req.getRequest().getOrderNo());
							}
							
						}
						
						if (isNeedWriteRedis)
						{
							try
							{
								ParseJson pj = new ParseJson();
								String Response_json = pj.beanToJson(req.getRequest());
								String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + req.getRequest().getShopNo();
								String hash_key = req.getRequest().getOrderNo();
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
								// TODO: handle exception
								HelpTools.writelog_waimai(
										"单号orderNo="+ req.getRequest().getOrderNo()+",渠道类型loadDocType="+loadDocType+",【写缓存】异常:"+e.getMessage());
							}
						}
						
						
					}
				}
				else if (loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID))
				{
					//pos的门店订单，配送门店、生产门店与下单门店不一致，需要写缓存
					String shop_create = req.getRequest().getShopNo();
					String shop_shipping = req.getRequest().getShippingShopNo();
					String shop_mach = req.getRequest().getMachShopNo();
					if ("Y".equals(req.getRequest().getCanModify()))
					{
						HelpTools.writelog_waimai(
								"稍后补录的订单，先不写缓存，单号orderNo="+ req.getRequest().getOrderNo()+",渠道类型loadDocType="+loadDocType);
					}
					else
					{
						try
						{
							
							ParseJson pj = new ParseJson();
							String Response_json = pj.beanToJson(req.getRequest());
							
							if(shop_shipping!=null&&!shop_shipping.isEmpty()&&!shop_shipping.equals(shop_create))
							{
								String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shop_shipping;
								String hash_key = req.getRequest().getOrderNo();
								HelpTools.writelog_waimai(
										"渠道类型loadDocType="+loadDocType+",【配送门店】【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
								RedisPosPub redis = new RedisPosPub();
								boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
								if (nret) {
									HelpTools.writelog_waimai("【配送门店】【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
								} else {
									HelpTools.writelog_waimai("【配送门店】【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
								}
							}
							
							//生产门店与其他2个门店不一致
							if(shop_mach!=null&&!shop_mach.isEmpty()&&!shop_mach.equals(shop_shipping)&&!shop_mach.equals(shop_create))
							{
								String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shop_mach;
								String hash_key = req.getRequest().getOrderNo();
								HelpTools.writelog_waimai(
										"渠道类型loadDocType="+loadDocType+",【生产门店】【开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
								RedisPosPub redis = new RedisPosPub();
								boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
								if (nret) {
									HelpTools.writelog_waimai("【生产门店】【写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
								} else {
									HelpTools.writelog_waimai("【生产门店】【写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
								}
							}
							
						}
						catch (Exception e)
						{
							// TODO: handle exception
							HelpTools.writelog_waimai(
									"单号orderNo="+ req.getRequest().getOrderNo()+",渠道类型loadDocType="+loadDocType+",【写缓存】异常:"+e.getMessage());
						}
					}
					
				}
				
				
				
				try
				{
					//单据保存成功之后，台湾调用发票开立
					StringBuffer errorInvoiceCreate = new StringBuffer();
					JSONObject invoiceCreateResObj = new JSONObject();
					boolean callTWInvoiceCreate = HelpTools.OrderInvoiceCreate(req.getRequest(), req.getLangType(), invoiceCreateResObj,errorInvoiceCreate);
					if(callTWInvoiceCreate)
					{
						ParseJson pj = new  ParseJson();
						String invoiceCreateResObj_json = invoiceCreateResObj.toString();
						DCP_OrderCreateRes.PosInvoce invoice_res_model = pj.jsonToBean(invoiceCreateResObj_json, new TypeToken<DCP_OrderCreateRes.PosInvoce>(){});
						if(invoice_res_model!=null)
						{
							res.getDatas().setInvoiceInfo(invoice_res_model);
							String invoiceDate = invoice_res_model.getInvoiceDate();//开票日期YYYY-MM-DD
							String invOperateType = invoice_res_model.getInvOperateType();//记录类型：0-开立 1-作废 2-折让
							String invNo = invoice_res_model.getInvNo();//发票号码
							
							HelpTools.writelog_waimai("【更新发票信息开始】发票日期invoiceDate="+invoiceDate + ",记录类型invOperateType="+invOperateType+",发票号码invNo="+invNo+", 订单号orderNo:" + orderNo);
							//更新发票信息
							this.pData.clear();
							UptBean ub1 = new UptBean("DCP_ORDER");
							ub1.addUpdateValue("INVOICEDATE", new DataValue(invoiceDate, Types.VARCHAR));
							ub1.addUpdateValue("INVOPERATETYPE", new DataValue(invOperateType, Types.VARCHAR));
							ub1.addUpdateValue("INVNO", new DataValue(invNo, Types.VARCHAR));
							ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
							ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
							ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
							ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							this.addProcessData(new DataProcessBean(ub1));
							
							
							UptBean ub2 = new UptBean("DCP_ORDER_DETAIL");
							
							ub2.addUpdateValue("INVITEM", new DataValue("1", Types.VARCHAR));
							ub2.addUpdateValue("INVNO", new DataValue(invNo, Types.VARCHAR));
							
							ub2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
							ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							this.addProcessData(new DataProcessBean(ub2));
							
							
							this.doExecuteDataToDB();
							HelpTools.writelog_waimai("【更新发票信息成功】" + " 订单号orderNo:" + orderNo);
							
							
						}
						
					}
					else
					{
						if(req.getRequest().getExceptionStatus()!=null&&req.getRequest().getExceptionStatus().equals("Y"))
						{
							try
							{
								this.pData.clear();
								//更新发票信息
								UptBean ub1 = new UptBean("DCP_ORDER");
								ub1.addUpdateValue("EXCEPTIONSTATUS", new DataValue("Y", Types.VARCHAR));
								ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
								ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
								
								ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
								ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
								this.addProcessData(new DataProcessBean(ub1));
								this.doExecuteDataToDB();
							}
							catch (Exception e)
							{
								this.pData.clear();
							}
							
						}
					}
					
				}
				catch (Exception e)
				{
					// TODO: handle exception
					this.pData.clear();
				}
				
				
				//【ID1035408】【阿哆诺斯升级3.0】券找零移植3.0---POS服务 by jinzma 20230828
				if (!CollectionUtils.isEmpty(req.getRequest().getCouponChangeList())) {
					try {
						callMemberPayCouponChange(req.getApiUserCode(), req.getApiUser().getUserKey(),
								eId,req.getRequest().getMemberPayNo(), req.getRequest().getCouponChangeList());
						
					} catch (Exception e) {
					
					}
				}
				
				
				//写日志
				
				//region 写订单日志
				// 写订单日志
				List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
				orderStatusLog onelv1 = new orderStatusLog();
				onelv1.setLoadDocType(loadDocType);
				onelv1.setChannelId(req.getRequest().getChannelId());
				onelv1.setLoadDocBillType(req.getRequest().getLoadDocBillType());
				onelv1.setLoadDocOrderNo(req.getRequest().getLoadDocOrderNo());
				onelv1.seteId(eId);
				
				String o_opName = req.getRequest().getOpName();
				if (o_opName==null||o_opName.isEmpty())
				{
					o_opName = req.getOpName();
				}
				
				
				
				onelv1.setOpName(o_opName);
				onelv1.setOpNo(req.getRequest().getOpNo());
				onelv1.setShopNo(req.getRequest().getShopNo());
				onelv1.setOrderNo(orderNo);
				onelv1.setMachShopNo(req.getRequest().getMachShopNo());
				onelv1.setShippingShopNo(req.getRequest().getShippingShopNo());
				String statusType = "";
				String updateStaus = req.getRequest().getStatus();
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
				if (updateStaus.equals("0")||updateStaus.equals("1"))
				{
					onelv1.setDisplay("0");
				}
				
				String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				onelv1.setUpdate_time(updateDatetime);
				
				orderStatusLogList.add(onelv1);
				
				StringBuilder errorStatusLogMessage = new StringBuilder();
				boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
				if (nRet) {
					HelpTools.writelog_waimai("【写表dcp_orderStatuslog保存成功】" + " 订单号orderNo:" + orderNo);
				} else {
					HelpTools.writelog_waimai(
							"【写表dcp_orderStatuslog异常】" + errorMessage.toString() + " 订单号orderNo:" + orderNo);
				}
				this.pData.clear();
				
				//endregion
				
			}
			else
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("传入的参数异常，新增订单失败！");
				String exStr = errorMessage.toString();
				if(exStr.isEmpty()==false)
				{
					res.setServiceDescription(exStr);
				}
			}
			
		}
		catch (SPosCodeException e)
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("新增订单异常："+e.getMessage());
		}
		catch (Exception e)
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("新增订单异常："+e.getMessage());
		}
		
		
	}
	
	Map<String, Object> matchshop(DCP_OrderCreateReq req, String lon, String lat) throws Exception
	{
		// 是否大蛋糕
		try
		{
		} catch (Exception ex)
		{
			// 匹配报错
		}
		// 没有最后就用距离匹配
		return null;
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderCreateReq req) throws Exception
	{
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderCreateReq req) throws Exception
	{
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderCreateReq req) throws Exception
	{
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_OrderCreateReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
		
		if (req.getRequest() == null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		order order = req.getRequest();
		
		if (Check.Null(order.geteId()))
		{
			/*errCt++;
			errMsg.append("企业编号EID不可为空值, ");
			isFail = true;*/
		}

		/*if (Check.Null(order.getLoadDocOrderNo()))
		{
			errCt++;
			errMsg.append("订单来源单号loadDocOrderNo不可为空值, ");
			isFail = true;
		}
		if (Check.Null(order.getLoadDocType()))
		{
			errCt++;
			errMsg.append("渠道类型LoadDocType不可为空值, ");
			isFail = true;
		}
		if (Check.Null(order.getChannelId()))
		{
			errCt++;
			errMsg.append("渠道编码channelId不可为空值, ");
			isFail = true;
		}

		if (!Check.Null(order.getLoadDocType()) && order.getLoadDocType().equals("4"))
		{
			if (Check.Null(order.getShopNo()))
			{
				errCt++;
				errMsg.append("下订门店编号shopNo不可为空值, ");
				isFail = true;
			}
			if (Check.Null(order.getShopName()))
			{
				errCt++;
				errMsg.append("下订门店名称shopName不可为空值, ");
				isFail = true;
			}
		}*/
		
		if (Check.Null(order.getStatus()))
		{
			errCt++;
			errMsg.append("订单状态status不可为空值, ");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
		
	}
	
	@Override
	protected TypeToken<DCP_OrderCreateReq> getRequestType()
	{
		return new TypeToken<DCP_OrderCreateReq>() {};
	}
	
	@Override
	protected DCP_OrderCreateRes getResponseType()
	{
		return new DCP_OrderCreateRes();
	}
	
	private boolean IsExistsOrderNo(String eId,  String orderNo) throws Exception
	{
		String sql = "Select * from DCP_ORDER WHERE EID='" + eId + "'  and ORDERNO='" + orderNo + "' ";
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.size() > 0)
		{
			return true;
		}
		
		return false;
	}
	
	private void SaveRedis(String redis_key, String hash_key, String hash_value) throws Exception
	{
		try
		{
			RedisPosPub redis = new RedisPosPub();
			String Response_json = hash_value;
			// Response_json = Response_json.replace("\"[{",
			// "[{").replace("}]\"", "}]").replace("\"{", "{").replace("}\"",
			// "}");
			boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
			if (isexistHashkey)
			{
				redis.DeleteHkey(redis_key, hash_key);//
				HelpTools.writelog_waimai(
						"【WDM删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
			}
			HelpTools.writelog_waimai(
					"【WDM开始写缓存】" + "redis_key:" + redis_key + " hash_key:" + hash_key + " hash_value:" + Response_json);
			boolean nret = redis.setHashMap(redis_key, hash_key, Response_json);
			if (nret)
			{
				HelpTools.writelog_waimai("【WDM写缓存】OK" + " redis_key:" + redis_key + " hash_key:" + hash_key);
			} else
			{
				HelpTools.writelog_waimai("【WDM写缓存】Error" + " redis_key:" + redis_key + " hash_key:" + hash_key);
			}
			redis.Close();
			
		} catch (Exception e)
		{
			HelpTools.writelog_waimai(
					"【WDM写缓存】异常 " + e.getMessage() + " redis_key:" + redis_key + " hash_key:" + hash_key);
		}
	}
	
	/**
	 * 订单新建调用会员memberPay
	 * @param req
	 * @param res
	 * @param error
	 * @return
	 * @throws Exception
	 */
	public  boolean callMemerPay(DCP_OrderCreateReq req,DCP_OrderCreateRes res,StringBuffer error) throws Exception
	{
		//error = new StringBuffer("");
		order dcpOrder = req.getRequest();
		
		if(dcpOrder == null)
		{
			error.append("order对象为空！");
			return true;
		}
		String loadDocType = dcpOrder.getLoadDocType();
		if(loadDocType.equals(orderLoadDocType.POS)||loadDocType.equals(orderLoadDocType.POSANDROID)||loadDocType.equals(orderLoadDocType.OWNCHANNEL))
		{
			
		}
		else
		{
			error.append("渠道类型loadDocType="+loadDocType+"无需调用！");
			return true;
		}
		
		List<orderPay> orderPayList = dcpOrder.getPay();
		if(orderPayList==null||orderPayList.isEmpty())
		{
			error.append("没有付款方式无需调用！");
			return true;
		}
		String eId = dcpOrder.geteId();
		String orderNo = dcpOrder.getOrderNo();
		HelpTools.writelog_waimai("【订单创建】检查是否使用会员付款方式开始 ，单号orderNo="+orderNo);
		
		String memberPayNo=PosPub.getGUID(false);//调用积分memberpay的orderno
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
				//【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服 by jinzma 20231205
				tempCard.put("partnerMember",pay.getPayChannelCode());
				
				cardlistArray.add(tempCard);
			}else
			if (pay.getFuncNo().equals("302"))//积分扣减
			{
				pay.setPaySerNum(memberPayNo);
				
				//if("qimai".equals(partnerMember)){
				if("qimai".equals(pay.getPayChannelCode())){
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
				//【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服 by jinzma 20231205
				tempCard.put("partnerMember",pay.getPayChannelCode());
				
				cardlistArray.add(tempCard);
			} else
			if (pay.getFuncNo().equals("304") || pay.getFuncNo().equals("305")|| pay.getFuncNo().equals("307"))//现金券/折扣券/换购
			{
				pay.setPaySerNum(memberPayNo);
				
				//if("qimai".equals(partnerMember)){
				if("qimai".equals(pay.getPayChannelCode())){
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
					//【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服 by jinzma 20231205
					tempCard.put("partnerMember",pay.getPayChannelCode());
					
					cardlistArray.add(tempCard);
				}
				
				//
				com.alibaba.fastjson.JSONObject tempCoupon=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				tempCoupon.put("couponCode",pay.getCardNo());//券号
				tempCoupon.put("couponType","");//券类型
				tempCoupon.put("quantity",pay.getCouponQty());//使用张数
				tempCoupon.put("faceAmount",faceAmt);//总面额
				tempCoupon.put("buyAmount",p_realpay);//抵账金额
				//【ID1037473】【3.0嘉华】合同需求：公司预付卡业务---券核销---服 by jinzma 20231205
				tempCoupon.put("partnerMember",pay.getPayChannelCode());
				
				couponlistArray.add(tempCoupon);
			} else
			if (pay.getFuncNo().equals("3011"))//禄品电影卡
			{
				//
				com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				tempCard.put("cardNo",pay.getCardNo());
				tempCard.put("amount",p_realpay);//0只处理积分
				tempCard.put("getPoint","0");//0只处理积分
				tempCard.put("cardType","LPDY");//
				tempCard.put("cardPwd",pay.getPassword());//
				cardlistArray.add(tempCard);
			} else
			if (pay.getFuncNo().equals("3012"))//四威
			{
				//
				com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				tempCard.put("cardNo",pay.getCardNo());
				tempCard.put("amount",p_realpay);//0只处理积分
				tempCard.put("getPoint","0");//0只处理积分
				tempCard.put("cardType","SIWEI_CARD");//
				tempCard.put("cardPwd",pay.getPassword());//
				cardlistArray.add(tempCard);
			}  else
			if (pay.getFuncNo().equals("3013"))//乐享支付
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
			} else
			if (pay.getFuncNo().equals("3014"))//聚优福利卡
			{
				//
				com.alibaba.fastjson.JSONObject tempCard=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				tempCard.put("cardNo",pay.getCardNo());
				tempCard.put("amount",p_realpay);//0只处理积分
				tempCard.put("getPoint","0");//0只处理积分
				tempCard.put("cardType","JYFL");//
				cardlistArray.add(tempCard);
			} else
			{
				//pay.setPaySerNum(memberPayNo);
				//【ID1028552】【大连大万V3.0.1.6】下单有礼的活动，零售业务场景送券，但订单业务从下订到订转销，都没有送券
				com.alibaba.fastjson.JSONObject tempPay=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
				tempPay.put("payType",pay.getPayType());//收款方式代号
				tempPay.put("payName",pay.getPayName());//收款方式名称
				tempPay.put("payAmount",p_realpay);//付款金额
				tempPay.put("noCode",pay.getCardNo());//卡号
				tempPay.put("trade_no", pay.getPaySerNum());
				tempPay.put("isCardPay",0);//
				payslistArray.add(tempPay);
			}
		}

//		if (payslistArray.size()==0 &&  cardlistArray.size()==0 && couponlistArray.size()==0) 
		//【ID1028552】【大连大万V3.0.1.6】下单有礼的活动，零售业务场景送券，但订单业务从下订到订转销，都没有送券
		if (cardlistArray.size()==0 && couponlistArray.size()==0 && Check.Null(dcpOrder.getMemberId()))
		{
			error.append("没有使用会员付款方式无需调用！");
			HelpTools.writelog_waimai("【订单创建】没有使用会员付款方式无需调用MemberPay ，单号orderNo="+orderNo);
			return true;
		}
		
		
		//积分累计或301消费扣款处理
		
		
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
		
		req_sign=DigestUtils.md5Hex(req_sign);
		
		//
		signheader.put("key", Yc_Key);//
		signheader.put("sign", req_sign);//md5
		
		payReq.put("serviceId", "MemberPay");
		
		payReq.put("request", reqheader);
		payReq.put("sign", signheader);
		
		HelpTools.writelog_waimai("【订单创建】调用会员积分接口MemberPay请求地址："+Yc_Url +"，请求key："+Yc_Key+",请求sign："+req_sign+"，单号orderNo="+orderNo);
		String str = payReq.toString();
		
		HelpTools.writelog_waimai("【订单创建】调用会员积分接口MemberPay请求内容："+str +" ，单号orderNo="+orderNo);
		
		String	resbody = "";
		
		//编码处理
		str=URLEncoder.encode(str,"UTF-8");
		
		resbody=HttpSend.Sendcom(str, Yc_Url);
		
		HelpTools.writelog_waimai("【订单创建】调用会员积分接口MemberPay返回："+resbody +"，单号orderNo="+orderNo);
		
		//获得积分
		BigDecimal getPoint=new BigDecimal(0);
		
		if(res.getDatas()==null)
		{
			res.setDatas(res.new responseDatas());
		}
		
		if (resbody.equals("")==false)
		{
			com.alibaba.fastjson.JSONObject jsonres = JSON.parseObject(resbody);
			String serviceDescription="";
			String serviceStatus="000";
			if(null!=jsonres.get("serviceDescription"))
			{
				serviceDescription=jsonres.get("serviceDescription").toString();
			}
			if(null!=jsonres.get("serviceStatus"))
			{
				serviceDescription=jsonres.get("serviceStatus").toString();
			}
			
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
				
				HelpTools.writelog_waimai("【订单创建】【serviceStatus=900】继续调用会员积分查询接口MemberPayQuery请求内容："+str +" ，单号orderNo="+orderNo);
				
				//编码处理
				str=URLEncoder.encode(str,"UTF-8");
				
				resbody=HttpSend.Sendcom(str, Yc_Url);
				
				HelpTools.writelog_waimai("【订单创建】【serviceStatus=900】继续调用会员积分查询接口MemberPayQuery返回："+resbody +" ，单号orderNo="+orderNo);
				
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
						
						res.getDatas().getVipDatas().setCardsInfo(new ArrayList<Card>());
						
						com.alibaba.fastjson.JSONArray cardsList=jsonres.getJSONObject("datas").getJSONArray("cards");
						for (int pi = 0; pi < cardsList.size(); pi++)
						{
							//多张卡累加积分
							getPoint=getPoint.add(new BigDecimal(cardsList.getJSONObject(pi).getDouble("getPoint")));
							
							Card card=res.new Card();
							card.setAmount(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount")));
							card.setAmount1(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1")));
							card.setAmount1_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_after")));
							card.setAmount2(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2")));
							card.setAmount2_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_after")));
							card.setAmount_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_after")));
							card.setAmount1_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_before")));
							card.setAmount2_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_before")));
							card.setAmount_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_before")));
							card.setCardNo(cardsList.getJSONObject(pi).getString("cardNo"));
							card.setGetPoint(getPoint.toPlainString());
							card.setPoint_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_after")));
							card.setPoint_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_before")));
							
							res.getDatas().getVipDatas().getCardsInfo().add(card);
							
							//
							res.getDatas().getVipDatas().setAmount(card.getAmount_after());
							res.getDatas().getVipDatas().setGetPoints(card.getGetPoint());
							res.getDatas().getVipDatas().setMemberId(dcpOrder.getMemberId());
							res.getDatas().getVipDatas().setPoints(card.getPoint_after());
						}
						return true;
					}
					else
					{
						error.append("调用会员积分查询接口MemberPayQuery失败:" +serviceDescription );
						HelpTools.writelog_waimai("【订单创建】【serviceStatus=900】继续调用会员积分查询接口MemberPayQuery失败："+serviceDescription +" ，单号orderNo="+orderNo);
						return false;
					}
				}
				else
				{
					error.append("调用会员积分查询接口MemberPayQuery失败:返回为空");
					HelpTools.writelog_waimai("【订单创建】【serviceStatus=900】继续调用会员积分查询接口MemberPayQuery失败：返回为空 ，单号orderNo="+orderNo);
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
					res.getDatas().getVipDatas().setCardsInfo(new ArrayList<Card>());
					
					com.alibaba.fastjson.JSONArray cardsList=jsonres.getJSONObject("datas").getJSONArray("cards");
					for (int pi = 0; pi < cardsList.size(); pi++)
					{
						//多张卡累加积分
						getPoint=getPoint.add(new BigDecimal(cardsList.getJSONObject(pi).getDouble("getPoint")));
						
						Card card=res.new Card();
						card.setAmount(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount")));
						card.setAmount1(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1")));
						card.setAmount1_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_after")));
						card.setAmount2(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2")));
						card.setAmount2_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_after")));
						card.setAmount_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_after")));
						card.setAmount1_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount1_before")));
						card.setAmount2_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount2_before")));
						card.setAmount_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("amount_before")));
						card.setCardNo(cardsList.getJSONObject(pi).getString("cardNo"));
						card.setGetPoint(getPoint.toPlainString());
						card.setPoint_after(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_after")));
						card.setPoint_before(new DecimalFormat("#.##").format(cardsList.getJSONObject(pi).getDouble("point_before")));
						
						res.getDatas().getVipDatas().getCardsInfo().add(card);
						
						//
						res.getDatas().getVipDatas().setAmount(card.getAmount_after());
						res.getDatas().getVipDatas().setGetPoints(card.getGetPoint());
						res.getDatas().getVipDatas().setMemberId(dcpOrder.getMemberId());
						res.getDatas().getVipDatas().setPoints(card.getPoint_after());
						
					}
					return true;
				}
				else
				{
					error.append("调用会员积分查询接口MemberPay失败:" + serviceDescription);
					HelpTools.writelog_waimai("【订单创建】调用会员积分查询接口MemberPay失败："+serviceDescription +" ，单号orderNo="+orderNo);
					return false;
				}
			}
		}
		else
		{
			error.append("调用会员积分接口MemberPay失败:返回为空！");
			HelpTools.writelog_waimai("【订单创建】调用会员积分接口MemberPay失败：返回为空 ，单号orderNo="+orderNo);
			return false;
		}
		
	}
	
	/**
	 * 调用会员支付撤销
	 * @param req
	 * @param res
	 * @param error
	 * @return
	 * @throws Exception
	 */
	public  boolean callMemerPayReverse(DCP_OrderCreateReq req,DCP_OrderCreateRes res,StringBuffer error) throws Exception
	{
		//error = new StringBuffer("");
		order dcpOrder = req.getRequest();
		
		if(dcpOrder == null)
		{
			error.append("order对象为空！");
			return true;
		}
		String loadDocType = dcpOrder.getLoadDocType();
		/*if(loadDocType.equals(orderLoadDocType.POS)==false)
		{
			error.append("渠道类型loadDocType="+loadDocType+"无需调用！");
			return true;
		}*/
		
		String eId = dcpOrder.geteId();
		String orderNo = dcpOrder.getOrderNo();
		HelpTools.writelog_waimai("【订单创建】【撤销】会员付款方式开始 ，单号orderNo="+orderNo);
		String memberPayNo = dcpOrder.getMemberPayNo();
		if(memberPayNo==null||memberPayNo.trim().isEmpty())
		{
			HelpTools.writelog_waimai("【订单创建】【撤销】会员付款方式,支付单号memberPayNo为空，无需调用，单号orderNo="+orderNo);
			return true;
		}
		
		
		String Yc_Url="";
		String Yc_Key=req.getApiUserCode();
		String Yc_Sign_Key=req.getApiUser().getUserKey();
		
		Yc_Url=PosPub.getCRM_INNER_URL(eId);
		
		if(Yc_Url.trim().isEmpty() ||  Yc_Key==null|| Yc_Key.trim().isEmpty() || Yc_Sign_Key==null|| Yc_Sign_Key.trim().isEmpty())
		{
			//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "CrmUrl、apiUserCode、userKey移动支付接口参数未设置!");
			HelpTools.writelog_waimai("【订单创建】【撤销】会员付款方式,CrmUrl、apiUserCode、userKey移动支付接口参数未设置，单号orderNo="+orderNo);
			error.append("CrmUrl、apiUserCode、userKey移动支付接口参数未设置");
			return false;
		}
		
		
		try
		{
			com.alibaba.fastjson.JSONObject payReq=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
			com.alibaba.fastjson.JSONObject reqheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
			com.alibaba.fastjson.JSONObject signheader=new com.alibaba.fastjson.JSONObject(new TreeMap<String, Object>());
			
			reqheader.put("orderNo", memberPayNo);//需唯一
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
	
	
	/**
	 * 调用会员找零券
	 * @param Yc_Key
	 * @param Yc_Sign_Key
	 * @param eId
	 * @param thirdTransNo
	 * @param couponChangeList
	 * @return
	 * @throws Exception
	 */
	public void callMemberPayCouponChange(String Yc_Key,String Yc_Sign_Key,String eId,String thirdTransNo,List<order.CouponChange> couponChangeList) throws Exception {
		String Yc_Url=PosPub.getCRM_INNER_URL(eId);
		if(Yc_Url.trim().isEmpty() ||  Yc_Key==null|| Yc_Key.trim().isEmpty() || Yc_Sign_Key==null|| Yc_Sign_Key.trim().isEmpty()) {
			HelpTools.writelog_waimai("会员找零券MemberPayCouponChange,CrmUrl、apiUserCode、userKey移动支付接口参数未设置，会员单号thirdTransNo="+thirdTransNo);
			return;
		}
		
		HelpTools.writelog_waimai("会员找零券MemberPayCouponChange开始,会员单号thirdTransNo="+thirdTransNo);
		
		
		try {
			com.alibaba.fastjson.JSONObject payCouponChangeReq=new com.alibaba.fastjson.JSONObject(new TreeMap<>());
			com.alibaba.fastjson.JSONObject reqheader=new com.alibaba.fastjson.JSONObject(new TreeMap<>());
			com.alibaba.fastjson.JSONObject signheader=new com.alibaba.fastjson.JSONObject(new TreeMap<>());
			
			reqheader.put("orderNo", thirdTransNo);  //此处必须给CRM的单号，通过单号查询 select thirdtransno from crm_consume
			reqheader.put("trade_no", "");          //这个其实给不了，不知道从哪里去获取
			reqheader.put("changeAmount",0);        //这个给值无意义，都不知道是干嘛用的
			reqheader.put("coupons", couponChangeList);
			
			String req_sign= reqheader + Yc_Sign_Key;
			req_sign=DigestUtils.md5Hex(req_sign);
			
			signheader.put("key", Yc_Key);
			signheader.put("sign", req_sign);
			
			payCouponChangeReq.put("serviceId", "MemberPayCouponChange");
			
			payCouponChangeReq.put("request", reqheader);
			payCouponChangeReq.put("sign", signheader);
			
			String str = payCouponChangeReq.toString();
			
			HelpTools.writelog_waimai("会员找零券MemberPayCouponChange请求内容: "+str +"，会员单号thirdTransNo="+thirdTransNo);
			
			//编码处理
			str=URLEncoder.encode(str,"UTF-8");
			String resbody=HttpSend.Sendcom(str, Yc_Url);
			HelpTools.writelog_waimai("会员找零券MemberPayCouponChange返回："+resbody +" ,会员单号thirdTransNo="+thirdTransNo);
			
		} catch (Exception ignored) {
		
		}
	}


    /**
     * 追加订单，数据校验
     * @param dcpOrder
     * @param errMsg
     * @return
     * @throws Exception
     */
	private boolean isFailOrderRequest(order dcpOrder,StringBuffer errMsg) throws Exception
    {
        boolean nRet = false;
        try
        {
            if (dcpOrder.getShippingShopNo()==null||dcpOrder.getShippingShopNo().trim().isEmpty())
            {
                errMsg.append("配送机构shippingShopNo不可为空值, ");
                nRet = true;
            }
            if (dcpOrder.getPayStatus()==null||dcpOrder.getPayStatus().trim().isEmpty())
            {
                errMsg.append("支付状态payStatus不可为空值, ");
                nRet = true;
            }
            else
            {
                if (!"3".equals(dcpOrder.getPayStatus()))
                {
                    errMsg.append("支付状态payStatus必须付清状态, ");
                    nRet = true;
                }
            }
            if (Math.abs(dcpOrder.getTot_oldAmt()-dcpOrder.getTotDisc()-dcpOrder.getEraseAmt()-dcpOrder.getTot_Amt())>0.001)
            {
                errMsg.append("订单原价tot_oldAmt-总折扣totDisc-抹零金额eraseAmt与订单总价tot_Amt不一致，");
                nRet = true;
            }
            if (Math.abs(dcpOrder.getTot_Amt()-dcpOrder.getPayAmt())>0.001)
            {
                errMsg.append("订单总价tot_Amt与已付金额payAmt不一致, ");
                nRet = true;
            }

            if (dcpOrder.getGoodsList()==null||dcpOrder.getGoodsList().isEmpty())
            {
                errMsg.append("商品列表goodsList不能为空, ");
                nRet = true;
            }
            else
            {
                for (orderGoodsItem goodsItem : dcpOrder.getGoodsList())
                {
                    if (goodsItem.getItem()==null||goodsItem.getItem().isEmpty())
                    {
                        errMsg.append("商品项次item不能为空, ");
                        nRet = true;
                    }
                    if (goodsItem.getPluNo()==null||goodsItem.getPluNo().isEmpty())
                    {
                        errMsg.append("商品编码pluNo不能为空, ");
                        nRet = true;
                    }
                    if (goodsItem.getPluBarcode()==null||goodsItem.getPluBarcode().isEmpty())
                    {
                        errMsg.append("商品条码pluBarcode不能为空, ");
                        nRet = true;
                    }
                    if (goodsItem.getsUnit()==null||goodsItem.getsUnit().isEmpty())
                    {
                        errMsg.append("商品单位编码sUnit不能为空, ");
                        nRet = true;
                    }
                    if (goodsItem.getQty()<=0)
                    {
                        errMsg.append("商品数量qty不能小于等于0, ");
                        nRet = true;
                    }
                }
            }

            if (dcpOrder.getPay()==null||dcpOrder.getPay().isEmpty())
            {
                errMsg.append("订单付款列表pay不能为空, ");
                nRet = true;
            }
            else
            {
                for (orderPay payItem : dcpOrder.getPay())
                {
                    if (payItem.getItem()==null||payItem.getItem().isEmpty())
                    {
                        errMsg.append("付款项次item不能为空, ");
                        nRet = true;
                    }
                    if (payItem.getPayType()==null||payItem.getPayType().isEmpty())
                    {
                        errMsg.append("付款方式payType不能为空, ");
                        nRet = true;
                    }
                    if (payItem.getPayCode()==null||payItem.getPayCode().isEmpty())
                    {
                        errMsg.append("付款编码payCode不能为空, ");
                        nRet = true;
                    }
                    if (payItem.getPayCodeErp()==null||payItem.getPayCodeErp().isEmpty())
                    {
                        errMsg.append("ERP付款编码payCodeErp不能为空, ");
                        nRet = true;
                    }
                    if (payItem.getPay()==null||payItem.getPay().isEmpty())
                    {
                        errMsg.append("付款金额pay不能为空, ");
                        nRet = true;
                    }

                }
            }

            return nRet;
        }
        catch (Exception e)
        {

        }

        return nRet;

    }
	
}
