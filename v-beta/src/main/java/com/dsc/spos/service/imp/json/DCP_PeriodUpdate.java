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
import com.dsc.spos.json.cust.req.DCP_PeriodUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PeriodUpdateReq.range;
import com.dsc.spos.json.cust.res.DCP_PeriodUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PeriodUpdate extends SPosAdvanceService<DCP_PeriodUpdateReq, DCP_PeriodUpdateRes> {

	@Override
	protected void processDUID(DCP_PeriodUpdateReq req, DCP_PeriodUpdateRes res) throws Exception {
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
		String periodNo = req.getRequest().getPeriodNo();
		String beginTime = req.getRequest().getBeginTime();
		String endTime = req.getRequest().getEndTime();
		String status = req.getRequest().getStatus();
		List<String> timeList = req.getRequest().getTimeList();
		 
		
		String restrictShop = req.getRequest().getRestrictShop();//适用门店：0-所有门店1-指定门店	
	
		String memo = req.getRequest().getMemo();		
		List<DCP_PeriodUpdateReq.range> rangeList = req.getRequest().getRangeList();	
		List<DCP_PeriodUpdateReq.periodName> langList = req.getRequest().getPeriodName_lang();
		
		
		
		
		DelBean	db1 = new DelBean("DCP_PERIOD_RANGE");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
		db1.addCondition("PERIODNO", new DataValue(periodNo, Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(db1));
		
		db1 = new DelBean("DCP_PERIOD_LANG");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
		db1.addCondition("PERIODNO", new DataValue(periodNo, Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(db1));
		
		db1 = new DelBean("DCP_PERIOD_TIME");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
		db1.addCondition("PERIODNO", new DataValue(periodNo, Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(db1));
		
		UptBean up1 = new UptBean("DCP_PERIOD");
		up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		up1.addCondition("PERIODNO", new DataValue(periodNo, Types.VARCHAR));
		
		up1.addUpdateValue("BEGINTIME", new DataValue(beginTime, Types.VARCHAR));
		up1.addUpdateValue("ENDTIME", new DataValue(endTime, Types.VARCHAR));
		up1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
		up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
		up1.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.VARCHAR));
		up1.addUpdateValue("LASTMODIOPID", new DataValue(createopid, Types.VARCHAR));
		up1.addUpdateValue("LASTMODIOPNAME", new DataValue(createopname, Types.VARCHAR));
		up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
		this.addProcessData(new DataProcessBean(up1));
		
		String[] columns_class_lang =
			{
					"EID",
					"PERIODNO",				
					"LANG_TYPE" ,
					"PERIODNAME",
				  "LASTMODITIME"
		
			};
		
		for (DCP_PeriodUpdateReq.periodName par : langList) 
		{			
			DataValue[] insValue1_lang = null;			
			insValue1_lang = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(periodNo, Types.VARCHAR),
					new DataValue(par.getLangType(), Types.VARCHAR),				
					new DataValue(par.getName(), Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)
				};
			
			InsBean ib1_lang = new InsBean("DCP_PERIOD_LANG", columns_class_lang);
			ib1_lang.addValues(insValue1_lang);
			this.addProcessData(new DataProcessBean(ib1_lang)); 
	
		}
					
		if(rangeList!=null&&rangeList.isEmpty()==false)
		{
			String[] columns_class_range =
				{
						"EID",
						"PERIODNO",					
						"SHOPID",
					  "LASTMODITIME"
			
				};
				
			for (range par : rangeList) 
			{	
				
				DataValue[] insValue1 = null;			
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(periodNo, Types.VARCHAR),								
						new DataValue(par.getShopId(), Types.VARCHAR),					
						new DataValue(lastmoditime, Types.DATE)
					};
				
				InsBean ib1 = new InsBean("DCP_PERIOD_RANGE", columns_class_range);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); 
				
			}
			
		}
		
		
		if(timeList!=null&&timeList.isEmpty()==false)
		{
			String[] columns_class_time =
				{
						"EID",
						"PERIODNO",					
						"TIME",
					  "LASTMODITIME"
			
				};
			
			for (String par : timeList) 
			{
				DataValue[] insValue1 = null;			
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(periodNo, Types.VARCHAR),								
						new DataValue(par, Types.VARCHAR),					
						new DataValue(lastmoditime, Types.DATE)
					};
				
				InsBean ib1 = new InsBean("DCP_PERIOD_TIME", columns_class_time);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));			
		
			}
		}
		
		
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PeriodUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PeriodUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PeriodUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PeriodUpdateReq req) throws Exception {
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
		String periodNo = req.getRequest().getPeriodNo();
		String beginTime = req.getRequest().getBeginTime();
		String endTime = req.getRequest().getEndTime();
		String status = req.getRequest().getStatus();
	 
		
		String restrictShop = req.getRequest().getRestrictShop();//适用门店：0-所有门店1-指定门店	
	
		//List<DCP_PeriodUpdateReq.range> rangeList = req.getRequest().getRangeList();	
		List<DCP_PeriodUpdateReq.periodName> langList = req.getRequest().getPeriodName_lang();
		if(langList==null||langList.isEmpty())
		{
			errMsg.append("多语言不能为空值 ");
	  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		if(Check.Null(periodNo)){
	   	errMsg.append("编码periodNo不能为空值， ");
	   	isFail = true;

	  }
	  if(Check.Null(beginTime)){
	   	errMsg.append("开始时间beginTime不能为空值， ");
	   	isFail = true;

	  }
	  if(Check.Null(endTime)){
	   	errMsg.append("截止时间endTime不能为空值， ");
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
	protected TypeToken<DCP_PeriodUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_PeriodUpdateReq>(){};
	}

	@Override
	protected DCP_PeriodUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_PeriodUpdateRes();
	}
	
	

}
