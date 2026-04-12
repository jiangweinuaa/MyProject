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
import com.dsc.spos.json.cust.req.DCP_PayClassDeleteReq;
import com.dsc.spos.json.cust.req.DCP_PayClassDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PayClassDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PayClassDelete extends SPosAdvanceService<DCP_PayClassDeleteReq, DCP_PayClassDeleteRes> {

	@Override
	protected void processDUID(DCP_PayClassDeleteReq req, DCP_PayClassDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			String eId= req.geteId();		
			String sql = "";
			for (level1Elm par : req.getRequest().getClassList())
			{
				
				String classNo = par.getClassNo();	
				sql = "";
				sql = "select status from DCP_PAYCLASS "
					+ "where status='-1' and eid='"+eId+"' and CLASSNO='"+classNo+"' ";
				List<Map<String, Object>> getData = this.doQueryData(sql, null);
				if(getData==null||getData.isEmpty())
				{
					continue;
				}
				
				
				DelBean db1 = new DelBean("DCP_PAYCLASS_LANG");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));				
				db1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
								
				db1 = new UptBean("DCP_PAYCLASS");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));				
				db1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));				
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
	protected List<InsBean> prepareInsertData(DCP_PayClassDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayClassDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayClassDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayClassDeleteReq req) throws Exception {
	// TODO Auto-generated method stub

	boolean isFail = false;
  StringBuffer errMsg = new StringBuffer("");

  if(req.getRequest()==null)
  {
  	errMsg.append("request不能为空 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }
  
  if (req.getRequest().getClassList()==null) 
  {
  	errMsg.append("编码不能为空 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	}
  
  
  for (level1Elm par : req.getRequest().getClassList())
	{ 	
  	String classNo = par.getClassNo();    
    if(Check.Null(classNo)){
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
	protected TypeToken<DCP_PayClassDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_PayClassDeleteReq>(){};
	}

	@Override
	protected DCP_PayClassDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_PayClassDeleteRes();
	}

}
