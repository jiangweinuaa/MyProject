package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DualPlayTemEnableReq;
import com.dsc.spos.json.cust.req.DCP_DualPlayTemEnableReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_DualPlayTemEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_DualPlayTemEnable extends SPosAdvanceService<DCP_DualPlayTemEnableReq, DCP_DualPlayTemEnableRes> {

	@Override
	protected void processDUID(DCP_DualPlayTemEnableReq req, DCP_DualPlayTemEnableRes res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			String status = "100";//状态：-1未启用100已启用 0已禁用
			
			if(req.getRequest().getOprType().equals("1"))//操作类型：1-启用2-禁用
			{
				status = "100";
			}
			else
			{
				status = "0";
			}
			
			for (level1Elm par : req.getRequest().getTemplateList())
			{
				String keyNo = par.getTemplateNo();
				UptBean up1 = new UptBean("DCP_DUALPLAY_TEMPLATE");
				up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				up1.addCondition("TEMPLATENO", new DataValue(keyNo, Types.VARCHAR));
				if(status.equals("0"))//只能禁用，已启用的。
				{
					up1.addCondition("STATUS", new DataValue("100", Types.VARCHAR));
				}						
				up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(up1));
			}
			
			this.doExecuteDataToDB();		
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");	
		
	
		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常："+e.getMessage());

		}
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DualPlayTemEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DualPlayTemEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DualPlayTemEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DualPlayTemEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	boolean isFail = false;
	StringBuffer errMsg = new StringBuffer("");
	
	if(req.getRequest()==null)
  {
  	errMsg.append("request不能为空值 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }

	if(Check.Null(req.getRequest().getOprType()))
	{
		errMsg.append("操作类型不可为空值, ");
		isFail = true;
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
	protected TypeToken<DCP_DualPlayTemEnableReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_DualPlayTemEnableReq>(){};
	}

	@Override
	protected DCP_DualPlayTemEnableRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_DualPlayTemEnableRes();
	}

}
