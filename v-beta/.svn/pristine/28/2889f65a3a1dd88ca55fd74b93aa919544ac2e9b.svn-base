package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DualPlayTemDeleteReq;
import com.dsc.spos.json.cust.req.DCP_DualPlayTemDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_DualPlayTemDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_DualPlayTemDelete extends SPosAdvanceService<DCP_DualPlayTemDeleteReq,DCP_DualPlayTemDeleteRes> {

	@Override
	protected void processDUID(DCP_DualPlayTemDeleteReq req, DCP_DualPlayTemDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			String eId= req.geteId();
			String sql = "";
			boolean isExistDelete = false;
			for (level1Elm par : req.getRequest().getTemplateList())
			{
				String templateNo = par.getTemplateNo();
				sql = "";
				sql = "select status from DCP_DUALPLAY_TEMPLATE "
						+ " where  status='100' and eid='"+eId+"' and TEMPLATENO='"+templateNo+"' ";
				List<Map<String, Object>> getData = this.doQueryData(sql, null);
				if(getData!=null&&getData.isEmpty()==false)
				{
					continue;
				}

				DelBean db1 = new DelBean("DCP_DUALPLAY_TEMPLATE");
				db1.addCondition("EID",new DataValue(eId, Types.VARCHAR));
				db1.addCondition("TEMPLATENO",new DataValue(templateNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				db1 = new DelBean("DCP_DUALPLAY_TEMPLATE_SHOP");
				db1.addCondition("EID",new DataValue(eId, Types.VARCHAR));
				db1.addCondition("TEMPLATENO",new DataValue(templateNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				db1 = new DelBean("DCP_DUALPLAY_TEMPLATE_TIME");
				db1.addCondition("EID",new DataValue(eId, Types.VARCHAR));
				db1.addCondition("TEMPLATENO",new DataValue(templateNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				db1 = new DelBean("DCP_DUALPLAY_TEMPLATE_FILE");
				db1.addCondition("EID",new DataValue(eId, Types.VARCHAR));
				db1.addCondition("TEMPLATENO",new DataValue(templateNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				isExistDelete = true;

			}
			if (isExistDelete)
			{
				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("只能删除未启用或已禁用的");
			}
		
	
		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常："+e.getMessage());

		}
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DualPlayTemDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DualPlayTemDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DualPlayTemDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DualPlayTemDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	boolean isFail = false;
	StringBuffer errMsg = new StringBuffer("");
	
	if(req.getRequest()==null)
  {
  	errMsg.append("request不能为空值 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }

	
	List<level1Elm> stausList = req.getRequest().getTemplateList();
	
	if (stausList==null || stausList.isEmpty()) 
	{
		errMsg.append("模板列表不可为空, ");
		isFail = true;
		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	} 
	for(level1Elm par : stausList)
	{
		if(Check.Null(par.getTemplateNo()))
		{
			errMsg.append("模板编码不可为空值, ");
			isFail = true;
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	}

	if (isFail)
	{
		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	}

	return isFail;
	}

	@Override
	protected TypeToken<DCP_DualPlayTemDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_DualPlayTemDeleteReq>(){};
	}

	@Override
	protected DCP_DualPlayTemDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_DualPlayTemDeleteRes();
	}

}
