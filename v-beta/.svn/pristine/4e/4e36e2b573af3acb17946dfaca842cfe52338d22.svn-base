package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TagGroupDeleteReq;
import com.dsc.spos.json.cust.res.DCP_TagGroupDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_TagGroupDelete extends SPosAdvanceService<DCP_TagGroupDeleteReq, DCP_TagGroupDeleteRes>
{

	@Override
	protected void processDUID(DCP_TagGroupDeleteReq req, DCP_TagGroupDeleteRes res) throws Exception 
	{

		String eId = req.geteId();
		for (DCP_TagGroupDeleteReq.tagGroupInfo par : req.getRequest().getTagGroupList())
		{
			String tagGroupType = par.getTagGroupType();
			String tagGroupNo = par.getTagGroupNo();

			//
			DelBean db1 = new DelBean("DCP_TAGGROUP");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("TAGGROUPTYPE", new DataValue(tagGroupType,Types.VARCHAR));
			db1.addCondition("TAGGROUPNO", new DataValue(tagGroupNo,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			//
			db1 = new DelBean("DCP_TAGGROUP_LANG");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("TAGGROUPTYPE", new DataValue(tagGroupType,Types.VARCHAR));
			db1.addCondition("TAGGROUPNO", new DataValue(tagGroupNo,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			db1 = new DelBean("DCP_TAGTYPE");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("TAGGROUPTYPE", new DataValue(tagGroupType,Types.VARCHAR));
			db1.addCondition("TAGGROUPNO", new DataValue(tagGroupNo,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			db1 = new DelBean("DCP_TAGTYPE_LANG");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("TAGGROUPTYPE", new DataValue(tagGroupType,Types.VARCHAR));
			db1.addCondition("TAGGROUPNO", new DataValue(tagGroupNo,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			db1 = new DelBean("DCP_TAGTYPE_DETAIL");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("TAGGROUPTYPE", new DataValue(tagGroupType,Types.VARCHAR));
			db1.addCondition("TAGGROUPNO", new DataValue(tagGroupNo,Types.VARCHAR));
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
	protected List<InsBean> prepareInsertData(DCP_TagGroupDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TagGroupDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TagGroupDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TagGroupDeleteReq req) throws Exception 
	{	
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if(req.getRequest().getTagGroupList()==null||req.getRequest().getTagGroupList().isEmpty())
		{
			isFail = true;
			errMsg.append("列表不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		
		for (DCP_TagGroupDeleteReq.tagGroupInfo par : req.getRequest().getTagGroupList())
		{
			String tagGroupType = par.getTagGroupType();
			String tagGroupNo = par.getTagGroupNo();
			if(Check.Null(tagGroupType))
			{
				errMsg.append("标签分组类型不能为空值 ");
				isFail = true;
			}
			if(Check.Null(tagGroupNo))
			{
				errMsg.append("标签分组编码不能为空值 ");
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
	protected TypeToken<DCP_TagGroupDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TagGroupDeleteReq>(){};
	}

	@Override
	protected DCP_TagGroupDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_TagGroupDeleteRes();
	}

}
