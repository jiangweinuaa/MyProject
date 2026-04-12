package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_AttrGroupDeleteReq;
import com.dsc.spos.json.cust.req.DCP_AttrGroupDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_AttrGroupDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_AttrGroupDelete extends SPosAdvanceService<DCP_AttrGroupDeleteReq,DCP_AttrGroupDeleteRes>{

	@Override
	protected void processDUID(DCP_AttrGroupDeleteReq req, DCP_AttrGroupDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			
			String eId = req.geteId();
			for (level1Elm par : req.getRequest().getAttrGroupIdList())
			{
				String attrGroupId =par.getAttrGroupId();
				DelBean db1 = new DelBean("DCP_ATTRGROUP");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("ATTRGROUPID", new DataValue(attrGroupId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				DelBean db2 = new DelBean("DCP_ATTRGROUP_LANG");
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db2.addCondition("ATTRGROUPID", new DataValue(attrGroupId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2));
				
				DelBean db3 = new DelBean("DCP_ATTRGROUP_DETAIL");//属性分组明细
				db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db3.addCondition("ATTRGROUPID", new DataValue(attrGroupId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db3));
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
	protected List<InsBean> prepareInsertData(DCP_AttrGroupDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_AttrGroupDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_AttrGroupDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_AttrGroupDeleteReq req) throws Exception {
		
  boolean isFail = false;
  StringBuffer errMsg = new StringBuffer("");

  if(req.getRequest()==null)
  {
  	errMsg.append("request不能为空 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }
  
  if (req.getRequest().getAttrGroupIdList()==null) 
  {
  	errMsg.append("编码不能为空 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	}
  
  
  for (level1Elm par : req.getRequest().getAttrGroupIdList())
	{
  	String attrGroupId = par.getAttrGroupId();   
    if(Check.Null(attrGroupId)){
     	errMsg.append("属性分组编码不能为空值 ");
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
	protected TypeToken<DCP_AttrGroupDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_AttrGroupDeleteReq>(){};
	}

	@Override
	protected DCP_AttrGroupDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_AttrGroupDeleteRes() ;
	}

}
