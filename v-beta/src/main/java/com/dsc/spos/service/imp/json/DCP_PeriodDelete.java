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
import com.dsc.spos.json.cust.req.DCP_PeriodDeleteReq;
import com.dsc.spos.json.cust.req.DCP_PeriodDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PeriodDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PeriodDelete extends SPosAdvanceService<DCP_PeriodDeleteReq, DCP_PeriodDeleteRes> {

	@Override
	protected void processDUID(DCP_PeriodDeleteReq req, DCP_PeriodDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			String eId= req.geteId();		
			String sql = "";
			for (level1Elm par : req.getRequest().getPeriodList())
			{
				
				String periodNo = par.getPeriodNo();	
				sql = "";
				sql = "select status from DCP_PERIOD "
					+ "where status='-1' and eid='"+eId+"' and PERIODNO='"+periodNo+"' ";
				List<Map<String, Object>> getData = this.doQueryData(sql, null);
				if(getData==null||getData.isEmpty())
				{
					continue;
				}
				
				
				DelBean db1 = new DelBean("DCP_PERIOD");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));				
				db1.addCondition("PERIODNO", new DataValue(periodNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				db1 = new DelBean("DCP_PERIOD_LANG");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));				
				db1.addCondition("PERIODNO", new DataValue(periodNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
								
				db1 = new UptBean("DCP_PERIOD_RANGE");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));				
				db1.addCondition("PERIODNO", new DataValue(periodNo, Types.VARCHAR));				
				this.addProcessData(new DataProcessBean(db1));
				
				db1 = new UptBean("DCP_PERIOD_TIME");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));				
				db1.addCondition("PERIODNO", new DataValue(periodNo, Types.VARCHAR));				
				this.addProcessData(new DataProcessBean(db1));
			}

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("服务执行异常:"+e.getMessage());
		}
		
		
		
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PeriodDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PeriodDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PeriodDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PeriodDeleteReq req) throws Exception {
	// TODO Auto-generated method stub

	boolean isFail = false;
  StringBuffer errMsg = new StringBuffer("");

  if(req.getRequest()==null)
  {
  	errMsg.append("request不能为空 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }
  
  if (req.getRequest().getPeriodList()==null) 
  {
  	errMsg.append("编码不能为空 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	}
  
  
  for (level1Elm par : req.getRequest().getPeriodList())
	{ 	
  	String payType = par.getPeriodNo();    
    if(Check.Null(payType)){
     	errMsg.append("编码不能为空值 ，");
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
	protected TypeToken<DCP_PeriodDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_PeriodDeleteReq>(){};
	}

	@Override
	protected DCP_PeriodDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_PeriodDeleteRes();
	}

}
