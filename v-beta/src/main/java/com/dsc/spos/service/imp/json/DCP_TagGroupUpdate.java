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
import com.dsc.spos.json.cust.req.DCP_TagGroupUpdateReq;
import com.dsc.spos.json.cust.req.DCP_TagGroupUpdateReq.levelGroup_lang;
import com.dsc.spos.json.cust.res.DCP_TagGroupUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_TagGroupUpdate extends SPosAdvanceService<DCP_TagGroupUpdateReq, DCP_TagGroupUpdateRes> 
{
	@Override
	protected void processDUID(DCP_TagGroupUpdateReq req, DCP_TagGroupUpdateRes res) throws Exception 
	{
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

		String eId=req.geteId();
		String exclusived = req.getRequest().getExclusived();		
		String sortId=req.getRequest().getSortId();
		String tagGroupNo=req.getRequest().getTagGroupNo();
		String tagGroupType=req.getRequest().getTagGroupType();

		List<levelGroup_lang> tagGroupName_lang=req.getRequest().getTagGroupName_lang();

		String sql = "select TAGGROUPNO from DCP_TAGGROUP where eid='"+eId+"' and TAGGROUPNO='"+tagGroupNo+"' and TAGGROUPTYPE='"+tagGroupType+"' ";
		List<Map<String , Object>> getData=this.doQueryData(sql, null);
		if (getData==null || getData.isEmpty()) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此标签分组不存在！");
			return;
		}

		if (tagGroupName_lang.size()==0) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("标签分组多语言必须有值！");
			return;
		}

		//删除之前的,再插入
		DelBean db1 = new DelBean("DCP_TAGGROUP_LANG");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("TAGGROUPTYPE", new DataValue(tagGroupType,Types.VARCHAR));
		db1.addCondition("TAGGROUPNO", new DataValue(tagGroupNo,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));	


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
		UptBean ub1 = new UptBean("DCP_TAGGROUP");
		ub1.addUpdateValue("SORTID", new DataValue(sortId,Types.INTEGER));
		ub1.addUpdateValue("EXCLUSIVED", new DataValue(exclusived,Types.VARCHAR));
		ub1.addUpdateValue("MEMO", new DataValue(req.getRequest().getMemo(),Types.VARCHAR));
		if(req.getRequest().getStatus()!=null&&req.getRequest().getStatus().length()>0)
		{
			ub1.addUpdateValue("STATUS", new DataValue(req.getRequest().getStatus(),Types.VARCHAR));
		}
		
		ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(),Types.VARCHAR));
		ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(),Types.VARCHAR));
		ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));


		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("TAGGROUPTYPE", new DataValue(tagGroupType, Types.VARCHAR));
		ub1.addCondition("TAGGROUPNO", new DataValue(tagGroupNo, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(ub1));

		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TagGroupUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TagGroupUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TagGroupUpdateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TagGroupUpdateReq req) throws Exception 
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
	protected TypeToken<DCP_TagGroupUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TagGroupUpdateReq>(){};
	}

	@Override
	protected DCP_TagGroupUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_TagGroupUpdateRes();
	}

}
