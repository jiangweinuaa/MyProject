package com.dsc.spos.service.imp.json;
import java.util.Calendar;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderPickUpGoodsUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrderPickUpGoodsUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderPickUpGoodsUpdate extends SPosAdvanceService<DCP_OrderPickUpGoodsUpdateReq,DCP_OrderPickUpGoodsUpdateRes>
{

	@Override
	protected void processDUID(DCP_OrderPickUpGoodsUpdateReq req, DCP_OrderPickUpGoodsUpdateRes res) throws Exception
	{
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String eId=req.geteId();

		List<DCP_OrderPickUpGoodsUpdateReq.order> orderList=req.getRequest().getOrderList();

		for (DCP_OrderPickUpGoodsUpdateReq.order order : orderList)
		{
			//更新为已打印
			UptBean ub1 = new UptBean("DCP_ORDER");
			ub1.addUpdateValue("PICKUPDOCPRINT", new DataValue("Y",Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("ORDERNO", new DataValue(order.getOrderNo(), Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

			/*
			//写订单日志
			orderStatusLog onelv1=new orderStatusLog();
			onelv1.setLoadDocType(loadDocType);
			onelv1.setLoadDocBillType(loadDocBillType);
			onelv1.setLoadDocOrderNo(loadDocOrderNo);
			onelv1.seteId(eId);
			onelv1.setOpName(o_opName);
			onelv1.setOpNo(opNO);				
			onelv1.setShopNo(shopNo);
			onelv1.setOrderNo(orderNo);
			onelv1.setMachShopNo(machShopNo);
			onelv1.setShippingShopNo(shippingShopNo);
			String statusType = "";
			String updateStaus = res_order.getStatus() ;
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

			//
			InsBean ib1=HelpTools.InsertOrderStatusLog(onelv1);			
			this.addProcessData(new DataProcessBean(ib1));
			*/
		}

		//
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderPickUpGoodsUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderPickUpGoodsUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderPickUpGoodsUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderPickUpGoodsUpdateReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		List<DCP_OrderPickUpGoodsUpdateReq.order> orderList=req.getRequest().getOrderList();

		if(orderList==null || orderList.size()==0)
		{
			errMsg.append("订单列表orderList不能为空值 ");
			isFail = true;
		}	

		for (DCP_OrderPickUpGoodsUpdateReq.order order : orderList)
		{
			if(Check.Null(order.getOrderNo()))
			{
				errMsg.append("订单号不能为空值 ");
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
	protected TypeToken<DCP_OrderPickUpGoodsUpdateReq> getRequestType()
	{
		return new TypeToken<DCP_OrderPickUpGoodsUpdateReq>() {};
	}

	@Override
	protected DCP_OrderPickUpGoodsUpdateRes getResponseType()
	{
		return new DCP_OrderPickUpGoodsUpdateRes();
	}



}
