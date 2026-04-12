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
import com.dsc.spos.json.cust.req.DCP_PosFuncTemplateUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PosFuncTemplateUpdateReq.funcNo;
import com.dsc.spos.json.cust.req.DCP_PosFuncTemplateUpdateReq.levelFuncGroup;
import com.dsc.spos.json.cust.req.DCP_PosFuncTemplateUpdateReq.levelRange;
import com.dsc.spos.json.cust.req.DCP_PosFuncTemplateUpdateReq.levelTemplateName;
import com.dsc.spos.json.cust.res.DCP_PosFuncTemplateUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PosFuncTemplateUpdate extends SPosAdvanceService<DCP_PosFuncTemplateUpdateReq,DCP_PosFuncTemplateUpdateRes>
{

	@Override
	protected void processDUID(DCP_PosFuncTemplateUpdateReq req, DCP_PosFuncTemplateUpdateRes res) throws Exception 
	{
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String eId=req.geteId();
		String templateId = req.getRequest().getTemplateId();		
		String pageType=req.getRequest().getPageType();

		String restrictshop=req.getRequest().getRestrictShop();

		String status=req.getRequest().getStatus();
		String memo=req.getRequest().getMemo();
		List<levelRange> rangeList=req.getRequest().getRangeList();

		List<levelTemplateName> templateName_lang=req.getRequest().getTemplateName_lang();
		List<levelFuncGroup> groupsList=req.getRequest().getFuncList();


		UptBean up1 = new UptBean("DCP_POSFUNCTEMPLATE");
		up1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		up1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));

		up1.addUpdateValue("PAGETYPE", new DataValue(pageType, Types.VARCHAR));
		up1.addUpdateValue("RESTRICTSHOP", new DataValue(restrictshop, Types.VARCHAR));
		up1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
		up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
		up1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
		up1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
		up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

		this.addProcessData(new DataProcessBean(up1));

		DelBean	db1 = new DelBean("DCP_POSFUNCTEMPLATE_LANG");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));

		db1 = new DelBean("DCP_POSFUNCTEMPLATE_FUNC");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));

		db1 = new DelBean("DCP_POSFUNCTEMPLATE_RANGE");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));


		String[] columns_DCP_POSFUNCTEMPLATE_LANG = 
			{ 
					"EID","TEMPLATEID","LANG_TYPE","TEMPLATENAME","LASTMODITIME"
			};
		for (levelTemplateName template : templateName_lang) 
		{

			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(templateId, Types.VARCHAR),
					new DataValue(template.getLangType(), Types.VARCHAR),
					new DataValue(template.getName(), Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)				
			};

			InsBean ib1 = new InsBean("DCP_POSFUNCTEMPLATE_LANG", columns_DCP_POSFUNCTEMPLATE_LANG);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增
		}		

		String[] columns_DCP_POSFUNCTEMPLATE_FUNC = 
			{ 
					"EID","TEMPLATEID","FUNCGROUP","FUNCNO","SORTID","LASTMODITIME"
			};
		for (levelFuncGroup group : groupsList) 
		{
			String funcGroup = group.getFuncGroup();
			String sortId = group.getSortId();
			List<funcNo> funcNoList  = group.getFuncNoList();
			for (funcNo item : funcNoList) 
			{

				DataValue[] insValue1 = null;

				String funcNo = item.getFuncNo();
				String funcSortId = item.getSortId();
				
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(templateId, Types.VARCHAR),
						new DataValue(funcGroup, Types.VARCHAR),
						new DataValue(funcNo, Types.VARCHAR),
						new DataValue(funcSortId, Types.VARCHAR),						
						new DataValue(lastmoditime, Types.DATE)				
				};

				InsBean ib1 = new InsBean("DCP_POSFUNCTEMPLATE_FUNC", columns_DCP_POSFUNCTEMPLATE_FUNC);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增


			}

		}

		//适用门店：0-所有门店1-指定门店
		if (restrictshop.equals("1") || restrictshop.equals("2"))
		{
			//
			String[] columns_DCP_POSFUNCTEMPLATE_RANGE = 
				{ 
						"EID","TEMPLATEID","SHOPID","LASTMODITIME"
				};
			for (levelRange range : rangeList) 
			{


				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(templateId, Types.VARCHAR),
						new DataValue(range.getShopId(), Types.VARCHAR),					
						new DataValue(lastmoditime, Types.DATE)				
				};

				InsBean ib1 = new InsBean("DCP_POSFUNCTEMPLATE_RANGE", columns_DCP_POSFUNCTEMPLATE_RANGE);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增			
			}
		}	

		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PosFuncTemplateUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PosFuncTemplateUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PosFuncTemplateUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PosFuncTemplateUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String templateId = req.getRequest().getTemplateId();		
		String pageType=req.getRequest().getPageType();

		String restrictshop=req.getRequest().getRestrictShop();

		String status=req.getRequest().getStatus();

		List<levelTemplateName> templateName_lang=req.getRequest().getTemplateName_lang();
		if(templateName_lang==null)
		{
			errMsg.append("模板多语言不能为空值 ");
			isFail = true;
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		List<levelFuncGroup> groupsList=req.getRequest().getFuncList();
		if(groupsList==null)
		{
			isFail = true;
			errMsg.append("功能分组不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());

		}
		for (levelFuncGroup levelFuncGroup : groupsList) 
		{
			if(levelFuncGroup.getFuncNoList()==null)
			{
				isFail = true;
				errMsg.append("功能不能为空值 ");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}

		}

		if(Check.Null(templateId))
		{
			errMsg.append("模板编码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(pageType))
		{
			errMsg.append("页面类型不能为空值, ");
			isFail = true;
		}

		if(Check.Null(restrictshop))
		{
			errMsg.append("适用门店不能为空值, ");
			isFail = true;
		}

		if(Check.Null(status))
		{
			errMsg.append("状态不能为空值 ");
			isFail = true;
		}



		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PosFuncTemplateUpdateReq> getRequestType() 
	{		
		return new TypeToken<DCP_PosFuncTemplateUpdateReq>() {};
	}

	@Override
	protected DCP_PosFuncTemplateUpdateRes getResponseType() 
	{
		return new DCP_PosFuncTemplateUpdateRes();
	}	



}
