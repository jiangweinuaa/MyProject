package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayClassEnableReq;
import com.dsc.spos.json.cust.req.DCP_PayClassEnableReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PayClassEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PayClassEnable extends SPosAdvanceService<DCP_PayClassEnableReq,DCP_PayClassEnableRes> {

	@Override
	protected void processDUID(DCP_PayClassEnableReq req, DCP_PayClassEnableRes res) throws Exception {
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
			
			for (level1Elm par : req.getRequest().getClassList()) 
			{
				String keyNo = par.getClassNo();
				UptBean up1 = new UptBean("DCP_PAYCLASS");
				up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				up1.addCondition("CLASSNO", new DataValue(keyNo, Types.VARCHAR));
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
	protected List<InsBean> prepareInsertData(DCP_PayClassEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayClassEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayClassEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayClassEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	boolean isFail = false;
	StringBuffer errMsg = new StringBuffer("");
	
	if(req.getRequest()==null)
  {
  	errMsg.append("requset不能为空值 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }

	if(Check.Null(req.getRequest().getOprType()))
	{
		errMsg.append("操作类型不可为空值, ");
		isFail = true;
		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	}
			
	
	List<level1Elm> stausList = req.getRequest().getClassList();
	
	if (stausList==null || stausList.isEmpty()) 
	{
		errMsg.append("编码不可为空, ");
		isFail = true;
		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	} 
	for(level1Elm par : stausList)
	{
		if(Check.Null(par.getClassNo()))
		{
			errMsg.append("编码不可为空值, ");
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
	protected TypeToken<DCP_PayClassEnableReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_PayClassEnableReq>(){};
	}

	@Override
	protected DCP_PayClassEnableRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_PayClassEnableRes();
	}

}
