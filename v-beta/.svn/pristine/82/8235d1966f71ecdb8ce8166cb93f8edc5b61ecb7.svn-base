package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_AttributionDeleteReq;
import com.dsc.spos.json.cust.req.DCP_AttributionDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_AttributionDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_AttributionDelete extends SPosAdvanceService<DCP_AttributionDeleteReq,DCP_AttributionDeleteRes>{

	@Override
	protected void processDUID(DCP_AttributionDeleteReq req, DCP_AttributionDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			
			String eId = req.geteId();
			for (level1Elm par : req.getRequest().getAttrIdList()) 
			{
				String attrId = par.getAttrId();
				DelBean db1 = new DelBean("DCP_ATTRIBUTION");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("ATTRID", new DataValue(attrId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				DelBean db2 = new DelBean("DCP_ATTRIBUTION_LANG");
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db2.addCondition("ATTRID", new DataValue(attrId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2));
				
				DelBean db3 = new DelBean("DCP_ATTRGROUP_DETAIL");//属性分组明细
				db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db3.addCondition("ATTRID", new DataValue(attrId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db3));
				
				DelBean db4 = new DelBean("DCP_ATTRIBUTION_VALUE");//属性规格
				db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db4.addCondition("ATTRID", new DataValue(attrId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db4));
				
				DelBean db5 = new DelBean("DCP_ATTRIBUTION_VALUE_LANG");//属性规格多语言
				db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db5.addCondition("ATTRID", new DataValue(attrId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db5));
		
		
			}
			
			
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");	
		
		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("服务执行异常:"+e.getMessage());	

		}
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_AttributionDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_AttributionDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_AttributionDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_AttributionDeleteReq req) throws Exception {
		
  boolean isFail = false;
  StringBuffer errMsg = new StringBuffer("");

  if(req.getRequest()==null)
  {
  	errMsg.append("requset不能为空值 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }
  
  List<level1Elm> parList = req.getRequest().getAttrIdList();
  if(parList==null||parList.isEmpty())
  {
  	errMsg.append("属性编码不能为空值 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }
    
  for (level1Elm par : req.getRequest().getAttrIdList()) 
	{
		String attrId = par.getAttrId();
		if(Check.Null(attrId)){
	   	errMsg.append("属性编码不能为空值 ");
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
	protected TypeToken<DCP_AttributionDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_AttributionDeleteReq>(){};
	}

	@Override
	protected DCP_AttributionDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_AttributionDeleteRes() ;
	}

}
