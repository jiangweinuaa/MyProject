package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_OrderModify_PartToRefundReq;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq;
import com.dsc.spos.json.cust.req.DCP_OrderModify_PartToRefundReq.level2Good;
import com.dsc.spos.json.cust.req.DCP_OrderStatusLogCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderModify_PartToRefundRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderModify_PartToRefund extends SPosAdvanceService<DCP_OrderModify_PartToRefundReq,DCP_OrderModify_PartToRefundRes>{

	@Override
	protected void processDUID(DCP_OrderModify_PartToRefundReq req, DCP_OrderModify_PartToRefundRes res)
		throws Exception {
	// TODO Auto-generated method stub
		String oShopId = req.getRequest().getShopId();
		String oEId = req.getRequest().geteId();
		String orderNO = req.getRequest().getOrderNo();		
		String loadDocType = req.getRequest().getLoad_docType();
		String opNO = req.getRequest().getOpNo()==null?"":req.getRequest().getOpNo();
		String opName = req.getRequest().getOpName()==null?"":req.getRequest().getOpName();
		
		
		if(loadDocType==null||loadDocType.equals("4")==false)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "不支持该类型订单！(loadDocType="+loadDocType+")");
		}
	  				
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from (");
		sqlbuf.append("select A.*,B.Item,B.Pluno,B.Plubarcode,B.Pluname,B.SPECNAME,B.ATTRNAME,B.UNIT,B.PRICE,B.QTY,B.Goodsgroup,B.Disc,B.Boxnum,B.BOXPRICE,B.AMT AS DETAILAMT,B.ISMEMO,B.SKUID,B.PICKQTY,B.RQTY,B.RCQTY,decode(B.QTY,0,null,B.AMT / B.QTY) DEALPRICE,B.PACKAGETYPE,B.PACKAGEMITEM,B.TOPPINGTYPE,B.TOPPINGMITEM,B.COUPONTYPE,B.COUPONCODE,B.SOURCECODE as SOURCECODE_DETAIL,B.SHOPQTY "
			+ " from OC_order A inner join OC_order_detail B on A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.Orderno=B.Orderno  AND A.LOAD_DOCTYPE=B.LOAD_DOCTYPE ");
		sqlbuf.append(" where A.EID='"+oEId+"'  and A.LOAD_DOCTYPE='"+ loadDocType+"' and A.orderno='"+orderNO+"'");
		sqlbuf.append(")");
		String sql = sqlbuf.toString();
		String orderStatus = "";//订单状态
		String orderShop = "";//数据库里面下单门店
		String isShipcompany = "N";
		boolean nResult = false;
		StringBuilder meassgeInfo = new StringBuilder();
		HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退货】查询开始：单号OrderNO="+orderNO+" 传入的参数shopNO="+oShopId+" LOAD_DOCTYPE="+loadDocType+" 查询语句："+sql);
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
		if(getQDataDetail==null||getQDataDetail.isEmpty())
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("该订单不存在");
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退货】查询完成：该订单不存在！ 单号OrderNO="+orderNO);
			return;
		}
		
		orderStatus = getQDataDetail.get(0).get("STATUS").toString();
		
		orderShop = getQDataDetail.get(0).get("SHOPID").toString();
		
		try 
		{
			isShipcompany = getQDataDetail.get(0).get("ISSHIPCOMPANY").toString();		
		} catch (Exception e) {
		}
		
		HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退货】查询完成：单号OrderNO="+orderNO+" 数据库中下单门店shop="+orderShop+" 订单状态status="+orderStatus);
		if(orderStatus.equals("3")||orderStatus.equals("11")||orderStatus.equals("12"))//已经退单
		{
			String ss = "未知";
			if(orderStatus.equals("1"))
			{
				ss = "未接单";
			}
			else if(orderStatus.equals("3"))
			{
				ss = "已取消";
			}
			/*else if(orderStatus.equals("11"))
			{
				ss = "已完成";
			}*/
			else if(orderStatus.equals("12"))
			{
				ss = "已退单";
			}
			
			res.setSuccess(false);
			res.setServiceDescription("该订单状态是"+ss+"，无法部分退货！");
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退货】该订单状态是"+ss+"，无法部分退货！单号OrderNO="+orderNO+" 数据库中下单门店shop="+orderShop+" 订单状态status="+orderStatus);			
			return;
		}
		
		boolean isNeedUpdate = false;
		String logmemo = "";
		logmemo +="部分退款金额："+req.getRequest().getRefundAmt()+"<br>";
		
	  // t100req中的payload对象
		JSONObject payload = new JSONObject();
		// 自定义payload中的json结构
		JSONObject std_data = new JSONObject();
		JSONObject parameter = new JSONObject();

		JSONArray request = new JSONArray();
		JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
		JSONArray request_detail = new JSONArray(); // 商品单身
		
		JSONArray pay_detail = new JSONArray(); // 付款单身
		
		header.put("refundtype", "1");//0.（全退），1.（部分退，默认给1） 全退：暂不走改接口；部分退：只传退货商品及退款的支付方式
		header.put("oEId", oEId);
		header.put("customerNO", " ");//操作类型（0-新建，1-修改）20191212添加		
		header.put("organizationNO", oShopId);	
		header.put("shopId", oShopId);
		header.put("orderNO", orderNO);
		header.put("loadDocType", loadDocType);
		header.put("o_opNO", opNO);
		header.put("o_opName", opName);	
		header.put("memo", "");
		
		
		//单头更新的内容
		UptBean ub1 = null;	
		ub1 = new UptBean("OC_ORDER");
		
		//已付金额
		ub1.addUpdateValue("REFUNDSTATUS", new DataValue("10", Types.VARCHAR));
		ub1.addUpdateValue("PARTREFUNDAMT", new DataValue(req.getRequest().getRefundAmt(), Types.FLOAT, DataExpression.UpdateSelf));
		ub1.addUpdateValue("REFUNDAMT", new DataValue(req.getRequest().getRefundAmt(), Types.FLOAT, DataExpression.UpdateSelf));
		//condition
		ub1.addCondition("EID", new DataValue(oEId, Types.VARCHAR));
		ub1.addCondition("orderno", new DataValue(orderNO, Types.VARCHAR));
		ub1.addCondition("load_doctype", new DataValue(loadDocType, Types.VARCHAR));		
		this.addProcessData(new DataProcessBean(ub1));
		
		for (DCP_OrderModify_PartToRefundReq.level2Good map : req.getRequest().getGoods()) 
		{
			boolean isExist = false;//是否存在
			String item = map.getItem();
			int item_i = 0;
			try 
			{
				item_i = Integer.parseInt(item);		
			} catch (Exception e) {
		// TODO: handle exception
		
			}
			
			String pluBarcode = map.getPluBarcode();
			String rQty_str = map.getrQty();
			float rQty = 0;
			try 
			{					
				rQty = Float.parseFloat(rQty_str);				
			} catch (Exception e) {
		// TODO: handle exception	
			}
			//检查传入的商品项次+条码是否存在
			for (Map<String, Object> oneData : getQDataDetail) 
			{				
				String item_db = oneData.get("ITEM").toString();				
				int item_db_i = 0;
				try 
				{
					item_db_i = Integer.parseInt(item_db);		
				} catch (Exception e) {
			// TODO: handle exception
			
				}
				String pluBarcode_db = oneData.get("PLUBARCODE").toString();
				String qty_db_str = oneData.get("QTY").toString();//单身数量
				String rQty_db_str = oneData.get("RQTY").toString();//已经退货数量
				String pickQty_db_str = oneData.get("PICKQTY").toString();//已经提货数量
				float qty_db = 0;
				try 
				{					
					qty_db = Float.parseFloat(qty_db_str);				
				} catch (Exception e) {
			// TODO: handle exception	
				}
				if (qty_db<0) //部分退单的单身qty为负
				{
					continue;		
				}
				
				float rQty_db = 0;
				try 
				{					
					rQty_db = Float.parseFloat(rQty_db_str);				
				} catch (Exception e) {
			// TODO: handle exception	
				}
				
				float pickQty_db = 0;
				try 
				{					
					pickQty_db = Float.parseFloat(pickQty_db_str);				
				} catch (Exception e) {
			// TODO: handle exception	
				}
				
				if(item_i==item_db_i&&pluBarcode.equals(pluBarcode_db))
				{
					if(rQty>qty_db)
					{
						String ss = "传入的退货项次item="+item+" 条码pluBarcode="+pluBarcode+" 名称"+map.getPluName()+" 退货数量pickQty="+rQty+" 大于数量库中QTY="+qty_db_str;
						HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退货】异常："+ss+" 单号OrderNO="+orderNO);
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,ss);
					}
					
					if(rQty+rQty_db+pickQty_db>qty_db)
					{
						String ss = "传入的退货项次item="+item+" 条码pluBarcode="+pluBarcode+" 名称"+map.getPluName()+" 退货数量pickQty="+rQty+" +数据库中已退货数量rQTY="+rQty_db+" +数据库中已提货数量pickQTY="+pickQty_db+" 大于数量库中QTY="+qty_db_str;
						HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退货】异常："+ss+" 单号OrderNO="+orderNO);
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,ss);
					}				
					UptBean ub2 = null;	
					ub2 = new UptBean("OC_order_detail");
					
					
					ub2.addUpdateValue("RQTY", new DataValue(rQty, Types.FLOAT, DataExpression.UpdateSelf));
					//已提货或已退货更新前数量
					ub2.addUpdateValue("PRQTY_PREVIOUS", new DataValue(rQty_db, Types.FLOAT));
					//撤销时需要
					ub2.addUpdateValue("ORDERSALEID", new DataValue(req.getRequestId(), Types.VARCHAR));

					//condition
					ub2.addCondition("EID", new DataValue(oEId, Types.VARCHAR));
					ub2.addCondition("orderno", new DataValue(orderNO, Types.VARCHAR));
					ub2.addCondition("load_doctype", new DataValue(loadDocType, Types.VARCHAR));
					ub2.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(ub2));
					isExist = true;
				  float	rQty_tot = rQty+rQty_db;//总退货数量
					logmemo +="退货项次："+item+" 名称："+map.getPluName()+" 退货数量："+rQty_db_str+"-->"+rQty_tot+"<br>";		
					
					//部分退单身
					 // 获取单身数据并赋值
					JSONObject body = new JSONObject(); // 存一笔单身
					body.put("seq", item_i+"");
					body.put("item_no", oneData.get("PLUNO").toString());
					body.put("unit", oneData.get("UNIT").toString());
					body.put("qty", oneData.get("QTY").toString());
					body.put("oriprice", oneData.get("PRICE").toString());
					try 
					{
						body.put("qrcode_key", oneData.get("SOURCECODE_DETAIL").toString());						
					} 
					catch (Exception e) 
					{
						body.put("qrcode_key", "");										
					}
					body.put("shopqty", oneData.get("SHOPQTY").toString() );				
					body.put("pickqty", oneData.get("PICKQTY").toString() );				
					body.put("rqty", map.getrQty() );					
					body.put("price", map.getrPrice());
					body.put("amount", map.getrAmt());
					body.put("packagetype", oneData.get("PACKAGETYPE").toString());
					body.put("packagemitem", oneData.get("PACKAGEMITEM").toString());
					int gift = 0;
					String gift_str = "N";
					try 
					{
						gift = Integer.parseInt(oneData.get("PACKAGEMITEM").toString());				
					} catch (Exception e) {				
					}
					if(gift==1)
					{
						gift_str = "Y";						
					}
					
					body.put("gift", gift_str);					
					request_detail.put(body);
					
					break;
				}
		
			}
		
			if(!isExist)
			{
				String ss ="没有匹配到数据库中该订单对应的商品资料！"+ "传入的退货项次item="+item+" 条码pluBarcode="+pluBarcode+" 退货数量rQty="+rQty+" ";
				HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退货】异常："+ss+" 单号OrderNO="+orderNO);
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,ss);
			}			
			isNeedUpdate = true;
		}
		
		if(isNeedUpdate)
		{
			StringBuilder errorMsg = new StringBuilder();
			//添加部分退单单身为负数
			//this.AddReturnGoodDetail(req, orderShop, errorMsg);
			//添加部分退款付款档为负数
			this.AddReturnPay(req, orderShop,pay_detail, errorMsg);
			
			//这里判断下 需要不需要调用ERP
			String isRequestErp = PosPub.getPARA_SMS(this.dao, oEId, "", "OrgOrderOnline");//订单修改退订是否调用ERP接口
			HelpTools.writelog_waimai("门店订单部分退货是否调用ERP接口参数值： ERP是否检核参数OrgOrderOnline="+isRequestErp+" 单号orderNO="+orderNO);
			if(isRequestErp!=null&&isRequestErp.toUpperCase().equals("Y"))
			{
				boolean isUploadErp = IsUploadErp(oEId, loadDocType, orderNO);
				if(isUploadErp)
				{
					String erpServiceName = "orderrefund.update";
					HelpTools.writelog_waimai("门店订单部分退货是否调用ERP接口， 该订单【已上传】需要调用ERP接口("+erpServiceName+") 单号orderNO="+orderNO);
					
					header.put("request_detail", request_detail);
					header.put("pay_detail", pay_detail);//付款档
					request.put(header);

					parameter.put("request", request);
					std_data.put("parameter", parameter);
					payload.put("std_data", std_data);							
					String str = payload.toString();// 将json对象转换为字符串	
					HelpTools.writelog_waimai("调用ERP接口("+erpServiceName+")请求传入参数："+str+" 单号orderNO="+orderNO);
					String resbody="";
					try 
					{
						resbody=HttpSend.Send(str, erpServiceName, oEId, oShopId,oShopId,orderNO);

						HelpTools.writelog_waimai("调用ERP接口("+erpServiceName+")返回参数："+resbody+" 单号orderNO="+orderNO);
						String ss ="";
						if(Check.Null(resbody) || resbody.isEmpty() )
						{
							ss ="调用ERP接口"+erpServiceName+"返回为空！";
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,ss);
						}
						JSONObject jsonres = new JSONObject(resbody);
						JSONObject std_data_res = jsonres.getJSONObject("std_data");
						JSONObject execution_res = std_data_res.getJSONObject("execution");

						String code = execution_res.get("code").toString();
						
						String description ="";
						if  (!execution_res.isNull("description") )
						{
						     description = execution_res.get("description").toString();
						}
						
						
						if(code.equals("0"))
						{
							try
							{/*				
								 DCP_OrderStatusLogCreateReq req_log = new DCP_OrderStatusLogCreateReq();
								 req_log.setDatas(new ArrayList<DCP_OrderStatusLogCreateReq.level1Elm>());
								 
								 //region订单状态
								 DCP_OrderStatusLogCreateReq.level1Elm onelv1 = req_log.new level1Elm();
								 onelv1.setCallback_status("0");
								 onelv1.setLoadDocType(loadDocType);
								 
								 onelv1.setNeed_callback("N");
								 onelv1.setNeed_notify("N");								
								 onelv1.setoEId(oEId);								 														 
								 onelv1.setO_opName(opName);
								 onelv1.setO_opNO(opNO);
								 onelv1.setO_organizationNO(oShopId);
								 onelv1.setoShopId(oShopId);								 
								 onelv1.setOrderNO(orderNO);								 
								 String statusType = "4";//
								 String updateStaus = "9";//								 
								 onelv1.setStatusType(statusType);				 					
								 onelv1.setStatus(updateStaus);
								 StringBuilder statusTypeNameObj = new StringBuilder();
								 String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
								 String statusTypeName = statusTypeNameObj.toString();
								 onelv1.setStatusTypeName(statusTypeName);
								 onelv1.setStatusName(statusName);
								 
								 String memo = "";
								 memo += statusTypeName+"-->" + statusName+"(调用ERP部分退单成功)";			 
								 onelv1.setMemo(memo);							 
								 String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
								 onelv1.setUpdate_time(updateDatetime);
								 
								 
								 req_log.getDatas().add(onelv1);
								 		  	
							   String req_log_json ="";
							   try
							   {
							  	 ParseJson pj = new ParseJson();
							  	 req_log_json = pj.beanToJson(req_log);
							  	 pj=null;
							   }
							   catch(Exception e)
							   {
							  	 
							   }			   			   			  	
						  	 StringBuilder errorMessage2 = new StringBuilder();
						  	 boolean nRet = HelpTools.InsertOrderStatusLog(StaticInfo.dao, req_log_json, errorMessage2);						
							*/}
							catch (Exception  e)
							{
								
							}
						}
						else
						{
							ss ="ERP返回错误信息:" + code + "," + description;
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,ss);
						}
						
						
					} 
					catch (Exception e) 
					{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,e.getMessage());
					}
				}
				else
				{
					HelpTools.writelog_waimai("门店订单部分退货是否调用ERP接口， 该订单还【未上传】无须调用ERP接口,单号orderNO="+orderNO);
				}
			}
			
			//执行SQL
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退货】sql语句组装完成，执行sql【开始】"+" 单号OrderNO="+orderNO);
			try 
			{
		
				this.doExecuteDataToDB();
				HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退货】sql语句组装完成，执行sql【成功】"+" 单号OrderNO="+orderNO);
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


					String o_opName = opName;

					onelv1.setO_opName(o_opName);
					onelv1.setO_opNO(opNO);
					
					onelv1.setO_organizationNO(orderShop);//下单门店
					onelv1.setoShopId(orderShop);
					onelv1.setOrderNO(orderNO);
					String statusType = "3";//退单状态
					String updateStaus = "10";//部分退单成功
					
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
				
				this.UpdateOrderCompleted(req, orderShop);
				
			} 
			catch (Exception e) 
			{
				HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退货】sql语句组装完成，执行sql【异常】："+e.getMessage()+" 单号OrderNO="+orderNO);
		
			}
			
		}
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderModify_PartToRefundReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderModify_PartToRefundReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderModify_PartToRefundReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderModify_PartToRefundReq req) throws Exception {
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
    
    
    if (isFail)
    {
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }   
    
    List<level2Good> goods =  req.getRequest().getGoods();
    if(goods==null||goods.isEmpty())
    {
    	errCt++;
	 	  errMsg.append("请求节点goods提货数量列表不能为空, ");
		  isFail = true; 
    	
    }
    if (isFail)
    {
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }  
    for (level2Good level2Good : goods) 
    {
    	if(Check.Null(level2Good.getItem()))
    	{
    		errCt++;
  	 	  errMsg.append("请求节点item退货项次不能为空, ");
  		  isFail = true; 
  		  throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    	}
    	if(Check.Null(level2Good.getrQty()))
    	{
    		errCt++;
  	 	  errMsg.append("请求节点rQty退货数量不能为空, ");
  		  isFail = true; 
  		  throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    	}
			float rQty = 0;
			try 
			{				
				rQty = Float.parseFloat(level2Good.getrQty());
				
			} catch (Exception e) {				
			}
			
			if(rQty==0)
			{
				errCt++;
		 	  errMsg.append("请求节点rQty退货数量不能为0, ");
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
	protected TypeToken<DCP_OrderModify_PartToRefundReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderModify_PartToRefundReq>(){};
	}

	@Override
	protected DCP_OrderModify_PartToRefundRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderModify_PartToRefundRes();
	}
	
	private void AddReturnPay(DCP_OrderModify_PartToRefundReq req,String shop_create,JSONArray pay_reqErpDetail,StringBuilder errorMsg) throws Exception
	{
		if(errorMsg==null)
		{
			errorMsg = new StringBuilder();
		}
		
		boolean nResult = false;
		
		String oShopId = req.getRequest().getShopId();
		String oEId = req.getRequest().geteId();
		String orderNO = req.getRequest().getOrderNo();
		String loadDocType = req.getRequest().getLoad_docType();
		
		if (req.getRequest().getPay()!=null&&req.getRequest().getPay().isEmpty()==false) 
		{
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退款付款方式增加】【新增退款付款档】开始，单号OrderNO="+orderNO);
			
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

				for (DCP_OrderModify_PartToRefundReq.level2Pay map : req.getRequest().getPay()) 
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
							payShop = oShopId;
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
						String isOrderpay = map.getIsOrderpay();
						String isOnlinePay = map.getIsOnlinePay();
						String refNO = map.getRefNO();
						String teriminalNO = map.getTeriminalNO();
						
						payMemo+="项次："+map.getItem() +" 付款方式："+map.getPayName() +" 金额 ："+map.getPay()+" 找零："+map.getChanged()+" 溢收："+map.getExtra()+"<br>";
						
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
							new DataValue(0-descore, Types.VARCHAR), // DESCORE  退款为负数
							new DataValue(0-payAmount, Types.VARCHAR), // PAY  退款为负数
							new DataValue(0-extra, Types.VARCHAR), // EXTRA  退款为负数
							new DataValue(0-changed, Types.VARCHAR), // CHANGED  退款为负数
							new DataValue(bdate, Types.VARCHAR), // BDATE
							new DataValue(isOrderpay, Types.VARCHAR), // ISORDERPAY
							new DataValue("100", Types.VARCHAR), // status
							new DataValue(isOnlinePay, Types.VARCHAR), // ISONLINEPAY
							new DataValue("", Types.VARCHAR), // ORDER_PAYCODE
							new DataValue("0", Types.VARCHAR), // RCPAY
							new DataValue(payShop, Types.VARCHAR), // SHOP_PAY
							new DataValue(payLoadDocType, Types.VARCHAR), // LOAD_DOCTYPE_PAY
							new DataValue(req.getRequestId(), Types.VARCHAR) //ORDERPAYID
						};
	
						InsBean ib2 = new InsBean("OC_ORDER_PAY", columns2);
						ib2.addValues(insValue2);
						this.addProcessData(new DataProcessBean(ib2));
						paycount++;
						
						
						JSONObject body_pay = new JSONObject(); // 存一笔单身
						body_pay.put("paycode", payCode);					
						body_pay.put("paycodeerp", payCodeerp);
						body_pay.put("payname", payName);
						body_pay.put("pay", 0-payAmount+"");
						body_pay.put("bdate", bdate);
						body_pay.put("thirdtransno", paySernum);
						body_pay.put("isorderpay", isOrderpay);						
						body_pay.put("isinvoice", "N");
						body_pay.put("invoicetype", "1");
						body_pay.put("invoiceno", "");						
						body_pay.put("loaddoctype", payLoadDocType);
						body_pay.put("shoppay", payShop);
						body_pay.put("changed", 0-changed+"");
						body_pay.put("extra", 0-extra+"");
						body_pay.put("cttype", ctType);
						body_pay.put("cardno", payCardNO);
						pay_reqErpDetail.put(body_pay);
												
					}
											
				HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退款付款方式增加】【新增退款付款档】添加结束，单号OrderNO="+orderNO);
				nResult = true;
						
				
		  }
			catch (Exception e)
			{
				HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退款付款方式增加】【新增退款付款档】异常："+e.getMessage()+"，单号OrderNO="+orderNO);			
				errorMsg.append("新增退款付款方式失败！异常："+e.getMessage());				
			}		
			
		}		
	}
	
	private void AddReturnGoodDetail(DCP_OrderModify_PartToRefundReq req,String shop_create,StringBuilder errorMsg) throws Exception
	{
		if(errorMsg==null)
		{
			errorMsg = new StringBuilder();
		}
		
		boolean nResult = false;
		
		String oShopId = req.getRequest().getShopId();
		String oEId = req.getRequest().geteId();
		String orderNO = req.getRequest().getOrderNo();
		String loadDocType = req.getRequest().getLoad_docType();
		try 
		{
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退款商品列表增加】【新增商品单身档】开始，单号OrderNO="+orderNO);
			
		// 这里可以把退订的支付方式存一下
			String sqlString = "select * from (select max(item) as MAXITEM from OC_order_detail where EID='" + oEId
				+ "'  and load_doctype='" + loadDocType + "' and orderno='" + orderNO + "' )";
			List<Map<String, Object>> listGoodList = this.doQueryData(sqlString, null);
			int goodItem = 1;
			if (listGoodList != null && !listGoodList.isEmpty()) 
			{
				int maxItem = 100;
				try {
					maxItem = Integer.parseInt(listGoodList.get(0).get("MAXITEM").toString());

        } catch (Exception e) {

        }
				
				goodItem = maxItem +1;
													
			}
			
			for (DCP_OrderModify_PartToRefundReq.level2Good map : req.getRequest().getGoods()) 
			{
				String rQty_str = map.getrQty();
				float rQty = 0;
				try {
					rQty = Float.parseFloat(rQty_str);

        } catch (Exception e) {

        }
				
				String rAmt_str = map.getrAmt();
				float rAmt = 0;
				try {
					rAmt = Float.parseFloat(rAmt_str);

        } catch (Exception e) {

        }
				
				String pluName = map.getPluName();
				String specName = map.getSpecName();
				String attrName = map.getAttrName();
				String unit = map.getUnit();
				if (pluName != null && pluName.length() > 120)
				{
					pluName = pluName.substring(0, 120);
				}
				if (specName != null && specName.length() > 120)
				{
					specName = specName.substring(0, 120);//数据库最长120
				}
				if (attrName != null && attrName.length() > 120)
				{
					attrName = attrName.substring(0, 120);//数据库最长120
				}
				if (unit != null && unit.length() > 10)
				{
					unit = unit.substring(0, 10);//数据库最长120
				}
				
				
				String[] columns2 = {"EID","ORGANIZATIONNO","SHOPID","ORDERNO","ITEM","LOAD_DOCTYPE",
						"PLUNO","PLUBARCODE","PLUNAME","SPECNAME","ATTRNAME","UNIT",
						"PRICE","QTY","GOODSGROUP","DISC","BOXNUM","BOXPRICE","AMT","ISMEMO","SKUID","PICKQTY","RQTY","STATUS","RCQTY","PACKAGETYPE","PACKAGEMITEM","TOPPINGTYPE","TOPPINGMITEM",
						"COUPONTYPE","COUPONCODE","SHOPQTY"};
				DataValue[] insValue2 = null;

				insValue2 = new DataValue[]{
						new DataValue(oEId, Types.VARCHAR),
						new DataValue(shop_create, Types.VARCHAR),//组织编号=门店编号
						new DataValue(shop_create, Types.VARCHAR),//映射后的门店		
						//new DataValue(orderid, Types.VARCHAR),//
						new DataValue(orderNO, Types.VARCHAR),//美团 饿了么 订单ID 唯一				
						new DataValue(goodItem, Types.INTEGER),//项次
						new DataValue(loadDocType, Types.VARCHAR),//1.饿了么 2.美团外卖 3.微商城
						new DataValue(map.getPluBarcode(), Types.VARCHAR),//PLUNO
						new DataValue(map.getPluBarcode(), Types.VARCHAR),//PLUBARCODE
						new DataValue(pluName, Types.VARCHAR),//PLUNAME
						new DataValue(specName, Types.VARCHAR),//SPECNAME
						new DataValue(attrName, Types.VARCHAR),//ATTRNAME
						new DataValue(unit, Types.VARCHAR),//UNIT
						new DataValue(map.getrPrice(), Types.VARCHAR),//PRICE
						new DataValue(0-rQty, Types.VARCHAR),//QTY
						new DataValue("", Types.VARCHAR),//GOODSGROUP
						new DataValue("0", Types.VARCHAR),//DISC
						new DataValue("0", Types.VARCHAR),//BOXNUM
						new DataValue("0", Types.VARCHAR),//BOXPRICE
						new DataValue(0-rAmt, Types.VARCHAR),//AMT
						new DataValue("N", Types.VARCHAR),//ISMEMO
						new DataValue(map.getPluBarcode(), Types.VARCHAR),//SKUID
						new DataValue("0", Types.FLOAT),//PICKQTY
						new DataValue("0", Types.FLOAT),//RQTY
						new DataValue("100", Types.VARCHAR),//status
						new DataValue("0", Types.VARCHAR),//RCQTY
						new DataValue("", Types.VARCHAR),//PACKAGETYPE
						new DataValue("0", Types.INTEGER),//PACKAGEMITEM
						new DataValue("", Types.VARCHAR),//TOPPINGTYPE
						new DataValue("0", Types.INTEGER),//TOPPINGMITEM
						new DataValue("", Types.VARCHAR),//COUPONTYPE
						new DataValue("", Types.VARCHAR),//COUPONCODE
						new DataValue("0", Types.FLOAT)//SHOPQTY
				};

				InsBean ib2 = new InsBean("OC_ORDER_DETAIL", columns2);
				ib2.addValues(insValue2);
				this.addProcessData(new DataProcessBean(ib2));	
				goodItem++;
		
			}
			
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退款商品列表增加】【新增商品单身档】添加结束，单号OrderNO="+orderNO);
			
	
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退款商品列表增加】【新增商品单身档】异常："+e.getMessage()+"，单号OrderNO="+orderNO);			
			errorMsg.append("新增商品列表失败！异常："+e.getMessage());			
		}
		
	}
	
	/**
	 * 判断下是否全部退完，更新订单状态
	 * @param req 请求
	 * @param orderShop 下单门店
	 * @throws Exception
	 */
	private void UpdateOrderCompleted(DCP_OrderModify_PartToRefundReq req,String orderShop) throws Exception
	{
		try 
		{
			String oShopId = req.getRequest().getShopId();
			String oEId = req.getRequest().geteId();
			String orderNO = req.getRequest().getOrderNo();		
			String loadDocType = req.getRequest().getLoad_docType();
			StringBuffer sqlbuf = new StringBuffer("");
			sqlbuf.append("select * from (");
			sqlbuf.append("select sum(qty) as qty_tot,sum(pickqty) as pickqty_tot,sum(rqty) as rqty_tot  from OC_order_detail ");
			sqlbuf.append(" where qty>0 and EID='"+oEId+"'  and LOAD_DOCTYPE='"+ loadDocType+"' and orderno='"+orderNO+"'");
			sqlbuf.append(")");
			String sql = sqlbuf.toString();
			HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退货是否全部退完】【查询开始】，单号OrderNO="+orderNO+" 查询sql:"+sql);	
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
				
				//更新已完成，因为部分退，只能是有做过部分提货
				if(qty_tot-pickqty_tot-rqty_tot == 0)
				{
					String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					UptBean ub1 = null;	
					ub1 = new UptBean("OC_ORDER");
					
					ub1.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));
					//ub1.addUpdateValue("REFUNDSTATUS", new DataValue("6", Types.VARCHAR));
					ub1.addUpdateValue("UPDATE_TIME", new DataValue(updateDatetime, Types.VARCHAR));
					ub1.addUpdateValue("COMPLETE_DATETIME", new DataValue(updateDatetime, Types.VARCHAR));
					//condition
					ub1.addCondition("EID", new DataValue(oEId, Types.VARCHAR));
					ub1.addCondition("orderno", new DataValue(orderNO, Types.VARCHAR));
					ub1.addCondition("load_doctype", new DataValue(loadDocType, Types.VARCHAR));		
					this.addProcessData(new DataProcessBean(ub1));
					
					StringBuilder memoMsg = new StringBuilder();
					StringBuilder errorMsg = new StringBuilder();
				
					this.doExecuteDataToDB();
					HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退货是否全部退完】【全部退完】更新单据状态已完成，成功，单号OrderNO="+orderNO);
				
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
						String statusType = "1";//订单状态
						String updateStaus = "11";//已退单
						
						onelv1.setStatusType(statusType);				 					
						onelv1.setStatus(updateStaus);
						StringBuilder statusTypeNameObj = new StringBuilder();
						String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus,statusTypeNameObj);				 			 
						String statusTypeName = statusTypeNameObj.toString();
						onelv1.setStatusTypeName(statusTypeName);
						onelv1.setStatusName(statusName);

						String memo = "";
						memo += statusTypeName+"-->" + statusName+"(部分退货全部退完)<br>";								
						onelv1.setMemo(memo);
						
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
							HelpTools.writelog_waimai("【写表OC_orderStatuslog保存成功】【部分退货全部退完更新已退单】"+" 订单号orderNO:"+orderNO);
						}
						else
						{			  		 
							HelpTools.writelog_waimai("【写表OC_orderStatuslog异常】【部分退货全部退完更新已退单】"+errorMessage.toString()+" 订单号orderNO:"+orderNO);
						}
						this.pData.clear();
						

					*/}
					catch (Exception  e)
					{
						HelpTools.writelog_waimai("【写表OC_orderStatuslog异常】【部分退货全部退完更新已完成】 异常报错 "+e.toString()+" 订单号orderNO:"+orderNO);
					}			
					//endregion
					
				}
				else
				{
					HelpTools.writelog_waimai("【第三方调用DCP_OrderModify_PartToRefund接口，部分退货是否全部退完】【没有全部退完】无需更新单据状态，单号OrderNO="+orderNO);
				}			
				
			}
		
	
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
	
		}
		
	}
	
	
	/**
	 * 判断下有没有上传给ERP
	 * @param eId
	 * @param loadDocType
	 * @param orderNo
	 * @return
	 * @throws Exception
	 */
	private boolean IsUploadErp(String eId,String loadDocType,String orderNo) throws Exception
	{
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from OC_order");
	  sqlbuf.append(" where EID='"+eId+"'  and LOAD_DOCTYPE='"+ loadDocType+"' and orderno='"+orderNo+"'");
	  
	  try 
	  {
	  	List<Map<String, Object>> getQDataDetail=this.doQueryData(sqlbuf.toString(),null);
			if(getQDataDetail!=null&&getQDataDetail.isEmpty()==false)
			{
				String process_status = getQDataDetail.get(0).get("PROCESS_STATUS").toString();
				if (process_status.equals("Y")) 
				{
					return true;						
				}
			
			}			
	  } 
	  catch (Exception e) 
	  {
	
	  }
	  			
		return false;
	}
	

}
