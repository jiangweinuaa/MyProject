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
import com.dsc.spos.json.cust.req.DCP_OrderModifyCancle_PartToSale_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderModifyCancle_PartToSale_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderModifyCancle_PartToSale_Open extends SPosAdvanceService<DCP_OrderModifyCancle_PartToSale_OpenReq, DCP_OrderModifyCancle_PartToSale_OpenRes>
{

	@Override
	protected void processDUID(DCP_OrderModifyCancle_PartToSale_OpenReq req, DCP_OrderModifyCancle_PartToSale_OpenRes res) throws Exception {
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
		HelpTools.writelog_waimai("【第三方调用DCP_OrderModifyCancle_PartToSale_Open接口，部分提货撤销】查询开始：单号OrderNO="+orderNO+" 传入的参数oShopId="+oShopId+" LOAD_DOCTYPE="+loadDocType+" 查询语句："+sql);
		List<Map<String, Object>> listdateList=this.doQueryData(sql, null);
		if(listdateList!=null&&!listdateList.isEmpty())
		{	
		  String	orderShop = listdateList.get(0).get("SHOPID").toString();
		  String	orderStatus = listdateList.get(0).get("STATUS").toString();
		  float pickTotAmt_cancle = 0;//撤销提货金额
		  //判断下请求ID 是否存在部分提货商品
			sql="select * from OC_order_detail where EID='"+req.getRequest().geteId()+"' and orderno='"+req.getRequest().getOrderNo()+"' and load_doctype='"+req.getRequest().getLoad_docType()+"' and ORDERSALEID='"+req.getRequestId()+"'";
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModifyCancle_PartToSale_Open接口，部分提货撤销】【需要撤销的提货商品】查询开始：单号OrderNO="+orderNO+" 传入的请求Id参数RequestId=ORDERPAYID="+req.getRequestId()+" 查询语句："+sql);
			List<Map<String, Object>> listGoodList=this.doQueryData(sql, null);
			if(listGoodList!=null&&!listGoodList.isEmpty())
			{
				String payMemo = "";
				String goodMemo = "";
				double realPay_tot = 0;//撤销之前的实际付款金额(录入金额-找零-溢收)合计
				for (Map<String, Object> mapGood : listGoodList) 
				{
					String item = mapGood.get("ITEM").toString();
					int item_i = 0;
					try 
					{
						item_i = Integer.parseInt(item);		
					} catch (Exception e) {
				// TODO: handle exception
				
					}
					String qty_str = mapGood.get("QTY").toString();//单身数量
					String pickQty_str = mapGood.get("PICKQTY").toString();//已经提货数量
					String previousPRQty_str = mapGood.get("PRQTY_PREVIOUS").toString();//已提货/退货数量更新前数量
					String price_db_str = mapGood.get("PRICE").toString();//单价
					float qty = 0;
					try 
					{					
						qty = Float.parseFloat(qty_str);				
					} catch (Exception e) {			
					}
					
					float pickQty = 0;
					try 
					{					
						pickQty = Float.parseFloat(pickQty_str);				
					} catch (Exception e) {
					}
					
					float previousPRQty = 0;
					try 
					{					
						previousPRQty = Float.parseFloat(previousPRQty_str);				
					} catch (Exception e) {			
					}
					
					float price_db = 0;
					try 
					{					
						price_db = Float.parseFloat(price_db_str);				
					} catch (Exception e) {
				// TODO: handle exception	
					}
					
					if (qty<0) //部分退单的单身qty为负
					{
						continue;		
					}
					float pickAmt_previous = (pickQty - previousPRQty) * price_db;
					
					if(pickAmt_previous<0)
					{
						pickAmt_previous = 0;
					}
					
					UptBean ubGood = null;	
					ubGood = new UptBean("OC_order_detail");
					
					
					ubGood.addUpdateValue("PICKQTY", new DataValue(previousPRQty_str, Types.FLOAT));
					//已提货或已退货更新前数量
					ubGood.addUpdateValue("PRQTY_PREVIOUS", new DataValue(pickQty, Types.FLOAT));
					//撤销时需要
					ubGood.addUpdateValue("ORDERSALEID", new DataValue(req.getRequestId(), Types.VARCHAR));
					//condition
					ubGood.addCondition("EID", new DataValue(oEId, Types.VARCHAR));
					ubGood.addCondition("orderno", new DataValue(orderNO, Types.VARCHAR));
					ubGood.addCondition("load_doctype", new DataValue(loadDocType, Types.VARCHAR));
					ubGood.addCondition("ITEM", new DataValue(item_i, Types.INTEGER));
					this.addProcessData(new DataProcessBean(ubGood));
					pickTotAmt_cancle += pickAmt_previous;
					goodMemo +="撤销提货项次："+item+" 名称"+mapGood.get("PLUNAME")+" 数量："+pickQty+"-->"+previousPRQty_str+"<br>";
			
		
				}
				
			 //判断下请求ID 是否存在支付方式
				sql="select * from OC_order_pay where EID='"+req.getRequest().geteId()+"' and orderno='"+req.getRequest().getOrderNo()+"' and load_doctype='"+req.getRequest().getLoad_docType()+"' and ORDERPAYID='"+req.getRequestId()+"'";
				HelpTools.writelog_waimai("【第三方调用DCP_OrderModifyCancle_PartToSale_Open接口，部分提货撤销】【需要撤销的提货商品】【需要撤销的支付方式】查询开始：单号OrderNO="+orderNO+" 传入的请求Id参数RequestId"+req.getRequestId());
				List<Map<String, Object>> listPayList=this.doQueryData(sql, null);
				if(listPayList!=null&&!listPayList.isEmpty())
				{
					
					for (Map<String, Object> map : listPayList) 
					{				  
					  double realPay = 0;//实际付款金额=录入金额-找零-溢收=pay-change-extra
					  //付款金额
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
				
						payMemo+="撤销付款项次："+map.get("ITEM").toString() +" 付款方式："+map.get("PAYNAME").toString() +" 金额 ："+payAmount+" 找零："+changed+" 溢收："+extra+"<br>";
					}
					
				 
					//删除付款档
					DelBean db2 = null;	
					db2 = new UptBean("OC_ORDER_PAY");

					//condition
					db2.addCondition("ORDERPAYID", new DataValue(req.getRequestId(), Types.VARCHAR));
					
					db2.addCondition("EID", new DataValue(req.getRequest().geteId(), Types.VARCHAR));
					db2.addCondition("orderno", new DataValue(req.getRequest().getOrderNo(), Types.VARCHAR));
					db2.addCondition("load_doctype", new DataValue(req.getRequest().getLoad_docType(), Types.VARCHAR));		
					this.addProcessData(new DataProcessBean(db2));
					
					
				}
				 //更新单头
				if(realPay_tot>0)
				{
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
					
				}
				
				UptBean ub1 = null;	
				ub1 = new UptBean("OC_ORDER");
				
				//已付金额
				ub1.addUpdateValue("WRITEOFFAMT", new DataValue(pickTotAmt_cancle, Types.FLOAT, DataExpression.SubSelf));
        
				//已完成的撤销后要更新回去，这个地方有问题，因为没有记录之前的状态。只能更新成2
				if(orderStatus.equals("11"))
				{
					ub1.addCondition("STATUS", new DataValue("2", Types.VARCHAR));
				}
				
				
				//condition
				ub1.addCondition("EID", new DataValue(oEId, Types.VARCHAR));
				ub1.addCondition("orderno", new DataValue(orderNO, Types.VARCHAR));
				ub1.addCondition("load_doctype", new DataValue(loadDocType, Types.VARCHAR));		
				this.addProcessData(new DataProcessBean(ub1));
				
				
				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceDescription("服务执行成功！");
				HelpTools.writelog_waimai("【第三方调用DCP_OrderModifyCancle_PartToSale_Open接口，部分提货撤销】【需要撤销的支付方式】删除成功！单号OrderNO="+orderNO+" 传入的请求Id参数RequestId=ORDERPAYID="+req.getRequestId());
			  
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
					memo += statusTypeName+"-->" + statusName+"(部分提货撤销)<br>";
					memo += goodMemo;
					memo += "撤销部分提货付款金额:"+realPay_tot+"<br>";
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
				res.setServiceDescription("传入的请求节点RequestId没有对应的提货商品，撤销失败！");
				HelpTools.writelog_waimai("【第三方调用DCP_OrderModifyCancle_PartToSale_Open接口，部分提货撤销】【需要撤销的提货商品】查询完成：没有对应的付款方式！单号OrderNO="+orderNO+" 传入的请求Id参数RequestId=ORDERSALEID="+req.getRequestId()+" 查询语句："+sql);
				
			}
		  				
		}
		else
		{
			res.setSuccess(false);
			res.setServiceDescription("订单不存在请重新确认！");
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModifyCancle_PartToSale_Open接口，部分提货撤销】查询完成：该订单不存在！ 单号OrderNO="+orderNO);
		}
				
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderModifyCancle_PartToSale_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderModifyCancle_PartToSale_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderModifyCancle_PartToSale_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderModifyCancle_PartToSale_OpenReq req) throws Exception {
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
	protected TypeToken<DCP_OrderModifyCancle_PartToSale_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderModifyCancle_PartToSale_OpenReq>() {};
	}

	@Override
	protected DCP_OrderModifyCancle_PartToSale_OpenRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderModifyCancle_PartToSale_OpenRes();
	}

}
