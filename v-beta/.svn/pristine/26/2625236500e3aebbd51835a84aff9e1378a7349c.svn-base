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
import com.dsc.spos.json.cust.req.DCP_TagUpdateReq;
import com.dsc.spos.json.cust.req.DCP_TagUpdateReq.levelTagTypeLang;
import com.dsc.spos.json.cust.res.DCP_TagUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_TagUpdate extends SPosAdvanceService<DCP_TagUpdateReq,DCP_TagUpdateRes>
{

	@Override
	protected void processDUID(DCP_TagUpdateReq req, DCP_TagUpdateRes res) throws Exception 
	{
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

		String eId=req.geteId();
		String tagNo = req.getRequest().getTagNo();		
		String sortId=req.getRequest().getSortId();
		String tagGroupNo=req.getRequest().getTagGroupNo();
		String tagGroupType=req.getRequest().getTagGroupType();
		String memo = req.getRequest().getMemo();		
		String status = req.getRequest().getStatus();
        String isSingleProduce = req.getRequest().getIsSingleProduce();
        List<levelTagTypeLang> tagTypeName_lang=req.getRequest().getTagName_lang();

		String sql = "select TAGNO from DCP_TAGTYPE where eid='"+eId+"' and TAGNO='"+tagNo+"' and TAGGROUPTYPE='"+tagGroupType+"' ";
		List<Map<String , Object>> getData=this.doQueryData(sql, null);
		if (getData==null || getData.isEmpty()) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此标签不存在！");
			return;
		}

		if (tagTypeName_lang.size()==0) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("标签多语言必须有值！");
			return;
		}

		//删除之前的,再插入
		DelBean db1 = new DelBean("DCP_TAGTYPE_LANG");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("TAGGROUPTYPE", new DataValue(tagGroupType,Types.VARCHAR));
		//db1.addCondition("TAGGROUPNO", new DataValue(tagGroupNo,Types.VARCHAR));
		db1.addCondition("TAGNO", new DataValue(tagNo,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));	

		String[] columns_DCP_TAGTYPE_LANG = 
			{ 
					"EID","TAGGROUPTYPE","TAGGROUPNO","TAGNO","LANG_TYPE","TAGNAME","LASTMODITIME"
			};
		for (levelTagTypeLang lang : tagTypeName_lang) 
		{

			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(tagGroupType, Types.VARCHAR),
					new DataValue(tagGroupNo, Types.VARCHAR),
					new DataValue(tagNo, Types.VARCHAR),
					new DataValue(lang.getLangType(), Types.VARCHAR),
					new DataValue(lang.getName(), Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)				
			};

			InsBean ib1 = new InsBean("DCP_TAGTYPE_LANG", columns_DCP_TAGTYPE_LANG);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增
		}	



		//
		UptBean ub1 = new UptBean("DCP_TAGTYPE");
		ub1.addUpdateValue("TAGGROUPNO", new DataValue(tagGroupNo,Types.VARCHAR));
		ub1.addUpdateValue("SORTID", new DataValue(sortId,Types.INTEGER));
		ub1.addUpdateValue("MEMO", new DataValue(memo,Types.VARCHAR));
		ub1.addUpdateValue("ISSINGLEPRODUCE", new DataValue(isSingleProduce,Types.VARCHAR));
		if(status!=null&&status.length()>0)
		{
			ub1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));
		}
		
		ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(),Types.VARCHAR));
		ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(),Types.VARCHAR));
		ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));


		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("TAGGROUPTYPE", new DataValue(tagGroupType, Types.VARCHAR));
		//ub1.addCondition("TAGGROUPNO", new DataValue(tagGroupNo, Types.VARCHAR));
		ub1.addCondition("TAGNO", new DataValue(tagNo, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(ub1));


		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TagUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TagUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TagUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TagUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String tagNo = req.getRequest().getTagNo();		
		String sortId=req.getRequest().getSortId();
		String tagGroupNo=req.getRequest().getTagGroupNo();
		String tagGroupType=req.getRequest().getTagGroupType();
		String memo = req.getRequest().getMemo();		
		String status = req.getRequest().getStatus();
		List<levelTagTypeLang> tagTypeName_lang=req.getRequest().getTagName_lang();

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
		if(Check.Null(tagNo))
		{
			errMsg.append("标签编码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(sortId))
		{
			errMsg.append("标签组内顺序不能为空值 ");
			isFail = true;
		}

		if (tagTypeName_lang==null || tagTypeName_lang.size()==0) 
		{
			errMsg.append("标签多语言不能为空值 ");
			isFail = true;
		}

		for (levelTagTypeLang langTagType : tagTypeName_lang) 
		{
			String lang=langTagType.getLangType();
			String name=langTagType.getName();

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
	protected TypeToken<DCP_TagUpdateReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TagUpdateReq>() {};
	}

	@Override
	protected DCP_TagUpdateRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_TagUpdateRes();
	}



}
