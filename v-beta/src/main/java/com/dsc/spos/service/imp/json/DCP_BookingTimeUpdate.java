package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BookingTimeUpdateReq;
import com.dsc.spos.json.cust.req.DCP_BookingTimeUpdateReq.range;
import com.dsc.spos.json.cust.res.DCP_BookingTimeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_BookingTimeUpdate extends SPosAdvanceService<DCP_BookingTimeUpdateReq, DCP_BookingTimeUpdateRes> {

	@Override
	protected void processDUID(DCP_BookingTimeUpdateReq req, DCP_BookingTimeUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId= req.geteId();
		String createopid = req.getOpNO();
		String createopname = req.getOpName();
		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		
		//必传字段
		String bookingTimeNo = req.getRequest().getBookingTimeNo();
		String bookingTime = req.getRequest().getBookingTime();
		
		String status = req.getRequest().getStatus();
		
		 
		
		String restrictShop = req.getRequest().getRestrictShop();//适用门店：0-所有门店1-指定门店	
	
		String memo = req.getRequest().getMemo();		
		List<DCP_BookingTimeUpdateReq.range> rangeList = req.getRequest().getRangeList();	
		
		
		
		
		
		DelBean	db1 = new DelBean("DCP_BOOKINGTIME_RANGE");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
		db1.addCondition("BOOKTIMENO", new DataValue(bookingTimeNo, Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(db1));
		
		
		UptBean up1 = new UptBean("DCP_BOOKINGTIME");
		up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		up1.addCondition("BOOKTIMENO", new DataValue(bookingTimeNo, Types.VARCHAR));
		
		up1.addUpdateValue("BOOKTIME", new DataValue(bookingTime, Types.VARCHAR));		
		up1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
		up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
		up1.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.VARCHAR));
		up1.addUpdateValue("LASTMODIOPID", new DataValue(createopid, Types.VARCHAR));
		up1.addUpdateValue("LASTMODIOPNAME", new DataValue(createopname, Types.VARCHAR));
		up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
		this.addProcessData(new DataProcessBean(up1));
		
	
					
		String[] columns_class_range =
			{
					"EID",
					"BOOKTIMENO",					
					"SHOPID",
				  "LASTMODITIME"
		
			};
			
		for (range par : rangeList) 
		{	
			
			DataValue[] insValue1 = null;			
			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(bookingTimeNo, Types.VARCHAR),								
					new DataValue(par.getShopId(), Types.VARCHAR),					
					new DataValue(lastmoditime, Types.DATE)
				};
			
			InsBean ib1 = new InsBean("DCP_BOOKINGTIME_RANGE", columns_class_range);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); 
			
		}
		
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_BookingTimeUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_BookingTimeUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_BookingTimeUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_BookingTimeUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
	  StringBuffer errMsg = new StringBuffer("");

	  if(req.getRequest()==null)
	  {
	  	errMsg.append("requset不能为空值 ");
	  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	  }
	
    //必传字段
	//必传字段
			String bookingTimeNo = req.getRequest().getBookingTimeNo();
			String bookingTime = req.getRequest().getBookingTime();
			
			String status = req.getRequest().getStatus();
	 
		
		String restrictShop = req.getRequest().getRestrictShop();//适用门店：0-所有门店1-指定门店	
	
		//List<DCP_BookingTimeUpdateReq.range> rangeList = req.getRequest().getRangeList();	
		
		
		
		if(Check.Null(bookingTimeNo)){
	   	errMsg.append("编码bookingTimeNo不能为空值， ");
	   	isFail = true;

	  }
	  if(Check.Null(bookingTime)){
	   	errMsg.append("时段名称bookingTime不能为空值， ");
	   	isFail = true;

	  }
	  
	  if(Check.Null(status)){
	   	errMsg.append("状态不能为空值， ");
	   	isFail = true;

	  }
	  
	  if(Check.Null(restrictShop)){
	   	errMsg.append("restrictShop不能为空值， ");
	   	isFail = true;

	  }
	
	  	  
		
	
	  if (isFail)
	  {
	  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	  }
	    
	   return isFail;
	}

	@Override
	protected TypeToken<DCP_BookingTimeUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_BookingTimeUpdateReq>(){};
	}

	@Override
	protected DCP_BookingTimeUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_BookingTimeUpdateRes();
	}
	
	

}
