package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TagDetailDeleteReq;
import com.dsc.spos.json.cust.res.DCP_TagDetailDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_TagDetailDelete extends SPosAdvanceService<DCP_TagDetailDeleteReq,DCP_TagDetailDeleteRes>
{

	@Override
	protected void processDUID(DCP_TagDetailDeleteReq req, DCP_TagDetailDeleteRes res) throws Exception 
	{

		String eId = req.geteId();
		String tagGroupType = req.getRequest().getTagGroupType();
		String tagNo = req.getRequest().getTagNo();

		//
		for (DCP_TagDetailDeleteReq.levelTypeDetail par : req.getRequest().getIdList())
		{
			DelBean db1 = new DelBean("DCP_TAGTYPE_DETAIL");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("TAGGROUPTYPE", new DataValue(tagGroupType,Types.VARCHAR));
			db1.addCondition("TAGNO", new DataValue(tagNo,Types.VARCHAR));
			db1.addCondition("ID", new DataValue(par.getId(),Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
		}
			


		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TagDetailDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TagDetailDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TagDetailDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TagDetailDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String tagGroupType = req.getRequest().getTagGroupType();
		String tagNo = req.getRequest().getTagNo();

		if(Check.Null(tagGroupType))
		{
			errMsg.append("标签分组类型不能为空值 ");
			isFail = true;
		}
		if(Check.Null(tagNo))
		{
			errMsg.append("标签编码不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_TagDetailDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TagDetailDeleteReq>() {};
	}

	@Override
	protected DCP_TagDetailDeleteRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_TagDetailDeleteRes();
	}



}
