package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderCheckManualNo_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderCheckManualNo_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderCheckManualNo_Open extends SPosAdvanceService<DCP_OrderCheckManualNo_OpenReq,DCP_OrderCheckManualNo_OpenRes>
{

	@Override
	protected void processDUID(DCP_OrderCheckManualNo_OpenReq req, DCP_OrderCheckManualNo_OpenRes res) throws Exception
	{
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String sDate=df.format(cal.getTime());

		//7天之内重复就行
		String sBeginDate=PosPub.GetStringDate(sDate, -7);		

		String eId=req.getRequest().geteId();
		String ManualNo=req.getRequest().getManualNo();

		String sqlorder="select a.CREATE_DATETIME,a.MANUALNO from dcp_order a where a.eid = '"+eId+"' and a.create_datetime>='"+sBeginDate+"000000000' and a.create_datetime<='"+sDate+"235959999' and a.MANUALNO='"+ManualNo+"' ";
		List<Map<String , Object>> getDataOrder=this.doQueryData(sqlorder, null);
		if (getDataOrder==null || getDataOrder.isEmpty())
		{
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("手工单号可用");			
		}
		else 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("手工单号不可用");	
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderCheckManualNo_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderCheckManualNo_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderCheckManualNo_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderCheckManualNo_OpenReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (Check.Null(req.getRequest().geteId()))
		{
			isFail = true;
			errMsg.append("企业编码eId不能为空 ");
		}
		if (Check.Null(req.getRequest().getManualNo()))
		{
			isFail = true;
			errMsg.append("手工单号ManualNo不能为空 ");
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderCheckManualNo_OpenReq> getRequestType()
	{
		return new TypeToken<DCP_OrderCheckManualNo_OpenReq>(){};
	}

	@Override
	protected DCP_OrderCheckManualNo_OpenRes getResponseType()
	{
		return new DCP_OrderCheckManualNo_OpenRes();
	}

}
