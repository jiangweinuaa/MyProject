package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderEcommerceDeleteReq;
import com.dsc.spos.json.cust.res.DCP_OrderEcommerceDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderEcommerceDelete extends SPosAdvanceService<DCP_OrderEcommerceDeleteReq,DCP_OrderEcommerceDeleteRes>
{

	@Override
	protected void processDUID(DCP_OrderEcommerceDeleteReq req, DCP_OrderEcommerceDeleteRes res) throws Exception
	{
		String eId=req.geteId();
		String channelId = req.getRequest().getChannelId();	
		String loadDocType = req.getRequest().getLoadDocType();	

		DelBean db1 = new DelBean("DCP_ECOMMERCE");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("CHANNELID", new DataValue(channelId,Types.VARCHAR));
		db1.addCondition("LOADDOCTYPE", new DataValue(loadDocType,Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(db1));
        DelBean db2 = new DelBean("DCP_CHANNELREVIEW_SHOP");
        db2.addCondition("EID", new DataValue(eId,Types.VARCHAR));
        db2.addCondition("CHANNELID", new DataValue(channelId,Types.VARCHAR));
        db2.addCondition("LOADDOCTYPE", new DataValue(loadDocType,Types.VARCHAR));
		//
		this.doExecuteDataToDB();


		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderEcommerceDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderEcommerceDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderEcommerceDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderEcommerceDeleteReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (Check.Null(req.getRequest().getChannelId()))
		{
			isFail = true;
			errMsg.append("渠道编码不能为空 ");
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderEcommerceDeleteReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderEcommerceDeleteReq>(){};
	}

	@Override
	protected DCP_OrderEcommerceDeleteRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderEcommerceDeleteRes();
	}

}
