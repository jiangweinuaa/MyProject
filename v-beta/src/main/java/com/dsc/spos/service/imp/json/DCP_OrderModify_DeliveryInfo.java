package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderModify_DeliveryInfoReq;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderModify_DeliveryInfoRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderModify_DeliveryInfo extends SPosAdvanceService<DCP_OrderModify_DeliveryInfoReq,DCP_OrderModify_DeliveryInfoRes> {

	@Override
	protected void processDUID(DCP_OrderModify_DeliveryInfoReq req, DCP_OrderModify_DeliveryInfoRes res)
		throws Exception {
	// TODO Auto-generated method stub
		String oShopId = req.getRequest().getShopId();
		String oEId = req.getRequest().geteId();
		String orderNO = req.getRequest().getOrderNo();		
		String loadDocType = req.getRequest().getLoad_docType();
		
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from OC_order where EID='"+oEId+"'");
		sqlbuf.append(" and LOAD_DOCTYPE='"+ loadDocType+"' and orderno='"+orderNO+"'");
		String sql = sqlbuf.toString();
		String orderStatus = "";//订单状态
		String orderShop = "";//数据库里面下单门店	
		boolean nResult = false;
		StringBuilder meassgeInfo = new StringBuilder();
		HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_DeliveryInfo接口，配送信息修改】查询开始：单号OrderNO="+orderNO+" 传入的参数shopId="+oShopId+" LOAD_DOCTYPE="+loadDocType+" 查询语句："+sql);
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
		if(getQDataDetail==null||getQDataDetail.isEmpty())
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("该订单不存在");
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_DeliveryInfo接口，配送信息修改】查询完成：该订单不存在！ 单号OrderNO="+orderNO);
			return;
		}
		
		orderStatus = getQDataDetail.get(0).get("STATUS").toString();
		
		orderShop = getQDataDetail.get(0).get("SHOPID").toString();
		
	  String orderShipType = getQDataDetail.get(0).get("SHIPTYPE").toString();
	  String orderContMan = getQDataDetail.get(0).get("CONTMAN").toString();
	  String orderContTel = getQDataDetail.get(0).get("CONTTEL").toString();
	  String orderAdress = getQDataDetail.get(0).get("ADDRESS").toString();
	  String orderGetMan = getQDataDetail.get(0).get("GETMAN").toString();
	  String orderGetManTel = getQDataDetail.get(0).get("GETMANTEL").toString();
	  String orderShipDate = getQDataDetail.get(0).get("SHIPDATE").toString();
	  String orderShipTime = getQDataDetail.get(0).get("SHIPTIME").toString();
	  String orderShipShopNo = getQDataDetail.get(0).get("SHIPPINGSHOP").toString();
	  String orderShipShopName = getQDataDetail.get(0).get("SHIPPINGSHOPNAME").toString();
	  String orderMemo = getQDataDetail.get(0).get("MEMO").toString();
		
		HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_DeliveryInfo接口，配送信息修改】查询完成：单号OrderNO="+orderNO+" 数据库中下单门店="+orderShop+" 订单状态status="+orderStatus);
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
			res.setServiceDescription("该订单状态是"+ss+"，不能修改配送信息！");
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_DeliveryInfo接口，配送信息修改】该订单状态是"+ss+"，不能修改配送信息！单号OrderNO="+orderNO+" 数据库中下单门店="+orderShop+" 订单状态status="+orderStatus);			
			return;
		}
		
	//防止没有传更新的节点，那么就不用执行语句
		boolean isNeedUpdate = false;
		StringBuffer logmemo = new StringBuffer("");
		UptBean ub1 = null;	
		ub1 = new UptBean("OC_ORDER");
		
		//condition
		ub1.addCondition("EID", new DataValue(oEId, Types.VARCHAR));
		ub1.addCondition("orderno", new DataValue(orderNO, Types.VARCHAR));
		ub1.addCondition("load_doctype", new DataValue(loadDocType, Types.VARCHAR));	
		
		if( req.getRequest().getShipType()!=null &&req.getRequest().getShipType().trim().length()>0)
		{							
			if(req.getRequest().getShipType().length()>1)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"传入的shipType节点值太大，长度不能超过1，");
			}
			ub1.addUpdateValue("shiptype", new DataValue(req.getRequest().getShipType(), Types.VARCHAR));
			
			//修改配送方式的时候，同时清空配送状态
			if( !orderStatus.equals("12") )
			{
				//为12时，不接受配送状态变更的信息，否则会影响到之前的配送状态
				ub1.addUpdateValue("DeliveryStutas", new DataValue("", Types.VARCHAR));
			}
			
			isNeedUpdate = true;
			
			String bname="";
			
			if(orderShipType.equals("2"))
			{
				bname="配送";
			}
			else if(orderShipType.equals("3"))
			{
				bname="自提";
			}
			else if(orderShipType.equals("5"))
			{
				bname="总部配送";
			}
			String ename="";
			if(req.getRequest().getShipType().equals("2"))
			{
				ename="配送";
			}
		  else if(req.getRequest().getShipType().equals("3"))
			{
				ename="自提";
			}
		  else if(req.getRequest().getShipType().equals("5"))
			{
				ename="总部配送";
			}
			
				
			logmemo.append("配送方式："+bname+"-->"+ ename+"<br>");
		}
		
		if( req.getRequest().getShipShopNo()!=null )
		{
			if(req.getRequest().getShipShopNo().length()>20)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"传入的shipShopNo节点值太大，长度不能超过20，");
			}
			ub1.addUpdateValue("SHIPPINGSHOP", new DataValue(req.getRequest().getShipShopNo(), Types.VARCHAR));
			String shippingShopName = req.getRequest().getShipShopName()==null?"":req.getRequest().getShipShopName();
			if(shippingShopName!=null&&shippingShopName.isEmpty()==false)
			{
				if(shippingShopName.length()>80)
				{
					shippingShopName = shippingShopName.substring(0, 80);
				}
				ub1.addUpdateValue("SHIPPINGSHOPNAME", new DataValue(shippingShopName, Types.VARCHAR));
			}
			
			isNeedUpdate = true;
			logmemo.append("配送门店："+orderShipShopNo+orderShipShopName+"-->"+ req.getRequest().getShipShopNo()+req.getRequest().getShipShopName()+"<br>");
			
		}	
		if( req.getRequest().getShipDate()!=null )
		{
			if(req.getRequest().getShipDate().length()>8)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"传入的shipDate节点值太大，长度不能超过8，");
			}
			ub1.addUpdateValue("SHIPDATE", new DataValue(req.getRequest().getShipDate(), Types.VARCHAR));
			
			isNeedUpdate = true;
			logmemo.append("配送日期："+orderShipDate+"-->"+ req.getRequest().getShipDate()+"<br>");
		}
		
		if( req.getRequest().getShipTime()!=null )
		{
			if(req.getRequest().getShipTime().length()>50)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"传入的shipTime节点值太大，长度不能超过50，");
			}
			ub1.addUpdateValue("SHIPTIME", new DataValue(req.getRequest().getShipTime(), Types.VARCHAR));
			
			isNeedUpdate = true;
			logmemo.append("配送时间："+orderShipTime+"-->"+ req.getRequest().getShipTime()+"<br>");
		}
		
		if(req.getRequest().getAddress()!=null )
		{
			if(req.getRequest().getAddress().length()>100)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"传入的address节点值太大，长度不能超过100，");
			}
			ub1.addUpdateValue("address", new DataValue(req.getRequest().getAddress(), Types.VARCHAR));
			
			isNeedUpdate = true;
			logmemo.append("收货地址："+orderAdress+"-->"+ req.getRequest().getAddress()+"<br>");
		}
		
		if( req.getRequest().getContMan()!=null )
		{
			if(req.getRequest().getContMan().length()>20)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"传入的contMant节点值太大，长度不能超过20，");
			}
			ub1.addUpdateValue("contman", new DataValue(req.getRequest().getContMan(), Types.VARCHAR));
			isNeedUpdate = true;
			
			logmemo.append("订货人："+orderContMan+"-->"+ req.getRequest().getContMan()+"<br>");
		}
		
		if( req.getRequest().getContTel()!=null )
		{
			if(req.getRequest().getContTel().length()>30)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"传入的contTel节点值太大，长度不能超过30，");
			}
			ub1.addUpdateValue("conttel", new DataValue(req.getRequest().getContTel(), Types.VARCHAR));
			isNeedUpdate = true;
			logmemo.append("订货人电话："+orderContTel+"-->"+ req.getRequest().getContTel()+"<br>");
		}
						
		if( req.getRequest().getGetMan()!=null )
		{
			if(req.getRequest().getGetMan().length()>40)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"传入的getMan节点值太大，长度不能超过40，");
			}
			ub1.addUpdateValue("getman", new DataValue(req.getRequest().getGetMan(), Types.VARCHAR));
			
			isNeedUpdate = true;
			logmemo.append("收货人："+orderGetMan+"-->"+ req.getRequest().getGetMan()+"<br>");
		}
		
		if( req.getRequest().getGetMantel()!=null )
		{
			if(req.getRequest().getGetMantel().length()>40)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"传入的getManTel节点值太大，长度不能超过40，");
			}
			ub1.addUpdateValue("GETMANTEL", new DataValue(req.getRequest().getGetMantel(), Types.VARCHAR));
			
			isNeedUpdate = true;
			logmemo.append("收货人电话："+orderGetManTel+"-->"+ req.getRequest().getGetMantel()+"<br>");
		}
		
		if(isNeedUpdate==false)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"请传入需要修改的节点！");
		}
		this.addProcessData(new DataProcessBean(ub1));
		this.doExecuteDataToDB();
		HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_DeliveryInfo接口，配送信息修改】修改成功，单号OrderNO="+orderNO);
	
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
			memo += statusTypeName+"-->" + statusName+"<br>";
			memo += logmemo;
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

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderModify_DeliveryInfoReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderModify_DeliveryInfoReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderModify_DeliveryInfoReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderModify_DeliveryInfoReq req) throws Exception {
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
    
    
    if (isFail)
    {
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }    
    return isFail;
	
	}

	@Override
	protected TypeToken<DCP_OrderModify_DeliveryInfoReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderModify_DeliveryInfoReq>(){} ;
	}

	@Override
	protected DCP_OrderModify_DeliveryInfoRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderModify_DeliveryInfoRes();
	}

}
