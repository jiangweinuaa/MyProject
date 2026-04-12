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
import com.dsc.spos.json.cust.req.DCP_PeriodCreateReq;
import com.dsc.spos.json.cust.req.DCP_PeriodCreateReq.range;
import com.dsc.spos.json.cust.res.DCP_PeriodCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PeriodCreate extends SPosAdvanceService<DCP_PeriodCreateReq, DCP_PeriodCreateRes> {

	@Override
	protected void processDUID(DCP_PeriodCreateReq req, DCP_PeriodCreateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId= req.geteId();
		
		
		//必传字段
		String periodNo = req.getRequest().getPeriodNo();
		String beginTime = req.getRequest().getBeginTime();
		String endTime = req.getRequest().getEndTime();
		String status = req.getRequest().getStatus();
		String createopid = req.getOpNO();
		String createopname = req.getOpName();
		List<String> timeList = req.getRequest().getTimeList();
		 
		
		String restrictShop = req.getRequest().getRestrictShop();//适用门店：0-所有门店1-指定门店	
	
		String memo = req.getRequest().getMemo();		
		List<DCP_PeriodCreateReq.range> rangeList = req.getRequest().getRangeList();	
		List<DCP_PeriodCreateReq.periodName> langList = req.getRequest().getPeriodName_lang();
		
		if (this.isExist(req)) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "该时段编码："+periodNo+"已存在！");
		}
		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		String[] columns_class ={"EID","PERIODNO","BEGINTIME","ENDTIME" ,"MEMO","STATUS","RESTRICTSHOP",
				"CREATEOPID","CREATEOPNAME","CREATETIME"
		};
		
		DataValue[] insValue_class = new DataValue[] 
				{
					new DataValue(eId, Types.VARCHAR),						
					new DataValue(periodNo, Types.VARCHAR),
					new DataValue(beginTime, Types.VARCHAR),
					new DataValue(endTime,Types.VARCHAR),
					new DataValue(memo, Types.VARCHAR),
					new DataValue(status, Types.VARCHAR),
					new DataValue(restrictShop ,Types.VARCHAR),										
					new DataValue(createopid ,Types.VARCHAR),
					new DataValue(createopname ,Types.VARCHAR),
					new DataValue(lastmoditime , Types.DATE) 						
				};
		
		InsBean ib_class = new InsBean("DCP_PERIOD", columns_class);
		ib_class.addValues(insValue_class);
		this.addProcessData(new DataProcessBean(ib_class)); 
		
		String[] columns_class_lang =
			{
					"EID",
					"PERIODNO",				
					"LANG_TYPE" ,
					"PERIODNAME",
				  "LASTMODITIME"
		
			};
		
		for (DCP_PeriodCreateReq.periodName par : langList) 
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
	protected List<InsBean> prepareInsertData(DCP_PeriodCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PeriodCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PeriodCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PeriodCreateReq req) throws Exception {
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
	
		//List<DCP_PeriodCreateReq.range> rangeList = req.getRequest().getRangeList();	
		List<DCP_PeriodCreateReq.periodName> langList = req.getRequest().getPeriodName_lang();
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
	protected TypeToken<DCP_PeriodCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_PeriodCreateReq>(){};
	}

	@Override
	protected DCP_PeriodCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_PeriodCreateRes();
	}
	
	private Boolean isExist(DCP_PeriodCreateReq req) throws Exception
	{
		boolean nRet = false;
		
		String sql = " select * from DCP_PERIOD where eid='"+req.geteId()+"' and PERIODNO='"+req.getRequest().getPeriodNo()+"' ";
		
		List<Map<String, Object>> mapList = this.doQueryData(sql, null);
		if (mapList!=null&&mapList.isEmpty()==false)
		{
			nRet = true;	
		}
		
		return nRet;
		
		
	}

}
