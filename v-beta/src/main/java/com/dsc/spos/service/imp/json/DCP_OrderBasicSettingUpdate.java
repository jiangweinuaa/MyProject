package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderBasicSettingUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrderBasicSettingUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderBasicSettingUpdate extends SPosAdvanceService<DCP_OrderBasicSettingUpdateReq, DCP_OrderBasicSettingUpdateRes>
{

	@Override
	protected void processDUID(DCP_OrderBasicSettingUpdateReq req, DCP_OrderBasicSettingUpdateRes res) throws Exception
	{
		// TODO Auto-generated method stub
		
		String eId = req.geteId();
		String settingNo = req.getRequest().getSettingNo();
		String settingValue = req.getRequest().getSettingValue();
		
		
		UptBean up1 = new UptBean("DCP_ORDERBASICSETTING");
		
		up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		up1.addCondition("SETTINGNO", new DataValue(settingNo, Types.VARCHAR));
		
		up1.addUpdateValue("SETTINGVALUE", new DataValue(settingValue, Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(up1));
		
		this.doExecuteDataToDB();
		
		res.setSuccess(true);
		res.setServiceDescription("服务执行成功！");
		res.setServiceStatus("000");
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderBasicSettingUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderBasicSettingUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderBasicSettingUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderBasicSettingUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");

	    if(req.getRequest()==null)
	    {
	    	errMsg.append("requset不能为空值 ");
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }
	    
	    if(Check.Null(req.getRequest().getSettingNo()))
	    {
	    	errMsg.append("settingNo编号不能为空值 ");
	    	isFail = true;
	    	
	    }
	    if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderBasicSettingUpdateReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderBasicSettingUpdateReq>(){};
	}

	@Override
	protected DCP_OrderBasicSettingUpdateRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderBasicSettingUpdateRes();
	}

}
