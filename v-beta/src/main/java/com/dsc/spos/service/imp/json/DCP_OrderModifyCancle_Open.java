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
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_OrderCreateCancle_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderModifyCancle_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderCreateCancle_OpenRes;
import com.dsc.spos.json.cust.res.DCP_OrderModifyCancle_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderModifyCancle_Open extends SPosAdvanceService<DCP_OrderModifyCancle_OpenReq, DCP_OrderModifyCancle_OpenRes>
{

	@Override
	protected void processDUID(DCP_OrderModifyCancle_OpenReq req, DCP_OrderModifyCancle_OpenRes res) throws Exception {
		// TODO Auto-generated method stub
		String oShopId = req.getRequest().getShopId();
		String oEId = req.getRequest().geteId();
		String orderNO = req.getRequest().getOrderNo();		
		String loadDocType = req.getRequest().getLoad_docType();
		if(loadDocType==null||loadDocType.equals("4")==false)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "不支持该类型订单！(loadDocType="+loadDocType+")");
		}
		String sql="select * from OC_order where EID='"+req.getRequest().geteId()+"' and orderno='"+req.getRequest().getOrderNo()+"' and load_doctype='"+req.getRequest().getLoad_docType()+"' ";
		HelpTools.writelog_waimai("【第三方调用DCP_OrderModifyCancle_Open接口，订金增加撤销】查询开始：单号OrderNO="+orderNO+" 传入的参数oShopId="+oShopId+" LOAD_DOCTYPE="+loadDocType+" 查询语句："+sql);
		List<Map<String, Object>> listdateList=this.doQueryData(sql, null);
		if(listdateList!=null&&!listdateList.isEmpty())
		{
			
		  String	orderShop = listdateList.get(0).get("SHOPID").toString();
			//判断下请求ID 是否存在支付方式
			sql="select * from OC_order_pay where EID='"+req.getRequest().geteId()+"' and orderno='"+req.getRequest().getOrderNo()+"' and load_doctype='"+req.getRequest().getLoad_docType()+"' and ORDERPAYID='"+req.getRequestId()+"'";
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModifyCancle_Open接口，订金增加撤销】【需要撤销的支付方式】查询开始：单号OrderNO="+orderNO+" 传入的请求Id参数RequestId=ORDERPAYID="+req.getRequestId()+" 查询语句："+sql);
			List<Map<String, Object>> listPayList=this.doQueryData(sql, null);
			if(listPayList!=null&&!listPayList.isEmpty())
			{
				double realPay_tot = 0;//实际付款金额(录入金额-找零-溢收)合计
				String payMemo = "";
				for (Map<String, Object> map : listPayList) 
				{
				//付款金额
				  double realPay = 0;//实际付款金额=录入金额-找零-溢收=pay-change-extra
					double payAmount = 0;
					try {

						payAmount = Double.parseDouble(map.get("PAY").toString());

					} catch (Exception e) {

					}
					
				  //找零
					double changed = 0;
					try {

						changed = Double.parseDouble(map.get("CHANGED").toString());

					} catch (Exception e) {

					}
				
					//溢收
					double extra = 0;
					try {

						extra = Double.parseDouble(map.get("EXTRA").toString());

					} catch (Exception e) {

					}
					realPay = payAmount-changed-extra;
					if(realPay<0)
					{
						realPay = 0;
					}
					realPay_tot += realPay;
			
					payMemo+="付款项次："+map.get("ITEM").toString() +" 付款方式："+map.get("PAYNAME").toString() +" 金额 ："+payAmount+" 找零："+changed+" 溢收："+extra+"<br>";
				}
				
			 //更新单头
				UptBean ub1 = null;	
				ub1 = new UptBean("OC_ORDER");
				
				//已付金额
				ub1.addUpdateValue("PAYAMT", new DataValue(realPay_tot, Types.FLOAT, DataExpression.SubSelf));
        String payStatus = "2";//1.未支付 2.部分支付 3.付清			
				ub1.addUpdateValue("PAYSTATUS", new DataValue(payStatus,Types.VARCHAR));//1.未支付 2.部分支付 3.付清
				//condition
				ub1.addCondition("EID", new DataValue(oEId, Types.VARCHAR));
				ub1.addCondition("orderno", new DataValue(orderNO, Types.VARCHAR));
				ub1.addCondition("load_doctype", new DataValue(loadDocType, Types.VARCHAR));		
				this.addProcessData(new DataProcessBean(ub1));
				//更新付款档
				DelBean ub2 = null;	
				ub2 = new UptBean("OC_ORDER_PAY");

				//condition
				ub2.addCondition("ORDERPAYID", new DataValue(req.getRequestId(), Types.VARCHAR));
				
				ub2.addCondition("EID", new DataValue(req.getRequest().geteId(), Types.VARCHAR));
				ub2.addCondition("orderno", new DataValue(req.getRequest().getOrderNo(), Types.VARCHAR));
				ub2.addCondition("load_doctype", new DataValue(req.getRequest().getLoad_docType(), Types.VARCHAR));		
				this.addProcessData(new DataProcessBean(ub2));
				
				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceDescription("服务执行成功！");
				HelpTools.writelog_waimai("【第三方调用DCP_OrderModifyCancle_Open接口，订金增加撤销】【需要撤销的支付方式】删除成功！单号OrderNO="+orderNO+" 传入的请求Id参数RequestId=ORDERPAYID="+req.getRequestId());
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
					memo += statusTypeName+"-->" + statusName+"(增加订金撤销)<br>";
					memo += "撤销订金金额:"+realPay_tot+"<br>";
					memo += payMemo;
								
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
			else
			{
				res.setSuccess(false);
				res.setServiceDescription("传入的请求节点RequestId没有对应的付款记录，撤销失败！");
				HelpTools.writelog_waimai("【第三方调用DCP_OrderModifyCancle_Open接口，订金增加撤销】【需要撤销的支付方式】查询完成：没有对应的付款方式！单号OrderNO="+orderNO+" 传入的请求Id参数RequestId=ORDERPAYID="+req.getRequestId()+" 查询语句："+sql);
			}
			
		}
		else
		{
			res.setSuccess(false);
			res.setServiceDescription("订单不存在请重新确认！");
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModifyCancle_Open接口，订金增加撤销】查询完成：该订单不存在！ 单号OrderNO="+orderNO);
		}
				
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderModifyCancle_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderModifyCancle_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderModifyCancle_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderModifyCancle_OpenReq req) throws Exception {
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
    if (isFail)
    {
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }    
    return isFail;
		
	}

	@Override
	protected TypeToken<DCP_OrderModifyCancle_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderModifyCancle_OpenReq>() {};
	}

	@Override
	protected DCP_OrderModifyCancle_OpenRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderModifyCancle_OpenRes();
	}

}
