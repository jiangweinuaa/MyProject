package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SupPriceTemplateEnableReq;
import com.dsc.spos.json.cust.res.DCP_SupPriceTemplateEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_SupPriceTemplateEnable extends SPosAdvanceService<DCP_SupPriceTemplateEnableReq,DCP_SupPriceTemplateEnableRes>
{

	@Override
	protected void processDUID(DCP_SupPriceTemplateEnableReq req, DCP_SupPriceTemplateEnableRes res) throws Exception
	{
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String eId=req.geteId();

		String oprType=req.getRequest().getOprType();		

		for (DCP_SupPriceTemplateEnableReq.Template par : req.getRequest().getTemplateList())
		{
			String templateId = par.getTemplateId();

			UptBean ub1 = new UptBean("DCP_SUPPRICETEMPLATE");

			ub1.addUpdateValue("STATUS", new DataValue(oprType.equals("1")?100:0,Types.VARCHAR));
			ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(),Types.VARCHAR));
			ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(),Types.VARCHAR));
			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));

			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

		}

		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;	

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SupPriceTemplateEnableReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SupPriceTemplateEnableReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SupPriceTemplateEnableReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SupPriceTemplateEnableReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String oprType=req.getRequest().getOprType();
		if(Check.Null(oprType))
		{
			errMsg.append("操作类型不能为空值 ");
			isFail = true;
		}		

		for (DCP_SupPriceTemplateEnableReq.Template par : req.getRequest().getTemplateList())
		{
			String templateId = par.getTemplateId();

			if(Check.Null(templateId))
			{
				errMsg.append("模板编码不能为空值 ");
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
	protected TypeToken<DCP_SupPriceTemplateEnableReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SupPriceTemplateEnableReq>(){};
	}

	@Override
	protected DCP_SupPriceTemplateEnableRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_SupPriceTemplateEnableRes();
	}



}
