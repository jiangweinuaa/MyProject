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
import com.dsc.spos.json.cust.req.DCP_TagCreateReq;
import com.dsc.spos.json.cust.req.DCP_TagCreateReq.levelTagTypeLang;
import com.dsc.spos.json.cust.res.DCP_TagCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_TagCreate extends SPosAdvanceService<DCP_TagCreateReq,DCP_TagCreateRes>
{

	@Override
	protected void processDUID(DCP_TagCreateReq req, DCP_TagCreateRes res) throws Exception 
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
        if(status==null||status.isEmpty())
		{
			status = "100";
		}
		List<levelTagTypeLang> tagTypeName_lang=req.getRequest().getTagName_lang();

		String sql = "select TAGNO from DCP_TAGTYPE where eid='"+eId+"' and TAGNO='"+tagNo+"' and TAGGROUPTYPE='"+tagGroupType+"' ";
		List<Map<String , Object>> getData=this.doQueryData(sql, null);
		if (getData!=null && getData.isEmpty()==false) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此标签已经存在！");
			return;
		}

		if (tagTypeName_lang.size()==0) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("标签多语言必须有值！");
			return;
		}

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
		String[] columns_DCP_TAGTYPE = 
			{ 
					"EID","TAGGROUPTYPE","TAGGROUPNO","SORTID",
					"TAGNO","MEMO","STATUS","CREATEOPID",
					"CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","ISSINGLEPRODUCE"
			};
		DataValue[] insValue1 = null;

		insValue1 = new DataValue[]{
				new DataValue(eId, Types.VARCHAR),
				new DataValue(tagGroupType, Types.VARCHAR),
				new DataValue(tagGroupNo, Types.VARCHAR),
				new DataValue(sortId, Types.INTEGER),
				new DataValue(tagNo, Types.VARCHAR),
				new DataValue(memo, Types.VARCHAR),
				new DataValue(status, Types.VARCHAR),				
				new DataValue(req.getOpNO(), Types.VARCHAR),
				new DataValue(req.getOpName(), Types.VARCHAR),
				new DataValue(lastmoditime, Types.DATE),
				new DataValue(req.getOpNO(), Types.VARCHAR),
				new DataValue(req.getOpName(), Types.VARCHAR),
				new DataValue(lastmoditime, Types.DATE),
				new DataValue(isSingleProduce, Types.VARCHAR),
		};

		InsBean ib1 = new InsBean("DCP_TAGTYPE", columns_DCP_TAGTYPE);
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
	protected List<InsBean> prepareInsertData(DCP_TagCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TagCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TagCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TagCreateReq req) throws Exception 
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
	protected TypeToken<DCP_TagCreateReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TagCreateReq>() {};
	}

	@Override
	protected DCP_TagCreateRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_TagCreateRes();
	}

}
