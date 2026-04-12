package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TagGroupCreateReq;
import com.dsc.spos.json.cust.req.DCP_TagGroupCreateReq.levelGroup_lang;
import com.dsc.spos.json.cust.res.DCP_TagGroupCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_TagGroupCreate extends SPosAdvanceService<DCP_TagGroupCreateReq, DCP_TagGroupCreateRes> 
{
	@Override
	protected void processDUID(DCP_TagGroupCreateReq req, DCP_TagGroupCreateRes res) throws Exception 
	{
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

		String eId=req.geteId();
		String exclusived = req.getRequest().getExclusived();		
		String sortId=req.getRequest().getSortId();
		String tagGroupNo=req.getRequest().getTagGroupNo();
		String tagGroupType=req.getRequest().getTagGroupType();
		String status = req.getRequest().getStatus();
		if(status==null||status.isEmpty())
		{
			status = "100";
		}

		List<levelGroup_lang> tagGroupName_lang=req.getRequest().getTagGroupName_lang();

		String sql = "select TAGGROUPNO from DCP_TAGGROUP where eid='"+eId+"' and TAGGROUPNO='"+tagGroupNo+"' and TAGGROUPTYPE='"+tagGroupType+"' ";
		List<Map<String , Object>> getData=this.doQueryData(sql, null);
		if (getData!=null && getData.isEmpty()==false) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此标签分组已经存在！");
			return;
		}

		if (tagGroupName_lang.size()==0) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("标签分组多语言必须有值！");
			return;
		}

		String[] columns_DCP_TAGGROUP_LANG = 
			{ 
					"EID","TAGGROUPTYPE","TAGGROUPNO","LANG_TYPE","TAGGROUPNAME","LASTMODITIME"
			};
		for (levelGroup_lang lang : tagGroupName_lang) 
		{

			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(tagGroupType, Types.VARCHAR),
					new DataValue(tagGroupNo, Types.VARCHAR),
					new DataValue(lang.getLangType(), Types.VARCHAR),
					new DataValue(lang.getName(), Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)				
			};

			InsBean ib1 = new InsBean("DCP_TAGGROUP_LANG", columns_DCP_TAGGROUP_LANG);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增
		}	


		//
		String[] columns_DCP_TAGGROUP = 
			{ 
					"EID","TAGGROUPTYPE","TAGGROUPNO","SORTID",
					"EXCLUSIVED","MEMO","STATUS","CREATEOPID",
					"CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
			};
		DataValue[] insValue1 = null;

		insValue1 = new DataValue[]{
				new DataValue(eId, Types.VARCHAR),
				new DataValue(tagGroupType, Types.VARCHAR),
				new DataValue(tagGroupNo, Types.VARCHAR),
				new DataValue(sortId, Types.INTEGER),
				new DataValue(exclusived, Types.VARCHAR),
				new DataValue(req.getRequest().getMemo(), Types.VARCHAR),
				new DataValue(status, Types.VARCHAR),				
				new DataValue(req.getOpNO(), Types.VARCHAR),
				new DataValue(req.getOpName(), Types.VARCHAR),
				new DataValue(lastmoditime, Types.DATE),
				new DataValue(req.getOpNO(), Types.VARCHAR),
				new DataValue(req.getOpName(), Types.VARCHAR),
				new DataValue(lastmoditime, Types.DATE)				
		};

		InsBean ib1 = new InsBean("DCP_TAGGROUP", columns_DCP_TAGGROUP);
		ib1.addValues(insValue1);
		this.addProcessData(new DataProcessBean(ib1)); // 新增

		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TagGroupCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TagGroupCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TagGroupCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TagGroupCreateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String exclusived = req.getRequest().getExclusived();		
		String sortId=req.getRequest().getSortId();
		String tagGroupNo=req.getRequest().getTagGroupNo();
		String tagGroupType=req.getRequest().getTagGroupType();

		List<levelGroup_lang> tagGroupName_lang=req.getRequest().getTagGroupName_lang();
		if(Check.Null(tagGroupType))
		{
			errMsg.append("标签分组类型不能为空值 ");
			isFail = true;
		}
		if(Check.Null(tagGroupNo))
		{
			errMsg.append("标签分组编号不能为空值 ");
			isFail = true;
		}
		if(Check.Null(exclusived))
		{
			errMsg.append("标签组内是否互斥不能为空值 ");
			isFail = true;
		}
		if(Check.Null(sortId))
		{
			errMsg.append("标签组内顺序不能为空值 ");
			isFail = true;
		}

		for (levelGroup_lang langGroup : tagGroupName_lang) 
		{
			String lang=langGroup.getLangType();
			String name=langGroup.getName();

			if(Check.Null(lang))
			{
				errMsg.append("语言类型不能为空值 ");
				isFail = true;
			}
			if(Check.Null(name))
			{
				errMsg.append("分组名称不能为空值 ");
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
	protected TypeToken<DCP_TagGroupCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TagGroupCreateReq>(){};
	}

	@Override
	protected DCP_TagGroupCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_TagGroupCreateRes();
	}



}
