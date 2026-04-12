package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderChargeAmtUpdate_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderChargeAmtUpdate_OpenRes;

import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderChargeAmtUpdate_Open extends SPosAdvanceService<DCP_OrderChargeAmtUpdate_OpenReq,DCP_OrderChargeAmtUpdate_OpenRes> 
{
	@Override
	protected void processDUID(DCP_OrderChargeAmtUpdate_OpenReq req, DCP_OrderChargeAmtUpdate_OpenRes res) throws Exception {
	// TODO Auto-generated method stub
		String o_shop = req.getRequest().getShopId();
		String o_organizationNO = o_shop;
		String oEId = req.getRequest().geteId();
		String orderNO = req.getRequest().getOrderNo();		
		String loadDocType = req.getRequest().getLoad_docType();
		if(loadDocType==null||loadDocType.equals("4")==false)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "不支持该类型订单！(loadDocType="+loadDocType+")");
		}
		  				
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from (");
		sqlbuf.append("select A.*  from OC_order A ");
		sqlbuf.append(" where A.EID='"+oEId+"'  and A.LOAD_DOCTYPE='"+ loadDocType+"' and A.orderno='"+orderNO+"'");
		sqlbuf.append(")");
		String sql = sqlbuf.toString();
		String orderStatus = "";//订单状态
		String orderShop = "";//数据库里面下单门店
		String isShipcompany = "N";
		String isOrderCharge = "N";
		boolean nResult = false;
		StringBuilder meassgeInfo = new StringBuilder();
		HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销】查询开始：单号OrderNO="+orderNO+" 传入的参数SHOPID="+o_shop+" LOAD_DOCTYPE="+loadDocType+" 查询语句："+sql);
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
		if(getQDataDetail==null||getQDataDetail.isEmpty())
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("该订单不存在");
			HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销】查询完成：该订单不存在！ 单号OrderNO="+orderNO);
			return;
		}
		isOrderCharge = getQDataDetail.get(0).get("ISCHARGEORDER").toString();
		
		if(isOrderCharge.equals("Y")==false)
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("该订单不是订金商品订单！");
			HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销】查询完成：该订单不是订金商品订单！ 单号OrderNO="+orderNO);
			return;
		}
		
		orderStatus = getQDataDetail.get(0).get("STATUS").toString();
		
		orderShop = getQDataDetail.get(0).get("SHOPID").toString();
		
		String orderAmt = getQDataDetail.get(0).get("TOT_AMT").toString();//订单金额
		//String orderPayAmt = getQDataDetail.get(0).get("PAYAMT").toString();//已支付金额
		String chargeGetAmt = getQDataDetail.get(0).get("CHARGEORDERGETAMT").toString();//已取货金额
		double orderAmt_d = 0;
		
		try 
		{
			orderAmt_d = Double.parseDouble(orderAmt);
		} catch (Exception e) {
		}
		
		/*double orderPayAmt_d = 0;
		try 
		{
			orderPayAmt_d = Double.parseDouble(orderPayAmt);
		} catch (Exception e) {
		}*/
		
		double chargeGetAmt_d = 0;
		try 
		{
			chargeGetAmt_d = Double.parseDouble(chargeGetAmt);
		} catch (Exception e) {
		}
		
		
		HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销】查询完成：单号OrderNO="+orderNO+" 数据库中下单门店shop="+orderShop+" 订单状态status="+orderStatus);
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
			res.setServiceDescription("该订单状态是"+ss+"，无法冲销订金订单！");
			HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销】该订单状态是"+ss+"，无法冲销订金订单！单号OrderNO="+orderNO+" 数据库中下单门店shop="+orderShop+" 订单状态status="+orderStatus);			
			return;
		}
		
		String getAmt = req.getRequest().getChargeGetAmt();
		double getAmt_d = 0;		
		try 
		{
			getAmt_d = Double.parseDouble(getAmt);
		} catch (Exception e) {
		}
			
		double getAmt_tot = getAmt_d+chargeGetAmt_d;
		//当前传入的核销金额+已核销的金额>订单金额
		if(getAmt_tot>orderAmt_d)
		{
			res.setSuccess(false);
			res.setServiceDescription("冲销金额超过订单金额(当前冲销金额"+getAmt+" 已冲销金额"+chargeGetAmt+">订单金额"+orderAmt+")！");
			HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销】冲销金额超过订单金额！当前冲销金额"+getAmt+" 已冲销金额"+chargeGetAmt+">订单金额"+orderAmt+" 单号OrderNO="+orderNO+" 数据库中下单门店shop="+orderShop+" 订单状态status="+orderStatus);			
			return;
		}
		
		boolean isComplete = false;
		
		if(getAmt_tot==orderAmt_d)
		{
			isComplete = true;
		}
		
		String logmemo = "";
		
		logmemo +="冲销金额："+chargeGetAmt_d+"-->"+getAmt_tot+"<br>";
		
		if(req.getRequest().getGoods()!=null)
		{
			for (DCP_OrderChargeAmtUpdate_OpenReq.level2Good map : req.getRequest().getGoods()) 
			{
				
				logmemo +="冲销商品明细<br>";
				logmemo +="条码："+map.getPluBarcode()+" 名称："+map.getPluName()+" 数量："+map.getQty()+" 单价："+map.getPrice()+"<br>";
				
			}
			
		}
		
		
		//if(isNeedUpdate)
		{	
			String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			
			UptBean ub1 = null;	
			ub1 = new UptBean("OC_ORDER");
			
			//冲销金额
			ub1.addUpdateValue("CHARGEORDERGETAMT", new DataValue(getAmt_tot, Types.FLOAT));
			ub1.addUpdateValue("UPDATE_TIME", new DataValue(updateDatetime, Types.VARCHAR));
			if(isComplete)
			{
				ub1.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));			
				ub1.addUpdateValue("COMPLETE_DATETIME", new DataValue(updateDatetime, Types.VARCHAR));
			}
     
			//condition
			ub1.addCondition("EID", new DataValue(oEId, Types.VARCHAR));
			ub1.addCondition("orderno", new DataValue(orderNO, Types.VARCHAR));
			ub1.addCondition("load_doctype", new DataValue(loadDocType, Types.VARCHAR));		
			this.addProcessData(new DataProcessBean(ub1));
			
			StringBuilder memoMsg = new StringBuilder();
			StringBuilder errorMsg = new StringBuilder();
			
			this.doExecuteDataToDB();
			HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销】成功，单号OrderNO="+orderNO);
			if(isComplete)
			{
				HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销】已全部冲销，更新订单已完成！单号OrderNO="+orderNO);			
			}
			
			//region 写下日志
			try
			{/*
				DCP_OrderStatusLogCreateReq req_log = new DCP_OrderStatusLogCreateReq();
				req_log.setDatas(new ArrayList<DCP_OrderStatusLogCreateReq.level1Elm>());
			
				DCP_OrderStatusLogCreateReq.level1Elm onelv1 = req_log.new level1Elm();
				onelv1.setCallback_status("0");
				onelv1.setLoadDocType(loadDocType);

				onelv1.setNeed_callback("N");
				onelv1.setNeed_notify("N");
				
				onelv1.setoEId(oEId);

				String opNO = req.getRequest().getOpNo()==null?"":req.getRequest().getOpNo();

				String o_opName = req.getRequest().getOpName()==null?"":req.getRequest().getOpName();

				onelv1.setO_opName(o_opName);
				onelv1.setO_opNO(opNO);
				
				onelv1.setO_organizationNO(orderShop);//下单门店
				onelv1.setoShopId(orderShop);
				onelv1.setOrderNO(orderNO);
				String statusType = "4";//其他状态
				String updateStaus = "1";//订单修改
				
				onelv1.setStatusType(statusType);				 					
				onelv1.setStatus(updateStaus);
				StringBuilder statusTypeNameObj = new StringBuilder();
				String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
				String statusTypeName = statusTypeNameObj.toString();
				onelv1.setStatusTypeName(statusTypeName);
				onelv1.setStatusName(statusName);

				String memo = "";
				memo += statusTypeName+"-->" + statusName+"(订金订单冲销)<br>";
				memo += logmemo;
				memo += memoMsg.toString();
				onelv1.setMemo(memo);
				
				onelv1.setUpdate_time(updateDatetime);
				req_log.getDatas().add(onelv1);
				
				if(isComplete)
				{
					DCP_OrderStatusLogCreateReq.level1Elm onelv2 = req_log.new level1Elm();
					onelv2.setCallback_status("0");
					onelv2.setLoadDocType(loadDocType);

					onelv2.setNeed_callback("N");
					onelv2.setNeed_notify("N");
					
					onelv2.setoEId(oEId);

				

					onelv2.setO_opName(o_opName);
					onelv2.setO_opNO(opNO);
					
					onelv2.setO_organizationNO(orderShop);//下单门店
					onelv2.setoShopId(orderShop);
					onelv2.setOrderNO(orderNO);
				  statusType = "1";//其他状态
					updateStaus = "11";//订单修改
					
					onelv2.setStatusType(statusType);				 					
					onelv2.setStatus(updateStaus);
					statusTypeNameObj = new StringBuilder();
					statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
					statusTypeName = statusTypeNameObj.toString();
					onelv2.setStatusTypeName(statusTypeName);
					onelv2.setStatusName(statusName);

					memo = "";
					memo += statusTypeName+"-->" + statusName+"(订金订单冲销)<br>";
					memo += logmemo;
					memo += memoMsg.toString();
					onelv2.setMemo(memo);
					updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					onelv2.setUpdate_time(updateDatetime);
					req_log.getDatas().add(onelv2);
				}
				
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
					HelpTools.writelog_waimai("【写表OC_orderStatuslog保存成功】"+" 订单号orderNO:"+orderNO);
				}
				else
				{			  		 
					HelpTools.writelog_waimai("【写表OC_orderStatuslog异常】"+errorMessage.toString()+" 订单号orderNO:"+orderNO);
				}
				this.pData.clear();
				

			*/}
			catch (Exception  e)
			{
				HelpTools.writelog_waimai("【写表OC_orderStatuslog异常】 异常报错 "+e.toString()+" 订单号orderNO:"+orderNO);
			}			
			//endregion
			
		
			
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderChargeAmtUpdate_OpenReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderChargeAmtUpdate_OpenReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderChargeAmtUpdate_OpenReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderChargeAmtUpdate_OpenReq req) throws Exception {
	// TODO Auto-generated method stub
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
    
    if(Check.Null(req.getRequestId()))
    {
    	errCt++;
	 	  errMsg.append("请求节点requestId不能为空, ");
		  isFail = true;
	  }
    
    if(Check.Null(req.getRequestId())==false)    		
    {
    	if(req.getRequestId().length()>64)
    	{
    		errCt++;
  	 	  errMsg.append("请求节点requestId长度不能超过64, ");
  		  isFail = true;    		
    	}
    	
    }
    
    if(Check.Null(req.getRequest().getChargeGetAmt()))    		
    {
    	
  		errCt++;
	 	  errMsg.append("请求节点取货金额chargeGetAmt不能为空, ");
		  isFail = true;    		  	
    }
    
    if (isFail)
    {
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }    
    
   
    if (isFail)
    {
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }  
    
    
    if (isFail)
    {
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    } 
    
    return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderChargeAmtUpdate_OpenReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderChargeAmtUpdate_OpenReq>(){};
	}

	@Override
	protected DCP_OrderChargeAmtUpdate_OpenRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderChargeAmtUpdate_OpenRes();
	}
	
	private void AddPartToSalePay(DCP_OrderChargeAmtUpdate_OpenReq req,String shop_create,Double orderAmt,Double orderPayAmt,StringBuilder memoMsg,StringBuilder errorMsg) throws Exception
	{
		if(memoMsg==null)
		{
			memoMsg = new StringBuilder();
		}
		
		if(errorMsg==null)
		{
			errorMsg = new StringBuilder();
		}
		
		boolean nResult = false;
		
		String o_shop = req.getRequest().getShopId();
		String o_organizationNO = o_shop;
		String oEId = req.getRequest().geteId();
		String orderNO = req.getRequest().getOrderNo();
		String loadDocType = req.getRequest().getLoad_docType();
		
		if (req.getRequest().getPay()!=null&&req.getRequest().getPay().isEmpty()==false) 
		{
			HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销付款方式增加】【新增提货付款档】开始，单号OrderNO="+orderNO);
			
			try
			{								
					
				// 这里可以把退订的支付方式存一下
				String sqlString = "select * from (select max(item) as MAXITEM from OC_order_pay where EID='" + oEId
					+ "'  and load_doctype='" + loadDocType + "' and orderno='" + orderNO + "' )";
				List<Map<String, Object>> listpayList = this.doQueryData(sqlString, null);
				int paycount = 1;
				if (listpayList != null && !listpayList.isEmpty()) 
				{
					int maxItem = 0;
					try {
						maxItem = Integer.parseInt(listpayList.get(0).get("MAXITEM").toString());
	
          } catch (Exception e) {
	
          }
					
					paycount = maxItem +1;
														
				}
				String curdate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
				double realPay_tot = 0;//实际付款金额(录入金额-找零-溢收)合计
				String payMemo = "";

				for (DCP_OrderChargeAmtUpdate_OpenReq.level2Pay map : req.getRequest().getPay()) 
				{
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
					
						String bdate = map.getBdate();
						if(bdate==null||bdate.trim().isEmpty())
						{
							bdate = curdate;
						}
						
						String payShop = map.getPayShop();
						if(payShop==null||payShop.trim().isEmpty())
						{
							payShop = o_shop;
						}
						
						String payLoadDocType = map.getPayLoadDocType();
						if(payLoadDocType==null||payLoadDocType.trim().isEmpty())
						{
							payLoadDocType = loadDocType;
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
						String teriminalNO = map.getTeriminalNO();
						
						payMemo+="付款项次："+map.getItem() +" 付款方式："+map.getPayName() +" 金额 ："+map.getPay()+" 找零："+map.getChanged()+" 溢收："+map.getExtra()+"<br>";
						
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
						
						if (isOnlinePay != null && isOnlinePay.length() > 1) {
							isOnlinePay = isOnlinePay.substring(0, 1);// 数据库最长1
						}
							
						String[] columns2 = { "EID", "ORGANIZATIONNO", "SHOPID", "ORDERNO", "ITEM", "LOAD_DOCTYPE",
							"PAYCODE", "PAYCODEERP", "PAYNAME", "CARDNO", "CTTYPE", "PAYSERNUM", "SERIALNO", "REFNO",
							"TERIMINALNO", "DESCORE", "PAY", "EXTRA", "CHANGED", "BDATE", "ISORDERPAY", "STATUS",
							"ISONLINEPAY", "ORDER_PAYCODE", "RCPAY", "SHOP_PAY", "LOAD_DOCTYPE_PAY","ORDERPAYID" };
						DataValue[] insValue2 = null;
	
						insValue2 = new DataValue[] { new DataValue(oEId, Types.VARCHAR), // EID
							new DataValue(shop_create, Types.VARCHAR), // ORGANIZATIONNO 数据库下单门店与单头一致
							new DataValue(shop_create, Types.VARCHAR), // SHOPID
							new DataValue(orderNO, Types.VARCHAR), // ORDERNO
							new DataValue(paycount, Types.INTEGER), // ITEM
							new DataValue(loadDocType, Types.VARCHAR), // LOAD_DOCTYPE
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
							new DataValue("N", Types.VARCHAR), // ISORDERPAY
							new DataValue("100", Types.VARCHAR), // status
							new DataValue(isOnlinePay, Types.VARCHAR), // ISONLINEPAY
							new DataValue("", Types.VARCHAR), // ORDER_PAYCODE
							new DataValue("0", Types.VARCHAR), // RCPAY
							new DataValue(payShop, Types.VARCHAR), // SHOP_PAY
							new DataValue(payLoadDocType, Types.VARCHAR),// LOAD_DOCTYPE_PAY
							new DataValue(req.getRequestId(), Types.VARCHAR) //ORDERPAYID
						};
	
						InsBean ib2 = new InsBean("OC_ORDER_PAY", columns2);
						ib2.addValues(insValue2);
						this.addProcessData(new DataProcessBean(ib2));
						paycount++;
					}
				
			//更新下已付金额
				if(realPay_tot>0)
				{
					UptBean ub1 = null;	
					ub1 = new UptBean("OC_ORDER");
					
					//已付金额
					ub1.addUpdateValue("PAYAMT", new DataValue(realPay_tot, Types.FLOAT, DataExpression.UpdateSelf));
          String payStatus = "2";//1.未支付 2.部分支付 3.付清
					if(realPay_tot+orderPayAmt<orderAmt)//原支付金额+当前支付金额小于订单金额=部分支付
					{
						payStatus = "2";
					}
					else
					{
						payStatus = "3";
					}
					ub1.addUpdateValue("PAYSTATUS", new DataValue(payStatus,Types.VARCHAR));//1.未支付 2.部分支付 3.付清
					//condition
					ub1.addCondition("EID", new DataValue(oEId, Types.VARCHAR));
					ub1.addCondition("orderno", new DataValue(orderNO, Types.VARCHAR));
					ub1.addCondition("load_doctype", new DataValue(loadDocType, Types.VARCHAR));		
					this.addProcessData(new DataProcessBean(ub1));
				}
											
				HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销付款方式增加】【新增订金订单冲销付款档】添加结束，单号OrderNO="+orderNO);
				nResult = true;
				memoMsg.append(payMemo);
				
		  }
			catch (Exception e)
			{
				HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销付款方式增加】【新增订金订单冲销付款档】异常："+e.getMessage()+"，单号OrderNO="+orderNO);			
				errorMsg.append("新增订金订单冲销付款方式失败！异常："+e.getMessage());				
			}		
			
		}		
	}
	
	/**
	 * 判断下是否全部提完了，更新订单已完成状态
	 * @param req 请求
	 * @param orderShop 下单门店（数据库中shop）
	 * @throws Exception
	 */
	private void UpdateOrderCompleted(DCP_OrderChargeAmtUpdate_OpenReq req,String orderShop) throws Exception
	{
		try 
		{
			String o_shop = req.getRequest().getShopId();
			String o_organizationNO = o_shop;
			String oEId = req.getRequest().geteId();
			String orderNO = req.getRequest().getOrderNo();		
			String loadDocType = req.getRequest().getLoad_docType();
			StringBuffer sqlbuf = new StringBuffer("");
			sqlbuf.append("select * from (");
			sqlbuf.append("select sum(qty) as qty_tot,sum(pickqty) as pickqty_tot,sum(rqty) as rqty_tot  from OC_order_detail ");
			sqlbuf.append(" where qty>0 and EID='"+oEId+"'  and LOAD_DOCTYPE='"+ loadDocType+"' and orderno='"+orderNO+"'");
			sqlbuf.append(")");
			String sql = sqlbuf.toString();
			HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销是否全部提完】【查询开始】，单号OrderNO="+orderNO+" 查询sql:"+sql);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
			if(getQDataDetail!=null&&getQDataDetail.isEmpty()==false)
			{
				float qty_tot = 0;
				float pickqty_tot = 0;
				float rqty_tot = 0;
				try 
				{					
					qty_tot = Float.parseFloat(getQDataDetail.get(0).get("QTY_TOT").toString());				
				} catch (Exception e) {
			// TODO: handle exception	
				}
				
				try 
				{					
					pickqty_tot = Float.parseFloat(getQDataDetail.get(0).get("PICKQTY_TOT").toString());				
				} catch (Exception e) {
			// TODO: handle exception	
				}
				
				try 
				{					
					rqty_tot = Float.parseFloat(getQDataDetail.get(0).get("RQTY_TOT").toString());				
				} catch (Exception e) {
			// TODO: handle exception	
				}
				
				//更新已完成
				if(qty_tot-pickqty_tot-rqty_tot == 0)
				{
					UptBean ub1 = null;	
					ub1 = new UptBean("OC_ORDER");
					
					ub1.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));
		     
					//condition
					ub1.addCondition("EID", new DataValue(oEId, Types.VARCHAR));
					ub1.addCondition("orderno", new DataValue(orderNO, Types.VARCHAR));
					ub1.addCondition("load_doctype", new DataValue(loadDocType, Types.VARCHAR));		
					this.addProcessData(new DataProcessBean(ub1));
					
					StringBuilder memoMsg = new StringBuilder();
					StringBuilder errorMsg = new StringBuilder();
				
					this.doExecuteDataToDB();
					HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销是否全部提完】【全部提完】更新单据状态已完成，成功，单号OrderNO="+orderNO);
				
					//region 写下日志
					try
					{/*
						DCP_OrderStatusLogCreateReq req_log = new DCP_OrderStatusLogCreateReq();
						req_log.setDatas(new ArrayList<DCP_OrderStatusLogCreateReq.level1Elm>());
					
						DCP_OrderStatusLogCreateReq.level1Elm onelv1 = req_log.new level1Elm();
						onelv1.setCallback_status("0");
						onelv1.setLoadDocType(loadDocType);

						onelv1.setNeed_callback("N");
						onelv1.setNeed_notify("N");
						
						onelv1.setoEId(oEId);

						String opNO = req.getRequest().getOpNo()==null?"":req.getRequest().getOpNo();

						String o_opName = req.getRequest().getOpName()==null?"":req.getRequest().getOpName();

						onelv1.setO_opName(o_opName);
						onelv1.setO_opNO(opNO);
						
						onelv1.setO_organizationNO(orderShop);//下单门店
						onelv1.setoShopId(orderShop);
						onelv1.setOrderNO(orderNO);
						String statusType = "1";//其他状态
						String updateStaus = "11";//订单修改
						
						onelv1.setStatusType(statusType);				 					
						onelv1.setStatus(updateStaus);
						StringBuilder statusTypeNameObj = new StringBuilder();
						String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
						String statusTypeName = statusTypeNameObj.toString();
						onelv1.setStatusTypeName(statusTypeName);
						onelv1.setStatusName(statusName);

						String memo = "";
						memo += statusTypeName+"-->" + statusName+"(订金订单冲销全部提完)<br>";								
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
							HelpTools.writelog_waimai("【写表OC_orderStatuslog保存成功】【订金订单冲销全部提完更新已完成】"+" 订单号orderNO:"+orderNO);
						}
						else
						{			  		 
							HelpTools.writelog_waimai("【写表OC_orderStatuslog异常】【订金订单冲销全部提完更新已完成】"+errorMessage.toString()+" 订单号orderNO:"+orderNO);
						}
						this.pData.clear();
						

					*/}
					catch (Exception  e)
					{
						HelpTools.writelog_waimai("【写表OC_orderStatuslog异常】【订金订单冲销全部提完更新已完成】 异常报错 "+e.toString()+" 订单号orderNO:"+orderNO);
					}			
					//endregion
					
				}
				else
				{
					HelpTools.writelog_waimai("【第三方调用DCP_OrderChargeAmtUpdate_Open接口，订金订单冲销是否全部提完】【没有全部提完】无需更新单据状态，单号OrderNO="+orderNO);
				}			
				
			}
		
	
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
	
		}
		
	}
	
}
