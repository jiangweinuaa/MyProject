package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ScanOrderBaseSetCreateReq;
import com.dsc.spos.json.cust.req.DCP_ScanOrderBaseSetCreateReq.RangeList;
import com.dsc.spos.json.cust.res.DCP_ScanOrderBaseSetCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_ScanOrderBaseSetCreate extends SPosAdvanceService<DCP_ScanOrderBaseSetCreateReq,DCP_ScanOrderBaseSetCreateRes>
{
	
	@Override
	protected void processDUID(DCP_ScanOrderBaseSetCreateReq req, DCP_ScanOrderBaseSetCreateRes res) throws Exception
	{
		String eId = req.geteId();
		
		SimpleDateFormat sdfs=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal=Calendar.getInstance();
		String sNOW=sdfs.format(cal.getTime());
		
		//
		sdfs=new SimpleDateFormat("yyyyMMddHHmmss");
		String ruleno="SMDC" +sdfs.format(cal.getTime());

//		String invoice=null;
//		String area=null;
//
//		String reservedInvoiceinfo="";
//		String orderInvoice="";
//		String memberCarrier="";
//		DCP_ScanOrderBaseSetCreateReq.Invoice invoiceData=req.getRequest().getInvoice();
//		if(invoiceData!=null){
//			reservedInvoiceinfo=invoiceData.getReservedInvoiceinfo();
//			orderInvoice=invoiceData.getOrderInvoice();
//			memberCarrier=invoiceData.getMemberCarrier();
//			area=invoiceData.getArea();
//			invoice=invoiceData.getIsinvoice();
//		}
		
		String restrictAdvanceOrder = "1"; //允许提前选菜0.允许 1.不允许
		String retainTime = "";//保留时长，单位小时
		if(!Check.Null(req.getRequest().getRestrictAdvanceOrder())){
			restrictAdvanceOrder = req.getRequest().getRestrictAdvanceOrder();
		}
		
		if(!Check.Null(req.getRequest().getRetainTime())){
			retainTime = req.getRequest().getRetainTime();
		}
		
		//  新增开关takeAway外带打包，coupon优惠券、integral积分 BY WANGZYC 20201123
		// 打包带走 0.禁用 1.启用
		String takeAway = "1";
		if(!Check.Null(req.getRequest().getTakeAway())){
			takeAway = req.getRequest().getTakeAway();
		}
		// 优惠券 0.禁用 1.启用
		String coupon = "1";
		if(!Check.Null(req.getRequest().getCoupon())){
			coupon = req.getRequest().getCoupon();
		}
		
		// 积分 0.禁用 1.启用
		String integral = "1";
		if(!Check.Null(req.getRequest().getIntegral())){
			integral = req.getRequest().getIntegral();
		}
		
		// 新增firstRegister点餐前会员注册 BY WANGZYC 20201130
		String firstRegister = "1";
		if(!Check.Null(req.getRequest().getFirstRegister())){
			firstRegister = req.getRequest().getFirstRegister();
		}
		
		// 新增orderMemo订单备注开关 BY WANGZYC 20201214 
		String orderMemo = "1";
		if(!Check.Null(req.getRequest().getOrderMemo())){
			orderMemo = req.getRequest().getOrderMemo();
		}
		
		String description = req.getRequest().getDescription();
		
		String recommendedDishes = "1";
		if(!Check.Null( req.getRequest().getRecommendedDishes() )){
			recommendedDishes =  req.getRequest().getRecommendedDishes();
		}
		
		/**
		 *  recommendedDishes;//是否开启推荐菜品提示的开关
		 *  description; //对应菜品提示编辑文案
		 */
		String scanType="0";
		if(!Check.Null(req.getRequest().getScanType())){
			scanType = req.getRequest().getScanType();
		}
		
		String status = "100";
		if(!Check.Null(req.getRequest().getStatus())){
			status = req.getRequest().getStatus();
		}

//		String restrictLike = "0";
//		if(!Check.Null(req.getRequest().getRestrictLike())){
//			restrictLike = req.getRequest().getRestrictLike();
//		}
		
		//结账类型0可点餐，可结账      1可点餐，不可结账
		String checkType = "0";
		if(!Check.Null(req.getRequest().getCheckType())){
			checkType = req.getRequest().getCheckType();
		}
		
		//是否启用桌台 0.否 1.是
		String restrictTable = "1";
		if(!Check.Null(req.getRequest().getRestrictTable())){
			restrictTable = req.getRequest().getRestrictTable();
		}
		
		//适用门店：0-所有门店 1-指定门店 2-排除门店
		String restrictShop = "0";
		if(!Check.Null(req.getRequest().getRestrictShop())){
			restrictShop = req.getRequest().getRestrictShop();
		}
		
		//适用渠道：0-所有渠道 1-指定渠道 2-排除渠道
		String restrictChannel = "0";
		if(!Check.Null(req.getRequest().getRestrictChannel())){
			restrictChannel = req.getRequest().getRestrictChannel();
		}
		
		String restrictRegister = "1";
		if(!Check.Null(req.getRequest().getRestrictRegister())){
			restrictRegister = req.getRequest().getRestrictRegister();
		}
		
		String counterPay = "0";
		if(!Check.Null(req.getRequest().getCounterPay())){
			counterPay = req.getRequest().getCounterPay();
		}
		
		String recharge = "1";
		if(!Check.Null(req.getRequest().getRecharge())){
			recharge = req.getRequest().getRecharge();
		}
		
		//支付成功页评价 0.禁用 1.启用，默认禁用，空为禁用  by jinzma 20230109
		String evaluation = req.getRequest().getEvaluation();
		if (Check.Null(evaluation)){
			evaluation="0";
		}
		
		String isPayCard = req.getRequest().getIsPayCard();
		if (Check.Null(isPayCard)){
			isPayCard="0";
		}
		
		//【ID1035591】扫码点餐及外卖点单加详情配置项-服务 by jinzma 20230828
		String isGoodsDetailDisplay = req.getRequest().getIsGoodsDetailDisplay();  //是否显示商品详情页0.否 1.是
		if (Check.Null(isGoodsDetailDisplay)){
			isGoodsDetailDisplay="0";
		}
		
		String[] columns_SB ={"EID","RULENO","RULENAME",
//				"PACKNO","PACKNAME",
//				"RESTRICTCHECK","RESTRICTORDER",
				"TABLESET","QRCODE","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","STATUS",
//				"ISINVOICE","AREA","RESERVEDINVOICEINFO","ORDERINVOICE","MEMBERCARRIER",
				"RESTRICTADVANCEORDER","RETAINTIME" ,"RECOMMENDEDDISHES" ,
				"DESCRIPTION","SCANTYPE","CHECKTYPE","RESTRICTTABLE",
				"RESTRICTSHOP","RESTRICTCHANNEL","REGISTER","COUNTERPAY","TAKEAWAY","CONPON","INTEGRAL","FIRSTREGISTER",
				"ORDERMEMO","ISAUTOREGISTER","ISAUTOPROM","ISAUTOFOLD","BEFORORDER","CHOOSABLETIME","ISEVALUATEREMIND",
				"REMINDTIME","SEARCH","RECHARGE","EVALUATION","ISPAYCARD","ISGOODSDETAILDISPLAY"
		};
		DataValue[] insValue_SB = new DataValue[]
				{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(ruleno, Types.VARCHAR),
						new DataValue(req.getRequest().getRuleName(), Types.VARCHAR),
//						new DataValue(req.getRequest().getPackNo(), Types.VARCHAR),
//						new DataValue(req.getRequest().getPackName(), Types.VARCHAR),
						new DataValue(req.getRequest().getTableSet(), Types.VARCHAR), //开台设置 askAdult:询问成人人数 askAdultChild:询问成人与儿童人数 notAsk:不询问人数
						new DataValue(req.getRequest().getQrCode(), Types.VARCHAR),
						new DataValue(req.getOpNO(), Types.VARCHAR),
						new DataValue(req.getOpName(), Types.VARCHAR),
						new DataValue(sNOW, Types.DATE),
						new DataValue(status, Types.VARCHAR), //状态：-1未启用、100已启用、0已禁用
//						new DataValue(invoice, Types.VARCHAR),
//						new DataValue(area, Types.VARCHAR),
//						new DataValue(reservedInvoiceinfo, Types.INTEGER),
//						new DataValue(orderInvoice, Types.INTEGER),
//						new DataValue(memberCarrier, Types.INTEGER),
//						new DataValue(restrictLike, Types.VARCHAR),//RESTRICTLIKE	允许点赞 0.允许 1.不允许
						new DataValue(restrictAdvanceOrder, Types.VARCHAR),
						new DataValue(retainTime, Types.VARCHAR),
						new DataValue(recommendedDishes, Types.VARCHAR),
						new DataValue(description, Types.VARCHAR),
						
						new DataValue(scanType, Types.VARCHAR),
						new DataValue(checkType, Types.VARCHAR),
						new DataValue(restrictTable, Types.VARCHAR), //是否启用桌台 0.否 1.是 ,   默认 1
						
						new DataValue(restrictShop, Types.VARCHAR),//适用门店：0-所有门店 1-指定门店 2-排除门店 , 默认 0 
						new DataValue(restrictChannel, Types.VARCHAR),
						new DataValue(restrictRegister, Types.VARCHAR),
						new DataValue(counterPay, Types.VARCHAR),
						
						new DataValue(takeAway, Types.VARCHAR), // 打包带走 0.禁用 1.启用
						new DataValue(coupon, Types.VARCHAR),// 优惠券 0.禁用 1.启用
						new DataValue(integral, Types.VARCHAR),// 积分 0.禁用 1.启用
						
						new DataValue(firstRegister, Types.VARCHAR),// 点餐前会员注册 0.禁用 1.启用
						
						new DataValue(orderMemo, Types.VARCHAR),// 订单备注 0.禁用 1.启用
						
						// Add 2021-06-07 王欢 新增isAutoProm、isAutoRegister字段
						new DataValue(req.getRequest().getIsAutoRegister(), Types.VARCHAR),// 是否自动注册会员 0.禁用 1.启用
						new DataValue(req.getRequest().getIsAutoProm(), Types.VARCHAR), // 点单页促销提示 0.禁用 1.启用
						
						new DataValue(req.getRequest().getIsAutoFold(), Types.VARCHAR), // 售罄商品折叠 0.禁用 1.启用
						new DataValue(req.getRequest().getBeforOrder(), Types.VARCHAR), // 预约点单 0.禁用 1.启用
						new DataValue(req.getRequest().getChoosableTime(), Types.VARCHAR), // 可选时间范围：近X天
						new DataValue(req.getRequest().getIsEvaluateRemind(), Types.VARCHAR), // 是否启用用餐评价提醒，0.禁用 1.启用
						new DataValue(req.getRequest().getRemindTime(), Types.VARCHAR), // 下单X分钟后提醒
						new DataValue(req.getRequest().getSearch(), Types.VARCHAR), //
						new DataValue(recharge, Types.VARCHAR), //
						new DataValue(evaluation, Types.VARCHAR),
						new DataValue(isPayCard, Types.INTEGER),
						new DataValue(isGoodsDetailDisplay, Types.VARCHAR)  //是否显示商品详情页0.否 1.是
				};
		
		InsBean ib_SB = new InsBean("DCP_SCANORDER_BASESET", columns_SB);
		ib_SB.addValues(insValue_SB);
		this.addProcessData(new DataProcessBean(ib_SB));
		
		// 增加支付设置 DCP_SCANORDER_BASESET_PAYTYPE
		List<DCP_ScanOrderBaseSetCreateReq.level2Elm> paySet = req.getRequest().getPaySet();
		if(!CollectionUtils.isEmpty(paySet)){
			String[] columns_PAYTYPE ={"EID","RULENO","SORTID","PAYTYPE","PAYNAME"};
			
			for (DCP_ScanOrderBaseSetCreateReq.level2Elm level2Elm : paySet) {
				DataValue[] insValue_PAYTYPE = new DataValue[]
						{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(ruleno, Types.VARCHAR),
								new DataValue(level2Elm.getSortId(), Types.VARCHAR),
								new DataValue(level2Elm.getPayType(), Types.VARCHAR),
								new DataValue(level2Elm.getPayName(), Types.VARCHAR)
						};
				
				InsBean ib_PAYTYPE = new InsBean("DCP_SCANORDER_BASESET_PAYTYPE", columns_PAYTYPE);
				ib_PAYTYPE.addValues(insValue_PAYTYPE);
				this.addProcessData(new DataProcessBean(ib_PAYTYPE));
			}
		}
		
		//指定门店 或 指定渠道
		if (restrictShop.equals("1") || restrictShop.equals("2")|| restrictChannel.equals("1") || restrictChannel.equals("2") )
		{
			String[] columns_SBS ={"EID","RULENO","RANGETYPE","ID","NAME","LASTMODITIME"};
			
			List<RangeList> rangeList = req.getRequest().getRangeList();
			
			List<String> shopList=new ArrayList<String>();
			List<String> channelList=new ArrayList<String>();
			
			for (RangeList rangeMap : rangeList) {
				
				String rangType = rangeMap.getRangeType(); // 适用范围：1-公司 2-门店 3-渠道 4-应用 5-时段
				if(!Check.Null(rangType) && rangType.equals("2")&&restrictShop.equals("1")){
					shopList.add("'"+rangeMap.getId()+"'");
				}
				if(!Check.Null(rangType) && rangType.equals("3")&&restrictChannel.equals("1")){
					channelList.add("'"+rangeMap.getId()+"'");
				}
				
			}
			
			String shopStr = StringUtils.join(shopList.toArray(), ",");
			String channelStr = StringUtils.join(channelList.toArray(), ",");
			
			List<Map<String, Object>> getData = new ArrayList<>();
			
			if(!Check.Null(shopStr)){
				StringBuffer sqlbuf = new StringBuffer("");
				sqlbuf.append("SELECT a.RULENO, a.RESTRICTCHANNEL, b.id AS shopid, c.id AS channelId " +
						" FROM DCP_SCANORDER_BASESET a " +
						" LEFT JOIN Dcp_Scanorder_Baseset_Range b ON a.EID = b.EID AND a.RULENO = b.RULENO AND b.RANGETYPE = '2' " +
						" LEFT JOIN Dcp_Scanorder_Baseset_Range c ON a.eid = c.eid AND a.RULENO = c.RULENO AND c.RANGETYPE = '3' " +
						" WHERE a.eid = '"+eId+"' AND a.RESTRICTCHANNEL = '"+restrictChannel+"' AND b.ID IN ("+shopStr+") and a.RESTRICTSHOP = '"+restrictShop+"'");
				if("1".equals(restrictChannel)||"2".equals(restrictChannel)){
					sqlbuf.append(" and c.id in("+channelStr+")");
				}
				getData = this.doQueryData(sqlbuf.toString(), null);
			}
//			if(!Check.Null(channelStr)){
//				String channelSql = "SELECT * FROM Dcp_Scanorder_Baseset_Range A where A.EID='"+eId+"' and A.rangeType = '3' and A.ID in ( "+channelStr+" ) " ;
//				List<Map<String, Object>> channelDatas = this.doQueryData(channelSql, null);
//				getData.addAll(channelDatas);
//			}
			
			
			if (getData!=null && getData.size()>0)
			{
				String sExistShopno="";
//				String sExitChannel = "";
				
				String errorMsg = "";
				StringBuffer strbuf = new StringBuffer("");
				String errorChannel = "";
				if(restrictChannel.equals("0")){
					errorChannel = "适用全部渠道";
				}else if(restrictChannel.equals("1")){
					errorChannel = "适用指定渠道";
				}
				
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("RULENO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader = MapDistinct.getMap(getData, condition);
				for (Map<String, Object> map2 : getQHeader)
				{
					String ruleNo = map2.get("RULENO").toString();
//					if(!Check.Null(exitRangeType) && exitRangeType.equals("2")){
//						sExistShopno += map2.get("ID").toString()+",";
//					}
					strbuf.append("规则:" + ruleNo + ""+errorChannel+"中已存在门店:(");
					for (Map<String, Object> shop : getData) {
						if (ruleNo.equals(shop.get("RULENO").toString())) {
							strbuf.append(shop.get("SHOPID").toString());
							strbuf.append("、");
						}
					}
					strbuf.deleteCharAt(strbuf.length() - 1);
					strbuf.append(");</br>");
//					if(!Check.Null(exitRangeType) && exitRangeType.equals("3")){
//						sExitChannel += map2.get("ID").toString()+",";
//					}
//					sExistShopno += map2.get("ID").toString()+",";
				}
				
				strbuf.delete(strbuf.length() - 5, strbuf.length() - 1);
				strbuf.deleteCharAt(strbuf.length() - 1);

//				if( !Check.Null(sExistShopno) ){
//					errorMsg = errorMsg + "门店"+sExistShopno+"已存在规则！";
//				}
//				if( !Check.Null(sExitChannel) ){
//					errorMsg = errorMsg + "渠道"+sExitChannel+"已存在规则！";
//				}
				
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("服务执行失败："+ strbuf.toString());
				
				getData.clear();
				getData=null;
				
				this.pData.clear();//清理
				return;
			}
			getData.clear();
			getData=null;
			
			
			for (RangeList rangeMap : rangeList) {
				String rangeType = rangeMap.getRangeType(); // 适用范围：1-公司 2-门店 3-渠道 4-应用 5-时段
				String name = rangeMap.getName();
				String Id = rangeMap.getId();
				
				DataValue[] insValue_SBS = new DataValue[]
						{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(ruleno, Types.VARCHAR),
								new DataValue(rangeType, Types.VARCHAR),
								new DataValue(Id, Types.VARCHAR),
								new DataValue(name, Types.VARCHAR),
								new DataValue(sNOW, Types.DATE)
						};
				
				InsBean ib_SBS = new InsBean("DCP_SCANORDER_BASESET_RANGE", columns_SBS);
				ib_SBS.addValues(insValue_SBS);
				this.addProcessData(new DataProcessBean(ib_SBS));
			}
			
			
		}
//		else 
//		{
		// 全部门店和全部渠道要分开查
		if (restrictShop.equals("0")){
			String sql="select eid from dcp_scanorder_baseset t where t.eid='"+eId+"' and t.status='100' and t.restrictShop= '0' ";
			List<Map<String, Object>> getData=this.doQueryData(sql, null);
			if (getData!=null && getData.size()>0)
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("服务执行失败：所有门店使用规则只能有1个生效！");
				getData.clear();
				getData=null;
				
				this.pData.clear();//清理
				return;
			}
			getData.clear();
			getData=null;
		}


//			if (restrictChannel.equals("0")){
//				String sql="select eid from dcp_scanorder_baseset t where t.eid='"+eId+"' and t.status='100' and t.restrictChannel= '0' ";
//				List<Map<String, Object>> getData=this.doQueryData(sql, null);
//				if (getData!=null && getData.size()>0)
//				{
//					res.setSuccess(false);
//					res.setServiceStatus("100");
//					res.setServiceDescription("服务执行失败：所有渠道使用规则只能有1个生效！");
//					getData.clear();
//					getData=null;
//
//					this.pData.clear();//清理
//					return;
//				}
//				getData.clear();
//				getData=null;
//			}


//		}
		
		this.doExecuteDataToDB();
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_ScanOrderBaseSetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_ScanOrderBaseSetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_ScanOrderBaseSetCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_ScanOrderBaseSetCreateReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if(!Check.Null(req.getRequest().getRestrictAdvanceOrder())){
			String restrictAdvanceOrder = req.getRequest().getRestrictAdvanceOrder();
			if(!"0".equals(restrictAdvanceOrder)&&!"1".equals(restrictAdvanceOrder)){
				errMsg.append("允许提前点菜应为[0,1], ");
				isFail = true;
			}
		}
		else{
			errMsg.append("允许提前点菜不可为空值, ");
			isFail = true;
		}

//		if (Check.Null(req.getRequest().getRuleNo())) 
//		{
//			errMsg.append("规则编号不可为空值, ");
//			isFail = true;
//		}
		
		if (Check.Null(req.getRequest().getRuleName()))
		{
			errMsg.append("规则名称不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(req.getRequest().getRestrictShop()))
		{
			errMsg.append("适用门店类型不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(req.getRequest().getRestrictChannel()))
		{
			errMsg.append("适用渠道类型不可为空值, ");
			isFail = true;
		}
		
		
		if (req.getRequest().getRestrictShop().equals("1") || req.getRequest().getRestrictShop().equals("2")
				|| req.getRequest().getRestrictChannel().equals("1") || req.getRequest().getRestrictChannel().equals("2"))
		{
			if (req.getRequest().getRangeList()==null || req.getRequest().getRangeList().size()==0)
			{
				errMsg.append("指定范围列表不可为空值, ");
				isFail = true;
			}
		}
		
		if (Check.Null(req.getRequest().getTableSet()))
		{
			errMsg.append("开台设置不可为空值, ");
			isFail = true;
		}
		
		if (req.getRequest().getTableSet().equals("askAdult")==false && req.getRequest().getTableSet().equals("askAdultChild")==false&& req.getRequest().getTableSet().equals("notAsk")==false)
		{
			errMsg.append("开台设置值不正确, ");
			isFail = true;
		}
		
		if (Check.Null(req.getRequest().getQrCode()))
		{
			errMsg.append("二维码不可为空值, ");
			isFail = true;
		}

//		Invoice invoice = req.getRequest().getInvoice();
//		if(invoice!=null){
//			//RESERVEDINVOICEINFO	预留发票信息 0否 1是
//			String reservedInvoiceinfo=invoice.getReservedInvoiceinfo();
//			if (!Check.Null(reservedInvoiceinfo)){
//				if(!"0".equals(reservedInvoiceinfo)&&!"1".equals(reservedInvoiceinfo)){
//					errMsg.append("预留发票信息应为[0,1], ");
//					isFail = true;
//				}
//			}
//			//ORDERINVOICE	下单开票，0-否，1-是
//			String orderInvoice=invoice.getOrderInvoice();
//			if (!Check.Null(orderInvoice)){
//				if(!"0".equals(orderInvoice)&&!"1".equals(orderInvoice)){
//					errMsg.append("下单开票应为[0,1], ");
//					isFail = true;
//				}
//			}
//			////MEMBERCARRIER	会员载具，0-否 1-是
//			String memberCarrier=invoice.getMemberCarrier();
//			if (!Check.Null(memberCarrier)){
//				if(!"0".equals(memberCarrier)&&!"1".equals(memberCarrier)){
//					errMsg.append("会员载具应为[0,1], ");
//					isFail = true;
//				}
//			}
//		}
//		//RESTRICTLIKE	允许点赞 0.允许 1.不允许
//		String restrictLike=req.getRequest().getRestrictLike();
//		if (!Check.Null(restrictLike)){
//			if(!"0".equals(restrictLike)&&!"1".equals(restrictLike)){
//				errMsg.append("允许点赞应为[0,1], ");
//				isFail = true;
//			}
//		}
		
		//isAutoProm	点单页促销实时提示 0.禁用 1.启用
		String isAutoProm=req.getRequest().getIsAutoProm();
		if (!Check.Null(isAutoProm)){
			if(!"0".equals(isAutoProm)&&!"1".equals(isAutoProm)){
				errMsg.append("点单页促销实时提示应为[0,1], ");
				isFail = true;
			}
		}else{
			errMsg.append("点单页促销实时提示不可为空值, ");
			isFail = true;
		}
		
		//isAutoFold	售罄商品折叠 0.禁用 1.启用
		String isAutoFold=req.getRequest().getIsAutoFold();
		if (!Check.Null(isAutoFold)){
			if(!"0".equals(isAutoFold)&&!"1".equals(isAutoFold)){
				errMsg.append("售罄商品折叠应为[0,1], ");
				isFail = true;
			}
		}else{
			errMsg.append("售罄商品折叠不可为空值, ");
			isFail = true;
		}
		
		//beforOrder	预约点单 0.禁用 1.启用
		String beforOrder=req.getRequest().getBeforOrder();
		if (!Check.Null(beforOrder)){
			if(!"0".equals(beforOrder)&&!"1".equals(beforOrder)){
				errMsg.append("预约点单应为[0,1], ");
				isFail = true;
			}
		}else{
			errMsg.append("预约点单不可为空值, ");
			isFail = true;
		}
		
		//isEvaluateRemind	是否启用用餐评价提醒，0.禁用 1.启用
		String isEvaluateRemind=req.getRequest().getIsEvaluateRemind();
		if (!Check.Null(isEvaluateRemind)){
			if(!"0".equals(isEvaluateRemind)&&!"1".equals(isEvaluateRemind)){
				errMsg.append("是否启用用餐评价提醒应为[0,1], ");
				isFail = true;
			}
		}else{
			errMsg.append("是否启用用餐评价提醒不可为空值, ");
			isFail = true;
		}

//        //choosableTime	可选时间范围：近X天
//        String choosableTime=req.getRequest().getChoosableTime();
//        if (Check.Null(choosableTime)){
//                errMsg.append("可选时间范围不可为空值, ");
//                isFail = true;
//        }
		
		//isAutoRegister	是否自动注册 0.禁用 1.启用
		String isAutoRegister =req.getRequest().getIsAutoRegister();
		if (!Check.Null(isAutoRegister)){
			if(!"0".equals(isAutoRegister)&&!"1".equals(isAutoRegister)){
				errMsg.append("是否自动注册应为[0,1], ");
				isFail = true;
			}
		}else{
			errMsg.append("是否自动注册不可为空值, ");
			isFail = true;
		}
		
		String search =req.getRequest().getSearch();
		if (!Check.Null(search)){
			if(!"0".equals(search)&&!"1".equals(search)){
				errMsg.append("点单页搜索应为[0,1], ");
				isFail = true;
			}
		}else{
			errMsg.append("点单页搜索不可为空值, ");
			isFail = true;
		}

//        String recharge =req.getRequest().getRecharge();
//        if (!Check.Null(recharge)){
//            if(!"0".equals(recharge)&&!"1".equals(recharge)){
//                errMsg.append("结账提示充值应为[0,1], ");
//                isFail = true;
//            }
//        }else{
//            errMsg.append("结账提示充值不可为空值, ");
//            isFail = true;
//        }
		
		
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
	}
	
	@Override
	protected TypeToken<DCP_ScanOrderBaseSetCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ScanOrderBaseSetCreateReq>(){};
	}
	
	@Override
	protected DCP_ScanOrderBaseSetCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ScanOrderBaseSetCreateRes();
	}
	
	
}
