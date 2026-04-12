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
import com.dsc.spos.json.cust.req.DCP_BookingTimeCreateReq;
import com.dsc.spos.json.cust.req.DCP_BookingTimeCreateReq.range;
import com.dsc.spos.json.cust.res.DCP_BookingTimeCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_BookingTimeCreate extends SPosAdvanceService<DCP_BookingTimeCreateReq, DCP_BookingTimeCreateRes> {

	@Override
	protected void processDUID(DCP_BookingTimeCreateReq req, DCP_BookingTimeCreateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId= req.geteId();
		
		
		//必传字段
		String bookingTimeNo = req.getRequest().getBookingTimeNo();
		String bookingTime = req.getRequest().getBookingTime();
		
		String status = req.getRequest().getStatus();
		String createopid = req.getOpNO();
		String createopname = req.getOpName();
		 
		
		String restrictShop = req.getRequest().getRestrictShop();//适用门店：0-所有门店1-指定门店	
	
		String memo = req.getRequest().getMemo();		
		List<DCP_BookingTimeCreateReq.range> rangeList = req.getRequest().getRangeList();	
	
		
		if (this.isExist(req)) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "该时段编码："+bookingTimeNo+"已存在！");
		}
		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		String[] columns_class ={"EID","BOOKTIMENO","BOOKTIME","MEMO","STATUS","RESTRICTSHOP",
				"CREATEOPID","CREATEOPNAME","CREATETIME"
		};
		
		DataValue[] insValue_class = new DataValue[] 
				{
					new DataValue(eId, Types.VARCHAR),						
					new DataValue(bookingTimeNo, Types.VARCHAR),
					new DataValue(bookingTime, Types.VARCHAR),				
					new DataValue(memo, Types.VARCHAR),
					new DataValue(status, Types.VARCHAR),
					new DataValue(restrictShop ,Types.VARCHAR),										
					new DataValue(createopid ,Types.VARCHAR),
					new DataValue(createopname ,Types.VARCHAR),
					new DataValue(lastmoditime , Types.DATE) 						
				};
		
		InsBean ib_class = new InsBean("DCP_BOOKINGTIME", columns_class);
		ib_class.addValues(insValue_class);
		this.addProcessData(new DataProcessBean(ib_class)); 
		
		
					
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
	protected List<InsBean> prepareInsertData(DCP_BookingTimeCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_BookingTimeCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_BookingTimeCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_BookingTimeCreateReq req) throws Exception {
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
	
		//List<DCP_BookingTimeCreateReq.range> rangeList = req.getRequest().getRangeList();	
		
		
		
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
	protected TypeToken<DCP_BookingTimeCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_BookingTimeCreateReq>(){};
	}

	@Override
	protected DCP_BookingTimeCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_BookingTimeCreateRes();
	}
	
	private Boolean isExist(DCP_BookingTimeCreateReq req) throws Exception
	{
		boolean nRet = false;
		
		String sql = " select * from DCP_BOOKINGTIME where eid='"+req.geteId()+"' and BOOKTIMENO='"+req.getRequest().getBookingTimeNo()+"' ";
		
		List<Map<String, Object>> mapList = this.doQueryData(sql, null);
		if (mapList!=null&&mapList.isEmpty()==false)
		{
			nRet = true;	
		}
		
		return nRet;
		
		
	}

}
