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
import com.dsc.spos.json.cust.req.DCP_OrderDeliveryNoCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderDeliveryNoCreateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;
import java.util.Calendar;
public class DCP_OrderDeliveryNoCreate extends SPosAdvanceService<DCP_OrderDeliveryNoCreateReq,DCP_OrderDeliveryNoCreateRes>
{

	@Override
	protected void processDUID(DCP_OrderDeliveryNoCreateReq req, DCP_OrderDeliveryNoCreateRes res) throws Exception
	{
		try
		{
			ParseJson pj = new ParseJson();
			HelpTools.writelog_waimai("【第三方调用DCP_OrderDeliveryNoCreate接口】传入请求:"+pj.beanToJson(req));
			pj = null;
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		
		
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String eId=req.geteId();

		List<DCP_OrderDeliveryNoCreateReq.Order> orderList=req.getRequest().getOrderList();

		res.setDatas(res.new level1Elm());
		res.getDatas().setErrorOrderList(new ArrayList<DCP_OrderDeliveryNoCreateRes.Order>());

		boolean bOk=true;

		for (DCP_OrderDeliveryNoCreateReq.Order order : orderList)
		{
			this.pData.clear();
			String orderNo=order.getOrderNo();
			String deliveryType= order.getDeliveryType();
			//手动录入物流单号就不用取了
			String deliveryNo=order.getDeliveryNo();

			//验证订单的必填栏位值
			String sqlorder="select a.* from dcp_order a where a.eid='"+eId+"' and a.orderno='"+orderNo+"' ";
			List<Map<String , Object>> getDataOrder=this.doQueryData(sqlorder, null);

			String errorDesc="";

			if (getDataOrder==null || getDataOrder.isEmpty())
			{
				errorDesc="查不到订单号";
				DCP_OrderDeliveryNoCreateRes.Order errOrder=res.new Order();
				errOrder.setOrderNo(orderNo);
				errOrder.setErrorDesc(errorDesc);
				res.getDatas().getErrorOrderList().add(errOrder);		

				//
				bOk=false;
				continue;
			}

			//订单资料
			String address=getDataOrder.get(0).get("ADDRESS").toString();
			String tot_amt=getDataOrder.get(0).get("TOT_AMT").toString();
			String tot_qty=getDataOrder.get(0).get("TOT_QTY").toString();
			String payamt=getDataOrder.get(0).get("PAYAMT").toString();
			String getman=getDataOrder.get(0).get("GETMAN").toString();
			String getmantel=getDataOrder.get(0).get("GETMANTEL").toString();
			String isAutoDelivery = getDataOrder.get(0).getOrDefault("AUTODELIVERY","").toString();
			String deliveryNo_DB = getDataOrder.get(0).getOrDefault("DELIVERYNO","").toString();
			String deliveryStatus_DB = getDataOrder.get(0).getOrDefault("DELIVERYSTATUS","").toString();
			String deliveryType_DB = getDataOrder.get(0).getOrDefault("DELIVERYTYPE","").toString();
			
			String loadDocType = getDataOrder.get(0).getOrDefault("LOADDOCTYPE","").toString();
			String channelId = getDataOrder.get(0).getOrDefault("CHANNELID","").toString();
			String loadDocBillType = getDataOrder.get(0).getOrDefault("LOADDOCBILLTYPE","").toString();
			String loadDocOrderNo = getDataOrder.get(0).getOrDefault("LOADDOCORDERNO","").toString();

            String delId = getDataOrder.get(0).getOrDefault("DELID", "").toString();
            String delName = getDataOrder.get(0).getOrDefault("DELNAME", "").toString();
            String delTelephone = getDataOrder.get(0).getOrDefault("DELTELEPHONE", "").toString();
			if ("Y".equals(isAutoDelivery))
			{
				errorDesc="自动发快递，无须手动录入";
				DCP_OrderDeliveryNoCreateRes.Order errOrder=res.new Order();
				errOrder.setOrderNo(orderNo);
				errOrder.setErrorDesc(errorDesc);
				res.getDatas().getErrorOrderList().add(errOrder);	

				//
				bOk=false;
				continue;
			}
			//是否手工录入的物流单号
			String deliveryHandinput="Y";
			
			//传入的物流单号和物流类型都为空,再取值
			if (Check.Null(deliveryNo)&&Check.Null(deliveryType))
			{
                errorDesc="物流类型和物流单号至少有一个不为空，";
                DCP_OrderDeliveryNoCreateRes.Order errOrder=res.new Order();
                errOrder.setOrderNo(orderNo);
                errOrder.setErrorDesc(errorDesc);
                res.getDatas().getErrorOrderList().add(errOrder);
                bOk=false;
                continue;
			}
			else 
			{
				deliveryHandinput="Y";
			}

			//只要物流单号不为空就认为成功
			try
			{
				//物流状态deliveryStatus改为0已下单
				//更新物流单号DeliveryNo
				UptBean ub1 = new UptBean("DCP_ORDER");
				//String deliveryStatus_update = "0";
				//ub1.addUpdateValue("DELIVERYSTATUS", new DataValue(deliveryStatus_update,Types.VARCHAR));
                StringBuffer memo = new StringBuffer("");
                if (!Check.Null(deliveryNo))
                {
                    ub1.addUpdateValue("DELIVERYNO", new DataValue(deliveryNo,Types.VARCHAR));
                    if(!deliveryNo.equals(deliveryNo_DB))
                    {
                        memo.append( "<br>物流单号:"+deliveryNo_DB+"-->"+deliveryNo);
                    }
                }
                if (!Check.Null(deliveryType))
                {
                    ub1.addUpdateValue("DELIVERYTYPE", new DataValue(deliveryType,Types.VARCHAR));
                    if(!deliveryType.equals(deliveryType_DB))
                    {
                        memo.append( "<br>物流类型:"+deliveryType_DB+"-->"+deliveryType);
                    }
                }
                if (order.getDelId() != null)
                {
                    if(order.getDelId().equals(delId)==false)
                    {
                        ub1.addUpdateValue("DELID", new DataValue(order.getDelId(), Types.VARCHAR));
                        memo.append( "<br>配送人ID:" + delId + "-->" + order.getDelId());
                    }

                }
                if (order.getDelName() != null)
                {
                    if(order.getDelName().equals(delName)==false)
                    {
                        ub1.addUpdateValue("DELNAME", new DataValue(order.getDelName(), Types.VARCHAR));
                        memo.append( "<br>配送人:" + delName + "-->" + order.getDelName());
                    }

                }
                if (order.getDelTelephone() != null)
                {
                    if(order.getDelTelephone().equals(delTelephone)==false)
                    {
                        ub1.addUpdateValue("DELTELEPHONE", new DataValue(order.getDelTelephone(), Types.VARCHAR));
                        memo.append( "<br>配送人电话:" + delTelephone + "-->" + order.getDelTelephone());
                    }

                }

				ub1.addUpdateValue("DELIVERYHANDINPUT", new DataValue(deliveryHandinput,Types.VARCHAR));
		        ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
	            ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));
				//
				this.doExecuteDataToDB();
				
				// region 写下日志
				try
				{

					List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
					orderStatusLog onelv1 = new orderStatusLog();

					onelv1.setLoadDocType(loadDocType);
					onelv1.setChannelId(channelId);

					onelv1.setNeed_callback("N");
					onelv1.setNeed_notify("N");

					onelv1.seteId(eId);

					String opNO = req.getOpNO() == null ? "" : req.getOpNO();

					String o_opName = req.getOpName() == null ? "" : req.getOpName();

					onelv1.setOpNo(opNO);
					onelv1.setOpName(o_opName);
					onelv1.setOrderNo(orderNo);
					onelv1.setLoadDocBillType(loadDocBillType);
					onelv1.setLoadDocOrderNo(loadDocOrderNo);

					String statusType = "99";// 其他状态
					String updateStaus = "99";// 订单修改

					onelv1.setStatusType(statusType);
					onelv1.setStatus(updateStaus);
					StringBuilder statusTypeName = new StringBuilder();
					String statusName_log = "手动录入配送信息";//HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeName);
					String statusTypeName_log = "其他状态";// statusTypeName.toString();
					onelv1.setStatusTypeName(statusTypeName_log);
					onelv1.setStatusName(statusName_log);

					String memoStr = statusTypeName_log + "-->" + statusName_log;
					onelv1.setMemo(memoStr+memo.toString());
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

				} catch (Exception e)
				{
					HelpTools.writelog_waimai("【写表DCP_orderStatuslog异常】 异常报错 " + e.toString() + " 订单号orderNO:" + orderNo);
				}
				// endregion
			}
			catch (Exception e)
			{
				DCP_OrderDeliveryNoCreateRes.Order errOrder=res.new Order();
				errOrder.setOrderNo(orderNo);
				errOrder.setErrorDesc(e.getMessage());
				res.getDatas().getErrorOrderList().add(errOrder);	

				bOk=false;
				continue;
			}
		}

		res.setSuccess(bOk);
		res.setServiceStatus(bOk?"000":"100");
		res.setServiceDescription(bOk?"服务执行成功！":"服务执行失败！");
		return;
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderDeliveryNoCreateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderDeliveryNoCreateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderDeliveryNoCreateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderDeliveryNoCreateReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		List<DCP_OrderDeliveryNoCreateReq.Order> orderList=req.getRequest().getOrderList();

		if (orderList==null || orderList.size()==0)
		{
			isFail = true;
			errMsg.append("订单列表orderList不能为空 ");
		}

		for (DCP_OrderDeliveryNoCreateReq.Order order : orderList)
		{
			if (Check.Null(order.getOrderNo()))
			{
				isFail = true;
				errMsg.append("订单号不能为空 ");
			}
			if (Check.Null(order.getDeliveryType()))
			{
				isFail = true;
				errMsg.append("物流类型不能为空 ");
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderDeliveryNoCreateReq> getRequestType()
	{
		return new TypeToken<DCP_OrderDeliveryNoCreateReq>(){};
	}

	@Override
	protected DCP_OrderDeliveryNoCreateRes getResponseType()
	{
		return new DCP_OrderDeliveryNoCreateRes();
	}

}
