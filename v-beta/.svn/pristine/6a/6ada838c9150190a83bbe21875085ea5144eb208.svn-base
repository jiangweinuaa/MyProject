package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_AttributionValueDeleteReq;
import com.dsc.spos.json.cust.req.DCP_AttributionValueDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_AttributionValueDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_AttributionValueDelete extends SPosAdvanceService<DCP_AttributionValueDeleteReq,DCP_AttributionValueDeleteRes>{

	@Override
	protected void processDUID(DCP_AttributionValueDeleteReq req, DCP_AttributionValueDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			
			
			String eId = req.geteId();
			for (level1Elm par : req.getRequest().getAttrValueIdList()) 
			{
				String attrValueId = par.getAttrValueId();
				String attrId = par.getAttrId();
				
				DelBean db1 = new DelBean("DCP_ATTRIBUTION_VALUE");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("ATTRID", new DataValue(attrId, Types.VARCHAR));
				db1.addCondition("ATTRVALUEID", new DataValue(attrValueId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				DelBean db2 = new DelBean("DCP_ATTRIBUTION_VALUE_LANG");
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db2.addCondition("ATTRID", new DataValue(attrId, Types.VARCHAR));
				db2.addCondition("ATTRVALUEID", new DataValue(attrValueId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2));
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
	protected List<InsBean> prepareInsertData(DCP_AttributionValueDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_AttributionValueDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_AttributionValueDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_AttributionValueDeleteReq req) throws Exception {
		
  boolean isFail = false;
  StringBuffer errMsg = new StringBuffer("");

  if(req.getRequest()==null)
  {
  	errMsg.append("requset不能为空值 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }
  
  if(req.getRequest().getAttrValueIdList()==null||req.getRequest().getAttrValueIdList().isEmpty())
  {
  	errMsg.append("编码不能为空 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }
	for (level1Elm par : req.getRequest().getAttrValueIdList())
	{
		String attrId = par.getAttrId();
		String attrValueId = par.getAttrValueId();
		if (Check.Null(attrId))
		{
			errMsg.append("属性编码不能为空值 ");
			isFail = true;
		}
		if (Check.Null(attrValueId))
		{
			errMsg.append("规格编码不能为空值 ");
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
	protected TypeToken<DCP_AttributionValueDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_AttributionValueDeleteReq>(){};
	}

	@Override
	protected DCP_AttributionValueDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_AttributionValueDeleteRes() ;
	}

}
